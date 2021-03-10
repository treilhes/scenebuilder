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
package com.oracle.javafx.scenebuilder.core.metadata.property;

import java.util.Arrays;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

public class PropertyGroupMetadata extends ValuePropertyMetadata {
    
    private final ValuePropertyMetadata[] properties;

    public PropertyGroupMetadata(PropertyName name, ValuePropertyMetadata... properties) {
        super(name, true, Arrays.stream(properties).anyMatch(p -> p.isReadWrite()), properties[0].getInspectorPath());
        this.properties = properties;
    }

    public ValuePropertyMetadata[] getProperties() {
        return properties;
    }

    @Override
    public Object getDefaultValueObject() {
        return Arrays.stream(properties)
                .map(it -> it.getDefaultValueObject())
                .collect(Collectors.toList())
                .toArray();
    }

    @Override
    public void setValueInSceneGraphObject(FXOMInstance fxomInstance, Object newValue) {
        assert newValue instanceof Object[];
        Object[] values = (Object[])newValue;
        
        assert values.length == getProperties().length;
        
        for (int i=0; i<getProperties().length; i++) {
            Object value = values[i];
            ValuePropertyMetadata property = getProperties()[i];
            
            assert property.getValueClass().isAssignableFrom(value.getClass());
            
            property.setValueInSceneGraphObject(fxomInstance, value);
        }
        
    }

    @Override
    public Object getValueObject(FXOMInstance fxomInstance) {
        Object[] values = new Object[getProperties().length];
        for (int i=0; i<getProperties().length; i++) {
            ValuePropertyMetadata property = getProperties()[i];
            values[i] = property.getValueObject(fxomInstance);
        }
        return values;
    }

    @Override
    public void setValueObject(FXOMInstance fxomInstance, Object newValue) {
        assert newValue instanceof Object[];
        Object[] values = (Object[])newValue;
        
        assert values.length == getProperties().length;
        
        for (int i=0; i<getProperties().length; i++) {
            Object value = values[i];
            ValuePropertyMetadata property = getProperties()[i];
            
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
}