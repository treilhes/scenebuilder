package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleBooleanProperty;

public class BooleanPreference extends AbstractPreference<Boolean> {
	
	public BooleanPreference(PreferencesContext preferencesContext, String name, Boolean defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleBooleanProperty(), false);
	}

	@Override
	public void write() {
		getNode().putBoolean(getName(), getValue());
	}

	@Override
	public void read() {
		assert getName() != null;
		setValue(getNode().getBoolean(getName(), getDefault()));
	}

	@Override
	public boolean isValid() {
		return getValue() != null;
	}

}
