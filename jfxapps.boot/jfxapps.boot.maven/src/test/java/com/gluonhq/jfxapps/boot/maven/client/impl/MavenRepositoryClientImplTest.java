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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.Classifier;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.boot.api.maven.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.api.maven.UniqueArtifact;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.maven.client.config.RepositoryConfig;
import com.gluonhq.jfxapps.boot.maven.client.preset.MavenPresets;

/**
 * NEED TO BE ONLINE The Class MavenClientImplTest.
 */
@ExtendWith(MockitoExtension.class)
class MavenRepositoryClientImplTest {

    private static final String testGroupId = "org.apache.commons";
    private static final String testArtifactId = "commons-lang3";

    private static final Artifact testArtifact = Artifact.builder().groupId(testGroupId)
            .artifactId(testArtifactId).build();

    private static final Artifact testArtifactWithDependency = Artifact.builder().groupId("ch.qos.logback")
            .artifactId("logback-classic").build();


    @TempDir
    File tempRepoDir;

    MavenRepositoryClientImpl client;

    @Mock
    JfxAppsPlatform platform;

    @Mock
    RepositoryConfig config;

    @BeforeEach
    public void beforeEach() {
        Mockito.when(platform.defaultUserM2Repository()).thenReturn(tempRepoDir);
        System.out.println(tempRepoDir.getAbsolutePath());

        client = new MavenRepositoryClientImpl(platform, RepositoryManager.readOnlyManager(MavenPresets.getPresetRepositories()), config);
        // client = new MavenClientImpl(MavenClient.getDefaultUserM2Repository(),
        // MavenPresets.getPresetRepositories());
        // client.favorizeLocalResolution();
    }

    @Test
    void shouldFindSomeVersions() {

        List<UniqueArtifact> versions = client.getAvailableVersions(testArtifact);
        assertTrue(versions.size() > 0, "should find some versions");
    }

    @Test
    void shouldFindLatestVersions() {
        Optional<UniqueArtifact> version = client.getLatestVersion(testArtifact);
        assertTrue(version.isPresent(), "should find some versions");
    }

    @Test
    void shouldResolveJarOfSomeVersion() {
        List<UniqueArtifact> versions = client.getAvailableVersions(testArtifact);
        UniqueArtifact artefact = versions.get(0);

        Optional<ResolvedArtifact> resolved = client.resolve(artefact);

        assertTrue(resolved.isPresent(), "should return a path");
        assertTrue(Files.exists(resolved.get().getPath()), "path should exists");
        assertTrue(Files.isRegularFile(resolved.get().getPath()), "path should be a file");
    }

    @Test
    void shouldResolveJarAndSourceOfSomeVersion() {
        List<UniqueArtifact> versions = client.getAvailableVersions(testArtifact);
        UniqueArtifact artefact = versions.get(0);

        Map<Classifier, Optional<ResolvedArtifact>> resolved = client.resolve(artefact,
                Arrays.asList(Classifier.DEFAULT, Classifier.JAVADOC));

        resolved.values().forEach(item -> {
            assertTrue(item.isPresent(), "should return a path");
            assertTrue(Files.exists(item.get().getPath()), "path should exists");
            assertTrue(Files.isRegularFile(item.get().getPath()), "path should be a file");
        });
    }

    @Test
    void shouldResolveJarOfSomeVersionForTheWholeTree() {

        var search = Artifact.builder().groupId("org.apache.maven").artifactId("maven-resolver-provider")
                .build();

        UniqueArtifact version = client.getLatestVersion(search).get();
        UniqueArtifact artefact = version;

        Optional<ResolvedArtifact> resolved = client.resolveWithDependencies(artefact);

        assertTrue(resolved.isPresent(), "should be resolved");
        assertTrue(resolved.get().getDependencies().size() > 1, "should contains more than one jar");
        resolved.get().getDependencies().stream().forEach(item -> {
            assertTrue(item.hasPath(), "should return a path");
            assertTrue(Files.exists(item.getPath()), "path should exists");
            assertTrue(Files.isRegularFile(item.getPath()), "path should be a file");
        });

    }

    @Test
    void shouldDeployFileToLocalRepository() throws URISyntaxException {

        var r = this.getClass().getResource("somefake.file");
        var fakeFile = new File(r.toURI());

        var artifact = UniqueArtifact.builder()
                .artifact("some.group", "some.id")
                .version("1.0.0")
                .build();

        var resolved = ResolvedArtifact.builder().artifact(artifact).path(fakeFile.toPath()).build();

        var result = client.deployToLocalRepository(resolved);

        assertTrue(result, "should be successfull");

        File target = new File(tempRepoDir, "some/group/some.id/1.0.0/some.id-1.0.0.jar");

        assertTrue(target.exists(), "file exists in repository");
        assertTrue(target.length() > 0, "file must not be empty");

    }

    @Test
    void shouldDeployFileToLocalRepositoryWithClassifier() throws URISyntaxException {

        var r = this.getClass().getResource("somefake.file");
        var fakeFile = new File(r.toURI());

        var artifact = UniqueArtifact.builder()
                .artifact("some.group", "some.id")
                .version("1.0.0")
                .classifier(Classifier.builder().classifier("xxx").extension("aaa").build())
                .build();

        var resolved = ResolvedArtifact.builder().artifact(artifact).path(fakeFile.toPath()).build();

        var result = client.deployToLocalRepository(resolved);

        assertTrue(result, "should be successfull");

        File target = new File(tempRepoDir, "some/group/some.id/1.0.0/some.id-1.0.0-xxx.aaa");

        assertTrue(target.exists(), "file exists in repository");
        assertTrue(target.length() > 0, "file must not be empty");

    }

    @Test
    void shouldResolveFromLocalRepository() throws URISyntaxException {

        int[] index = {20,40,60};

        var available = client.getAvailableVersions(testArtifactWithDependency);

        assertTrue(available.size() > 60);

        client.resolve(available.get(index[0])); // only jar

        var artifact = available.get(index[1]);
        var a = UniqueArtifact.builder()
                .artifact(artifact.getGroupId(), artifact.getArtifactId())
                .version(artifact.getVersion())
                .classifier(Classifier.POM).build();
        client.resolve(available.get(index[1]));
        client.resolve(a); // jar and pom

        client.resolveWithDependencies(available.get(index[2])); // jar, pom and dependencies


        // turn off network resolution
        var c = client.localOnly();

        var availableLocaly = c.getAvailableVersions(testArtifactWithDependency);
        assertTrue(availableLocaly.size() == index.length);

        // 0 : only jar
        assertTrue(c.resolveWithDependencies(availableLocaly.get(0)).isEmpty());

        // 1 : jar and pom but missing dependencies
        assertTrue(c.resolveWithDependencies(availableLocaly.get(1)).isEmpty());

        // 2 : jar pom and dependencies
        var local = availableLocaly.get(2);
        var latestLocal = c.getLatestVersion(testArtifactWithDependency);
        var localWithDep = c.resolveWithDependencies(local).orElse(null);

        // localy resolved
        assertNotNull(localWithDep);
        // localy resolved with dependency
        assertTrue(!localWithDep.getDependencies().isEmpty());
        // local latest
        assertEquals(local, latestLocal.orElse(null));



    }
}
