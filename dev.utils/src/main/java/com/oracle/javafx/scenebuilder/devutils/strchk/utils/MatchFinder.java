package com.oracle.javafx.scenebuilder.devutils.strchk.utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import com.oracle.javafx.scenebuilder.devutils.strchk.Config;
import com.oracle.javafx.scenebuilder.devutils.strchk.controller.ResourceLocationItem;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.I18nFile;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.ProjectFile;

public class MatchFinder {

    

    private enum Type {
        ALL, FILE, ABSOLUTE_FILE, RELATIVE_FILE, PROPERTY_KEY
    }

    public static ResourceLocationItem findMatch(Project root, ResourceLocationItem item) {
        Type type = Type.ALL;
        String value = item.getValue();

        if (value.startsWith("@")) { // looking
            type = Type.FILE;
            value = value.substring(1);
        }

        if (value.startsWith("/")) { //
            type = Type.ABSOLUTE_FILE;
            value = value.substring(1);
        }
        
        if (value.startsWith("./") || value.startsWith("../")) { //
            type = Type.RELATIVE_FILE;
        }

        if (value.startsWith("%")) { // looking
            type = Type.PROPERTY_KEY;
            value = value.substring(1);
        }

        long start = System.currentTimeMillis();
        switch (type) {
        case FILE:
        case RELATIVE_FILE:
        case ABSOLUTE_FILE: {
            findResource(root, item, value, type);
            
            if (item.getMatchProject() == null) {
                item.setMatchError("FILE NOT FOUND");
            }
            long end = System.currentTimeMillis();
            System.out.println(String.format("findResource time %s ms", (end - start)));
            break;
        }
        case PROPERTY_KEY: {
            findPropertyKey(root, item, value);
            
            if (item.getMatchProject() == null) {
                item.setMatchError("KEY NOT FOUND");
            }
            
            long end = System.currentTimeMillis();
            System.out.println(String.format("findPropertyKey time %s ms", (end - start)));
            break;
        }
        case ALL: {
            findPropertyKey(root, item, value);
            long preend = System.currentTimeMillis();
            System.out.println(String.format("findPropertyKey time %s ms", (preend - start)));
            if (item.getMatchProject() == null) {
                findResource(root, item, value, type);
                long end = System.currentTimeMillis();
                System.out.println(String.format("findResource time %s ms", (end - preend)));
            }
            
            if (item.getMatchProject() == null) {
                item.setMatchError("NOT FOUND");
                item.setMatchSolution(String.format("If the value is not a path and not an i18n value\nPlease suffix it with //NOCHECK or //NOI18N", item.getValue()));
            }
            break;
        }

        default:
            break;
        }

        return null;
    }

    private static void findResource(Project root, ResourceLocationItem item, String value, Type type) {
        // first check if the file exists relative to file path or absolute path
        // then  check if the file exists relative to file path or absolute path transposed to the resource folder
        // then if not found for all project
        // try to find a file with the same name regardless of location
        
        Project currentProject = item.getProject();
        Path resourceTarget = null;
        Path javaTarget = null;
        
        try {
            
            if (type == Type.RELATIVE_FILE || type == Type.ALL || type == Type.FILE) {
                javaTarget = item.getProjectFile().getSource().getParentFile().toPath().resolve(value).normalize();
                resourceTarget = ModelUtils.tranformJavaToResource(item.getProjectFile()).toPath().resolve(value).normalize();
            } else {
                javaTarget = new File(currentProject.getRoot(), Config.PROJECT_JAVA_FOLDER).toPath().resolve(value).normalize();
                resourceTarget = new File(currentProject.getRoot(), Config.PROJECT_RESOURCE_FOLDER).toPath().resolve(value).normalize();
            }
            
            String javaRelativePath = ModelUtils.relativePath(currentProject, javaTarget);
            String resourceRelativePath = ModelUtils.relativePath(currentProject, resourceTarget);
            
            if (Files.exists(javaTarget) && Files.isRegularFile(javaTarget)) {
                item.updateMatch(currentProject, currentProject.getResources().get(javaRelativePath),
                        javaTarget.getFileName().toString(), "OK");
            } else if (Files.exists(resourceTarget) && Files.isRegularFile(resourceTarget)) {
                item.updateMatch(currentProject, currentProject.getResources().get(resourceRelativePath),
                        javaTarget.getFileName().toString(), "OK");
            } else {
                String fileName = resourceTarget.getFileName().toString();
                Map<Project, List<ProjectFile>> results = ModelUtils.findFileInRoot(root, fileName);
                if (results.size() > 0) {
                    results.forEach((k,v) -> {
                        v.forEach(pf -> {
                            if (item.getMatchProject() == null) {
                                item.updateMatch(k, pf, pf.getName(), "LOCATION");
                                item.setMatchSolution(String.format("Move %s %s.%s to %s", 
                                        k.getName(), pf.getPackageName(), pf.getName(), currentProject.getName()));
                            } else {
                                item.setMatchError("MULTI-LOCATION");
                                item.setMatchSolution(item.getMatchSolution() + String.format("\nMove %s %s.%s to %s", 
                                        k.getName(), pf.getPackageName(), pf.getName(), currentProject.getName()));
                            }
                        });
                        
                    });
                }
            }
        } catch (Exception e) {
            System.out.println("Not a path : " + value);
        }
    }
    
    private static void findPropertyKey(Project root, ResourceLocationItem item, String value) {
        Project currentProject = item.getProject();
        
        currentProject.getResources().values().stream()
            .filter(pf -> pf instanceof I18nFile)
            .forEach(pf -> {
                I18nFile ifn = (I18nFile)pf;
                
                if (ifn.getProperties().containsKey(value)) {
                    item.updateMatch(currentProject, ifn, ifn.getBaseName(), "OK");
                }
            });
        
        if (item.getMatchProject() == null) {
            root.getSubProjects().forEach(p -> {
                p.getResources().values().stream()
                .filter(pf -> pf instanceof I18nFile)
                .forEach(pf -> {
                    I18nFile ifn = (I18nFile)pf;
                    
                    if (ifn.getProperties().containsKey(value)) {
                        if (item.getMatchProject() == null) {
                            item.updateMatch(p, ifn, ifn.getBaseName(), "KEY LOCATION");
                            item.setMatchSolution(String.format("Move key %s %s.%s to %s", 
                                    p.getName(), pf.getPackageName(), pf.getName() ,currentProject.getName()));
                        } else if (!ifn.getBaseName().equals(item.getFileName())) {
                            item.setMatchError("KEY MULTI-LOCATION");
                            item.setMatchSolution(item.getMatchSolution() + String.format("\nMove key %s %s.%s to %s", 
                                    p.getName(), pf.getPackageName(), pf.getName(), currentProject.getName()));
                        }
                    }
                });
            });
            
        }
    }
}
