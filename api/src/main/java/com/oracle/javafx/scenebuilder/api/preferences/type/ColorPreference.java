package com.oracle.javafx.scenebuilder.api.preferences.type;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

public class ColorPreference extends AbstractPreference<Color> {
		
	public ColorPreference(PreferencesContext preferencesContext, String name, Color defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		getNode().put(getName(), getValue().toString());
	}

	@Override
	public void read() {
		assert getName() != null;
		String colorDefault = (getDefault() == null) ? null : getDefault().toString();
		String colorString = getNode().get(getName(), colorDefault);
		
		try {
			setValue(Color.valueOf(colorString));
		} catch (Exception e) {
			setValue(null);
		}
	}

	@Override
	public boolean isValid() {
		return getValue() != null;
	}

}
