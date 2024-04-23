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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;

import javafx.scene.control.ButtonType;

/**
 *
 *
 */
public class ButtonTypePropertyMetadata<VC> extends ComplexPropertyMetadata<ButtonType,VC> {

    private static Map<ButtonType, String> buttonTypeMap;

    public static synchronized Map<ButtonType, String> getButtonTypeMap() {
        if (buttonTypeMap == null) {
            buttonTypeMap = new HashMap<>();
            buttonTypeMap.put(ButtonType.APPLY, "APPLY"); // NOCHECK
            buttonTypeMap.put(ButtonType.CANCEL, "CANCEL"); // NOCHECK
            buttonTypeMap.put(ButtonType.CLOSE, "CLOSE"); // NOCHECK
            buttonTypeMap.put(ButtonType.FINISH, "FINISH"); // NOCHECK
            buttonTypeMap.put(ButtonType.NEXT, "NEXT"); // NOCHECK
            buttonTypeMap.put(ButtonType.NO, "NO"); // NOCHECK
            buttonTypeMap.put(ButtonType.OK, "OK"); // NOCHECK
            buttonTypeMap.put(ButtonType.PREVIOUS, "PREVIOUS"); // NOCHECK
            buttonTypeMap.put(ButtonType.YES, "YES"); // NOCHECK
            buttonTypeMap = Collections.unmodifiableMap(buttonTypeMap);
        }

        return buttonTypeMap;
    }

//    protected ButtonTypePropertyMetadata(PropertyName name, boolean readWrite, ButtonType defaultValue,
//            InspectorPath inspectorPath) {
//        super(name, ButtonType.class, readWrite, defaultValue, inspectorPath);
//    }

    protected ButtonTypePropertyMetadata(AbstractBuilder<?, ?,VC> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */
    @Override
    public FXOMInstance makeFxomInstanceFromValue(ButtonType value, FXOMDocument fxomDocument) {
        final FXOMInstance result;

        final String buttonName = getButtonTypeMap().get(value);
        if (buttonName != null) {
            // It's a standard button type
            result = new FXOMInstance(fxomDocument, ButtonType.class);
            result.setFxConstant(buttonName);
        } else {
            // Emergency code
            assert false;
            result = new FXOMInstance(fxomDocument, ButtonType.class);
            result.setFxConstant(getButtonTypeMap().get(ButtonType.APPLY));
        }

        return result;
    }

    protected static abstract class AbstractBuilder<SELF, TOBUILD,VC>
            extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, ButtonType,VC> {
        public AbstractBuilder() {
            super();
            withValueClass(ButtonType.class);
        }
    }

    public static final class Builder<VC> extends AbstractBuilder<Builder<VC>, ButtonTypePropertyMetadata<VC>,VC> {
        @Override
        public ButtonTypePropertyMetadata<VC> build() {
            return new ButtonTypePropertyMetadata<VC>(this);
        }
    }
}
