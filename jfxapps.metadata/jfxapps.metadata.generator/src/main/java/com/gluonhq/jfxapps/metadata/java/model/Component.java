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
package com.gluonhq.jfxapps.metadata.java.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.gluonhq.jfxapps.metadata.properties.model.ComponentBase;

@JsonDeserialize(using = Component.Deserializer.class)
public class Component<CC, CPC, VPC> extends ComponentBase<CC, CPC, ComponentProperty<CPC>, VPC, ValueProperty<VPC>>
        implements Comparable<Component<CC, CPC, VPC>> {

    private Component<CC, CPC, VPC> parent;

    private String metadataClassName;
    private String metadataClassSimpleName;
    private String metadataClassPackage;

    private Map<Class<?>, String> componentDependencies;

    @JsonIgnore
    private Map<String, ValueProperty<VPC>> updatedValueProperties;

    public Component() {
        super();
        updatedValueProperties = new HashMap<>();
        componentDependencies = new HashMap<>();
    }

    public Component<CC, CPC, VPC> getParent() {
        return parent;
    }

    public void setParent(Component<CC, CPC, VPC> parent) {
        this.parent = parent;
    }

    public String getMetadataClassName() {
        return metadataClassName;
    }

    public void setMetadataClassName(String metadataClassName) {
        this.metadataClassName = metadataClassName;
    }

    public String getMetadataClassSimpleName() {
        return metadataClassSimpleName;
    }

    public void setMetadataClassSimpleName(String metadataClassSimpleName) {
        this.metadataClassSimpleName = metadataClassSimpleName;
    }

    public String getMetadataClassPackage() {
        return metadataClassPackage;
    }

    public void setMetadataClassPackage(String metadataClassPackage) {
        this.metadataClassPackage = metadataClassPackage;
    }

    public Map<String, ValueProperty<VPC>> getUpdatedValueProperties() {
        return updatedValueProperties;
    }

    public void setUpdatedValueProperties(Map<String, ValueProperty<VPC>> updatedValueProperties) {
        this.updatedValueProperties = updatedValueProperties;
    }

    public Map<Class<?>, String> getComponentDependencies() {
        return componentDependencies;
    }

    public void setComponentDependencies(Map<Class<?>, String> componentDependencies) {
        this.componentDependencies = componentDependencies;
    }

    @Override
    public int compareTo(Component<CC, CPC, VPC> o) {
        Comparator<Component<CC, CPC, VPC>> comparator = Comparator
                .comparing((Component<CC, CPC, VPC> c) -> c.getMetadata().getType().getSimpleName())
                .thenComparing((Component<CC, CPC, VPC> c) -> c.getMetadata().getType().getName());
        return comparator.compare(this, o);
    }

    public static class Deserializer
            extends AbstractDeserializer<Component<?, ?, ?>, ComponentProperty<?>, ValueProperty<?>> {

        public Deserializer() {
            super((Class<ComponentProperty<?>>)(Class<?>)ComponentProperty.class, (Class<ValueProperty<?>>)(Class<?>)ValueProperty.class);
        }

        @Override
        public AbstractDeserializer<?, ?, ?> newInstance() {
            return new Deserializer();
        }

    }
}