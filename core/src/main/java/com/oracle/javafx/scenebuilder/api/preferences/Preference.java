package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.prefs.Preferences;

import javafx.beans.value.ObservableValue;

public interface Preference<T> extends ManagedPreference {
	
	Preferences getNode();
	String getName();
	
	Preference<T> setValue(T value);
	T getValue();
	T getDefault();
	ObservableValue<T> getObservableValue();
	
	boolean isValid();
	
	Preference<T> reset();
	
}
