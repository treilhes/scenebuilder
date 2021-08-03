package com.oracle.javafx.scenebuilder.welcome.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.action.AbstractActionExtension;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.launcher.actions.OpenScenebuilderAction;
import com.oracle.javafx.scenebuilder.welcome.controller.WelcomeDialogWindowController;

public class OpenScenebuilderExtension extends AbstractActionExtension<OpenScenebuilderAction> {

    private ApplicationContext context;

    public OpenScenebuilderExtension(
            @Autowired ApplicationContext context) {
        super();
        this.context = context;
    }

    @Override
    public boolean canPerform() {
        return getExtendedAction().getFiles() == null || getExtendedAction().getFiles().size() == 0;
    }

    @Override
    public void postPerform() {
        WelcomeDialogWindowController wdwc = context.getBean(WelcomeDialogWindowController.class);

        // Unless we're on a Mac we're starting SB directly (fresh start)
        // so we're not opening any file and as such we should show the Welcome Dialog

        SbPlatform.runLater(() -> {
            wdwc.getStage().show();
        });
    }

}
