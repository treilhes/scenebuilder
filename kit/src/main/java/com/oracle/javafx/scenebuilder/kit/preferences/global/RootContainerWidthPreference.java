package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.DoublePreference;

@Component
public class RootContainerWidthPreference extends DoublePreference implements ManagedGlobalPreference {

	public static final String PREFERENCE_KEY = "ROOT_CONTAINER_WIDTH"; //NOI18N
	public static final Double PREFERENCE_DEFAULT_VALUE = -1.0; //NOI18N
    
    public RootContainerWidthPreference(PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

}
