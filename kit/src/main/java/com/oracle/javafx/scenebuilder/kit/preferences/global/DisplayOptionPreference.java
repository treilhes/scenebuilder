package com.oracle.javafx.scenebuilder.kit.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.kit.editor.panel.hierarchy.AbstractHierarchyPanelController.DisplayOption;
import com.oracle.javafx.scenebuilder.kit.preferences.PreferenceEditorFactory;

import javafx.scene.Parent;

@Component
public class DisplayOptionPreference extends EnumPreference<DisplayOption> implements ManagedGlobalPreference, UserPreference<DisplayOption> {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "HIERARCHY_DISPLAY_OPTION"; //NOI18N
    public static final DisplayOption PREFERENCE_DEFAULT_VALUE = DisplayOption.INFO;

	public DisplayOptionPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, DisplayOption.class, PREFERENCE_DEFAULT_VALUE);
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.hierarchy.displayoption";
	}

	@Override
	public Parent getEditor() {
		return PreferenceEditorFactory.newEnumFieldEditor(this);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_C;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_C";
	}
}
