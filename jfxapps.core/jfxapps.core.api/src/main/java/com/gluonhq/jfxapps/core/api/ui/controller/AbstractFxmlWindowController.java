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
package com.gluonhq.jfxapps.core.api.ui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

/**
 *
 *
 */
public abstract class AbstractFxmlWindowController extends AbstractWindowController implements FxmlController {

    private final I18N i18n;
    private final URL fxmlURL;


    public AbstractFxmlWindowController(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            URL fxmlURL) {
        this(i18n, sceneBuilderManager, iconSetting, fxmlURL, null);
    }

    public AbstractFxmlWindowController(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            URL fxmlURL,
            boolean sizeToScene) {
        this(i18n, sceneBuilderManager, iconSetting, fxmlURL, null, sizeToScene);
    }

    public AbstractFxmlWindowController(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            URL fxmlURL,
            InstanceWindow owner) {
        super(sceneBuilderManager, iconSetting, owner);
        assert fxmlURL != null : "Check fxml path given to " + getClass().getSimpleName();
        this.fxmlURL = fxmlURL;
        this.i18n = i18n;
    }

    public AbstractFxmlWindowController(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            URL fxmlURL,
            InstanceWindow owner,
            boolean sizeToScene) {
        super(sceneBuilderManager, iconSetting, owner, sizeToScene);
        assert fxmlURL != null : "Check fxml path given to " + getClass().getSimpleName();
        this.fxmlURL = fxmlURL;
        this.i18n = i18n;
    }

    @Override
    public URL getFxmlURL() {
        return fxmlURL;
    }

    @Override
    public ResourceBundle getResources() {
        return i18n.getBundle();
    }

    protected I18N getI18n() {
        return i18n;
    }

    @Override
    public void controllerDidLoadFxml() {
        assert getRoot() != null;
        assert getRoot().getScene() == null;
    }

//    /*
//     * AbstractWindowController
//     */
//
//    /**
//     * This implementation loads the FXML file using the URL passed to
//     * {@link AbstractFxmlWindowController}.
//     */
//    @Override
//    protected void makeRoot() {
//        final FXMLLoader loader = new FXMLLoader();
//
//        loader.setController(this);
//        loader.setLocation(fxmlURL);
//        loader.setResources(resources);
//        try {
//            setRoot((Region)loader.load());
//            controllerDidLoadFxml();
//        } catch (RuntimeException | IOException x) {
//            System.out.println("loader.getController()=" + loader.getController());
//            System.out.println("loader.getLocation()=" + loader.getLocation());
//            throw new RuntimeException("Failed to load " + fxmlURL.getFile(), x); //NOCHECK
//        }
//    }
}
