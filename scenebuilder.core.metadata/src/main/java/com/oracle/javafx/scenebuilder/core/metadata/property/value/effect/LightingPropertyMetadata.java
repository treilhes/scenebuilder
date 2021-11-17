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
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.effect.light.LightPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.Lighting;

/**
 *
 */
public class LightingPropertyMetadata extends ComplexPropertyMetadata<Lighting> {
    
    private static final Lighting LIGHTING_DEFAULT = new Lighting();
    
    private final EffectPropertyMetadata bumpInputMetadata = new EffectPropertyMetadata.Builder()
            .withName(new PropertyName("bumpInput"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getBumpInput())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EffectPropertyMetadata contentInputMetadata = new EffectPropertyMetadata.Builder()
            .withName(new PropertyName("contentInput"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getContentInput())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata diffuseConstantMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("diffuseConstant"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getDiffuseConstant())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final LightPropertyMetadata lightMetadata = new LightPropertyMetadata.Builder()
            .withName(new PropertyName("light"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getLight())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata specularConstantMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("specularConstant"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getSpecularConstant())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata specularExponentMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("specularExponent"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getSpecularExponent())
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata surfaceScaleMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("surfaceScale"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(LIGHTING_DEFAULT.getSurfaceScale())
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected LightingPropertyMetadata(PropertyName name, boolean readWrite, 
            Lighting defaultValue, InspectorPath inspectorPath) {
        super(name, Lighting.class, readWrite, defaultValue, inspectorPath);
    }

    protected LightingPropertyMetadata(AbstractBuilder<?, ?> builder) {
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
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Lighting> {
        public AbstractBuilder() {
            super();
            withValueClass(Lighting.class);
        }
    }
    
    public static final class Builder extends AbstractBuilder<Builder, LightingPropertyMetadata> {
        @Override
        public LightingPropertyMetadata build() {
            return new LightingPropertyMetadata(this);
        }
    }
}
