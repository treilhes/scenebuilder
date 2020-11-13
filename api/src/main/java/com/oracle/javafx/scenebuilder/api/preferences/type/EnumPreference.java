package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class EnumPreference<T extends Enum<T>> extends AbstractPreference<T> {
	
	private final Class<T> enumClass;
	
	public EnumPreference(PreferencesContext preferencesContext, String name, Class<T> enumClass, T defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
		this.enumClass = enumClass;
	}

	@Override
	public void writeToJavaPreferences() {
		if (isValid(getValue())) {
			getNode().put(getName(), getValue().name());
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		String enumDefault = (getDefault() == null) ? null : getDefault().name();
		String enumString = getNode().get(getName(), enumDefault);
		
		try {
			setValue(Enum.valueOf(enumClass, enumString));
		} catch (Exception e) {
			setValue(null);
		}
	}

	@Override
	public boolean isValid(T value) {
		return value != null;
	}

}
