/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.gluonhq.jfxapps.boot.loader.content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;

public class MavenExtensionProvider implements ExtensionContentProvider {

    private static final Logger logger = LoggerFactory.getLogger(MavenExtensionProvider.class);

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("artifactId")
    private String artifactId;

    @JsonProperty("version")
    private String version;

    @JsonIgnore
    private UniqueArtifact mavenArtifact;

    @JsonIgnore
    private ResolvedArtifact resolvedArtifact;

    private RepositoryClient repositoryClient;

    private final Map<ResolvedArtifact, ExtensionContentProvider> aggregate = new HashMap<>();

    public MavenExtensionProvider(String groupId, String artifactId, String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    private void resolveArtefact() {
        if (mavenArtifact == null) {
            mavenArtifact = UniqueArtifact.builder().withArtifact(groupId, artifactId).withVersion(version).build();
        }
    }

    private void resolveArtefactDependencies() {
        resolveArtefact();

        if (resolvedArtifact == null) {
            resolvedArtifact = repositoryClient.resolveWithDependencies(mavenArtifact).orElse(null);
            buildAggregate(resolvedArtifact);
        }
    }

    private void buildAggregate(ResolvedArtifact artifact) {

        Function<Path, ExtensionContentProvider> setupProvider = (
                p) -> Files.isDirectory(p) ? new FolderExtensionProvider(p) : new FileExtensionProvider(p);

        aggregate.put(artifact, setupProvider.apply(artifact.getPath()));

        artifact.getDependencies().forEach(d -> aggregate.put(d, setupProvider.apply(d.getPath())));

    }

    @Override
    public boolean isValid() {
        resolveArtefact();
        return mavenArtifact != null;
    }

    @Override
    public boolean isUpToDate(Path targetFolder) {
        resolveArtefactDependencies();

        boolean upToDate = true;

        for (var entry : aggregate.entrySet()) {
            var artifactId = entry.getKey().getUniqueArtifact().getArtifact().getArtifactId();
            var provider = entry.getValue();

            upToDate &= switch (provider) {
            case FileExtensionProvider file -> file.isUpToDate(targetFolder);
            case FolderExtensionProvider folder -> folder.isUpToDate(targetFolder.resolve(artifactId));
            default -> throw new IllegalArgumentException("Unexpected value: " + provider);
            };
        }

        return upToDate;
    }

    @Override
    public boolean update(Path targetFolder) throws IOException {
        resolveArtefactDependencies();

        List<Path> obsoleteContent = Files.list(targetFolder).collect(Collectors.toList());

        for (var entry : aggregate.entrySet()) {
            var artifactId = entry.getKey().getUniqueArtifact().getArtifact().getArtifactId();
            var provider = entry.getValue();

            switch (provider) {
            case FileExtensionProvider file -> {
                file.update(targetFolder);
                obsoleteContent.remove(targetFolder.resolve(file.getFile().getName()));
            }
            case FolderExtensionProvider folder -> {
                Path path = targetFolder.resolve(artifactId);
                folder.update(path);
                obsoleteContent.remove(path);
            }
            default -> throw new IllegalArgumentException("Unexpected value: " + provider);
            }
            ;
        }

        obsoleteContent.forEach(p -> {
            try {
                logger.debug("Cleaning obsolete dependency {}", p);
                if (!deleteDirectory(p.toFile())) {
                    throw new IOException();
                }
            } catch (IOException e) {
                logger.error("Unable to delete obsolete dependency {}", p, e);
            }
        });
        return true;
    }

    private static boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    public String getGroupId() {
        return groupId;
    }

    protected void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    protected void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    public RepositoryClient getRepositoryClient() {
        return repositoryClient;
    }

    public void setRepositoryClient(RepositoryClient repositoryClient) {
        this.repositoryClient = repositoryClient;
    }

    @Override
    public String toString() {
        return "MavenExtensionProvider [groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version
                + ", resolvedArtifact=" + resolvedArtifact + "]";
    }

}