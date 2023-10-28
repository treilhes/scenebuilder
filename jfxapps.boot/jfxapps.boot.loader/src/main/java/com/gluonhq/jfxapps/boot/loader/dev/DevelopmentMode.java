package com.gluonhq.jfxapps.boot.loader.dev;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class DevelopmentMode {

    private static boolean active = false;

    private static List<Path> mavenProjectsDirectory = new ArrayList<>();

    public static boolean isActive() {
        return active;
    }

    public static void setActive(boolean active) {
        DevelopmentMode.active = active;
    }

    public static void addMavenProjectDirectory(Path directory) {
        mavenProjectsDirectory.add(directory);
    }

    public static Path findModuleClassesFolder(String moduleName) {
        for (Path path:mavenProjectsDirectory) {
            Path modulePath = path.resolve(moduleName);
            Path classesPath = modulePath.resolve("target/classes");
            if (Files.exists(modulePath) && Files.exists(classesPath)) {
                return classesPath;
            }
        }
        return null;
    }
}
