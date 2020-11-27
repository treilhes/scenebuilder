package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.prefs.Preferences;

import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;

public abstract class AbstractPreference<T> implements Preference<T> {

	private String name;
	private final T defaultValue;
	private final Property<T> value;
	private PreferencesContext preferencesContext;
		
	public AbstractPreference(PreferencesContext preferencesContext, String name, T defaultValue, Property<T> propertyHolder, boolean isNode) {
		this.name = name == null ? "" : name;
		this.value = propertyHolder;
		this.defaultValue = defaultValue;
		this.preferencesContext = preferencesContext;

		// handle document scoped value 
		if (preferencesContext.isDocumentScope(getClass()) && !preferencesContext.isDocumentAlreadyInPathScope()) {
			this.preferencesContext = this.preferencesContext.nodeContext(this, preferencesContext.computeDocumentNodeName());
		}
		if (isNode) {
			this.preferencesContext = this.preferencesContext.nodeContext(this, this.name);
		}
		
		setValue(defaultValue);
	}

	protected abstract void write();
	protected abstract void read();
	
	@Override
	public Preferences getNode() {
		if (preferencesContext.isDocumentScope(getClass())) {
			return preferencesContext.getDocumentsNode().getNode();
		} else {
			return preferencesContext.getRootNode().getNode();
		}
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}

	protected PreferencesContext getPreferencesContext() {
		return preferencesContext;
	}

	@Override
	public T getValue() {
		return value.getValue();
	}

	@Override
	public Preference<T> setValue(T value) {
		this.value.setValue(value);
		return this;
	}

	@Override
	public ObservableValue<T> getObservableValue() {
		return this.value;
	}

	@Override
	public T getDefault() {
		return defaultValue;
	}

	@Override
	public Preference<T> reset() {
		setValue(getDefault());
		return this;
	}
	
	@Override
	public void writeToJavaPreferences() {
		if (isValid()
				&& (!preferencesContext.isDocumentScope(this.getClass()) || preferencesContext.isDocumentNameDefined())) {
			write();
		} else {
			getNode().remove(getName());
		}
	}

	@Override
	public void readFromJavaPreferences() {
		assert getName() != null;
		read();
	}
	
}
