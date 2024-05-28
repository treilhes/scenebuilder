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
package com.oracle.javafx.scenebuilder.core.metadata.fx;

import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.fxom.FXOMInclude;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic.Type;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.CharsetStringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.ResourceStringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.SourceStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization.Qualifier;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentPropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;

@Component
public class IncludeMetadata extends ComponentClassMetadata<FXOMInclude, ComponentClassMetadataCustomization,
ComponentPropertyMetadataCustomization,
ValuePropertyMetadataCustomization> {

    private final SourceStringPropertyMetadata<ValuePropertyMetadataCustomization> sourceMetadata = new SourceStringPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
            .name(new PropertyName("source"))
            .readWrite(true)
            .defaultValue("")
            .customization(ValuePropertyMetadataCustomization.builder()
                    .inspectorPath(ValuePropertyMetadataCustomization.InspectorPath.builder()
                            .sectionTag("Properties")
                            .subSectionTag("Include FXML file")
                            .subSectionIndex(1)
                            .build())
                    .build())
            .fileUrlDetection(true)
            .build();

    private final ResourceStringPropertyMetadata<ValuePropertyMetadataCustomization> resourcesMetadata = new ResourceStringPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
            .name(new PropertyName("resources"))
            .readWrite(true)
            .defaultValue("")
            .customization(ValuePropertyMetadataCustomization.builder()
                    .inspectorPath(ValuePropertyMetadataCustomization.InspectorPath.builder()
                            .sectionTag("Properties")
                            .subSectionTag("Include FXML file")
                            .subSectionIndex(2)
                            .build())
                    .build())
            .fileUrlDetection(true)
            .build();

    private final CharsetStringPropertyMetadata<ValuePropertyMetadataCustomization> charsetMetadata = new CharsetStringPropertyMetadata.Builder<ValuePropertyMetadataCustomization>()
            .name(new PropertyName("charset"))
            .readWrite(true)
            .defaultValue("")
            .customization(ValuePropertyMetadataCustomization.builder()
                    .inspectorPath(ValuePropertyMetadataCustomization.InspectorPath.builder()
                            .sectionTag("Properties")
                            .subSectionTag("Include FXML file")
                            .subSectionIndex(3)
                            .build())
                    .build())
            .build();

    protected IncludeMetadata(IntrinsicMetadata parent) {
        super(FXOMInclude.class, parent, ComponentClassMetadataCustomization.builder()
                .qualifier("include", Qualifier.builder()
                        .applicabilityCheck((FXOMIntrinsic o) -> o.getType() == Type.FX_INCLUDE)
                        .label("default")
                        .description("")
                        .category("Fx")
                        .fxmlUrl(CopyMetadata.class.getResource("Include.fxml"))
                        .iconUrl(CopyMetadata.class.getResource("Include.png"))
                        .iconX2Url(CopyMetadata.class.getResource("Include@2x.png"))
                        .build())
                .build());

        getProperties().add(sourceMetadata);
        getProperties().add(resourcesMetadata);
        getProperties().add(charsetMetadata);
    }

    @Override
    public String getName() {
        return "fx:include";
    }
}