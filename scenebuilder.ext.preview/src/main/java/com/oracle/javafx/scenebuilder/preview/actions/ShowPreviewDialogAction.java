package com.oracle.javafx.scenebuilder.preview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

import javafx.scene.control.DialogPane;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.preview", descriptionKey = "action.description.show.preview")
public class ShowPreviewDialogAction extends AbstractAction {

    private final DocumentManager documentManager;
    private final PreviewWindowController previewWindowController;
    
    public ShowPreviewDialogAction(
            @Autowired Api api,
            @Autowired PreviewWindowController previewWindowController,
            @Autowired DocumentManager documentManager) {
        super(api);
        this.documentManager = documentManager;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public boolean canPerform() {
        FXOMDocument fd = documentManager.fxomDocument().get();
        return fd != null && fd.getSceneGraphRoot() instanceof DialogPane;
    }

    @Override
    public ActionStatus perform() {
        previewWindowController.openDialog();
        return ActionStatus.DONE;
    }
}