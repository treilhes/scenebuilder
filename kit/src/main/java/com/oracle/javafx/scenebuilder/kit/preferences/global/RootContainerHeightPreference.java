package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.type.DoublePreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactoryImpl;

import javafx.scene.Parent;

@Component
public class RootContainerHeightPreference extends DoublePreference implements ManagedGlobalPreference, UserPreference<Double> {

	public static final String PREFERENCE_KEY = "ROOT_CONTAINER_HEIGHT"; //NOI18N
	public static final Double PREFERENCE_DEFAULT_VALUE = 400.0; //NOI18N

	private final PreferenceEditorFactory preferenceEditorFactory;

	public RootContainerHeightPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.doc.height";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newDoubleFieldEditor(this);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_A;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_B";
	}
}
