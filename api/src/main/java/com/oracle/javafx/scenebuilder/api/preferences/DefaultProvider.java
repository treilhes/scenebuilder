package com.oracle.javafx.scenebuilder.api.preferences;

import java.util.prefs.Preferences;

@FunctionalInterface
public interface DefaultProvider<T> {
	T newDefault(Preferences node);
}
