package com.oracle.javafx.scenebuilder.ext.actions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Workspace;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import javafx.scene.Parent;
import javafx.scene.SubScene;
import lombok.Getter;
import lombok.Setter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
		nameKey = "action.name.show.jar.analysis.report",
		descriptionKey = "action.description.show.jar.analysis.report",
		accelerator = "CTRL+J")
public class ApplyCssContentAction extends AbstractAction {

	private ApplyCssContentConfig config;

	private final Editor editorController;
	private final Content contentPanelController;
	private final Workspace workspaceController;

	public ApplyCssContentAction(
			@Autowired ApplicationContext context,
			@Autowired @Lazy Editor editorController,
			@Autowired @Lazy Content contentPanelController,
			@Autowired @Lazy Workspace workspaceController) {
		super(context);
		this.editorController = editorController;
		this.contentPanelController = contentPanelController;
		this.workspaceController = workspaceController;

	}

	public synchronized ApplyCssContentConfig getActionConfig() {
		if (config == null) {
			config = new ApplyCssContentConfig();
		}
		return config;
	}

	public synchronized void resetActionConfig() {
		config = null;
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public void perform() {
		assert getActionConfig() != null;

		StylesheetProvider2 stylesheetProvider = getActionConfig();

        SubScene contentSubScene = contentPanelController.getContentSubScene();
        contentSubScene.setUserAgentStylesheet(stylesheetProvider.getUserAgentStylesheet());
        workspaceController.getThemeStyleSheets().clear();
        workspaceController.getThemeStyleSheets().addAll(stylesheetProvider.getStylesheets());

        // Update scenegraph layout, etc
        FXOMDocument fxomDocument = editorController.getFxomDocument();
        if (fxomDocument != null) {
            fxomDocument.refreshSceneGraph();
        }
//        Parent contentGroup = contentSubScene.getRoot();
//        contentGroup.getStylesheets().setAll(stylesheetProvider.getStylesheets());
//        if (fxomDocument != null) {
//            contentGroup.getStylesheets().addAll(fxomDocument.getDisplayStylesheets());
//        }
//        contentGroup.applyCss();
	}

	public static class ApplyCssContentConfig implements StylesheetProvider2 {
		private @Getter @Setter String userAgentStylesheet;
		private @Getter List<String> stylesheets = new ArrayList<>();
	}

}
