package com.oracle.javafx.scenebuilder.app.fs;

import java.io.File;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.jfxplace.core.api.application.ApplicationActionFactory;
import com.treilhes.jfxplace.core.api.document.DocumentActionFactory;
import com.treilhes.jfxplace.core.api.fs.OpenFileHandler;
import com.treilhes.jfxplace.core.api.ui.dialog.ApplicationDialog;

@ApplicationSingleton
public class FxmlOpenFileHandler implements OpenFileHandler {

	private static final String FXML_EXTENSION = ".fxml";

	private static final Logger logger = LoggerFactory.getLogger(FxmlOpenFileHandler.class);

	private final ApplicationDialog applicationDialog;
	private final ApplicationActionFactory applicationActionFactory;
	private final DocumentActionFactory documentActionFactory;

	public FxmlOpenFileHandler(
			ApplicationActionFactory applicationActionFactory,
			DocumentActionFactory documentActionFactory, 
			ApplicationDialog applicationDialog) {

		this.applicationActionFactory = applicationActionFactory;
		this.documentActionFactory = documentActionFactory;
		this.applicationDialog = applicationDialog;
	}

	@Override
	public boolean canOpen(File file) {
		return file.getName().toLowerCase().endsWith(FXML_EXTENSION);
	}

	@Override
	public void open(File file) {
		try {
			var fileURL = file.toURI().toURL();
			applicationActionFactory.lookupUnusedInstance(fileURL, (instance) -> {
				instance.openWindow();
				documentActionFactory.loadFile(file).perform();
			}).perform();
		} catch (MalformedURLException e) {
			logger.error("Error converting file to URL: {}", file, e);
			applicationDialog.addError("Unable to open file", e.getMessage(), e);
		}
	}

}
