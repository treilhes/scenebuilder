package com.oracle.javafx.scenebuilder.preview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.set.preview.size", descriptionKey = "action.description.set.preview.size")
public class SetPreviewSizeAction extends AbstractAction {

    private final PreviewWindowController previewWindowController;
    private final DocumentManager documentManager;
    
    private Size size;

    public SetPreviewSizeAction(
            @Autowired Api api, 
            @Autowired DocumentManager documentManager,
            @Autowired PreviewWindowController previewWindowController) {
        super(api);
        this.documentManager = documentManager;
        this.previewWindowController = previewWindowController;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public boolean canPerform() {
        if (size == null) {
            return false;
        }
        FXOMDocument fd = documentManager.fxomDocument().get();
        
        boolean previewIsValid = previewWindowController.getStage().isShowing() && !fd.is3D() && fd.isNode()
                && previewWindowController.sizeDoesFit(size);
        
        return previewIsValid;
    }

    @Override
    public ActionStatus perform() {
        previewWindowController.setSize(size);
        return ActionStatus.DONE;
    }
}