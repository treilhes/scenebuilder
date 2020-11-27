package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleStringProperty;

public class StringPreference extends AbstractPreference<String> {
	
	public StringPreference(PreferencesContext preferencesContext, String name, String defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleStringProperty(), false);
	}

	@Override
	public void write() {
		getNode().put(getName(), getValue());
	}

	@Override
	public void read() {
		assert getName() != null;
		setValue(getNode().get(getName(), getDefault()));
	}

	@Override
	public boolean isValid() {
		return getValue() != null && !getValue().isEmpty();
	}

}
