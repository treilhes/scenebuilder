package com.oracle.javafx.scenebuilder.api.job;


import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;


public class JobFactory<T extends AbstractJob> extends AbstractFactory<T> {

    public JobFactory(SceneBuilderBeanFactory sbContext) {
        super(sbContext);
    }

}

