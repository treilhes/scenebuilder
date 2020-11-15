package com.oracle.javafx.scenebuilder.kit.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference.GluonTheme;

import javafx.scene.Parent;

@Component("gluonThemeDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonThemePreference extends EnumPreference<GluonTheme> implements ManagedDocumentPreference, UserPreference<GluonTheme> {
	/***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "gluonTheme"; //NOI18N
    //public static final GluonTheme PREFERENCE_DEFAULT_VALUE = GluonTheme.LIGHT;

	public GluonThemePreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired com.oracle.javafx.scenebuilder.kit.preferences.global.GluonThemePreference defaultGluonThemePreference
			) {
		super(preferencesContext, PREFERENCE_KEY, GluonTheme.class, defaultGluonThemePreference.getValue());
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.document.gluontheme";
	}

	@Override
	public Parent getEditor() {
		return PreferenceEditorFactory.newEnumFieldEditor(this);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_D;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_C";
	}

}
