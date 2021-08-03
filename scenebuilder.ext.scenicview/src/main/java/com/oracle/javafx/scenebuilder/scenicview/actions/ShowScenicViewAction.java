package com.oracle.javafx.scenebuilder.scenicview.actions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.scenicview.controller.ScenicViewStarter;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class ShowScenicViewAction extends AbstractAction {

    public ShowScenicViewAction(
            @Autowired Api api) {
        super(api);
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
      //TODO allow a more easy usage of scenic
        // Show ScenicView Tool when the JVM is started with option -Dscenic.
        // NetBeans: set it on [VM Options] line in [Run] category of project's Properties.
        //if (System.getProperty("scenic") != null) //NOCHECK
        { 
            SbPlatform.runLater(new ScenicViewStarter(getApi().getContext()));
        }
        return ActionStatus.DONE;
    }
}