package com.oracle.javafx.scenebuilder.ext.theme.global;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.ObjectPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeProvider;

import javafx.scene.Parent;

@Component
public class ThemePreference extends ObjectPreference<Class<? extends Theme>> implements ManagedGlobalPreference, UserPreference<Class<? extends Theme>> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "theme"; //NOI18N

    private final List<Class<? extends Theme>> themeClasses;

	private final PreferenceEditorFactory preferenceEditorFactory;

	public ThemePreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory,
			@Autowired @Qualifier("default") ThemeProvider themeProvider,
			@Autowired List<ThemeProvider> themeProviders) {
		super(preferencesContext, PREFERENCE_KEY, themeProvider.themes().get(0));
		this.preferenceEditorFactory = preferenceEditorFactory;
		themeClasses = new ArrayList<>();
		themeProviders.forEach(tp -> themeClasses.addAll(tp.themes()));
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
			setValue(clsName == null ? getDefault() : (Class<? extends Theme>) Class.forName(clsName));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.global.theme";
	}

	@SuppressWarnings("unchecked")
	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newChoiceFieldEditor(this,
				themeClasses.toArray((Class<? extends Theme>[])new Class[0]), (c) -> Theme.name(c));
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_D;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}

}
