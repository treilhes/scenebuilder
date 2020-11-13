package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleStringProperty;

public class StringPreference extends AbstractPreference<String> {
	
	public StringPreference(PreferencesContext preferencesContext, String name, String defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleStringProperty(), false);
	}

	@Override
	public void writeToJavaPreferences() {
		if (isValid(getValue())) {
			getNode().put(getName(), getValue());
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		setValue(getNode().get(getName(), getDefault()));
	}

	@Override
	public boolean isValid(String value) {
		return value != null && !value.isEmpty();
	}

}
