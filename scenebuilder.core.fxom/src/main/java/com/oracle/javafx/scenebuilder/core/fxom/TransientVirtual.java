package com.oracle.javafx.scenebuilder.core.fxom;

import com.oracle.javafx.scenebuilder.core.fxom.glue.GlueElement;

public abstract class TransientVirtual extends TransientObject  {

    public TransientVirtual(TransientNode parentNode, Class<?> declaredClass, GlueElement glueElement) {
        super(parentNode, declaredClass, glueElement);
    }

    public TransientVirtual(TransientNode parentNode, GlueElement glueElement) {
        super(parentNode, glueElement);
    }

    public TransientVirtual(TransientNode parentNode, String unknownClassName, GlueElement glueElement) {
        super(parentNode, unknownClassName, glueElement);
    }

    @Override
    public abstract FXOMObject makeFxomObject(FXOMDocument fxomDocument);

    public abstract FXOMProperty makeFxomProperty(FXOMDocument document);

}
