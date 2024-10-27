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
package com.gluonhq.jfxapps.core.fs;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;

/**
 * Default implementation of the {@link FXOMDocumentFactory} interface.
 * The main goal of this class is to ensure each document is created on the JavaFX Thread.
 * Why ? Because controls like WebView or WebEngine must be created on the JavaFX Thread.
 * Until a better solution is found, this class is the best way to ensure the document is created on the JavaFX Thread.
 */
@ApplicationSingleton
public class DefaultDocumentFactory implements FXOMDocumentFactory {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDocumentFactory.class);
    private final JfxAppPlatform platform;

    public DefaultDocumentFactory(JfxAppPlatform platform) {
        this.platform = platform;
    }

    @Override
    public FXOMDocument newDocument(FXOMDocumentFactory factory) {
        return FXOMDocumentFactory.DEFAULT.newDocument(this);
    }

    @Override
    public FXOMDocument newDocument(FXOMDocumentFactory factory, String fxmlText, URL location, ClassLoader classLoader,
            ResourceBundle resources, boolean normalize) throws IOException {
        try {
            return platform.callOnFxThreadWithActiveScope(() -> {
                return FXOMDocumentFactory.DEFAULT.newDocument(this, fxmlText, location, classLoader, resources, FXOMDocumentFactory.DEFAULT_NORMALIZE);
            }).get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Error creating new document", e);
        }
        return null;
    }
}
