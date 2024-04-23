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


import java.util.Optional;

import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;

/**
 *
 *
 */
public abstract class ComplexPropertyMetadata<T,VC> extends SingleValuePropertyMetadata<T,VC> {

//    protected ComplexPropertyMetadata(PropertyName name, Class<T> valueClass,
//            boolean readWrite, T defaultValue, InspectorPath inspectorPath) {
//        super(name, valueClass, readWrite, defaultValue, inspectorPath);
//    }

    protected ComplexPropertyMetadata(AbstractBuilder<?, ?, T,VC> builder) {
        super(builder);
    }

    /*
     * SingleValuePropertyMetadata
     */
    @Override
    public T makeValueFromProperty(FXOMPropertyT fxomProperty) {
        final T result;

        final PrefixedValue pv = new PrefixedValue(fxomProperty.getValue());
        if (pv.isExpression()) {
            final String fxId = pv.getSuffix();

            Optional<FXOMObject> targetObject = fxomProperty.getFxomDocument()
                    .collect(FxIdCollector.findFirstById(fxId));

            if (targetObject.isEmpty()) {
                // Emergency code
                result = getDefaultValue();
            } else {
                result = targetObject.get().getSceneGraphObject().getAs(getValueClass());
            }
        } else {
            result = makeValueFromString(fxomProperty.getValue());
        }

        return result;
    }

    @Override
    public T makeValueFromString(String string) {
        throw new RuntimeException("Bug"); //NOCHECK
    }

    @Override
    public boolean canMakeStringFromValue(T value) {
        return value == null;
    }

    @Override
    public String makeStringFromValue(T value) {
        assert value == null;
        return "$null"; //NOCHECK
    }

    @Override
    public T makeValueFromFxomInstance(FXOMInstance valueFxomInstance) {
        return getValueClass().cast(valueFxomInstance.getSceneGraphObject());
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, T,VC> extends SingleValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD, T,VC> {
    }

}
