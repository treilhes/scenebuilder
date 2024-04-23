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
package com.gluonhq.jfxapps.boot.maven.client.search;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.gluonhq.jfxapps.boot.maven.client.api.Artifact;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.boot.maven.client.preset.MavenPresets;
import com.gluonhq.jfxapps.boot.maven.client.type.Local;
import com.gluonhq.jfxapps.boot.maven.client.type.Maven;
import com.gluonhq.jfxapps.boot.maven.client.type.Nexus;

class RepositoryTypeTest {

    private static final String searchTerm = "gluon";

    @TempDir
    Path tempRepoDir;

    @Test
    void must_return_results_with_local() {
        Repository repo = Repository.builder().id(MavenPresets.LOCAL)
                .url(tempRepoDir.toString()).build();

        List<File> matches = List.of(
                new File(tempRepoDir.toFile(), String.format("some/%s/path/some.artifact/1.0.0/x.jar", searchTerm)),
                new File(tempRepoDir.toFile(), String.format("some/other/path/%s.artifact/1.0.0/x.jar", searchTerm))
                );

        matches.forEach(f -> {
            f.getParentFile().mkdirs();
            try {
                f.createNewFile();
            } catch (IOException e) {}
        });

        RepositoryType search = new Local();
        Set<Artifact> result = search.getCoordinates(repo, searchTerm);

        assertTrue(result != null && result.size() == matches.size());
        assertTrue(result.stream().allMatch(r -> r.getGroupId() != null));
        assertTrue(result.stream().allMatch(r -> r.getArtifactId() != null));
    }

    @Test
    void must_return_results_with_maven() {
        Repository repo = MavenPresets.getPresetRepositories().stream()
                .filter(r -> r.getId().equals(MavenPresets.MAVEN)).findFirst().get();
        RepositoryType search = new Maven();
        Set<Artifact> result = search.getCoordinates(repo, searchTerm);

        assertTrue(result != null && result.size() > 0);
        assertTrue(result.stream().allMatch(r -> r.getGroupId() != null));
        assertTrue(result.stream().allMatch(r -> r.getArtifactId() != null));
    }

    @Test
    void must_return_results_with_sonatype() {
        Repository repo = MavenPresets.getPresetRepositories().stream()
                .filter(r -> r.getId().equals(MavenPresets.SONATYPE)).findFirst().get();
        RepositoryType search = new Nexus();
        Set<Artifact> result = search.getCoordinates(repo, searchTerm);

        assertTrue(result != null && result.size() > 0);
        assertTrue(result.stream().allMatch(r -> r.getGroupId() != null));
        assertTrue(result.stream().allMatch(r -> r.getArtifactId() != null));
    }

    @Test
    void must_return_results_with_gluon() {
        Repository repo = MavenPresets.getPresetRepositories().stream()
                .filter(r -> r.getId().equals(MavenPresets.GLUON_NEXUS)).findFirst().get();
        RepositoryType search = new Nexus();
        Set<Artifact> result = search.getCoordinates(repo, searchTerm);

        assertTrue(result != null && result.size() > 0);
        assertTrue(result.stream().allMatch(r -> r.getGroupId() != null));
        assertTrue(result.stream().allMatch(r -> r.getArtifactId() != null));
    }

}
