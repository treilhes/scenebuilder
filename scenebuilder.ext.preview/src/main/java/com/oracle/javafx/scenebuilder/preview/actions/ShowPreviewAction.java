package com.oracle.javafx.scenebuilder.preview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.preview.dialog", descriptionKey = "action.description.show.preview.dialog", 
    accelerator = "CTRL+P")
public class ShowPreviewAction extends AbstractAction {

    private final DocumentManager documentManager;
    private final PreviewWindowController previewWindowController;

    public ShowPreviewAction(@Autowired Api api, 
            @Autowired PreviewWindowController previewWindowController,
            @Autowired DocumentManager documentManager) {
        super(api);
        this.documentManager = documentManager;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public boolean canPerform() {
        return documentManager.fxomDocument().get() != null;
    }

    @Override
    public ActionStatus perform() {
        previewWindowController.getStage().centerOnScreen();
        previewWindowController.openWindow();
        return ActionStatus.DONE;
    }
}