package com.oracle.javafx.scenebuilder.maven.client.impl;

import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifact;
import com.oracle.javafx.scenebuilder.maven.client.api.Repository;

public class LocalizedArtifact extends MavenArtifact {

    private final Repository repository;

    protected LocalizedArtifact(MavenArtifact artefact, Repository resolvedRepository) {
        super(artefact.getCoordinates());
        repository = resolvedRepository;
    }

    protected LocalizedArtifact(MavenArtifact artefact, String version, Repository resolvedRepository) {
        super(artefact.getGroupId(), artefact.getArtifactId(), version);
        repository = resolvedRepository;
    }

    protected Repository getRepository() {
        return repository;
    }


}
