package com.oracle.javafx.scenebuilder.app.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class DocumentVisiblePreference extends BooleanPreference implements ManagedDocumentPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "documentVisible"; //NOI18N
    public static final boolean PREFERENCE_DEFAULT_VALUE = true;

	public DocumentVisiblePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

}
