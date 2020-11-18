package com.oracle.javafx.scenebuilder.gluon.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;

import javafx.scene.Parent;

@Component
public class GluonSaveFolderUserPrefernce extends StringPreference implements UserPreference<String>, ManagedGlobalPreference {

	private final static String PREF_NAME="gluonDefaultFolder";
	private final static String PREF_VALUE="somefolder";
	
	public GluonSaveFolderUserPrefernce(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREF_NAME, PREF_VALUE);
	}

	@Override
	public String getLabelI18NKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Parent getEditor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PreferenceGroup getGroup() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getOrderKey() {
		// TODO Auto-generated method stub
		return null;
	}

}
