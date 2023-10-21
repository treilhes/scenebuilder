package com.oracle.javafx.scenebuilder.api.content.gesture;

import com.oracle.javafx.scenebuilder.api.Gesture;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;
import com.oracle.javafx.scenebuilder.core.context.SbContext;

public class GestureFactory<T extends Gesture> extends AbstractFactory<T> {

    public GestureFactory(SbContext sbContext) {
        super(sbContext);
    }

}
