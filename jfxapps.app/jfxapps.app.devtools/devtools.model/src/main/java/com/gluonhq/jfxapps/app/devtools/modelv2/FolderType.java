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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;

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
     *
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
     * Factory interface for creating FolderType instances.
     * It provides a method to find the appropriate FolderType based on a given path.
     */
    public interface Factory {

        /**
         * Default factory instance that uses the Default FolderType.
         * This can be used when no specific FolderType is defined.
         */
        public static final Factory DEFAULT = Factory.of(List.of(FolderType.Default.INSTANCE));

        /**
         * Finds the FolderType that applies to the given path.
         *
         * @param path the path to check
         * @return the FolderType that applies to the path, or Default if none match
         */
        FolderType findFolderType(Path path);

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
            public FolderType findFolderType(Path path) {
                for (var type:folderTypes) {
                    if (type.isDefinitionApplicable(path)) {
                        return type;
                    }
                }
                return FolderType.Default.INSTANCE;
            }
        }
    }

    /**
     * Default implementation of the FolderType interface.
     * This implementation applies to all paths and allows for the addition of custom file types.
     * It provides a factory for creating instances of this FolderType.
     */
    public static class Default implements FolderType {

        /**
         * The singleton instance of the Default FolderType.
         * This instance can be used when no specific FolderType is defined.
         */
        public static final FolderType INSTANCE = new Default();
        /**
         * The factory for creating instances of this FolderType.
         * It uses the Default factory to create FolderType instances.
         */
        private static final Factory FACTORY = Factory.of(Default.INSTANCE);
        /**
         * A map of file extensions to their corresponding FileType instances.
         * This allows the Default FolderType to recognize and handle files of those types.
         */
        private final Map<String, FileType> fileTypeMap = new HashMap<>();

        /**
         * Adds a FileType to this FolderType's file type map, associating each of its extensions (case-insensitive)
         * with the FileType instance. This allows the FolderType to recognize and handle files of those types.
         *
         * @param fileType the FileType to add
         */
        public void addFileType(FileType fileType) {
            for (String ext : fileType.getExtensions()) {
                fileTypeMap.put(ext.toLowerCase(), fileType);
            }
        }

        /*
         * @inheritDoc
         */
        @Override
        public boolean isDefinitionApplicable(Path path) {
            return true;
        }

        /*
         * @inheritDoc
         */
        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path) {
            return new Folder(watcher, parent, path, this);
        }

        /*
         * @inheritDoc
         */
        @Override
        public File createFile(Folder parent, Path path) {
            var extension = FileUtils.getFileExtension(path);
            var fileType = fileTypeMap.get(extension.toLowerCase());
            if (fileType != null) {
                return fileType.createFile(parent, path);
            }
            return new File(parent, path, FileType.Default.INSTANCE);
        }

        /*
         * @inheritDoc
         */
        @Override
        public Factory getFactory() {
            return FACTORY;
        }

    }

    /**
     * Creates a FolderType instance based on the provided applicability predicate,
     * folder supplier, folder types, and file types.
     *
     * @param isApplicable the predicate to check if this FolderType applies to a given path
     * @param folderSupplier the supplier for creating folders of this type
     * @param folderTypes the list of FolderTypes that this FolderType can contain
     * @param fileTypes the list of FileTypes that this FolderType can handle
     * @return a new FolderType instance
     */
    public static FolderType of(Predicate<Path> isApplicable, FolderSupplier folderSupplier, List<FolderType> folderTypes, List<FileType> fileTypes) {
        return of(isApplicable, folderSupplier, folderTypes, fileTypes, false);
    }

    /**
     * Creates a FolderType instance based on the provided applicability predicate,
     * folder supplier, folder types, file types, and whether it is a recursive type.
     *
     * @param isApplicable the predicate to check if this FolderType applies to a given path
     * @param folderSupplier the supplier for creating folders of this type
     * @param folderTypes the list of FolderTypes that this FolderType can contain
     * @param fileTypes the list of FileTypes that this FolderType can handle
     * @param recursiveType whether this FolderType should be recursive
     * @return a new FolderType instance
     */
    public static FolderType of(Predicate<Path> isApplicable, FolderSupplier folderSupplier, List<FolderType> folderTypes, List<FileType> fileTypes, boolean recursiveType) {
        var builder = builder()
                .withIsApplicable(isApplicable)
                .withFolderSupplier(folderSupplier)
                .withFolderTypes(folderTypes)
                .withFileTypes(fileTypes);

        return recursiveType ? builder.withThisFolderType().build() : builder.build();
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
        private FolderSupplier folderSupplier = (watcher, parent, path, type) -> new Folder(watcher, parent, path, type);

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
         *
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
         *
         * @param folderTypes the list of FolderTypes to add
         * @return this Builder instance for method chaining
         */
        public Builder withFolderTypes(List<FolderType> folderTypes) {
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
            fileTypes.add(fileType);
            return this;
        }

        /**
         * Adds a list of FileTypes to the list of file types for this FolderType.
         * This allows the FolderType to recognize and handle multiple file types at once.
         *
         * @param fileTypes the list of FileTypes to add
         * @return this Builder instance for method chaining
         */
        public Builder withFileTypes(List<FileType> fileTypes) {
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
         * Builds and returns a new FolderType instance based on the configured properties.
         * It creates a FolderType that applies to the specified paths, creates folders and files,
         * and includes the defined folder types and file types.
         *
         * @return a new FolderType instance
         */
        public FolderType build() {

            return new FolderType() {

                private final Map<String, FileType> fileTypeMap = new HashMap<>();

                {
                    if (addThisFolderType) {
                        folderTypes.add(0, this);
                    }

                    for (FileType fileType : fileTypes) {
                        for (String ext : fileType.getExtensions()) {
                            fileTypeMap.put(ext.toLowerCase(), fileType);
                        }
                    }
                }

                @Override
                public boolean isDefinitionApplicable(Path path) {
                    return isApplicable.test(path);
                }

                @Override
                public Folder createFolder(Watcher watcher, Folder parent, Path path) {
                    return folderSupplier.createFolder(watcher, parent, path, this);
                }

                @Override
                public File createFile(Folder parent, Path path) {
                    var extension = FileUtils.getFileExtension(path);
                    var fileType = fileTypeMap.get(extension.toLowerCase());
                    if (fileType != null) {
                        return fileType.createFile(parent, path);
                    }
                    return new File(parent, path, FileType.Default.INSTANCE);
                }

                @Override
                public Factory getFactory() {
                    return Factory.of(folderTypes);
                }
            };
        }
    }

    /**
     * Functional interface for creating Folder instances.
     * This allows for custom folder creation logic to be provided when defining a FolderType.
     */
    @FunctionalInterface
    public interface FolderSupplier {
        Folder createFolder(Watcher watcher, Folder parent, Path path, FolderType type);
    }

}