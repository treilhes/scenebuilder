package com.oracle.javafx.scenebuilder.api.preferences.type;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.preferences.AbstractPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;

import javafx.beans.property.SimpleObjectProperty;

public class ListOfStringPreference extends AbstractPreference<List<String>> {

	private final static String JOIN_SEPARATOR = "\\" + File.pathSeparator;
	private final static String REGEX_JOIN_SEPARATOR = "\\"+ JOIN_SEPARATOR;
	public ListOfStringPreference(PreferencesContext preferencesContext, String name, List<String> defaultValue) {
		super(preferencesContext, name, defaultValue, new SimpleObjectProperty<>(), false);
	}

	@Override
	public void write() {
		getNode().put(getName(), String.join(JOIN_SEPARATOR, getValue()));
	}

	@Override
	public void read() {
		assert getName() != null;
		String defaultValue = getDefault() == null || getDefault().isEmpty() ? "" : String.join(JOIN_SEPARATOR, getDefault());
		String value = getNode().get(getName(), defaultValue);
		final String[] items = value.split(REGEX_JOIN_SEPARATOR);
		final List<String> newValue = new ArrayList<>();
		newValue.addAll(Arrays.asList(items));
		setValue(newValue);
	}

	@Override
	public boolean isValid() {
		return getValue() != null;
	}

}
