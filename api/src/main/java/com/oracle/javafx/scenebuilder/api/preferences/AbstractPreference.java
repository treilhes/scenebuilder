package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.prefs.Preferences;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

public abstract class AbstractPreference<T> implements Preference<T> {

	private final String name;
	private final Preferences node;
	private final T defaultValue;
	private final Property<T> value;
	
	public AbstractPreference(Preferences node, String name, T defaultValue, Property<T> propertyHolder) {
		this.node = node;
		this.name = name;
		this.value = propertyHolder;
		this.defaultValue = defaultValue;
		setValue(defaultValue);
	}
	
	@Override
	public Preferences getNode() {
		return node;
	}
	
	public String getName() {
		return name;
	}

	@Override
	public T getValue() {
		return value.getValue();
	}

	@Override
	public void setValue(T newValue) {
		this.value.setValue(newValue);
	}

	@Override
	public ObservableValue<T> getObservableValue() {
		return this.value;
	}

	@Override
	public T getDefaultValue() {
		return defaultValue;
	}
}
