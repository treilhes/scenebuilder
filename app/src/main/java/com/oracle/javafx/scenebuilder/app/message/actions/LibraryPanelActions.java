package com.oracle.javafx.scenebuilder.app.message.actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.MainController;
import com.oracle.javafx.scenebuilder.app.report.JarAnalysisReportController;
import com.oracle.javafx.scenebuilder.kit.editor.EditorController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.library.manager.LibraryDialogController;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.dialog.ErrorDialog;
import com.oracle.javafx.scenebuilder.kit.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.kit.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.kit.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.kit.library.user.UserLibrary;
import com.oracle.javafx.scenebuilder.kit.preferences.global.DisplayModePreference;

import javafx.stage.Window;

public class LibraryPanelActions {

	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.show.jar.analysis.report",
			descriptionKey = "action.description.show.jar.analysis.report",
			accelerator = "CTRL+J")
	public static class ShowJarAnalysisReport extends AbstractAction {
		
		@Autowired @Lazy private DocumentWindowController documentWindowController;
		@Autowired @Lazy private JarAnalysisReportController jarAnalysisReportController;
		
		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			jarAnalysisReportController.setToolStylesheet(documentWindowController.getToolStylesheet());
	        jarAnalysisReportController.openWindow();
		}
				
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.reveal.custom.folder",
			descriptionKey = "action.description.reveal.custom.folder")
	public static class RevealCustomFolder extends AbstractAction {
		
		@Autowired @Lazy private DocumentWindowController documentWindowController;
		@Autowired @Lazy private UserLibrary userLibrary;
		
		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
			String userLibraryPath = userLibrary.getPath();
			try {
				EditorPlatform.revealInFileBrowser(new File(userLibraryPath));
			} catch (IOException x) {
				final ErrorDialog errorDialog = new ErrorDialog(null);
				errorDialog.setMessage(
						I18N.getString("alert.reveal.failure.message", 
								documentWindowController.getStage().getTitle()));
				errorDialog.setDetails(I18N.getString("alert.reveal.failure.details"));
				errorDialog.setDebugInfoWithThrowable(x);
				errorDialog.showAndWait();
			}
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.import.selection",
			descriptionKey = "action.description.import.selection")
	public static class ImportSelectionAction extends AbstractAction {
		
		@Autowired @Lazy private EditorController editorController;
		@Autowired @Lazy private LibraryPanelController libraryPanelController;
		
		@Override
		public boolean canPerform() {
			// This method cannot be called if there is not a valid selection, a selection
		    // eligible for being dropped onto Library panel.
		    return editorController.getSelection().getGroup() instanceof ObjectSelectionGroup;
		}

		@Override
		public void perform() {
			AbstractSelectionGroup asg = editorController.getSelection().getGroup();
            ObjectSelectionGroup osg = (ObjectSelectionGroup)asg;
            assert !osg.getItems().isEmpty();
            List<FXOMObject> selection = new ArrayList<>(osg.getItems());
            libraryPanelController.performImportSelection(selection);
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.view.as.sections",
			descriptionKey = "action.description.view.as.sections")
	public static class ViewAsSectionsAction extends AbstractAction {
		
		@Autowired @Lazy private LibraryPanelController libraryPanelController;
		@Autowired @Lazy private DisplayModePreference displayModePreference;
		
		@Override
		public boolean canPerform() {
			return libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SECTIONS;
		}

		@Override
		public void perform() {
			if (libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SEARCH) {
	            libraryPanelController.setDisplayMode(LibraryPanelController.DISPLAY_MODE.SECTIONS);
	        } else {
	            libraryPanelController.setPreviousDisplayMode(LibraryPanelController.DISPLAY_MODE.SECTIONS);
	        }

			displayModePreference.setValue(libraryPanelController.getDisplayMode())
				.writeToJavaPreferences();
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.view.as.list",
			descriptionKey = "action.description.view.as.list")
	public static class ViewAsListAction extends AbstractAction {
		
		@Autowired @Lazy private LibraryPanelController libraryPanelController;
		@Autowired @Lazy private DisplayModePreference displayModePreference;
		
		@Override
		public boolean canPerform() {
			return libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.LIST;
		}

		@Override
		public void perform() {
			if (libraryPanelController.getDisplayMode() != LibraryPanelController.DISPLAY_MODE.SEARCH) {
	            libraryPanelController.setDisplayMode(LibraryPanelController.DISPLAY_MODE.LIST);
	        } else {
	            libraryPanelController.setPreviousDisplayMode(LibraryPanelController.DISPLAY_MODE.LIST);
	        }

			displayModePreference.setValue(libraryPanelController.getDisplayMode())
				.writeToJavaPreferences();
		}
	}
	
	@Component
	@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
	@Lazy
	@ActionMeta(
			nameKey = "action.name.manage.jar.fxml",
			descriptionKey = "action.description.manage.jar.fxml")
	public static class ManageJarFxmlAction extends AbstractAction {
		
		@Autowired @Lazy private MainController mainController;
		@Autowired @Lazy private DocumentWindowController documentWindowController;
		@Autowired @Lazy private LibraryDialogController libraryDialogController;
		@Autowired @Lazy private LibraryPanelController libraryPanelController;
		
		@Override
		public boolean canPerform() {
			return true;
		}

		@Override
		public void perform() {
//			libraryDialogController = new LibraryDialogController(editorController, libraryPanelController,
//            		mavenSetting, mavenPreferences, repositoryPreferences, getStage());
			
			libraryDialogController.setOnAddJar(() -> onImportJarFxml(libraryDialogController.getStage()));
            libraryDialogController.setOnEditFXML(fxmlPath -> {
                    if (mainController.lookupUnusedDocumentWindowController() != null) {
                        libraryDialogController.closeWindow();
                    }
                    mainController.performOpenRecent(documentWindowController, fxmlPath.toFile());
            });
            libraryDialogController.setOnAddFolder(() -> onImportFromFolder(libraryDialogController.getStage()));

	        libraryDialogController.openWindow();
		}
		
		private void onImportJarFxml(Window owner) {
	        libraryPanelController.performImportJarFxml(owner);
	    }
	    
		private void onImportFromFolder(Window owner) {
	        libraryPanelController.performImportFromFolder(owner);
	    }
	}
}
