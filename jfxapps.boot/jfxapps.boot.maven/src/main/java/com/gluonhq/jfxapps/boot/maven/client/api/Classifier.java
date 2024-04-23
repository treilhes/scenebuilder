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

public final class Classifier {

    public static final String POM_EXTENSION = "pom";
    public static final String JAR_EXTENSION = "jar";
    public static final String SHA1_EXTENSION = "sha1";

    public static final Classifier DEFAULT = new Classifier("", JAR_EXTENSION);
    public static final Classifier DEFAULT_SHA1 = new Classifier("", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final Classifier SOURCES = new Classifier("sources", JAR_EXTENSION);
    public static final Classifier SOURCES_SHA1 = new Classifier("sources", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final Classifier JAVADOC = new Classifier("javadoc", JAR_EXTENSION);
    public static final Classifier JAVADOC_SHA1 = new Classifier("javadoc", JAR_EXTENSION + "." + SHA1_EXTENSION);
    public static final Classifier POM = new Classifier("", POM_EXTENSION);
    public static final Classifier POM_SHA1 = new Classifier("", POM_EXTENSION + "." + SHA1_EXTENSION);

    private String classifier;
    private String extension;

    private Classifier(String classifier, String extension) {
        super();
        this.classifier = classifier;
        this.extension = extension;
    }

    private Classifier(Builder builder) {
        this(builder.classifier, builder.extension);
    }

    public String getClassifier() {
        return classifier;
    }

    public String getExtension() {
        return extension;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
        result = prime * result + ((extension == null) ? 0 : extension.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Classifier other = (Classifier) obj;
        if (classifier == null) {
            if (other.classifier != null)
                return false;
        } else if (!classifier.equals(other.classifier))
            return false;
        if (extension == null) {
            if (other.extension != null)
                return false;
        } else if (!extension.equals(other.extension))
            return false;
        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String classifier;
        private String extension;

        public Builder extension(String extension) {
            this.extension = extension;
            return this;
        }
        public Builder classifier(String classifier) {
            this.classifier = classifier;
            return this;
        }

        public Classifier build() {
            return new Classifier(this);
        }
    }
}
