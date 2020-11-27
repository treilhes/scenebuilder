package com.oracle.javafx.scenebuilder.api.preferences;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;

import javafx.scene.Parent;

public interface UserPreference<T> extends Preference<T> {
	PreferenceGroup getGroup();
	String getOrderKey();
	String getLabelI18NKey();
	Parent getEditor();
}
