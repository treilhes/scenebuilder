package com.oracle.javafx.scenebuilder.app.preferences.global;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.app.preferences.GlobalPreferences.CSSAnalyzerColumnsOrder;

/**
 * 
 * @author ptreilhes
 *
 */
@Component
public class CssAnalyzerColumnsOrderPreference extends EnumPreference<CSSAnalyzerColumnsOrder> implements ManagedGlobalPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "CSS_TABLE_COLUMNS_ORDERING"; //NOI18N
    public static final CSSAnalyzerColumnsOrder PREFERENCE_DEFAULT_VALUE = CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;

	public CssAnalyzerColumnsOrderPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, CSSAnalyzerColumnsOrder.class, PREFERENCE_DEFAULT_VALUE);
	}

}
