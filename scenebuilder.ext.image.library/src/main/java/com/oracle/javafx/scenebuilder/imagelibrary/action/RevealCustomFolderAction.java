package com.oracle.javafx.scenebuilder.imagelibrary.action;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibrary;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.reveal.custom.folder",
		descriptionKey = "action.description.reveal.custom.folder")
public class RevealCustomFolderAction extends AbstractAction {

	private final DocumentWindow documentWindowController;
	private final ImageLibrary userLibrary;
	private final FileSystem fileSystem;
	private final Dialog dialog;

	public RevealCustomFolderAction(
	        @Autowired Api api,
	        @Autowired ImageLibrary imageLibrary,
			@Autowired @Lazy DocumentWindow documentWindowController
	        ) {
		super(api);
		this.documentWindowController = documentWindowController;
		this.userLibrary = imageLibrary;
		this.fileSystem = api.getFileSystem();
		this.dialog = api.getApiDoc().getDialog();
	}

	@Override
	public boolean canPerform() {
		return true;
	}

	@Override
	public ActionStatus perform() {
		try {
			fileSystem.revealInFileBrowser(userLibrary.getPath());
		} catch (IOException x) {
			dialog.showErrorAndWait("",
					I18N.getString("alert.reveal.failure.message", documentWindowController.getStage().getTitle()),
					I18N.getString("alert.reveal.failure.details"),
					x);
			return ActionStatus.FAILED;
		}
		return ActionStatus.DONE;
	}
}