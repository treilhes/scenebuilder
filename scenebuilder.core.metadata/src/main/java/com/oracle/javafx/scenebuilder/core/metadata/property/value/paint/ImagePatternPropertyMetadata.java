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
import com.oracle.javafx.scenebuilder.core.fxom.util.DesignImage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.paint.ImagePattern;

/**
 *
 */
public class ImagePatternPropertyMetadata extends ComplexPropertyMetadata<ImagePattern> {

    private final ImagePropertyMetadata imageMetadata = new ImagePropertyMetadata.Builder()
            .withName(new PropertyName("image")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata xMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("x")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata yMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("y")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata widthMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("width")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata heightMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("height")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata proportionalMetadata = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("proportional")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(true)
            .withInspectorPath(InspectorPath.UNUSED).build();

//    public ImagePatternPropertyMetadata(PropertyName name, boolean readWrite, 
//            ImagePattern defaultValue, InspectorPath inspectorPath) {
//        super(name, ImagePattern.class, readWrite, defaultValue, inspectorPath);
//    }
    
    public ImagePatternPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(ImagePattern value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        imageMetadata.setValue(result, new DesignImage(value.getImage()));
        xMetadata.setValue(result, value.getX());
        yMetadata.setValue(result, value.getY());
        widthMetadata.setValue(result, value.getWidth());
        heightMetadata.setValue(result, value.getHeight());
        proportionalMetadata.setValue(result, value.isProportional());
        
        return result;
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, ImagePattern> {
        public AbstractBuilder() {
            super();
            withValueClass(ImagePattern.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, ImagePatternPropertyMetadata> {
        @Override
        public ImagePatternPropertyMetadata build() {
            return new ImagePatternPropertyMetadata(this);
        }
    }
}
