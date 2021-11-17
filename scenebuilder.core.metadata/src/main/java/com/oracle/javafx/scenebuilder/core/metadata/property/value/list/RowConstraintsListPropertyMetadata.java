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
package com.oracle.javafx.scenebuilder.core.metadata.property.value.list;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.RowConstraintsPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.layout.RowConstraints;

/**
 *
 */
public class RowConstraintsListPropertyMetadata extends ListValuePropertyMetadata<RowConstraints> {

    private static final RowConstraintsPropertyMetadata itemMetadata = new RowConstraintsPropertyMetadata.Builder()
            .withName(new PropertyName("unused"))
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED)
            .build();

//    public RowConstraintsListPropertyMetadata(PropertyName name, boolean readWrite, List<RowConstraints> defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, RowConstraints.class, itemMetadata, readWrite, defaultValue, inspectorPath);
//    }
//
//    public RowConstraintsListPropertyMetadata() {
//        this(new PropertyName("rowConstraints"), true /* readWrite */, // NOCHECK
//                Collections.emptyList(), InspectorPath.UNUSED);
//    }

    protected RowConstraintsListPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    public void pack(FXOMInstance fxomInstance) {
        final RowConstraints def = new RowConstraints();
        final List<RowConstraints> v = new ArrayList<>(getValue(fxomInstance));
        if (v.isEmpty() == false) {
            RowConstraints last = v.get(v.size() - 1);
            while ((last != null) && RowConstraintsPropertyMetadata.equals(last, def)) {
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

    public void unpack(FXOMInstance fxomInstance, int rowCount) {
        final List<RowConstraints> value = new ArrayList<>(getValue(fxomInstance));
        if (value.size() < rowCount) {
            final List<RowConstraints> newValue = new ArrayList<>();
            newValue.addAll(value);
            while (newValue.size() < rowCount) {
                newValue.add(new RowConstraints());
            }
            setValue(fxomInstance, newValue);
        }
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD>
            extends ListValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD, RowConstraints> {
        public AbstractBuilder() {
            super();
            withItemClass(RowConstraints.class);
            withItemMetadata(RowConstraintsListPropertyMetadata.itemMetadata);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, RowConstraintsListPropertyMetadata> {
        @Override
        public RowConstraintsListPropertyMetadata build() {
            return new RowConstraintsListPropertyMetadata(this);
        }
    }

    public static boolean equals(List<RowConstraints> l1, List<RowConstraints> l2) {

        assert l1 != null;
        assert l2 != null;

        boolean result;
        if (l1.size() != l2.size()) {
            result = false;
        } else {
            result = true;
            for (int i = 0, count = l1.size(); (i < count) && result; i++) {
                final RowConstraints c1 = l1.get(i);
                final RowConstraints c2 = l2.get(i);
                result = RowConstraintsPropertyMetadata.equals(c1, c2);
            }
        }

        return result;
    }

}
