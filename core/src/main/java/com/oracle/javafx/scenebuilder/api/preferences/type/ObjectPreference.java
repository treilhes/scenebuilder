package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public abstract class ObjectPreference<T> extends AbstractPreference<T> {

	public ObjectPreference(PreferencesContext preferencesContext, String name, T defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public boolean isValid() {
		return getValue() != null;
	}


}
