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
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;

/**
 *
 */
public class ShadowPropertyMetadata extends ComplexPropertyMetadata<Shadow> {
    
    private final EnumerationPropertyMetadata blurTypeMetadata = new EnumerationPropertyMetadata.Builder<>(BlurType.class)
            .withName(new PropertyName("blurType"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(BlurType.THREE_PASS_BOX)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final ColorPropertyMetadata colorMetadata = new ColorPropertyMetadata.Builder()
            .withName(new PropertyName("color"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(Color.BLACK)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata heightMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("height"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(21.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EffectPropertyMetadata inputMetadata = new EffectPropertyMetadata.Builder()
            .withName(new PropertyName("input"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata widthMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("width"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(21.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata radiusMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("radius"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(10.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected ShadowPropertyMetadata(PropertyName name, boolean readWrite, 
            Shadow defaultValue, InspectorPath inspectorPath) {
        super(name, Shadow.class, readWrite, defaultValue, inspectorPath);
    }
    
    protected ShadowPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(Shadow value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        blurTypeMetadata.setValue(result, value.getBlurType().toString());
        colorMetadata.setValue(result, value.getColor());
        heightMetadata.setValue(result, value.getHeight());
        inputMetadata.setValue(result, value.getInput());
        radiusMetadata.setValue(result, value.getRadius());
        widthMetadata.setValue(result, value.getWidth());

        return result;
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Shadow> {
        public AbstractBuilder() {
            super();
            withValueClass(Shadow.class);
        }
    }
    
    public static final class Builder extends AbstractBuilder<Builder, ShadowPropertyMetadata> {
        @Override
        public ShadowPropertyMetadata build() {
            return new ShadowPropertyMetadata(this);
        }
    }
}
