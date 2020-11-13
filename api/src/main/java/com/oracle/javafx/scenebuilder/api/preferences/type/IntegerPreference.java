package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class IntegerPreference extends AbstractPreference<Integer> {
	
	public IntegerPreference(PreferencesContext preferencesContext, String name, Integer defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void writeToJavaPreferences() {
		if (isValid(getValue())) {
			getNode().putInt(getName(), getValue());
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		setValue(getNode().getInt(getName(), getDefault()));
	}

	@Override
	public boolean isValid(Integer value) {
		return value != null;
	}

}
