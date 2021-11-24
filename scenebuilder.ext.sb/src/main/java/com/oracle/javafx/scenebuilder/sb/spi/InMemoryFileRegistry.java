package com.oracle.javafx.scenebuilder.sb.spi;

import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class InMemoryFileRegistry {

    private static Map<String, Map<String, String>> registry = new HashMap<>();

    public static void clearSession(String sessionId) {
        registry.remove(sessionId);
    }

    public static void addFile(String sessionId, String path, String content) {
        registry.computeIfAbsent(sessionId, (k) -> new HashMap<>()).put(path, content);
    }

    public static String getFile(String fullpath) throws FileNotFoundException {

        if (fullpath == null || fullpath.indexOf("/") == -1) {
            throw new FileNotFoundException(fullpath);
        }
        String sessionId = fullpath.substring(0, fullpath.indexOf("/"));
        String path = fullpath.substring(fullpath.indexOf("/") + 1, fullpath.length());

        Map<String, String> files = registry.get(sessionId);

        if (files == null) {
            throw new FileNotFoundException(fullpath);
        }

        String content = files.get(path);

        if (content == null) {
            throw new FileNotFoundException(fullpath);
        }

        return content;
    }
}
