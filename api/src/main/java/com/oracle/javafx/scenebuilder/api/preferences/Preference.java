package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.prefs.Preferences;

import javafx.beans.value.ObservableValue;

public interface Preference<T> {
	
	Preferences getNode();
	String getName();
	void setValue(T value);
	ObservableValue<T> getObservableValue();
	T getValue();
	T getDefaultValue();
	void writeToJavaPreferences();
	void readFromJavaPreferences(String key);
	
}
