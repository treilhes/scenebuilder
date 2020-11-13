package com.oracle.javafx.scenebuilder.kit.preferences.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@Component
public class GluonSwatchPreference extends EnumPreference<GluonSwatchPreference.GluonSwatch> implements ManagedGlobalPreference {
	
	/***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

	/**
     * Gluon Swatch
     */
    public enum GluonSwatch implements StylesheetProvider {
        BLUE,
        CYAN,
        DEEP_ORANGE,
        DEEP_PURPLE,
        GREEN,
        INDIGO,
        LIGHT_BLUE,
        PINK,
        PURPLE,
        RED,
        TEAL,
        LIGHT_GREEN,
        LIME,
        YELLOW,
        AMBER,
        ORANGE,
        BROWN,
        GREY,
        BLUE_GREY;

        private static final String PRIMARY_SWATCH_500_STR = "-primary-swatch-500:";

        Color color;

        @Override
        public String toString() {
            String lowerCaseSwatch = "title.gluon.swatch." + name().toLowerCase(Locale.ROOT);
            return I18N.getString(lowerCaseSwatch);
        }

        @Override
        public String getStylesheetURL() {
            return GlistenStyleClasses.impl_loadResource("swatch_" + name().toLowerCase(Locale.ROOT) + ".gls");
        }

        public Color getColor() {
            if (color == null) {
                URL url = null;
                try {
                    url = new URL(getStylesheetURL());
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
                        String s = reader.readLine();
                        while (s != null) {
                            // Remove white spaces
                            String trimmedString = s.replaceAll("\\s+", "");
                            int indexOf = trimmedString.indexOf(PRIMARY_SWATCH_500_STR);
                            if (indexOf != -1) {
                                int indexOfSemiColon = trimmedString.indexOf(";");
                                String colorString = trimmedString.substring(indexOf + PRIMARY_SWATCH_500_STR.length(), indexOfSemiColon);
                                color = Color.web(colorString);
                            }
                            s = reader.readLine();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return color;
        }

        public Node createGraphic() {
            Rectangle rect = new Rectangle(8, 8);
            rect.setFill(getColor());
            rect.setStroke(Color.BLACK);
            return rect;
        }
    }
    
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
