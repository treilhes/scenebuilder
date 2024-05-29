package com.gluonhq.jfxapps.core.api.application.javafx.internal;

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