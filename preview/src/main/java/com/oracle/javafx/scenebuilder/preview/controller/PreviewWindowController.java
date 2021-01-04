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
package com.oracle.javafx.scenebuilder.preview.controller;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.i18n.I18nResourceProvider;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider2;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.ui.AbstractWindowController;
import com.oracle.javafx.scenebuilder.core.util.MathUtils;
import com.oracle.javafx.scenebuilder.core.util.Utils;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.ParallelCamera;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

/**
 * Controller for Window when calling "Show Preview in Window"
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class PreviewWindowController extends AbstractWindowController {

    private final Editor editorController;
    private Timer timer = null;
    private final int WIDTH_WHEN_EMPTY = 320;
    private final int HEIGHT_WHEN_EMPTY = 200;
    private CameraType cameraType;
    private boolean autoResize3DContent = true;
    private static final String NID_PREVIEW_ROOT = "previewRoot"; //NOI18N
//    private Theme editorControllerTheme;
//    private GluonTheme editorControllerGluonTheme;
//    private GluonSwatch editorControllerGluonSwatch;
    private ObservableList<File> sceneStyleSheet;
    private Size currentSize = Size.SIZE_PREFERRED;
    private boolean sizeChangedFromMenu = false;
    private static final double TARGET_SIZE_3D = 500;

    // These two one are used to host the width and height difference
    // coming from the decoration of the window; this is something highly
    // dependent on the operating system.
    private double decorationX = 0;
    private double decorationY = 0;
    private boolean isDirty = true;
    private final long IMMEDIATE = 0; // milliseconds
    private final long DELAYED = 1000; // milliseconds
	private StylesheetProvider2 stylesheetConfig;
    private I18nResourceProvider resourceConfig;

    /**
     * The type of Camera used by the Preview panel.
     */
    public enum CameraType {

        PARALLEL, PERSPECTIVE
    }

    public PreviewWindowController(
            @Autowired SceneBuilderManager sceneBuilderManager,
            @Autowired Editor editorController,
            @Autowired DocumentManager documentManager,
            @Autowired Document document) {
        super(sceneBuilderManager, document.getStage());
        this.editorController = editorController;
        
        makeRoot();
        
        this.editorController.fxomDocumentProperty().addListener(
                (ChangeListener<FXOMDocument>) (ov, od, nd) -> {
                    assert editorController.getFxomDocument() == nd;
                    if (od != null) {
                        od.sceneGraphRevisionProperty().removeListener(fxomDocumentRevisionListener);
                        od.cssRevisionProperty().removeListener(cssRevisionListener);
                    }
                    if (nd != null) {
                        nd.sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
                        nd.cssRevisionProperty().addListener(cssRevisionListener);
                        requestUpdate(DELAYED);
                    }
                });

        if (editorController.getFxomDocument() != null) {
            editorController.getFxomDocument().sceneGraphRevisionProperty().addListener(fxomDocumentRevisionListener);
            editorController.getFxomDocument().cssRevisionProperty().addListener(cssRevisionListener);
        }

        documentManager.stylesheetConfig().subscribe(s -> {
        	stylesheetConfig = s;
        	requestUpdate(DELAYED);
        });

        documentManager.i18nResourceConfig().subscribe(s -> {
            resourceConfig = s;
            requestUpdate(DELAYED);
        });

        this.editorController.sampleDataEnabledProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> requestUpdate(DELAYED));
    }

//    /*
//     * AbstractWindowController
//     */
//    @Override
    protected void makeRoot() {
        // Until the timer used in requestUpdate() expires, so that the root of
        // the scene is updated to the real content, we set a placeholder.
        StackPane sp = new StackPane();
        sp.setPrefSize(WIDTH_WHEN_EMPTY, HEIGHT_WHEN_EMPTY);
        setRoot(sp);

    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        getStage().close();
    }

    @Override
    public void onFocus() {}

    @Override
    protected void controllerDidCreateStage() {
        updateWindowSize();
        updateWindowTitle();

     // Until the timer used in requestUpdate() expires, so that the root of
        // the scene is updated to the real content, we set a placeholder.
        StackPane sp = new StackPane();
        sp.setPrefSize(WIDTH_WHEN_EMPTY, HEIGHT_WHEN_EMPTY);
        setRoot(sp);

        requestUpdate(IMMEDIATE);
    }

    @Override
    public void openWindow() {
        super.openWindow();
        if (isDirty) {
            requestUpdate(IMMEDIATE);
            isDirty = false;
        }
    }

    public void openDialog() {
        final FXOMDocument fxomDocument = editorController.getFxomDocument();
        assert fxomDocument != null;
        // We clone the FXOMDocument
        FXOMDocument clone;
        try {
            clone = new FXOMDocument(fxomDocument.getFxmlText(false),
                    fxomDocument.getLocation(),
                    fxomDocument.getClassLoader(),
                    fxomDocument.getResources());
            clone.setSampleDataEnabled(fxomDocument.isSampleDataEnabled());
        } catch (IOException ex) {
            throw new RuntimeException("Bug in PreviewWindowController::openDialog", ex); //NOI18N
        }

        final Object sceneGraphRoot = clone.getSceneGraphRoot();
        assert sceneGraphRoot instanceof DialogPane;
        final DialogPane dialogPane = (DialogPane) sceneGraphRoot;
        final Dialog<? extends Object> dialog = new Dialog<>();
        dialog.setDialogPane(dialogPane);
        dialog.initModality(Modality.NONE);
        if (dialogPane.getButtonTypes().isEmpty()) {
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.CLOSE);
        }
        dialog.show();
    }

    @Override
    public void closeWindow() {
        super.closeWindow();
        isDirty = true;
    }

//    @Override
//    protected void toolStylesheetDidChange(String oldStylesheet) {
//        // Preview window ignores the tool style sheet.
//        // Unlike other windows, its styling is driven by the user design.
//    }

    /*
     * Private
     */

    private final ChangeListener<Number> fxomDocumentRevisionListener
            = (observable, oldValue, newValue) -> requestUpdate(DELAYED);

    private final ChangeListener<Number> cssRevisionListener
            = (observable, oldValue, newValue) -> requestUpdate(IMMEDIATE);

    /**
     * We use the provided delay before refreshing the content of the preview.
     * If further modification is brought to the layout before expiration of it
     * we restart the timer. The idea is to lower the resources used to refresh
     * the preview window content.
     * The delay is expressed in milliseconds.
     * In some cases it is wise to used delay = 0, e.g. when opening the window.
     */
    private void requestUpdate(long delay) {

        if (!getStage().isShowing()) {
            return;
        }
        TimerTask timerTask = new TimerTask() {

            @Override
            public void run() {
            // JavaFX data should only be accessed on the JavaFX thread.
            // => we must wrap the code into a Runnable object and call the Platform.runLater
            Platform.runLater(() -> {
                final FXOMDocument fxomDocument = editorController.getFxomDocument();
                String themeStyleSheetString = null;
                if (fxomDocument != null) {
                    // We clone the FXOMDocument
                    FXOMDocument clone;

                    try {
                        clone = new FXOMDocument(fxomDocument.getFxmlText(false),
                                fxomDocument.getLocation(),
                                fxomDocument.getClassLoader(),
                                fxomDocument.getResources());
                        clone.setSampleDataEnabled(fxomDocument.isSampleDataEnabled());
                    } catch (IOException ex) {
                        throw new RuntimeException("Bug in PreviewWindowController::requestUpdate", ex); //NOI18N
                    }

                    Object sceneGraphRoot = clone.getDisplayNodeOrSceneGraphRoot();
                    themeStyleSheetString = stylesheetConfig.getUserAgentStylesheet();

                    if (sceneGraphRoot instanceof Parent) {
                        Parent root = (Parent) sceneGraphRoot;
                        root.setId(NID_PREVIEW_ROOT);
                        assert root.getScene() == null;

                        setRoot((Parent) updateAutoResizeTransform(root));

                        // Compute the proper styling
                        List<String> newStyleSheets1 = new ArrayList<>();
                        computeStyleSheets(newStyleSheets1, sceneGraphRoot, clone.getDisplayStylesheets());

                        // Clean all styling
                        root.getStylesheets().removeAll();

                        // Apply the new styling
                        root.getStylesheets().addAll(newStyleSheets1);
                    } else if (sceneGraphRoot instanceof Node) {
                        Node root = (Node) sceneGraphRoot;
                        StackPane sp1 = new StackPane();
                        sp1.setId(NID_PREVIEW_ROOT);

                        // Compute the proper styling
                        List<String> newStyleSheets2 = new ArrayList<>();
                        computeStyleSheets(newStyleSheets2, sceneGraphRoot, clone.getDisplayStylesheets());

                        // Apply the new styling as a whole
                        sp1.getStylesheets().addAll(newStyleSheets2);

                        // With some 3D assets such as TuxRotation the
                        // rendering is wrong unless applyCSS is called.
                        root.applyCss();
                        sp1.getChildren().add(updateAutoResizeTransform((Node) sceneGraphRoot));
                        setRoot(sp1);
                    } else {
                        setCameraType(CameraType.PARALLEL);
                        sizeChangedFromMenu = false;
                        StackPane sp2 = new StackPane(new Label(I18N.getString("preview.not.node")));
                        sp2.setId(NID_PREVIEW_ROOT);
                        sp2.setPrefSize(WIDTH_WHEN_EMPTY, HEIGHT_WHEN_EMPTY);
                        setRoot(sp2);
                    }
                } else {
                    setCameraType(CameraType.PARALLEL);
                    sizeChangedFromMenu = false;
                    StackPane sp3 = new StackPane(new Label(I18N.getString("preview.no.document")));
                    sp3.setId(NID_PREVIEW_ROOT);
                    sp3.setPrefSize(WIDTH_WHEN_EMPTY, HEIGHT_WHEN_EMPTY);
                    setRoot(sp3);
                }

                getScene().setRoot(getRoot());
                if (themeStyleSheetString != null) {
                	ObservableList<String> newStylesheets = FXCollections.observableArrayList(getScene().getStylesheets());
                	getScene().setUserAgentStylesheet(themeStyleSheetString);// OR stylesheetConfig.getUserAgentStylesheet()
                	getScene().getStylesheets().clear();
                    getScene().getStylesheets().addAll(newStylesheets);
                    getScene().getStylesheets().addAll(stylesheetConfig.getStylesheets());
                }
                
//                PerspectiveCamera pc = new PerspectiveCamera(false);
//                pc.setLayoutX(50);
//                pc.setCLayoutY(50);
//                ParallelCamera pl = new ParallelCamera();
//                getScene().setCamera(pc);
                updateWindowSize();
                updateWindowTitle();
            });
            }
        };

        if (timer != null) {
            timer.cancel();
        }

        timer = new Timer(true);
        timer.schedule(timerTask, delay); // milliseconds
    }

    public boolean userResizedPreviewWindow() {
        boolean res = false;
        double sceneHeight = getScene().getHeight();
        double sceneWidth = getScene().getWidth();

        if (sceneHeight > 0 && sceneWidth > 0) {
            double prefHeight = getRoot().prefHeight(-1);
            double prefWidth = getRoot().prefWidth(-1);

            if ((!MathUtils.equals(prefHeight, sceneHeight)
                    && !MathUtils.equals(sceneHeight, HEIGHT_WHEN_EMPTY)
                    && !MathUtils.equals(sceneHeight, getHeightFromSize(getSize())))
                    ||
                    (!MathUtils.equals(prefWidth, sceneWidth)
                            && !MathUtils.equals(sceneWidth, WIDTH_WHEN_EMPTY)
                            && !MathUtils.equals(sceneWidth, getWidthFromSize(getSize())))) {
                res = true;
            }
        }

        return res;
    }

    // With some 3D layout the preferred size can be ridiculous (< 1) hence a dot
    // on screen, or it can be gigantic (several thousands). Do we want to put
    // some bounds so that something is made visible ?
    // In the same spirit some of the predefined sizes such as 1920x1080 or even
    // 1280x800 may exceed the capability of the user screen: should we greyed
    // relevant size values accordingly in Preview menu ?
    private void updateWindowSize() {
        final FXOMDocument fxomDocument = editorController.getFxomDocument();

        if (fxomDocument != null) {
            // A size setup action taken from menu bar has priority over a resize
            // done directly on the Preview window.
            if (sizeChangedFromMenu) {
                sizeChangedFromMenu = false;
                // We take into account size taken by decoration so that we are
                // sure the area rendered has the exact size we want.
                getStage().setWidth(getWidthFromSize(getSize()) + decorationX);
                getStage().setHeight(getHeightFromSize(getSize()) + decorationY);
            } else if (!userResizedPreviewWindow()) {
                // Experience shows 3D layout defined so that top level item is
                // a Group are rendered correctly on their own. The 3D case is
                // something that deserves a closer look anyway.
                if (fxomDocument.getSceneGraphRoot() instanceof MeshView) {
                    getStage().setWidth(TARGET_SIZE_3D);
                    getStage().setHeight(TARGET_SIZE_3D);
                } else {
                    // When we change the stylesheet (Modena, Caspian) we need to know
                    // if the user has resized the preview window: if yes we keep
                    // the user size, else we size the layout to the scene.
                    getStage().sizeToScene();
                }

                // The first time preview is rendered we always enter this case.
                // The whole layout is made visible and size difference between
                // Scene and Stage allows to compute size taken by decoration.
                decorationX = getStage().getWidth() - getRoot().prefWidth(-1);
                decorationY = getStage().getHeight() - getRoot().prefHeight(-1);
            }
        } else {
            getStage().setWidth(WIDTH_WHEN_EMPTY);
            getStage().setHeight(HEIGHT_WHEN_EMPTY);
        }
    }

    private void updateWindowTitle() {
        final FXOMDocument fxomDocument
                = editorController.getFxomDocument();
        getStage().setTitle(Utils.makeTitle(fxomDocument));
    }

    public void setCameraType(PreviewWindowController.CameraType ct) {
        cameraType = ct;
        updateCamera();
    }

    void updateCamera() {
        if (getScene() != null) {
            if (cameraType == CameraType.PERSPECTIVE) {
                // Set Perspective Camera
                getScene().setCamera(new PerspectiveCamera(false));
            } else {
                // Set Parallel Camera
                getScene().setCamera(null); // null defaults to Parallel camera
            }
        }
    }

    /**
     * Returns true if this preview panel automatically resize 3D content.
     *
     * @return true if this preview panel automatically resize 3D content.
     */
    public boolean isAutoResize3DContent() {
        return autoResize3DContent;
    }

    /**
     * Enables or disables autoresizing of 3D content.
     *
     * @param autoResize3DContent true if this preview panel should autoresize
     *                            3D content.
     */
    public void setAutoResize3DContent(boolean autoResize3DContent) {
        this.autoResize3DContent = autoResize3DContent;
    }

    // If the given node is 3D stuff we add transforms and set perspective
    // camera to become able to display it.
    Node updateAutoResizeTransform(Node whatever) {
        Node res = whatever;
        assert editorController.getFxomDocument() != null;

        if (editorController.is3D() && autoResize3DContent) {
            res.getTransforms().clear();
            final Bounds rootBounds = res.getLayoutBounds();
            // Content is 3D.
            // Zoom to get a TARGET_SIZE_3D size.
            final double scaleX = TARGET_SIZE_3D / rootBounds.getWidth();
            final double scaleY = TARGET_SIZE_3D / rootBounds.getHeight();
            final double scaleZ = TARGET_SIZE_3D / rootBounds.getDepth();
            final double scale = Math.min(scaleX, Math.min(scaleY, scaleZ));
            final double tX = -rootBounds.getMinX();
            final double tY = -rootBounds.getMinY();
            final double tZ = -rootBounds.getMinZ();
            res.getTransforms().add(new Scale(scale, scale, scale));
            res.getTransforms().add(new Translate(tX, tY, tZ));

            // Set the scene camera to PerspectiveCamera, to see 3D nodes correctly.
            setCameraType(CameraType.PERSPECTIVE);
        } else {
            setCameraType(CameraType.PARALLEL);
        }

        return res;
    }

    private double getWidthFromSize(Size size) {
        switch (size) {
            case SIZE_DEFAULT:
                return WIDTH_WHEN_EMPTY;
            case SIZE_PREFERRED:
                return getRoot().prefWidth(-1);
            default:
                return size.getWidth();
        }
    }

    private double getHeightFromSize(Size size) {
        switch (size) {
            case SIZE_DEFAULT:
                return HEIGHT_WHEN_EMPTY;
            case SIZE_PREFERRED:
                return getRoot().prefHeight(-1);
            default:
                return size.getHeight();
        }
    }

    /**
     * @return the current Size used for previewing.
     */
    public Size getSize() {
        return currentSize;
    }

    public void setSize(Size size) {
        currentSize = size;
        sizeChangedFromMenu = true;
        requestUpdate(IMMEDIATE);
    }

    private void computeStyleSheets(List<String> newStyleSheets, Object sceneGraphRoot, List<String> displayStylesheets) {
        if (sceneGraphRoot instanceof Parent) {
            // At that stage current style sheets are the one defined within the FXML
            ObservableList<String> currentStyleSheets = ((Parent) sceneGraphRoot).getStylesheets();

            for (String stylesheet : currentStyleSheets) {
                newStyleSheets.add(stylesheet);
            }
        }

        newStyleSheets.addAll(displayStylesheets);

        // Add style sheet set thanks Preview > Scene Style Sheets > Add a Style Sheet
        if (sceneStyleSheet != null) {
            for (File f : sceneStyleSheet) {
                String urlString = ""; //NOI18N
                try {
                    urlString = f.toURI().toURL().toString();
                } catch (MalformedURLException ex) {
                    throw new RuntimeException("Bug in PreviewWindowController", ex); //NOI18N
                }
                newStyleSheets.add(urlString);
            }
        }
    }

    public boolean sizeDoesFit(Size size) {
        boolean res = false;

        if (getStage() != null) {
            Rectangle2D frame = getBiggestViewableRectangle();

            if (getWidthFromSize(size) <= frame.getWidth() - decorationX
                    && getHeightFromSize(size) <= frame.getHeight() - decorationY) {
                res = true;
            }
        }

        return res;
    }
}