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
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.core.context.annotation.EditorSingleton;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoryPathPreference;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifact;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifactId;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenClassifier;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenClient;
import com.oracle.javafx.scenebuilder.maven.client.api.Repository;
import com.oracle.javafx.scenebuilder.maven.client.api.RepositoryType;

import jakarta.annotation.PostConstruct;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

@EditorSingleton
public class MavenClientController implements com.oracle.javafx.scenebuilder.api.maven.MavenClient {

    private MavenClient client;
    private final MavenRepositoriesPreferences repositoryPreferences;
    private final MavenRepositoryPathPreference repositoryPathPreference;
    private final BooleanProperty searching = new SimpleBooleanProperty();

    // @formatter:off
    public MavenClientController(
            MavenRepositoriesPreferences repositoryPreferences,
            MavenRepositoryPathPreference repositoryPathPreference
            ) {
     // @formatter:on
        super();
        this.repositoryPreferences = repositoryPreferences;
        this.repositoryPathPreference = repositoryPathPreference;
    }

    @PostConstruct
    protected void postContruct() {
        if (repositoryPreferences == null && repositoryPathPreference == null) {
            client = MavenClient.newDefaultClient(MavenClient.getDefaultUserM2Repository());
        } else {
            List<Repository> prefs = repositoryPreferences.getRepositories();
            File repoPath = new File(repositoryPathPreference.getValue());
            client = MavenClient.newClient(repoPath, prefs);
        }
    }

    @Override
    public List<MavenArtifact> getAvailableVersions(String groupId, String artifactId) {
        return client.getAvailableVersions(groupId, artifactId);
    }

    public List<MavenArtifact> getAvailableVersions(String groupId, String artefactId, boolean onlyRelease) {
        return client.getAvailableVersions(groupId, artefactId, onlyRelease);
    }

    public Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId) {
        return client.getLatestVersion(groupId, artefactId);
    }

    public Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId, boolean onlyRelease) {
        return client.getLatestVersion(groupId, artefactId, onlyRelease);
    }

    public Optional<Path> resolve(MavenArtifact artifact, MavenClassifier classifier) {
        return client.resolve(artifact, classifier);
    }

    public Map<MavenClassifier, Optional<Path>> resolve(MavenArtifact artifact, List<MavenClassifier> classifiers) {
        return client.resolve(artifact, classifiers);
    }

    @Override
    public MavenArtifact resolveWithDependencies(MavenArtifact artifact) {
        return client.resolveWithDependencies(artifact);
    }

    public void favorizeLocalResolution() {
        client.favorizeLocalResolution();
    }

    @Override
    public List<Repository> repositories() {
        return client.repositories();
    }

    @Override
    public void add(Repository repository) {
        client.add(repository);
        repositoryPreferences.getRecordRepository(repository).writeToJavaPreferences();
    }

    @Override
    public void remove(Repository repository) {
        client.remove(repository);
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
    public Set<MavenArtifactId> search(String query) {
        SbPlatform.runOnFxThread(() -> searching.set(true));
        Set<MavenArtifactId> result = client.search(query);
        SbPlatform.runOnFxThread(() -> searching.set(false));
        return result;
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return client.repositoryTypes();
    }

}
