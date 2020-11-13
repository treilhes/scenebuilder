package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;

@Component
public class DisplayOptionPreference extends EnumPreference<DisplayOption> implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "HIERARCHY_DISPLAY_OPTION"; //NOI18N
    public static final DisplayOption PREFERENCE_DEFAULT_VALUE = DisplayOption.INFO;

	public DisplayOptionPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, DisplayOption.class, PREFERENCE_DEFAULT_VALUE);
	}

}
