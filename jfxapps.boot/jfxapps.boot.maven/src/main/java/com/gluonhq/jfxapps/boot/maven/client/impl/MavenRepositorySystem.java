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

import static org.eclipse.aether.repository.RepositoryPolicy.CHECKSUM_POLICY_IGNORE;
import static org.eclipse.aether.repository.RepositoryPolicy.UPDATE_POLICY_ALWAYS;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.AbstractRepositoryListener;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystem;

import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.impl.DefaultServiceLocator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallationException;
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

import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.Classifier;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryManager;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;

public class MavenRepositorySystem {

    private static final Logger logger = LoggerFactory.getLogger(MavenRepositorySystem.class);

    private final static String ALL_VERSION_SEARCH = "[0,)";

    private final static String SNAPSHOT_SUFFIX = "snapshot";

    private RepositorySystem system;

    private DefaultRepositorySystemSession session;

    private final LocalRepository localRepo;

    private BasicRepositoryConnectorFactory basicRepositoryConnectorFactory;

    private final File userM2Repository;

    private final RepositoryManager repositoryManager;

    private boolean offline = false;

    public MavenRepositorySystem(File repositoryFolder, RepositoryManager repositoryManager, boolean offline) {

        this.userM2Repository = repositoryFolder;
        this.localRepo = new LocalRepository(userM2Repository);
        this.repositoryManager = repositoryManager;
        this.offline = repositoryManager == null ? true : offline;
        initRepositorySystem();
    }

    public MavenRepositorySystem(File repositoryFolder) {
        this(repositoryFolder, null, true);
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
        logger.info("Resolution is set to offline: {}", offline);
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
        return repositoryManager.repositories().stream().map(this::toRemoteRepository).map(Optional::get)
                .filter(Objects::nonNull).toList();
    }

    private VersionRangeResult findVersionRangeResult(Artifact artifact) {
        String coordinates = UniqueArtifact.getCoordinates(artifact.getGroupId(), artifact.getArtifactId(),
                ALL_VERSION_SEARCH);
        DefaultArtifact localArtifact = new DefaultArtifact(coordinates);

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact(localArtifact);
        rangeRequest.setRepositories(getRepositories());

        try {
            VersionRangeResult result = system.resolveVersionRange(session, rangeRequest);

            if (offline) {
                filterLocalresultOnly(result, localArtifact);
            }

            // cleanMetadata(localArtifact);
            return result;
        } catch (VersionRangeResolutionException ex) {
        }
        return null;
    }

    public List<UniqueArtifact> findVersions(Artifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);

        return (result == null) ? new ArrayList<>()
                : result.getVersions().stream().map(v -> toLocalizedArtifact(result, artifact, v))
                        .collect(Collectors.toList());
    }

    public List<UniqueArtifact> findReleases(Artifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);

        return (result == null) ? new ArrayList<>()
                : result.getVersions().stream()
                        .filter(v -> !v.toString().toLowerCase(Locale.ROOT).contains(SNAPSHOT_SUFFIX))
                        .map(v -> toLocalizedArtifact(result, artifact, v)).collect(Collectors.toList());
    }

    public Optional<UniqueArtifact> findLatestVersion(Artifact artifact) {
        VersionRangeResult result = findVersionRangeResult(artifact);
        Version version = result.getHighestVersion();

        if (version == null) {
            return Optional.empty();
        }

        return Optional.of(toLocalizedArtifact(result, artifact, version));
    }

    public Optional<UniqueArtifact> findLatestRelease(Artifact artifact) {
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

    private void filterLocalresultOnly(VersionRangeResult result, DefaultArtifact artifact) {
        var it = result.getVersions().listIterator();
        while (it.hasNext()) {
            var v = it.next();

            final String path = localRepo.getBasedir().getAbsolutePath() + File.separator
                    + artifact.getGroupId().replaceAll("\\.", Matcher.quoteReplacement(File.separator)) + File.separator
                    + artifact.getArtifactId() + File.separator + v;

            if (!new File(path).exists()) {
                it.remove();
            }
        }
    }

    public Optional<ResolvedArtifact> resolveArtifact(UniqueArtifact artifact) {
        final RemoteRepository remoteRepository = toRemoteRepository(artifact.getRepository()).orElse(null);
        return resolveArtifact(remoteRepository, artifact);
    }

    public Map<Classifier, Optional<ResolvedArtifact>> resolveArtifacts(UniqueArtifact artifact,
            List<Classifier> classifiers) {

        final RemoteRepository remoteRepository = toRemoteRepository(artifact.getRepository()).orElse(null);

        Map<Classifier, Optional<ResolvedArtifact>> result = new HashMap<>();

        classifiers.forEach(c -> {

            UniqueArtifact a = UniqueArtifact.builder().withArtifact(artifact.getArtifact()).withClassifier(c)
                    .withVersion(artifact.getVersion()).withRepository(artifact.getRepository()).build();

            result.put(c, resolveArtifact(remoteRepository, a));
        });

        return result;

    }

    private Optional<ResolvedArtifact> resolveArtifact(RemoteRepository remoteRepository, UniqueArtifact artifact) {

        String groupId = artifact.getArtifact().getGroupId();
        String artefactId = artifact.getArtifact().getArtifactId();
        String version = artifact.getVersion();
        Classifier classifier = artifact.getClassifier();

        DefaultArtifact localArtifact = new DefaultArtifact(groupId, artefactId, classifier.getClassifier(),
                classifier.getExtension(), version);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(localArtifact);
        artifactRequest.setRepositories(remoteRepository == null ? getRepositories() : Arrays.asList(remoteRepository));

        try {
            ArtifactResult result = system.resolveArtifact(session, artifactRequest);
            if (result != null && result.getExceptions().isEmpty()) {

                ResolvedArtifact resolved = ResolvedArtifact.builder().withArtifact(artifact)
                        .withPath(result.getArtifact().getFile().toPath()).build();

                return Optional.of(resolved);
            } else {
                result.getExceptions().forEach(ex -> logger.error("", ex));
            }

        } catch (ArtifactResolutionException ex) {
            logger.error("", ex);
        }
        return Optional.empty();

    }

    public Optional<ResolvedArtifact> resolveWithDependencies(UniqueArtifact artifact) {

        String groupId = artifact.getArtifact().getGroupId();
        String artefactId = artifact.getArtifact().getArtifactId();
        String version = artifact.getVersion();
        Classifier def = artifact.getClassifier();

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

            if (artifactResults.size() == 1 && offline) {
                // ensure pom is present to allow dependencies resolution or return empty
                var pom = UniqueArtifact.builder()
                        .withArtifact(artifact.getGroupId(), artifact.getArtifactId())
                        .withVersion(artifact.getVersion())
                        .withRepository(artifact.getRepository())
                        .withClassifier(Classifier.POM).build();
                if (resolveArtifact(pom).isEmpty()) {
                    return Optional.empty();
                }
            }

            List<ResolvedArtifact> dependencies = new ArrayList<>();

            artifactResults.stream().skip(1) // exclude jar itself
                    .forEach(a -> {
                        var lArtifact = a.getArtifact();

                        var classifier = Classifier.builder().withClassifier(lArtifact.getClassifier())
                                .withExtension(lArtifact.getExtension()).build();

                        var id = Artifact.builder().withGroupId(lArtifact.getGroupId())
                                .withArtifactId(lArtifact.getArtifactId()).build();

                        var unique = UniqueArtifact.builder().withArtifact(id).withClassifier(classifier)
                                .withVersion(lArtifact.getVersion()).build();

                        ResolvedArtifact mArtefact = ResolvedArtifact.builder().withArtifact(unique)
                                .withPath(lArtifact.getFile().toPath()).build();

                        dependencies.add(mArtefact);
                    });

            ResolvedArtifact mainArtifact = ResolvedArtifact.builder().withArtifact(artifact)
                    .withPath(main.getArtifact().getFile().toPath()).withDependencies(dependencies).build();

            return Optional.of(mainArtifact);
        } catch (Exception ex) {
            logger.error("", ex);
        }
        return Optional.empty();
    }

    private Optional<RemoteRepository> toRemoteRepository(Repository repository) {

        if (repository == null) {
            return Optional.empty();
        }

        Authentication auth = null;
        if (repository.getUser() != null && !repository.getUser().isEmpty() && repository.getPassword() != null
                && !repository.getPassword().isEmpty()) {
            auth = new AuthenticationBuilder().addUsername(repository.getUser()).addPassword(repository.getPassword())
                    .build();
        }

        RepositoryPolicy releasePolicy = new RepositoryPolicy();

        RepositoryPolicy snapshotPolicy = new RepositoryPolicy(true, UPDATE_POLICY_ALWAYS, CHECKSUM_POLICY_IGNORE);

        final RemoteRepository repo = new RemoteRepository.Builder(repository.getId(), "default", repository.getURL())
                .setReleasePolicy(releasePolicy).setSnapshotPolicy(snapshotPolicy).setAuthentication(auth).build();

        return Optional.of(repo);
    }

    public String validateRepository(Repository repository) {
        RemoteRepository remoteRepository = toRemoteRepository(repository).orElse(null);

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

    private UniqueArtifact toLocalizedArtifact(VersionRangeResult result, Artifact artifact, Version version) {
        ArtifactRepository repo = result.getRepository(version);

        Repository repository = repositoryManager.get(repo.getId()).orElse(null);

        return UniqueArtifact.builder().withRepository(repository).withArtifact(artifact)
                .withVersion(version.toString()).build();
    }

    public boolean install(ResolvedArtifact artifact) {

        var ua = artifact.getUniqueArtifact();
        var a = ua.getArtifact();

        String groupId = a.getGroupId();
        String artefactId = a.getArtifactId();
        String version = ua.getVersion();
        File file = artifact.getPath().toFile();

        Classifier classifier = ua.getClassifier();
        String ext = classifier.getExtension();
        String classif = classifier.getClassifier();

        var mArtifact = new DefaultArtifact(groupId, artefactId, classif, ext, version, Collections.emptyMap(), file);

        var request = new InstallRequest();
        request.addArtifact(mArtifact);

        try {
            system.install(session, request);
        } catch (InstallationException e) {
            logger.error("Unable to push artifact to local repository", e);
            return false;
        }
        return true;
    }

}
