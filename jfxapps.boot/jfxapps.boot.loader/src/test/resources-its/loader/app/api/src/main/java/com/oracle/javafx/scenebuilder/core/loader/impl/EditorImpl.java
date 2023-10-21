package com.oracle.javafx.scenebuilder.core.loader.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.oracle.javafx.scenebuilder.core.loader.api.Editor;

public class EditorImpl implements Editor {

    private ApplicationContext context;

    public EditorImpl(ApplicationContext context) {
        super();
        this.context = new GenericApplicationContext(context);
        System.out.println("EditorImpl > ApplicationContext " + context);
    }

    @Override
    public List<Class<?>> explicitClassToRegister() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UUID id() {
        // TODO Auto-generated method stub
        return UUID.randomUUID();
    }

}