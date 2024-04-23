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

import java.util.HashSet;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;

/**
 * This class describes a property used as a placeholder for other component
 *
 */
public class ComponentPropertyMetadata<PC> extends PropertyMetadata<PC> {

    /** The class metadata of the owner component. */
    private final ComponentClassMetadata<?, ?, PC, ?> classMetadata;

    /** Does this placeholder accept a collection of components. */
    private final boolean collection;

    /** true if the component deserves a resizing while used as top element of the layout. default: false */
    private boolean main = false;

    private Set<PropertyName> disabledChildProperties = new HashSet<>();

    protected ComponentPropertyMetadata(AbstractBuilder<?, ?, PC> builder) {
        super(builder);
        this.classMetadata = builder.classMetadata;
        this.collection = builder.collection;
        this.disabledChildProperties.addAll(builder.disabledChildProperties);
        this.main = builder.main;
    }

    /**
     * Gets the the owner component metadata.
     *
     * @return the owner component metadata
     */
    public ComponentClassMetadata<?, ?, PC, ?> getClassMetadata() {
        return classMetadata;
    }

    /**
     * Checks if if it accepts a collection of components or only one.
     *
     * @return true, if is accepts a collection
     */
    public boolean isCollection() {
        return collection;
    }

    public boolean isMain() {
        return main;
    }

    public ComponentPropertyMetadata<PC> disableChildProperty(PropertyName propertyName) {
        if (!disabledChildProperties.contains(propertyName)) {
            disabledChildProperties.add(propertyName);
        }
        return this;
    }

    public boolean isChildPropertyDisabled(PropertyName propertyName) {
        return disabledChildProperties.contains(propertyName);
    }

    public Set<PropertyName> getDisabledProperties() {
        return disabledChildProperties;
    }

    @Override
    public int hashCode() { // To please FindBugs
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) { // To please FindBugs
        if (obj == null) {
            return false;
        }
        if (PropertyMetadata.class != obj.getClass()) {
            return false;
        }

        return super.equals(obj);
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, PC>
            extends PropertyMetadata.AbstractBuilder<SELF, TOBUILD, PC> {
        /** The class metadata of the owner component. */
        protected ComponentClassMetadata<?, ?, PC, ?> classMetadata;

        /** Does this placeholder accept a collection of components. */
        protected boolean collection;

        /** true if the component deserves a resizing while used as top element of the layout. default: false */
        protected boolean main = false;

        protected Set<PropertyName> disabledChildProperties = new HashSet<>();

        public AbstractBuilder() {
            super();
        }

        protected SELF withClassMetadata(ComponentClassMetadata<?, ?, PC, ?> classMetadata) {
            this.classMetadata = classMetadata;
            return self();
        }

        protected SELF withIsCollection(boolean isCollection) {
            this.collection = isCollection;
            return self();
        }

        protected SELF withIsMain(boolean isMain) {
            this.main = isMain;
            return self();
        }

        protected SELF withDisableChildProperties(Set<PropertyName> disabledPropertyNames) {
            this.disabledChildProperties.addAll(disabledPropertyNames);
            return self();
        }
    }

    public static final class Builder<PC> extends AbstractBuilder<Builder<PC>, ComponentPropertyMetadata<PC>, PC> {

        @Override
        public Builder<PC> withClassMetadata(ComponentClassMetadata<?, ?, PC, ?> classMetadata) {
            return super.withClassMetadata(classMetadata);
        }

        @Override
        public Builder<PC> withIsCollection(boolean isCollection) {
            return super.withIsCollection(isCollection);
        }

        @Override
        public Builder<PC> withIsMain(boolean isMain) {
            return super.withIsMain(isMain);
        }
        @Override
        public Builder<PC> withDisableChildProperties(Set<PropertyName> disabledPropertyNames) {
            return super.withDisableChildProperties(disabledPropertyNames);
        }

        @Override
        public ComponentPropertyMetadata<PC> build() {
            return new ComponentPropertyMetadata<PC>(this);
        }
    }
}
