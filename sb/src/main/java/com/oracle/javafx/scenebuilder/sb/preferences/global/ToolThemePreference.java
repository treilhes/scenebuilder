package com.oracle.javafx.scenebuilder.sb.preferences.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.ObjectPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolTheme;
import com.oracle.javafx.scenebuilder.api.tooltheme.ToolThemeProvider;
import com.oracle.javafx.scenebuilder.sb.tooltheme.DefaultToolThemesList;

import javafx.scene.Parent;

@Component
public class ToolThemePreference extends ObjectPreference<Class<? extends ToolTheme>>
        implements ManagedGlobalPreference, UserPreference<Class<? extends ToolTheme>> {
//	private static ToolThemePreference instance = null;
    /***************************************************************************
     * * Static fields * *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "TOOL_THEME"; // NOI18N
    public static final Class<? extends ToolTheme> PREFERENCE_DEFAULT_VALUE = DefaultToolThemesList.Default.class;

    private final List<Class<? extends ToolTheme>> toolThemeClasses;

    // TODO bad bad bad, but PropertyEditors need this instance and i don't want to
    // change editors constructors
    // TODO editors musn't use dialogs internaly, change this later
    // TODO same problem for FXOMLoadr
//    public static ToolThemePreference getInstance() {
//    	return instance;
//    }

    private final PreferenceEditorFactory preferenceEditorFactory;

    public ToolThemePreference(@Autowired PreferencesContext preferencesContext,
            @Autowired PreferenceEditorFactory preferenceEditorFactory,
            @Autowired List<ToolThemeProvider> toolThemeProviders) {
        super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
//		instance = this;
        this.preferenceEditorFactory = preferenceEditorFactory;
        toolThemeClasses = new ArrayList<>();
        toolThemeProviders.forEach(tp -> toolThemeClasses.addAll(tp.toolThemes()));
    }

    @Override
    protected void write() {
        getNode().put(getName(), getValue().getName());
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void read() {
        assert getName() != null;
        String clsName = getNode().get(getName(), null);
        try {
            setValue(clsName == null ? getDefault() : (Class<? extends ToolTheme>) Class.forName(clsName));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getLabelI18NKey() {
        return "prefs.tooltheme";
    }

    @SuppressWarnings("unchecked")
    @Override
    public Parent getEditor() {
        return preferenceEditorFactory.newChoiceFieldEditor(this,
                toolThemeClasses.toArray((Class<? extends ToolTheme>[]) new Class[0]), (c) -> ToolTheme.name(c));
    }

    @Override
    public PreferenceGroup getGroup() {
        return DefaultPreferenceGroups.GLOBAL_GROUP_C;
    }

    @Override
    public String getOrderKey() {
        return getGroup().getOrderKey() + "_A";
    }
}
