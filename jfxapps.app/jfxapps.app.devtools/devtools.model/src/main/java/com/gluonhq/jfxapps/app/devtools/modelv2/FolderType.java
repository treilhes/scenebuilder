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
import java.util.function.Predicate;

public interface FolderType {

    boolean isDefinitionApplicable(Path path);
    Folder createFolder(Watcher watcher, Folder parent, Path path);
    File createFile(Folder parent, Path path);
    Factory getFactory();

    public interface Factory {

        public static final Factory DEFAULT = Factory.of(List.of(FolderType.Default.INSTANCE));

        FolderType findFolderType(Path path);

        public static Factory of(List<FolderType> folderTypes) {
            return new Default(folderTypes);
        }
        public static Factory of(FolderType... folderTypes) {
            return new Default(Arrays.asList(folderTypes));
        }

        public class Default implements Factory {

            private final List<FolderType> folderTypes;

            public Default(List<FolderType> folderTypes) {
                this.folderTypes = folderTypes;
            }

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

    public static class Default implements FolderType {

        public static final FolderType INSTANCE = new Default();
        private static final Factory FACTORY = Factory.of(Default.INSTANCE);
        private final Map<String, FileType> fileTypeMap = new HashMap<>();

        public void addFileType(FileType fileType) {
            for (String ext : fileType.getExtensions()) {
                fileTypeMap.put(ext.toLowerCase(), fileType);
            }
        }
        @Override
        public boolean isDefinitionApplicable(Path path) {
            return true;
        }

        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path) {
            return new Folder(watcher, parent, path, this);
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
            return FACTORY;
        }

    }

    public static FolderType of(Predicate<Path> isApplicable, FolderSupplier folderSupplier, List<FolderType> folderTypes, List<FileType> fileTypes) {
        return of(isApplicable, folderSupplier, folderTypes, fileTypes, false);
    }

    public static FolderType of(Predicate<Path> isApplicable, FolderSupplier folderSupplier, List<FolderType> folderTypes, List<FileType> fileTypes, boolean recursiveType) {
        var builder = builder()
                .withIsApplicable(isApplicable)
                .withFolderSupplier(folderSupplier)
                .withFolderTypes(folderTypes)
                .withFileTypes(fileTypes);

        return recursiveType ? builder.withThisFolderType().build() : builder.build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private boolean addThisFolderType = false;
        private Predicate<Path> isApplicable = path -> true;
        private FolderSupplier folderSupplier = (watcher, parent, path, type) -> new Folder(watcher, parent, path, type);
        private final List<FolderType> folderTypes = new ArrayList<>();
        private final List<FileType> fileTypes = new ArrayList<>();

        public Builder withThisFolderType() {
            this.addThisFolderType = true;
            return this;
        }
        public Builder withFolderType(FolderType folderType) {
            folderTypes.add(folderType);
            return this;
        }
        public Builder withFolderTypes(List<FolderType> folderTypes) {
            this.folderTypes.addAll(folderTypes);
            return this;
        }
        public Builder withFileType(FileType fileType) {
            fileTypes.add(fileType);
            return this;
        }
        public Builder withFileTypes(List<FileType> fileTypes) {
            this.fileTypes.addAll(fileTypes);
            return this;
        }

        public Builder withIsApplicable(Predicate<Path> isApplicable) {
            this.isApplicable = isApplicable;
            return this;
        }

        public Builder withFolderSupplier(FolderSupplier folderSupplier) {
            this.folderSupplier = folderSupplier;
            return this;
        }

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

    @FunctionalInterface
    public interface FolderSupplier {
        Folder createFolder(Watcher watcher, Folder parent, Path path, FolderType type);
    }

}
