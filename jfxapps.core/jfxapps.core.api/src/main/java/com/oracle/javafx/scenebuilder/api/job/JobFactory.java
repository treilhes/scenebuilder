package com.oracle.javafx.scenebuilder.api.job;


import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;


public class JobFactory<T extends AbstractJob> extends AbstractFactory<T> {

    public JobFactory(JfxAppContext sbContext) {
        super(sbContext);
    }

}

