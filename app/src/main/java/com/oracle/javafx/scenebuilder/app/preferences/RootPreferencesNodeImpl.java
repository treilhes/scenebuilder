package com.oracle.javafx.scenebuilder.app.preferences;

import java.util.prefs.Preferences;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.RootPreferencesNode;

@Component
public class RootPreferencesNodeImpl implements RootPreferencesNode {

	// PREFERENCES NODE NAME
    static final String SB_RELEASE_NODE = "SB_2.0"; //NOI18N
    
	@Override
	public Preferences getNode() {
		return Preferences.userNodeForPackage(getClass()).node(SB_RELEASE_NODE);
	}

}
