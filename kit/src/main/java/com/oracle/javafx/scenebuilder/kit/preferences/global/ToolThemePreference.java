package com.oracle.javafx.scenebuilder.kit.preferences.global;

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
	private static ToolThemePreference instance = null;
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "TOOL_THEME"; //NOI18N
    public static final ToolTheme PREFERENCE_DEFAULT_VALUE = ToolTheme.DEFAULT;
    
    //TODO bad bad bad, but PropertyEditors need this instance and i don't want to change editors constructors
    //TODO editors musn't use dialogs internaly, change this later
    //TODO same problem for FXOMLoadr
    public static ToolThemePreference getInstance() {
    	return instance;
    }

	public ToolThemePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, ToolTheme.class, PREFERENCE_DEFAULT_VALUE);
		instance = this;
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
