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

import static org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.AbstractTransferListener;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.JavaScopes;
import org.eclipse.aether.util.filter.DependencyFilterUtils;
import org.eclipse.aether.util.repository.AuthenticationBuilder;
import org.eclipse.aether.version.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClassifier;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;

public class MavenRepositorySystem {

    private static final Logger logger = LoggerFactory.getLogger(MavenRepositorySystem.class);

    private final static String ALL_VERSION_SEARCH = "[0,)";

    private final static String SNAPSHOT_SUFFIX = "snapshot";

    private RepositorySystem system;

    private DefaultRepositorySystemSession session;

    private final LocalRepository localRepo;

    private BasicRepositoryConnectorFactory basicRepositoryConnectorFactory;

    private final File userM2Repository;

    private final Map<String, Repository> repositories = new HashMap<>();
    private final Map<String, RemoteRepository> remoteRepositories = new HashMap<>();

    private boolean offline = false;

    public MavenRepositorySystem(File userM2RepositoryFolder, List<Repository> repositories) {

        this.userM2Repository = userM2RepositoryFolder;
        this.localRepo = new LocalRepository(userM2Repository);
        if (repositories != null) {
            repositories.stream().filter(r -> r != null).peek(r -> this.repositories.put(r.getId(), r))
                    .map(this::createRepository).forEach(r -> this.remoteRepositories.put(r.getId(), r));
        } else {
            offline = true;
        }
        initRepositorySystem();
    }

    public MavenRepositorySystem(File repositoryFolder) {
        this(repositoryFolder, null);
    }

    private void initRepositorySystem() {

        DefaultServiceLocator locator = MavenRepositorySystemUtils.newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);
        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                throw new RuntimeException(exception);
            }
        });

        basicRepositoryConnectorFactory = new BasicRepositoryConnectorFactory();
        basicRepositoryConnectorFactory.initService(locator);

        system = locator.getService(RepositorySystem.class);

        session = MavenRepositorySystemUtils.newSession();
        session.setOffline(offline);
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        session.setIgnoreArtifactDescriptorRepositories(true);
        session.setTransferListener(new AbstractTransferListener() {
            @Override
            public void transferSucceeded(TransferEvent event) {
                logger.info(event.toString());
            }

            @Override
            public void transferFailed(TransferEvent event) {
                logger.info(event.toString());
            }
        });

        session.setRepositoryListener(new AbstractRepositoryListener() {
            @Override
            public void artifactResolved(RepositoryEvent event) {
                logger.info(event.toString());
            }
        });

    }

    private List<RemoteRepository> getRepositories() {
        final List<RemoteRepository> list = new ArrayList<>();
        list.addAll(remoteRepositories.values().stream().collect(Collectors.toList()));
        return list;
    }

    private VersionRangeResult findVersionRangeResult(MavenArtifact artifact) {
        String coordinates = MavenArtifact.getCoordinates(artifact.getGroupId(), artifact.getArtifactId(),
                ALL_VERSION_SEARCH);
        Artifact localArtifact = new DefaultArtifact(coordinates);

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(localArtifact);
        rangeRequest.setRepositories(getRepositories());
        try {
            VersionRangeResult result = system.resolveVersionRange(session, rangeRequest);
            cleanMetadata(localArtifact);
            return result;
        } catch (VersionRangeResolutionException ex) {
        }
        return null;
    }

    public List<MavenArtifact> findVersions(MavenArtifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);

        return (result == null) ? new ArrayList<>()
                : result.getVersions().stream().map(v -> toLocalizedArtifact(result, artifact, v))
                        .collect(Collectors.toList());
    }

    public List<MavenArtifact> findReleases(MavenArtifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);

        return (result == null) ? new ArrayList<>()
                : result.getVersions().stream()
                        .filter(v -> !v.toString().toLowerCase(Locale.ROOT).contains(SNAPSHOT_SUFFIX))
                        .map(v -> toLocalizedArtifact(result, artifact, v)).collect(Collectors.toList());
    }

    public Optional<MavenArtifact> findLatestVersion(MavenArtifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);
        Version version = result.getHighestVersion();

        if (version == null) {
            return Optional.empty();
        }

        return Optional.of(toLocalizedArtifact(result, artifact, version));
    }

    public Optional<MavenArtifact> findLatestRelease(MavenArtifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);

        if (result != null) {
            return result.getVersions().stream()
                    .filter(v -> !v.toString().toLowerCase(Locale.ROOT).contains(SNAPSHOT_SUFFIX))
                    .sorted((v1, v2) -> v2.compareTo(v1)).findFirst()
                    .map(v -> toLocalizedArtifact(result, artifact, v));
        } else {
            return Optional.empty();
        }
    }

    private void cleanMetadata(Artifact artifact) {
        final String path = localRepo.getBasedir().getAbsolutePath() + File.separator
                + artifact.getGroupId().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
                + artifact.getArtifactId() + File.separator;
        final DefaultMetadata metadata = new DefaultMetadata("maven-metadata.xml", Metadata.Nature.RELEASE);
        getRepositories().stream()
                .map(r -> session.getLocalRepositoryManager().getPathForRemoteMetadata(metadata, r, "")).forEach(s -> {
                    File file = new File(path + s);
                    if (file.exists()) {
                        try {
                            Files.delete(file.toPath());
                            Files.delete(new File(path + s + ".sha1").toPath());
                        } catch (IOException ex) {
                        }
                    }
                });
    }

    public Map<MavenClassifier, Optional<Path>> resolveArtifacts(MavenArtifact artifact,
            List<MavenClassifier> classifiers) {

        final RemoteRepository remoteRepository;

        if (artifact.isResolved()) {
            Repository repository = artifact.getRepository();
            remoteRepository = remoteRepositories.get(repository.getId());
        } else {
            remoteRepository = null;
        }

        Map<MavenClassifier, Optional<Path>> result = new HashMap<>();

        classifiers.forEach(c -> result.put(c, resolveArtifactClassifier(remoteRepository, artifact, c)));

        return result;

    }

    private Optional<Path> resolveArtifactClassifier(RemoteRepository remoteRepository, MavenArtifact artifact,
            MavenClassifier classifier) {

        String groupId = artifact.getGroupId();
        String artefactId = artifact.getArtifactId();
        String version = artifact.getVersion();

        DefaultArtifact localArtifact = new DefaultArtifact(groupId, artefactId, classifier.getClassifier(),
                classifier.getExtension(), version);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(localArtifact);
        artifactRequest.setRepositories(remoteRepository == null ? getRepositories() : Arrays.asList(remoteRepository));

        try {
            ArtifactResult result = system.resolveArtifact(session, artifactRequest);
            if (result != null && result.getExceptions().isEmpty()) {
                return Optional.of(result.getArtifact().getFile().toPath());
            } else {
                result.getExceptions().forEach(ex -> logger.error("", ex));
            }

        } catch (ArtifactResolutionException ex) {
        }
        return Optional.empty();

    }

    public MavenArtifact resolveWithDependencies(MavenArtifact artifact) {

        String groupId = artifact.getGroupId();
        String artefactId = artifact.getArtifactId();
        String version = artifact.getVersion();
        MavenClassifier def = MavenClassifier.DEFAULT;

        DefaultArtifact localArtifact = new DefaultArtifact(groupId, artefactId, def.getClassifier(),
                def.getExtension(), version);

        DependencyFilter classpathFlter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE);
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(localArtifact, JavaScopes.COMPILE));
        collectRequest.setRepositories(getRepositories());

        DependencyRequest dependencyRequest = new DependencyRequest(collectRequest, classpathFlter);
        try {
            List<ArtifactResult> artifactResults = system.resolveDependencies(session, dependencyRequest)
                    .getArtifactResults();

            ArtifactResult main = artifactResults.get(0);
            List<MavenArtifact> dependencies = new ArrayList<>();

            artifactResults.stream().skip(1) // exclude jar itself
                    .forEach(a -> {
                        Artifact lArtefact = a.getArtifact();

                        MavenClassifier classifier = new MavenClassifier(lArtefact.getClassifier(), null);
                        MavenArtifact mArtefact = MavenArtifact.builder()
                                .withGroupId(lArtefact.getGroupId())
                                .withArtifactId(lArtefact.getArtifactId())
                                .withVersion(lArtefact.getVersion())
                                .withPath(lArtefact.getFile().toPath())
                                .withClassifier(classifier)
                                .build();


                        dependencies.add(mArtefact);
                    });

            MavenArtifact mainArtifact = MavenArtifact.builder()
                    .withArtifact(artifact)
                    .withPath(main.getArtifact().getFile().toPath())
                    .withDependencies(dependencies)
                    .build();

            return mainArtifact;
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return null;
    }

    private RemoteRepository createRepository(Repository repository) {
        Authentication auth = null;
        if (repository.getUser() != null && !repository.getUser().isEmpty() && repository.getPassword() != null
                && !repository.getPassword().isEmpty()) {
            auth = new AuthenticationBuilder().addUsername(repository.getUser()).addPassword(repository.getPassword())
                    .build();
        }

        RepositoryPolicy releasePolicy = new RepositoryPolicy();

        //TODO allow local search only for dev env
//        RepositoryPolicy snapshotPolicy = repository.getContentType() != Content.RELEASE || this.favorizeLocalResolution
//                ? new RepositoryPolicy(true, UPDATE_POLICY_NEVER, UPDATE_POLICY_NEVER)
//                : new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

        RepositoryPolicy snapshotPolicy = new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

        final RemoteRepository repo = new RemoteRepository.Builder(repository.getId(), "default", repository.getURL())
                .setReleasePolicy(releasePolicy).setSnapshotPolicy(snapshotPolicy).setAuthentication(auth).build();
        return repo;
    }

    public String validateRepository(Repository repository) {
        RemoteRepository remoteRepository = createRepository(repository);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(new DefaultArtifact("test:test:1.0"));
        artifactRequest.setRepositories(Arrays.asList(remoteRepository));
        try {
            system.resolveArtifact(session, artifactRequest);
        } catch (ArtifactResolutionException ex) {
            final String rootCauseMessage = ExceptionUtils.getRootCauseMessage(ex);
            if (rootCauseMessage != null && !rootCauseMessage.contains("ArtifactNotFoundException")) {
                return rootCauseMessage;
            }
        }

        return "";
    }

    private MavenArtifact toLocalizedArtifact(VersionRangeResult result, MavenArtifact artifact, Version version) {
        ArtifactRepository repo = result.getRepository(version);
        Repository repository = repositories.get(repo.getId());

        if (repository == null) {
            repository = Repository.builder().withId(repo.getId()).build();
        }

        return MavenArtifact.builder()
                .withRepository(repository)
                .withArtifact(artifact)
                .withVersion(version.toString()).build();
    }

}
