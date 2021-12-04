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
package com.oracle.javafx.scenebuilder.core.metadata.property.value;

import com.oracle.javafx.scenebuilder.core.metadata.BasicSelection;

/**
 * Base class for number
 */
public abstract class NumberPropertyMetadata<T extends Number> extends TextEncodablePropertyMetadata<T> {

    private T min;
    private T max;

//    protected NumberPropertyMetadata(PropertyName name, Class<T> cls, boolean readWrite, T defaultValue, InspectorPath inspectorPath) {
//        super(name, cls, readWrite, defaultValue, inspectorPath);
//    }

    protected NumberPropertyMetadata(AbstractBuilder<?,?,T> builder) {
        super(builder);
    }

    public T getMin(BasicSelection selectionState) {
        return min;
    }

    protected NumberPropertyMetadata<T> setMin(T min) {
        this.min = min;
        return this;
    }

    public T getMax(BasicSelection selectionState) {
        return max;
    }

    protected NumberPropertyMetadata<T> setMax(T max) {
        this.max = max;
        return this;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, T> extends TextEncodablePropertyMetadata.AbstractBuilder<SELF, TOBUILD, T> {
        protected T min;
        protected T max;

        protected SELF withMin(T min) {
            this.min = min;
            return self();
        }

        protected SELF withMax(T max) {
            this.max = max;
            return self();
        }
    }

}