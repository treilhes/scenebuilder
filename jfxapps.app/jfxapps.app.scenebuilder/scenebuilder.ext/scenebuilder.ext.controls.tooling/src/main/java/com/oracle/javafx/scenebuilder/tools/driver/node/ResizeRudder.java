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
package com.oracle.javafx.scenebuilder.tools.driver.node;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.util.javafx.LineEquation;
import com.oracle.javafx.scenebuilder.api.control.rudder.AbstractRudder;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Line;

/**
*
* Resize Rudder: <br><img src="doc-files/resize-rudder.png" alt="Resize Rudder"><br>
* Appears when resizing while preserving aspect ratio in the Editor<br>
* Appear by default while resizing a <a href="https://openjfx.io/javadoc/15/javafx.graphics/javafx/scene/shape/Circle.html">javafx.scene.shape.Circle</a>
* or by holding the SHIFT key for others
*/
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ResizeRudder extends AbstractRudder<Node> {

    private final Line diagonalLine = new Line();

    public ResizeRudder(
            Content contentPanelController,
            FxmlDocumentManager documentManager) {
        super(contentPanelController, documentManager, Node.class);

        diagonalLine.setMouseTransparent(true);
        diagonalLine.getStyleClass().add("resize-rudder"); //NOCHECK
        getRootNode().getChildren().add(diagonalLine);
    }

    @Override
    public void initialize() {

    }

    /*
     * AbstractRudder
     */
    @Override
    public Bounds getSceneGraphObjectBounds() {
        return getSceneGraphObject().getLayoutBounds();
    }

    @Override
    public Node getSceneGraphObjectProxy() {
        return getSceneGraphObject();
    }

    @Override
    public FXOMObject getFxomObjectProxy() {
        return getFxomObject();
    }

    @Override
    protected void startListeningToSceneGraphObject() {
        startListeningToLayoutBounds(getSceneGraphObject());
        startListeningToLocalToSceneTransform(getSceneGraphObject());
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        stopListeningToLayoutBounds(getSceneGraphObject());
        stopListeningToLocalToSceneTransform(getSceneGraphObject());
    }

    @Override
    protected void layoutDecoration() {
        final Bounds b = getSceneGraphObjectBounds();

        final boolean snapToPixel = true;
        final Point2D p0 = sceneGraphObjectToDecoration(b.getMinX(), b.getMinY(), snapToPixel);
        final Point2D p1 = sceneGraphObjectToDecoration(b.getMaxX(), b.getMaxY(), snapToPixel);

        final LineEquation eq = new LineEquation(p0.getX(), p0.getY(), p1.getX(), p1.getY());
        final double outset = 0.1;
        final Point2D d0 = eq.pointAtP(0.0 - outset);
        final Point2D d1 = eq.pointAtP(1.0 + outset);

        diagonalLine.setStartX(d0.getX());
        diagonalLine.setStartY(d0.getY());
        diagonalLine.setEndX(d1.getX());
        diagonalLine.setEndY(d1.getY());
    }


}
