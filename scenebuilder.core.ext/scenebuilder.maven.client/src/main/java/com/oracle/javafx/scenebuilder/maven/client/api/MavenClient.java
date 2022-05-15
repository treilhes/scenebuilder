package com.oracle.javafx.scenebuilder.maven.client.api;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.oracle.javafx.scenebuilder.maven.client.impl.MavenClientImpl;

public interface MavenClient {

    public static MavenClient newDefault(File repositoryFolder, List<Repository> repositories) {
        return new MavenClientImpl(repositoryFolder, repositories);
    }

    List<MavenArtifact> getAvailableVersions(String groupId, String artefactId);
    List<MavenArtifact> getAvailableVersions(String groupId, String artefactId, boolean onlyRelease);

    Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId);
    Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId, boolean onlyRelease);

    Optional<Path> resolve(MavenArtifact artifact, MavenClassifier classifier);
    Map<MavenClassifier, Optional<Path>> resolve(MavenArtifact artifact, List<MavenClassifier> classifiers);

    Map<MavenArtifact, Optional<Path>> resolveWithDependencies(MavenArtifact artifact);
}
