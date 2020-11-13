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
public class ThemePreference extends EnumPreference<ThemePreference.Theme> implements ManagedGlobalPreference {
	
	/***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

	/**
     * Themes supported by Scene Builder Kit.
     */
    public enum Theme implements StylesheetProvider {
        GLUON_MOBILE_LIGHT(GlistenStyleClasses.impl_loadResource("glisten.gls")),
        GLUON_MOBILE_DARK(GlistenStyleClasses.impl_loadResource("glisten.gls")),
        MODENA("com/sun/javafx/scene/control/skin/modena/modena.bss"),
        MODENA_TOUCH("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch.css"),
        MODENA_HIGH_CONTRAST_BLACK_ON_WHITE("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-blackOnWhite.css"),
        MODENA_HIGH_CONTRAST_WHITE_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-whiteOnBlack.css"),
        MODENA_HIGH_CONTRAST_YELLOW_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-highContrast-yellowOnBlack.css"),
        MODENA_TOUCH_HIGH_CONTRAST_BLACK_ON_WHITE("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-blackOnWhite.css"),
        MODENA_TOUCH_HIGH_CONTRAST_WHITE_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-whiteOnBlack.css"),
        MODENA_TOUCH_HIGH_CONTRAST_YELLOW_ON_BLACK("com/oracle/javafx/scenebuilder/kit/util/css/modena/modena-touch-highContrast-yellowOnBlack.css"),
        CASPIAN("com/sun/javafx/scene/control/skin/caspian/caspian.bss"),
        CASPIAN_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-highContrast.css"),
        CASPIAN_EMBEDDED("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded.css"),
        CASPIAN_EMBEDDED_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-highContrast.css"),
        CASPIAN_EMBEDDED_QVGA("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga.css"),
        CASPIAN_EMBEDDED_QVGA_HIGH_CONTRAST("com/oracle/javafx/scenebuilder/kit/util/css/caspian/caspian-embedded-qvga-highContrast.css");

        private String url;

        Theme(String url) {
            this.url = url;
        }

        @Override
        public String getStylesheetURL() {
            return url;
        }

        @Override
        public String toString() {
            String lowerCaseName = name().toLowerCase(Locale.ROOT);
            return I18N.getString("title.theme." + lowerCaseName);
        }
    }
    
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
