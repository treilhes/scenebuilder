package com.oracle.javafx.scenebuilder.api.preferences;

public interface ManagedPreference {
	void writeToJavaPreferences();
	void readFromJavaPreferences();
}
