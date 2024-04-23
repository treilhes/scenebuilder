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
package com.gluonhq.jfxapps.core.metadata.property.value;

import java.util.Objects;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.ComputedAndPrefSizeDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.ComputedSizeDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.PercentageDoublePropertyMetadata;
import com.gluonhq.jfxapps.util.MathUtils;

import javafx.geometry.VPos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 *
 */
public class RowConstraintsPropertyMetadata<VC> extends ComplexPropertyMetadata<RowConstraints, VC> {

    private static final RowConstraints DEFAULT = new RowConstraints();

    private final BooleanPropertyMetadata<Void> fillHeightMetadata = new BooleanPropertyMetadata.Builder<Void>()
            .withName(new PropertyName("fillHeight")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.isFillHeight())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedSizeDoublePropertyMetadata<Void> maxHeightMetadata = new ComputedSizeDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("maxHeight")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getMaxHeight())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedSizeDoublePropertyMetadata<Void> minHeightMetadata = new ComputedSizeDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("minHeight")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getMinHeight())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final PercentageDoublePropertyMetadata<Void> percentHeightMetadata = new PercentageDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("percentHeight")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getPercentHeight())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedAndPrefSizeDoublePropertyMetadata<Void> prefHeightMetadata = new ComputedAndPrefSizeDoublePropertyMetadata.Builder<Void>()
            .withName(new PropertyName("prefHeight")) //NOCHECK
            .withReadWrite(true)
            .withDefaultValue(DEFAULT.getPrefHeight())
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> valignmentMetadata = new EnumerationPropertyMetadata.Builder<VPos, Void>(VPos.class)
            .withName(new PropertyName("valignment")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(EnumerationPropertyMetadata.EQUIV_INHERITED)
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> vgrowMetadata = new EnumerationPropertyMetadata.Builder<Priority, Void>(Priority.class)
            .withName(new PropertyName("vgrow")) //NOCHECK
            .withReadWrite(true)
            .withNullEquivalent(EnumerationPropertyMetadata.EQUIV_INHERITED)
            //.withInspectorPath(InspectorPath.UNUSED)
            .build();

//    public RowConstraintsPropertyMetadata(PropertyName name, boolean readWrite, RowConstraints defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, RowConstraints.class, readWrite, defaultValue, inspectorPath);
//    }

    protected RowConstraintsPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * Utility
     */

    public static boolean equals(RowConstraints r1, RowConstraints r2) {
        assert r1 != null;
        assert r2 != null;

        final boolean result;
        if (r1 == r2) {
            result = true;
        } else {
            result = Objects.equals(r1.getValignment(), r2.getValignment())
                    && Objects.equals(r1.getVgrow(), r2.getVgrow())
                    && MathUtils.equals(r1.getMaxHeight(), r2.getMaxHeight())
                    && MathUtils.equals(r1.getMinHeight(), r2.getMinHeight())
                    && MathUtils.equals(r1.getPercentHeight(), r2.getPercentHeight())
                    && MathUtils.equals(r1.getPrefHeight(), r2.getPrefHeight());
        }

        return result;
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(RowConstraints value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, getValueClass());

        fillHeightMetadata.setValue(result, value.isFillHeight());
        maxHeightMetadata.setValue(result, value.getMaxHeight());
        minHeightMetadata.setValue(result, value.getMinHeight());
        percentHeightMetadata.setValue(result, value.getPercentHeight());
        prefHeightMetadata.setValue(result, value.getPrefHeight());

        final VPos valignment = value.getValignment();
        if (valignment == null) {
            valignmentMetadata.setValue(result, valignmentMetadata.getDefaultValue());
        } else {
            valignmentMetadata.setValue(result, valignment.toString());
        }
        final Priority vgrow = value.getVgrow();
        if (vgrow == null) {
            vgrowMetadata.setValue(result, vgrowMetadata.getDefaultValue());
        } else {
            vgrowMetadata.setValue(result, vgrow.toString());
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, RowConstraints, VC> {
        public AbstractBuilder() {
            super();
            withValueClass(RowConstraints.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, RowConstraintsPropertyMetadata<VC>, VC> {
        @Override
        public RowConstraintsPropertyMetadata<VC> build() {
            return new RowConstraintsPropertyMetadata<VC>(this);
        }
    }
}
