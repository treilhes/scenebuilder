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
package com.gluonhq.jfxapps.test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.application.ApplicationClassloader;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.UiController;
import com.gluonhq.jfxapps.core.api.javafx.internal.FxmlControllerBeanPostProcessor;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

@Prototype
public class UiControllerBuilder<T extends UiController> {

    private final JfxAppContext context;
    private final ApplicationClassloader classloader;
    private final ApplicationEvents events;
    private final FxomEvents fxomEvents;
    private final FxRobot robot;

    private Class<T> controller;
    private T controllerInstance;
    private String css;
    private String i18n;
    private Stage stage;
    private int width;
    private int height;

    private StageSetup stageSetup;
    private String fxml;
    private FXOMDocument document;
    private ToolStylesheetProvider toolStylesheetProvider;
    private List<URL> cssUrl = new ArrayList<>();
    private List<URL> i18nUrl = new ArrayList<>();

    protected UiControllerBuilder(JfxAppContext context, ApplicationClassloader classloader, ApplicationEvents events, FxomEvents instanceEvents) {
        this.context = context;
        this.classloader = classloader;
        this.events = events;
        this.fxomEvents = instanceEvents;
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

    public UiControllerBuilder<T> css(URL cssUrl) {
        this.cssUrl.add(cssUrl);
        return this;
    }

    public UiControllerBuilder<T> i18n(URL i18nUrl) {
        this.i18nUrl.add(i18nUrl);
        return this;
    }

    public UiControllerBuilder<T> i18n(String i18n) {
        this.i18n = i18n;
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
        var w = width == 0 ? 800 : width;
        var h = height == 0 ? 600 : height;

        T instance;
        var docRef = new AtomicReference<FXOMDocument>();

        robot.interact(() -> {
            classloader.putClassLoader(UiControllerBuilder.class.getName() + "_context", Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classloader);
        });

        var i18nInstance = context.getBean(I18N.class);

        if (i18n != null) {
            i18nInstance.addBundleProvider(() -> new PropertyResourceBundle(new ByteArrayInputStream(i18n.getBytes())));
        }

        if (i18nUrl != null) {
            for (var url : i18nUrl) {
                final File file;
                try {
                    file = new File(url.toURI());
                } catch (URISyntaxException e) {
                    throw new IllegalArgumentException("Invalid i18n file: " + url, e);
                }
                if (!file.exists()) {
                    throw new IllegalArgumentException("Invalid i18n file: " + file);
                }
                i18nInstance.addBundleProvider(() -> {
                    var parent = file.getParentFile();
                    var fileName = file.toPath().getFileName().toString();
                    var nameWithoutExt = fileName.replaceFirst("[.][^.]+$", ""); // Remove last dot + extension

                    var urlClassLoader = new URLClassLoader(new URL[]{parent.toURI().toURL()});

                    return ResourceBundle.getBundle(nameWithoutExt, Locale.getDefault(), urlClassLoader);

                });
            }
        }
        i18nInstance.changeLocale(Locale.getDefault());

        if (controllerInstance != null) {
            instance = controllerInstance;
            new FxmlControllerBeanPostProcessor().postProcessAfterInitialization(instance, "controller");
        } else {
            instance = context.getBean(controller);
        }

        // add default theme class to controller
        instance.getRoot().getStyleClass().add("theme-presets");

        if (controller != null) {
            classloader.putClassLoader(UiControllerBuilder.class.getName() + "_controller", controller.getClassLoader());
        }

        robot.interact(() -> {

            if (stageSetup != null) {
                var c = instance.getRoot() == null ? new Pane() : instance.getRoot();
                stageSetup.setup(stage, w, h, c);
            }

            var builder = ToolStylesheetProvider.builder();
            if (toolStylesheetProvider != null) {
                builder.userAgentStylesheet(toolStylesheetProvider.getUserAgentStylesheet());
                builder.stylesheets(toolStylesheetProvider.getStylesheets());
            }
            if (css != null) {
                var dataUri = URLUtils.toDataURI(css).toString();
                builder.stylesheet(dataUri);
            }
            if (!cssUrl.isEmpty()) {
                cssUrl.forEach(url -> builder.stylesheet(url.toExternalForm()));
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

                fxomEvents.fxomDocument().set(doc);
            }

            if (stage.getScene() != null) {
                stage.getScene().getRoot().getStyleClass().add("testStage");
            }

        });

        return new TestStage<>(stage, instance, docRef.get());
    }

}
