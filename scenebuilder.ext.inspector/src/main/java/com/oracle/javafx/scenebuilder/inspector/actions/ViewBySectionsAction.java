package com.oracle.javafx.scenebuilder.inspector.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController.ViewMode;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.view.by.section",
		descriptionKey = "action.description.view.by.section")
public class ViewBySectionsAction extends AbstractViewAction {
	public ViewBySectionsAction(
	        @Autowired Api api,
			@Autowired @Lazy InspectorPanelController inspectorPanelController) {
		super(api,ViewMode.SECTION, inspectorPanelController);
	}
}