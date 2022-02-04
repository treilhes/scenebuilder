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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.CardinalPoint;
import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HudWindow;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.control.CurveEditor;
import com.oracle.javafx.scenebuilder.api.control.Driver;
import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide;
import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.api.control.Handles;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractHandles;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class EditCurveGesture extends AbstractMouseGesture {

    private FXOMInstance fxomInstance;

    private CurveEditor<?> editor;
    private EditCurveGuide controller;

    private boolean straightAnglesMode = false;

    private static final PropertyName POINTS_NAME = new PropertyName("points"); //NOCHECK
    private static final int MAX_POINTS_HUD = 24;

    private final EnumMap<Tunable, Integer> tunableMap = new EnumMap<>(Tunable.class);

	private final Metadata metadata;
	private final DesignHierarchyMask.Factory designMaskFactory;
	private final JobManager jobManager;
	private final Driver driver;
	private final HudWindow hudWindow;
	private final DocumentManager documentManager;
	private final ModifyObjectJob.Factory modifyObjectJobFactory;

	private Parent closestParent;

	protected EditCurveGesture(
	        Content contentPanelController,
	        Metadata metadata,
	        Driver driver,
	        DesignHierarchyMask.Factory designMaskFactory,
	        JobManager jobManager,
	        DocumentManager documentManager,
	        HudWindow hudWindow,
	        ModifyObjectJob.Factory modifyObjectJobFactory) {
        super(contentPanelController);
        this.metadata = metadata;
        this.designMaskFactory = designMaskFactory;
        this.driver = driver;
        this.hudWindow = hudWindow;
        this.jobManager = jobManager;
        this.documentManager = documentManager;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
    }

	protected void setupGestureParameters(FXOMInstance fxomInstance, Tunable tunable) {
        assert fxomInstance.getSceneGraphObject() instanceof Node;
        this.fxomInstance = fxomInstance;
        this.editor = driver.makeCurveEditor(fxomInstance);
        tunableMap.put(tunable, -1);

        FXOMObject parent = fxomInstance.getClosestParent();
        if (parent != null && parent.getSceneGraphObject() != null) {
            this.closestParent = ((Parent)parent.getSceneGraphObject());
        } else {
            this.closestParent = null;
        }
        assert closestParent != null;
    }

    public EnumMap<Tunable, Integer> getTunableMap() {
        return tunableMap;
    }

    /*
     * AbstractMouseGesture
     */

    private boolean inserted, removed;
    private Point2D insertionPoint;

    @Override
    protected void mousePressed() {
        final MouseEvent mousePressedEvent = getMousePressedEvent();
        inserted = false;
        removed = false;
        if (tunableMap.containsKey(Tunable.SIDE) && mousePressedEvent.isShortcutDown()) {
            final double hitX = mousePressedEvent.getSceneX();
            final double hitY = mousePressedEvent.getSceneY();
            insertionPoint = CoordinateHelper.sceneToLocal(editor.getFxomObject(), hitX, hitY, true);
            inserted = true;
        } else if (tunableMap.containsKey(Tunable.VERTEX) && mousePressedEvent.isShortcutDown()) {
            removed = true;
        }
        updateHandle(true);
    }

    @Override
    protected void mouseDragStarted() {
        assert editor != null;
        assert editor.getSceneGraphObject() == fxomInstance.getSceneGraphObject();

        controller = editor.createController(tunableMap);

        final double hitX = getLastMouseEvent().getSceneX();
        final double hitY = getLastMouseEvent().getSceneY();
        final Set<FXOMObject> pickExcludes = new HashSet<>();
        pickExcludes.add(fxomInstance);

        FXOMObject hitParent = contentPanelController.pick(hitX, hitY, pickExcludes);
        if (hitParent == null) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            hitParent = fxomDocument.getFxomRoot();
        }

        assert hitParent != null;

        HierarchyMask hitParentMask = designMaskFactory.getMask(hitParent);

        // no free child positioning is not needed here
        //assert hitParentMask.getMainAccessory() != null && hitParentMask.getMainAccessory().isFreeChildPositioning();

        for (int i = 0, c = hitParentMask.getSubComponentCount(); i < c; i++) {
            final FXOMObject child = hitParentMask.getSubComponentAtIndex(i);
            final boolean isNode = child.getSceneGraphObject() instanceof Node;
            if (isNode && child != fxomInstance) {
                final Node childNode = (Node) child.getSceneGraphObject();
                controller.addSampleBounds(childNode);
            }
        }

        assert hitParent.getSceneGraphObject() instanceof Node;
        final Node hitParentNode = (Node) hitParent.getSceneGraphObject();
        controller.addSampleBounds(hitParentNode);

        setupAndOpenHudWindow();

        mouseDragged();
    }

    @Override
    protected void mouseDragged() {
        hudWindow.updatePopupLocation();
        updateCurvePosition();
    }

    @Override
    protected void mouseDragEnded() {
        final Map<PropertyName, Object> changeMap = editor.getChangeMap();
        List<Double> points = null;
        if (editor.getPoints() != null) {
            points = new ArrayList<>(editor.getPoints());
        }
        userDidCancel();

        final Map<ValuePropertyMetadata, Object> metaValueMap = new HashMap<>();
        for (Map.Entry<PropertyName,Object> e : changeMap.entrySet()) {
            final ValuePropertyMetadata vpm = metadata.queryValueProperty(fxomInstance, e.getKey());
            assert vpm != null;
            metaValueMap.put(vpm, e.getValue());
        }
        if (!changeMap.isEmpty()) {
            for (Map.Entry<ValuePropertyMetadata, Object> e : metaValueMap.entrySet()) {
                final AbstractJob job = modifyObjectJobFactory.getJob("Edit",fxomInstance,e.getKey(),e.getValue());
                if (job.isExecutable()) {
                    jobManager.push(job);
                }
            }
        }

        if (points != null) {
            final ValuePropertyMetadata pointsMeta
                = metadata.queryValueProperty(fxomInstance, POINTS_NAME);
            final AbstractJob job = modifyObjectJobFactory.getJob(fxomInstance,pointsMeta,points);
            if (job.isExecutable()) {
                jobManager.push(job);
            }
        }
    }

    @Override
    protected void mouseReleased() {
        updateHandle(false);
        if (removed || inserted) {
            if (removed) {
                editor.removePoint(tunableMap);
            } else if (inserted) {
                editor.addPoint(tunableMap, insertionPoint.getX(), insertionPoint.getY());
            }

            List<Double> points = null;
            if (editor.getPoints() != null) {
                points = editor.getPoints().stream().collect(Collectors.toList());
            }
            userDidCancel();

            final Metadata metadata = Api.get().getMetadata();
            if (points != null) {
                final ValuePropertyMetadata pointsMeta = metadata.queryValueProperty(fxomInstance, POINTS_NAME);
                final AbstractJob job = modifyObjectJobFactory.getJob(fxomInstance,pointsMeta,points);
                if (job.isExecutable()) {
                    jobManager.push(job);
                }
            }
        }
    }

    @Override
    protected void keyEvent(KeyEvent e) {
        if (e.getCode() == KeyCode.SHIFT) {
            if (e.getEventType() == KeyEvent.KEY_PRESSED) {
                straightAnglesMode = true;
            } else if (e.getEventType() == KeyEvent.KEY_RELEASED) {
                straightAnglesMode = false;
            }
            mouseDragged();
        }
   }

    @Override
    protected void userDidCancel() {
        editor.revertToOriginalState();
        closestParent.layout();
        hudWindow.closeWindow();
    }

    private void updateCurvePosition() {
        if (editor == null || controller == null) {
            return;
        }
        //final Node sceneGraphObject = editor.getSceneGraphObject();
        closestParent.layout();

        final double currentSceneX = getLastMouseEvent().getSceneX();
        final double currentSceneY = getLastMouseEvent().getSceneY();
        Point2D current = new Point2D(currentSceneX, currentSceneY);

        if (straightAnglesMode) {
            current = controller.makeStraightAngles(current);
        } else {
            current = controller.correct(current);
        }

        current = CoordinateHelper.sceneToLocal(editor.getFxomObject(), current.getX(), current.getY(), true);
        editor.moveTunable(tunableMap, current.getX(), current.getY());
        closestParent.layout();

        updateHudWindow();
    }

    private void updateHandle(boolean value) {
        Node hitNode = (Node) getMousePressedEvent().getTarget();
        Handles<?> hitHandles = AbstractHandles.lookupHandles(hitNode);
        while (hitHandles == null && hitNode.getParent() != null) {
            hitNode = hitNode.getParent();
            hitHandles = AbstractHandles.lookupHandles(hitNode);
        }
        if (hitNode instanceof Circle) {
            if (removed) {
                hitNode.setCursor(value ? Cursor.CROSSHAIR : Cursor.OPEN_HAND);
            } else {
                hitNode.setCursor(value ? Cursor.CLOSED_HAND : Cursor.OPEN_HAND);
            }
        }
    }

    private void setupAndOpenHudWindow() {

        final int propertiesCount = editor.getPropertyNames().size();
        final int pointsCount = editor.getPoints() != null ? Math.min(MAX_POINTS_HUD, editor.getPoints().size()) : 0;
        hudWindow.setRowCount(propertiesCount + pointsCount);

        final List<PropertyName> sizePropertyNames = editor.getPropertyNames();
        for (int i = 0; i < propertiesCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            hudWindow.setNameAtRowIndex(pn.getName() + ":", i);
        }

        for (int i = 0; i < pointsCount / 2; i++) {
            hudWindow.setNameAtRowIndex("" + (i + 1) + ".X:", 2 * i + propertiesCount);
            hudWindow.setNameAtRowIndex("" + (i + 1) + ".Y:", 2 * i + 1 + propertiesCount);
        }

        updateHudWindow();

        hudWindow.setRelativePosition(CardinalPoint.E);
        hudWindow.openWindow((Node)editor.getFxomObject().getClosestMainGraphNode().getSceneGraphObject());
    }

    private void updateHudWindow() {
        final List<PropertyName> sizePropertyNames = editor.getPropertyNames();
        final int propertiesCount = sizePropertyNames.size();

        for (int i = 0; i < propertiesCount; i++) {
            final PropertyName pn = sizePropertyNames.get(i);
            final String value = String.valueOf(editor.getValue(pn));
            hudWindow.setValueAtRowIndex(value, i);
        }

        // Limit added points to grid
        // TODO: Add another column to the grid
        final int pointsCount = editor.getPoints() != null ? Math.min(MAX_POINTS_HUD, editor.getPoints().size()) : 0;
        if (pointsCount > 0) {
            for (int i = 0; i < pointsCount; i++) {
                hudWindow.setValueAtRowIndex(String.format("%.3f", editor.getPoints().get(i)), i + propertiesCount);
            }
        }

    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<EditCurveGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public EditCurveGesture getGesture(FXOMInstance fxomInstance, Tunable tunable) {
            return create(EditCurveGesture.class, g -> g.setupGestureParameters(fxomInstance, tunable));
        }
    }

}