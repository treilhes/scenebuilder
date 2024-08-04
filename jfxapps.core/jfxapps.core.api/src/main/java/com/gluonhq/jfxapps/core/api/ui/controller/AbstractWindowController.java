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

import java.util.List;

import org.pdfsam.rxjavafx.schedulers.JavaFxScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.FxThread;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 * The Class AbstractWindowController.
 */
public abstract class AbstractWindowController implements InstanceWindow {

    public final static Logger logger = LoggerFactory.getLogger(AbstractWindowController.class);

    /** The owner window stage. */
    final private InstanceWindow owner;

    /** The root. */
    private Parent root;

    /** The scene. */
    private Scene scene;

    /** The stage. */
    private Stage stage;

    /** The clamp factor. */
    private final double CLAMP_FACTOR = 0.9;

    /** The size to scene. */
    private final boolean sizeToScene; // true by default

    /** The tool stylesheet config. */
    private ToolStylesheetProvider toolStylesheetConfig;

    private CloseHandler closeHandler;
    private FocusHandler focusHandler;

    private final SceneBuilderManager sceneBuilderManager;
    private final IconSetting iconSetting;

    /**
     * Instantiates a new abstract window controller.
     *
     * @param api the scene builder api
     * @param owner the owner
     */
    public AbstractWindowController(SceneBuilderManager sceneBuilderManager, IconSetting iconSetting, InstanceWindow owner) {
        this(sceneBuilderManager, iconSetting, owner, true);
    }

    /**
     * Instantiates a new abstract window controller.
     *
     * @param api the scene builder api
     * @param owner the owner
     * @param sizeToScene the size to scene
     */
    public AbstractWindowController(SceneBuilderManager sceneBuilderManager, IconSetting iconSetting, InstanceWindow owner, boolean sizeToScene) {
        this.sceneBuilderManager = sceneBuilderManager;
        this.iconSetting = iconSetting;
        this.owner = owner;
        this.sizeToScene = sizeToScene;

    }


    /**
     * Set the root of this panel controller. This routine must be invoked by
     * subclass's makePanel() routine.
     *
     * @param root the root panel (non null).
     */
    public void setRoot(Parent root) {
        assert root != null;
        this.root = root;

        sceneBuilderManager.stylesheetConfig().subscribeOn(JavaFxScheduler.platform()).subscribe(s -> {
            toolStylesheetDidChange(s);
        });
    }


    /** The close request handler. */
    private final EventHandler<WindowEvent> closeRequestHandler = event -> {
        onCloseRequest();
        event.consume();
    };

    /** The focus handler. */
    private final ChangeListener<Boolean> focusEventHandler = (ob, o, n) -> {
        if (n) {
            onFocus();
        }
    };

    /**
     * Returns the root FX object of this window.
     *
     * @return the root object of this window (never null)
     */
    public Parent getRoot() {
        assert root != null;
        return root;
    }

    /**
     * Returns the scene of this window. This method invokes {@link #getRoot()}.
     * When called the first time, it also invokes
     * {@link #controllerDidCreateScene()} just after creating the scene object.
     *
     * @return the scene object of this window (never null)
     */
    public Scene getScene() {
        assert Platform.isFxApplicationThread();

        if (scene == null) {
            scene = new Scene(getRoot());
            controllerDidCreateScene();
        }

        return scene;
    }

    /**
     * Returns the stage of this window. This method invokes {@link #getScene()}.
     * When called the first time, it also invokes
     * {@link #controllerDidCreateStage()} just after creating the stage object.
     *
     * @return the stage object of this window (never null).
     */
    public Stage getStage(boolean renew) {
        assert Platform.isFxApplicationThread();

        if (stage == null || renew) {
            stage = new Stage();
            stage.initOwner(this.owner == null ? null : this.owner.getStage());
            stage.setOnCloseRequest(closeRequestHandler);
            stage.focusedProperty().addListener(focusEventHandler);
            stage.setScene(getScene());
            clampWindow();
            if (sizeToScene) {
                stage.sizeToScene();
            }
            // By default we set the same icons as the owner
            if (this.owner != null) {
                stage.getIcons().addAll(this.owner.getStage().getIcons());
            } else if (iconSetting != null) {
                iconSetting.setWindowIcon(stage);
            }

            controllerDidCreateStage();
        }

        return stage;
    }

    @Override
    public Stage getStage() {
        return getStage(false);
    }

    public boolean isOpen() {
        return stage != null && stage.isShowing();
    }
    /**
     * Opens this window and place it in front.
     */
    @Override
    @FxThread
    public void openWindow() {
        assert Platform.isFxApplicationThread();
        iconSetting.setWindowIcon(getStage());
        getStage().show();
        getStage().toFront();
    }

    /**
     * Closes this window.
     */
    @Override
    public void closeWindow() {
        assert Platform.isFxApplicationThread();
        getStage().close();
    }


    @Override
    public void setCloseHandler(CloseHandler closeHandler) {
        this.closeHandler = closeHandler;
    }

    @Override
    public void setFocusHandler(FocusHandler focusHandler) {
        this.focusHandler = focusHandler;
    }

    public void onCloseRequest() {
        if (closeHandler != null) {
            closeHandler.onClose();
        }
    }

    public void onFocus() {
        if (focusHandler != null) {
            focusHandler.onFocus();
        }
    }

    /**
     * Controller did create scene.
     */
    protected void controllerDidCreateScene() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() == null;
    }

    /**
     * Controller did create stage.
     */
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;
    }

    /*
     * For subclasses
     */

    /**
     * Replaces old Stylesheet config by the tool style sheet assigned to this
     * controller. This methods is event binded to {@link DocumentManager#stylesheetConfig()} using an RxJava2 subscription.
     *
     * @param newToolStylesheetConfig null or the new style sheet configuration to apply
     */
    protected void toolStylesheetDidChange(ToolStylesheetProvider newToolStylesheetConfig) {

        if (root == null) { // nothing to style so return
            return;
        }

        if (toolStylesheetConfig != null) { // if old conf then removeit
            root.getStylesheets().remove(toolStylesheetConfig.getUserAgentStylesheet());
            root.getStylesheets().removeAll(toolStylesheetConfig.getStylesheets());
        }

        if (newToolStylesheetConfig != null) { // replace the active conf only if the new one is valid
            toolStylesheetConfig = newToolStylesheetConfig;
        }

        // apply the conf if the current one is valid
        if (toolStylesheetConfig != null) {
            if (toolStylesheetConfig.getUserAgentStylesheet() != null) {
                root.getStylesheets().add(toolStylesheetConfig.getUserAgentStylesheet());
            }
            if (toolStylesheetConfig.getStylesheets() != null) {
                logger.info("Applying new tool theme using {} on {}",
                        toolStylesheetConfig.getStylesheets(), this.getClass().getName());
                root.getStylesheets().addAll(toolStylesheetConfig.getStylesheets());
            }
        }

        logger.info("Applying new tool theme using  {} on {}", root.getStylesheets(), this.getClass().getName());

    }

    /*
     * Private
     */

    // See DTL-5928
    // The three approaches below do not provide any resizing, for some reason:
    // (1)
    // stage.setHeight(newHeight);
    // stage.setWidth(newWidth);
    // (2)
    // scene.getWindow().setHeight(newHeight);
    // scene.getWindow().setWidth(newWidth);
    // (3)
    // getRoot().resize(newWidth, newHeight);
    //
    // The current implementation raises the point root of layout must be
    /**
     * Clamp window.
     */
    // a Region, which is for now acceptable but could perhaps be an issue later.
    private void clampWindow() {
        if (getRoot() instanceof Region) {
            Rectangle2D vBounds = Screen.getPrimary().getVisualBounds();
            double primaryScreenHeight = vBounds.getHeight();
            double primaryScreenWidth = vBounds.getWidth();
            double currentHeight = getRoot().prefHeight(-1);
            double currentWidth = getRoot().prefWidth(-1);

            if (currentHeight > primaryScreenHeight) {
                double newHeight = primaryScreenHeight * CLAMP_FACTOR;
                // System.out.println("Clamp: new height is " + newHeight);
                assert getRoot() instanceof Region;
                ((Region) getRoot()).setPrefHeight(newHeight);
            }

            if (currentWidth > primaryScreenWidth) {
                double newWidth = primaryScreenWidth * CLAMP_FACTOR;
                // System.out.println("Clamp: new width is " + newWidth);
                assert getRoot() instanceof Region;
                ((Region) getRoot()).setPrefWidth(newWidth);
            }
        }
    }

    /**
     * Gets the biggest viewable rectangle.
     *
     * @return the biggest viewable rectangle
     */
    protected Rectangle2D getBiggestViewableRectangle() {
        assert stage != null;

        Rectangle2D res;

        if (Screen.getScreens().size() == 1) {
            res = Screen.getPrimary().getVisualBounds();
        } else {
            Rectangle2D stageRect = new Rectangle2D(stage.getX(), stage.getY(), stage.getWidth(), stage.getHeight());
            List<Screen> screens = Screen.getScreensForRectangle(stageRect);

            // The stage is entirely rendered on one screen, which is either the
            // primary one or not, we don't care here.
//            if (screens.size() == 1) {
            res = screens.get(0).getVisualBounds();
//            } else {
            // The stage is spread over several screens.
            // We compute the surface of the stage on each on the involved
            // screen to select the biggest one == still to be implemented.
//                TreeMap<String, Screen> sortedScreens = new TreeMap<>();
//
//                for (Screen screen : screens) {
//                    computeSurface(screen, stageRect, sortedScreens);
//                }
//
//                res = sortedScreens.get(sortedScreens.lastKey()).getVisualBounds();
//            }
        }

        return res;
    }

//    public Api getApi() {
//        return api;
//    }

    // Compute the percentage of the surface of stageRect which is rendered in
    // the given screen and write the result in sortedScreens (percentage is
    // rounded and turned into a String so that we benefit natural order sorting.
//    private void computeSurface(Screen screen, Rectangle2D stageRect, TreeMap<String, Screen> sortedScreens) {
//        Rectangle2D screenBounds = screen.getVisualBounds();
//        double surfaceX, surfaceY, surfaceW, surfaceH;
//        if (screenBounds.getMinX() < stageRect.getMinX()) {
//            if (screenBounds.getMinX() < 0) {
//                surfaceX = stageRect.getMinX();
//            } else {
//                surfaceX = screenBounds.getMinX();
//            }
//        } else {
//
//        }
//    }


}
