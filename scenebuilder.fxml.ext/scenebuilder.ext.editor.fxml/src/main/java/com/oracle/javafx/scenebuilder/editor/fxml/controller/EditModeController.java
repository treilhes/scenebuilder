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
package com.oracle.javafx.scenebuilder.editor.fxml.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Gesture;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.content.mode.AbstractModeController;
import com.oracle.javafx.scenebuilder.api.content.mode.Layer;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.Pring;
import com.oracle.javafx.scenebuilder.api.control.ResizeGuide;
import com.oracle.javafx.scenebuilder.api.control.Rudder;
import com.oracle.javafx.scenebuilder.api.control.Shadow;
import com.oracle.javafx.scenebuilder.api.control.Tring;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.api.control.outline.Outline;
import com.oracle.javafx.scenebuilder.api.control.pring.AbstractPring;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.StringUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.RootDropTarget;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.DragGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.ZoomGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse.SelectAndMoveGesture;
import com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse.SelectWithMarqueeGesture;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.key.MoveWithKeyGesture;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.DragEvent;
import javafx.scene.input.InputEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ZoomEvent;
import javafx.util.Callback;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class EditModeController extends AbstractModeController implements Gesture.Observer {

    private final static Logger logger = LoggerFactory.getLogger(EditModeController.class);

    public final static Object ID = EditModeController.class;

    public final static Class<Outline> OUTLINE_LAYER = Outline.class;
    public final static Class<Shadow> SHADOW_LAYER = Shadow.class;
    public final static Class<Rudder> RUDDER_LAYER = Rudder.class;
    public final static Class<ResizeGuide> RESIZE_GUIDE_LAYER = ResizeGuide.class;
    public final static Class<Pring> PRING_LAYER = Pring.class;
    public final static Class<Handles> HANDLES_LAYER = Handles.class;
    public final static Class<Tring> TRING_LAYER = Tring.class;

    // private final SceneBuilderBeanFactory context;
    private final Driver driver;

    private SelectWithMarqueeGesture selectWithMarqueeGesture;
    private SelectAndMoveGesture selectAndMoveGesture;
    private ZoomGesture zoomGesture;

    private Gesture activeGesture;
    private Gesture glassGesture;
    private FXOMInstance inlineEditedObject;

    private final JobManager jobManager;

    private final DesignHierarchyMask.Factory maskFactory;

    private final SelectWithMarqueeGesture.Factory selectWithMarqueeGestureFactory;

    private final SelectAndMoveGesture.Factory selectAndMoveGestureFactory;

    private final ZoomGesture.Factory zoomGestureFactory;

    private final MoveWithKeyGesture.Factory moveWithKeyGestureFactory;

    private final Selection selection;

    private final DragGesture.Factory dragGestureFactory;

    private final ContextMenu contextMenu;

    private final MessageLogger messageLogger;

    private final InlineEdit inlineEdit;

    private final ModifyObjectJob.Factory modifyObjectJobFactory;

    private final DocumentManager documentManager;

    // @formatter:off
    public EditModeController(
            Selection selection,
            DragGesture.Factory dragGestureFactory,
            Driver driver,
            Drag drag,
            ContextMenu contextMenu,
            MessageLogger messageLogger,
            InlineEdit inlineEdit,
            @Lazy Content contentPanelController,
            JobManager jobManager,
            DesignHierarchyMask.Factory maskFactory,
            DocumentManager documentManager,
            SelectWithMarqueeGesture.Factory selectWithMarqueeGestureFactory,
            SelectAndMoveGesture.Factory selectAndMoveGestureFactory,
            ZoomGesture.Factory zoomGestureFactory,
            MoveWithKeyGesture.Factory moveWithKeyGestureFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory
            ) {
     // @formatter:on
        super(contentPanelController);
        this.selection = selection;
        this.contextMenu = contextMenu;
        this.messageLogger = messageLogger;
        this.inlineEdit = inlineEdit;
        this.dragGestureFactory = dragGestureFactory;
        this.driver = driver;
        this.jobManager = jobManager;
        this.maskFactory = maskFactory;
        this.documentManager = documentManager;

        this.selectWithMarqueeGestureFactory = selectWithMarqueeGestureFactory;
        this.selectAndMoveGestureFactory = selectAndMoveGestureFactory;
        this.zoomGestureFactory = zoomGestureFactory;
        this.moveWithKeyGestureFactory = moveWithKeyGestureFactory;
        this.modifyObjectJobFactory = modifyObjectJobFactory;

        newLayer(OUTLINE_LAYER, true, selection,
                // object selection
                s -> collectNodesToOutline(),
                // Handles creation
                fxomObject -> driver.makeOutline(fxomObject));

        newLayer(SHADOW_LAYER, true, selection,
                // object selection
                s -> s.isEmpty() ? new HashSet<>() : s.getGroup().getItems(),
                // Handles creation
                fxomObject -> driver.makeShadow(fxomObject));

        newLayer(RUDDER_LAYER, false, selection,
                // object selection
                s -> s.getAncestor() == null ? new HashSet<>() : new HashSet<>(Arrays.asList(s.getAncestor())),
                // Handles creation
                fxomObject -> driver.makeRudder(fxomObject));

        newLayer(RESIZE_GUIDE_LAYER, true, selection,
                // object selection
                s -> s.isEmpty() ? new HashSet<>() : s.getGroup().getItems(),
                // Handles creation
                fxomObject -> driver.makeResizeGuide(fxomObject));

        newLayer(PRING_LAYER, false, selection,
                // object selection
                s -> s.getAncestor() == null ? new HashSet<>() : new HashSet<>(Arrays.asList(s.getAncestor())),
                // pring creation
                fxomObject -> {
                    Pring<?> pring = driver.makePring(fxomObject);
//                    if (pring != null) {
//                        pring.changeStroke(contentPanelController.getPringColor());
//                    }
                    return pring;
                });

        newLayer(HANDLES_LAYER, false, selection,
                // object selection
                s -> s.isEmpty() ? new HashSet<>() : s.getGroup().getItems(),
                // Handles creation
                fxomObject -> driver.makeHandles(fxomObject));

        newLayer(TRING_LAYER, true, selection,
                s -> drag.isDropAccepted() && !(drag.getDropTarget() instanceof RootDropTarget)
                        ? new HashSet<>(Arrays.asList(drag.getDropTarget().getTargetObject()))
                        : null,
                fxomObject -> {
                    Tring<?> tring = driver.makeTring(drag.getDropTarget());
//                    if (tring != null) {
//                        tring.changeStroke(contentPanelController.getPringColor());
//                    }
                    return tring;
                });

    }

    @Override
    public Object getModeId() {
        return ID;
    }

    /*
     * AbstractGesture.Observer
     */

    @Override
    public void gestureDidTerminate(Gesture gesture) {
        assert activeGesture == gesture;
        activeGesture = null;
        startListeningToInputEvents();
        content.endInteraction();

        // Object below the mouse may have changed : current glass gesture
        // must be searched again.
        this.glassGesture = null;
    }

    /*
     * AbstractModeController
     */

    @Override
    public void willResignActive(AbstractModeController nextModeController) {
        stopListeningToInputEvents();
        clearLayers();
    }

    @Override
    public void didBecomeActive(AbstractModeController previousModeController) {
        assert content.getGlassLayer() != null;

        if (this.selectWithMarqueeGesture == null) {
            this.selectWithMarqueeGesture = selectWithMarqueeGestureFactory.getGesture();
            this.selectAndMoveGesture = selectAndMoveGestureFactory.getGesture();
            this.zoomGesture = zoomGestureFactory.getGesture();
        }

        getLayers().forEach(l -> l.enable());

        editorSelectionDidChange();
        startListeningToInputEvents();
    }

    @Override
    public void editorSelectionDidChange() {
        getLayer(Pring.class).update();
        getLayer(Handles.class).update();
        makeSelectionVisible();
    }

    @Override
    public void fxomDocumentDidChange(FXOMDocument oldDocument) {
        // Same logic as when the scene graph is changed
        fxomDocumentDidRefreshSceneGraph();
    }

    @Override
    public void fxomDocumentDidRefreshSceneGraph() {
        getLayer(Pring.class).update();
        getLayer(Handles.class).update();

        // Object below the mouse may have changed : current glass gesture
        // must searched again.
        this.glassGesture = null;
    }

    @Override
    public void dropTargetDidChange() {
        getLayer(Tring.class).update();
    }

    /*
     * Private
     */

    private void makeSelectionVisible() {

        // Scrolls the content panel so that selected objects are visible.
        content.scrollToSelection();

        // Walks trough the ancestor nodes of the first selected object and
        // makes sure that TabPane and Accordion are setup for displaying
        // this selected object.
        Layer<Handles> layer = getLayer(Handles.class);
        if (layer.getActiveItems().isEmpty() == false) {
            content.reveal(layer.getActiveItems().get(0).getFxomObject());
        }
    }

    private void startListeningToInputEvents() {
        final Node glassLayer = content.getGlassLayer();
        assert glassLayer.getOnMouseEntered() == null;
        assert glassLayer.getOnMouseExited() == null;
        assert glassLayer.getOnMouseMoved() == null;
        assert glassLayer.getOnMousePressed() == null;
        assert glassLayer.getOnKeyPressed() == null;
        assert glassLayer.getOnZoomStarted() == null;
        assert glassLayer.getOnDragEntered() == null;

        glassLayer.setOnMouseEntered(e -> mouseEnteredGlassLayer(e));
        glassLayer.setOnMouseExited(e -> mouseExitedGlassLayer(e));
        glassLayer.setOnMouseMoved(e -> mouseMovedOnGlassLayer(e));
        glassLayer.setOnMousePressed(e -> mousePressedOnGlassLayer(e));
        glassLayer.setOnKeyPressed(e -> keyPressedOnGlassLayer(e));
        glassLayer.setOnZoomStarted(e -> zoomStartedOnGlassLayer(e));
        glassLayer.setOnDragEntered(e -> dragEnteredGlassLayer(e));

        final Layer<Handles> handleLayer = getLayer(Handles.class);
        assert handleLayer.getOnMousePressed() == null;
        handleLayer.setOnMousePressed(e -> mousePressedOnHandleLayer(e));

        final Layer<Pring> pringLayer = getLayer(Pring.class);
        assert pringLayer.getOnMousePressed() == null;
        pringLayer.setOnMousePressed(e -> mousePressedOnPringLayer(e));
    }

    private void stopListeningToInputEvents() {

        final Node glassLayer = content.getGlassLayer();
        glassLayer.setOnMouseEntered(null);
        glassLayer.setOnMouseExited(null);
        glassLayer.setOnMouseMoved(null);
        glassLayer.setOnMousePressed(null);
        glassLayer.setOnKeyPressed(null);
        glassLayer.setOnZoomStarted(null);
        glassLayer.setOnDragEntered(null);

        final Layer<Handles> handleLayer = getLayer(Handles.class);
        handleLayer.setOnMousePressed(null);

        final Layer<Pring> pringLayer = getLayer(Pring.class);
        pringLayer.setOnMousePressed(null);
    }

    /*
     * Private (event handlers)
     */

    private void mouseEnteredGlassLayer(MouseEvent e) {
        mouseMovedOnGlassLayer(e);
    }

    private void mouseExitedGlassLayer(MouseEvent e) {
        assert activeGesture == null : "activeGesture=" + activeGesture;
        glassGesture = null;
    }

    private void mouseMovedOnGlassLayer(MouseEvent e) {
        assert activeGesture == null : "activeGesture=" + activeGesture;

        final FXOMObject hitObject = content.pick(e.getSceneX(), e.getSceneY());
        final FXOMObject selectionAncestor = selection.getAncestor();

        // The code below handles selction of detached graph objects
        if (!selection.isEmpty() && selection.getGroup() instanceof ObjectSelectionGroup) {
            ObjectSelectionGroup selGroup = (ObjectSelectionGroup) selection.getGroup();

            if (selGroup.getItems().size() == 1 && selGroup.getHitItem().isViewable()
                    && selGroup.getHitItem().isDescendantOf(hitObject)
                    && CoordinateHelper.isHit(selGroup.getHitItem(), e.getSceneX(), e.getSceneY())) {
                selectAndMoveGesture.setHitObject(selGroup.getHitItem());
                selectAndMoveGesture.setHitSceneX(e.getSceneX());
                selectAndMoveGesture.setHitSceneY(e.getSceneY());
                glassGesture = selectAndMoveGesture;
                return;
            }

        }

        /*
         * 1) hitObject == null => mouse is over the workspace/background => mouse
         * press+drag should "select with marquee"
         *
         * 2) hitObject != null
         *
         * 2.1) hitObject == root object
         *
         * 2.1) hitObject is the selectionAncestor => mouse is over the
         * "parent ring object" => mouse press+drag should "select with marquee"
         *
         * 2.2) hitObject is not the selectionAncestor => mouse is over an object =>
         * this object is inside or outside of the parent ring => mouse press+drag
         * should "select and move"
         *
         */

        if (hitObject == null) {
            // Case #1
            selectWithMarqueeGesture.setup(null, selectionAncestor);
            glassGesture = selectWithMarqueeGesture;
        } else if (hitObject == selectionAncestor) {
            // Case #2.1
            selectWithMarqueeGesture.setup(selectionAncestor, selectionAncestor);
            glassGesture = selectWithMarqueeGesture;
        } else {
            // Case #2.2
            selectAndMoveGesture.setHitObject(hitObject);
            selectAndMoveGesture.setHitSceneX(e.getSceneX());
            selectAndMoveGesture.setHitSceneY(e.getSceneY());
            glassGesture = selectAndMoveGesture;
        }
    }

    private void mousePressedOnGlassLayer(MouseEvent e) {

        // Make sure that glass layer has keyboard focus
        content.getGlassLayer().requestFocus();

        /*
         * At that point, is expected that a "mouse entered" or "mouse moved" event was
         * received before and that this.glassGesture is setup.
         *
         * However this is no always the case. It may be null in two cases: 1) on Linux,
         * mouse entered/moved events are not always delivered before mouse pressed
         * event (see DTL-5956). 2) while the mouse is immobile,
         * fxomDocumentDidRefreshSceneGraph() method may have been invoked and reset
         * this.glassGesture.
         *
         * That is why we test this.glassGesture and manually invoke
         * mouseMovedOnGlassLayer() here.
         */
        if (glassGesture == null) {
            mouseMovedOnGlassLayer(e);
        }

        assert glassGesture != null;
        switch (e.getClickCount()) {
        case 1:
            if (e.getButton() == MouseButton.SECONDARY) {
                // Update the selection (see spec detailed in DTL-5640)
                final FXOMObject hitObject;
                if (glassGesture == selectAndMoveGesture) {
                    hitObject = selectAndMoveGesture.getHitObject();
                } else {
                    assert glassGesture == selectWithMarqueeGesture;
                    hitObject = selectWithMarqueeGesture.getHitObject();
                }

                if (hitObject != null && selection.isSelected(hitObject) == false) {
                    selection.select(hitObject);
                }

                // The context menu items depend on the selection so
                // we need to rebuild it each time it is invoked.
                contextMenu.updateContextMenuItems();
            } else {
                activateGesture(glassGesture, e);
            }
            break;
        case 2:
            mouseDoubleClickedOnGlassLayer(e);
            break;
        default:
            // We ignore triple clicks and upper...
            break;
        }
        e.consume();
    }

    private void mouseDoubleClickedOnGlassLayer(MouseEvent e) {
        assert activeGesture == null;
        assert (glassGesture == selectAndMoveGesture) || (glassGesture == selectWithMarqueeGesture);

        if (glassGesture == selectAndMoveGesture) {
            assert selectAndMoveGesture.getHitObject() instanceof FXOMInstance;
            final FXOMInstance hitObject = (FXOMInstance) selectAndMoveGesture.getHitObject();
            final HierarchyMask m = maskFactory.getMask(hitObject);
            // Do not allow inline editing of the I18N value
            if (m.isResourceKey(m.getPropertyNameForDescription()) == false) {
                handleInlineEditing((FXOMInstance) selectAndMoveGesture.getHitObject());
            } else {
                messageLogger.logWarningMessage("log.warning.inline.edit.internationalized.strings");
            }
        }
    }

    private void handleInlineEditing(FXOMInstance hitObject) {

        assert hitObject != null;
        assert inlineEditedObject == null;

        final Node inlineEditingBounds = driver.getInlineEditorBounds(hitObject);

        if (inlineEditingBounds != null) {
            inlineEditedObject = hitObject;


            final HierarchyMask m = maskFactory.getMask(inlineEditedObject);
            final String text = m.getDescription();
            final InlineEdit.Type type;
            if (inlineEditingBounds instanceof TextArea || StringUtils.containsLineFeed(text)) {
                type = InlineEdit.Type.TEXT_AREA;
            } else {
                type = InlineEdit.Type.TEXT_FIELD;
            }
            final TextInputControl inlineEditor = inlineEdit.createTextInputControl(type, inlineEditingBounds, text);

            // CSS
            final ObservableList<String> styleSheets = getContent().getRoot().getStylesheets();
            inlineEditor.getStylesheets().addAll(styleSheets);
            inlineEditor.getStyleClass().add("theme-presets"); // NOCHECK
            inlineEditor.getStyleClass().add(InlineEdit.INLINE_EDITOR_CLASS);

            final Callback<String, Boolean> requestCommit = this::inlineEditingDidRequestCommit;
            final Callback<String, Boolean> requestRevert = this::inlineEditingDidRequestRevert;

            inlineEdit.startEditingSession(inlineEditor, inlineEditingBounds, requestCommit, requestRevert);
        } else {
            logger.debug("Beep");
        }

        assert inlineEdit.isTextEditingSessionOnGoing() || (inlineEditedObject == null);
    }

    private boolean inlineEditingDidRequestCommit(String newValue) {
        assert inlineEditedObject != null;

        final HierarchyMask m = maskFactory.getMask(inlineEditedObject);
        final PropertyName propertyName = m.getPropertyNameForDescription();
        assert propertyName != null;
        final ValuePropertyMetadata vpm = m.getPropertyMetadata(propertyName);

        final AbstractJob job = modifyObjectJobFactory.getJob(inlineEditedObject, vpm, newValue);

        if (job.isExecutable()) {
            jobManager.push(job);
        }

        inlineEditedObject = null;

        return true;
    }

    private boolean inlineEditingDidRequestRevert(String currentValue) {
        assert inlineEditedObject != null;
        inlineEditedObject = null;
        return true;
    }

    private void keyPressedOnGlassLayer(KeyEvent e) {
        assert activeGesture == null : "activeGesture=" + activeGesture;
        switch (e.getCode()) {
        case UP:
        case DOWN:
        case LEFT:
        case RIGHT:
            if (selection.isMovable()) {
                activateGesture(moveWithKeyGestureFactory.getGesture(), e);
            } else {
                logger.debug("Selection is not movable");
            }
            e.consume();
            break;
        case ENTER:
            if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
                if (osg.getItems().size() == 1) {
                    final HierarchyMask mask = maskFactory.getMask(osg.getSortedItems().get(0));
                    final FXOMObject nodeFxomObject = mask.getClosestFxNode();
                    if (nodeFxomObject instanceof FXOMInstance) {
                        handleInlineEditing((FXOMInstance) nodeFxomObject);
                    }
                }
            }
            break;
        default:
            // We let other key events flow up in the scene graph
            break;
        }
    }

    private void zoomStartedOnGlassLayer(ZoomEvent e) {
        activateGesture(zoomGesture, e);
        e.consume();
    }

    private void dragEnteredGlassLayer(DragEvent e) {
        activateGesture(dragGestureFactory.getGesture(), e);
    }

    private void mousePressedOnHandleLayer(MouseEvent e) {
        assert e.getTarget() instanceof Node;

        if (e.getButton() == MouseButton.SECONDARY) {
            // The context menu items depend on the selection so
            // we need to rebuild it each time it is invoked.
            contextMenu.updateContextMenuItems();
        } else {
            final Node target = (Node) e.getTarget();
            Node hitNode = target;
            Handles<?> hitHandles = AbstractHandles.lookupHandles(hitNode);
            while ((hitHandles == null) && (hitNode.getParent() != null)) {
                hitNode = hitNode.getParent();
                hitHandles = AbstractHandles.lookupHandles(hitNode);
            }

            if (hitHandles != null) {
                activateGesture(hitHandles.findEnabledGesture(hitNode), e);
            } else {
                // Emergency code
                assert false : "event target has no HANDLES property :" + target;
            }
        }
        e.consume();
    }

    private void mousePressedOnPringLayer(MouseEvent e) {
        assert e.getTarget() instanceof Node;

        final Node target = (Node) e.getTarget();
        Node hitNode = target;
        Pring<?> hitPring = AbstractPring.lookupPring(target);
        while ((hitPring == null) && (hitNode.getParent() != null)) {
            hitNode = hitNode.getParent();
            hitPring = AbstractPring.lookupPring(hitNode);
        }

        if (hitPring != null) {
            activateGesture(hitPring.findGesture(hitNode), e);
        } else {
            // Emergency code
            assert false : "event target has no PRING property :" + target;
        }
        e.consume();
    }

    private void activateGesture(Gesture gesture, InputEvent e) {
        assert activeGesture == null : "activeGesture=" + activeGesture;
        if (gesture == null) {
            return;
        }
        /*
         * Before activating the gesture, we check: - that there is a document attached
         * to the editor controller - if a text session is on-going and can be completed
         * cleanly. If not, we do not activate the gesture.
         */

        if (content.isContentDisplayable() && inlineEdit.canGetFxmlText()) {

            content.beginInteraction();

            stopListeningToInputEvents();
            activeGesture = gesture;
            gesture.start(e, this);

            // Note that some gestures may terminates immediately.
            // So activeGesture may have switch back to null.
            assert (activeGesture == gesture) || (activeGesture == null);
        }
    }

    private Set<FXOMObject> collectNodesToOutline() {
        final Set<FXOMObject> result = new HashSet<>();

        final List<FXOMObject> candidates = new ArrayList<>();
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        if ((fxomDocument != null) && (fxomDocument.getFxomRoot() != null)) {
            candidates.add(fxomDocument.getFxomRoot());
        }

        while (candidates.isEmpty() == false) {
            final FXOMObject candidate = candidates.get(0);
            candidates.remove(0);
            if (candidate.isNode()) {
                final Node sgo = (Node) candidate.getSceneGraphObject();
                // if (sgo.getScene() == getRoot().getScene()) {
                if (sgo.getScene() == ((Node) fxomDocument.getSceneGraphRoot()).getScene()) {
                    result.add(candidate);
                }
            }
            final HierarchyMask m = maskFactory.getMask(candidate);

            List<Accessory> allAccessories = new ArrayList<>(m.getAccessories());
            if (m.getMainAccessory() != null && !allAccessories.contains(m.getMainAccessory())) {
                allAccessories.add(0, m.getMainAccessory());
            }

            for (Accessory a : allAccessories) {
                final List<FXOMObject> accessoryObjects = m.getAccessories(a, false);
                if (accessoryObjects != null) {
                    accessoryObjects.stream().filter(accessoryObject -> accessoryObject != null && accessoryObject.isNode())
                    .forEach(accessoryObject -> {
                        candidates.add(accessoryObject);
                    });
                }

            }
        }

        return result;
    }

}
