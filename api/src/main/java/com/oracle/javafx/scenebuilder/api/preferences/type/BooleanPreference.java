package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleBooleanProperty;

public class BooleanPreference extends AbstractPreference<Boolean> {
	
	public BooleanPreference(PreferencesContext preferencesContext, String name, Boolean defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleBooleanProperty(), false);
	}

	@Override
	public void writeToJavaPreferences() {
		if (isValid(getValue())) {
			getNode().putBoolean(getName(), getValue());
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		setValue(getNode().getBoolean(getName(), getDefault()));
	}

	@Override
	public boolean isValid(Boolean value) {
		return value != null;
	}

}
