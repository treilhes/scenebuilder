package com.oracle.javafx.scenebuilder.api.preferences;

import javafx.scene.Parent;

public interface UserPreference<T> extends Preference<T> {
	String getLabelKey();
	Parent getEditor();
	T getEditValue();
}
