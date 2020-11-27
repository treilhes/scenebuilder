package com.oracle.javafx.scenebuilder.api;

public interface UILogger {
	void logInfoMessage(String key);
    void logInfoMessage(String key, Object... args);
}
