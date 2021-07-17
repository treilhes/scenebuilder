package com.oracle.javafx.scenebuilder.api.action;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ActionFactory {

    private final ApplicationContext context;

    public ActionFactory(@Autowired ApplicationContext context) {
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
