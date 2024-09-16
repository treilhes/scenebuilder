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
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.scene.Parent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

@Prototype
public class StageBuilder {

    private final JfxAppContext context;
    private final JavafxThreadClassloader classloader;
    private final ApplicationEvents events;
    private final ApplicationInstanceEvents instanceEvents;
    private final FxRobot robot;;

    private Class<? extends UiController> controller;
    private UiController controllerInstance;
    private String css;
    private Stage stage;
    private int width;
    private int height;

    private StageSetup stageSetup;
    private String fxml;
    private FXOMDocument document;
    private ToolStylesheetProvider toolStylesheetProvider;


    protected StageBuilder(JfxAppContext context, JavafxThreadClassloader classloader, ApplicationEvents events, ApplicationInstanceEvents instanceEvents) {
        this.context = context;
        this.classloader = classloader;
        this.events = events;
        this.instanceEvents = instanceEvents;
        this.robot = new FxRobot();
    }

    protected StageBuilder stage(Stage stage) {
        this.stage = stage;
        return this;
    }

    public StageBuilder controller(Class<? extends UiController> controller) {
        this.controller = controller;
        this.controllerInstance = null;
        return this;
    }

    public StageBuilder controller(UiController controllerInstance) {
        this.controllerInstance = controllerInstance;
        this.controller = controllerInstance.getClass();
        return this;
    }

    public StageBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public StageBuilder document(String fxml) {
        this.fxml = fxml;
        return this;
    }

    public StageBuilder document(FXOMDocument document) {
        this.document = document;
        return this;
    }

    public StageBuilder css(String css) {
        this.css = css;
        return this;
    }

    public StageBuilder css(ToolStylesheetProvider toolStylesheetProvider) {
        this.toolStylesheetProvider = toolStylesheetProvider;
        return this;
    }

    public StageBuilder setup(StageSetup stageSetup) {
        this.stageSetup = stageSetup;
        return this;
    }

    public <T extends UiController> T show() {

        robot.interact(() -> {
            classloader.addClassLoader(Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classloader);
        });

        UiController instance;

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
                int w = width == 0 ? 800 : width;
                int h = height == 0 ? 600 : height;
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
                events.stylesheetConfig().onNext(provider);
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
                    doc = new FXOMDocument(fxml);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Invalid fxml document", e);
                }
            }
            // create an hidden stage for the document scene graph
            if (doc != null) {
//                var hiddenStage = new Stage();
//                var pane = new Pane(doc.getFxomRoot().getSceneGraphObject().getAs(Node.class));
//                hiddenStage.hide();
//                hiddenStage.setScene(new Scene(pane));
                instanceEvents.fxomDocument().set(doc);
            }

        });

        return (T) instance;
    }

}
