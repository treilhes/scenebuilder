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

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.boot.maven.client.api.MavenArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClassifier;
import com.gluonhq.jfxapps.boot.maven.client.api.MavenClient;
import com.gluonhq.jfxapps.boot.maven.client.impl.MavenClientImpl;
import com.gluonhq.jfxapps.boot.maven.client.preset.MavenPresets;

/**
 * NEED TO BE ONLINE
 * The Class MavenClientImplTest.
 */
class MavenClientImplTest {

    private static final String testGroupId = "org.apache.commons";
    private static final String testArtifactId = "commons-lang3";

    @TempDir
    File tempRepoDir;

    MavenClientImpl client;

    @BeforeEach
    public void beforeEach() {
        System.out.println(tempRepoDir.getAbsolutePath());

        client = new MavenClientImpl(tempRepoDir, MavenPresets.getPresetRepositories());
        //client = new MavenClientImpl(MavenClient.getDefaultUserM2Repository(), MavenPresets.getPresetRepositories());
        //client.favorizeLocalResolution();
    }

    @Test
    void shouldFindSomeVersions() {
        List<MavenArtifact> versions = client.getAvailableVersions(testGroupId, testArtifactId);
        assertTrue(versions.size() > 0, "should find some versions");
    }

    @Test
    void shouldFindLatestVersions() {
        Optional<MavenArtifact> version = client.getLatestVersion(testGroupId, testArtifactId);
        assertTrue(version.isPresent(), "should find some versions");
    }

    @Test
    void shouldResolveJarOfSomeVersion() {
        List<MavenArtifact> versions = client.getAvailableVersions(testGroupId, testArtifactId);
        MavenArtifact artefact = versions.get(0);

        Optional<Path> resolved = client.resolve(artefact, MavenClassifier.DEFAULT);

        assertTrue(resolved.isPresent(), "should return a path");
        assertTrue(Files.exists(resolved.get()), "path should exists");
        assertTrue(Files.isRegularFile(resolved.get()), "path should be a file");
    }

    @Test
    void shouldResolveJarAndSourceOfSomeVersion() {
        List<MavenArtifact> versions = client.getAvailableVersions(testGroupId, testArtifactId);
        MavenArtifact artefact = versions.get(0);

        Map<MavenClassifier, Optional<Path>> resolved = client.resolve(artefact,
                Arrays.asList(MavenClassifier.DEFAULT, MavenClassifier.JAVADOC));

        resolved.values().forEach(item -> {
            assertTrue(item.isPresent(), "should return a path");
            assertTrue(Files.exists(item.get()), "path should exists");
            assertTrue(Files.isRegularFile(item.get()), "path should be a file");
        });
    }

    @Test
    void shouldResolveJarOfSomeVersionForTheWholeTree() {

        MavenArtifact version = client.getLatestVersion("org.apache.maven", "maven-resolver-provider").get();
        MavenArtifact artefact = version;

        MavenArtifact resolved = client.resolveWithDependencies(artefact);

        assertTrue(resolved.getDependencies().size() > 1, "should contains more than one jar");
        resolved.getDependencies().stream().forEach(item -> {
            assertTrue(item.hasPath(), "should return a path");
            assertTrue(Files.exists(item.getPath()), "path should exists");
            assertTrue(Files.isRegularFile(item.getPath()), "path should be a file");
        });

    }

}
