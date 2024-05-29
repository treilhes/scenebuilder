package com.gluonhq.jfxapps.core.api.content.gesture;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.Gesture;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;

public class GestureFactory<T extends Gesture> extends AbstractFactory<T> {

    public GestureFactory(JfxAppContext sbContext) {
        super(sbContext);
    }

}
