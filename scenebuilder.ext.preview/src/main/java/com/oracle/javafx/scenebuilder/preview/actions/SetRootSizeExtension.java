package com.oracle.javafx.scenebuilder.preview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.contenteditor.actions.SetRootSizeAction;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SetRootSizeExtension extends AbstractActionExtension<SetRootSizeAction> {

    private final ActionFactory actionFactory;

    public SetRootSizeExtension(@Autowired ActionFactory actionFactory) {
        super();
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerform() {
        // return preview.getStage().isShowing();
        return actionFactory.create(SetPreviewSizeAction.class, a -> a.setSize(getExtendedAction().getSize()))
                .canPerform();
    }

    @Override
    public void postPerform() {
        // preview.setSize(getExtendedAction().getSize());
        actionFactory.create(SetPreviewSizeAction.class, a -> a.setSize(getExtendedAction().getSize())).perform();
    }

}
