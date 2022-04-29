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

import com.oracle.javafx.scenebuilder.core.fxom.FXOMInclude;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic.Type;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata.Visibility;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.CharsetStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.ResourceStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.SourceStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

@Component
public class IncludeMetadata extends ComponentClassMetadata<FXOMInclude> {

    private final SourceStringPropertyMetadata sourceMetadata = new SourceStringPropertyMetadata.Builder()
            .withName(new PropertyName("source"))
            .withReadWrite(true)
            .withDefaultValue("")
            .withInspectorPath(new InspectorPath("Properties", "Include FXML file", 2))
            .withVisibility(Visibility.STANDARD)
            .withFileUrlDetection(true)
            .build();

    private final ResourceStringPropertyMetadata resourcesMetadata = new ResourceStringPropertyMetadata.Builder()
            .withName(new PropertyName("resources"))
            .withReadWrite(true)
            .withDefaultValue("")
            .withInspectorPath(new InspectorPath("Properties", "Include FXML file", 2))
            .withVisibility(Visibility.STANDARD)
            .withFileUrlDetection(true)
            .build();

    private final CharsetStringPropertyMetadata charsetMetadata = new CharsetStringPropertyMetadata.Builder()
            .withName(new PropertyName("charset"))
            .withReadWrite(true)
            .withDefaultValue("")
            .withInspectorPath(new InspectorPath("Properties", "Include FXML file", 2))
            .withVisibility(Visibility.STANDARD)
            .build();

    protected IncludeMetadata(IntrinsicMetadata parent) {
        super(FXOMInclude.class, parent);

        getProperties().add(sourceMetadata);
        getProperties().add(resourcesMetadata);
        getProperties().add(charsetMetadata);

        getQualifiers().put("include",
                new Qualifier(
                        getClass().getResource("Include.fxml"),
                        "default",
                        "",
                        getClass().getResource("Include.png"),
                        getClass().getResource("Include@2x.png"),
                        "Fx",
                        (FXOMIntrinsic o) -> o.getType() == Type.FX_INCLUDE
                        ));

    }

    @Override
    public String getName() {
        return "fx:include";
    }
}