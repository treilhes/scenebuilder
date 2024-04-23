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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

/**
 *
 */
public abstract class SingleValuePropertyMetadata<T, VC> extends ValuePropertyMetadata<VC> {

    private static final Logger logger = LoggerFactory.getLogger(SingleValuePropertyMetadata.class);

    private final Class<T> valueClass;
    private final T defaultValue;

//    protected SingleValuePropertyMetadata(PropertyName name, Class<T> valueClass,
//            boolean readWrite, T defaultValue, InspectorPath inspectorPath) {
//        super(name, readWrite, inspectorPath);
//        this.defaultValue = defaultValue;
//        this.valueClass = valueClass;
//    }

    protected SingleValuePropertyMetadata(AbstractBuilder<?,?,T,VC> builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;
        this.valueClass = builder.valueClass;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public T getValue(FXOMElement fxomInstance) {
        final T result;

        if (isReadWrite()) {
            final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());
            if (fxomProperty == null) {
                // propertyName is not specified in the fxom instance.
                // We return the default value specified in the metadata of the
                // property
                result = defaultValue;
            } else if (fxomProperty instanceof FXOMPropertyT) {
                final FXOMPropertyT fxomPropertyT = (FXOMPropertyT) fxomProperty;
                final PrefixedValue pv = new PrefixedValue(fxomPropertyT.getValue());
                if (pv.isBindingExpression()) {
                    result = getDefaultValue();
                } else {
                    result = makeValueFromProperty(fxomPropertyT);
                }
            } else if (fxomProperty instanceof FXOMPropertyC) {
                final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
                assert fxomPropertyC.getChildren().isEmpty() == false;
                final FXOMObject firstValue = fxomPropertyC.getChildren().get(0);
                if (firstValue instanceof FXOMInstance) {
                    result = makeValueFromFxomInstance((FXOMInstance) firstValue);
                } else {
                    result = getDefaultValue();
                }
            } else {
                assert false;
                result = defaultValue;
            }
        } else {
            if (valueClass == null) {
                logger.error("valueClass is null for Class : {}", this.getClass());
            }
            result = valueClass.cast(getName().getValue(fxomInstance.getSceneGraphObject()));
        }

        return result;
    }

    public void setValue(FXOMElement fxomInstance, T value) {
        assert isReadWrite();

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());

        if (Objects.equals(value, getDefaultValueObject())) {
            // We must remove the fxom property if any
            if (fxomProperty != null) {
                fxomProperty.removeFromParentInstance();
            }
        } else {
            final FXOMDocument fxomDocument = fxomInstance.getFxomDocument();
            final FXOMProperty newProperty;
            if (canMakeStringFromValue(value)) {
                final String valueString = makeStringFromValue(value);
                newProperty = new FXOMPropertyT(fxomDocument, getName(), valueString);
            } else {
                final FXOMInstance valueInstance = makeFxomInstanceFromValue(value, fxomDocument);
                newProperty = new FXOMPropertyC(fxomDocument, getName(), valueInstance);
            }
            FXOMNodes.updateProperty(fxomInstance, newProperty);
        }
    }

    public abstract T makeValueFromString(String string);
    public abstract T makeValueFromFxomInstance(FXOMInstance valueFxomInstance);
    public abstract boolean canMakeStringFromValue(T value);
    public abstract String makeStringFromValue(T value);
    public abstract FXOMInstance makeFxomInstanceFromValue(T value, FXOMDocument fxomDocument);

    /* This routine should become abstract and replace makeValueFromString(). */
    public T makeValueFromProperty(FXOMPropertyT fxomProperty) {
        return makeValueFromString(fxomProperty.getValue());
    }

    /*
     * ValuePropertyMetadata
     */
    @Override
    public Class<? extends T> getValueClass() {
        return valueClass;
    }

    @Override
    public Object getDefaultValueObject() {
        return defaultValue;
    }

    @Override
    public Object getValueObject(FXOMElement fxomInstance) {
        return getValue(fxomInstance);
    }

    @Override
    public void setValueObject(FXOMElement fxomInstance, Object valueObject) {
        setValue(fxomInstance, valueClass.cast(valueObject));
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, T,VC> extends ValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD,VC> {
        /** The property default value. */
        protected T defaultValue;

        /** The property value class. */
        protected Class<T> valueClass;

        public SELF withDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }

        protected SELF withValueClass(Class<T> valueClass) {
            this.valueClass = valueClass;
            return self();
        }
    }

}
