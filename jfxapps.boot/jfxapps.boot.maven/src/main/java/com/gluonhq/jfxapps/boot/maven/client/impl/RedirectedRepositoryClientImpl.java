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
package com.gluonhq.jfxapps.boot.maven.client.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.Classifier;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig.Redirect;
import com.gluonhq.jfxapps.boot.mavenam.PomDependencyReader;

/**
 * When the property jfxapps.repository.redirect is provided, this component is engaged
 *
 */
@Component
@Primary
@ConditionalOnProperty(prefix = RepositoryConfig.PREFIX, name = "redirectionsEnabled")
public class RedirectedRepositoryClientImpl implements RepositoryClient {

    private final MavenRepositoryClientImpl client;
    private final RepositoryConfig config;
    private final RepositoryMapper mappers;

    public RedirectedRepositoryClientImpl(RepositoryConfig config, RepositoryMapper mappers,
            MavenRepositoryClientImpl client) {
        super();
        this.config = config;
        this.mappers = mappers;
        this.client = client;
    }

    private Optional<Redirect> findMatch(Artifact artifact) {
        return config.getRedirect().stream().filter(r -> Objects.equals(artifact.getGroupId(), r.groupId()))
                .filter(r -> Objects.equals(artifact.getArtifactId(), r.artifactId())).findAny();
    }

    @Override
    public RepositoryClient withRepositories(List<Repository> repositories) {
        return new RedirectedRepositoryClientImpl(config, mappers,
                (MavenRepositoryClientImpl) client.withRepositories(repositories));
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(Artifact artifact) {
        return findMatch(artifact).map(mappers::map).map(ResolvedArtifact::getUniqueArtifact).map(List::of)
                .orElseGet(() -> client.getAvailableVersions(artifact));
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(Artifact artifact, Scope scope) {
        return findMatch(artifact).map(mappers::map).map(ResolvedArtifact::getUniqueArtifact).map(List::of)
                .orElseGet(() -> client.getAvailableVersions(artifact, scope));
    }

    @Override
    public Optional<UniqueArtifact> getLatestVersion(Artifact artifact) {
        return findMatch(artifact).map(mappers::map).map(ResolvedArtifact::getUniqueArtifact)
                .or(() -> client.getLatestVersion(artifact));
    }

    @Override
    public Optional<UniqueArtifact> getLatestVersion(Artifact artifact, Scope scope) {
        return findMatch(artifact).map(mappers::map).map(ResolvedArtifact::getUniqueArtifact)
                .or(() -> client.getLatestVersion(artifact, scope));
    }

    @Override
    public Optional<ResolvedArtifact> resolve(UniqueArtifact artifact) {
        return findMatch(artifact.getArtifact()).map(mappers::map).or(() -> client.resolve(artifact));
    }

    @Override
    public Optional<ResolvedArtifact> resolveWithDependencies(UniqueArtifact artifact) {

        Optional<ResolvedArtifact> redirect = findMatch(artifact.getArtifact()).map(mappers::map);

        if (redirect.isEmpty()) {
            return client.resolveWithDependencies(artifact);
        }

        var target = redirect.get();
        var path = target.getPath();
        var groupId = artifact.getArtifact().getGroupId();
        var artifactId = artifact.getArtifact().getArtifactId();

        String pomResource =String.format("META-INF/maven/%s/%s/pom.xml",groupId, artifactId);


        Set<ResolvedArtifact> dependencies = new HashSet<>();

        try (var pomStream = Files.isDirectory(path) ? pomStreamFromFolder(path, pomResource) : pomStreamFromJar(path, pomResource) ) {
            var pomDependencies = PomDependencyReader.read(pomStream);
            pomDependencies.stream()
                .filter(d -> !"test".equals(d.scope()))
                .filter(d -> !"system".equals(d.scope()))
                .filter(d -> !"provided".equals(d.scope()))
                .map(mappers::map)
                .map(this::resolveWithDependencies)
                .forEach(o -> dependencies.add(o.orElseThrow()));

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        };

        return Optional.of(ResolvedArtifact.builder()
                .withArtifact(target.getUniqueArtifact())
                .withPath(target.getPath())
                .withDependencies(new ArrayList<>(dependencies)).build());
    }

    private InputStream pomStreamFromJar(Path path, String pomResource)
            throws IOException, FileNotFoundException {
        try (FileInputStream fis = new FileInputStream(path.toFile());
                ZipInputStream zip = new ZipInputStream(fis)) {
            ZipEntry next = zip.getNextEntry();
            while (next != null) {
                if (pomResource.equals(next.getName())) {
                    return zip;
                }
                next = zip.getNextEntry();
            }
        }
        return null;
    }

    private FileInputStream pomStreamFromFolder(Path path, String pomResource)
            throws FileNotFoundException {
        return new FileInputStream(path.resolve(pomResource).toFile());
    }



    @Override
    public Map<Classifier, Optional<ResolvedArtifact>> resolve(UniqueArtifact artifact, List<Classifier> classifiers) {
        Optional<ResolvedArtifact> redirected = findMatch(artifact.getArtifact()).map(mappers::map);
        if (redirected.isPresent()) {
            return Map.of(Classifier.DEFAULT, redirected);
        } else {
            return client.resolve(artifact, classifiers);
        }
    }

    @Override
    public void favorizeLocalResolution() {
        client.favorizeLocalResolution();
    }

    @Override
    public boolean deployToLocalRepository(ResolvedArtifact artifact) {
        return client.deployToLocalRepository(artifact);
    }

    @Override
    public List<Repository> repositories() {
        return client.repositories();
    }

    @Override
    public String validate(Repository repository) {
        return client.validate(repository);
    }

    @Override
    public Set<Artifact> search(String query) {
        // maybe redirection must contribute to search here, wait and see
        return client.search(query);
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return client.repositoryTypes();
    }

}
