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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.effect.light;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.Light;
import javafx.scene.paint.Color;

/**
 *
 */
public class PointLightPropertyMetadata extends ComplexPropertyMetadata<Light.Point> {

    private final ColorPropertyMetadata colorMetadata = new ColorPropertyMetadata.Builder()
            .withName(new PropertyName("color"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(Color.WHITE)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata xMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("x"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata yMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("y"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final CoordinateDoublePropertyMetadata zMetadata = new CoordinateDoublePropertyMetadata.Builder()
            .withName(new PropertyName("z"))//NOCHECK
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected PointLightPropertyMetadata(PropertyName name, boolean readWrite, Light.Point defaultValue,
            InspectorPath inspectorPath) {
        super(name, Light.Point.class, readWrite, defaultValue, inspectorPath);
    }

    protected PointLightPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }
    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(Light.Point value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, getValueClass());

        colorMetadata.setValue(result, value.getColor());
        xMetadata.setValue(result, value.getX());
        yMetadata.setValue(result, value.getY());
        zMetadata.setValue(result, value.getZ());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD>
            extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, Light.Point> {
        public AbstractBuilder() {
            super();
            withValueClass(Light.Point.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, PointLightPropertyMetadata> {
        @Override
        public PointLightPropertyMetadata build() {
            return new PointLightPropertyMetadata(this);
        }
    }
}
