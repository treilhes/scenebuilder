/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.emc4j.plugin.util;

import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;

public class PluginArtifactFactory {

    private final RepositorySystem repositorySystem;
    private final RepositorySystemSession repositorySession;
    private final List<RemoteRepository> projectRepositories;

    public PluginArtifactFactory(RepositorySystem repoSystem, RepositorySystemSession repoSession,
            List<RemoteRepository> remoteRepos) {
        this.repositorySystem = repoSystem;
        this.repositorySession = repoSession;
        this.projectRepositories = remoteRepos;
    }

    public PluginArtifact createArtifact(String groupId, String artifactId, String version) {
        return new PluginArtifact(this, groupId, artifactId, version);
    }

    public PluginArtifact createArtifact(String groupId, String artifactId, String version, String classifier) {
        return new PluginArtifact(this, groupId, artifactId, version, classifier);
    }

    Artifact resolveArtifact(String coords) throws Exception {
        ArtifactRequest request = new ArtifactRequest();
        DefaultArtifact artifact = new DefaultArtifact(coords);
        request.setArtifact(artifact);
        request.setRepositories(projectRepositories);

        ArtifactResult result = repositorySystem.resolveArtifact(repositorySession, request);
        return result.getArtifact();
    }

    List<Artifact> resolveDependencies(String coords) throws Exception {

        Dependency dependency = new Dependency(new DefaultArtifact(coords), "compile");

        // Collect request: what do we want, from where?
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(dependency);
        collectRequest.setRepositories(projectRepositories);

        // DependencyRequest wraps it
        DependencyRequest dependencyRequest = new DependencyRequest();
        dependencyRequest.setCollectRequest(collectRequest);

        // Resolve!
        DependencyResult result = repositorySystem.resolveDependencies(repositorySession, dependencyRequest);

        //CollectResult r = repositorySystem.collectDependencies(repositorySession, collectRequest);

        //tmpFindArtifact(r.getRoot(), "over");

        // To list of files
        List<Artifact> dependencies = result.getArtifactResults().stream()
            .map(artifactResult -> artifactResult.getArtifact())
            .collect(Collectors.toList());

        return dependencies;
    }

    public static String toCoordinate(String groupId, String artifactId, String version) {
        return String.format("%s:%s:%s", groupId, artifactId, version);
    }
}
