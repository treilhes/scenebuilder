package com.oracle.javafx.scenebuilder.about.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.about.controller.AboutWindowController;
import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ShowAboutAction extends AbstractAction {

    private final AboutWindowController aboutWindowController;
    private final IconSetting iconSetting;
    
    public ShowAboutAction(
            @Autowired Api api,
            @Autowired IconSetting iconSetting,
            @Autowired @Lazy AboutWindowController aboutWindowController) {
        super(api);
        this.aboutWindowController = aboutWindowController;
        this.iconSetting = iconSetting;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        aboutWindowController.openWindow();
        iconSetting.setWindowIcon(aboutWindowController.getStage());
        return ActionStatus.DONE;
    }
}