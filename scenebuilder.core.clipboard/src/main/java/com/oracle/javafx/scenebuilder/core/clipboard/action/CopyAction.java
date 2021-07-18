package com.oracle.javafx.scenebuilder.core.clipboard.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.clipboard.Clipboard;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.copy", descriptionKey = "action.description.copy")
public class CopyAction extends AbstractAction {

    private final Clipboard clipboard;
    
    public CopyAction(
            @Autowired Api api,
            @Autowired Clipboard clipboard) {
        super(api);
        this.clipboard = clipboard;
    }

    @Override
    public boolean canPerform() {
        return clipboard.canPerformCopy();
    }

    @Override
    public ActionStatus perform() {
        clipboard.performCopy();
        return ActionStatus.DONE;
    }
}