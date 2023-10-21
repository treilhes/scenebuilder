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
package com.oracle.javafx.scenebuilder.api.content.decoration;

import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.ui.misc.Workspace;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;

/**
 * @treatAsPrivate
 */
public abstract class AbstractDecoration<T> implements Decoration<T> {

    private final Workspace workspace;
    private final DocumentManager documentManager;
    private final Class<T> sceneGraphClass;
    private final Group rootNode = new Group();
    private FXOMObject fxomObject;
    private T sceneGraphObject;


    public AbstractDecoration(
            Workspace workspace,
            DocumentManager documentManager,
            Class<T> sceneGraphClass) {
        assert workspace != null;
        assert sceneGraphClass != null;

        this.workspace = workspace;
        this.documentManager = documentManager;
        this.sceneGraphClass = sceneGraphClass;

        this.rootNode.sceneProperty().addListener((ChangeListener<Scene>) (ov, v1, v2) -> rootNodeSceneDidChange());
    }

    @Override
    public void setFxomObject(FXOMObject fxomObject) {
        assert fxomObject != null;
        assert !fxomObject.getSceneGraphObject().isEmpty();
        assert sceneGraphClass.isAssignableFrom(fxomObject.getSceneGraphObject().getObjectClass());
        assert fxomObject.getFxomDocument() == documentManager.fxomDocument().get();
        this.fxomObject = fxomObject;
        this.sceneGraphObject = fxomObject.getSceneGraphObject().getAs(sceneGraphClass);
    }

//    public Content getContentPanelController() {
//        return content;
//    }

    @Override
    public FXOMObject getFxomObject() {
        return fxomObject;
    }

    public T getSceneGraphObject() {
        return (T)fxomObject.getSceneGraphObject().get();
    }

    @Override
    public Group getRootNode() {
        return rootNode;
    }

    @Override
    public State getState() {
        final State result;

        if (fxomObject.getSceneGraphObject().get() == sceneGraphObject) {
            result = State.CLEAN;
        } else if (fxomObject.getSceneGraphObject().isEmpty()) {
            // Scene graph object became unresolved !
            result = State.NEEDS_REPLACE;
        } else if (fxomObject.getSceneGraphObject().getObjectClass() == sceneGraphClass) {
            result = State.NEEDS_RECONCILE;
        } else {
            result = State.NEEDS_REPLACE;
        }

        return result;
    }

    @Override
    public void reconcile() {
        assert getState() == State.NEEDS_RECONCILE;

        stopListeningToSceneGraphObject();
        updateSceneGraphObject();
        startListeningToSceneGraphObject();
        layoutDecoration();
    }


    @Override
    public void update() {}

    @Override
    public void setEnabled(boolean enabled) {
        // always enabled by default
    }

    public Point2D sceneGraphObjectToDecoration(double x, double y, boolean snapToPixel) {
        Point2D result = sceneGraphObjectToDecoration(x, y);
        if (snapToPixel) {
            final double rx = Math.round(result.getX());
            final double ry = Math.round(result.getY());
            result = new Point2D(rx, ry);
        }
        return result;
    }

    public Transform getSceneGraphObjectToDecorationTransform() {
        final Node proxy = getSceneGraphObjectProxy();
        final SubScene contentSubScene = workspace.getContentSubScene();
        final Transform t0 = proxy.getLocalToSceneTransform();
        final Transform t1 = contentSubScene.getLocalToSceneTransform();
        final Transform t2 = getRootNode().getLocalToSceneTransform();
        final Transform result;

        try {
            final Transform i2 = t2.createInverse();
            result = i2.createConcatenation(t1).createConcatenation(t0);
        } catch(NonInvertibleTransformException x) {
            throw new RuntimeException(x);
        }

        return result;
    }

    public abstract Bounds getSceneGraphObjectBounds();
    public abstract Node getSceneGraphObjectProxy();
    public abstract FXOMObject getFxomObjectProxy();
    protected abstract void startListeningToSceneGraphObject();
    protected abstract void stopListeningToSceneGraphObject();
    protected abstract void layoutDecoration();


    /*
     * Utilities for subclasses
     */

    public Point2D sceneGraphObjectToDecoration(double x, double y) {
        final FXOMObject proxy = getFxomObjectProxy();
//
//        Point2D resultNew = null;
//        boolean check = true;
//        if (check) {
//            Point2D sceneXY = fxomObject.localToScene(new Point2D(x, y));
//            resultNew = getRootNode().sceneToLocal(sceneXY, true /* rootScene */);
//            System.out.println("NEW " + resultNew.getX() + "," + resultNew.getY());
//        }
//        Point2D resultOld = Deprecation.localToLocal(proxy, x, y, getRootNode());
//        System.out.println("OLD " + resultOld.getX() + "," + resultOld.getY());
//
//        if (!resultOld.equals(resultNew)) {
//            System.out.println("BUG");
//        }
        //return resultOld;
        //return resultNew;
        //return CoordinateHelper.localToLocal(getFxomObject(), x, y, getRootNode());
        return CoordinateHelper.localToLocal(proxy, x, y, getRootNode());
    }

    protected void startListeningToLayoutBounds(Node node) {
        assert node != null;
        node.layoutBoundsProperty().addListener(layoutBoundsListener);
    }

    protected void stopListeningToLayoutBounds(Node node) {
        assert node != null;
        node.layoutBoundsProperty().removeListener(layoutBoundsListener);
    }

    protected void startListeningToBoundsInParent(Node node) {
        assert node != null;
        node.boundsInParentProperty().addListener(boundsInParentListener);
    }

    protected void stopListeningToBoundsInParent(Node node) {
        assert node != null;
        node.boundsInParentProperty().removeListener(boundsInParentListener);
    }

    protected void startListeningToLocalToSceneTransform(Node node) {
        assert node != null;
        node.localToSceneTransformProperty().addListener(localToSceneTransformListener);
        node.sceneProperty().addListener(sceneListener);
        final SubScene contentSubScene = workspace.getContentSubScene();
        contentSubScene.localToSceneTransformProperty().addListener(localToSceneTransformListener);
    }

    protected void stopListeningToLocalToSceneTransform(Node node) {
        assert node != null;
        node.localToSceneTransformProperty().removeListener(localToSceneTransformListener);
        node.sceneProperty().removeListener(sceneListener);
        final SubScene contentSubScene = workspace.getContentSubScene();
        contentSubScene.localToSceneTransformProperty().removeListener(localToSceneTransformListener);
    }

    /*
     * Protected
     */

    protected void rootNodeSceneDidChange() {
        if (rootNode.getScene() == null) {
            stopListeningToSceneGraphObject();
        } else {
            startListeningToSceneGraphObject();
            layoutDecoration();
        }
    }

    protected void updateSceneGraphObject() {
        this.sceneGraphObject = sceneGraphClass.cast(fxomObject.getSceneGraphObject());
    }

    /**
     * Computes the transform that projects from local coordinates of a
     * scene graph object to the rudder layer local coordinates.
     * @param fxomObject a scene graph object
     * @return transform from sceneGraphObject local coordinates to rudder local coordinates
     */
    public Transform computeSceneGraphToLayerTransform(FXOMObject fxomObject) {
        assert fxomObject != null;
        assert !fxomObject.getSceneGraphObject().isEmpty();
        assert fxomObject.getSceneGraphObject().isNode();

        // not needed for now CoordinateHelper does not depend on scene
        // more shapes does not have any scene but clip does
        //assert ((Node)fxomObject.getSceneGraphObject()).getScene() == getRootNode().getScene();

        final Transform t1 = CoordinateHelper.localToSceneTransform(fxomObject);
        final Transform t2 = workspace.getContentSubScene().getLocalToSceneTransform();
        final Transform t3 = getRootNode().getLocalToSceneTransform();
        final Transform result;

        try {
            final Transform i3 = t3.createInverse();
            result = i3.createConcatenation(t2).createConcatenation(t1);
        } catch(NonInvertibleTransformException x) {
            throw new RuntimeException(x);
        }

        return result;
    }

    /*
     * Private
     */

    private final ChangeListener<Bounds> layoutBoundsListener
        = (ov, v1, v2) -> layoutDecoration();

    private final ChangeListener<Bounds> boundsInParentListener
        = (ov, v1, v2) -> layoutDecoration();

    private final ChangeListener<Transform> localToSceneTransformListener
        = (ov, v1, v2) -> layoutDecoration();

    private final ChangeListener<Scene> sceneListener
        = (ov, v1, v2) -> layoutDecoration();

}
