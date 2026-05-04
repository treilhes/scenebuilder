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
import java.net.URL;

import com.treilhes.jfxplace.core.fxom.FXOMDocument;
import com.treilhes.jfxplace.core.fxom.FXOMInstance;
import com.treilhes.jfxplace.core.fxom.FXOMObject;
import com.treilhes.jfxplace.core.fxom.FXOMPropertyC;
import com.treilhes.jfxplace.core.fxom.FXOMPropertyT;
import com.treilhes.jfxplace.core.fxom.ext.FileLoader;
import com.treilhes.jfxplace.core.fxom.util.PropertyName;

import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MediaFileLoader implements FileLoader {

    @Override
    public boolean canLoad(File file) {
        try {
            return new Media(file.toURI().toURL().toString()).getError() == null;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @Override
    public FXOMObject loadInto(FXOMDocument targetDocument, File file) throws IOException {
        FXOMObject result = null;
        final String fileURL = file.toURI().toURL().toString();

        try {
            final Media media = new Media(fileURL);
            if (media.getError() == null) {
                final FXOMDocument transientDoc = makeFxomDocumentFromMedia(targetDocument, media, 200.0);
                result = transientDoc.getFxomRoot();
                if (result != null) {
                    result.moveToFxomDocument(targetDocument);
                }
            } else {
                throw new IOException(media.getError());
            }
        } catch (MediaException x) {
            throw new IOException(x);
        }

        return result;
    }

    private static FXOMDocument makeFxomDocumentFromMedia(FXOMDocument target, Media media, double fitSize) throws IOException {

        assert media != null;
        assert fitSize > 0.0;

        final double mediaWidth = media.getWidth();
        final double mediaHeight = media.getHeight();

        final double fitWidth, fitHeight;
        final double mediaSize = Math.max(mediaWidth, mediaHeight);
        if (mediaSize < fitSize) {
            fitWidth = 0;
            fitHeight = 0;
        } else {
            final double widthScale = fitSize / mediaSize;
            final double heightScale = fitSize / mediaHeight;
            final double scale = Math.min(widthScale, heightScale);
            fitWidth = Math.floor(mediaWidth * scale);
            fitHeight = Math.floor(mediaHeight * scale);
        }

        return makeFxomDocumentFromMedia(target, media, fitWidth, fitHeight);
    }

    private static FXOMDocument makeFxomDocumentFromMedia(FXOMDocument target, Media media, double fitWidth, double fitHeight) {

        /*
         * <MediaView fitWidth="200" fitHeight="2003 > <mediaPlayer> <MediaPlayer
         * cycleCount="-1"> <media> <Media> <source> <URL
         * value="file:/Users/elp/Dekstop/blah.flv" /> </source> <Media/> </media>
         * </MediaPlayer> </mediaPlayer> </MediaView>
         */

        final FXOMDocument result = target.getFactory().newDocument();

        /*
         * URL
         */
        final PropertyName valueName = new PropertyName("value"); // NOCHECK
        final FXOMPropertyT valueProperty = new FXOMPropertyT(result, valueName, media.getSource());
        final FXOMInstance urlInstance = new FXOMInstance(result, URL.class);
        valueProperty.addToParentInstance(-1, urlInstance);

        /*
         * Media
         */
        final PropertyName sourceName = new PropertyName("source"); // NOCHECK
        final FXOMPropertyC sourceProperty = new FXOMPropertyC(result, sourceName, urlInstance);
        final FXOMInstance mediaInstance = new FXOMInstance(result, Media.class);
        sourceProperty.addToParentInstance(-1, mediaInstance);

        /*
         * MediaPlayer
         */
        final PropertyName mediaName = new PropertyName("media"); // NOCHECK
        final FXOMPropertyC mediaProperty = new FXOMPropertyC(result, mediaName, mediaInstance);
        final FXOMInstance mediaPlayerInstance = new FXOMInstance(result, MediaPlayer.class);
        mediaProperty.addToParentInstance(-1, mediaPlayerInstance);

        /*
         * MediaView
         */
        final PropertyName mediaPlayerName = new PropertyName("mediaPlayer"); // NOCHECK
        final FXOMPropertyC mediaPlayerProperty = new FXOMPropertyC(result, mediaPlayerName, mediaPlayerInstance);
        final PropertyName fitWidthName = new PropertyName("fitWidth"); // NOCHECK
        final FXOMPropertyT fitWidthProperty = new FXOMPropertyT(result, fitWidthName, String.valueOf(fitWidth));
        final PropertyName fitHeightName = new PropertyName("fitHeight"); // NOCHECK
        final FXOMPropertyT fitHeightProperty = new FXOMPropertyT(result, fitHeightName, String.valueOf(fitHeight));
        final FXOMInstance mediaView = new FXOMInstance(result, MediaView.class);
        mediaPlayerProperty.addToParentInstance(-1, mediaView);
        fitWidthProperty.addToParentInstance(-1, mediaView);
        fitHeightProperty.addToParentInstance(-1, mediaView);

        result.setFxomRoot(mediaView);

        return result;
    }

}
