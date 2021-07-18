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
		nameKey = "action.name.view.by.property.name",
		descriptionKey = "action.description.view.by.property.name")
public class ViewByPropertyNameAction extends AbstractViewAction {
	public ViewByPropertyNameAction(
	        @Autowired Api api,
			@Autowired @Lazy InspectorPanelController inspectorPanelController) {
		super(api,ViewMode.PROPERTY_NAME, inspectorPanelController);
	}
}