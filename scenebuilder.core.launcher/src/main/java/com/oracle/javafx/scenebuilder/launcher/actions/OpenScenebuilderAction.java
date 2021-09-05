package com.oracle.javafx.scenebuilder.launcher.actions;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.lifecycle.InitWithSceneBuilder;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.fs.action.LoadBlankInNewWindowAction;
import com.oracle.javafx.scenebuilder.fs.action.OpenFilesAction;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
public class OpenScenebuilderAction extends AbstractAction {
    
    private List<File> files;
    private final List<InitWithSceneBuilder> initializations;
    private final Main main;
    private final ActionFactory actionFactory;
    
    public OpenScenebuilderAction(
            @Autowired Api api,
            @Autowired Main main,
            @Autowired ActionFactory actionFactory,
            @Lazy @Autowired(required = false) List<InitWithSceneBuilder> initializations) {
        super(api);
        this.main = main;
        this.actionFactory = actionFactory;
        this.initializations = initializations;
    }

    public List<File> getFiles() {
        return files;
    }

    public void setFiles(List<File> files) {
        this.files = files;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        initializations.forEach(a -> a.init());

        if (files.isEmpty()) {
//            // Creates an empty document
//            final Document newWindow = main.makeNewWindow();
//
//            // Unless we're on a Mac we're starting SB directly (fresh start)
//            // so we're not opening any file and as such we should show the Welcome Dialog
//
//            SbPlatform.runLater(() -> {
//                newWindow.updateWithDefaultContent();
//                newWindow.openWindow();
//                SceneBuilderLoadingProgress.get().end();
//            });

            actionFactory.create(LoadBlankInNewWindowAction.class).checkAndPerform();
        } else {
            // Open files passed as arguments by the platform
            actionFactory.create(OpenFilesAction.class, a -> a.setFxmlFile(files)).checkAndPerform();
        }
        SceneBuilderLoadingProgress.get().end();
        return ActionStatus.DONE;
    }
}