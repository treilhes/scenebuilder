package com.oracle.javafx.scenebuilder.inspector.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController.ShowMode;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.show.edited",
		descriptionKey = "action.description.show.edited")
public class ShowEditedAction extends AbstractShowAction {
	public ShowEditedAction(
	        @Autowired Api api,
			@Autowired @Lazy InspectorPanelController inspectorPanelController) {
		super(api,ShowMode.EDITED, inspectorPanelController);
	}
}