package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.time.LocalDate;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class LocalDatePreference extends AbstractPreference<LocalDate> {
		
	public LocalDatePreference(PreferencesContext preferencesContext, String name, LocalDate defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		getNode().put(getName(), getValue().toString());
	}

	@Override
	public void read() {
		assert getName() != null;
		String dateDefault = (getDefault() == null) ? null : getDefault().toString();
		String dateString = getNode().get(getName(), dateDefault);
		try {
			setValue(LocalDate.parse(dateString));
		} catch (Exception e) {
			setValue(null);
		}
	}

	@Override
	public boolean isValid() {
		return getValue() != null;
	}

}
