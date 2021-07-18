package com.oracle.javafx.scenebuilder.imagelibrary.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.imagelibrary.controller.ImageJarAnalysisReportController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.show.jar.analysis.report",
		descriptionKey = "action.description.show.jar.analysis.report",
		accelerator = "CTRL+J")
public class ShowJarAnalysisReportAction extends AbstractAction {

	private final DocumentWindow documentWindowController;
	private final ImageJarAnalysisReportController jarAnalysisReportController;

	public ShowJarAnalysisReportAction(
	        @Autowired Api api,
			@Autowired @Lazy DocumentWindow documentWindowController,
			@Autowired @Lazy ImageJarAnalysisReportController jarAnalysisReportController) {
		super(api);
		this.documentWindowController = documentWindowController;
		this.jarAnalysisReportController = jarAnalysisReportController;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public ActionStatus perform() {
		//jarAnalysisReportController.setToolStylesheet(documentWindowController.getToolStylesheet());
        jarAnalysisReportController.openWindow();
        return ActionStatus.DONE;
	}

}