package com.oracle.javafx.scenebuilder.api.content.gesture;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.Gesture;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;

public class GestureFactory<T extends Gesture> extends AbstractFactory<T> {

    public GestureFactory(JfxAppContext sbContext) {
        super(sbContext);
    }

}
