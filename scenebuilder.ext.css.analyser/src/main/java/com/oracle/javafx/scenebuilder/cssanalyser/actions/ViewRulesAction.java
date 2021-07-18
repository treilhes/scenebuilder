package com.oracle.javafx.scenebuilder.cssanalyser.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelController;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelMenuController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.css.view.rules",
		descriptionKey = "action.description.css.view.rules")
public class ViewRulesAction extends AbstractAction {

	private final CssPanelMenuController cssPanelMenuController;
	private final CssPanelController cssPanelController;

	public ViewRulesAction(
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
		cssPanelMenuController.viewRules();
        cssPanelController.getDefaultsSplit().setDisable(true);
        cssPanelController.getHideDefaultValues().setDisable(true);
        return ActionStatus.DONE;
	}
}