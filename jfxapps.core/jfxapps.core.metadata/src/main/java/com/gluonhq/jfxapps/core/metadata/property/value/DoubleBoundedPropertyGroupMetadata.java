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
package com.gluonhq.jfxapps.core.metadata.property.value;

import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

public class DoubleBoundedPropertyGroupMetadata<VC> extends PropertyGroupMetadata<VC> {

//    public DoubleBoundedPropertyGroupMetadata(PropertyName name, ValuePropertyMetadata min, ValuePropertyMetadata value, ValuePropertyMetadata max) {
//        super(name, min, value, max);
//    }

    protected DoubleBoundedPropertyGroupMetadata(AbstractBuilder<?, ?, VC> builder) {
        super(builder);
    }

    public ValuePropertyMetadata<VC> getMinPropertyMetadata() {
        return getPropertiesMap().get("min");
    }

    public ValuePropertyMetadata<VC> getValuePropertyMetadata() {
        return getPropertiesMap().get("value");
    }

    public ValuePropertyMetadata<VC> getMaxPropertyMetadata() {
        return getPropertiesMap().get("max");
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD, VC>
            extends PropertyGroupMetadata.AbstractBuilder<SELF, TOBUILD, VC> {

        public SELF minProperty(ValuePropertyMetadata<VC> property) {
            return property("min", property);
        }

        public SELF valueProperty(ValuePropertyMetadata<VC> property) {
            return property("value", property);
        }

        public SELF maxProperty(ValuePropertyMetadata<VC> property) {
            return property("max", property);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, DoubleBoundedPropertyGroupMetadata<VC>, VC> {

        @Override
        public DoubleBoundedPropertyGroupMetadata<VC> build() {
            return new DoubleBoundedPropertyGroupMetadata<VC>(this);
        }

    }
}
