package com.oracle.javafx.scenebuilder.controls.fxom;

import com.gluonhq.jfxapps.core.fxom.ext.WeakProperty;

import javafx.scene.control.Accordion;

public class ExpandedPaneWeakProperty implements WeakProperty {

    @Override
    public String getPropertyName() {
        return "expandedPane";
    }

    @Override
    public Class<?> getPropertyOwnerType() {
        return Accordion.class;
    }

}
