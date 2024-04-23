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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

public class ValuePropertyCusto {
    private int order;
    @JsonInclude(Include.NON_NULL)
    private String nullEquivalent;
    @JsonInclude(Include.NON_NULL)
    private String section;
    @JsonInclude(Include.NON_NULL)
    private String subSection;

    public int getOrder() {
        return order;
    }
    public void setOrder(int order) {
        this.order = order;
    }
    public String getNullEquivalent() {
        return nullEquivalent;
    }
    public void setNullEquivalent(String nullEquivalent) {
        this.nullEquivalent = nullEquivalent;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    public String getSubSection() {
        return subSection;
    }
    public void setSubSection(String subSection) {
        this.subSection = subSection;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private int order;
        private String nullEquivalent;
        private String section;
        private String subSection;

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder nullEquivalent(String nullEquivalent) {
            this.nullEquivalent = nullEquivalent;
            return this;
        }

        public Builder section(String section) {
            this.section = section;
            return this;
        }

        public Builder subSection(String subSection) {
            this.subSection = subSection;
            return this;
        }

        public ValuePropertyCusto build() {
            ValuePropertyCusto valueProperty = new ValuePropertyCusto();
            valueProperty.setOrder(order);
            valueProperty.setNullEquivalent(nullEquivalent);
            valueProperty.setSection(section);
            valueProperty.setSubSection(subSection);
            return valueProperty;
        }
    }
}