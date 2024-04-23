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

import javafx.geometry.HPos;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.Priority;

/**
 *
 */
public class ColumnConstraintsPropertyMetadata<VC> extends ComplexPropertyMetadata<ColumnConstraints, VC> {

    private static final ColumnConstraints DEFAULT = new ColumnConstraints();

    private final BooleanPropertyMetadata<Void> fillWidthMetadata = new BooleanPropertyMetadata.Builder<Void>()
            .name(new PropertyName("fillWidth")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.isFillWidth())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedSizeDoublePropertyMetadata<Void> maxWidthMetadata = new ComputedSizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("maxWidth")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getMaxWidth())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedSizeDoublePropertyMetadata<Void> minWidthMetadata = new ComputedSizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("minWidth")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getMinWidth())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final PercentageDoublePropertyMetadata<Void> percentWidthMetadata = new PercentageDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("percentWidth")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getPercentWidth())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final ComputedAndPrefSizeDoublePropertyMetadata<Void> prefWidthMetadata = new ComputedAndPrefSizeDoublePropertyMetadata.Builder<Void>()
            .name(new PropertyName("prefWidth")) //NOCHECK
            .readWrite(true)
            .defaultValue(DEFAULT.getPrefWidth())
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> halignmentMetadata = new EnumerationPropertyMetadata.Builder<HPos, Void>(HPos.class)
            .name(new PropertyName("halignment")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(EnumerationPropertyMetadata.EQUIV_INHERITED)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

    private final EnumerationPropertyMetadata<Void> hgrowMetadata = new EnumerationPropertyMetadata.Builder<Priority, Void>(Priority.class)
            .name(new PropertyName("hgrow")) //NOCHECK
            .readWrite(true)
            .nullEquivalent(EnumerationPropertyMetadata.EQUIV_INHERITED)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    public ColumnConstraintsPropertyMetadata(PropertyName name, boolean readWrite, ColumnConstraints defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, ColumnConstraints.class, readWrite, defaultValue, inspectorPath);
//    }

    protected ColumnConstraintsPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    /*
     * Utility
     */

    public static boolean equals(ColumnConstraints c1, ColumnConstraints c2) {
        assert c1 != null;
        assert c2 != null;

        final boolean result;
        if (c1 == c2) {
            result = true;
        } else {
            result = Objects.equals(c1.getHalignment(), c2.getHalignment())
                    && Objects.equals(c1.getHgrow(), c2.getHgrow())
                    && MathUtils.equals(c1.getMaxWidth(), c2.getMaxWidth())
                    && MathUtils.equals(c1.getMinWidth(), c2.getMinWidth())
                    && MathUtils.equals(c1.getPercentWidth(), c2.getPercentWidth())
                    && MathUtils.equals(c1.getPrefWidth(), c2.getPrefWidth());
        }

        return result;
    }

    /*
     * ComplexPropertyMetadata
     */

    @Override
    public FXOMInstance makeFxomInstanceFromValue(ColumnConstraints value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, value.getClass());

        fillWidthMetadata.setValue(result, value.isFillWidth());
        maxWidthMetadata.setValue(result, value.getMaxWidth());
        minWidthMetadata.setValue(result, value.getMinWidth());
        percentWidthMetadata.setValue(result, value.getPercentWidth());
        prefWidthMetadata.setValue(result, value.getPrefWidth());

        final HPos halignment = value.getHalignment();
        if (halignment == null) {
            halignmentMetadata.setValue(result, halignmentMetadata.getDefaultValue());
        } else {
            halignmentMetadata.setValue(result, halignment.toString());
        }

        final Priority hgrow = value.getHgrow();
        if (hgrow == null) {
            hgrowMetadata.setValue(result, hgrowMetadata.getDefaultValue());
        } else {
            hgrowMetadata.setValue(result, hgrow.toString());
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, ColumnConstraints, VC> {
        public AbstractBuilder() {
            super();
            valueClass(ColumnConstraints.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ColumnConstraintsPropertyMetadata<VC>, VC> {
        @Override
        public ColumnConstraintsPropertyMetadata<VC> build() {
            return new ColumnConstraintsPropertyMetadata<VC>(this);
        }
    }
}
