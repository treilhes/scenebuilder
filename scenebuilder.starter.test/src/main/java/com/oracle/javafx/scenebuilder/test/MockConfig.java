package com.oracle.javafx.scenebuilder.test;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MockConfig {

    Map<Class<?>, Consumer<?>> mockConfig = new HashMap<>();

    public <T> void put(Class<T> cls, Consumer<T> configurer) {
        mockConfig.put(cls, configurer);
    }

    public Map<Class<?>, Consumer<?>> getMap() {
        return mockConfig;
    }
}
