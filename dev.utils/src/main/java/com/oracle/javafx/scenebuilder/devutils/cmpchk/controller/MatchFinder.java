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
package com.oracle.javafx.scenebuilder.devutils.cmpchk.controller;

import com.oracle.javafx.scenebuilder.devutils.model.Project;

public class MatchFinder {

    public static ComponentItem findMatch(Project root, ComponentItem item) {
        findRegistration(root, item);

        if (item.getMatchProject() == null) {
          item.setMatchError("NOT FOUND");
          item.setMatchSolution(String.format("You need to declare the component class %s into the project extension file", item.getClassName()));
      }

        return null;
    }

    private static void findRegistration(Project root, ComponentItem item) {
        // search for project extension declaration which contains the class name
        // then search for other projects extension declaration which contains the class name

        Project currentProject = item.getProject();

        currentProject.getClasses().values().stream()
            .filter(l -> l != null)
            .flatMap(l -> l.stream())
            .forEach(cf -> {
                if (cf.getStringOccurences().stream().anyMatch(so -> so.getValue().equals(item.getClassName()))) {
                    item.updateMatch(currentProject, cf, cf.getName(), "OK");
                }
            });

        //if (item.getMatchProject() == null) {
            root.getSubProjects().stream().filter(p -> p != currentProject).forEach(p -> {
                p.getClasses().values().stream()
                .filter(l -> l != null)
                .flatMap(l -> l.stream())
                .forEach(cf -> {
                    if (cf.getStringOccurences().stream().anyMatch(so -> so.getValue().equals(item.getClassName()))) {
                        if (item.getMatchProject() == null) {
                            item.updateMatch(p, cf, cf.getName(), "CLASS REGISTERED IN WRONG LOCATION");
                            item.setMatchSolution(String.format("Move class declaration %s %s.%s to %s",
                                    p.getName(), cf.getPackageName(), cf.getName() ,currentProject.getName()));
                        } else {
                            item.setMatchError("CLASS REGISTERED IN MULTI-LOCATION");
                            item.setMatchSolution(item.getMatchSolution() + String.format("\nRemove class from %s %s.%s",
                                    p.getName(), cf.getPackageName(), cf.getName(), currentProject.getName()));
                        }
                    }
                });
            });

        //}

//        try {
//
//            if (type == Type.RELATIVE_FILE || type == Type.ALL || type == Type.FILE) {
//                javaTarget = item.getProjectFile().getSource().getParentFile().toPath().resolve(value).normalize();
//                resourceTarget = ModelUtils.tranformJavaToResource(item.getProjectFile()).toPath().resolve(value).normalize();
//            } else {
//                javaTarget = new File(currentProject.getRoot(), CommonConfig.PROJECT_JAVA_FOLDER).toPath().resolve(value).normalize();
//                resourceTarget = new File(currentProject.getRoot(), CommonConfig.PROJECT_RESOURCE_FOLDER).toPath().resolve(value).normalize();
//            }
//
//            String javaRelativePath = ModelUtils.relativePath(currentProject, javaTarget);
//            String resourceRelativePath = ModelUtils.relativePath(currentProject, resourceTarget);
//
//            if (Files.exists(javaTarget) && Files.isRegularFile(javaTarget)) {
//                item.updateMatch(currentProject, currentProject.getResources().get(javaRelativePath),
//                        javaTarget.getFileName().toString(), "OK");
//            } else if (Files.exists(resourceTarget) && Files.isRegularFile(resourceTarget)) {
//                item.updateMatch(currentProject, currentProject.getResources().get(resourceRelativePath),
//                        javaTarget.getFileName().toString(), "OK");
//            } else {
//                String fileName = resourceTarget.getFileName().toString();
//                Map<Project, List<ProjectFile>> results = ModelUtils.findFileInRoot(root, fileName);
//                if (results.size() > 0) {
//                    results.forEach((k,v) -> {
//                        v.forEach(pf -> {
//                            if (item.getMatchProject() == null) {
//                                item.updateMatch(k, pf, pf.getName(), "LOCATION");
//                                item.setMatchSolution(String.format("Move %s %s.%s to %s",
//                                        k.getName(), pf.getPackageName(), pf.getName(), currentProject.getName()));
//                            } else {
//                                item.setMatchError("MULTI-LOCATION");
//                                item.setMatchSolution(item.getMatchSolution() + String.format("\nMove %s %s.%s to %s",
//                                        k.getName(), pf.getPackageName(), pf.getName(), currentProject.getName()));
//                            }
//                        });
//
//                    });
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("Not a path : " + value);
//        }
    }

}
