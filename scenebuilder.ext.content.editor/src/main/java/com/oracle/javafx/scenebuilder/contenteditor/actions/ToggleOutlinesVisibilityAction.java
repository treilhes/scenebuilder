package com.oracle.javafx.scenebuilder.contenteditor.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.contenteditor.controller.ContentPanelController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about",
        accelerator = "CTRL+E")
public class ToggleOutlinesVisibilityAction extends AbstractAction {

    private final ContentPanelController contentPanelController;
    
    public ToggleOutlinesVisibilityAction(
            @Autowired Api api,
            @Autowired @Lazy ContentPanelController contentPanelController) {
        super(api);
        this.contentPanelController = contentPanelController;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        contentPanelController.setOutlinesVisible(!contentPanelController.isOutlinesVisible());
        return ActionStatus.DONE;
    }
}