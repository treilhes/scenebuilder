/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

import java.util.Objects;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.util.Callback;

/**
 *
 */

public abstract class CallbackPropertyMetadata extends ValuePropertyMetadata {

    private final Object defaultValue;

//    public CallbackPropertyMetadata(PropertyName name, boolean readWrite, Object defaultValue, InspectorPath inspectorPath) {
//        super(name, readWrite, inspectorPath);
//        this.defaultValue = defaultValue;
//    }

    protected CallbackPropertyMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
        this.defaultValue = builder.defaultValue;
    }

    public Object getDefaultValue() {
        return defaultValue;
    }

    public Object getValue(FXOMElement fxomInstance) {
        final Object result;

        if (isReadWrite()) {
            final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());
            if (fxomProperty instanceof FXOMPropertyC) {

                final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
                assert fxomPropertyC.getChildren().size() == 1;

                final FXOMObject valueFxomObject = fxomPropertyC.getChildren().get(0);
                final Object sceneGraphObject = valueFxomObject.getSceneGraphObject();

                result = castValue(sceneGraphObject);
            } else {
                assert fxomProperty == null;

                // propertyName is not specified in the fxom instance.
                // We return the default value specified in the metadata of the
                // property
                result = defaultValue;
            }
        } else {
            result = castValue(getName().getValue(fxomInstance.getSceneGraphObject()));
        }

        return result;
    }

    public void setValue(FXOMElement fxomInstance, Object value) {
        assert isReadWrite();

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());

        if (Objects.equals(value, getDefaultValueObject())) {
            // We must remove the fxom property if any
            if (fxomProperty != null) {
                fxomProperty.removeFromParentInstance();
            }
        } else {
            if (fxomProperty == null) {
                // propertyName is not specified in the fxom instance.
                // We insert a new fxom property
                final FXOMProperty newProperty
                        = makeFxomPropertyFromValue(fxomInstance, value);
                newProperty.addToParentInstance(-1, fxomInstance);
            } else {
                updateFxomPropertyWithValue(fxomProperty, value);
            }
        }
    }


    protected abstract void updateFxomInstanceWithValue(FXOMInstance valueInstance, Object value);
    protected abstract Class<?> getFxConstantClass();
    protected abstract Object castValue(Object value);




    /*
     * ValuePropertyMetadata
     */

    @Override
    public Class<?> getValueClass() {
        return Callback.class;
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
        setValue(fxomInstance, castValue(valueObject));
    }


    /*
     * Private
     */

    protected FXOMProperty makeFxomPropertyFromValue(FXOMElement fxomInstance, Object value) {
        assert fxomInstance != null;
        assert value != null;

        final FXOMDocument fxomDocument = fxomInstance.getFxomDocument();
        final FXOMInstance valueInstance = new FXOMInstance(fxomDocument, getFxConstantClass());
        updateFxomInstanceWithValue(valueInstance, value);
        return new FXOMPropertyC(fxomDocument, getName(), valueInstance);
    }

    protected void updateFxomPropertyWithValue(FXOMProperty fxomProperty, Object value) {
        assert value != null;
        assert fxomProperty instanceof FXOMPropertyC; // Because Callback are expressed using fx:constant

        final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
        assert fxomPropertyC.getChildren().size() == 1;

        FXOMObject valueObject = fxomPropertyC.getChildren().get(0);
        if (valueObject instanceof FXOMInstance) {
            updateFxomInstanceWithValue((FXOMInstance) valueObject, value);
        } else {
            final FXOMDocument fxomDocument = fxomProperty.getFxomDocument();
            final FXOMInstance valueInstance = new FXOMInstance(fxomDocument, getFxConstantClass());
            updateFxomInstanceWithValue(valueInstance, value);
            valueInstance.addToParentProperty(0, fxomPropertyC);
            valueObject.removeFromParentProperty();
        }
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
        /** The property default value. */
        protected Object defaultValue;

        public SELF withDefaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }
    }
}
