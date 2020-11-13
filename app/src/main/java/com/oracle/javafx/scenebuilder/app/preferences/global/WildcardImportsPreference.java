package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;

@Component
public class WildcardImportsPreference extends BooleanPreference implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "WILDCARD_IMPORT"; //NOI18N
    public static final boolean PREFERENCE_DEFAULT_VALUE = false;

	public WildcardImportsPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

}
