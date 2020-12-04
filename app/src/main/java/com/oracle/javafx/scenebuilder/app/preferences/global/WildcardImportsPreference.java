package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;

import javafx.scene.Parent;

@Component
public class WildcardImportsPreference extends BooleanPreference implements ManagedGlobalPreference, UserPreference<Boolean> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "WILDCARD_IMPORT"; //NOI18N
    public static final boolean PREFERENCE_DEFAULT_VALUE = false;
	private final PreferenceEditorFactory preferenceEditorFactory;

	public WildcardImportsPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.wildcard.import";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newBooleanFieldEditor(this);
	}


	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_F;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_B";
	}
}