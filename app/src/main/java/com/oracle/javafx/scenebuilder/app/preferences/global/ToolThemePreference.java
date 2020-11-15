package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactory;

import javafx.scene.Parent;

@Component
public class ToolThemePreference extends EnumPreference<ToolTheme> implements ManagedGlobalPreference, UserPreference<ToolTheme> {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "TOOL_THEME"; //NOI18N
    public static final ToolTheme PREFERENCE_DEFAULT_VALUE = ToolTheme.DEFAULT;

	public ToolThemePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, ToolTheme.class, PREFERENCE_DEFAULT_VALUE);
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.tooltheme";
	}

	@Override
	public Parent getEditor() {
		return PreferenceEditorFactory.newEnumFieldEditor(this);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_C;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}
}
