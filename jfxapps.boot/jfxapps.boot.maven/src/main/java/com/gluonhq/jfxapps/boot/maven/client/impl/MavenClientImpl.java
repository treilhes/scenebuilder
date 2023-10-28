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

import java.io.File;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifactId;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClassifier;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClient;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.boot.maven.client.type.Maven;
import com.gluonhq.jfxapps.boot.maven.client.type.Nexus;

public class MavenClientImpl implements MavenClient {

    private MavenRepositorySystem onlineMaven;
    private MavenRepositorySystem offlineMaven;
    private MavenRepositorySystem maven;
    private File repositoryFolder;
    private final List<Repository> repositories;
    private final SearchService searchService;

    public MavenClientImpl(File repositoryFolder, List<Repository> repositories){
        this.repositories = repositories;
        this.repositoryFolder = repositoryFolder;
        this.searchService = new SearchService();
        this.offlineMaven = new MavenRepositorySystem(repositoryFolder);
        updateSystem();
        this.maven = onlineMaven;
    }

    private void updateSystem() {
        this.onlineMaven = new MavenRepositorySystem(repositoryFolder, repositories);
    }

    @Override
    public void favorizeLocalResolution() {
        this.maven = offlineMaven;
    }

    @Override
    public List<MavenArtifact> getAvailableVersions(String groupId, String artefactId) {
        return getAvailableVersions(groupId, artefactId, false);
    }

    @Override
    public List<MavenArtifact> getAvailableVersions(String groupId, String artefactId, boolean onlyRelease) {
        MavenArtifact artifact = MavenArtifact.builder().withGroupId(groupId).withArtifactId(artefactId)
                .build();
        if (onlyRelease) {
            return maven.findReleases(artifact);
        } else {
            return maven.findVersions(artifact);
        }
    }

    @Override
    public Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId) {
        return getLatestVersion(groupId, artefactId, false);
    }

    @Override
    public Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId, boolean onlyRelease) {
        MavenArtifact artifact = MavenArtifact.builder().withGroupId(groupId).withArtifactId(artefactId)
                .build();
        if (onlyRelease) {
            return maven.findLatestRelease(artifact);
        } else {
            return maven.findLatestVersion(artifact);
        }
    }

    @Override
    public Optional<Path> resolve(MavenArtifact artifact, MavenClassifier classifier) {
        Map<MavenClassifier, Optional<Path>> result = maven.resolveArtifacts(artifact, Arrays.asList(classifier));
        return result.get(classifier);
    }

    @Override
    public Map<MavenClassifier, Optional<Path>> resolve(MavenArtifact artifact, List<MavenClassifier> classifiers) {
        return maven.resolveArtifacts(artifact, classifiers);
    }

    @Override
    public MavenArtifact resolveWithDependencies(MavenArtifact artifact) {
        return maven.resolveWithDependencies(artifact);
    }

    @Override
    public List<Repository> repositories() {
        return Collections.unmodifiableList(repositories);
    }

    @Override
    public void add(Repository repository) {
        repositories.add(repository);
        updateSystem();
    }

    @Override
    public void remove(Repository repository) {
        repositories.remove(repository);
        updateSystem();
    }

    @Override
    public String validate(Repository repository) {
        return maven.validateRepository(repository);
    }

    @Override
    public Set<MavenArtifactId> search(String query) {
        return searchService.search(query, repositories);
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return Set.of(Maven.class, Nexus.class);
    }


}