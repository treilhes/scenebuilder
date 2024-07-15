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
package com.gluonhq.jfxapps.boot.maven.client.impl;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.Classifier;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryClient;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryManager;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig;
import com.gluonhq.jfxapps.boot.maven.client.type.Maven;
import com.gluonhq.jfxapps.boot.maven.client.type.Nexus;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;

@Component
public class MavenRepositoryClientImpl implements RepositoryClient {

    private static final Logger log = LoggerFactory.getLogger(MavenRepositoryClientImpl.class);
    private final RepositoryManager repositoryManager;
    private final RepositoryConfig config;
    private final SearchService searchService;

    private MavenRepositorySystem maven;
    private File repositoryFolder;
    private boolean offline;



    @Autowired
    protected MavenRepositoryClientImpl(JfxAppsPlatform platform, RepositoryManager repositoryManager, RepositoryConfig config) {
        this.config = config;
        this.repositoryFolder = config.getDirectory() != null ? config.getDirectory() : platform.defaultUserM2Repository();
        this.repositoryManager = repositoryManager;
        this.searchService = new SearchService();
        // FIXME: offline mode should be set to true when no connection is available
        // when no connection is available, the client wait for some timeouts which cause an extremely slow startup
        // i think it must be some automatic detection of the connection status and the status must be periodicaly checked
        // to switch back to online mode asap
        try {
            InetAddress address = InetAddress.getByName("8.8.8.8"); // Google's public DNS server
            this.offline = !address.isReachable(2000); // Timeout in milliseconds
        } catch (IOException e) {
            // Handle exceptions, possibly log them
        } finally {
            if (this.offline) {
                log.info("Unable to reach google dns fallback to Offline mode");
            }
        }

        this.maven = new MavenRepositorySystem(repositoryFolder, repositoryManager, this.offline);
    }

    private MavenRepositoryClientImpl(MavenRepositoryClientImpl client, RepositoryManager repositoryManager, File storage, boolean offline) {
        super();
        this.config = client.config;
        this.repositoryFolder = storage != null ? storage : client.repositoryFolder;
        this.repositoryManager = repositoryManager;
        this.searchService = client.searchService;
        this.maven = new MavenRepositorySystem(repositoryFolder, repositoryManager, offline);
    }

    @Override
    public RepositoryClient localOnly() {
        return new MavenRepositoryClientImpl(this, this.repositoryManager,  this.repositoryFolder, true);
    }

    @Override
    public RepositoryClient repositories(List<Repository> repositories) {
        return new MavenRepositoryClientImpl(this, RepositoryManager.readOnlyManager(repositories),  this.repositoryFolder, false);
    }

    @Override
    public RepositoryClient localPath(File path) {
        return new MavenRepositoryClientImpl(this, this.repositoryManager,  path, this.offline);
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(Artifact artifact) {
        return getAvailableVersions(artifact, Scope.RELEASE_SNAPHOT);
    }

    @Override
    public List<UniqueArtifact> getAvailableVersions(Artifact artifact,  Scope scope) {
        return switch (scope) {
        case RELEASE -> maven.findReleases(artifact);
        case RELEASE_SNAPHOT -> maven.findVersions(artifact);
        default -> throw new IllegalArgumentException("Unexpected value: " + scope);
        };
    }

    @Override
    public Optional<UniqueArtifact> getLatestVersion(Artifact artifact) {
        return getLatestVersion(artifact, Scope.RELEASE_SNAPHOT);
    }

    @Override
    public Optional<UniqueArtifact> getLatestVersion(Artifact artifact, Scope scope) {
        return switch (scope) {
        case RELEASE -> maven.findLatestRelease(artifact);
        case RELEASE_SNAPHOT -> maven.findLatestVersion(artifact);
        default -> throw new IllegalArgumentException("Unexpected value: " + scope);
        };
    }

    @Override
    public Map<Classifier, Optional<ResolvedArtifact>> resolve(UniqueArtifact artifact,
            List<Classifier> classifiers){
        return maven.resolveArtifacts(artifact, classifiers);
    }

    @Override
    public Map<Classifier, Optional<ResolvedArtifact>> resolve(ResolvedArtifact artifact, List<Classifier> classifiers) {
        return maven.resolveArtifacts(artifact.getUniqueArtifact(), classifiers);
    }


    @Override
    public Optional<ResolvedArtifact> resolve(UniqueArtifact artifact) {
        return maven.resolveArtifact(artifact);
    }

    @Override
    public Optional<ResolvedArtifact> resolveWithDependencies(UniqueArtifact artifact) {
        return maven.resolveWithDependencies(artifact);
    }

    @Override
    public Optional<ResolvedArtifact> resolveWithDependencies(ResolvedArtifact artifact) {
        return maven.resolveWithDependencies(artifact.getUniqueArtifact());
    }

    @Override
    public List<Repository> repositories() {
        return Collections.unmodifiableList(repositoryManager.repositories());
    }

    @Override
    public String validate(Repository repository) {
        return maven.validateRepository(repository);
    }

    @Override
    public Set<Artifact> search(String query) {
        return searchService.search(query, repositoryManager.repositories());
    }

    @Override
    public Set<Class<? extends RepositoryType>> repositoryTypes() {
        return Set.of(Maven.class, Nexus.class);
    }

    @Override
    public boolean deployToLocalRepository(ResolvedArtifact artifact) {
        return maven.install(artifact);
    }

}