package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.ToolTheme;

@Component
public class ToolThemePreference extends EnumPreference<ToolTheme> implements ManagedGlobalPreference {
	    
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

}
