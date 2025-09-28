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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/*
 * This interface defines the contract for a FolderType, which is responsible for determining
 * the type of folder based on a given path, creating folders and files, and managing file types.
 * It includes a factory for creating instances of FolderType and provides a default implementation.
 * It also allows for the addition of custom file types and supports recursive folder types.
 *
 * The FolderType interface is designed to be flexible and extensible, allowing developers
 * to define their own folder types and file types as needed.
 *
 * The Default implementation provides a basic folder type that applies to all paths
 * and allows for the addition of custom file types.
 * It also includes a factory for finding the appropriate FolderType
 * based on the path provided.
 *
 * The Builder class allows for the creation of custom FolderType instances
 * with specific applicability checks, folder creation logic, and file type handling.
 *
 * isDefinitionApplicable(Path path) checks if the FolderType applies to the given path.
 *
 * createFolder(Watcher watcher, Folder parent, Path path) creates a new Folder instance
 *
 * createFile(Folder parent, Path path) creates a new File instance based on the file type
 *
 * getFactory() returns the factory for creating the current FolderType instances.
 *
 */
public interface FolderType {

    String getId();

    /**
     * Checks if this FolderType is applicable to the given path.
     *
     * @param path the path to check
     * @return true if this FolderType applies to the path, false otherwise
     */
    boolean isDefinitionApplicable(Path path);

    /**
     * Creates a Folder instance for the given path.
     *
     * @param watcher the Watcher instance to use for monitoring file system events
     * @param parent the parent folder, can be null for root folders
     * @param path the path of the folder to create
     * @return a new Folder instance
     */
    Folder createFolder(Watcher watcher, Folder parent, Path path);

    /**
     * Creates a File instance for the given path.
     * If the folder is able to handle the file type it will return an instance otherwise it will return null
     * @param parent the parent folder of the file
     * @param path the path of the file to create
     * @return a new File instance
     */
    File createFile(Folder parent, Path path);

    /**
     * Returns the factory for creating instances of this FolderType.
     *
     * @return the factory for this FolderType
     */
    Factory getFactory();

    /**
     * List of inclusion patterns for files and folders in this folder.
     * Only files and folders matching these patterns will be processed.
     */
    List<Pattern> getInclusionPatterns();
    /**
     * List of exclusion patterns for files and folders in this folder.
     * Files and folders matching these patterns will not be processed.
     */
    List<Pattern> getExclusionPatterns();

    Builder copy();


    void registerFileType(FileType fileType);
    void unregisterFileType(FileType fileType);
    void addFileTypeListener(ListChangeListener<FileType> fileTypeListener);
    void removeFileTypeListener(ListChangeListener<FileType> fileTypeListener);

    void registerFolderType(FolderType folderType);
    void unregisterFolderType(FolderType folderType);
    void addFolderTypeListener(ListChangeListener<FolderType> folderTypeListener);
    void removeFolderTypeeListener(ListChangeListener<FolderType> folderTypeListener);

    /**
     * Factory interface for creating FolderType instances.
     * It provides a method to find the appropriate FolderType based on a given path.
     */
    public interface Factory {

        /**
         * Default factory instance that uses the Default FolderType.
         * This can be used when no specific FolderType is defined.
         */
        public static Factory newInstance() {
            return Factory.of(List.of(FolderType.generic()));
        }

        /**
         * Finds the FolderType that applies to the given path.
         *
         * @param path the path to check
         * @return the FolderType that applies to the path, or Default if none match
         */
        Optional<FolderType> findFolderType(Path path);

        List<FolderType> getFolderTypes();

        /**
         * Creates a Factory instance with the specified list of FolderTypes.
         *
         * @param folderTypes the list of FolderTypes to use
         * @return a new Factory instance
         */
        public static Factory of(List<FolderType> folderTypes) {
            return new Default(folderTypes);
        }

        /**
         * Creates a Factory instance with the specified FolderTypes.
         * This is a convenience method for creating a Factory with an array of FolderTypes.
         *
         * @param folderTypes the FolderTypes to use
         * @return a new Factory instance
         */
        public static Factory of(FolderType... folderTypes) {
            return new Default(Arrays.asList(folderTypes));
        }

        /**
         * Default implementation of the Factory interface.
         * It iterates through the provided FolderTypes to find the one that applies to the given path.
         */
        public class Default implements Factory {

            /**
             * The list of FolderTypes to check against the path.
             */
            private final List<FolderType> folderTypes = new ArrayList<>();

            public Default(List<FolderType> folderTypes) {
                Objects.requireNonNull(folderTypes, "folderTypes cannot be null");
                this.folderTypes.addAll(folderTypes);
            }

            /*
             * @inheritDoc
             */
            @Override
            public Optional<FolderType> findFolderType(Path path) {
                for (var type:folderTypes) {
                    if (type.isDefinitionApplicable(path)) {
                        return Optional.of(type);
                    }
                }
                return Optional.empty();
            }

            @Override
            public List<FolderType> getFolderTypes() {
                return folderTypes;
            }
        }
    }

    public static FolderType generic() {
        return FolderType.builder()
                .withId("GENERIC")
                .withFileType(FileType.generic())
                .withThisFolderType()
                .build();
    }

    /**
     * Creates a new Builder instance for constructing a FolderType.
     *
     * @return a new Builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder class for constructing FolderType instances.
     * It allows for the configuration of folder types, file types, applicability checks,
     * and folder creation logic.
     */
    public static class Builder {


        private String id = null;

        /**
         * Indicates whether this FolderType should be added as the first type in the list.
         * This is useful for defining a recursive folder type that includes itself.
         */
        private boolean addThisFolderType = false;

        /**
         * A predicate to check if this FolderType applies to a given path.
         * By default, it applies to all paths.
         */
        private Predicate<Path> isApplicable = path -> true;

        /**
         * A supplier for creating Folder instances of this type.
         * By default, it creates a new Folder with the provided parameters.
         */
        private FolderSupplier folderSupplier = (watcher, parent, path, handlers, type) -> new Folder(watcher, parent, path, handlers, type);

        private List<FolderFeatureHandler> featureHandlers = new ArrayList<>();
        /**
         * Lists to hold the FolderTypes and FileTypes associated with this FolderType.
         * These will be used to create folders and files of the specified types.
         */
        private final List<FolderType> folderTypes = new ArrayList<>();

        /**
         * List to hold the FileTypes associated with this FolderType.
         */
        private final List<FileType> fileTypes = new ArrayList<>();

        /**
         * List of inclusion patterns for files and folders in this folder.
         * Only files and folders matching these patterns will be processed.
         */
        private final List<Pattern> inclusionPattens = new ArrayList<>();
        /**
         * List of exclusion patterns for files and folders in this folder.
         * Files and folders matching these patterns will not be processed.
         */
        private final List<Pattern> exclusionPattens = new ArrayList<>();

        public Builder withId(String id) {
            this.id = id;
            return this;
        }

        /**
         * Adds the current FolderType to the list of folder types, allowing for recursive definitions.
         * This is useful when the FolderType needs to include itself in its definition.
         *
         * @return this Builder instance for method chaining
         */
        public Builder withThisFolderType() {
            this.addThisFolderType = true;
            return this;
        }

        /**
         * Adds a FolderType to the list of folder types for this FolderType.
         * This allows for the definition of nested folder types.
         * Types are checked in the order they are added,
         * @param folderType the FolderType to add
         * @return this Builder instance for method chaining
         */
        public Builder withFolderType(FolderType folderType) {
            folderTypes.add(folderType);
            return this;
        }

        /**
         * Adds a list of FolderTypes to the list of folder types for this FolderType.
         * This allows for the definition of multiple nested folder types at once.
         * Types are checked in the order they are added,
         * @param folderTypes the list of FolderTypes to add
         * @return this Builder instance for method chaining
         */
        public Builder withFolderTypes(Collection<FolderType> folderTypes) {
            this.folderTypes.addAll(folderTypes);
            return this;
        }

        /**
         * Adds a FileType to the list of file types for this FolderType.
         * This allows the FolderType to recognize and handle files of that type.
         *
         * @param fileType the FileType to add
         * @return this Builder instance for method chaining
         */
        public Builder withFileType(FileType fileType) {
            if (fileType != null) {
                fileTypes.add(fileType);
            }
            return this;
        }

        /**
         * Adds a list of FileTypes to the list of file types for this FolderType.
         * This allows the FolderType to recognize and handle multiple file types at once.
         *
         * @param fileTypes the list of FileTypes to add
         * @return this Builder instance for method chaining
         */
        public Builder withFileTypes(Collection<FileType> fileTypes) {
            this.fileTypes.addAll(fileTypes);
            return this;
        }

        /**
         * Sets the applicability predicate for this FolderType.
         * This predicate is used to determine if this FolderType applies to a given path.
         *
         * @param isApplicable the predicate to set
         * @return this Builder instance for method chaining
         */
        public Builder withIsApplicable(Predicate<Path> isApplicable) {
            this.isApplicable = isApplicable;
            return this;
        }

        /**
         * Sets the folder supplier for this FolderType.
         * This supplier is used to create Folder instances of this type.
         *
         * @param folderSupplier the folder supplier to set
         * @return this Builder instance for method chaining
         */
        public Builder withFolderSupplier(FolderSupplier folderSupplier) {
            this.folderSupplier = folderSupplier;
            return this;
        }

        /**
         * Adds an inclusion pattern for files and folders in this folder.
         * Files and folders matching this pattern will be processed.
         *
         * @param pattern the inclusion pattern to add
         */
        public Builder withInclusionPattern(String pattern) {
            inclusionPattens.add(Pattern.compile(pattern));
            return this;
        }
        public Builder withInclusionPatterns(List<String> patterns) {
            patterns.forEach(this::withInclusionPattern);
            return this;
        }
        public Builder withInclusionPatterns(String... patterns) {
            Arrays.stream(patterns).forEach(this::withInclusionPattern);
            return this;
        }
        /**
         * Adds an exclusion pattern for files and folders in this folder.
         * Files and folders matching this pattern will not be processed.
         *
         * @param pattern the exclusion pattern to add
         */
        public Builder withExclusionPattern(String pattern) {
            exclusionPattens.add(Pattern.compile(pattern));
            return this;
        }
        public Builder withExclusionPatterns(Collection<String> patterns) {
            patterns.forEach(this::withExclusionPattern);
            return this;
        }
        public Builder withExclusionPatterns(String... patterns) {
            Arrays.stream(patterns).forEach(this::withExclusionPattern);
            return this;
        }

        public <T extends FolderFeature> Builder withFeatureHandler(FolderFeatureHandler handler) {
            this.featureHandlers.add(handler);
            return this;
        }

        /**
         * Builds and returns a new FolderType instance based on the configured properties.
         * It creates a FolderType that applies to the specified paths, creates folders and files,
         * and includes the defined folder types and file types.
         *
         * @return a new FolderType instance
         */
        public FolderType build() {

            return new InternalFolderType(this);
        }



        private static class InternalFolderType implements FolderType {

            private static final Logger logger = LoggerFactory.getLogger(FolderType.class);

            private String id;

            private Predicate<Path> isApplicable;

            private FolderSupplier folderSupplier;

            private final ObservableList<FolderType> folderTypes = FXCollections.observableArrayList();

            private final ObservableList<FileType> fileTypes = FXCollections.observableArrayList();

            private final ObservableList<FolderFeatureHandler> featureHandlers = FXCollections.observableArrayList();

            private List<Pattern> inclusionPattens = new ArrayList<>();

            private List<Pattern> exclusionPattens = new ArrayList<>();

            private InternalFolderType(Builder builder) {
                this.id = builder.id;
                this.isApplicable = builder.isApplicable;
                this.folderSupplier = builder.folderSupplier;
                this.folderTypes.addAll(builder.folderTypes);
                this.fileTypes.addAll(builder.fileTypes);
                this.featureHandlers.addAll(builder.featureHandlers);
                this.inclusionPattens.addAll(builder.inclusionPattens);
                this.exclusionPattens.addAll(builder.exclusionPattens);

                if (builder.addThisFolderType) {
                    folderTypes.add(this);
                }
            }

            @Override
            public String getId() {
                return id != null ? id : this.getClass().getName();
            }

            @Override
            public boolean isDefinitionApplicable(Path path) {
                return isApplicable.test(path);
            }

            @Override
            public Folder createFolder(Watcher watcher, Folder parent, Path path) {
                logger.trace("Handling folder: {} with type: {}", path, this.getId());
                var folder = folderSupplier.createFolder(watcher, parent, path, featureHandlers, this);
                folder.applyFeatures();
                return folder;
            }

            @Override
            public File createFile(Folder parent, Path path) {

                for (var fileType : fileTypes) {
                    if (fileType.isApplicable(path)) {
                        logger.trace("File type {} is applicable for path: {}", fileType.getId(), path);
                        var file = fileType.createFile(parent, path);
                        file.applyFeatures();
                        return file;
                    }
                }

                logger.trace("Folder type {} has no file type for: {} , returning null", getId(), path);
                return null;
            }

            @Override
            public Factory getFactory() {
                return Factory.of(folderTypes);
            }

            @Override
            public List<Pattern> getInclusionPatterns() {
                return inclusionPattens;
            }

            @Override
            public List<Pattern> getExclusionPatterns() {
                return exclusionPattens;
            }

            @Override
            public Builder copy() {
                return FolderType.builder()
                        .withId(id)
                        .withIsApplicable(isApplicable)
                        .withFolderSupplier(folderSupplier)
                        .withFolderTypes(folderTypes)
                        .withFileTypes(fileTypes)
                        .withInclusionPatterns(inclusionPattens.stream().map(Pattern::pattern).toList())
                        .withExclusionPatterns(exclusionPattens.stream().map(Pattern::pattern).toList());
            }

            @Override
            public void registerFileType(FileType fileType) {
                if (fileType != null && !fileTypes.contains(fileType)) {
                    fileTypes.add(fileType);
                    logger.debug("Registered new file type: {} for folder type: {}", fileType.getId(), getId());
                } else {
                    logger.warn("File type {} is already registered for folder type: {}", fileType, getId());
                }
            }

            @Override
            public void unregisterFileType(FileType fileType) {
                fileTypes.remove(fileType);
            }

            @Override
            public void addFileTypeListener(ListChangeListener<FileType> fileTypeListener) {
                fileTypes.addListener(fileTypeListener);
            }

            @Override
            public void removeFileTypeListener(ListChangeListener<FileType> fileTypeListener) {
                fileTypes.removeListener(fileTypeListener);
            }

            @Override
            public void registerFolderType(FolderType folderType) {
                if (folderType != null && !folderTypes.contains(folderType)) {
                    folderTypes.add(folderType);
                    logger.debug("Registered new folder type: {} for folder type: {}", folderType.getId(), getId());
                } else {
                    logger.warn("Folder type {} is already registered for folder type: {}", folderType, getId());
                }
            }

            @Override
            public void unregisterFolderType(FolderType folderType) {
                folderTypes.remove(folderType);
            }

            @Override
            public void addFolderTypeListener(ListChangeListener<FolderType> folderTypeListener) {
                folderTypes.addListener(folderTypeListener);
            }

            @Override
            public void removeFolderTypeeListener(ListChangeListener<FolderType> folderTypeListener) {
                folderTypes.removeListener(folderTypeListener);
            }
        }
    }


    /**
     * Functional interface for creating Folder instances.
     * This allows for custom folder creation logic to be provided when defining a FolderType.
     */
    @FunctionalInterface
    public interface FolderSupplier {
        Folder createFolder(Watcher watcher, Folder parent, Path path, List<FolderFeatureHandler> handlers, FolderType type);
    }



}