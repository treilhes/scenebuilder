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
package com.oracle.javafx.scenebuilder.core.ui;

import java.net.URL;
import java.util.ResourceBundle;

import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.util.FxmlController;

import javafx.stage.Stage;

/**
 *
 *
 */
public abstract class AbstractFxmlWindowController extends AbstractWindowController implements FxmlController {

    private final URL fxmlURL;
    private final ResourceBundle resources;

    public AbstractFxmlWindowController(SceneBuilderManager sceneBuilderManager, URL fxmlURL, ResourceBundle resources) {
        this(sceneBuilderManager, fxmlURL, resources, null);
    }

    public AbstractFxmlWindowController(SceneBuilderManager sceneBuilderManager,URL fxmlURL, ResourceBundle resources, boolean sizeToScene) {
        this(sceneBuilderManager, fxmlURL, resources, null, sizeToScene);
    }

    public AbstractFxmlWindowController(SceneBuilderManager sceneBuilderManager,URL fxmlURL, ResourceBundle resources, Stage owner) {
        super(sceneBuilderManager, owner);
        assert fxmlURL != null : "Check fxml path given to " + getClass().getSimpleName();
        this.fxmlURL = fxmlURL;
        this.resources = resources;
    }

    public AbstractFxmlWindowController(SceneBuilderManager sceneBuilderManager,URL fxmlURL, ResourceBundle resources, Stage owner, boolean sizeToScene) {
        super(sceneBuilderManager, owner, sizeToScene);
        assert fxmlURL != null : "Check fxml path given to " + getClass().getSimpleName();
        this.fxmlURL = fxmlURL;
        this.resources = resources;
    }

    @Override
    public URL getFxmlURL() {
        return fxmlURL;
    }

    @Override
    public ResourceBundle getResources() {
        return resources;
    }

    /*
     * To be implemented by subclasses
     */

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
//            throw new RuntimeException("Failed to load " + fxmlURL.getFile(), x); //NOI18N
//        }
//    }
}