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
package com.gluonhq.jfxapps.core.metadata.property.value.list;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.ColumnConstraintsPropertyMetadata;

import javafx.scene.layout.ColumnConstraints;

/**
 *
 */
public class ColumnConstraintsListPropertyMetadata<VC> extends ListValuePropertyMetadata<ColumnConstraints, VC> {

    private static final ColumnConstraintsPropertyMetadata<Void> itemMetadata = new ColumnConstraintsPropertyMetadata.Builder<Void>()
            .name(new PropertyName("unused")) // NOCHECK
            .readWrite(true)
            .defaultValue(null)
            //.inspectorPath(InspectorPath.UNUSED)
            .build();

//    public ColumnConstraintsListPropertyMetadata(PropertyName name, boolean readWrite,
//            List<ColumnConstraints> defaultValue, InspectorPath inspectorPath) {
//        super(name, ColumnConstraints.class, itemMetadata, readWrite, defaultValue, inspectorPath);
//    }

//    public ColumnConstraintsListPropertyMetadata() {
//        this(new PropertyName("columnConstraints"), true /* readWrite */, Collections.emptyList(),
//                InspectorPath.UNUSED);
//    }

    protected ColumnConstraintsListPropertyMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    public void pack(FXOMInstance fxomInstance) {
        final ColumnConstraints def = new ColumnConstraints();
        final List<ColumnConstraints> v = new ArrayList<>(getValue(fxomInstance));
        if (v.isEmpty() == false) {
            ColumnConstraints last = v.get(v.size() - 1);
            while ((last != null) && ColumnConstraintsPropertyMetadata.equals(last, def)) {
                v.remove(v.size() - 1);
                if (v.isEmpty()) {
                    last = null;
                } else {
                    last = v.get(v.size() - 1);
                }
            }
            setValue(fxomInstance, v);
        }
    }

    public void unpack(FXOMInstance fxomInstance, int columnCount) {
        final List<ColumnConstraints> value = new ArrayList<>(getValue(fxomInstance));
        if (value.size() < columnCount) {
            final List<ColumnConstraints> newValue = new ArrayList<>();
            newValue.addAll(value);
            while (newValue.size() < columnCount) {
                newValue.add(new ColumnConstraints());
            }
            setValue(fxomInstance, newValue);
        }
    }

    public static boolean equals(List<ColumnConstraints> l1, List<ColumnConstraints> l2) {

        assert l1 != null;
        assert l2 != null;

        boolean result;
        if (l1.size() != l2.size()) {
            result = false;
        } else {
            result = true;
            for (int i = 0, count = l1.size(); (i < count) && result; i++) {
                final ColumnConstraints c1 = l1.get(i);
                final ColumnConstraints c2 = l2.get(i);
                result = ColumnConstraintsPropertyMetadata.equals(c1, c2);
            }
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC>
            extends ListValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD, ColumnConstraints, VC> {
        public AbstractBuilder() {
            super();
            name(new PropertyName("columnConstraints"));
            itemClass(ColumnConstraints.class);
            itemMetadata(ColumnConstraintsListPropertyMetadata.itemMetadata);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ColumnConstraintsListPropertyMetadata<VC>, VC> {
        @Override
        public ColumnConstraintsListPropertyMetadata<VC> build() {
            return new ColumnConstraintsListPropertyMetadata<VC>(this);
        }
    }
}
