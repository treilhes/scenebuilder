package com.oracle.javafx.scenebuilder.cssanalyser.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.cssanalyser.controller.CssPanelMenuController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.css.copy.styleable.path",
		descriptionKey = "action.description.css.copy.styleable.path")
public class CopyStyleablePathAction extends AbstractAction {

	private final CssPanelMenuController cssPanelMenuController;

	public CopyStyleablePathAction(
	        @Autowired Api api,
			@Autowired @Lazy CssPanelMenuController cssPanelMenuController) {
		super(api);
		this.cssPanelMenuController = cssPanelMenuController;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public ActionStatus perform() {
		cssPanelMenuController.copyStyleablePath();
		return ActionStatus.DONE;
	}
}