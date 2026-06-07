package com.oracle.javafx.scenebuilder.controls.fxom;

import com.treilhes.jfxplace.fxom.model.ext.WeakProperty;

import javafx.scene.control.Label;

public class LabelForWeakProperty implements WeakProperty {

    @Override
    public String getPropertyName() {
        return "labelFor";
    }

    @Override
    public Class<?> getPropertyOwnerType() {
        return Label.class;
    }

}
