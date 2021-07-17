package com.oracle.javafx.scenebuilder.fs.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save",
        accelerator = "CTRL+N")
public class LoadBlankInNewWindowAction extends AbstractAction {

    private final Main main;
    private final ActionFactory actionFactory;

    public LoadBlankInNewWindowAction(
    // @formatter:off
            @Autowired Api api, 
            @Autowired ActionFactory actionFactory,
            @Autowired Main main) {
    // @formatter:on
        super(api);
        this.main = main;
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        final Document newWindow = main.makeNewWindow();
        newWindow.openWindow();
        return actionFactory.create(LoadBlankAction.class).perform();
   }
}