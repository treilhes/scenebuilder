/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.app.devtools.modelv2;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public interface FileType {

    List<String> getExtensions();
    File createFile(Folder parent, Path path);

    public static class Default implements FileType {

        public static final FileType INSTANCE = new Default();

        @Override
        public List<String> getExtensions() {
            return null;
        }

        @Override
        public File createFile(Folder parent, Path path) {
            return new File(parent, path, this);
        }
    }

    public static FileType of(List<String> extensions, FileSupplier fileSupplier) {
        return builder()
                .withFileSupplier(fileSupplier)
                .withExtensions(extensions)
                .build();
    }
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final List<String> extensions = new ArrayList<>();
        private FileSupplier fileSupplier = (parent, path, type) -> new File(parent, path, type);

        public Builder withExtension(String extension) {
            extensions.add(extension);
            return this;
        }

        public Builder withExtensions(List<String> extensions) {
            this.extensions.addAll(extensions);
            return this;
        }

        public Builder withFileSupplier(FileSupplier fileSupplier) {
            this.fileSupplier = fileSupplier;
            return this;
        }

        public FileType build() {

            return new FileType() {
                @Override
                public List<String> getExtensions() {
                    return extensions;
                }

                @Override
                public File createFile(Folder parent, Path path) {
                    return fileSupplier.createFile(parent, path, this);
                }
            };
        }
    }

    @FunctionalInterface
    public interface FileSupplier {
        File createFile(Folder parent, Path path, FileType type);
    }
}
