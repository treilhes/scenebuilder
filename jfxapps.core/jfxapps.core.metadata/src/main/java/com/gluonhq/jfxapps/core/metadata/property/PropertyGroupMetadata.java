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
package com.gluonhq.jfxapps.core.metadata.property;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.gluonhq.jfxapps.core.fxom.FXOMElement;

// TODO check if complexmetadata is not a valid substitute of this one
public class PropertyGroupMetadata<VC> extends ValuePropertyMetadata<VC> {

    private final Map<String, ValuePropertyMetadata<VC>> properties = new HashMap<>();

//    protected PropertyGroupMetadata(PropertyName name, ValuePropertyMetadata... properties) {
//        super(name, true, Arrays.stream(properties).anyMatch(p -> p.isReadWrite()), properties[0].getInspectorPath());
//
//        for (ValuePropertyMetadata p:properties) {
//            this.properties.put(p.getName().getName(), p);
//        }
//
//    }

    protected PropertyGroupMetadata(AbstractBuilder<?,?, VC> builder) {
//        super((AbstractBuilder<?,?, VC>)((
//                AbstractBuilder<PropertyGroupMetadata.AbstractBuilder<?,?, VC>, PropertyGroupMetadata<VC>, VC>)builder)
//                .withInspectorPath(builder.properties.isEmpty() ? null : builder.properties.values().iterator().next().getInspectorPath())
//                .withReadWrite(builder.properties.values().stream().anyMatch(p -> p.isReadWrite()))
//                );
        super(builder);
        this.properties.putAll(builder.properties);
    }

    public Map<String, ValuePropertyMetadata<VC>> getPropertiesMap() {
        return Collections.unmodifiableMap(properties);
    }

    @SuppressWarnings("unchecked")
    public ValuePropertyMetadata<VC>[] getProperties() {
        return properties.values().toArray((ValuePropertyMetadata<VC>[])new ValuePropertyMetadata[0]);
    }

    @Override
    public Object getDefaultValueObject() {
//        return properties.values().stream()
//                .map(it -> it.getDefaultValueObject())
//                .collect(Collectors.toList())
//                .toArray();
        Map<String, Object> map = new HashMap<>();
        properties.entrySet().forEach(e -> map.put(e.getKey(), e.getValue().getDefaultValueObject()));
        return map;
    }

    @Override
    public void setValueInSceneGraphObject(FXOMElement fxomInstance, Object newValue) {
//        assert newValue instanceof Object[];
//        Object[] values = (Object[])newValue;
//
//        assert values.length == getProperties().length;
//
//        for (int i=0; i<getProperties().length; i++) {
//            Object value = values[i];
//            ValuePropertyMetadata property = getProperties()[i];
//
//            assert property.getValueClass().isAssignableFrom(value.getClass());
//
//            property.setValueInSceneGraphObject(fxomInstance, value);
//        }
        assert newValue instanceof Map;
        Map<String, Object> values = (Map<String, Object>)newValue;

        assert values.size() == properties.size();

        for (Entry<String, ValuePropertyMetadata<VC>> entry : properties.entrySet()) {
            String propertyKey = entry.getKey();
            assert values.containsKey(propertyKey);
            Object value = values.get(propertyKey);
            ValuePropertyMetadata<VC> property = entry.getValue();

            assert property.getValueClass().isAssignableFrom(value.getClass());

            property.setValueInSceneGraphObject(fxomInstance, value);
        }

    }

    @Override
    public Object getValueObject(FXOMElement fxomInstance) {
//        Object[] values = new Object[getProperties().length];
//        for (int i=0; i<getProperties().length; i++) {
//            ValuePropertyMetadata property = getProperties()[i];
//            values[i] = property.getValueObject(fxomInstance);
//        }
//        return values;
        Map<String, Object> map = new HashMap<>();
        properties.entrySet().forEach(e -> map.put(e.getKey(), e.getValue().getValueObject(fxomInstance)));
        return map;
    }

    @Override
    public void setValueObject(FXOMElement fxomInstance, Object newValue) {
//        assert newValue instanceof Object[];
//        Object[] values = (Object[])newValue;
//
//        assert values.length == getProperties().length;
//
//        for (int i=0; i<getProperties().length; i++) {
//            Object value = values[i];
//            ValuePropertyMetadata property = getProperties()[i];
//
//            assert value == null || property.getValueClass().isAssignableFrom(value.getClass());
//
//            property.setValueObject(fxomInstance, value);
//        }
        assert newValue instanceof Map;
        Map<String, Object> values = (Map<String, Object>)newValue;

        assert values.size() == properties.size();

        for (Entry<String, ValuePropertyMetadata<VC>> entry : properties.entrySet()) {
            String propertyKey = entry.getKey();
            assert values.containsKey(propertyKey);
            Object value = values.get(propertyKey);
            ValuePropertyMetadata<VC> property = entry.getValue();

            assert value == null || property.getValueClass().isAssignableFrom(value.getClass());

            property.setValueObject(fxomInstance, value);
        }
    }

    @Override
    public Class<?> getValueClass() {
        boolean sameClassForAll = Arrays.stream(getProperties()).map(v -> v.getValueClass()).distinct().count() == 1;

        if (sameClassForAll) {
            return getProperties()[0].getValueClass();
        }
        return Object.class;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC> extends ValuePropertyMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

        private final Map<String, ValuePropertyMetadata<VC>> properties = new HashMap<>();

        public AbstractBuilder() {
            super();
            withGroup(true);
        }

        protected SELF withProperty(String key, ValuePropertyMetadata<VC> property) {
            properties.put(key, property);
            return self();
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, PropertyGroupMetadata<VC>, VC> {

        @Override
        public Builder<VC> withProperty(String key, ValuePropertyMetadata<VC> property) {
            return super.withProperty(key, property);
        }

        @Override
        public PropertyGroupMetadata<VC> build() {
            return new PropertyGroupMetadata<VC>(this);
        }

    }
}
