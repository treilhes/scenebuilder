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
package com.oracle.javafx.scenebuilder.kit.editor.panel.content.driver.handles;

import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.AbstractGesture;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;

/**
*
* QuadCurve handles: <br><img src="doc-files/quad-curve-handles.png" alt="quad curve handles"><br>
* Appears when resizing a 
* <a href="https://openjfx.io/javadoc/15/javafx.graphics/javafx/scene/shape/QuadCurve.html">javafx.scene.shape.QuadCurve</a>
* in the Editor<br>
* Subclasses will use the same handles until a more specialized one has been registered
*/
public class QuadCurveHandles extends AbstractCurveHandles<QuadCurve> {

    private final Circle startHandle = new Circle(SELECTION_HANDLES_SIZE / 2.0);
    private final Circle controlHandle = new Circle(SELECTION_HANDLES_SIZE / 3.0);
    private final Circle endHandle = new Circle(SELECTION_HANDLES_SIZE / 2.0);
    private final Line leftHandle = new Line();
    private final Line rightHandle = new Line();
	private final ApplicationContext context;


    public QuadCurveHandles(
    		ApplicationContext context,
    		Content contentPanelController,
            FXOMInstance fxomInstance) {
        super(contentPanelController, fxomInstance, QuadCurve.class);
        this.context = context;

        setupHandleState(startHandle);
        setupHandleState(controlHandle);
        setupHandleState(endHandle);
        leftHandle.getStyleClass().add(SELECTION_WIRE);
        rightHandle.getStyleClass().add(SELECTION_WIRE);

        setupHandles(startHandle);
        setupHandles(controlHandle);
        setupHandles(endHandle);
        setupHandles(leftHandle);
        setupHandles(rightHandle);

        final List<Node> rootNodeChildren = getRootNode().getChildren();
        rootNodeChildren.add(leftHandle);
        rootNodeChildren.add(rightHandle);
        rootNodeChildren.add(startHandle);
        rootNodeChildren.add(controlHandle);
        rootNodeChildren.add(endHandle);
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }

    /*
     * AbstractCurveHandles
     */
    @Override
    protected void layoutDecoration() {
        final QuadCurve l = getSceneGraphObject();

        final boolean snapToPixel = true;
        final Point2D s = sceneGraphObjectToDecoration(l.getStartX(), l.getStartY(), snapToPixel);
        final Point2D c = sceneGraphObjectToDecoration(l.getControlX(), l.getControlY(), snapToPixel);
        final Point2D e = sceneGraphObjectToDecoration(l.getEndX(), l.getEndY(), snapToPixel);

        startHandle.setCenterX(s.getX());
        startHandle.setCenterY(s.getY());
        controlHandle.setCenterX(c.getX());
        controlHandle.setCenterY(c.getY());
        endHandle.setCenterX(e.getX());
        endHandle.setCenterY(e.getY());

        leftHandle.setStartX(s.getX());
        leftHandle.setStartY(s.getY());
        leftHandle.setEndX(c.getX());
        leftHandle.setEndY(c.getY());
        rightHandle.setStartX(c.getX());
        rightHandle.setStartY(c.getY());
        rightHandle.setEndX(e.getX());
        rightHandle.setEndY(e.getY());
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        super.startListeningToSceneGraphObject();

        final QuadCurve l = getSceneGraphObject();
        l.startXProperty().addListener(coordinateListener);
        l.startYProperty().addListener(coordinateListener);
        l.controlXProperty().addListener(coordinateListener);
        l.controlYProperty().addListener(coordinateListener);
        l.endXProperty().addListener(coordinateListener);
        l.endYProperty().addListener(coordinateListener);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        super.stopListeningToSceneGraphObject();

        final QuadCurve l = getSceneGraphObject();
        l.startXProperty().removeListener(coordinateListener);
        l.startYProperty().removeListener(coordinateListener);
        l.controlXProperty().removeListener(coordinateListener);
        l.controlYProperty().removeListener(coordinateListener);
        l.endXProperty().removeListener(coordinateListener);
        l.endYProperty().removeListener(coordinateListener);
    }

    @Override
    public AbstractGesture findGesture(Node node) {
        final AbstractGesture result;

        if (node == startHandle) {
            result = new EditCurveGesture(context, getContentPanelController(),
                    getFxomInstance(), Tunable.START);
        } else if (node == controlHandle) {
            result = new EditCurveGesture(context, getContentPanelController(),
                    getFxomInstance(), Tunable.CONTROL1);
        } else if (node == endHandle) {
            result = new EditCurveGesture(context, getContentPanelController(),
                    getFxomInstance(), Tunable.END);
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void enabledDidChange() {
        setupHandleState(startHandle);
        setupHandleState(controlHandle);
        setupHandleState(endHandle);
    }

    /*
     * Private
     */

    private void setupHandleState(Circle handleCircle) {

        final String styleClass = isEnabled() ? SELECTION_HANDLES : SELECTION_HANDLES_DIM;
        final Cursor cursor = isEnabled() ? Cursor.OPEN_HAND : Cursor.DEFAULT;

        handleCircle.getStyleClass().add(styleClass);
        handleCircle.setCursor(cursor);
    }


    /*
     * Wraper to avoid the 'leaking this in constructor' warning emitted by NB.
     */
    private void setupHandles(Node node) {
        attachHandles(node, this);
    }
}
