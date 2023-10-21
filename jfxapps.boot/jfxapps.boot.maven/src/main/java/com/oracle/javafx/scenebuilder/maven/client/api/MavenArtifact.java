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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MavenArtifact implements Comparable<MavenArtifact> {

    private final static String COORDINATE_FORMAT = "%s:%s:%s";

    private final static Comparator<MavenArtifact> comparator = Comparator.comparing(MavenArtifact::getGroupId)
            .thenComparing(MavenArtifact::getArtifactId);

    private final String coordinates;
    private final String groupId;
    private final String artifactId;
    private final String version;
    private final Version internalVersion;
    private final MavenClassifier classifier;

    private final Repository repository;
    private final Path path;
    private List<MavenArtifact> dependencies;

    public static Builder builder() {
        return new Builder();
    }

    protected MavenArtifact(Builder builder) {
        this.coordinates = builder.coordinates;
        this.groupId = builder.groupId;
        this.artifactId = builder.artifactId;
        this.version = builder.version;
        this.repository = builder.repository;
        this.classifier = builder.classifier;
        this.path = builder.path;
        this.dependencies = builder.dependencies;
        this.internalVersion = Version.fromString(this.version);
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

    public Repository getRepository() {
        return repository;
    }

    public MavenClassifier getClassifier() {
        return classifier;
    }

    public Path getPath() {
        return path;
    }

    public List<MavenArtifact> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public boolean isResolved() {
        return repository != null;
    }

    public boolean hasPath() {
        return path != null && Files.exists(path);
    }

    public boolean hasDependenciesCollected() {
        return dependencies != null;
    }

    public List<Path> toJarList() {
        List<Path> files = new ArrayList<>();
        files.add(getPath());
        if (!getDependencies().isEmpty()) {
            files.addAll(getDependencies().stream()
                    .map(MavenArtifact::getPath)
                    .collect(Collectors.toList()));
        }
        return files;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classifier, coordinates, dependencies);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MavenArtifact other = (MavenArtifact) obj;
        return Objects.equals(classifier, other.classifier) && Objects.equals(coordinates, other.coordinates)
                && Objects.equals(dependencies, other.dependencies);
    }

    @Override
    public String toString() {
        return "MavenArtifact{" + "coordinates=" + coordinates + ", dependencies=" + dependencies + '}';
    }

    public static String getCoordinates(String groupId, String artefactId, String version) {
        return String.format(COORDINATE_FORMAT, groupId, artefactId, version);
    }

    @Override
    public int compareTo(MavenArtifact o) {
        if (o == null) {
            return 1;
        }

        int result = comparator.compare(this, o);

        if (result == 0) {
            if (this.internalVersion != null && o.internalVersion != null) {
                return this.internalVersion.compareTo(o.internalVersion);
            }
            return this.version.compareTo(o.version);
        }
        return result;
    }

    public static class Builder {
        private Repository repository;
        private Path path;
        private String coordinates;
        private String groupId;
        private String artifactId;
        private String version;
        private MavenClassifier classifier;
        private List<MavenArtifact> dependencies = new ArrayList<>();

        private void updateCoordinate() {
            this.coordinates = getCoordinates(this.groupId, this.artifactId, this.version);
        }
        private void updateFromCoordinate() {
            String[] parts = this.coordinates.split(":");
            this.groupId = parts.length > 0 ? parts[0] : "";
            this.artifactId = parts.length > 1 ? parts[1] : "";
            this.version = parts.length > 2 ? parts[2] : "";
        }
        public Builder withCoordinates(String coordinates) {
            this.coordinates = coordinates;
            updateFromCoordinate();
            return this;
        }
        public Builder withGroupId(String groupId) {
            this.groupId = groupId;
            updateCoordinate();
            return this;
        }
        public Builder withArtifactId(String artifactId) {
            this.artifactId = artifactId;
            updateCoordinate();
            return this;
        }
        public Builder withVersion(String version) {
            this.version = version;
            updateCoordinate();
            return this;
        }
        public Builder withRepository(Repository repository) {
            this.repository = repository;
            return this;
        }

        public Builder withClassifier(MavenClassifier classifier) {
            this.classifier = classifier;
            return this;
        }
        public Builder withPath(Path path) {
            this.path = path;
            return this;
        }
        public Builder withArtifact(MavenArtifact artifact) {
            withCoordinates(artifact.getCoordinates());
            withDependencies(artifact.getDependencies());
            return this;
        }
        public Builder withDependencies(List<MavenArtifact> dependencies) {
            this.dependencies.addAll(dependencies);
            return this;
        }

        public MavenArtifact build() {
            return new MavenArtifact(this);
        }
    }

    private static class Version implements Comparable<Version> {

        //private final static Logger logger = LoggerFactory.getLogger(Version.class);

        private final static Comparator<Version> comparator = Comparator.comparing(Version::getMajor)
                    .thenComparing(Version::getMinor).thenComparing(Version::getPatch);

        private String major = "";
        private String minor = "";
        private String patch = "";

        static Version fromString(String version) {

            if (version == null) {
                return null;
            }

            Version v = new Version();
            String[] parts = version.split("\\.");

            if (parts.length >= 1) {
                v.major = String.format("%8s",parts[0]).replace(' ', '0');
            }
            if (parts.length >= 2) {
                v.minor = String.format("%8s",parts[1]).replace(' ', '0');
            }
            if (parts.length >= 3) {
                v.patch = String.format("%20s",parts[2]).replace(' ', '0');
            }
            return v;
        }

        public String getMajor() {
            return major;
        }

        public String getMinor() {
            return minor;
        }

        public String getPatch() {
            return patch;
        }

        @Override
        public int compareTo(Version o) {
            if (o == null) {
                return 1;
            }
            return comparator.compare(this, o);
        }
    }
}
