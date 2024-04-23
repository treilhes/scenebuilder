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
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;

import javafx.geometry.Rectangle2D;

/**
 *
 */
public class Rectangle2DPropertyMetadata<VC> extends ComplexPropertyMetadata<Rectangle2D, VC> {

    private final CoordinateDoublePropertyMetadata<Void> minXMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("minX")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> minYMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("minY")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> widthMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("width")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> heightMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("height")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();


//    public Rectangle2DPropertyMetadata(PropertyName name, boolean readWrite, Rectangle2D defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, Rectangle2D.class, readWrite, defaultValue, inspectorPath);
//    }

    protected Rectangle2DPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Rectangle2D value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        minXMetadata.setValue(result, value.getMinX());
        minYMetadata.setValue(result, value.getMinY());
        widthMetadata.setValue(result, value.getWidth());
        heightMetadata.setValue(result, value.getHeight());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Rectangle2D, VC> {
        public AbstractBuilder() {
            super();
            valueClass(Rectangle2D.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, Rectangle2DPropertyMetadata<VC>, VC> {
        @Override
        public Rectangle2DPropertyMetadata<VC> build() {
            return new Rectangle2DPropertyMetadata<VC>(this);
        }
    }
}