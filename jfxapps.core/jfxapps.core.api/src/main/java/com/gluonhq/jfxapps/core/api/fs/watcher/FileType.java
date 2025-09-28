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
package com.gluonhq.jfxapps.core.api.fs.watcher;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * Represents a type of file in the filesystem, defining its extensions and how to create it.
 * This interface allows for flexible file type definitions and creation logic.
 */
public interface FileType {

    /**
     * The default implementation creates a File instance with the given parent and path.
     */
    public FileSupplier DEFAULT_FILE_SUPPLIER = (parent, path, handlers, type) -> new File(parent, path, handlers, type);

    boolean isApplicable(Path path);

    String getId();

    List<Pattern> getPatterns();


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

    void registerFeatureHandler(FileFeatureHandler handler);
    void unregisterFeatureHandler(FileFeatureHandler handler);
    void addFeatureHandlerListener(ListChangeListener<FileFeatureHandler> handlerListener);
    void removeFeatureHandlerListener(ListChangeListener<FileFeatureHandler> handlerListener);

    public static FileType generic() {
        return builder()
                .withFileSupplier(DEFAULT_FILE_SUPPLIER)
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

        private String id;

        private final List<String> patterns = new ArrayList<>();

        /**
         * List of file extensions associated with this FileType.
         */
        private final List<String> extensions = new ArrayList<>();

        /**
         * Supplier that creates files of this FileType.
         * The default implementation creates a File instance with the given parent and path.
         */
        private FileSupplier fileSupplier = DEFAULT_FILE_SUPPLIER;

        private List<FileFeatureHandler> featureHandlers = new ArrayList<>();

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

        public Builder withNamePattern(String pattern) {
            patterns.add(pattern);
            return this;
        }

        public Builder withNamePatterns(List<String> patterns) {
            this.patterns.addAll(patterns);
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
         * Sets the name of this FileType.
         *
         * @param id the name to set for this FileType
         * @return this Builder instance for method chaining
         */
        public Builder withName(String id) {
            this.id = id;
            return this;
        }

        public Builder withFeatureHandler(FileFeatureHandler featureHandler) {
            this.featureHandlers.add(featureHandler);
            return this;
        }

        /**
         * Builds and returns a new FileType instance with the specified properties.
         *
         * @return a new FileType instance
         */
        public FileType build() {
            return new InternalFileType(this);
        }

        private static class InternalFileType implements FileType {

            private final String id;
            private final List<Pattern> patterns = new ArrayList<>();
            private final List<String> extensions = new ArrayList<>();
            private final ObservableList<FileFeatureHandler> featureHandlers = FXCollections.observableArrayList();
            private FileSupplier fileSupplier = DEFAULT_FILE_SUPPLIER;

            public InternalFileType(Builder builder) {
                this.id = builder.id;
                this.extensions.addAll(builder.extensions);
                this.featureHandlers.addAll(builder.featureHandlers);
                this.fileSupplier = builder.fileSupplier;

                if (builder.patterns != null) {
                    for (String pattern : builder.patterns) {
                        this.patterns.add(Pattern.compile(pattern));
                    }
                }
            }

            @Override
            public boolean isApplicable(Path path) {
                var patternMatch = patterns.stream().anyMatch(pattern -> pattern.matcher(path.getFileName().toString()).matches());

                if (patternMatch) {
                    return true;
                }

                // Check if the file has an extension that matches any of the defined extensions
                var extension = FileUtils.getFileExtension(path);
                for (String ext : extensions) {
                    if (ext.equalsIgnoreCase(extension)) {
                        return true;
                    }
                }
                // If no patterns are defined, we consider the file type applicable if it has no extensions
                return patterns.isEmpty() && extensions.isEmpty();
            }

            @Override
            public List<String> getExtensions() {
                return extensions;
            }

            @Override
            public File createFile(Folder parent, Path path) {
                var file = fileSupplier.createFile(parent, path, featureHandlers, this);
                return file;
            }

            @Override
            public List<Pattern> getPatterns() {
                return this.patterns;
            }

            @Override
            public String getId() {
                return id != null ? id : this.getClass().getName();
            }

            @Override
            public void registerFeatureHandler(FileFeatureHandler handler) {
                featureHandlers.add(handler);
            }

            @Override
            public void unregisterFeatureHandler(FileFeatureHandler handler) {
                featureHandlers.remove(handler);
            }

            @Override
            public void addFeatureHandlerListener(ListChangeListener<FileFeatureHandler> handlerListener) {
                featureHandlers.addListener(handlerListener);
            }

            @Override
            public void removeFeatureHandlerListener(ListChangeListener<FileFeatureHandler> handlerListener) {
                featureHandlers.removeListener(handlerListener);
            }
        }
    }

    /**
     * Functional interface for creating files of a specific type.
     * This allows for custom file creation logic when implementing the FileType interface.
     */
    @FunctionalInterface
    public interface FileSupplier {
        File createFile(Folder parent, Path path, List<FileFeatureHandler> handlers, FileType type);
    }
}
