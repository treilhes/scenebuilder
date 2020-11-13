package com.oracle.javafx.scenebuilder.kit.preferences.global;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;

@Component
public class GluonThemePreference extends EnumPreference<GluonThemePreference.GluonTheme> implements ManagedGlobalPreference {
	
	/***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

	/**
     * Gluon Theme
     */
    public enum GluonTheme implements StylesheetProvider {
        LIGHT,
        DARK;

        @Override
        public String toString() {
            String lowerCaseName = "title.gluon.theme." + name().toLowerCase(Locale.ROOT);
            return I18N.getString(lowerCaseName);
        }

        @Override
        public String getStylesheetURL() {
            return GlistenStyleClasses.impl_loadResource("theme_" + name().toLowerCase(Locale.ROOT) + ".gls");
        }
    }
    
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
