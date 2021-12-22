package com.oracle.javafx.scenebuilder.api.action;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
public class ActionFactory {

    private final SceneBuilderBeanFactory context;

    public ActionFactory(@Autowired SceneBuilderBeanFactory context) {
        super();
        this.context = context;
    }

    public <T extends Action> Action create(Class<T> actionClass) {
        return create(actionClass, null);
    }

    public <T extends Action> Action create(Class<T> actionClass, Consumer<T> init) {
        T actionInstance = context.getBean(actionClass);
        if (init != null) {
            init.accept(actionInstance);
        }
        return actionInstance.extend();
    }
}
