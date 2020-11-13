package com.oracle.javafx.scenebuilder.kit.preferences.global;

import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.ContentPanelController;

import javafx.scene.image.Image;

@Component
public class BackgroundImagePreference extends EnumPreference<BackgroundImagePreference.BackgroundImage> implements ManagedGlobalPreference {
	
	/***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public enum BackgroundImage {

        BACKGROUND_01 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value1");
            }
        },
        BACKGROUND_02 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value2");
            }
        },
        BACKGROUND_03 {

            @Override
            public String toString() {
                return I18N.getString("prefs.background.value3");
            }
        }
    }
    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "BACKGROUND_IMAGE"; //NOI18N
    public static final BackgroundImage PREFERENCE_DEFAULT_VALUE = BackgroundImage.BACKGROUND_03;

	public BackgroundImagePreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, BackgroundImage.class, PREFERENCE_DEFAULT_VALUE);
	}

	private static Image getImage(BackgroundImage bgi) {
        final URL url;
        switch (bgi) {
            case BACKGROUND_01:
                url = BackgroundImagePreference.class.getResource("Background-Blue-Grid.png"); //NOI18N
                break;
            case BACKGROUND_02:
                url = BackgroundImagePreference.class.getResource("Background-Neutral-Grid.png"); //NOI18N
                break;
            case BACKGROUND_03:
                url = ContentPanelController.getDefaultWorkspaceBackgroundURL();
                break;
            default:
                url = null;
                assert false;
                break;
        }
        assert url != null;
        return new Image(url.toExternalForm());
    }
	
	public Image getBackgroundImageImage() { return getImage(getValue()); }
}
