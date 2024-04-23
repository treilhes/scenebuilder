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
package com.gluonhq.jfxapps.metadata.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder(value = { "descriptor", "componentProperties", "valueProperties", "staticValueProperties" })
public class Component<CC, CPC, VPC> {

    @JsonProperty("class")
    @JsonInclude(Include.NON_NULL)
    private Descriptor<CC> descriptor;
    @JsonProperty("component")
    @JsonInclude(Include.NON_NULL)
    private Map<String, ComponentProperty<CPC>> componentProperties;
    @JsonProperty("property")
    @JsonInclude(Include.NON_NULL)
    private Map<String, ValueProperty<VPC>> valueProperties;
    @JsonProperty("static")
    @JsonInclude(Include.NON_NULL)
    private Map<String, ValueProperty<VPC>> staticValueProperties;

    public Component() {
        valueProperties = new HashMap<>();
        componentProperties = new HashMap<>();
        staticValueProperties = new HashMap<>();
    }

    public Descriptor<CC> getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(Descriptor<CC> descriptor) {
        this.descriptor = descriptor;
    }

    public Map<String, ValueProperty<VPC>> getValueProperties() {
        return valueProperties;
    }

    public void setValueProperties(Map<String, ValueProperty<VPC>> valueProperties) {
        this.valueProperties = valueProperties;
    }

    public Map<String, ComponentProperty<CPC>> getComponentProperties() {
        return componentProperties;
    }

    public void setComponentProperties(Map<String, ComponentProperty<CPC>> componentProperties) {
        this.componentProperties = componentProperties;
    }

    public Map<String, ValueProperty<VPC>> getStaticValueProperties() {
        return staticValueProperties;
    }

    public void setStaticValueProperties(Map<String, ValueProperty<VPC>> staticValueProperties) {
        this.staticValueProperties = staticValueProperties;
    }

    public static <CC, CPC, VPC> Builder<CC, CPC, VPC> builder() {
        return new Builder<CC, CPC, VPC>();
    }

    public static class Builder<CC, CPC, VPC> {
        private Descriptor<CC> descriptor;
        private Map<String, ComponentProperty<CPC>> componentProperties;
        private Map<String, ValueProperty<VPC>> valueProperties;
        private Map<String, ValueProperty<VPC>> staticValueProperties;

        public Builder<CC, CPC, VPC> descriptor(Descriptor<CC> descriptor) {
            this.descriptor = descriptor;
            return this;
        }

        public Builder<CC, CPC, VPC> componentProperties(Map<String, ComponentProperty<CPC>> componentProperties) {
            this.componentProperties = componentProperties;
            return this;
        }

        public Builder<CC, CPC, VPC> valueProperties(Map<String, ValueProperty<VPC>> valueProperties) {
            this.valueProperties = valueProperties;
            return this;
        }

        public Builder<CC, CPC, VPC> staticValueProperties(Map<String, ValueProperty<VPC>> staticValueProperties) {
            this.staticValueProperties = staticValueProperties;
            return this;
        }

        public Builder<CC, CPC, VPC> componentProperty(String key, ComponentProperty<CPC> property) {
            if (componentProperties == null) {
                componentProperties = new HashMap<>();
            }
            this.componentProperties.put(key, property);
            return this;
        }

        public Builder<CC, CPC, VPC> valueProperty(String key, ValueProperty<VPC> property) {
            if (valueProperties == null) {
                valueProperties = new HashMap<>();
            }
            this.valueProperties.put(key, property);
            return this;
        }

        public Builder<CC, CPC, VPC> staticProperty(String key, ValueProperty<VPC> property) {
            if (staticValueProperties == null) {
                staticValueProperties = new HashMap<>();
            }
            this.staticValueProperties.put(key, property);
            return this;
        }

        public Component<CC, CPC, VPC> build() {
            Component<CC, CPC, VPC> component = new Component<>();
            component.setDescriptor(descriptor);
            component.setComponentProperties(componentProperties);
            component.setValueProperties(valueProperties);
            component.setStaticValueProperties(staticValueProperties);
            return component;
        }
    }
}
