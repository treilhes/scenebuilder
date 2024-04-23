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

package com.gluonhq.jfxapps.core.metadata.property.value.effect;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.BooleanPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;

import javafx.scene.effect.DisplacementMap;

/**
 *
 */
public class DisplacementMapPropertyMetadata<VC> extends ComplexPropertyMetadata<DisplacementMap, VC> {

    private final DisplacementMap DEFAULT = new DisplacementMap();

    private final EffectPropertyMetadata<Void> inputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .name(new PropertyName("input")) //NOCHECK
            .readWrite(true)
            .defaultValue(null)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> offsetXMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("offsetX")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getOffsetX())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> offsetYMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("offsetY")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getOffsetY())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> scaleXMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("scaleX")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getScaleX())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> scaleYMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("scaleY")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getScaleY())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final BooleanPropertyMetadata<Void> wrapMetadata = new BooleanPropertyMetadata.Builder<Void>()
            .name(new PropertyName("wrap")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.isWrap())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final FloatMapPropertyMetadata<Void> mapDataMetadata = new FloatMapPropertyMetadata.Builder<Void>()
            .name(new PropertyName("mapData")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getMapData())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    protected DisplacementMapPropertyMetadata(PropertyName name, boolean readWrite,
//            DisplacementMap defaultValue, InspectorPath inspectorPath) {
//        super(name, DisplacementMap.class, readWrite, defaultValue, inspectorPath);
//    }

    protected DisplacementMapPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }
    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(DisplacementMap value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        inputMetadata.setValue(result, value.getInput());
        offsetXMetadata.setValue(result, value.getOffsetX());
        offsetYMetadata.setValue(result, value.getOffsetY());
        scaleXMetadata.setValue(result, value.getScaleX());
        scaleYMetadata.setValue(result, value.getScaleY());
        wrapMetadata.setValue(result, value.isWrap());
        mapDataMetadata.setValue(result, value.getMapData());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, DisplacementMap, VC> {
        public AbstractBuilder() {
            super();
            valueClass(DisplacementMap.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, DisplacementMapPropertyMetadata<VC>, VC> {
        @Override
        public DisplacementMapPropertyMetadata<VC> build() {
            return new DisplacementMapPropertyMetadata<VC>(this);
        }
    }
}
