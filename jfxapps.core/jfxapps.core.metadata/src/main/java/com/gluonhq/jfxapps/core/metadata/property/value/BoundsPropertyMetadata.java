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

import javafx.geometry.Bounds;

/**
 *
 *
 */
public class BoundsPropertyMetadata<VC> extends ComplexPropertyMetadata<Bounds, VC> {

    private final CoordinateDoublePropertyMetadata<Void> minXMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
                .name(new PropertyName("minx"))
                .readWrite(true)
                .defaultValue(0.0)
                //.inspectorPath(InspectorPath.UNUSED)
                .build();

    private final CoordinateDoublePropertyMetadata<Void> minYMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
                .name(new PropertyName("minY"))
                .readWrite(true)
                .defaultValue(0.0)
                //.inspectorPath(InspectorPath.UNUSED)
                .build();

    private final CoordinateDoublePropertyMetadata<Void> minZMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
                .name(new PropertyName("minZ"))
                .readWrite(true)
                .defaultValue(0.0)
                //.inspectorPath(InspectorPath.UNUSED)
                .build();

    private final SizeDoublePropertyMetadata<Void> widthMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
                .name(new PropertyName("width"))
                .readWrite(true)
                .defaultValue(0.0)
                //.inspectorPath(InspectorPath.UNUSED)
                .build();

    private final SizeDoublePropertyMetadata<Void> heightMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
                .name(new PropertyName("height"))
                .readWrite(true)
                .defaultValue(0.0)
                //.inspectorPath(InspectorPath.UNUSED)
                .build();

    private final SizeDoublePropertyMetadata<Void> depthMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("depth"))
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();



//    protected BoundsPropertyMetadata(PropertyName name, boolean readWrite,
//            Bounds defaultValue, InspectorPath inspectorPath) {
//        super(name, Bounds.class, readWrite, defaultValue, inspectorPath);
//    }

    protected BoundsPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Bounds value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        minXMetadata.setValue(result, value.getMinX());
        minYMetadata.setValue(result, value.getMinY());
        minZMetadata.setValue(result, value.getMinZ());
        widthMetadata.setValue(result, value.getWidth());
        heightMetadata.setValue(result, value.getHeight());
        depthMetadata.setValue(result, value.getDepth());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Bounds, VC> {

        public AbstractBuilder() {
            super();
            valueClass(Bounds.class);
        }

    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, BoundsPropertyMetadata<VC>, VC> {
        @Override
        public BoundsPropertyMetadata<VC> build() {
            return new BoundsPropertyMetadata<VC>(this);
        }
    }
}
