package com.oracle.javafx.scenebuilder.gluon.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.StringPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class GluonDefaultTextUserDocumentPreference extends StringPreference implements ManagedDocumentPreference {

	private final static String PREF_NAME="gluonDefaultText";
	private final static String PREF_VALUE="sometext";
	
	public GluonDefaultTextUserDocumentPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREF_NAME, PREF_VALUE);
	}
	
}
