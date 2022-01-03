package com.oracle.javafx.scenebuilder.core.fxom.control;

import javafx.scene.control.Label;

public class NullReference extends Label {

    public NullReference() {
        super("null reference");
    }

    public NullReference(NullReference copy) {
        this();
    }

}
