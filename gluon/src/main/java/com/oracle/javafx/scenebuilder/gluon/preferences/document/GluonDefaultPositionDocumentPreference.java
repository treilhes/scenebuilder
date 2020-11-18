package com.oracle.javafx.scenebuilder.gluon.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.scene.Parent;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class GluonDefaultPositionDocumentPreference extends StringPreference implements UserPreference<String>, ManagedDocumentPreference  {

	private final static String PREF_NAME="gluonDefaultPosition";
	private final static String PREF_VALUE="1";
	
	public GluonDefaultPositionDocumentPreference(@Autowired PreferencesContext preferencesContext) {
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
