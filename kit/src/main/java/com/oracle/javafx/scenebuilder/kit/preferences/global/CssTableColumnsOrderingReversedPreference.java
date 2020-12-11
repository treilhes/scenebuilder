package com.oracle.javafx.scenebuilder.kit.preferences.global;

import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups;
import com.oracle.javafx.scenebuilder.api.preferences.DefaultPreferenceGroups.PreferenceGroup;
import com.oracle.javafx.scenebuilder.api.preferences.ManagedGlobalPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.UserPreference;
import com.oracle.javafx.scenebuilder.api.preferences.type.BooleanPreference;
import com.oracle.javafx.scenebuilder.api.theme.PreferenceEditorFactory;
import javafx.scene.Parent;

@Component
public class CssTableColumnsOrderingReversedPreference extends BooleanPreference implements ManagedGlobalPreference, UserPreference<Boolean> {
	/***************************************************************************
     *                                                                         *
     * Support Classes                                                         *
     *                                                                         *
     **************************************************************************/

    public enum CSSAnalyzerColumnsOrder {

        DEFAULTS_FIRST {

            @Override
            public String toString() {
                return I18N.getString("prefs.cssanalyzer.columns.defaults.first");
            }
        },
        DEFAULTS_LAST {

            @Override
            public String toString() {
                return I18N.getString("prefs.cssanalyzer.columns.defaults.last");
            }
        }
    }

    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "CSS_TABLE_COLUMNS_ORDERING_REVERSED"; //NOI18N
    public static final boolean PREFERENCE_DEFAULT_VALUE = false;

    private final PreferenceEditorFactory preferenceEditorFactory;

	public CssTableColumnsOrderingReversedPreference(
			@Autowired PreferencesContext preferencesContext,
			@Autowired PreferenceEditorFactory preferenceEditorFactory) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
		this.preferenceEditorFactory = preferenceEditorFactory;
	}

	@Override
	public PreferenceGroup getGroup() {
		return DefaultPreferenceGroups.GLOBAL_GROUP_C;
	}

	@Override
	public String getOrderKey() {
		return getGroup() + "_D";
	}

	@Override
	public String getLabelI18NKey() {
		return "prefs.cssanalyzer.columns.order";
	}

	@Override
	public Parent getEditor() {
		Function<Boolean, CSSAnalyzerColumnsOrder> adapter =
				b -> b ? CSSAnalyzerColumnsOrder.DEFAULTS_LAST : CSSAnalyzerColumnsOrder.DEFAULTS_FIRST;

		Function<CSSAnalyzerColumnsOrder, Boolean> reverseAdapter =
				e -> {
					switch (e) {
					case DEFAULTS_FIRST:
						return false;
					default:
						return true;
					}
				};
		return preferenceEditorFactory.newChoiceFieldEditor(this, CSSAnalyzerColumnsOrder.values(), adapter, reverseAdapter);
	}

}
