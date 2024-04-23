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
package com.oracle.javafx.scenebuilder.library.maven.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.Classifier;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient.Scope;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryManager;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoryPathPreference;

import jakarta.annotation.PostConstruct;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@ApplicationSingleton
public class MavenClientController implements com.oracle.javafx.scenebuilder.api.maven.MavenClient {

    private RepositoryClient client;
    private RepositoryManager repositoryManager;

    private final MavenRepositoriesPreferences repositoryPreferences;
    private final MavenRepositoryPathPreference repositoryPathPreference;
    private final BooleanProperty searching = new SimpleBooleanProperty();
    private JfxAppsPlatform platform;

    // @formatter:off
    public MavenClientController(
            RepositoryClient client,
            RepositoryManager repositoryManager,
            JfxAppsPlatform platform,
            MavenRepositoriesPreferences repositoryPreferences,
            MavenRepositoryPathPreference repositoryPathPreference
            ) {
     // @formatter:on
        super();
        this.client = client;
        this.repositoryManager = repositoryManager;
        this.platform = platform;
        this.repositoryPreferences = repositoryPreferences;
        this.repositoryPathPreference = repositoryPathPreference;
    }

    @PostConstruct
    protected void postContruct() {
        if (repositoryPreferences == null && repositoryPathPreference == null) {
            // client =
            // RepositoryClient.newDefaultClient(RepositoryClient.getDefaultUserM2Repository());
        } else {
            List<Repository> prefs = repositoryPreferences.getRepositories();
            File repoPath = new File(repositoryPathPreference.getValue());
            client = client.localPath(repoPath).repositories(prefs);
        }
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(String groupId, String artifactId) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getAvailableVersions(a, Scope.RELEASE_SNAPHOT);
    }

    public List<UniqueArtifact> getAvailableVersions(String groupId, String artifactId, boolean onlyRelease) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getAvailableVersions(a, onlyRelease ? Scope.RELEASE : Scope.RELEASE_SNAPHOT);
    }

    public Optional<UniqueArtifact> getLatestVersion(String groupId, String artifactId) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getLatestVersion(a, Scope.RELEASE_SNAPHOT);
    }

    public Optional<UniqueArtifact> getLatestVersion(String groupId, String artifactId, boolean onlyRelease) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getLatestVersion(a, onlyRelease ? Scope.RELEASE : Scope.RELEASE_SNAPHOT);
    }

    public Optional<ResolvedArtifact> resolve(UniqueArtifact artifact, Classifier classifier) {
        return client.resolve(artifact);
    }

    public Map<Classifier, Optional<ResolvedArtifact>> resolve(UniqueArtifact artifact, List<Classifier> classifiers) {
        return client.resolve(artifact, classifiers);
    }

    @Override
    public Optional<ResolvedArtifact> resolveWithDependencies(UniqueArtifact artifact) {
        return client.resolveWithDependencies(artifact);
    }

    @Override
    public List<Repository> repositories() {
        return client.repositories();
    }

    @Override
    public void add(Repository repository) {
        repositoryManager.add(repository);
        repositoryPreferences.getRecordRepository(repository).writeToJavaPreferences();
    }

    @Override
    public void remove(Repository repository) {
        repositoryManager.remove(repository);
        repositoryPreferences.removeRecord(repository.getId());
    }

    @Override
    public String validate(Repository repository) {
        return client.validate(repository);
    }

    @Override
    public BooleanProperty searchingProperty() {
        return searching;
    }

    @Override
    public Set<Artifact> search(String query) {
        SbPlatform.runOnFxThread(() -> searching.set(true));
        Set<Artifact> result = client.search(query);
        SbPlatform.runOnFxThread(() -> searching.set(false));
        return result;
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return client.repositoryTypes();
    }

}
