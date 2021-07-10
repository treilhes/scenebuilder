package com.oracle.javafx.scenebuilder.preview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.contenteditor.actions.SetRootSizeAction;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SetRootSizeExtension extends AbstractActionExtension<SetRootSizeAction> {
    
    private final PreviewWindowController preview;

    public SetRootSizeExtension(@Autowired PreviewWindowController preview) {
        super();
        this.preview = preview;
    }

    @Override
    public boolean canPerform() {
        return preview.getStage().isShowing();
    }

    @Override
    public void postPerform() {
        preview.setSize(getExtendedAction().getSize());
    }
    
    
}
