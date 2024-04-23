/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.metadata.property.value;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;

import javafx.geometry.Insets;

/**
 *
 *
 */
public class InsetsPropertyMetadata<VC> extends ComplexPropertyMetadata<Insets, VC> {

    private final CoordinateDoublePropertyMetadata<Void> topMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("top"))
            .withReadWrite(true)
            .withDefaultValue(Insets.EMPTY.getTop())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();
    private final CoordinateDoublePropertyMetadata<Void> bottomMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("bottom"))
            .withReadWrite(true)
            .withDefaultValue(Insets.EMPTY.getTop())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();
    private final CoordinateDoublePropertyMetadata<Void> leftMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("left"))
            .withReadWrite(true)
            .withDefaultValue(Insets.EMPTY.getTop())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();
    private final CoordinateDoublePropertyMetadata<Void> rightMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("right"))
            .withReadWrite(true)
            .withDefaultValue(Insets.EMPTY.getTop())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();


//    public InsetsPropertyMetadata(PropertyName name, boolean readWrite,
//            Insets defaultValue, InspectorPath inspectorPath) {
//        super(name, Insets.class, readWrite, defaultValue, inspectorPath);
//    }

    protected InsetsPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexValuePropertyMetadata
     */
    @Override
    public FXOMInstance makeFxomInstanceFromValue(Insets value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, getValueClass());

        topMetadata.setValue(result, value.getTop());
        bottomMetadata.setValue(result, value.getBottom());
        leftMetadata.setValue(result, value.getLeft());
        rightMetadata.setValue(result, value.getRight());
        result.setSceneGraphObject(value);
        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Insets, VC> {
        public AbstractBuilder() {
            super();
            withValueClass(Insets.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, InsetsPropertyMetadata<VC>, VC> {
        @Override
        public InsetsPropertyMetadata<VC> build() {
            return new InsetsPropertyMetadata<VC>(this);
        }
    }
}
