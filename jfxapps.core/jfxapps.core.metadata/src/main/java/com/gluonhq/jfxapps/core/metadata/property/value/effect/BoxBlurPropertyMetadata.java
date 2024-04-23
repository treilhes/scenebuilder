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
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.EffectSizeDoublePropertyMetadata;

import javafx.scene.effect.BoxBlur;

/**
 *
 */
public class BoxBlurPropertyMetadata<VC> extends ComplexPropertyMetadata<BoxBlur, VC> {

    private final EffectPropertyMetadata<Void> inputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .name(new PropertyName("input")) //NOCHECK
            .readWrite(true)
            .defaultValue(null)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EffectSizeDoublePropertyMetadata<Void> heightMetadata = new EffectSizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("height")) //NOCHECK
            .readWrite(true)
            .defaultValue(5.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EffectSizeDoublePropertyMetadata<Void> widthMetadata = new EffectSizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("width")) //NOCHECK
            .readWrite(true)
            .defaultValue(5.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final IntegerPropertyMetadata<Void> iterationsMetadata = new IntegerPropertyMetadata.Builder<Void>()
            .name(new PropertyName("iterations")) //NOCHECK
            .readWrite(true)
            .defaultValue(1)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    protected BoxBlurPropertyMetadata(PropertyName name, boolean readWrite,
//            BoxBlur defaultValue, InspectorPath inspectorPath) {
//        super(name, BoxBlur.class, readWrite, defaultValue, inspectorPath);
//    }

    protected BoxBlurPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(BoxBlur value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        inputMetadata.setValue(result, value.getInput());
        heightMetadata.setValue(result, value.getHeight());
        widthMetadata.setValue(result, value.getWidth());
        iterationsMetadata.setValue(result, value.getIterations());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, BoxBlur, VC> {
        public AbstractBuilder() {
            super();
            valueClass(BoxBlur.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, BoxBlurPropertyMetadata<VC>, VC> {
        @Override
        public BoxBlurPropertyMetadata<VC> build() {
            return new BoxBlurPropertyMetadata<VC>(this);
        }
    }
}
