package com.oracle.javafx.scenebuilder.app.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference.GluonSwatch;

@Component("gluonSwatchDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class GluonSwatchPreference extends EnumPreference<GluonSwatch> implements ManagedDocumentPreference {
    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "gluonSwatch"; //NOI18N
    public static final GluonSwatch PREFERENCE_DEFAULT_VALUE = GluonSwatch.BLUE;

	public GluonSwatchPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, GluonSwatch.class, PREFERENCE_DEFAULT_VALUE);
	}

}
