package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.util.prefs.Preferences;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;

import javafx.beans.property.SimpleStringProperty;

public class StringPreference extends AbstractPreference<String> {

	public StringPreference(Preferences node, String name, String defaultValue) {
		super(node, name, defaultValue, new SimpleStringProperty());
	}

	@Override
	public void writeToJavaPreferences() {
		getNode().put(getName(), getValue());
	}

	@Override
	public void readFromJavaPreferences(String key) {
		assert getName() == key;
		setValue(getNode().get(getName(), getDefaultValue()));
	}

}
