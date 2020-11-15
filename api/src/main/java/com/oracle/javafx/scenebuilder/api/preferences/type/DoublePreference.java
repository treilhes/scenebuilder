package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class DoublePreference extends AbstractPreference<Double> {
	
	public DoublePreference(PreferencesContext preferencesContext, String name, Double defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		getNode().putDouble(getName(), getValue());
	}

	@Override
	public void read() {
		assert getName() != null;
		setValue(getNode().getDouble(getName(), getDefault()));
	}

	@Override
	public boolean isValid(Double value) {
		return value != null;
	}

}
