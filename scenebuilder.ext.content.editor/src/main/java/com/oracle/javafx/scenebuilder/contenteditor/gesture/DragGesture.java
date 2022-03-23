/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.contenteditor.gesture;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.content.ModeManager;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.content.mode.Layer;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.contenteditor.guides.MovingGuideController;
import com.oracle.javafx.scenebuilder.core.content.util.BoundsUtils;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.ContainerXYDropTarget;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.RootDropTarget;
import com.oracle.javafx.scenebuilder.util.MathUtils;

import javafx.event.EventType;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DragGesture extends AbstractGesture {

    private static final Logger logger = LoggerFactory.getLogger(DragGesture.class);

    private final double MARGIN = 14.0;

    private final Drag dragController;
    private final Set<FXOMObject> pickExcludes = new HashSet<>();
    private DragEvent dragEnteredEvent;
    private DragEvent lastDragEvent;
    private Observer observer;
    private boolean willReceiveDragDone;
    private boolean shouldInvokeEnd;
    private FXOMObject hitParent;
    private HierarchyMask hitParentMask;
    private MovingGuideController movingGuideController;
    private boolean guidesDisabled;
    private Node shadow;

    private final Driver driver;

    @SuppressWarnings("rawtypes")
    private Layer<Rudder> rudderLayer;

    private final DesignHierarchyMask.Factory maskFactory;
    private final ExternalDragSource.Factory externalDragSourceFactory;
    private final RootDropTarget.Factory rootDropTargetFactory;

    private final DocumentManager documentManager;

    protected DragGesture(
            Content contentPanelController,
            Drag dragController,
            ModeManager modeManager,
            Driver driver,
            DocumentManager documentManager,
            DesignHierarchyMask.Factory maskFactory,
            ExternalDragSource.Factory externalDragSourceFactory,
            RootDropTarget.Factory rootDropTargetFactory) {
        super(contentPanelController);
        this.dragController = dragController;
        this.driver = driver;
        this.documentManager = documentManager;
        this.maskFactory = maskFactory;
        this.externalDragSourceFactory = externalDragSourceFactory;
        this.rootDropTargetFactory = rootDropTargetFactory;

        if (modeManager.hasModeEnabled()) {
            rudderLayer = modeManager.getEnabledMode().getLayer(Rudder.class);
        }
        assert rudderLayer != null;
    }

    /*
     * AbstractDragGesture
     */

    @Override
    public void start(InputEvent e, Observer observer) {
        assert e != null;
        assert e instanceof DragEvent;
        assert e.getEventType() == DragEvent.DRAG_ENTERED;

        final Node glassLayer = contentPanelController.getGlassLayer();
        assert glassLayer.getOnDragEntered()== null;
        assert glassLayer.getOnDragOver()== null;
        assert glassLayer.getOnDragExited()== null;
        assert glassLayer.getOnDragDropped()== null;
        assert glassLayer.getOnDragDone()== null;
        assert glassLayer.getOnKeyPressed()== null;

        glassLayer.setOnDragEntered(e1 -> {
            lastDragEvent = e1;
            dragEnteredGlassLayer();
        });
        glassLayer.setOnDragOver(e1 -> {
            lastDragEvent = e1;
            dragOverGlassLayer();
        });
        glassLayer.setOnDragExited(e1 -> {
            lastDragEvent = e1;
            dragExitedGlassLayer();
        });
        glassLayer.setOnDragDropped(e1 -> {
            lastDragEvent = e1;
            dragDroppedOnGlassLayer();
            e1.consume();
            // On Linux, "drag over" is randomly called before "drag done".
            // It's unclear whether it's an FX bug or feature.
            // To make things unambiguous, we clear the "drag over" callback.
            // See DTL-5956.
            glassLayer.setOnDragOver(null);
        });
        glassLayer.setOnDragDone(e1 -> {
            lastDragEvent = e1;
            dragDoneOnGlassLayer();
            e1.getDragboard().clear();
            e1.consume();
        });
        glassLayer.setOnKeyPressed(e1 -> handleKeyPressed(e1));

        this.dragEnteredEvent = (DragEvent) e;
        this.lastDragEvent = this.dragEnteredEvent;
        this.observer = observer;
        this.willReceiveDragDone = this.dragEnteredEvent.getGestureSource() == glassLayer;
        this.shouldInvokeEnd = willReceiveDragDone;
        assert this.hitParent == null;
        assert this.hitParentMask == null;
        assert this.shadow == null;

        setupMovingGuideController();

        dragEnteredGlassLayer();
    }


    /*
     * Private
     */

    private void dragEnteredGlassLayer() {
        if (dragController.getDragSource() == null) { // Drag started externally
            final ExternalDragSource dragSource = externalDragSourceFactory.getDragSource(lastDragEvent.getDragboard());
            assert dragSource.isAcceptable();
            dragController.begin(dragSource);
            shouldInvokeEnd = true;
        }

        // Objects being dragged should be excluded from the pick.
        // We create the exclude list here once.
        pickExcludes.clear();
        pickExcludes.addAll(dragController.getDragSource().getDraggedObjects());

        // We show the shadow
        showShadow();

        // Now same logic as dragOver
        dragOverGlassLayer();
    }

    private void dragOverGlassLayer() {
        /*
         * On Linux, Node.onDragOver() is sometimes called *after*
         * Node.onDragDropped() : see RT-34537.
         * We detect those illegal invocations here and ignore them.
         */

        if (lastDragEvent.isDropCompleted()) {
            logger.warn("Ignored dragOver() after dragDropped()"); //NOCHECK
        } else {
            dragOverGlassLayerBis();
        }
    }

    private void dragOverGlassLayerBis() {

        // Let's set what is below the mouse
        final double hitX = lastDragEvent.getSceneX();
        final double hitY = lastDragEvent.getSceneY();
        FXOMObject hitObject = contentPanelController.pick(hitX, hitY, pickExcludes);
        if (hitObject == null) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            hitObject = fxomDocument.getFxomRoot();
        }

        if (hitObject == null) {
            // FXOM document is empty
            dragOverEmptyDocument();
        } else {
            dragOverHitObject(hitObject);
        }
    }

    private void dragOverEmptyDocument() {
        dragController.setDropTarget(rootDropTargetFactory.getDropTarget());
        lastDragEvent.acceptTransferModes(dragController.getAcceptedTransferModes());
        updateShadow(lastDragEvent.getSceneX(), lastDragEvent.getSceneY());
    }

    private void dragOverHitObject(FXOMObject hitObject) {
        assert hitObject != null;

        logger.debug("dragOverHitObject {}", hitObject == null ? "null" : hitObject.getSceneGraphObject().getClass().getName());

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        final DragSource dragSource = dragController.getDragSource();
        final double hitX = lastDragEvent.getSceneX();
        final double hitY = lastDragEvent.getSceneY();

        assert fxomDocument != null;
        assert dragSource != null;

        DropTarget dropTarget = null;
        FXOMObject newHitParent = null;

        dropTarget = driver.makeDropTarget(hitObject, hitX, hitY);
        if (dropTarget != null) {
            newHitParent = hitObject;
        } else {
            // hitObject parent is a container ? second chance
            final FXOMObject hitObjectParent = hitObject.getParentObject();
            if (hitObjectParent != null) {
                dropTarget = driver.makeDropTarget(hitObjectParent, hitX, hitY);
                if (dropTarget != null) {
                    newHitParent = hitObjectParent;
                }
            }
        }

        // Update movingGuideController
        if (newHitParent != hitParent) {
            hitParent = newHitParent;
            hitParentMask = maskFactory.getMask(hitParent);
            if (hitParent == null) {
                assert hitParentMask == null;
                movingGuideController.clearSampleBounds();
            } else {
                assert hitParentMask != null;
                if (hitParentMask.getMainAccessory() != null
                        && hitParentMask.getMainAccessory().isFreeChildPositioning() && dragSource.isNodeOnly()) {
                    populateMovingGuideController();
                } else {
                    movingGuideController.clearSampleBounds();
                }
            }
        }

        final double guidedX, guidedY;
        if (movingGuideController.hasSampleBounds() && (guidesDisabled == false)) {
            updateShadow(hitX, hitY);
            final Bounds shadowBounds = shadow.getLayoutBounds();
            final Bounds shadowBoundsInScene = shadow.localToScene(shadowBounds, true /* rootScene */);
            movingGuideController.match(shadowBoundsInScene);

            guidedX = hitX + movingGuideController.getSuggestedDX();
            guidedY = hitY + movingGuideController.getSuggestedDY();
        } else {
            guidedX = hitX;
            guidedY = hitY;
        }

        updateShadow(guidedX, guidedY);

        if (!MathUtils.equals(guidedX , hitX) || !MathUtils.equals(guidedY, hitY)) {
            assert dropTarget != null;
            assert dropTarget instanceof ContainerXYDropTarget;
            dropTarget = driver.makeDropTarget(hitParent, guidedX, guidedY); // create with new guided coord
            assert dropTarget instanceof ContainerXYDropTarget;
        }

        dragController.setDropTarget(dropTarget);
        lastDragEvent.acceptTransferModes(dragController.getAcceptedTransferModes());

    }

    private void dragExitedGlassLayer() {

        dragController.setDropTarget(null);
        hideShadow();
        movingGuideController.clearSampleBounds();

        if (willReceiveDragDone == false) {
            dragDoneOnGlassLayer();
        }
    }

    private void dragDroppedOnGlassLayer() {
        lastDragEvent.setDropCompleted(true);
        dragController.commit();
        contentPanelController.getGlassLayer().requestFocus();
    }

    private void dragDoneOnGlassLayer() {
        if (shouldInvokeEnd) {
            dragController.end();
        }
        performTermination();
    }

    private void handleKeyPressed(KeyEvent e) {
        if (e.getCode() == KeyCode.ESCAPE) {
            dragExitedGlassLayer();
            if (willReceiveDragDone) {
                // dragDone will not arrive but
                // we need to execute the corresponding logic
                dragDoneOnGlassLayer();
            }
        } else if (e.getCode() == KeyCode.ALT) {
            final EventType<KeyEvent> eventType = e.getEventType();
            if (eventType == KeyEvent.KEY_PRESSED) {
                guidesDisabled = true;
            } else if (eventType == KeyEvent.KEY_RELEASED) {
                guidesDisabled = false;
            }
            dragOverGlassLayer();
        }
    }


    private void performTermination() {
        final Node glassLayer = contentPanelController.getGlassLayer();
        glassLayer.setOnDragEntered(null);
        glassLayer.setOnDragOver(null);
        glassLayer.setOnDragExited(null);
        glassLayer.setOnDragDropped(null);
        glassLayer.setOnDragDone(null);
        glassLayer.setOnKeyPressed(null);

        dismantleMovingGuideController();

        observer.gestureDidTerminate(this);
        observer = null;

        dragEnteredEvent = null;
        lastDragEvent = null;
        shouldInvokeEnd = false;
        hitParent = null;
        hitParentMask = null;
        assert shadow == null; // Because dragExitedGlassLayer() called hideShadow()
    }

    /*
     * Shadow
     */

    private void showShadow() {
        assert shadow == null;

        shadow = dragController.getDragSource().makeShadow();
        shadow.setMouseTransparent(true);
        rudderLayer.getLayerUI().getChildren().add(shadow);

        updateShadow(0.0, 0.0);
    }

    private void updateShadow(double hitX, double hitY) {
        assert shadow != null;

        final Point2D p = rudderLayer.getLayerUI().sceneToLocal(hitX, hitY, true /* rootScene */);
        shadow.setLayoutX(p.getX());
        shadow.setLayoutY(p.getY());
    }

    private void hideShadow() {
        assert shadow != null;
        rudderLayer.getLayerUI().getChildren().remove(shadow);
        shadow = null;
    }

    /*
     * MovingGuideController
     */

    private void setupMovingGuideController() {
        final Bounds scope = contentPanelController.getWorkspacePane().getLayoutBounds();
        final Bounds scopeInScene = contentPanelController.getWorkspacePane().localToScene(scope, true /* rootScene */);
        this.movingGuideController = new MovingGuideController(
                contentPanelController.getGuidesColor(), scopeInScene);

        final Group guideGroup = movingGuideController.getGuideGroup();
        assert guideGroup.isMouseTransparent();
        rudderLayer.getLayerUI().getChildren().add(guideGroup);
    }


    private void populateMovingGuideController() {
        assert hitParentMask != null;
        assert hitParentMask.getMainAccessory() != null  && hitParentMask.getMainAccessory().isFreeChildPositioning(); // (1)

        movingGuideController.clearSampleBounds();

        // Adds N, S, E, W and center lines for each child of the hitParent
        for (int i = 0, c = hitParentMask.getSubComponentCount(); i < c; i++) {
            final FXOMObject child = hitParentMask.getSubComponentAtIndex(i);
            final boolean isNode = child.getSceneGraphObject() instanceof Node;
            if ((pickExcludes.contains(child) == false) && isNode) {
                final Node childNode = (Node) child.getSceneGraphObject();
                movingGuideController.addSampleBounds(childNode);
            }
        }

        // Adds N, S, E, W and center lines of the hitParent itself
        assert hitParent.getSceneGraphObject() instanceof Node; // Because (1)
        final Node hitParentNode = (Node) hitParent.getSceneGraphObject();
        movingGuideController.addSampleBounds(hitParentNode);

        // If bounds of hitParent are larger enough then adds the margin boundaries
        final Bounds hitParentBounds = hitParentNode.getLayoutBounds();
        final Bounds insetBounds = BoundsUtils.inset(hitParentBounds, MARGIN, MARGIN);
        if (insetBounds.isEmpty() == false) {
            final Bounds insetBoundsInScene = hitParentNode.localToScene(insetBounds, true /* rootScene */);
            movingGuideController.addSampleBounds(insetBoundsInScene, false /* addMiddle */);
        }
    }


    private void dismantleMovingGuideController() {
        assert movingGuideController != null;
        final Group guideGroup = movingGuideController.getGuideGroup();

        assert rudderLayer.getLayerUI().getChildren().contains(guideGroup);
        rudderLayer.getLayerUI().getChildren().remove(guideGroup);
        movingGuideController = null;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<DragGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public DragGesture getGesture() {
            return create(DragGesture.class, null); // g -> g.setupGestureParameters());
        }
    }

}
