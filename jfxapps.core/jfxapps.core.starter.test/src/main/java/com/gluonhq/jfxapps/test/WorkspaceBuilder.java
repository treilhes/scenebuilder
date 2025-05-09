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

import java.util.concurrent.atomic.AtomicReference;

import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.subjects.FxomEvents;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.UiController;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMDocumentFactory;
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@Prototype
public class WorkspaceBuilder {

    private final JfxAppContext context;
    private final JavafxThreadClassloader classloader;
    private final ApplicationEvents events;
    private final FxomEvents instanceEvents;
    private final FxRobot robot;;

    private String css;
    private Stage stage;
    private int width;
    private int height;

    private StageSetup stageSetup = StageType.Center;
    private String fxml;
    private FXOMDocument document;
    private ToolStylesheetProvider toolStylesheetProvider;

    protected WorkspaceBuilder(JfxAppContext context, JavafxThreadClassloader classloader, ApplicationEvents events,
            FxomEvents instanceEvents) {
        this.context = context;
        this.classloader = classloader;
        this.events = events;
        this.instanceEvents = instanceEvents;
        this.robot = new FxRobot();
    }

    protected WorkspaceBuilder stage(Stage stage) {
        this.stage = stage;
        return this;
    }

    public WorkspaceBuilder size(int width, int height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public WorkspaceBuilder document(String fxml) {
        this.fxml = fxml;
        return this;
    }

    public WorkspaceBuilder document(FXOMDocument document) {
        this.document = document;
        return this;
    }

    public WorkspaceBuilder css(String css) {
        this.css = css;
        return this;
    }

    public WorkspaceBuilder css(ToolStylesheetProvider toolStylesheetProvider) {
        this.toolStylesheetProvider = toolStylesheetProvider;
        return this;
    }

    public WorkspaceBuilder setup(StageSetup stageSetup) {
        this.stageSetup = stageSetup;
        return this;
    }

    public <T extends UiController> TestStage<SubSceneController> show() {
        int w = width == 0 ? 800 : width;
        int h = height == 0 ? 600 : height;
        int stageSizeDelta = 20;
        SubSceneController instance = new SubSceneController(instanceEvents, width, height, stageSizeDelta);

        AtomicReference<FXOMDocument> docRef = new AtomicReference<>();

        robot.interact(() -> {
            classloader.addClassLoader(Thread.currentThread().getContextClassLoader());
            Thread.currentThread().setContextClassLoader(classloader);
        });

        robot.interact(() -> setup(instance, w + stageSizeDelta * 2, h + stageSizeDelta * 2, docRef));

        return new TestStage<>(stage, instance, docRef.get());
    }

    private void setup(SubSceneController instance, int width, int height, AtomicReference<FXOMDocument> docRef) {
        if (stageSetup != null) {

            Parent c = instance.getRoot() == null ? new Pane() : instance.getRoot();
            stageSetup.setup(stage, width, height, c);
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

        var provider = builder.build();
        events.stylesheetConfig().set(provider);

        if (doc != null) {
            instance.getSubSceneRoot().getChildren().add(doc.getFxomRoot().getSceneGraphObject().getAs(Node.class));
            instanceEvents.fxomDocument().set(doc);
        }

        instance.getSubScene().applyCss();

        var b = instance.getSubSceneRoot().getLayoutBounds();
        double sw = b.getMaxX();
        double sh = b.getMaxY();
        instance.getSubScene().setWidth(sw);
        instance.getSubScene().setHeight(sh);
    }
    public class SubSceneController implements UiController {
        private final StackPane root = new StackPane();

        private final Group sceneGroup = new Group();
        private final Group contentGroup = new Group();
        private SubScene subScene;

        private final Pane glassLayer = new Pane();
        private final Group layer = new Group();

        public SubSceneController(FxomEvents instanceEvents, int width, int height, int delta) {
            super();
            contentGroup.setLayoutX(0);
            contentGroup.setLayoutY(0);
            subScene = new SubScene(contentGroup, width, height);
            subScene.setCamera(new PerspectiveCamera());

            root.setId("root");
            sceneGroup.setId("sceneGroup");
            contentGroup.setId("contentGroup");
            subScene.setId("subScene");
            glassLayer.setId("glassLayer");
            layer.setId("layer");

            sceneGroup.getChildren().add(subScene);
            glassLayer.getChildren().add(layer);

            root.getChildren().add(sceneGroup);
            root.getChildren().add(glassLayer);
            root.setStyle("-fx-background-color: grey;");

            glassLayer.setFocusTraversable(true);
            // glassLayer.setMouseTransparent(true);
            glassLayer.setPickOnBounds(false);

            // StackPane.setAlignment(sceneGroup, Pos.CENTER);

            subScene.setFill(Color.WHITE);
//            subScene.setLayoutX(delta);
//            subScene.setLayoutY(delta);

            events.stylesheetConfig().subscribe(s -> applyStylesheetConfig(s));
        }

        @Override
        public void setRoot(Parent root) {

        }

        public Group getLayer() {
            return layer;
        }


        public Pane getGlassLayer() {
            return glassLayer;
        }

        @Override
        public Pane getRoot() {
            return root;
        }

        public SubScene getSubScene() {
            return subScene;
        }

        public Group getSubSceneRoot() {
            return contentGroup;
        }

        public Group getSubSceneHolder() {
            return sceneGroup;
        }

        public void applyStylesheetConfig(ToolStylesheetProvider stylesheetConfig) {
            getRoot().getScene().setUserAgentStylesheet(stylesheetConfig.getUserAgentStylesheet());
            getRoot().getStylesheets().setAll(stylesheetConfig.getStylesheets());
            getRoot().applyCss();
        }
    }
}
