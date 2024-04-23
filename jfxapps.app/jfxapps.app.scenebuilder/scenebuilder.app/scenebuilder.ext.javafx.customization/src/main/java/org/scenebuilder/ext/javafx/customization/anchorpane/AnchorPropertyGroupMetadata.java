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
package org.scenebuilder.ext.javafx.customization.anchorpane;

import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

public class AnchorPropertyGroupMetadata extends PropertyGroupMetadata {

//    public AnchorPropertyGroupMetadata(PropertyName name, ValuePropertyMetadata topAnchorProperty,
//            ValuePropertyMetadata rightAnchorProperty, ValuePropertyMetadata bottomAnchorProperty,
//            ValuePropertyMetadata leftAnchorProperty) {
//        super(name, topAnchorProperty, rightAnchorProperty, bottomAnchorProperty, leftAnchorProperty);
//    }

    protected AnchorPropertyGroupMetadata(AbstractBuilder<?, ?> builder) {
        super(builder);
    }

    public ValuePropertyMetadata getTopAnchorPropertyPropertyMetadata() {
        return getPropertiesMap().get("topAnchor");
    }

    public ValuePropertyMetadata getRightAnchorPropertyPropertyMetadata() {
        return getPropertiesMap().get("rightAnchor");
    }

    public ValuePropertyMetadata getBottomAnchorPropertyPropertyMetadata() {
        return getPropertiesMap().get("bottomAnchor");
    }

    public ValuePropertyMetadata getLeftAnchorPropertyPropertyMetadata() {
        return getPropertiesMap().get("leftAnchor");
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD>
            extends PropertyGroupMetadata.AbstractBuilder<SELF, TOBUILD> {
        public SELF withTopAnchorProperty(ValuePropertyMetadata property) {
            return withProperty("topAnchor", property);
        }

        public SELF withRightAnchorProperty(ValuePropertyMetadata property) {
            return withProperty("rightAnchor", property);
        }

        public SELF withBottomAnchorProperty(ValuePropertyMetadata property) {
            return withProperty("bottomAnchor", property);
        }

        public SELF withLeftAnchorProperty(ValuePropertyMetadata property) {
            return withProperty("leftAnchor", property);
        }
    }

    public static final class Builder extends AbstractBuilder<Builder, AnchorPropertyGroupMetadata> {

        @Override
        public AnchorPropertyGroupMetadata build() {
            return new AnchorPropertyGroupMetadata(this);
        }

    }
}
