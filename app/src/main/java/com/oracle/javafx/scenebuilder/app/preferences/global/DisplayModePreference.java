package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController.DISPLAY_MODE;

@Component
public class DisplayModePreference extends EnumPreference<DISPLAY_MODE> implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "LIBRARY_DISPLAY_OPTION"; //NOI18N
    public static final DISPLAY_MODE PREFERENCE_DEFAULT_VALUE = DISPLAY_MODE.SECTIONS;

	public DisplayModePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, DISPLAY_MODE.class, PREFERENCE_DEFAULT_VALUE);
	}

}
