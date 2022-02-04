/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.devutils.strchk.loader;

import java.io.File;
import java.util.ArrayList;

import com.oracle.javafx.scenebuilder.devutils.CommonConfig;
import com.oracle.javafx.scenebuilder.devutils.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.utils.ModelUtils;

public class ProjectLoader {

    public static Project load(File root) {
        assert root != null;
        assert root.exists();
        assert root.isDirectory();

        Project project = new Project(root, root.getName());

        for (var file:root.listFiles()) {
            if (file.isDirectory() && !CommonConfig.EXCLUDED_PROJECTS.contains(file.getName()) && new File(file, "pom.xml").exists()) {
                project.getSubProjects().add(load(file));
            }
        }

        File javaSources = new File(root, CommonConfig.PROJECT_JAVA_FOLDER);
        if (javaSources.exists() && javaSources.isDirectory()) {
            loadJavaSources(project, javaSources);
        }

        File javaResources = new File(root, CommonConfig.PROJECT_RESOURCE_FOLDER);
        if (javaResources.exists() && javaResources.isDirectory()) {
            loadJavaResources(project, javaResources);
        }

        return project;
    }

    private static void loadJavaResources(Project project, File javaResources) {
        for (var file:javaResources.listFiles()) {
            if (file.isDirectory()) {
                loadJavaResources(project, file);
            } else {
                String relativePath = ModelUtils.relativePath(project, file);
                if (file.getName().toLowerCase().endsWith(".properties")) {
                    project.getResources().put(relativePath, ResourceLoader.loadI18nFile(file));
                } else if (file.getName().toLowerCase().endsWith(".fxml")) {
                    project.getResources().put(relativePath, ResourceLoader.loadFxmlFile(file));
                } else if (file.getName().toLowerCase().endsWith(".css")) {
                    project.getResources().put(relativePath, ResourceLoader.loadCssFile(file));
                } else {
                    project.getResources().put(relativePath, ResourceLoader.loadResourceFile(file));
                }
            }
        }
    }

    private static void loadJavaSources(Project project, File javaSources) {
        for (var file:javaSources.listFiles()) {
            if (file.isDirectory()) {
                loadJavaSources(project, file);
            } else {
                String relativePath = ModelUtils.relativePath(project, file);
                if (file.getName().toLowerCase().equals("module-info.java")) {
                    project.setModuleDescriptor(ClassFileLoader.loadModuleFile(file));
                } else if (file.getName().toLowerCase().endsWith(".java")) {
                    project.getClasses().computeIfAbsent(relativePath, (k) -> new ArrayList<>()).add(ClassFileLoader.loadClassFile(file));
                }
//                else  {
//                    project.getResources().add(ResourceLoader.loadResourceFile(file));
//                }
            }
        }
    }


}
