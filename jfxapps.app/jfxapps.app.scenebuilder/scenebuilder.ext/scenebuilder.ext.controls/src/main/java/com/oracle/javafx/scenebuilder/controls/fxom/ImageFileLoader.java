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
package com.oracle.javafx.scenebuilder.controls.fxom;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.ext.FileLoader;
import com.gluonhq.jfxapps.core.fxom.util.DesignImage;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ImagePropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.javafx.scene.image.ImageViewMetadata;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ImageFileLoader implements FileLoader {

    @Override
    public boolean canLoad(File file) {
        try {
            return new Image(file.toURI().toURL().toString()).isError() == false;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException {
        FXOMObject result = null;
        final String fileURL = file.toURI().toURL().toString();
        final Image image = new Image(fileURL);

        if (image.isError() == false) {
            final FXOMDocument transientDoc = makeFxomDocumentFromImageURL(image, 200.0);
            result = transientDoc.getFxomRoot();
            if (result != null) {
                result.moveToFxomDocument(targetDocument);
            }
        }
        return result;
    }

    private static FXOMDocument makeFxomDocumentFromImageURL(Image image, double fitSize) throws IOException {

        assert image != null;
        assert fitSize > 0.0;

        final double imageWidth = image.getWidth();
        final double imageHeight = image.getHeight();

        final double fitWidth, fitHeight;
        final double imageSize = Math.max(imageWidth, imageHeight);
        if (imageSize < fitSize) {
            fitWidth = 0;
            fitHeight = 0;
        } else {
            final double widthScale = fitSize / imageSize;
            final double heightScale = fitSize / imageHeight;
            final double scale = Math.min(widthScale, heightScale);
            fitWidth = Math.floor(imageWidth * scale);
            fitHeight = Math.floor(imageHeight * scale);
        }

        return makeFxomDocumentFromImageURL(image, fitWidth, fitHeight);
    }

    private static FXOMDocument makeFxomDocumentFromImageURL(Image image, double fitWidth, double fitHeight) {

        final FXOMDocument result = new FXOMDocument();
        final FXOMInstance imageView = new FXOMInstance(result, ImageView.class);

        final ImagePropertyMetadata imageMeta = ImageViewMetadata.imagePropertyMetadata;
        final DoublePropertyMetadata fitWidthMeta = ImageViewMetadata.fitWidthPropertyMetadata;
        final DoublePropertyMetadata fitHeightMeta = ImageViewMetadata.fitHeightPropertyMetadata;

        imageMeta.setValue(imageView, new DesignImage(image));
        fitWidthMeta.setValue(imageView, fitWidth);
        fitHeightMeta.setValue(imageView, fitHeight);

        result.setFxomRoot(imageView);

        return result;
    }
}
