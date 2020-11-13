package com.oracle.javafx.scenebuilder.app.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference.GluonTheme;

@Component("gluonThemeDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class GluonThemePreference extends EnumPreference<GluonTheme> implements ManagedDocumentPreference {
	/***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "gluonTheme"; //NOI18N
    public static final GluonTheme PREFERENCE_DEFAULT_VALUE = GluonTheme.LIGHT;

	public GluonThemePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, GluonTheme.class, PREFERENCE_DEFAULT_VALUE);
	}

}
