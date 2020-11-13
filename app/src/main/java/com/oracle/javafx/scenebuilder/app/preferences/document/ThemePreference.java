package com.oracle.javafx.scenebuilder.app.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.global.ThemePreference.Theme;

@Component("themeDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ThemePreference extends EnumPreference<Theme> implements ManagedDocumentPreference {
    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "theme"; //NOI18N
    public static final Theme PREFERENCE_DEFAULT_VALUE = Theme.MODENA;

	public ThemePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, Theme.class, PREFERENCE_DEFAULT_VALUE);
	}

}
