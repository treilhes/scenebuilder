package com.oracle.javafx.scenebuilder.document.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.HierarchyPanel.DisplayOption;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.document.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.show.node.id",
		descriptionKey = "action.description.show.node.id")
public class ShowNodeIdAction extends AbstractShowAction {
	public ShowNodeIdAction(
	        @Autowired Api api,
			@Autowired @Lazy DocumentPanelController documentPanelController,
			@Autowired @Lazy DisplayOptionPreference displayOptionPreference) {
		super(api, DisplayOption.NODEID, documentPanelController, displayOptionPreference);
	}
}