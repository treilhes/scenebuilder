package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.ColorPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;

import javafx.scene.Parent;
import javafx.scene.paint.Color;

@Component
public class ParentRingColorPreference extends ColorPreference implements ManagedGlobalPreference, UserPreference<Color> {

	public static final String PREFERENCE_KEY = "PARENT_RING_COLOR"; //NOI18N
	public static final Color PREFERENCE_DEFAULT_VALUE = Color.rgb(238, 168, 47); //NOI18N

	private final PreferenceEditorFactory preferenceEditorFactory;

	public ParentRingColorPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.drop.ring";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newColorFieldEditor(this);
	}


	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_B;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_C";
	}
}
