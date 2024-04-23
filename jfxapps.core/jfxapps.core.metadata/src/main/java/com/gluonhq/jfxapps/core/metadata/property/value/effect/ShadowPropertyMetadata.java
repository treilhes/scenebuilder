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
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.paint.ColorPropertyMetadata;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.Shadow;
import javafx.scene.paint.Color;

/**
 *
 */
public class ShadowPropertyMetadata<VC> extends ComplexPropertyMetadata<Shadow, VC> {

    private final EnumerationPropertyMetadata<Void> blurTypeMetadata = new EnumerationPropertyMetadata.Builder<BlurType, Void>(BlurType.class)
            .name(new PropertyName("blurType"))//NOCHECK
            .readWrite(true)
            .defaultValue(BlurType.THREE_PASS_BOX)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final ColorPropertyMetadata<Void> colorMetadata = new ColorPropertyMetadata.Builder<Void>()
            .name(new PropertyName("color"))//NOCHECK
            .readWrite(true)
            .defaultValue(Color.BLACK)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> heightMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("height"))//NOCHECK
            .readWrite(true)
            .defaultValue(21.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EffectPropertyMetadata<Void> inputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .name(new PropertyName("input"))//NOCHECK
            .readWrite(true)
            .defaultValue(null)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> widthMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("width"))//NOCHECK
            .readWrite(true)
            .defaultValue(21.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> radiusMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("radius"))//NOCHECK
            .readWrite(true)
            .defaultValue(10.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    protected ShadowPropertyMetadata(PropertyName name, boolean readWrite,
//            Shadow defaultValue, InspectorPath inspectorPath) {
//        super(name, Shadow.class, readWrite, defaultValue, inspectorPath);
//    }

    protected ShadowPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Shadow, VC> {
        public AbstractBuilder() {
            super();
            valueClass(Shadow.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ShadowPropertyMetadata<VC>, VC> {
        @Override
        public ShadowPropertyMetadata<VC> build() {
            return new ShadowPropertyMetadata<VC>(this);
        }
    }
}
