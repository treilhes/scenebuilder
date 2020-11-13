package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.ColorPreference;

import javafx.scene.paint.Color;

@Component
public class ParentRingColorPreference extends ColorPreference implements ManagedGlobalPreference {

	public static final String PREFERENCE_KEY = "PARENT_RING_COLOR"; //NOI18N
	public static final Color PREFERENCE_DEFAULT_VALUE = Color.rgb(238, 168, 47); //NOI18N
    
    public ParentRingColorPreference(PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

}
