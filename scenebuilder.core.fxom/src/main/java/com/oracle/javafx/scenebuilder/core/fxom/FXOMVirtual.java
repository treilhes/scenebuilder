package com.oracle.javafx.scenebuilder.core.fxom;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;

abstract class FXOMVirtual extends FXOMObject {

    FXOMVirtual(FXOMDocument fxomDocument, GlueElement glueElement, Object sceneGraphObject) {
        super(fxomDocument, glueElement, sceneGraphObject);
    }

    public FXOMVirtual(FXOMDocument fxomDocument, String tagName) {
        super(fxomDocument, tagName);
    }

}
