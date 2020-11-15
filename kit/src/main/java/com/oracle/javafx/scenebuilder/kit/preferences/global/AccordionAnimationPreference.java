package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactory;

import javafx.scene.Parent;

@Component
public class AccordionAnimationPreference extends BooleanPreference implements ManagedGlobalPreference, UserPreference<Boolean> {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "ACCORDION_ANIMATION"; //NOI18N
    public static final boolean PREFERENCE_DEFAULT_VALUE = true;

	public AccordionAnimationPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.animate.accordion";
	}

	@Override
	public Parent getEditor() {
		return PreferenceEditorFactory.newBooleanFieldEditor(this);
	}


	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_F;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}
}
