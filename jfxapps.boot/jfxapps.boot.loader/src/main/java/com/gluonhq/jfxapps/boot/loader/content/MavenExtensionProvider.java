/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
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

    public MavenExtensionProvider(String groupId, String artifactId, String version) {
        super();
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    private void resolveArtefact() {
        if (mavenArtifact == null) {
            mavenArtifact = UniqueArtifact.builder().withArtifact(groupId, artifactId).withVersion(version)
                    .build();
        }
    }

    private void resolveArtefactDependencies() {
        resolveArtefact();

        if (resolvedArtifact == null) {
            resolvedArtifact = repositoryClient.resolveWithDependencies(mavenArtifact).orElse(null);
        }
    }

    @Override
    public boolean isValid() {
        resolveArtefact();
        return mavenArtifact != null;
    }

    @Override
    public boolean isUpToDate(Path targetFolder) {
        resolveArtefactDependencies();

        Predicate<Path> m2AndLocalMatches = p -> Utils.isFileUpToDate(p, targetFolder.resolve(p.getFileName()));

        boolean upToDate = m2AndLocalMatches.test(resolvedArtifact.getPath()) &&
                resolvedArtifact.getDependencies().stream()
                .allMatch(d -> m2AndLocalMatches.test(d.getPath()));

        return upToDate;
    }

    @Override
    public boolean update(Path targetFolder) throws IOException {
        resolveArtefactDependencies();

        List<Path> content = Files.list(targetFolder).collect(Collectors.toList());

        Set<ResolvedArtifact> artifacts = new HashSet<>();
        artifacts.add(resolvedArtifact);
        artifacts.addAll(resolvedArtifact.getDependencies());

        for (ResolvedArtifact artifact:artifacts) {

            //just update from maven
            Path source = artifact.getPath();
            Path target = targetFolder.resolve(source.getFileName());
            if (!Utils.isFileUpToDate(source, target)) {
                logger.debug("Copying dependency to {}", target);
                Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            }
            content.remove(target);

        }

        content.forEach(p -> {
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

}
