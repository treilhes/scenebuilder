package com.oracle.javafx.scenebuilder.gluon.preferences;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonDefaultPositionDocumentPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonDefaultTextUserDocumentPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonDocumentPreferences {

	@Autowired
	@Lazy
	private GluonDefaultPositionDocumentPreference defaultPosition;
	
	@Autowired
	@Lazy
	private GluonDefaultTextUserDocumentPreference defaultText;
	
	public GluonDocumentPreferences() {
		// TODO Auto-generated constructor stub
	}

	public GluonDefaultPositionDocumentPreference getDefaultPosition() {
		return defaultPosition;
	}

	public GluonDefaultTextUserDocumentPreference getDefaultText() {
		return defaultText;
	}

	
}
