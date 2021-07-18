package com.oracle.javafx.scenebuilder.imagelibrary.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;
import com.oracle.javafx.scenebuilder.library.api.LibraryDialogFactory;

@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.manage.jar.fxml",
			descriptionKey = "action.description.manage.jar.fxml")
	public class ManageJarFxmlAction extends AbstractAction {

		private final Main mainController;
		private final Document documentWindowController;
		private final LibraryDialogFactory libraryDialogFactory;
		private final ImageLibraryPanelController libraryPanelController;
        private final ImageLibrary controlLibrary;

		public ManageJarFxmlAction(
		        @Autowired Api api,
		        @Autowired @Lazy ImageLibrary controlLibrary,
				@Autowired @Lazy Document documentWindowController,
				@Autowired @Lazy LibraryDialogFactory libraryDialogFactory,
				@Autowired @Lazy ImageLibraryPanelController libraryPanelController) {
			super(api);
			this.mainController = api.getMain();
			this.documentWindowController = documentWindowController;
			this.libraryDialogFactory = libraryDialogFactory;
			this.libraryPanelController = libraryPanelController;
			this.controlLibrary = controlLibrary;
		}

		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public ActionStatus perform() {
//			libraryDialogController = new LibraryDialogController(editorController, libraryPanelController,
//            		mavenSetting, mavenPreferences, repositoryPreferences, getStage());

		    /*
			libraryDialogController.setOnAddJar(() -> onImportJarFxml(libraryDialogController.getStage()));
            libraryDialogController.setOnEditFXML(fxmlPath -> {
                    if (mainController.lookupUnusedDocumentWindowController() != null) {
                        libraryDialogController.closeWindow();
                    }
                    mainController.performOpenRecent(documentWindowController, fxmlPath.toFile());
            });
            libraryDialogController.setOnAddFolder(() -> onImportFromFolder(libraryDialogController.getStage()));
*/
		    controlLibrary.openDialog();
		    return ActionStatus.DONE;
		}

	}