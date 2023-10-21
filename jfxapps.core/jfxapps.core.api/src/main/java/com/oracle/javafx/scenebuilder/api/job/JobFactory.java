package com.oracle.javafx.scenebuilder.api.job;


import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;
import com.oracle.javafx.scenebuilder.core.context.SbContext;


public class JobFactory<T extends AbstractJob> extends AbstractFactory<T> {

    public JobFactory(SbContext sbContext) {
        super(sbContext);
    }

}

