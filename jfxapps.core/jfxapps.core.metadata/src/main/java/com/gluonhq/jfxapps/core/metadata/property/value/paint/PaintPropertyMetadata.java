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
package com.gluonhq.jfxapps.core.metadata.property.value.paint;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

/**
 *
 */
public class PaintPropertyMetadata<VC> extends ComplexPropertyMetadata<Paint, VC> {

    private final ColorPropertyMetadata<Void> colorMetadata;
    private final ImagePatternPropertyMetadata<Void> imagePatternMetadata;
    private final LinearGradientPropertyMetadata<Void> linearGradientMetadata;
    private final RadialGradientPropertyMetadata<Void> radialGradientMetadata;

//    protected PaintPropertyMetadata(PropertyName name, boolean readWrite,
//            Paint defaultValue, InspectorPath inspectorPath) {
//        super(name, Paint.class, readWrite, defaultValue, inspectorPath);
//
//        colorMetadata = fillBuilder(this, new ColorPropertyMetadata.Builder()).defaultValue(null).build();
//        imagePatternMetadata = fillBuilder(this, new ImagePatternPropertyMetadata.Builder()).defaultValue(null).build();
//        linearGradientMetadata = fillBuilder(this, new LinearGradientPropertyMetadata.Builder()).defaultValue(null).build();
//        radialGradientMetadata = fillBuilder(this, new RadialGradientPropertyMetadata.Builder()).defaultValue(null).build();
//    }

    protected PaintPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);

        colorMetadata = new ColorPropertyMetadata.Builder<Void>()
                .name(this.getName())
                .readWrite(this.isReadWrite())
                .defaultValue(null).build();
        imagePatternMetadata = new ImagePatternPropertyMetadata.Builder<Void>()
                .name(this.getName())
                .readWrite(this.isReadWrite())
                .defaultValue(null).build();
        linearGradientMetadata = new LinearGradientPropertyMetadata.Builder<Void>()
                .name(this.getName())
                .readWrite(this.isReadWrite())
                .defaultValue(null).build();
        radialGradientMetadata = new RadialGradientPropertyMetadata.Builder<Void>()
                .name(this.getName())
                .readWrite(this.isReadWrite())
                .defaultValue(null).build();
    }

    /*
     * ComplexPropertyMetadata
     */


    @Override
    public Paint makeValueFromString(String string) {
        return colorMetadata.makeValueFromString(string);
    }

    @Override
    public boolean canMakeStringFromValue(Paint value) {
        return value instanceof Color;
    }

    @Override
    public String makeStringFromValue(Paint value) {
        assert value instanceof Color; // Because canMakeStringFromValue() is true
        return colorMetadata.makeStringFromValue((Color) value);
    }

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Paint value, FXOMDocument fxomDocument) {
        final FXOMInstance result;

        if (value instanceof Color) {
            result = colorMetadata.makeFxomInstanceFromValue((Color) value, fxomDocument);
        } else if (value instanceof ImagePattern) {
            result = imagePatternMetadata.makeFxomInstanceFromValue((ImagePattern) value, fxomDocument);
        } else if (value instanceof LinearGradient) {
            result = linearGradientMetadata.makeFxomInstanceFromValue((LinearGradient) value, fxomDocument);
        } else if (value instanceof RadialGradient) {
            result = radialGradientMetadata.makeFxomInstanceFromValue((RadialGradient) value, fxomDocument);
        } else {
            assert false;
            result = colorMetadata.makeFxomInstanceFromValue(Color.BLACK, fxomDocument);
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Paint, VC> {
        public AbstractBuilder() {
            super();
            valueClass(Paint.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, PaintPropertyMetadata<VC>, VC> {
        @Override
        public PaintPropertyMetadata<VC> build() {
            return new PaintPropertyMetadata<VC>(this);
        }
    }
}