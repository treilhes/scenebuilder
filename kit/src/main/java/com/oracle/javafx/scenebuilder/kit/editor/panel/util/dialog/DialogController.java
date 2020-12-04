package com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog;

import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;

@Component
public class DialogController implements Dialog {

	public DialogController() {

	}

	@Override
	public void showErrorAndWait(String title, String message, String detail, Throwable cause) {
		final ErrorDialog errorDialog = new ErrorDialog(null);
        errorDialog.setTitle(title);
        errorDialog.setMessage(message);
        errorDialog.setDetails(detail);
        errorDialog.setDebugInfoWithThrowable(cause);
        errorDialog.showAndWait();
	}

}
