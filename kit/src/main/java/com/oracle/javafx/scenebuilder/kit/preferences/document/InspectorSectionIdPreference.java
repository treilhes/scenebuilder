package com.oracle.javafx.scenebuilder.kit.preferences.document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.EnumPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.kit.editor.panel.inspector.InspectorPanelController.SectionId;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class InspectorSectionIdPreference extends EnumPreference<SectionId> implements ManagedDocumentPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "inspectorSectionId"; //NOI18N
    public static final SectionId PREFERENCE_DEFAULT_VALUE = SectionId.PROPERTIES;

	public InspectorSectionIdPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, SectionId.class, PREFERENCE_DEFAULT_VALUE);
	}

}
