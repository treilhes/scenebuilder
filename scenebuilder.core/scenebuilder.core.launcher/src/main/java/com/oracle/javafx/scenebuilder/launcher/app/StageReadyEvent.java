package com.oracle.javafx.scenebuilder.launcher.app;

import org.springframework.context.ApplicationEvent;

import javafx.stage.Stage;

public class StageReadyEvent extends ApplicationEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public Stage getStage() {
        return Stage.class.cast(getSource());
    }

    public StageReadyEvent(Object source) {
        super(source);
    }
}