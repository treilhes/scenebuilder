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

package com.gluonhq.jfxapps.boot.maven.client.api;

import java.util.Comparator;
import java.util.Objects;

public class UniqueArtifact implements Comparable<UniqueArtifact> {

    private final static String COORDINATE_FORMAT = "%s:%s";

    private final static Comparator<UniqueArtifact> comparator = Comparator
            .comparing(UniqueArtifact::getArtifact)
            .thenComparing(UniqueArtifact::getVersion);

    private final String coordinates;
    private final Artifact artifact;
    private final String version;
    private final Version internalVersion;
    private final Classifier classifier;
    private final Repository repository;

    public static Builder builder() {
        return new Builder();
    }

    protected UniqueArtifact(Builder builder) {
        this.coordinates = builder.coordinates;
        this.artifact = builder.artifact;
        this.version = builder.version;
        this.classifier = builder.classifier;
        this.internalVersion = Version.fromString(this.version);
        this.repository = builder.repository;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public Artifact getArtifact() {
        return artifact;
    }

    public String getVersion() {
        return version;
    }

    public Classifier getClassifier() {
        return classifier;
    }

    public Repository getRepository() {
        return repository;
    }

    /**
     * Localized on a specific repository
     * @return true if repository is set
     */
    public boolean isLocalized() {
        return repository != null;
    }

    @Override
    public int hashCode() {
        return Objects.hash(classifier, coordinates);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UniqueArtifact other = (UniqueArtifact) obj;
        return Objects.equals(classifier, other.classifier) && Objects.equals(coordinates, other.coordinates);
    }

    @Override
    public String toString() {
        return "MavenArtifact{" + "coordinates=" + coordinates  + '}';
    }

    public static String getCoordinates(String groupId, String artefactId, String version) {
        return String.format(COORDINATE_FORMAT, Artifact.getCoordinates(groupId, artefactId), version);
    }

    @Override
    public int compareTo(UniqueArtifact o) {
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
        private String coordinates;
        private Artifact artifact;
        private String version;
        private Classifier classifier = Classifier.DEFAULT;
        private Repository repository;

        private void updateCoordinate() {
            this.coordinates = getCoordinates(this.artifact.getGroupId(), this.artifact.getArtifactId(), this.version);
        }
        private void updateFromCoordinate() {
            String[] parts = this.coordinates.split(":");
            this.artifact = Artifact.builder()
                    .withGroupId(parts.length > 0 ? parts[0] : "")
                    .withArtifactId(parts.length > 1 ? parts[1] : "").build();
            this.version = parts.length > 2 ? parts[2] : "";
        }
        public Builder withCoordinates(String coordinates) {
            this.coordinates = coordinates;
            updateFromCoordinate();
            return this;
        }
        public Builder withArtifact(Artifact artifact) {
            this.artifact = artifact;
            updateCoordinate();
            return this;
        }
        public Builder withArtifact(String groupId, String artifactId) {
            this.artifact = Artifact.builder().withGroupId(groupId).withArtifactId(artifactId).build();
            updateCoordinate();
            return this;
        }
        public Builder withVersion(String version) {
            this.version = version;
            updateCoordinate();
            return this;
        }
        public Builder withClassifier(Classifier classifier) {
            this.classifier = classifier;
            return this;
        }
        public Builder withRepository(Repository repository) {
            this.repository = repository;
            return this;
        }
        public UniqueArtifact build() {
            return new UniqueArtifact(this);
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
