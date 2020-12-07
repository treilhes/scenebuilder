package com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;

@Component
public class DialogController implements Dialog {

	private final SceneBuilderManager sceneBuilderManager;

    public DialogController(SceneBuilderManager sceneBuilderManager) {
	    this.sceneBuilderManager = sceneBuilderManager;
	}

	@Override
	public void showErrorAndWait(String title, String message, String detail, Throwable cause) {
		final ErrorDialog errorDialog = new ErrorDialog(sceneBuilderManager, null);
        errorDialog.setTitle(title);
        errorDialog.setMessage(message);
        errorDialog.setDetails(detail);
        errorDialog.setDebugInfoWithThrowable(cause);
        errorDialog.showAndWait();
	}

}
