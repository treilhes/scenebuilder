package org.scenebuilder.ext.script.preference.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups;
import com.gluonhq.jfxapps.core.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.gluonhq.jfxapps.core.api.preferences.ManagedGlobalPreference;
import com.gluonhq.jfxapps.core.api.preferences.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preferences.PreferencesContext;
import com.gluonhq.jfxapps.core.api.preferences.UserPreference;
import com.gluonhq.jfxapps.core.api.preferences.type.BooleanPreference;

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
