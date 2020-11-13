package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class StringArrayPreference extends AbstractPreference<String[]> {

	private final static String JOIN_SEPARATOR = ",";
	
	public StringArrayPreference(PreferencesContext preferencesContext, String name, String[] defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void writeToJavaPreferences() {
		if (isValid(getValue())) {
			getNode().put(getName(), String.join(JOIN_SEPARATOR, getValue()));
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		String defaultValue = getDefault() == null || getDefault().length == 0 ? "" : String.join(JOIN_SEPARATOR, getDefault());
		String value = getNode().get(getName(), defaultValue);
		final String[] items = value.split(JOIN_SEPARATOR);
		setValue(items);
	}

	@Override
	public boolean isValid(String[] value) {
		return value != null;
	}

}
