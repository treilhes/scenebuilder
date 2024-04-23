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
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.OpacityDoublePropertyMetadata;

import javafx.scene.effect.Bloom;

/**
 *
 */
public class BloomPropertyMetadata<VC> extends ComplexPropertyMetadata<Bloom, VC> {

    private final EffectPropertyMetadata<Void> inputMetadata = new EffectPropertyMetadata.Builder<Void>()
            .withName(new PropertyName("input")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final OpacityDoublePropertyMetadata<Void> thresholdMetadata = new OpacityDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("threshold")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.3)
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

//    protected BloomPropertyMetadata(PropertyName name, boolean readWrite,
//            Bloom defaultValue, InspectorPath inspectorPath) {
//        super(name, Bloom.class, readWrite, defaultValue, inspectorPath);
//    }

    protected BloomPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Bloom value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        inputMetadata.setValue(result, value.getInput());
        thresholdMetadata.setValue(result, value.getThreshold());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Bloom, VC> {
        public AbstractBuilder() {
            super();
            withValueClass(Bloom.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, BloomPropertyMetadata<VC>, VC> {
        @Override
        public BloomPropertyMetadata<VC> build() {
            return new BloomPropertyMetadata<VC>(this);
        }
    }
}
