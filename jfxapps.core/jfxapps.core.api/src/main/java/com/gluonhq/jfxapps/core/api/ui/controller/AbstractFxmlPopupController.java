/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.api.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;

/**
 *
 *
 */
public abstract class AbstractFxmlPopupController extends AbstractPopupController implements FxmlController {

    private final URL fxmlURL;
    private final ResourceBundle resources;
    private final I18N i18n;

    public AbstractFxmlPopupController(I18N i18n, ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager, URL fxmlURL) {
        this(i18n, scenebuilderManager, documentManager, fxmlURL, null);
    };

    //@formatter:off
    public AbstractFxmlPopupController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            URL fxmlURL,
            ResourceBundle resources) {
        // @formatter:on
        super(i18n, scenebuilderManager, documentManager, fxmlURL);
        assert fxmlURL != null : "Check fxml path given to " + getClass().getSimpleName();
        this.i18n = i18n;
        this.fxmlURL = fxmlURL;
        this.resources = resources;
    }

    @Override
    public URL getFxmlURL() {
        return fxmlURL;
    }

    // FIXME: use i18n instead of resources
    @Override
    public ResourceBundle getResources() {
        return resources;
    }

    /**
     * Returns the I18N property. This property is bound to the I18N instance of the
     * application to allow i18n expression binding using
     * ${controller.i18n.some.key}
     *
     * @return
     */
    @Override
    public I18N i18nProperty() {
        return i18n;
    }
    /*
     * To be implemented by subclasses
     */

    @Override
    public void controllerDidLoadFxml() {
        assert getRoot() != null;
        assert getRoot().getScene() == null;
    }

}
