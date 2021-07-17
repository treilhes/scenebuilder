package com.oracle.javafx.scenebuilder.document.actions;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.HierarchyPanel.DisplayOption;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.document.panel.document.DocumentPanelController;
import com.oracle.javafx.scenebuilder.document.preferences.global.DisplayOptionPreference;

public class AbstractShowAction extends AbstractAction {

	private final DocumentPanelController documentPanelController;
	private final DisplayOptionPreference displayOptionPreference;
	private final DisplayOption option;

	public AbstractShowAction(Api api, DisplayOption option, DocumentPanelController documentPanelController, DisplayOptionPreference displayOptionPreference) {
		super(api);
		this.option = option;
		this.documentPanelController = documentPanelController;
		this.displayOptionPreference = displayOptionPreference;
	}

	@Override
	public boolean canPerform() {
		return documentPanelController.getHierarchyPanelController().getDisplayOption() != option;
	}

	@Override
	public ActionStatus perform() {
		documentPanelController.getHierarchyPanelController().setDisplayOption(option);
    	documentPanelController.getDocumentAccordion().setExpandedPane(
    		documentPanelController.getDocumentAccordion().getPanes().get(0));

    	displayOptionPreference
    		.setValue(documentPanelController.getHierarchyPanelController().getDisplayOption())
    		.writeToJavaPreferences();
    	return ActionStatus.DONE;
	}

}