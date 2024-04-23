package com.gluonhq.jfxapps.core.fxom;

import com.gluonhq.jfxapps.core.fxom.glue.GlueElement;

public abstract class TransientVirtual extends TransientObject  {

    private long virtualIndex;

    public TransientVirtual(TransientNode parentNode, Class<?> declaredClass, GlueElement glueElement, long virtualIndex) {
        super(parentNode, declaredClass, glueElement);
        this.virtualIndex = virtualIndex;
    }

    public TransientVirtual(TransientNode parentNode, GlueElement glueElement, long virtualIndex) {
        super(parentNode, glueElement);
        this.virtualIndex = virtualIndex;
    }

    public TransientVirtual(TransientNode parentNode, String unknownClassName, GlueElement glueElement, long virtualIndex) {
        super(parentNode, unknownClassName, glueElement);
        this.virtualIndex = virtualIndex;
    }

    @Override
    public abstract FXOMObject makeFxomObject(FXOMDocument fxomDocument);

    public abstract FXOMProperty makeFxomProperty(FXOMDocument document);

    public long getVirtualIndex() {
        return virtualIndex;
    }


}
