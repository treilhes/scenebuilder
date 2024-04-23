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

import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.BooleanPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ComplexPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.AngleDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.list.StopListPropertyMetadata;

import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;

/**
 *
 */
public class RadialGradientPropertyMetadata<VC> extends ComplexPropertyMetadata<RadialGradient, VC> {

    private static final List<Stop> DEFAULT_STOPS
            = new RadialGradient(0.0, 1.0, 0.0, 0.0, 1.0,
            true /* proportional */, CycleMethod.NO_CYCLE).getStops();

    private final AngleDoublePropertyMetadata<Void> focusAngleMetadata = new AngleDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("focusAngle")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> focusDistanceMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("focusDistance")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> centerXMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("centerX")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final CoordinateDoublePropertyMetadata<Void> centerYMetadata = new CoordinateDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("centerY")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final SizeDoublePropertyMetadata<Void> radiusMetadata = new SizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("radius")) //NOCHECK
            .readWrite(true)
            .defaultValue(0.0)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final BooleanPropertyMetadata<Void> proportionalMetadata = new BooleanPropertyMetadata.Builder<Void>()
            .name(new PropertyName("proportional")) //NOCHECK
            .readWrite(true)
            .defaultValue(true)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> cycleMethodMetadata = new EnumerationPropertyMetadata.Builder<CycleMethod, Void>(CycleMethod.class)
            .name(new PropertyName("cycleMethod")) //NOCHECK
            .readWrite(true)
            .defaultValue(CycleMethod.NO_CYCLE)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final StopListPropertyMetadata<Void> stopsMetadata = new StopListPropertyMetadata.Builder<Void>()
            .name(new PropertyName("stops")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT_STOPS)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    protected RadialGradientPropertyMetadata(PropertyName name, boolean readWrite,
//            RadialGradient defaultValue, InspectorPath inspectorPath) {
//        super(name, RadialGradient.class, readWrite, defaultValue, inspectorPath);
//    }

    protected RadialGradientPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(RadialGradient value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        focusAngleMetadata.setValue(result, value.getFocusAngle());
        focusDistanceMetadata.setValue(result, value.getFocusDistance());
        centerXMetadata.setValue(result, value.getCenterX());
        centerYMetadata.setValue(result, value.getCenterY());
        radiusMetadata.setValue(result, value.getRadius());
        proportionalMetadata.setValue(result, value.isProportional());
        cycleMethodMetadata.setValue(result, value.getCycleMethod().toString());
        stopsMetadata.setValue(result, value.getStops());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC>
            extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, RadialGradient, VC> {
        public AbstractBuilder() {
            super();
            valueClass(RadialGradient.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, RadialGradientPropertyMetadata<VC>, VC> {
        @Override
        public RadialGradientPropertyMetadata<VC> build() {
            return new RadialGradientPropertyMetadata<VC>(this);
        }
    }
}
