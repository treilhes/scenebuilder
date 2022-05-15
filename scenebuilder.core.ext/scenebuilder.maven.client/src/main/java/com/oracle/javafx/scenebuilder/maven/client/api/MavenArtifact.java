/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MavenArtifact {

    private final static String COORDINATE_FORMAT = "%s:%s:%s";

    private final String coordinates;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private List<MavenArtifact> dependencies;

    public MavenArtifact(String coordinates) {
        this.coordinates = coordinates;

        String[] parts = coordinates.split(":");

        this.groupId = parts.length > 0 ? parts[0] : "";
        this.artifactId = parts.length > 1 ? parts[1] : "";
        this.version = parts.length > 2 ? parts[2] : "";
    }

    public MavenArtifact(String groupId, String artifactId, String version) {
        this(MavenArtifact.getCoordinates(groupId, artifactId, version));
    }

    public MavenArtifact(MavenArtifact artifact, List<MavenArtifact> dependencies) {
        this(artifact.getCoordinates());
        this.dependencies = dependencies;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }

    public List<MavenArtifact> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public boolean hasDependenciesCollected() {
        return dependencies != null;
    }

//    public List<Path> toJarList() {
//        List<Path> files = new ArrayList<>();
//        files.add(Paths.get(getPath()));
//        if (!getDependencies().isEmpty()) {
//            files.addAll(Stream
//                    .of(getDependencies().split(File.pathSeparator))
//                    .map(File::new)
//                    .filter(File::exists)
//                    .map(File::toPath)
//                    .collect(Collectors.toList()));
//        }
//        return files;
//    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.coordinates);
        hash = 97 * hash + Objects.hashCode(this.dependencies);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MavenArtifact other = (MavenArtifact) obj;
        if (!Objects.equals(this.coordinates, other.coordinates)) {
            return false;
        }
        return Objects.equals(this.dependencies, other.dependencies);
    }


    @Override
    public String toString() {
        return "MavenArtifact{" + "coordinates=" + coordinates + ", dependencies=" + dependencies + '}';
    }

    public static String getCoordinates(String groupId, String artefactId, String version) {
        return String.format(COORDINATE_FORMAT, groupId, artefactId, version);
    }
}
