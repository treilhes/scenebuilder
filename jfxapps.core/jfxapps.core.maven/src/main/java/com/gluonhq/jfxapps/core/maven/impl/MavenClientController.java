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
package com.gluonhq.jfxapps.core.maven.impl;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.Classifier;
import com.gluonhq.jfxapps.boot.api.maven.Repository;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient.VersionType;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryType;
import com.gluonhq.jfxapps.boot.api.maven.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.api.maven.UniqueArtifact;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.maven.preference.MavenRepositoriesPreferences;
import com.gluonhq.jfxapps.core.maven.preference.MavenRepositoryPathPreference;

import jakarta.annotation.PostConstruct;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@ApplicationSingleton
public class MavenClientController implements com.gluonhq.jfxapps.core.api.maven.MavenClient {

    private RepositoryClient client;
    private RepositoryManager repositoryManager;

    private final MavenRepositoriesPreferences repositoryPreferences;
    private final MavenRepositoryPathPreference repositoryPathPreference;
    private final BooleanProperty searching = new SimpleBooleanProperty();
    private JfxAppsPlatform platform;
    private JfxAppPlatform jfxAppPlatform;

    // @formatter:off
    public MavenClientController(
            RepositoryClient client,
            RepositoryManager repositoryManager,
            JfxAppsPlatform platform,
            JfxAppPlatform jfxAppPlatform,
            MavenRepositoriesPreferences repositoryPreferences,
            MavenRepositoryPathPreference repositoryPathPreference
            ) {
     // @formatter:on
        super();
        this.client = client;
        this.repositoryManager = repositoryManager;
        this.platform = platform;
        this.jfxAppPlatform = jfxAppPlatform;
        this.repositoryPreferences = repositoryPreferences;
        this.repositoryPathPreference = repositoryPathPreference;
    }

    @PostConstruct
    protected void postContruct() {
        if (repositoryPreferences == null && repositoryPathPreference == null) {
            // client =
            // RepositoryClient.newDefaultClient(RepositoryClient.getDefaultUserM2Repository());
        } else {
            List<Repository> prefs = repositoryPreferences.getValue();

            File repoPath = repositoryPathPreference.getValue() == null ? platform.defaultUserM2Repository()
                    : new File(repositoryPathPreference.getValue());

            client = client.localPath(repoPath).repositories(prefs);
        }
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(String groupId, String artifactId) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getAvailableVersions(a, VersionType.RELEASE_SNAPHOT);
    }

    public List<UniqueArtifact> getAvailableVersions(String groupId, String artifactId, boolean onlyRelease) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getAvailableVersions(a, onlyRelease ? VersionType.RELEASE : VersionType.RELEASE_SNAPHOT);
    }

    public Optional<UniqueArtifact> getLatestVersion(String groupId, String artifactId) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getLatestVersion(a, VersionType.RELEASE_SNAPHOT);
    }

    public Optional<UniqueArtifact> getLatestVersion(String groupId, String artifactId, boolean onlyRelease) {
        var a = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        return client.getLatestVersion(a, onlyRelease ? VersionType.RELEASE : VersionType.RELEASE_SNAPHOT);
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
        repositoryPreferences.getValue().add(repository);
        repositoryPreferences.save();
    }

    @Override
    public void remove(Repository repository) {
        repositoryManager.remove(repository);
        repositoryPreferences.getValue().remove(repository);
        repositoryPreferences.save();
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
        jfxAppPlatform.runOnFxThread(() -> searching.set(true));
        Set<Artifact> result = client.search(query);
        jfxAppPlatform.runOnFxThread(() -> searching.set(false));
        return result;
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return client.repositoryTypes();
    }

}
