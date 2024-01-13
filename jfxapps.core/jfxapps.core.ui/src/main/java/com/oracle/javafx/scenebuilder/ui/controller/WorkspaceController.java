/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.theme.StylesheetProvider;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlPanelController;
import com.oracle.javafx.scenebuilder.api.ui.menu.ContextMenu;
import com.oracle.javafx.scenebuilder.api.ui.misc.Workspace;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.SceneGraphObject;

import javafx.animation.FadeTransition;
import javafx.application.ConditionalFeature;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Window;
import javafx.util.Duration;

/**
 *
 */
@com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton
public class WorkspaceController extends AbstractFxmlPanelController implements Workspace {

    private static Logger logger = LoggerFactory.getLogger(WorkspaceController.class);

    private static final double AUTORESIZE_SIZE = 500.0;

    private boolean tracingEvents; // For debugging purpose

    @FXML
    private ScrollPane scrollPane;
    @FXML
    private Pane workspacePane;
    @FXML
    private Rectangle extensionRect;
    @FXML
    private Label backgroundPane;
    @FXML
    private Group scalingGroup;
    @FXML
    private SubScene contentSubScene;
    @FXML
    private Group contentGroup;
    @FXML
    private Pane glassLayer;

    private boolean autoResize3DContent = true;

    private DoubleProperty scaling;

    private RuntimeException layoutException;
    private StylesheetProvider stylesheetConfig = null;

    private FXOMDocument fxomDocument;

    private final DocumentManager documentManager;
    private final ContextMenu contextMenu;

    public WorkspaceController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            ContextMenu contextMenu) {
        super(scenebuilderManager, documentManager, WorkspaceController.class.getResource("Workspace.fxml"),
                I18N.getBundle());
        this.documentManager = documentManager;
        this.contextMenu = contextMenu;
    }

    @Override
    public void controllerDidLoadFxml() {
        assert scrollPane != null;
        assert workspacePane != null;
        assert workspacePane.getPrefWidth() == Region.USE_COMPUTED_SIZE;
        assert workspacePane.getPrefHeight() == Region.USE_COMPUTED_SIZE;
        assert workspacePane.getMaxWidth() == Double.MAX_VALUE;
        assert workspacePane.getMaxHeight() == Double.MAX_VALUE;
        assert workspacePane.getMinWidth() == Region.USE_PREF_SIZE;
        assert workspacePane.getMinHeight() == Region.USE_PREF_SIZE;
        assert extensionRect != null;
        assert extensionRect.getLayoutX() == 0.0;
        assert extensionRect.getLayoutY() == 0.0;
        assert backgroundPane != null;
        assert backgroundPane.getLayoutX() == 0.0;
        assert backgroundPane.getLayoutY() == 0.0;
        assert backgroundPane.getMaxWidth() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMaxHeight() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMinWidth() == Region.USE_PREF_SIZE;
        assert backgroundPane.getMinHeight() == Region.USE_PREF_SIZE;
        assert scalingGroup != null;
        assert contentSubScene != null;
        assert contentSubScene.getLayoutX() == 0.0;
        assert contentSubScene.getLayoutY() == 0.0;
        assert contentSubScene.getParent() == scalingGroup;
        assert contentGroup != null;
        assert contentGroup == contentSubScene.getRoot();
        assert contentGroup.getLayoutX() == 0.0;
        assert contentGroup.getLayoutY() == 0.0;
        assert glassLayer != null;
        assert glassLayer.isMouseTransparent() == false;
        assert glassLayer.isFocusTraversable();

        // Remove fake content used to help design
        backgroundPane.setText(""); // NOCHECK

        // Add scene listener to panelRoot.sceneProperty()
        this.scrollPane.sceneProperty().addListener((ChangeListener<Scene>) (ov, t, t1) -> sceneDidChange());

        // Make scalingGroup invisible.
        // We'll turn it visible once content panel is displayed in a Scene
        this.scalingGroup.setVisible(false);

        // Remove sample content from contentGroup
        this.contentGroup.getChildren().clear();

        updateContentGroup();
        updateScalingGroup();

        resetViewport();
        setupEventTracingFilter();

        // Setup the context menu
        scrollPane.setContextMenu(contextMenu.getContextMenu());

        documentManager.stylesheetConfig().subscribe(s -> {
            stylesheetConfig = s;
            applyStylesheetConfig();
        });

        documentManager.fxomDocument().subscribe(om -> {
            SbPlatform.runOnFxThreadWithActiveScope(() -> {
                setFxomDocument(om);
            });
        });

        if (logger.isDebugEnabled()) {
            tracingEvents = false;
            setupEventTracingFilter();
        }
    }

    private void setFxomDocument(FXOMDocument fxomDocument) {
        if (this.fxomDocument != fxomDocument) {
            this.fxomDocument = fxomDocument;
            sceneGraphDidChange();
        }
    }

    public void sceneGraphDidChange() {
        if (this.scrollPane != null) {
            updateContentGroup();
            updateScalingGroup();
        }
    }

    public boolean isAutoResize3DContent() {
        return autoResize3DContent;
    }

    public void setAutoResize3DContent(boolean autoResize3DContent) {

        this.autoResize3DContent = autoResize3DContent;
        if ((scrollPane != null) && (scrollPane.getScene() != null)) {
            adjustWorkspace();
        }
    }

    @Override
    public DoubleProperty scalingProperty() {
        if (scaling == null) {
            scaling = new SimpleDoubleProperty(1.0) {
                @Override
                public void set(double newValue) {
                    super.set(newValue);
                    updateScalingGroup();
                }

            };
        }
        return scaling;
    }

    public void applyStylesheetConfig() {
        contentSubScene.setUserAgentStylesheet(stylesheetConfig.getUserAgentStylesheet());

        // Update scenegraph layout, etc
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument != null) {
            fxomDocument.refreshSceneGraph();
        }
    }

    @Override
    public void layoutContent(boolean applyCSS) {
        if (scrollPane != null) {
            try {
                if (applyCSS) {
                    contentGroupApplyCss();
                }
                scrollPane.layout();
                layoutException = null;
            } catch (RuntimeException x) {
                logger.error("Layout failure", x);
                layoutException = x;
            }
        }
    }

    @Override
    public RuntimeException getLayoutException() {
        return layoutException;
    }

    @Override
    public void beginInteraction() {
        assert scalingGroup.getParent().isManaged();
        assert scrollPane.getContent() instanceof StackPane;

        // Makes the user design and enclosing group unmanaged so
        // that they no longer influence the scroll pane viewport.
        scalingGroup.getParent().setManaged(false);

        // Renders the top stack pane fully rigid
        final StackPane contentPane = (StackPane) scrollPane.getContent();
        assert contentPane.getMinWidth() == Region.USE_PREF_SIZE;
        assert contentPane.getMinHeight() == Region.USE_PREF_SIZE;
        assert contentPane.getPrefWidth() == Region.USE_COMPUTED_SIZE;
        assert contentPane.getPrefHeight() == Region.USE_COMPUTED_SIZE;
        assert contentPane.getMaxWidth() == Double.MAX_VALUE;
        assert contentPane.getMaxHeight() == Double.MAX_VALUE;
        contentPane.setPrefWidth(contentPane.getWidth());
        contentPane.setPrefHeight(contentPane.getHeight());
        contentPane.setMaxWidth(Region.USE_PREF_SIZE);
        contentPane.setMaxHeight(Region.USE_PREF_SIZE);
    }

    @Override
    public void endInteraction() {
        assert scalingGroup.getParent().isManaged() == false;

        // Reverts the top stack pane : it now adjusts to the size of its children
        final StackPane contentPane = (StackPane) scrollPane.getContent();
        assert contentPane.getMinWidth() == Region.USE_PREF_SIZE;
        assert contentPane.getMinHeight() == Region.USE_PREF_SIZE;
        assert contentPane.getPrefWidth() != Region.USE_COMPUTED_SIZE;
        assert contentPane.getPrefHeight() != Region.USE_COMPUTED_SIZE;
        assert contentPane.getMaxWidth() == Region.USE_PREF_SIZE;
        assert contentPane.getMaxHeight() == Region.USE_PREF_SIZE;
        contentPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        contentPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        contentPane.setMaxWidth(Double.MAX_VALUE);
        contentPane.setMaxHeight(Double.MAX_VALUE);

        // Reverts scalingGroup setup
        scalingGroup.getParent().setManaged(true);
    }

    /*
     * Private
     */

    private void sceneDidChange() {
        assert this.scrollPane != null;

        if (scrollPane.getScene() != null) {
            assert scalingGroup.isVisible() == false;

            // Here we'd like to layout the user scene graph immediately
            // i.e. invoke:
            // 1) layoutContent() // to relayout user scene graph
            // 2) adjustWorkspace() // to size the content workspace
            //
            // However invoking layoutContent() from here (scene change listener)
            // does not work very well (see RT-32326).
            //
            // So we do these two steps in runLater().
            // Until they are done, scalingGroup is kept invisible to avoid
            // visual artifacts. After the two steps are done, we turn the
            // visible by calling revealScalingGroup().

            SbPlatform.runOnFxThreadWithActiveScope(() -> {
                layoutContent(true /* applyCSS */);
                adjustWorkspace();
                revealScalingGroup();
            });
        } else {
            assert scalingGroup.isVisible();
            scalingGroup.setVisible(false);
        }
    }

    private void updateContentGroup() {

        /*
         * fxomRoot
         */

        final String statusMessageText, statusStyleClass;
        contentGroup.getChildren().clear();

        boolean canDisplayDocument = false;
        if (fxomDocument == null) {
            statusMessageText = "FXOMDocument is null"; // NOCHECK
            statusStyleClass = "stage-prompt"; // NOCHECK
        } else if (fxomDocument.getFxomRoot() == null) {
            statusMessageText = I18N.getString("content.label.status.invitation");
            statusStyleClass = "stage-prompt"; // NOCHECK
        } else {
            final Object userSceneGraph = fxomDocument.getDisplayNodeOrSceneGraphRoot();
            if (userSceneGraph instanceof Node) {
                final Node rootNode = (Node) userSceneGraph;
                assert rootNode.getParent() == null;
                contentGroup.getChildren().add(rootNode);
                layoutContent(true /* applyCSS */);
                if (layoutException == null) {
                    statusMessageText = ""; // NOCHECK
                    statusStyleClass = "stage-prompt-default"; // NOCHECK
                    canDisplayDocument = true;
                } else {
                    contentGroup.getChildren().clear();
                    statusMessageText = I18N.getString("content.label.status.cannot.display");
                    statusStyleClass = "stage-prompt"; // NOCHECK
                }
            } else {
                statusMessageText = I18N.getString("content.label.status.cannot.display");
                statusStyleClass = "stage-prompt"; // NOCHECK
            }
        }

        backgroundPane.setText(statusMessageText);
        backgroundPane.getStyleClass().clear();
        backgroundPane.getStyleClass().add(statusStyleClass);

        // Display background fill of the Window/Scene
        if (canDisplayDocument) {
            assert fxomDocument != null;
            assert fxomDocument.getFxomRoot() != null;

            SceneGraphObject sceneGraphObject = fxomDocument.getFxomRoot().getSceneGraphObject();

            Paint backgroundPaneFillPaint = Color.WHITE;

            if (sceneGraphObject.isInstanceOf(Window.class)) {
                Window window = sceneGraphObject.getAs(Window.class);
                Scene scene = window.getScene();
                if (scene != null && scene.getFill() != null) {
                    backgroundPaneFillPaint = scene.getFill();
                }
            } else if (sceneGraphObject.isInstanceOf(Scene.class)) {
                Scene scene = sceneGraphObject.getAs(Scene.class);
                if (scene.getFill() != null) {
                    backgroundPaneFillPaint = scene.getFill();
                }
            }

            BackgroundFill backgroundPaneFill = new BackgroundFill(backgroundPaneFillPaint, CornerRadii.EMPTY,
                    Insets.EMPTY);
            backgroundPane.setBackground(new Background(backgroundPaneFill));
        }

        // If layoutException != null, then this layout call is required
        // so that backgroundPane updates its message... Strange...
        backgroundPane.layout();

        adjustWorkspace();
    }

    private void updateScalingGroup() {
        if (scalingGroup != null) {
            final double actualScaling;
            if (fxomDocument == null) {
                actualScaling = 1.0;
            } else if (fxomDocument.getSceneGraphRoot() == null) {
                actualScaling = 1.0;
            } else {
                actualScaling = scalingProperty().get();
            }
            scalingGroup.setScaleX(actualScaling);
            scalingGroup.setScaleY(actualScaling);

            if (Platform.isSupported(ConditionalFeature.SCENE3D)) {
                scalingGroup.setScaleZ(actualScaling);
            }
            // else {
            // leave scaleZ unchanged else it breaks zooming when running
            // with the software pipeline (see DTL-6661).
            // }
        }
    }

    private void adjustWorkspace() {
        final Bounds backgroundBounds, extensionBounds;

        final Object userSceneGraph;
        if (fxomDocument == null) {
            userSceneGraph = null;
        } else {
            userSceneGraph = fxomDocument.getDisplayNodeOrSceneGraphRoot();
        }
        if ((userSceneGraph instanceof Node) && (layoutException == null)) {
            final Node rootNode = (Node) userSceneGraph;

            final Bounds rootBounds = rootNode.getLayoutBounds();

            if (rootBounds.isEmpty() || (rootBounds.getWidth() == 0.0) || (rootBounds.getHeight() == 0.0)) {
                backgroundBounds = new BoundingBox(0.0, 0.0, 0.0, 0.0);
                extensionBounds = new BoundingBox(0.0, 0.0, 0.0, 0.0);
            } else {
                final double scale;
                if ((rootBounds.getDepth() > 0) && autoResize3DContent) {
                    // Content is 3D
                    final double scaleX = AUTORESIZE_SIZE / rootBounds.getWidth();
                    final double scaleY = AUTORESIZE_SIZE / rootBounds.getHeight();
                    final double scaleZ = AUTORESIZE_SIZE / rootBounds.getDepth();
                    scale = Math.min(scaleX, Math.min(scaleY, scaleZ));
                } else {
                    scale = 1.0;
                }
                contentGroup.setScaleX(scale);
                contentGroup.setScaleY(scale);
                contentGroup.setScaleZ(scale);

                final Bounds contentBounds = rootNode.localToParent(rootBounds);
                backgroundBounds = new BoundingBox(0.0, 0.0, contentBounds.getMinX() + contentBounds.getWidth(),
                        contentBounds.getMinY() + contentBounds.getHeight());

                final Bounds unclippedRootBounds = computeUnclippedBounds(rootNode);
                assert unclippedRootBounds.getHeight() != 0.0;
                assert unclippedRootBounds.getWidth() != 0.0;
                assert rootNode.getParent() == contentGroup;

                final Bounds unclippedContentBounds = rootNode.localToParent(unclippedRootBounds);
                extensionBounds = computeExtensionBounds(backgroundBounds, unclippedContentBounds);
            }
        } else {
            backgroundBounds = new BoundingBox(0.0, 0.0, 320.0, 150.0);
            extensionBounds = new BoundingBox(0.0, 0.0, 0.0, 0.0);
        }

        backgroundPane.setPrefWidth(backgroundBounds.getWidth());
        backgroundPane.setPrefHeight(backgroundBounds.getHeight());
        extensionRect.setX(extensionBounds.getMinX());
        extensionRect.setY(extensionBounds.getMinY());
        extensionRect.setWidth(extensionBounds.getWidth());
        extensionRect.setHeight(extensionBounds.getHeight());

        contentSubScene.setWidth(contentGroup.getLayoutBounds().getWidth());
        contentSubScene.setHeight(contentGroup.getLayoutBounds().getHeight());
    }

    private static Bounds computeUnclippedBounds(Node node) {
        final Bounds layoutBounds;
        double minX, minY, maxX, maxY, minZ, maxZ;

        assert node != null;
        assert node.getLayoutBounds().isEmpty() == false;

        layoutBounds = node.getLayoutBounds();
        minX = layoutBounds.getMinX();
        minY = layoutBounds.getMinY();
        maxX = layoutBounds.getMaxX();
        maxY = layoutBounds.getMaxY();
        minZ = layoutBounds.getMinZ();
        maxZ = layoutBounds.getMaxZ();

        if (node instanceof Parent) {
            final Parent parent = (Parent) node;

            for (Node child : parent.getChildrenUnmodifiable()) {
                final Bounds childBounds = child.getBoundsInParent();
                minX = Math.min(minX, childBounds.getMinX());
                minY = Math.min(minY, childBounds.getMinY());
                maxX = Math.max(maxX, childBounds.getMaxX());
                maxY = Math.max(maxY, childBounds.getMaxY());
                minZ = Math.min(minZ, childBounds.getMinZ());
                maxZ = Math.max(maxZ, childBounds.getMaxZ());
            }
        }

        assert minX <= maxX;
        assert minY <= maxY;
        assert minZ <= maxZ;

        return new BoundingBox(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
    }

    private static Bounds computeExtensionBounds(Bounds backgroundBounds, Bounds unclippedContentBounds) {
        final Bounds totalBounds = unionOfBounds(backgroundBounds, unclippedContentBounds);
        final double backgroundCenterX, backgroundCenterY;
        backgroundCenterX = (backgroundBounds.getMinX() + backgroundBounds.getMaxX()) / 2.0;
        backgroundCenterY = (backgroundBounds.getMinY() + backgroundBounds.getMaxY()) / 2.0;
        assert totalBounds.contains(backgroundCenterX, backgroundCenterY);
        double extensionHalfWidth, extensionHalfHeight;
        extensionHalfWidth = Math.max(backgroundCenterX - totalBounds.getMinX(),
                totalBounds.getMaxX() - backgroundCenterX);
        extensionHalfHeight = Math.max(backgroundCenterY - totalBounds.getMinY(),
                totalBounds.getMaxY() - backgroundCenterY);

        // We a few pixels in order the parent ring of root object
        // to fit inside the extension rect.
        extensionHalfWidth += 20.0;
        extensionHalfHeight += 20.0;

        return new BoundingBox(backgroundCenterX - extensionHalfWidth, backgroundCenterY - extensionHalfHeight,
                extensionHalfWidth * 2, extensionHalfHeight * 2);
    }

    private static Bounds unionOfBounds(Bounds b1, Bounds b2) {
        final Bounds result;

        if (b1.isEmpty()) {
            result = b2;
        } else if (b2.isEmpty()) {
            result = b1;
        } else {
            final double minX = Math.min(b1.getMinX(), b2.getMinX());
            final double minY = Math.min(b1.getMinY(), b2.getMinY());
            final double minZ = Math.min(b1.getMinZ(), b2.getMinZ());
            final double maxX = Math.max(b1.getMaxX(), b2.getMaxX());
            final double maxY = Math.max(b1.getMaxY(), b2.getMaxY());
            final double maxZ = Math.max(b1.getMaxZ(), b2.getMaxZ());

            assert minX <= maxX;
            assert minY <= maxY;
            assert minZ <= maxZ;

            result = new BoundingBox(minX, minY, minZ, maxX - minX, maxY - minY, maxZ - minZ);
        }

        return result;
    }

    private void revealScalingGroup() {
        assert scalingGroup.isVisible() == false;

        scalingGroup.setVisible(true);
        scalingGroup.setOpacity(0.0);

        FadeTransition showHost = new FadeTransition(Duration.millis(300), scalingGroup);
        showHost.setFromValue(0.0);
        showHost.setToValue(1.0);
        showHost.play();
    }

    private void contentGroupApplyCss() {
        if (stylesheetConfig != null) {
            contentGroup.getStylesheets().setAll(stylesheetConfig.getStylesheets());
        }

        if (fxomDocument != null) {
            contentGroup.getStylesheets().addAll(fxomDocument.getDisplayStylesheets());
        }
        contentGroup.applyCss();
    }

    @Override
    public Pane getGlassLayer() {
        return glassLayer;
    }

    /**
     * Returns null or the image used for tiling the background of this content
     * panel.
     *
     * @return null or the image used for tiling the background of this content
     *         panel.
     */
    public Image getBackground() {
        final Image result;

        final Background bg = workspacePane.getBackground();
        if (bg == null) {
            result = null;
        } else {
            assert bg.getImages().size() == 1;
            result = bg.getImages().get(0).getImage();
        }

        return result;
    }

    /**
     * Sets the image used for tiling the background of this content panel.
     *
     * @param image null or the image for tiling the background of this content
     *              panel.
     */
    public void setBackground(Image image) {
        final Background bg;
        if (image == null) {
            bg = null;
        } else {
            final BackgroundImage bgi = new javafx.scene.layout.BackgroundImage(image, BackgroundRepeat.REPEAT,
                    BackgroundRepeat.REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT);
            bg = new Background(bgi);
        }
        workspacePane.setBackground(bg);
    }

    @Override
    public void showContent() {
        contentGroup.setVisible(true);
    }

    @Override
    public void hideContent() {
        contentGroup.setVisible(false);
    }

    @Override
    public boolean isContentVisible() {
        return contentGroup.isVisible();
    }

    private void resetViewport() {
        if (scrollPane != null) {
            scrollPane.setHvalue(0.5);
            scrollPane.setVvalue(0.5);
        }
    }

    private void setupEventTracingFilter() {
        if (glassLayer != null) {
            if (tracingEvents) {
                glassLayer.addEventFilter(InputEvent.ANY, eventTracingFilter);
            } else {
                glassLayer.removeEventFilter(InputEvent.ANY, eventTracingFilter);
            }
        }
    }

    private void traceEvent(Event e) {
        final StringBuilder sb = new StringBuilder();

        sb.append("ContentPanelController: eventType="); // NOCHECK
        sb.append(e.getEventType());
        sb.append(", target="); // NOCHECK
        sb.append(e.getTarget());
        if (e instanceof KeyEvent) {
            final KeyEvent ke = (KeyEvent) e;
            sb.append(", keyCode="); // NOCHECK
            sb.append(ke.getCode());
        }

        logger.info(sb.toString());
    }

    private final EventHandler<Event> eventTracingFilter = e -> traceEvent(e);

    @Override
    public SubScene getContentSubScene() {
        return contentSubScene;
    }

    /**
     * @treatAsPrivate Returns true if this content panel is able to display the
     *                 content ie <br/>
     *                 1) fxomDocument != null<br/>
     *                 2) (fxomDocument.getFxomRoot() == null) or
     *                 fxomDocument.getFxomRoot().isNode()<br/>
     *                 3) workspaceController.getLayoutException() == null<br/>
     *
     * @return true if this content panel is able to display the content
     */

    @Override
    public boolean isContentDisplayable() {
        final boolean result;

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument == null) {
            result = false;
        } else if (fxomDocument.getFxomRoot() == null) {
            result = true;
        } else {
            result = fxomDocument.getDisplayNodeOrSceneGraphRoot() instanceof Node
                    && getLayoutException() == null;
        }

        return result;
    }
}
