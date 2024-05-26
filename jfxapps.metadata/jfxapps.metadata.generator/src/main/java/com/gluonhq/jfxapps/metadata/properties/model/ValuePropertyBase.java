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
package com.gluonhq.jfxapps.metadata.properties.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;

@JsonPropertyOrder(value = { "clazz", "customization" })
public class ValuePropertyBase<VPC> {

    @JsonIgnore
    private PropertyMetaData metadata;

    @JsonProperty("metadataClass")
    private Class<?> metadataClass;

    @JsonInclude(Include.NON_NULL)
    private String defaultValue;

    @JsonInclude(Include.NON_NULL)
    @JsonProperty("custo")
    private VPC customization;

    public PropertyMetaData getMetadata() {
        return metadata;
    }

    public void setMetadata(PropertyMetaData metadata) {
        this.metadata = metadata;
    }

    public Class<?> getMetadataClass() {
        return metadataClass;
    }
    public void setMetadataClass(Class<?> metadataClass) {
        this.metadataClass = metadataClass;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    public VPC getCustomization() {
        return customization;
    }
    public void setCustomization(VPC customization) {
        this.customization = customization;
    }

    public static class BuilderBase<VPC, CTB extends ValuePropertyBase<VPC>> {
        private Class<CTB> classToBuild;
        private Class<?> metadataClass;
        private String defaultValue;
        private VPC customization;

        public BuilderBase(Class<CTB> classToBuild) {
            super();
            this.classToBuild = classToBuild;
        }

        public BuilderBase<VPC, CTB> metadataClass(Class<?> clazz) {
            this.metadataClass = clazz;
            return this;
        }

        public BuilderBase<VPC, CTB> defaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public BuilderBase<VPC, CTB> customization(VPC customization) {
            this.customization = customization;
            return this;
        }

        public CTB build() {
            CTB valueProperty;
            try {
                valueProperty = classToBuild.getConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            valueProperty.setMetadataClass(metadataClass);
            valueProperty.setDefaultValue(defaultValue);
            valueProperty.setCustomization(customization);
            return valueProperty;
        }
    }
}
