package com.oracle.javafx.scenebuilder.inspector.actions;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController.ViewMode;

public class AbstractViewAction extends AbstractAction {

	private final InspectorPanelController inspectorPanelController;
	private final ViewMode option;

	public AbstractViewAction(Api api, ViewMode option, InspectorPanelController inspectorPanelController) {
		super(api);
		this.option = option;
		this.inspectorPanelController = inspectorPanelController;
	}

	@Override
	public boolean canPerform() {
		//return inspectorPanelController.getViewMode() != option;
		return true;
	}

	@Override
	public ActionStatus perform() {
		inspectorPanelController.setViewMode(option);
		return ActionStatus.DONE;
	}

}