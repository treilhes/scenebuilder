package com.oracle.javafx.scenebuilder.gluon.preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonDefaultColorPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSaveFolderUserPrefernce;

@Component
public class GluonPreferences {

	@Autowired
	@Lazy
	private GluonSaveFolderUserPrefernce saveFolder;
	
	@Autowired
	@Lazy
	private GluonDefaultColorPreference defaultColor;
	
	public GluonPreferences() {
		// TODO Auto-generated constructor stub
	}

	public GluonSaveFolderUserPrefernce getSaveFolder() {
		return saveFolder;
	}

	public GluonDefaultColorPreference getDefaultColor() {
		return defaultColor;
	}

	
}
