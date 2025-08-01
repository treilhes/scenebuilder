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

/**
 * Represents a type of file in the filesystem, defining its extensions and how to create it.
 * This interface allows for flexible file type definitions and creation logic.
 */
public interface FileType {

    /**
     * Returns a list of file extensions associated with this file type.
     *
     * @return a list of file extensions, or null if no specific extensions are defined
     */
    List<String> getExtensions();

    /**
     * Creates a new file of this type in the specified parent folder at the given path.
     *
     * @param parent the parent folder where the file will be created
     * @param path the path where the file will be created
     * @return a new File instance representing the created file
     */
    File createFile(Folder parent, Path path);

    /**
     * Default implementation of the FileType interface, which does not define any specific extensions.
     * It provides a basic file creation logic that creates a File instance with the given parent and path.
     */
    public static class Default implements FileType {

        /**
         * Singleton instance of the Default FileType.
         */
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

    /**
     * Creates a FileType with the specified extensions and a custom file supplier.
     *
     * @param extensions the list of file extensions associated with this file type
     * @param fileSupplier the supplier that creates files of this type
     * @return a new FileType instance with the specified extensions and file supplier
     */
    public static FileType of(List<String> extensions, FileSupplier fileSupplier) {
        return builder()
                .withFileSupplier(fileSupplier)
                .withExtensions(extensions)
                .build();
    }

     /**
     * Creates a FileType builder to construct a FileType instance.
     *
     * @return a new Builder instance for creating a FileType
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing FileType instances.
     * It allows adding extensions and setting a custom file supplier.
     */
    public static class Builder {

        /**
         * List of file extensions associated with this FileType.
         */
        private final List<String> extensions = new ArrayList<>();

        /**
         * Supplier that creates files of this FileType.
         * The default implementation creates a File instance with the given parent and path.
         */
        private FileSupplier fileSupplier = (parent, path, type) -> new File(parent, path, type);

        /**
         * Adds a file extension to this FileType.
         *
         * @param extension the file extension to add
         * @return this Builder instance for method chaining
         */
        public Builder withExtension(String extension) {
            extensions.add(extension);
            return this;
        }

        /**
         * Adds multiple file extensions to this FileType.
         *
         * @param extensions the list of file extensions to add
         * @return this Builder instance for method chaining
         */
        public Builder withExtensions(List<String> extensions) {
            this.extensions.addAll(extensions);
            return this;
        }

        /**
         * Sets a custom file supplier for this FileType.
         *
         * @param fileSupplier the supplier that creates files of this type
         * @return this Builder instance for method chaining
         */
        public Builder withFileSupplier(FileSupplier fileSupplier) {
            this.fileSupplier = fileSupplier;
            return this;
        }

        /**
         * Builds and returns a new FileType instance with the specified properties.
         *
         * @return a new FileType instance
         */
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

    /**
     * Functional interface for creating files of a specific type.
     * This allows for custom file creation logic when implementing the FileType interface.
     */
    @FunctionalInterface
    public interface FileSupplier {
        File createFile(Folder parent, Path path, FileType type);
    }
}
