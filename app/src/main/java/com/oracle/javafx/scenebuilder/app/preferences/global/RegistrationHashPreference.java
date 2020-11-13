package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;

@Component
public class RegistrationHashPreference extends StringPreference implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "REGISTRATION_HASH"; //NOI18N
    public static final String PREFERENCE_DEFAULT_VALUE = null;

	public RegistrationHashPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}
	
}
