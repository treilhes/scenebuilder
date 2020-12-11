package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.IntegerPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import javafx.scene.Parent;

@Component
public class RecentItemsSizePreference extends IntegerPreference implements ManagedGlobalPreference, UserPreference<Integer> {

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "RECENT_ITEMS_SIZE"; //NOI18N
    public static final int PREFERENCE_DEFAULT_VALUE = 15;
    public static final Integer[] RECENT_ITEMS_SIZE = {5, 10, 15, 20};

	private final RecentItemsPreference recentItems;
	private final PreferenceEditorFactory preferenceEditorFactory;

	public RecentItemsSizePreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired RecentItemsPreference recentItems,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
		this.recentItems = recentItems;
	}

	@Override
	public Preference<Integer> setValue(Integer newValue) {
		Preference<Integer> that = super.setValue(newValue);
		// Remove last items depending on the size

		if (recentItems != null) {
			while (recentItems.getValue().size() > getValue()) {
	            recentItems.getValue().remove(recentItems.getValue().size() - 1);
	        }
	        recentItems.writeToJavaPreferences();
		}

		return that;
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.recent.items";
	}

	@Override
	public Parent getEditor() {
		return preferenceEditorFactory.newChoiceFieldEditor(this, RECENT_ITEMS_SIZE);
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_E;
	}

	@Override
	public String getOrderKey() {
		return getGroup().getOrderKey() + "_A";
	}

}
