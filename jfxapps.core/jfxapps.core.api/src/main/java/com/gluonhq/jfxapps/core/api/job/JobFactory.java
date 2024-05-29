package com.gluonhq.jfxapps.core.api.job;


import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;


public class JobFactory<T extends AbstractJob> extends AbstractFactory<T> {

    public JobFactory(JfxAppContext sbContext) {
        super(sbContext);
    }

}

