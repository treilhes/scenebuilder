package com.oracle.javafx.scenebuilder.cssanalyser.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelMenuController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.css.split.defaults",
		descriptionKey = "action.description.css.split.defaults")
public class SplitDefaultsAction extends AbstractAction {

	private final CssPanelMenuController cssPanelMenuController;
	private final CssPanelController cssPanelController;

	public SplitDefaultsAction(
	        @Autowired Api api,
			@Autowired @Lazy CssPanelMenuController cssPanelMenuController,
			@Autowired @Lazy CssPanelController cssPanelController) {
		super(api);
		this.cssPanelMenuController = cssPanelMenuController;
		this.cssPanelController = cssPanelController;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public ActionStatus perform() {
		cssPanelMenuController.splitDefaultsAction(cssPanelController.getDefaultsSplit());
		return ActionStatus.DONE;
	}
}