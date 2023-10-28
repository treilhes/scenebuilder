package com.oracle.javafx.scenebuilder.api.content.gesture;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.Gesture;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;

public class GestureFactory<T extends Gesture> extends AbstractFactory<T> {

    public GestureFactory(SbContext sbContext) {
        super(sbContext);
    }

}
