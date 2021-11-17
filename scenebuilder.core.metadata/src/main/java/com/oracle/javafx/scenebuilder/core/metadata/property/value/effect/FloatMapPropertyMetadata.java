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
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.ComplexPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.effect.FloatMap;

/**
 *
 */
public class FloatMapPropertyMetadata extends ComplexPropertyMetadata<FloatMap> {
    
    private final IntegerPropertyMetadata widthMetadata = new IntegerPropertyMetadata.Builder()
            .withName(new PropertyName("height")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(1)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final IntegerPropertyMetadata heightMetadata = new IntegerPropertyMetadata.Builder()
            .withName(new PropertyName("height")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(1)
            .withInspectorPath(InspectorPath.UNUSED).build();

    protected FloatMapPropertyMetadata(PropertyName name, boolean readWrite, 
            FloatMap defaultValue, InspectorPath inspectorPath) {
        super(name, FloatMap.class, readWrite, defaultValue, inspectorPath);
    }

    protected FloatMapPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }
    
    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(FloatMap value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, getValueClass());
        
        widthMetadata.setValue(result, value.getWidth());
        heightMetadata.setValue(result, value.getHeight());

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, FloatMap> {
        public AbstractBuilder() {
            super();
            withValueClass(FloatMap.class);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, FloatMapPropertyMetadata> {
        @Override
        public FloatMapPropertyMetadata build() {
            return new FloatMapPropertyMetadata(this);
        }
    }
}
