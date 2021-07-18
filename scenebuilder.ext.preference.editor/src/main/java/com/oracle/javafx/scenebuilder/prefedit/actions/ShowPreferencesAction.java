package com.oracle.javafx.scenebuilder.prefedit.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.prefedit.controller.PreferencesWindowController;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.preferences", descriptionKey = "action.description.show.preferences")
public class ShowPreferencesAction extends AbstractAction {

    private final PreferencesWindowController preferencesWindowController;
    
    public ShowPreferencesAction(
            @Autowired Api api,
            @Autowired @Lazy PreferencesWindowController preferencesWindowController) {
        super(api);
        this.preferencesWindowController = preferencesWindowController;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        preferencesWindowController.getStage().centerOnScreen();
        preferencesWindowController.openWindow();
        return ActionStatus.DONE;
    }
}