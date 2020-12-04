package com.oracle.javafx.scenebuilder.gluon.preferences.global;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.charm.glisten.visual.GlistenStyleClasses;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

@Component
public class GluonSwatchPreference extends EnumPreference<GluonSwatch> implements ManagedGlobalPreference, UserPreference<GluonSwatch> {

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

    private final PreferenceEditorFactory preferenceEditorFactory;

	public GluonSwatchPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, GluonSwatch.class, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.global.gluonswatch";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newEnumFieldEditor(this, (g) -> g.createGraphic());
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_D;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_B";
	}
}
