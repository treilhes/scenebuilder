/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.fxom.FXOMCopy;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic.Type;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.fx.defaults.NullReference;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata.Visibility;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;

@Component
public class CopyMetadata extends ComponentClassMetadata<FXOMCopy> {

    static {
        FXOMDocument.DEFAULT_NAMESPACE.put("sb_nullCopy", new NullReference());
    }

    private final I18nStringPropertyMetadata sourceMetadata = new I18nStringPropertyMetadata.Builder()
            .name(new PropertyName("source"))
            .readWrite(true)
            .defaultValue("sb_nullCopy")
            .inspectorPath(new InspectorPath("Properties", "Reference", 1))
            .visibility(Visibility.STANDARD)
            .build();

    protected CopyMetadata(IntrinsicMetadata parent) {
        super(FXOMCopy.class, parent);

        getProperties().add(sourceMetadata);

        getQualifiers().put("copy",
                new Qualifier(
                        getClass().getResource("Copy.fxml"),
                        "default",
                        "",
                        getClass().getResource("Copy.png"),
                        getClass().getResource("Copy@2x.png"),
                        "Fx",
                        (FXOMIntrinsic o) -> o.getType() == Type.FX_COPY
                        ));

    }

    @Override
    public String getName() {
        return "fx:copy";
    }
}