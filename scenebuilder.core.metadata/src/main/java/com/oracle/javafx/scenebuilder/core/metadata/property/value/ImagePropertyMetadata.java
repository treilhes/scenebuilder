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
package com.oracle.javafx.scenebuilder.core.metadata.property.value;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.DesignImage;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.DoublePropertyMetadata.SizeDoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.image.Image;

/**
 *
 * 
 */
public class ImagePropertyMetadata extends ComplexPropertyMetadata<DesignImage> {

    private final I18nStringPropertyMetadata urlMetadata = new I18nStringPropertyMetadata.Builder()
            .withName(new PropertyName("url"))
            .withReadWrite(true)
            .withDefaultValue(null)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata requestedWidthMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("requestedWidth"))
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final SizeDoublePropertyMetadata requestedHeightMetadata = new SizeDoublePropertyMetadata.Builder()
            .withName(new PropertyName("requestedHeight"))
            .withReadWrite(true)
            .withDefaultValue(0.0)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata preserveRatioMetadata = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("preserveRatio"))
            .withReadWrite(true)
            .withDefaultValue(false)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata smoothMetadata = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("smooth"))
            .withReadWrite(true)
            .withDefaultValue(false)
            .withInspectorPath(InspectorPath.UNUSED).build();

    private final BooleanPropertyMetadata backgroundLoading = new BooleanPropertyMetadata.Builder()
            .withName(new PropertyName("backgroundLoading"))
            .withReadWrite(true)
            .withDefaultValue(false)
            .withInspectorPath(InspectorPath.UNUSED).build();
    
    protected ImagePropertyMetadata(PropertyName name, boolean readWrite, 
            DesignImage defaultValue, InspectorPath inspectorPath) {
        super(name, DesignImage.class, readWrite, defaultValue, inspectorPath);
    }
    
    protected ImagePropertyMetadata(AbstractBuilder<?,?> builder) {
        super(builder);
    }

    /*
     * ComplexPropertyMetadata
     */
    
    @Override
    public FXOMInstance makeFxomInstanceFromValue(DesignImage value, FXOMDocument fxomDocument) {
        final FXOMInstance result = new FXOMInstance(fxomDocument, Image.class);
        
        urlMetadata.setValue(result, value.getLocation());
        requestedWidthMetadata.setValue(result, value.getImage().getRequestedWidth());
        requestedHeightMetadata.setValue(result, value.getImage().getRequestedHeight());
        preserveRatioMetadata.setValue(result, value.getImage().isPreserveRatio());
        smoothMetadata.setValue(result, value.getImage().isSmooth());
        backgroundLoading.setValue(result, value.getImage().isBackgroundLoading());

        return result;
    }
    
    @Override
    public DesignImage makeValueFromFxomInstance(FXOMInstance valueFxomInstance) {
        final String location = urlMetadata.getValue(valueFxomInstance);
        final Image image = (Image)valueFxomInstance.getSceneGraphObject();
        return new DesignImage(image, location);
    }
    
    protected static abstract class AbstractBuilder<SELF, TOBUILD> extends ComplexPropertyMetadata.AbstractBuilder<SELF, TOBUILD, DesignImage> {
    }
    
    public static final class Builder extends AbstractBuilder<Builder, ImagePropertyMetadata> {
        @Override
        public ImagePropertyMetadata build() {
            return new ImagePropertyMetadata(this);
        }
    }
}
