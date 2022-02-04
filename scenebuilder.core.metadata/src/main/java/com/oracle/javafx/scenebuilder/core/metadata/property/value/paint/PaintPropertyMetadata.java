/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.paint;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;

import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;

/**
 *
 */
public class PaintPropertyMetadata extends ComplexPropertyMetadata<Paint> {

    private final ColorPropertyMetadata colorMetadata;
    private final ImagePatternPropertyMetadata imagePatternMetadata;
    private final LinearGradientPropertyMetadata linearGradientMetadata;
    private final RadialGradientPropertyMetadata radialGradientMetadata;

//    protected PaintPropertyMetadata(PropertyName name, boolean readWrite,
//            Paint defaultValue, InspectorPath inspectorPath) {
//        super(name, Paint.class, readWrite, defaultValue, inspectorPath);
//        
//        colorMetadata = fillBuilder(this, new ColorPropertyMetadata.Builder()).withDefaultValue(null).build();
//        imagePatternMetadata = fillBuilder(this, new ImagePatternPropertyMetadata.Builder()).withDefaultValue(null).build();
//        linearGradientMetadata = fillBuilder(this, new LinearGradientPropertyMetadata.Builder()).withDefaultValue(null).build();
//        radialGradientMetadata = fillBuilder(this, new RadialGradientPropertyMetadata.Builder()).withDefaultValue(null).build();
//    }

    protected PaintPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
        
        colorMetadata = fillBuilder(this, new ColorPropertyMetadata.Builder()).withDefaultValue(null).build();
        imagePatternMetadata = fillBuilder(this, new ImagePatternPropertyMetadata.Builder()).withDefaultValue(null).build();
        linearGradientMetadata = fillBuilder(this, new LinearGradientPropertyMetadata.Builder()).withDefaultValue(null).build();
        radialGradientMetadata = fillBuilder(this, new RadialGradientPropertyMetadata.Builder()).withDefaultValue(null).build();
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

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Paint> {
        public AbstractBuilder() {
            super();
            withValueClass(Paint.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, PaintPropertyMetadata> {
        @Override
        public PaintPropertyMetadata build() {
            return new PaintPropertyMetadata(this);
        }
    }
}
