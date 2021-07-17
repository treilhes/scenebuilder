package com.oracle.javafx.scenebuilder.inspector.actions;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController;
import com.oracle.javafx.scenebuilder.inspector.controller.InspectorPanelController.ShowMode;

public class AbstractShowAction extends AbstractAction {

	private final InspectorPanelController inspectorPanelController;
	private final ShowMode option;

	public AbstractShowAction(Api api, ShowMode option, InspectorPanelController inspectorPanelController) {
		super(api);
		this.option = option;
		this.inspectorPanelController = inspectorPanelController;
	}

	@Override
	public boolean canPerform() {
		//return inspectorPanelController.getShowMode() != option;
		return true;
	}

	@Override
	public ActionStatus perform() {
		inspectorPanelController.setShowMode(option);
		return ActionStatus.DONE;
	}

}