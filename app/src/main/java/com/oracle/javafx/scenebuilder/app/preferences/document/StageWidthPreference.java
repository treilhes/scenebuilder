package com.oracle.javafx.scenebuilder.app.preferences.document;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.DoublePreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class StageWidthPreference extends DoublePreference implements ManagedDocumentPreference {

	public static final String PREFERENCE_KEY = "width"; //NOI18N
	public static final Double PREFERENCE_DEFAULT_VALUE = 1240.0; //NOI18N
    
    public StageWidthPreference(PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

}