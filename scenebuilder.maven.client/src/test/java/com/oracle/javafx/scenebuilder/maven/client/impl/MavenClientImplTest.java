package com.oracle.javafx.scenebuilder.maven.client.impl;

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

import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifact;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenClassifier;
import com.oracle.javafx.scenebuilder.maven.client.preset.MavenPresets;

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

        Map<MavenArtifact, Optional<Path>> resolved = client.resolveWithDependencies(artefact);

        assertTrue(resolved.size() > 1, "should contains more than one jar");
        resolved.values().stream().forEach(item -> {
            assertTrue(item.isPresent(), "should return a path");
            assertTrue(Files.exists(item.get()), "path should exists");
            assertTrue(Files.isRegularFile(item.get()), "path should be a file");
        });

    }

}
