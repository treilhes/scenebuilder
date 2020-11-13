package com.oracle.javafx.scenebuilder.api.preferences;

@FunctionalInterface
public interface DefaultProvider<T> {
	T newDefault(PreferencesContext context, String name);
}
