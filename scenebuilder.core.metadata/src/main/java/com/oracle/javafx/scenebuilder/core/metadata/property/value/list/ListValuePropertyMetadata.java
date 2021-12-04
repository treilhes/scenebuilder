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
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.SingleValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.fxml.FXMLLoader;

/**
 *
 */
public abstract class ListValuePropertyMetadata<T> extends ValuePropertyMetadata {

    private final Class<T> itemClass;
    private final SingleValuePropertyMetadata<T> itemMetadata;
    private final List<T> defaultValue;

    protected ListValuePropertyMetadata(PropertyName name, Class<T> itemClass, SingleValuePropertyMetadata<T> itemMetadata,
            boolean readWrite, List<T> defaultValue, InspectorPath inspectorPath) {
        super(name, readWrite, inspectorPath);
        this.itemClass = itemClass;
        this.defaultValue = defaultValue;
        this.itemMetadata = itemMetadata;
    }

    protected ListValuePropertyMetadata(AbstractBuilder<?, ?, T> builder) {
        super(builder);
        this.itemClass = builder.itemClass;
        this.defaultValue = builder.defaultValue;
        this.itemMetadata = builder.itemMetadata;
    }

    public Class<T> getItemClass() {
        return itemClass;
    }

    public List<T> getDefaultValue() {
        return defaultValue;
    }

    public List<T> getValue(FXOMInstance fxomInstance) {
        final List<T> result;

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
                    result = makeValueFromString(fxomPropertyT.getValue());
                }
            } else if (fxomProperty instanceof FXOMPropertyC) {
                final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
                result = new ArrayList<>();
                for (FXOMObject itemFxomObject : fxomPropertyC.getValues()) {
                    if (itemFxomObject instanceof FXOMInstance) {
                        final FXOMInstance itemFxomInstance = (FXOMInstance) itemFxomObject;
                        result.add(itemMetadata.makeValueFromFxomInstance(itemFxomInstance));
                    } else {
                        assert false;
                    }
                }
            } else {
                assert false;
                result = defaultValue;
            }
        } else {
            final List<?> items = (List<?>) getName().getValue(fxomInstance.getSceneGraphObject());
            result = new ArrayList<>();
            for (Object item : items) {
                result.add(getItemClass().cast(item));
            }
        }

        return result;
    }

    public void setValue(FXOMInstance fxomInstance, List<T> value) {
        assert isReadWrite();

        final FXOMProperty fxomProperty = fxomInstance.getProperties().get(getName());

        if (Objects.equals(value, getDefaultValueObject()) || value.isEmpty()) {
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
                final List<FXOMObject> items = new ArrayList<>();
                for (T i : value) {
                    items.add(itemMetadata.makeFxomInstanceFromValue(i, fxomDocument));
                }
                newProperty = new FXOMPropertyC(fxomDocument, getName(), items);
            }
            FXOMNodes.updateProperty(fxomInstance, newProperty);
        }
    }

    /*
     * To be subclassed
     */

    protected boolean canMakeStringFromValue(List<T> value) {
        boolean result = true;

        for (T i : value) {
            result = itemMetadata.canMakeStringFromValue(i);
            if (result == false) {
                break;
            }
        }

        return result;
    }

    protected String makeStringFromValue(List<T> value) {
        assert canMakeStringFromValue(value);

        final StringBuilder result = new StringBuilder();

        for (T item : value) {
            if (result.length() >= 1) {
                result.append(FXMLLoader.ARRAY_COMPONENT_DELIMITER);
                result.append(' ');
            }
            result.append(itemMetadata.makeStringFromValue(item));
        }

        return result.toString();
    }

    protected List<T> makeValueFromString(String string) {
        final List<T> result;

        final String[] items = string.split(FXMLLoader.ARRAY_COMPONENT_DELIMITER);
        if (items.length == 0) {
            result = Collections.emptyList();
        } else {
            result = new ArrayList<>();
            for (String itemString : items) {
                result.add(itemMetadata.makeValueFromString(itemString));
            }
        }

        return result;
    }

    /*
     * ValuePropertyMetadata
     */

    @Override
    public Class<?> getValueClass() {
        return List.class;
    }

    @Override
    public Object getDefaultValueObject() {
        return defaultValue;
    }

    @Override
    public Object getValueObject(FXOMInstance fxomInstance) {
        return getValue(fxomInstance);
    }

    @Override
    public void setValueObject(FXOMInstance fxomInstance, Object valueObject) {
        assert valueObject instanceof List;
        setValue(fxomInstance, castItemList((List<?>) valueObject));
    }

    /*
     * Private
     */

    private List<T> castItemList(List<?> valueObject) {
        final List<T> result = new ArrayList<>();

        for (Object itemValueObject : valueObject) {
            result.add(getItemClass().cast(itemValueObject));
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, T>
            extends ValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD> {

        protected Class<T> itemClass;
        protected SingleValuePropertyMetadata<T> itemMetadata;
        protected List<T> defaultValue;

        protected SELF withItemClass(Class<T> itemClass) {
            this.itemClass = itemClass;
            return self();
        }

        protected SELF withItemMetadata(SingleValuePropertyMetadata<T> itemMetadata) {
            this.itemMetadata = itemMetadata;
            return self();
        }

        public SELF withDefaultValue(List<T> defaultValue) {
            this.defaultValue = defaultValue;
            return self();
        }
    }
}