package com.oracle.javafx.scenebuilder.imagelibrary.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.ImageLibraryPanelController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(
		nameKey = "action.name.import.selection",
		descriptionKey = "action.description.import.selection")
public class ImportSelectionAction extends AbstractAction {

	private final Editor editorController;
	private final ImageLibraryPanelController libraryPanelController;

	public ImportSelectionAction(
	        @Autowired Api api,
			@Autowired @Lazy Editor editorController,
			@Autowired @Lazy ImageLibraryPanelController libraryPanelController) {
		super(api);
		this.editorController = editorController;
		this.libraryPanelController = libraryPanelController;
	}

	@Override
	public boolean canPerform() {
		// This method cannot be called if there is not a valid selection, a selection
	    // eligible for being dropped onto Library panel.
	    return editorController.getSelection().getGroup() instanceof ObjectSelectionGroup;
	}

	@Override
	public ActionStatus perform() {
		AbstractSelectionGroup asg = editorController.getSelection().getGroup();
        ObjectSelectionGroup osg = (ObjectSelectionGroup)asg;
        assert !osg.getItems().isEmpty();
        List<FXOMObject> selection = new ArrayList<>(osg.getItems());
        libraryPanelController.performImportSelection(selection);
        return ActionStatus.DONE;
	}
}