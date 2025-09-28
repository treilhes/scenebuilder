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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A Maven project structure representation dedicated to javafx project
 * This class defines the folder and file types typically found in a Maven project,
 * including source code, resources, and their respective file types.
 *
 * The structure includes:
 * <ul>
 * <li>Java source files (.java)</li>
 * <li>FXML files (.fxml)</li>
 * <li>Properties files (.properties)</li>
 * <li>Java packages organized in a source directory</li>
 * <li>Resource packages organized in a resources directory</li>
 * <li>A main source directory containing both Java and resource packages</li>
 * <li>A test source directory mirroring the main structure</li>
 * <li>A top-level Maven project folder that includes the main and test directories</li>
 * </ul>
 * * The folder structure is designed to be flexible and extensible, allowing for
 * additional folder types and file types to be added as needed.
 * * The MavenProject2 class provides static methods to create package folders
 * and check folder conditions,
 * and it uses the {@link FolderType} and {@link FileType} classes
 * to define the structure and behavior of the project.
 * * <p>
 *
 */
public class TestFolderDefinitions {

    /**
     * custom file type definition.
     */
    public static final FileType TYPED_FILE = FileType.builder()
            .withExtension("typed")
            .withFileSupplier(TypedFile::new)
            .build();

    /**
     * A folder type representing a Java package.
     * It is applicable to any path and creates a package folder.
     * It includes Java files and excludes other file types.
     */
    public static final FolderType TYPED_FOLDER = FolderType.builder()
            .withId("TYPED_FOLDER")
            .withIsApplicable(Folder::any)
            .withFolderSupplier(TypedFolder::new)
            .withFileType(TYPED_FILE)
            .withThisFolderType()
            .build();

    public static final FolderType TEST_FOLDER_DEFINITION = FolderType.builder()
            .withIsApplicable(TestFolderDefinitions::hasMarkerFile)
            .withThisFolderType()// to handle sub custom foler
            .withFolderType(
                    FolderType.builder()
                    .withIsApplicable(path -> Folder.isNamed(path, "src"))
                    .withFolderType(FolderType.builder()
                        .withIsApplicable(path -> Folder.isNamed(path, "typed"))
                        .withFolderType(TYPED_FOLDER)
                        .build())
                    .withFolderType(FolderType.generic())
                    .build())
            .withExclusionPatterns("excluded")
            .build();

    private static boolean hasMarkerFile(Path path) {
        return path != null && Files.exists(path.resolve("test.marker"));
    }

    public static class TypedFile extends File{
        public TypedFile(Folder parent, Path location, List<FileFeatureHandler> handlers, FileType fileType) {
            super(parent, location, handlers, fileType);
        }
    }

    public static class TypedFolder extends Folder{
        public TypedFolder(Watcher watcher, Folder parent, Path location, List<FolderFeatureHandler> handlers, FolderType folderType) {
            super(watcher, parent, location, handlers, folderType);
        }
    }
}