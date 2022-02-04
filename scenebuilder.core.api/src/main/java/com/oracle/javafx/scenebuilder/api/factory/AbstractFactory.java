package com.oracle.javafx.scenebuilder.api.factory;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;

public abstract class AbstractFactory<T> {

    private static Logger logger = LoggerFactory.getLogger(AbstractFactory.class);

    private final SceneBuilderBeanFactory sbContext;

    public AbstractFactory(SceneBuilderBeanFactory sbContext) {
        super();
        this.sbContext = sbContext;
    }

    protected T create(Class<T> tClass, Consumer<T> setup) {

        logger.debug("Creation of {}", tClass.getName());

        T obj = sbContext.getBean(tClass);
        if (setup != null) {
            setup.accept(obj);
        }
        return obj;
    }
}