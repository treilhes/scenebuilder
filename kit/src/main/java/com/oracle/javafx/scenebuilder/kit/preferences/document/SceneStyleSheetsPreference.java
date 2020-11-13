package com.oracle.javafx.scenebuilder.kit.preferences.document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.preferences.ManagedDocumentPreference;
import com.oracle.javafx.scenebuilder.api.preferences.PreferencesContext;
import com.oracle.javafx.scenebuilder.api.preferences.type.ListOfStringPreference;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

import javafx.collections.ObservableList;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class SceneStyleSheetsPreference extends ListOfStringPreference implements ManagedDocumentPreference {
	    
    /***************************************************************************
     *                                                                         *
     * Static fields                                                           *
     *                                                                         *
     **************************************************************************/
    public static final String PREFERENCE_KEY = "sceneStyleSheets"; //NOI18N
    public static final List<String> PREFERENCE_DEFAULT_VALUE = new ArrayList<>();

	public SceneStyleSheetsPreference(@Autowired PreferencesContext preferencesContext) {
		super(preferencesContext, PREFERENCE_KEY, PREFERENCE_DEFAULT_VALUE);
	}
	
	public void setValue(ObservableList<File> files) {
        getValue().clear();
        for (File file : files) {
            final String filePath = file.getPath();
            getValue().add(filePath);
        }
    }

    public void removeSceneStyleSheet(String filePath) {
        getValue().remove(filePath);
    }

    public void removeSceneStyleSheet(List<String> filePaths) {
        for (String filePath : filePaths) {
            removeSceneStyleSheet(filePath);
        }
    }
}
