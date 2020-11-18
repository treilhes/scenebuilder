package com.oracle.javafx.scenebuilder.gluon.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;

@Component
public class GluonDefaultColorPreference extends StringPreference implements ManagedGlobalPreference {

	private final static String PREF_NAME="gluonDefaultColor";
	private final static String PREF_VALUE="blue";
	
	public GluonDefaultColorPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREF_NAME, PREF_VALUE);
	}
	
}
