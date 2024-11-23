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
package com.gluonhq.jfxapps.test;

import java.util.concurrent.atomic.AtomicReference;

import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.UiController;
import com.gluonhq.jfxapps.core.api.javafx.internal.FxmlControllerBeanPostProcessor;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@Prototype
public class UiControllerBuilder<T extends UiController> {

    private final JfxAppContext context;
    private final JavafxThreadClassloader classloader;
    private final ApplicationEvents events;
    private final ApplicationInstanceEvents instanceEvents;
    private final FxRobot robot;;

    private Class<T> controller;
    private T controllerInstance;
    private String css;
    private Stage stage;
    private int width;
    private int height;

    private StageSetup stageSetup;
    private String fxml;
    private FXOMDocument document;
    private ToolStylesheetProvider toolStylesheetProvider;


    protected UiControllerBuilder(JfxAppContext context, JavafxThreadClassloader classloader, ApplicationEvents events, ApplicationInstanceEvents instanceEvents) {
        this.context = context;
        this.classloader = classloader;
        this.events = events;
        this.instanceEvents = instanceEvents;
        this.robot = new FxRobot();
    }

    protected UiControllerBuilder<T> stage(Stage stage) {
        this.stage = stage;
        return this;
    }

    public UiControllerBuilder<T> controller(Class<T> controller) {
        this.controller = controller;
        this.controllerInstance = null;
        return this;
    }

    public UiControllerBuilder<T> controller(T controllerInstance) {
        this.controllerInstance = controllerInstance;
        this.controller = (Class<T>)controllerInstance.getClass();
        return this;
    }

    public UiControllerBuilder<T> size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public UiControllerBuilder<T> document(String fxml) {
        this.fxml = fxml;
        return this;
    }

    public UiControllerBuilder<T> document(FXOMDocument document) {
        this.document = document;
        return this;
    }

    public UiControllerBuilder<T> css(String css) {
        this.css = css;
        return this;
    }

    public UiControllerBuilder<T> css(ToolStylesheetProvider toolStylesheetProvider) {
        this.toolStylesheetProvider = toolStylesheetProvider;
        return this;
    }

    public UiControllerBuilder<T> setup(StageSetup stageSetup) {
        this.stageSetup = stageSetup;
        return this;
    }

    public TestStage<T> show() {
        int w = width == 0 ? 800 : width;
        int h = height == 0 ? 600 : height;

        T instance;
        AtomicReference<FXOMDocument> docRef = new AtomicReference<>();

        robot.interact(() -> {
            classloader.addClassLoader(Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classloader);
        });

        if (controllerInstance != null) {
            instance = controllerInstance;
            new FxmlControllerBeanPostProcessor().postProcessAfterInitialization(instance, "controller");
        } else {
            instance = context.getBean(controller);
        }

        // add default theme class to controller
        instance.getRoot().getStyleClass().add("theme-presets");

        if (controller != null) {
            classloader.addClassLoader(controller.getClassLoader());
        }

        robot.interact(() -> {

            if (stageSetup != null) {
                Parent c = instance.getRoot() == null ? new Pane() : instance.getRoot();
                stageSetup.setup(stage, w, h, c);
            }

            var builder = ToolStylesheetProvider.builder();
            if (toolStylesheetProvider != null) {
                builder.userAgentStylesheet(toolStylesheetProvider.getUserAgentStylesheet());
                builder.stylesheets(toolStylesheetProvider.getStylesheets());
            }
            if (css != null) {
                String dataUri = URLUtils.toDataURI(css).toString();
                builder.stylesheet(dataUri);
            }

            var provider = builder.build();
            if (controller != null) {
                //use events
                events.stylesheetConfig().set(provider);
            } else {
                // use stage
                if (provider.getUserAgentStylesheet() != null) {
                    stage.getScene().getStylesheets().add(provider.getUserAgentStylesheet());
                }
                stage.getScene().getStylesheets().addAll(provider.getStylesheets());
            }

            FXOMDocument doc = null;
            if (document != null) {
                doc = document;
            } else if (fxml != null) {
                try {
                    doc = FXOMDocumentFactory.DEFAULT.newDocument(fxml);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid fxml document", e);
                }
            }
            docRef.set(doc);

            // create an hidden stage for the document scene graph if it is a node (needed for css handling)
            if (doc != null) {
                var sceneGraphObject = doc.getFxomRoot().getSceneGraphObject();

                if (sceneGraphObject.isInstanceOf(Node.class)) {
                    var node = sceneGraphObject.getAs(Node.class);
                    var hiddenStage = new Stage();
                    var pane = new Pane(node);
                    hiddenStage.hide();
                    hiddenStage.setScene(new Scene(pane));
                }

                instanceEvents.fxomDocument().set(doc);
            }

        });

        return new TestStage<>(stage, instance, docRef.get());
    }

}
