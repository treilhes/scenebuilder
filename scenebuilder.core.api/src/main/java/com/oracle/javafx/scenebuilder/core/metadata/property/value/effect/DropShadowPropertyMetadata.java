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
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.OpacityDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 *
 */
public class DropShadowPropertyMetadata extends ComplexPropertyMetadata<DropShadow> {
    
    private final EnumerationPropertyMetadata blurTypeMetadata
            = new EnumerationPropertyMetadata(new PropertyName("blurType"), //NOCHECK
            BlurType.class, true, BlurType.THREE_PASS_BOX, InspectorPath.UNUSED);
    private final ColorPropertyMetadata colorMetadata
            = new ColorPropertyMetadata(new PropertyName("color"), //NOCHECK
            true /* readWrite */, Color.BLACK, InspectorPath.UNUSED);
    private final SizeDoublePropertyMetadata heightMetadata
            = new SizeDoublePropertyMetadata(new PropertyName("height"), //NOCHECK
            true /* readWrite */, 21.0, InspectorPath.UNUSED);
    private final EffectPropertyMetadata inputMetadata
            = new EffectPropertyMetadata(new PropertyName("input"), //NOCHECK
            true /* readWrite */, null, InspectorPath.UNUSED);
    private final CoordinateDoublePropertyMetadata offsetXMetadata
            = new CoordinateDoublePropertyMetadata(new PropertyName("offsetX"), //NOCHECK
            true /* readWrite */, 0.0, InspectorPath.UNUSED);
    private final CoordinateDoublePropertyMetadata offsetYMetadata
            = new CoordinateDoublePropertyMetadata(new PropertyName("offsetY"), //NOCHECK
            true /* readWrite */, 0.0, InspectorPath.UNUSED);
    private final CoordinateDoublePropertyMetadata radiusMetadata
            = new CoordinateDoublePropertyMetadata(new PropertyName("radius"), //NOCHECK
            true /* readWrite */, 10.0, InspectorPath.UNUSED);
    private final OpacityDoublePropertyMetadata spreadMetadata
            = new OpacityDoublePropertyMetadata(new PropertyName("spread"), //NOCHECK
            true /* readWrite */, 0.0, InspectorPath.UNUSED);
    private final OpacityDoublePropertyMetadata widthMetadata
            = new OpacityDoublePropertyMetadata(new PropertyName("width"), //NOCHECK
            true /* readWrite */, 21.0, InspectorPath.UNUSED);

    public DropShadowPropertyMetadata(PropertyName name, boolean readWrite, 
            DropShadow defaultValue, InspectorPath inspectorPath) {
        super(name, DropShadow.class, readWrite, defaultValue, inspectorPath);
    }

    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(DropShadow value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        blurTypeMetadata.setValue(result, value.getBlurType().toString());
        colorMetadata.setValue(result, value.getColor());
        heightMetadata.setValue(result, value.getHeight());
        inputMetadata.setValue(result, value.getInput());
        offsetXMetadata.setValue(result, value.getOffsetX());
        offsetYMetadata.setValue(result, value.getOffsetY());
        radiusMetadata.setValue(result, value.getRadius());
        spreadMetadata.setValue(result, value.getSpread());
        widthMetadata.setValue(result, value.getWidth());

        return result;
    }
}
