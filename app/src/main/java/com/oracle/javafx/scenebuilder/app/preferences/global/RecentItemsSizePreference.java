package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.Preference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.IntegerPreference;

@Component
public class RecentItemsSizePreference extends IntegerPreference implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "RECENT_ITEMS_SIZE"; //NOI18N
    public static final int PREFERENCE_DEFAULT_VALUE = 15;
    
	private final RecentItemsPreference recentItems;

	public RecentItemsSizePreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired RecentItemsPreference recentItems) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
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

}
