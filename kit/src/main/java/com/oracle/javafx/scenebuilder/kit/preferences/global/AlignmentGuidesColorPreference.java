package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.type.ColorPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactory;

import javafx.scene.Parent;
import javafx.scene.paint.Color;

@Component
public class AlignmentGuidesColorPreference extends ColorPreference implements ManagedGlobalPreference, UserPreference<Color> {

	public static final String PREFERENCE_KEY = "ALIGNMENT_GUIDES_COLOR"; //NOI18N
	public static final Color PREFERENCE_DEFAULT_VALUE = Color.RED; //NOI18N
    
    public AlignmentGuidesColorPreference(PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.alignment.guides";
	}

	@Override
	public Parent getEditor() {
		return PreferenceEditorFactory.newColorFieldEditor(this);
	}


	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_B;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_B";
	}
}
