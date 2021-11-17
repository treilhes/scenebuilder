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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.paint;

import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.BooleanPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.StopListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 *
 */
public class LinearGradientPropertyMetadata extends ComplexPropertyMetadata<LinearGradient> {

    private static final List<Stop> DEFAULT_STOPS 
            = new LinearGradient(0.0, 0.0, 1.0, 1.0,
            true /* proportional */, CycleMethod.NO_CYCLE).getStops();
    
    private final CoordinateDoublePropertyMetadata startXMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("startX")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata startYMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("startY")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata endXMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("endX")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata endYMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("endY")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata proportionalMetadata = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("proportional")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(true)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final EnumerationPropertyMetadata cycleMethodMetadata = new EnumerationPropertyMetadata.Builder<>(CycleMethod.class)
            .withName(new PropertyName("cycleMethod")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(CycleMethod.NO_CYCLE)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final StopListPropertyMetadata stopsMetadata = new StopListPropertyMetadata.Builder()
            .withName(new PropertyName("stops")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT_STOPS)
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected LinearGradientPropertyMetadata(PropertyName name, boolean readWrite, 
            LinearGradient defaultValue, InspectorPath inspectorPath) {
        super(name, LinearGradient.class, readWrite, defaultValue, inspectorPath);
    }

    protected LinearGradientPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }
    
    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(LinearGradient value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());
        
        startXMetadata.setValue(result, value.getStartX());
        startYMetadata.setValue(result, value.getStartY());
        endXMetadata.setValue(result, value.getEndX());
        endYMetadata.setValue(result, value.getEndY());
        proportionalMetadata.setValue(result, value.isProportional());
        cycleMethodMetadata.setValue(result, value.getCycleMethod().toString());
        stopsMetadata.setValue(result, value.getStops());
        
        return result;
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, LinearGradient> {
        public AbstractBuilder() {
            super();
            withValueClass(LinearGradient.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, LinearGradientPropertyMetadata> {
        @Override
        public LinearGradientPropertyMetadata build() {
            return new LinearGradientPropertyMetadata(this);
        }
    }
}
