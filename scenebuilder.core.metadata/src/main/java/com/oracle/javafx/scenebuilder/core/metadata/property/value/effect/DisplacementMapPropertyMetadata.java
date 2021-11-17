/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.DisplacementMap;

/**
 *
 */
public class DisplacementMapPropertyMetadata extends ComplexPropertyMetadata<DisplacementMap> {
    
    private final DisplacementMap DEFAULT = new DisplacementMap();
    
    private final EffectPropertyMetadata inputMetadata = new EffectPropertyMetadata.Builder()
            .withName(new PropertyName("input")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata offsetXMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("offsetX")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getOffsetX())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata offsetYMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("offsetY")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getOffsetY())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata scaleXMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("scaleX")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getScaleX())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata scaleYMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("scaleY")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getScaleY())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata wrapMetadata = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("wrap")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.isWrap())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final FloatMapPropertyMetadata mapDataMetadata = new FloatMapPropertyMetadata.Builder()
            .withName(new PropertyName("mapData")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getMapData())
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected DisplacementMapPropertyMetadata(PropertyName name, boolean readWrite, 
            DisplacementMap defaultValue, InspectorPath inspectorPath) {
        super(name, DisplacementMap.class, readWrite, defaultValue, inspectorPath);
    }

    protected DisplacementMapPropertyMetadata(AbstractBuilder<?, ?> builder) {
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
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, DisplacementMap> {
        public AbstractBuilder() {
            super();
            withValueClass(DisplacementMap.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, DisplacementMapPropertyMetadata> {
        @Override
        public DisplacementMapPropertyMetadata build() {
            return new DisplacementMapPropertyMetadata(this);
        }
    }
}
