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
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.effect.light.LightPropertyMetadata;

import javafx.scene.effect.Lighting;

/**
 *
 */
public class LightingPropertyMetadata<VC> extends ComplexPropertyMetadata<Lighting, VC> {

    private static final Lighting LIGHTING_DEFAULT = new Lighting();

    private final EffectPropertyMetadata<Void> bumpInputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .name(new PropertyName("bumpInput"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getBumpInput())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EffectPropertyMetadata<Void> contentInputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .name(new PropertyName("contentInput"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getContentInput())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> diffuseConstantMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("diffuseConstant"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getDiffuseConstant())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final LightPropertyMetadata<Void> lightMetadata = new LightPropertyMetadata.Builder<Void>()
            .name(new PropertyName("light"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getLight())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> specularConstantMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("specularConstant"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getSpecularConstant())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> specularExponentMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("specularExponent"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getSpecularExponent())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> surfaceScaleMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("surfaceScale"))//NOCHECK
            .readWrite(true)
            .defaultValue(LIGHTING_DEFAULT.getSurfaceScale())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    protected LightingPropertyMetadata(PropertyName name, boolean readWrite,
//            Lighting defaultValue, InspectorPath inspectorPath) {
//        super(name, Lighting.class, readWrite, defaultValue, inspectorPath);
//    }

    protected LightingPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Lighting value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        bumpInputMetadata.setValue(result, value.getBumpInput());
        contentInputMetadata.setValue(result, value.getContentInput());
        diffuseConstantMetadata.setValue(result, value.getDiffuseConstant());
        lightMetadata.setValue(result, value.getLight());
        specularConstantMetadata.setValue(result, value.getSpecularConstant());
        specularExponentMetadata.setValue(result, value.getSpecularExponent());
        surfaceScaleMetadata.setValue(result, value.getSurfaceScale());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Lighting, VC> {
        public AbstractBuilder() {
            super();
            valueClass(Lighting.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, LightingPropertyMetadata<VC>, VC> {
        @Override
        public LightingPropertyMetadata<VC> build() {
            return new LightingPropertyMetadata<VC>(this);
        }
    }
}
