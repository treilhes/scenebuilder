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

import com.gluonhq.jfxapps.metadata.properties.model.ValuePropertyBase;

public class ValueProperty<VPC> extends ValuePropertyBase<VPC> implements Comparable<ValueProperty<VPC>> {

    private String memberName;
    private String defaultValue;
    private String nullEquivalent;

    public ValueProperty() {
        super();
    }

    @Override
    public String getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getNullEquivalent() {
        return nullEquivalent;
    }

    public void setNullEquivalent(String nullEquivalent) {
        this.nullEquivalent = nullEquivalent;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public int compareTo(ValueProperty<VPC> o) {
        Comparator<ValueProperty<VPC>> comparator = Comparator
                .comparing((ValueProperty<VPC> p) -> p.getMetadata().getName())
                .thenComparing(
                        (ValueProperty<VPC> p) -> p.getMetadata().getGetterMethod().getDeclaringClass() == null ? ""
                                : p.getMetadata().getGetterMethod().getDeclaringClass().getName())
                .thenComparing(p -> p.getMetadata().isStatic());
        return comparator.compare(this, o);
    }
}
