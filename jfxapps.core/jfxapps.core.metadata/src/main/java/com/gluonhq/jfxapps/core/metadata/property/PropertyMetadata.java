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

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 * A base class that represents a property metadata of an fxml component
 *
 */
public abstract class PropertyMetadata<C> implements Comparable<PropertyMetadata<C>> {

    public enum Visibility {
        HIDDEN,
        STANDARD,
        EXPERT
    }


    /** The property name. */
    private final PropertyName name;

    /** The property is a group of properties. */
    private final boolean group;


    private Visibility visibility = Visibility.STANDARD;

    private C customization = null;

    /** The constants values of this property. */
    protected Map<String, Object> constants = new TreeMap<>();

//    /**
//     * Instantiates a new property metadata.
//     *
//     * @param name the name of the property
//     */
//    protected PropertyMetadata(PropertyName name, boolean isGroup) {
//        this.name = name;
//        this.group = isGroup;
//    }

    /**
     * Build a new property metadata.
     *
     * @param name the name of the property
     */
    protected PropertyMetadata(AbstractBuilder<?, ?, C> builder) {
        this.name = builder.name;
        this.group = builder.group;
        this.visibility = builder.visibility;
        this.customization = builder.customization;
        this.constants.putAll(builder.constants);

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

    public Visibility getVisibility() {
        return visibility;
    }

    public C getCustomization() {
        return customization;
    }

    /**
     * Get the list of constants associated with this metadata and their corresponding value
     * @return
     */
    public Map<String, Object> getConstants() {
        return Collections.unmodifiableMap(constants);
    }

    /*
     * Comparable
     */
    @Override
    public int compareTo(PropertyMetadata<C> o) {
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
        final PropertyMetadata<?> other = (PropertyMetadata<?>) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, CUSTO> {
        /** The property name. */
        protected PropertyName name;

        /** The property is a group of properties. */
        protected boolean group;

        protected Visibility visibility = Visibility.STANDARD;

        protected CUSTO customization = null;

        /** The constants values of this property. */
        protected Map<String, Object> constants = new TreeMap<>();

        @SuppressWarnings("unchecked")
        protected SELF self() {
            return (SELF)this;
        }
        public SELF withName(PropertyName name) {
            this.name = name;
            return self();
        }

        public SELF withVisibility(Visibility visibility) {
            this.visibility = visibility;
            return self();
        }

        protected SELF withGroup(boolean group) {
            this.group = group;
            return self();
        }

        protected SELF withConstant(String constantName, Object constantValue) {
            this.constants.put(constantName, constantValue);
            return self();
        }

        protected SELF withConstants(Map<String, Object> constants) {
            this.constants.putAll(constants);
            return self();
        }

        public SELF withCustomization(CUSTO customization) {
            this.customization = customization;
            return self();
        }

        public abstract TOBUILD build();
    }
}
