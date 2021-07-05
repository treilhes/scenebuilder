package com.oracle.javafx.scenebuilder.devutils.strchk.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.devutils.strchk.Config;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.Project;
import com.oracle.javafx.scenebuilder.devutils.strchk.model.ProjectFile;

public class ModelUtils {
    
    
    public static boolean isFileInResourceFolder(ProjectFile file) {
        return file.getSource().getAbsolutePath().replace("\\", "/").contains(Config.PROJECT_RESOURCE_FOLDER);
    }
    
    public static boolean isFileInJavaFolder(ProjectFile file) {
        return file.getSource().getAbsolutePath().replace("\\", "/").contains(Config.PROJECT_JAVA_FOLDER);
    }
    
    public static String relativePath(Project project, File file) {
        return file.getAbsolutePath().replace(project.getRoot().getAbsolutePath(), "");
    }
    
    public static String relativePath(Project project, Path file) {
        return file.normalize().toString().replace(project.getRoot().getAbsolutePath(), "");
    }
    
    public static File tranformJavaToResource(ProjectFile file) {
        File folder = file.getSource().getParentFile();
        return new File(folder.getAbsolutePath().replace("\\", "/").replace(Config.PROJECT_JAVA_FOLDER, Config.PROJECT_RESOURCE_FOLDER));
    }

    public static List<ProjectFile> findFile(Project project, String fileName) {
        
        return project.getResources().entrySet().stream()
                .filter((e) -> {
                    return e.getKey().endsWith(File.separator + fileName);
                })
                .map(k -> k.getValue())
                .collect(Collectors.toList());
        
    }
    
    public static Map<Project, List<ProjectFile>> findFileInRoot(Project rootProject, String fileName) {
        Map<Project, List<ProjectFile>> map = new HashMap<>();
        rootProject.getSubProjects().forEach(p -> {
            List<ProjectFile> pf = findFile(p, fileName);
            if (pf != null && pf.size() > 0) {
                map.put(p, pf);
            }
        });
        return map;
    }
}
