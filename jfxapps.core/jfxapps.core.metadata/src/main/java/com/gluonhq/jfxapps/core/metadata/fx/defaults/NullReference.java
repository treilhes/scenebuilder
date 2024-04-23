package com.gluonhq.jfxapps.core.metadata.fx.defaults;

import javafx.scene.control.Label;

public class NullReference extends Label {

    public NullReference() {
        super("null reference");
    }

    public NullReference(NullReference copy) {
        this();
    }

}
