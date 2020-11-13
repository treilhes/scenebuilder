package com.oracle.javafx.scenebuilder.app.preferences.global;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.LocalDatePreference;

@Component
public class LastSentTrackingInfoDatePreference extends LocalDatePreference implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "LAST_SENT_TRACKING_INFO_DATE"; //NOI18N
    public static final LocalDate PREFERENCE_DEFAULT_VALUE = null;

	public LastSentTrackingInfoDatePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}
	
}
