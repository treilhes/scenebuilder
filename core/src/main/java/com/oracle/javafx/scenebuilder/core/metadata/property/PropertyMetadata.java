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

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

/**
 * A base class that represents a property metadata of an fxml component
 * 
 */
public abstract class PropertyMetadata implements Comparable<PropertyMetadata> {
    
    /** The property name. */
    private final PropertyName name;

    /** The property is a group of properties. */
    private final boolean group;
    
    /** The constants values of this property. */
    protected Map<String, Object> constants = new TreeMap<>();
    
    /**
     * Instantiates a new property metadata.
     *
     * @param name the name of the property
     */
    public PropertyMetadata(PropertyName name, boolean isGroup) {
        this.name = name;
        this.group = isGroup;
    }

    /**
     * Gets the name.
     *
     * @return the name
     */
    public PropertyName getName() {
        return name;
    }
    
    /**
     * Checks if this property is a group.
     *
     * @return true, if it is a group
     */
    public boolean isGroup() {
        return group;
    }

    /**
     * Get the list of constants associated with this metadata and their corresponding value
     * @return
     */
    public Map<String, Object> getConstants() {
        return constants;
    }
    
    /**
     * Add a constant for a specific instance and the corresponding value
     * @return
     */
    public PropertyMetadata addConstant(String key, Object value) {
        constants.put(key, value);
        return this;
    }
    
    /*
     * Comparable
     */
    @Override
    public int compareTo(PropertyMetadata o) {
        return this.name.compareTo(o.name);
    }

    /*
     * Object
     */
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PropertyMetadata other = (PropertyMetadata) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    
}
