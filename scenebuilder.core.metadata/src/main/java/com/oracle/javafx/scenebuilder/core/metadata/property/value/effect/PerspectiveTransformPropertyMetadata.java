/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates.
 * All rights reserved. Use is subject to license terms.
 *
 * This file is available and licensed under the following license:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  - Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *  - Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the distribution.
 *  - Neither the name of Oracle Corporation and Gluon nor the names of its
 *    contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.oracle.javafx.scenebuilder.core.metadata.property.value.effect;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.PerspectiveTransform;

/**
 *
 */
public class PerspectiveTransformPropertyMetadata extends ComplexPropertyMetadata<PerspectiveTransform> {
    
    private final EffectPropertyMetadata inputMetadata = new EffectPropertyMetadata.Builder()
            .withName(new PropertyName("input"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata llxMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("llx"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata llyMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("lly"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata lrxMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("lrx"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata lryMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("lry"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata ulxMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("ulx"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata ulyMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("uly"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata urxMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("urx"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata uryMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("ury"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

//    protected PerspectiveTransformPropertyMetadata(PropertyName name, boolean readWrite, 
//            PerspectiveTransform defaultValue, InspectorPath inspectorPath) {
//        super(name, PerspectiveTransform.class, readWrite, defaultValue, inspectorPath);
//    }

    protected PerspectiveTransformPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }
    
    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(PerspectiveTransform value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        inputMetadata.setValue(result, value.getInput());
        llxMetadata.setValue(result, value.getLlx());
        llyMetadata.setValue(result, value.getLly());
        lrxMetadata.setValue(result, value.getLrx());
        lryMetadata.setValue(result, value.getLry());
        ulxMetadata.setValue(result, value.getUlx());
        ulyMetadata.setValue(result, value.getUly());
        urxMetadata.setValue(result, value.getUrx());
        uryMetadata.setValue(result, value.getUry());

        return result;
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, PerspectiveTransform> {
        public AbstractBuilder() {
            super();
            withValueClass(PerspectiveTransform.class);
        }
    }
    
    public static final class Builder extends AbstractBuilder<Builder, PerspectiveTransformPropertyMetadata> {
        @Override
        public PerspectiveTransformPropertyMetadata build() {
            return new PerspectiveTransformPropertyMetadata(this);
        }
    }
}
