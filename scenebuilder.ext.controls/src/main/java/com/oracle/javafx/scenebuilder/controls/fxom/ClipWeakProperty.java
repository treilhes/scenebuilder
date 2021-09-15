package com.oracle.javafx.scenebuilder.controls.fxom;

import com.oracle.javafx.scenebuilder.core.fxom.ext.WeakProperty;

import javafx.scene.Node;

public class ClipWeakProperty implements WeakProperty {

    @Override
    public String getPropertyName() {
        return "clip";
    }

    @Override
    public Class<?> getPropertyOwnerType() {
        return Node.class;
    }

}
