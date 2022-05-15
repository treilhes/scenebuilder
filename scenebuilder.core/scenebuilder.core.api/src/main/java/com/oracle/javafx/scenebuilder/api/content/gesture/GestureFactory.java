package com.oracle.javafx.scenebuilder.api.content.gesture;

import com.oracle.javafx.scenebuilder.api.Gesture;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;

public class GestureFactory<T extends Gesture> extends AbstractFactory<T> {

    public GestureFactory(SceneBuilderBeanFactory sbContext) {
        super(sbContext);
    }

}
