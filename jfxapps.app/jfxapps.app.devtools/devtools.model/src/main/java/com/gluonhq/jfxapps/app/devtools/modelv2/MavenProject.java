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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;

import com.gluonhq.jfxapps.app.devtools.modelv2.FolderType.Default;

import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

public class MavenProject extends Default {

    public static final FolderType INSTANCE = new MavenProject();

    private static final Factory localFolderTypeFactory = Factory.of(
            MavenProject.INSTANCE,
            Source.INSTANCE,
            Default.INSTANCE
            );

    @Override
    public boolean isDefinitionApplicable(Path path) {
        return path != null && Files.exists(path.resolve("pom.xml"));
    }

    @Override
    public Folder createFolder(Watcher watcher, Folder parent, Path path) {
        var folder = new Folder(watcher,parent, path, this);
        folder.addExclusionPattern("\\..*");
        folder.addExclusionPattern("target");
        return folder;
    }

    @Override
    public Factory getFactory() {
        return localFolderTypeFactory;
    }


    public static class Source extends Default {

        public static final FolderType INSTANCE = new Source();

        private static final Factory localFolderTypeFactory = Factory.of(
                MainSource.INSTANCE,
                TestSource.INSTANCE,
                Default.INSTANCE
                );

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return path != null && path.getFileName().toString().equals("src");
        }

        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path) {
            return new Folder(watcher,parent, path, this);
        }

        @Override
        public Factory getFactory() {
            return localFolderTypeFactory;
        }
    }

    public static class MainSource extends Default {

        public static final FolderType INSTANCE = new MainSource();

        private static final Factory localFolderTypeFactory = Factory.of(
                JavaSource.INSTANCE,
                Resource.INSTANCE,
                Default.INSTANCE
                );

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return path != null && path.getFileName().toString().equals("main");
        }

        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path) {
            return new Folder(watcher, parent, path, this);
        }

        @Override
        public Factory getFactory() {
            return localFolderTypeFactory;
        }
    }

    public static class TestSource extends Default {

        public static final FolderType INSTANCE = new TestSource();

        private static final Factory localFolderTypeFactory = Factory.of(
                JavaSource.INSTANCE,
                Resource.INSTANCE,
                Default.INSTANCE
                );

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return path != null && path.getFileName().toString().equals("test");
        }

        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path) {
            return new Folder(watcher, parent, path, this);
        }

        @Override
        public Factory getFactory() {
            return localFolderTypeFactory;
        }
    }

    public static class JavaSource extends Package {

        public static final FolderType INSTANCE = new JavaSource();

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return path != null && path.getFileName().toString().equals("java");
        }

    }

    public static class Resource extends Package {

        public static final FolderType INSTANCE = new Resource();

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return path != null && path.getFileName().toString().equals("resources");
        }

    }

    public static class Package extends Default {

        public static final FolderType INSTANCE = new Package();

        private static final Factory localFolderTypeFactory = Factory.of(
                Package.INSTANCE
                );

        public Package() {
            super();
            addFileType(new JavaFileType());
            addFileType(new FxmlFileType());
            addFileType(new PropertiesFileType());
        }

        @Override
        public boolean isDefinitionApplicable(Path path) {
            return true;
        }

        @Override
        public Folder createFolder(Watcher watcher, Folder parent, Path path ) {
            return new Package.PackageFolder(watcher, parent, path, this);
        }

        @Override
        public Factory getFactory() {
            return localFolderTypeFactory;
        }

        public static class PackageFolder extends Folder {
            public PackageFolder(Watcher watcher, Folder parent, Path location, FolderType type) {
                super(watcher, parent, location, type);
            }

            @Override
            public String toString() {
                return "PackageFolder [getLocation()=" + getLocation() + "]";
            }

        }
    }

    public static class JavaFileType implements FileType {

        @Override
        public List<String> getExtensions() {
            return List.of("java");
        }

        @Override
        public File createFile(Folder parent, Path path) {
            return new JavaFile(parent, path, this);
        }

    }

    public static class JavaFile extends File {

        public JavaFile(Folder parent, Path location, FileType fileType) {
            super(parent, location, fileType);
        }

        @Override
        public String toString() {
            return "JavaFile [getLocation()=" + getLocation() + "]";
        }

    }

    public static class FxmlFileType implements FileType {

        @Override
        public List<String> getExtensions() {
            return List.of("fxml");
        }

        @Override
        public File createFile(Folder parent, Path path) {
            return new FxmlFile(parent, path, this);
        }

    }

    public static class FxmlFile extends File {

        public FxmlFile(Folder parent, Path location, FileType fileType) {
            super(parent, location, fileType);
        }

        @Override
        public String toString() {
            return "FxmlFile [getLocation()=" + getLocation() + "]";
        }

    }

    public static class PropertiesFileType implements FileType {

        @Override
        public List<String> getExtensions() {
            return List.of("properties");
        }

        @Override
        public File createFile(Folder parent, Path path) {
            return new PropertiesFile(parent, path, this);
        }

    }

    public static class PropertiesFile extends File {

        private ObservableMap<String, String> properties = FXCollections.observableHashMap();

        public PropertiesFile(Folder parent, Path location, FileType fileType) {
            super(parent, location, fileType);
        }


        @Override
        public void refresh() {
            try {
                Properties p = new Properties();
                p.load(getLocation().toUri().toURL().openStream());

                p.entrySet().forEach(entry -> {
                    String key = entry.getKey().toString();
                    String value = entry.getValue() != null ? entry.getValue().toString() : null;
                    properties.put(key, value);
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }


        @Override
        public String toString() {
            return "PropertiesFile [getLocation()=" + getLocation() + "]";
        }

    }
}