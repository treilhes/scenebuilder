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
package com.oracle.javafx.scenebuilder.maven.client.api;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.oracle.javafx.scenebuilder.maven.client.impl.MavenClientImpl;
import com.oracle.javafx.scenebuilder.maven.client.preset.MavenPresets;

public interface MavenClient {

    public static MavenClient newDefaultClient(File repositoryFolder) {
        return new MavenClientImpl(repositoryFolder, MavenPresets.getPresetRepositories());
    }

    public static MavenClient newClient(File repositoryFolder, List<Repository> repositories) {
        return new MavenClientImpl(repositoryFolder, repositories);
    }

    List<MavenArtifact> getAvailableVersions(String groupId, String artefactId);
    List<MavenArtifact> getAvailableVersions(String groupId, String artefactId, boolean onlyRelease);

    Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId);
    Optional<MavenArtifact> getLatestVersion(String groupId, String artefactId, boolean onlyRelease);

    Optional<Path> resolve(MavenArtifact artifact, MavenClassifier classifier);
    Map<MavenClassifier, Optional<Path>> resolve(MavenArtifact artifact, List<MavenClassifier> classifiers);
    MavenArtifact resolveWithDependencies(MavenArtifact artifact);

    void favorizeLocalResolution();

    public static File getDefaultUserM2Repository() {
        String m2Path = System.getProperty("user.home") + File.separator + ".m2" + File.separator + "repository"; // NOCHECK

        assert m2Path != null;

        File target = new File(m2Path);
        if (!target.exists()) {
            target.mkdirs();
        }
        return target;
    }

    public static File getDefaultTempM2Repository() {
        String m2Path = System.getProperty("java.io.tmpdir") + File.separator + "m2Tmp"; // NOCHECK

        assert m2Path != null;

        File target = new File(m2Path);
        if (!target.exists()) {
            target.mkdirs();
        }
        return target;
    }

    List<Repository> repositories();
    void add(Repository repository);
    void remove(Repository repository);
    String validate(Repository repository);

    Set<MavenArtifactId> search(String query);

    Set<Class<? extends RepositoryType>> repositoryTypes();
}
