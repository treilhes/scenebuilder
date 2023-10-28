package com.gluonhq.jfxapps.boot.loader;

import java.nio.file.Path;
import java.nio.file.Paths;

public interface Constants {

    static Path createPath(String name) {
        return Paths.get(String.format(IT_ROOT, name, String.format(JAR_FORMAT, name, IT_VERSION)));
    }

    final static String IT_ROOT = "./src/test/resources-its/%s/target/%s";
    final static String IT_VERSION = "1.0.0-SNAPSHOT";
    final static String JAR_FORMAT = "%s-%s.jar";

    final static Path IT_MODULE_JAR = createPath("module");
    final static String IT_MODULE_NAME = "it.modul";
    final static String IT_MODULE_CLASS = "it.modul.ModuleClass";

    final static Path IT_MODULE_WITH_DEPENDENCY_JAR = createPath("module-with-dependency");
    final static Path IT_AUTOMATIC_MODULE_JAR = createPath("automatic-modul");
    final static Path IT_CLASSPATH_JAR = createPath("classpath");

    final static Path IT_APP_ROOT_JAR = createPath("app-root");

    final static Path TMP_EXT_API = Paths.get("D:/Dev/eclipse/scenebuilderx/scenebuilder/scenebuilder.core/scenebuilder.core.api/target/scenebuilder.core.api-17.0.0-SNAPSHOT.jar");
}
