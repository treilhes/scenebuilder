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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import com.gluonhq.jfxapps.app.devtools.modelv2.FolderType.Default;
import com.gluonhq.jfxapps.app.devtools.modelv2.MavenProject.FxmlFile;
import com.gluonhq.jfxapps.app.devtools.modelv2.MavenProject.JavaFile;
import com.gluonhq.jfxapps.app.devtools.modelv2.MavenProject.PropertiesFile;

public class MavenProject2 extends Default {

    public static final FileType JAVA_FILE = FileType.of(List.of("java"), JavaFile::new);
    public static final FileType FXML_FILE = FileType.of(List.of("fxml"), FxmlFile::new);
    public static final FileType PROPERTIES_FILE = FileType.of(List.of("properties"), PropertiesFile::new);

    public static final FolderType JAVA_PACKAGE = FolderType.of(
            MavenProject2::any,
            MavenProject2::createPackageFolder,
            List.of(),
            List.of(JAVA_FILE), true);

    public static final FolderType RESOURCE_PACKAGE = FolderType.of(
            MavenProject2::any,
            MavenProject2::createPackageFolder,
            List.of(),
            List.of(FXML_FILE, PROPERTIES_FILE), true);

    public static final FolderType JAVA_SRC = FolderType.of(
            p -> checkFolderIs(p, "scr"),
            MavenProject2::createPackageFolder,
            List.of(JAVA_PACKAGE),
            List.of(JAVA_FILE), false);

    public static final FolderType RESOURCES = FolderType.of(
            p -> checkFolderIs(p, "resources"),
            MavenProject2::createPackageFolder,
            List.of(RESOURCE_PACKAGE),
            List.of(FXML_FILE, PROPERTIES_FILE), false);

    public static final FolderType MAVEN_PROJECT = FolderType.builder()
            .withIsApplicable(MavenProject2::hasPomFile)
            .withThisFolderType()// to handle sub project
            .withFolderType(FolderType.builder()
                    .withIsApplicable(path -> checkFolderIs(path, "src"))
                    .withFolderType(FolderType.builder()
                        .withIsApplicable(path -> checkFolderIs(path, "main"))
                        .withFolderType(JAVA_SRC)
                        .withFolderType(RESOURCES)
                        .build())
                    .withFolderType(FolderType.builder()
                        .withIsApplicable(path -> checkFolderIs(path, "test"))
                        .withFolderType(JAVA_SRC)
                        .withFolderType(RESOURCES)
                        .build())
                    .build())
            .withFolderSupplier((watcher, parent, path, type) -> {
                var folder = new Folder(watcher, parent, path, type);
                folder.addExclusionPattern("\\..*");
                folder.addExclusionPattern("target");
                return folder;
            })
            .build();

    private static boolean any(Path path) {
        return true;
    }

    private static boolean checkFolderIs(Path path, String folderName) {
        return path != null && path.getFileName().toString().equals(folderName);
    }

    private static boolean hasPomFile(Path path) {
        return path != null && Files.exists(path.resolve("pom.xml"));
    }

    public static MavenProject.Package.PackageFolder createPackageFolder(Watcher watcher, Folder parent, Path path, FolderType type) {
        return new MavenProject.Package.PackageFolder(watcher, parent, path, type);
    }
}