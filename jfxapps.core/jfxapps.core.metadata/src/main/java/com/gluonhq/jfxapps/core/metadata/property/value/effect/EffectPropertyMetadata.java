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
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;

import javafx.scene.effect.Blend;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.effect.DisplacementMap;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.effect.ImageInput;
import javafx.scene.effect.InnerShadow;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.MotionBlur;
import javafx.scene.effect.PerspectiveTransform;
import javafx.scene.effect.Reflection;
import javafx.scene.effect.SepiaTone;
import javafx.scene.effect.Shadow;

/**
 *
 *
 */
//TODO update this class to handle custom effects provided by extension if it is possible
public class EffectPropertyMetadata<VC> extends ComplexPropertyMetadata<Effect, VC> {

//    protected EffectPropertyMetadata(PropertyName name, boolean readWrite,
//            Effect defaultValue, InspectorPath inspectorPath) {
//        super(name, Effect.class, readWrite, defaultValue, inspectorPath);
//    }

    protected EffectPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @SuppressWarnings("unchecked") // function makeFxomInstanceFromValue already check the value type
    private <U, V extends ComplexPropertyMetadata<U, Void>, T extends ComplexPropertyMetadata.AbstractBuilder<T, V, U, Void>> FXOMInstance makeFxomInstanceFromBuilderAndValue(
            T builder, Effect value, FXOMDocument fxomDocument) {
        return builder
                .name(this.getName())
                .readWrite(this.isReadWrite())
                .defaultValue(null)
                .build()
                .makeFxomInstanceFromValue((U) value, fxomDocument);
    }

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Effect value, FXOMDocument fxomDocument) {
        final FXOMInstance result;

        if (value instanceof Blend) {
            result = makeFxomInstanceFromBuilderAndValue(new BlendPropertyMetadata.Builder<Void>(), (Blend) value, fxomDocument);
        } else if (value instanceof Bloom) {
            result = makeFxomInstanceFromBuilderAndValue(new BloomPropertyMetadata.Builder<Void>(), (Bloom) value, fxomDocument);
        } else if (value instanceof BoxBlur) {
            result = makeFxomInstanceFromBuilderAndValue(new BoxBlurPropertyMetadata.Builder<Void>(), (BoxBlur) value, fxomDocument);
        } else if (value instanceof ColorAdjust) {
            result = makeFxomInstanceFromBuilderAndValue(new ColorAdjustPropertyMetadata.Builder<Void>(), (ColorAdjust) value, fxomDocument);
        } else if (value instanceof ColorInput) {
            result = makeFxomInstanceFromBuilderAndValue(new ColorInputPropertyMetadata.Builder<Void>(), (ColorInput) value, fxomDocument);
        } else if (value instanceof DisplacementMap) {
            result = makeFxomInstanceFromBuilderAndValue(new DisplacementMapPropertyMetadata.Builder<Void>(), (DisplacementMap) value, fxomDocument);
        } else if (value instanceof DropShadow) {
            result = makeFxomInstanceFromBuilderAndValue(new DropShadowPropertyMetadata.Builder<Void>(), (DropShadow) value, fxomDocument);
        } else if (value instanceof GaussianBlur) {
            result = makeFxomInstanceFromBuilderAndValue(new GaussianBlurPropertyMetadata.Builder<Void>(), (GaussianBlur) value, fxomDocument);
        } else if (value instanceof Glow) {
            result = makeFxomInstanceFromBuilderAndValue(new GlowPropertyMetadata.Builder<Void>(), (Glow) value, fxomDocument);
        } else if (value instanceof ImageInput) {
            result = makeFxomInstanceFromBuilderAndValue(new ImageInputPropertyMetadata.Builder<Void>(), (ImageInput) value, fxomDocument);
        } else if (value instanceof InnerShadow) {
            result = makeFxomInstanceFromBuilderAndValue(new InnerShadowPropertyMetadata.Builder<Void>(), (InnerShadow) value, fxomDocument);
        } else if (value instanceof Lighting) {
            result = makeFxomInstanceFromBuilderAndValue(new LightingPropertyMetadata.Builder<Void>(), (Lighting) value, fxomDocument);
        } else if (value instanceof MotionBlur) {
            result = makeFxomInstanceFromBuilderAndValue(new MotionBlurPropertyMetadata.Builder<Void>(), (MotionBlur) value, fxomDocument);
        } else if (value instanceof PerspectiveTransform) {
            result = makeFxomInstanceFromBuilderAndValue(new PerspectiveTransformPropertyMetadata.Builder<Void>(), (PerspectiveTransform) value, fxomDocument);
        } else if (value instanceof Reflection) {
            result = makeFxomInstanceFromBuilderAndValue(new ReflectionPropertyMetadata.Builder<Void>(), (Reflection) value, fxomDocument);
        } else if (value instanceof SepiaTone) {
            result = makeFxomInstanceFromBuilderAndValue(new SepiaTonePropertyMetadata.Builder<Void>(), (SepiaTone) value, fxomDocument);
        } else if (value instanceof Shadow) {
            result = makeFxomInstanceFromBuilderAndValue(new ShadowPropertyMetadata.Builder<Void>(), (Shadow) value, fxomDocument);
        } else {
            assert false : "unexpected effect class = " + value.getClass().getSimpleName(); //NOCHECK
            result = null;
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Effect, VC> {

        public AbstractBuilder() {
            super();
            valueClass(Effect.class);
        }

    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, EffectPropertyMetadata<VC>, VC> {
        @Override
        public EffectPropertyMetadata<VC> build() {
            return new EffectPropertyMetadata<VC>(this);
        }
    }
}
