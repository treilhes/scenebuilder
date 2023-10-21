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

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.scene.text.TextAlignment;

/**
 *
 *
 */
public class EnumerationPropertyMetadata extends ValuePropertyMetadata {

    public static final String EQUIV_NONE = "NONE"; // NOCHECK
    public static final String EQUIV_AUTOMATIC = "AUTOMATIC"; // NOCHECK
    public static final String EQUIV_INHERITED = "INHERIT"; // NOCHECK

    private final Class<?> enumClass;
    private final Enum<?> defaultValue;
    private final String nullEquivalent;
    private List<String> validValues;

//    protected EnumerationPropertyMetadata(PropertyName name, Class<?> enumClass, boolean readWrite, Enum<?> defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, readWrite, inspectorPath);
//        assert enumClass.isEnum();
//        assert (readWrite == false) || (defaultValue != null);
//        this.enumClass = enumClass;
//        this.defaultValue = defaultValue;
//        this.nullEquivalent = null;
//    }
//
//    protected EnumerationPropertyMetadata(PropertyName name, Class<?> enumClass, String nullEquivalent, boolean readWrite,
//            InspectorPath inspectorPath) {
//        super(name, readWrite, inspectorPath);
//        assert enumClass.isEnum();
//        assert nullEquivalent != null;
//        this.enumClass = enumClass;
//        this.defaultValue = null;
//        this.nullEquivalent = nullEquivalent;
//    }

    public EnumerationPropertyMetadata(AbstractBuilder<?, ?, ?> builder) {
        super(builder);
        assert builder.enumClass.isEnum();
        assert (this.isReadWrite() == false) || (builder.nullEquivalent != null || builder.defaultValue != null);
        this.enumClass = builder.enumClass;
        this.defaultValue = builder.defaultValue;
        this.nullEquivalent = builder.nullEquivalent;
    }

    public String getValue(FXOMElement fxomInstance) {
        final String result;

        if (isReadWrite()) {
            final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());
            if (fxomProperty == null) {
                // propertyName is not specified in the fxom instance.
                // We return the default value specified in the metadata of the
                // property
                result = getDefaultValue();
            } else {
                assert fxomProperty instanceof FXOMPropertyT;
                final FXOMPropertyT fxomPropertyT = (FXOMPropertyT) fxomProperty;
                final PrefixedValue pv = new PrefixedValue(fxomPropertyT.getValue());
                if (pv.isBindingExpression()) {
                    result = getDefaultValue();
                } else {
                    result = fxomPropertyT.getValue();
                }
            }
        } else {
            final Object o = getName().getValue(fxomInstance.getSceneGraphObject());
            if (o == null) {
                result = getDefaultValue();
            } else {
                assert o.getClass() == enumClass;
                result = o.toString();
            }
        }

        return result;
    }

    public void setValue(FXOMElement fxomInstance, String value) {
        assert isReadWrite();
        assert value != null;

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());
        if (fxomProperty == null) {
            // propertyName is not specified in the fxom instance.
            if (value.equals(getDefaultValue()) == false) {
                // We insert a new fxom property
                final FXOMPropertyT newProperty = new FXOMPropertyT(fxomInstance.getFxomDocument(), getName(), value);
                newProperty.addToParentInstance(-1, fxomInstance);
            }
        } else {
            assert fxomProperty instanceof FXOMPropertyT;
            final FXOMPropertyT fxomPropertyT = (FXOMPropertyT) fxomProperty;
            if (value.equals(getDefaultValue())) {
                fxomPropertyT.removeFromParentInstance();
            } else {
                fxomPropertyT.setValue(value);
            }
        }
    }

    public String getDefaultValue() {
        final String result;
        if (isReadWrite()) {
            assert (defaultValue == null) == (nullEquivalent != null);
            result = (defaultValue == null) ? nullEquivalent : defaultValue.toString();
        } else {
            result = null;
        }
        return result;
    }

    public List<String> getValidValues() {
        if (validValues == null) {
            validValues = new ArrayList<>();

            for (Object e : enumClass.getEnumConstants()) {
                validValues.add(e.toString());
            }
            if (nullEquivalent != null) {
                assert defaultValue == null;
                if (validValues.contains(nullEquivalent) == false) {
                    validValues.add(0, nullEquivalent);
                }
            }
        }
        return validValues;
    }

    public int getValidValuesNumber() {
        return getValidValues().size();
    }

    /*
     * ValuePropertyMetadata
     */

    @Override
    public Class<?> getValueClass() {
        return enumClass;
    }

    @Override
    public Object getDefaultValueObject() {
        return getDefaultValue();
    }

    @Override
    public Object getValueObject(FXOMElement fxomInstance) {
        return getValue(fxomInstance);
    }

    @Override
    public void setValueObject(FXOMElement fxomInstance, Object valueObject) {
        setValue(fxomInstance, valueObject == null ? null : valueObject.toString());
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, T extends Enum<?>>
            extends ValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {
        protected Class<?> enumClass;
        protected Enum<?> defaultValue;
        protected String nullEquivalent;

        public AbstractBuilder(Class<T> cls) {
            super();
            withEnumClass(cls);
        }

        protected SELF withEnumClass(Class<T> enumClass) {
            this.enumClass = enumClass;
            return self();
        }

        public SELF withDefaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }

        protected SELF withNullEquivalent(String nullEquivalent) {
            this.nullEquivalent = nullEquivalent;
            return self();
        }

    }

    public static class Builder<T extends Enum<?>> extends AbstractBuilder<Builder<T>, EnumerationPropertyMetadata, T> {

        public Builder(Class<T> cls) {
            super(cls);
        }

        @Override
        public Builder<T> withNullEquivalent(String nullEquivalent) {
            return super.withNullEquivalent(nullEquivalent);
        }

        @Override
        public EnumerationPropertyMetadata build() {
            return new EnumerationPropertyMetadata(this);
        }
    }

    public static class TextAlignmentEnumerationPropertyMetadata extends EnumerationPropertyMetadata {

//        public TextAlignmentEnumerationPropertyMetadata(PropertyName name, boolean readWrite,
//                Enum<javafx.scene.text.TextAlignment> defaultValue, InspectorPath inspectorPath) {
//            super(name, javafx.scene.text.TextAlignment.class, readWrite, defaultValue, inspectorPath);
//        }

        public TextAlignmentEnumerationPropertyMetadata(
                AbstractBuilder<?, ?> builder) {
            super(builder);
        }

        protected static abstract class AbstractBuilder<SELF, TOBUILD>
                extends EnumerationPropertyMetadata.AbstractBuilder<SELF, TOBUILD, TextAlignment> {

            public AbstractBuilder(Class<javafx.scene.text.TextAlignment> cls) {
                super(cls);
            }

        }

        public static class Builder<T> extends AbstractBuilder<Builder<T>, TextAlignmentEnumerationPropertyMetadata> {


            public Builder(Class<javafx.scene.text.TextAlignment> cls) {
                super(cls);
            }

            public Builder() {
                super(javafx.scene.text.TextAlignment.class);
            }

            @Override
            public TextAlignmentEnumerationPropertyMetadata build() {
                return new TextAlignmentEnumerationPropertyMetadata(this);
            }

        }
    }
}
