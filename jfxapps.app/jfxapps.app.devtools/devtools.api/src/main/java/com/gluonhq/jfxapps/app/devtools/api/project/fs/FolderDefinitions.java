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
package com.gluonhq.jfxapps.app.devtools.api.project.fs;

import java.nio.file.Files;
import java.nio.file.Path;

import com.gluonhq.jfxapps.core.api.fs.watcher.FileType;
import com.gluonhq.jfxapps.core.api.fs.watcher.Folder;
import com.gluonhq.jfxapps.core.api.fs.watcher.FolderType;

/**
 * A Maven project structure representation dedicated to javafx project
 * This class defines the folder and file types typically found in a Maven project,
 * including source code, resources, and their respective file types.
 *
 * It extends the {@link Default} class to provide a default implementation
 * for folder handling and file type definitions.
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
public class FolderDefinitions {

    /**
     * Java file type definition.
     */
    public static final FileType JAVA_FILE = FileType.builder()
            .withExtension("java")
            .withFileSupplier(JavaFile::new)
            .build();

    /**
     * FXML file type definition.
     */
    public static final FileType FXML_FILE = FileType.builder()
            .withExtension("fxml")
            .withFileSupplier(FxmlFile::new)
            .build();

    /**
     * Properties file type definition.
     */
    public static final FileType PROPERTIES_FILE = FileType.builder()
            .withExtension("properties")
            .withFileSupplier(PropertiesFile::new)
            .build();

    public static final FileType POM_FILE = FileType.builder()
            .withName("POM_DEF")
            .withNamePattern("pom\\.xml")
            .withFileSupplier(PomFile::new)
            .build();

    /**
     * A folder type representing a Java package.
     * It is applicable to any path and creates a package folder.
     * It includes Java files and excludes other file types.
     */
    public static final FolderType JAVA_PACKAGE = FolderType.builder()
            .withId("JAVA_PACKAGE")
            .withIsApplicable(Folder::any)
            .withFolderSupplier(PackageFolder::new)
            .withFileType(JAVA_FILE)
            .withThisFolderType()
            .build();

    public static final FolderType RESOURCE_PACKAGE = FolderType.builder()
            .withId("RESOURCE_PACKAGE")
            .withIsApplicable(Folder::any)
            .withFolderSupplier(PackageFolder::new)
            .withFileType(FXML_FILE)
            .withFileType(PROPERTIES_FILE)
            .withThisFolderType()
            .build();

    public static final FolderType JAVA_SRC = FolderType.builder()
            .withId("JAVA_SRC")
            .withIsApplicable(p -> Folder.isNamed(p, "java"))
            .withFolderSupplier(PackageFolder::new)
            .withFolderType(JAVA_PACKAGE)
            .withFileType(JAVA_FILE)
            .build();

    public static final FolderType RESOURCES = FolderType.builder()
            .withId("RESOURCES")
            .withIsApplicable(p -> Folder.isNamed(p, "resources"))
            .withFolderSupplier(PackageFolder::new)
            .withFolderType(RESOURCE_PACKAGE)
            .withFileType(FXML_FILE)
            .withFileType(PROPERTIES_FILE)
            .build();

    public static final FolderType MAVEN_PROJECT = FolderType.builder()
            .withId("MAVEN_PROJECT")
            .withIsApplicable(FolderDefinitions::hasPomFile)
            .withThisFolderType()// to handle sub project
            .withFileType(POM_FILE)
            .withFolderType(
                    FolderType.builder()
                    .withId("SRC")
                    .withIsApplicable(path -> Folder.isNamed(path, "src"))
                    .withFolderType(FolderType.builder()
                        .withId("MAIN")
                        .withIsApplicable(path -> Folder.isNamed(path, "main"))
                        .withFolderType(JAVA_SRC)
                        .withFolderType(RESOURCES)
                        .build())
                    .withFolderType(FolderType.builder()
                        .withId("TEST")
                        .withIsApplicable(path -> Folder.isNamed(path, "test"))
                        .withFolderType(JAVA_SRC)
                        .withFolderType(RESOURCES)
                        .build())
                    .build())
            .withExclusionPatterns("\\..*","target")
            .build();

    private static boolean hasPomFile(Path path) {
        return path != null && Files.exists(path.resolve("pom.xml"));
    }

}