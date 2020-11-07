package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;

import javafx.beans.property.SimpleObjectProperty;

public abstract class ObjectPreference<T> extends AbstractPreference<T> {
	
	public ObjectPreference(Preferences node, String name, T defaultValue) {
		super(node, name, defaultValue, new SimpleObjectProperty<>());
	}

}
