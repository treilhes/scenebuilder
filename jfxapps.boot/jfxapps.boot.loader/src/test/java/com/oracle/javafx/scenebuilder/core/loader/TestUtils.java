package com.oracle.javafx.scenebuilder.core.loader;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;

import com.oracle.javafx.scenebuilder.core.loader.internal.layer.Layer;

public final class TestUtils {

    private TestUtils() {
        super();
    }

    public static void copy(Path target, Path jarPath) throws IOException {
        Files.copy(jarPath, target.resolve(jarPath.getFileName()));
    }

    public static void delete(Path target, Path jarPath) throws IOException {
        Files.deleteIfExists(target.resolve(jarPath.getFileName()));
    }

    public static Object instanciate(Layer layer, String moduleSlashClass) throws Exception {
        Class<?> cls = layer.getClass(moduleSlashClass);
        Constructor<?> constructor = cls.getDeclaredConstructor();
        return constructor.newInstance();
    }
}
