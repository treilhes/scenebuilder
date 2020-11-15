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
import com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference.GluonSwatch;

import javafx.scene.Parent;

@Component("gluonSwatchDocumentPreference")
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonSwatchPreference extends EnumPreference<GluonSwatch> implements ManagedDocumentPreference, UserPreference<GluonSwatch> {
    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "gluonSwatch"; //NOI18N
    //public static final GluonSwatch PREFERENCE_DEFAULT_VALUE = GluonSwatch.BLUE;
	
	public GluonSwatchPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired com.oracle.javafx.scenebuilder.kit.preferences.global.GluonSwatchPreference defaultGluonSwatchPreference) {
		super(preferencesContext, PREFERENCE_KEY, GluonSwatch.class, defaultGluonSwatchPreference.getValue());
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.document.gluonswatch";
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
		return getGroup().getOrderKey() + "_B";
	}

}
