package org.scenebuilder.ext.script.preference.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;

import javafx.scene.Parent;

@Component
public class StaticLoadPreference extends BooleanPreference implements ManagedGlobalPreference, UserPreference<Boolean> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "STATIC_LOAD"; //NOCHECK
    public static final boolean PREFERENCE_DEFAULT_VALUE = true;
    private final PreferenceEditorFactory preferenceEditorFactory;

    public StaticLoadPreference(
            @Autowired PreferencesContext preferencesContext,
            @Autowired PreferenceEditorFactory preferenceEditorFactory) {
        super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
        this.preferenceEditorFactory = preferenceEditorFactory;
    }

    @Override
    public String getLabelI18NKey() {
        return "pref.script.staticload";
    }

    @Override
    public Parent getEditor() {
        return preferenceEditorFactory.newBooleanFieldEditor(this);
    }


    @Override
    public PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_G;
    }

    @Override
    public String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }
}
