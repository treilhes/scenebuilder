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
package com.oracle.javafx.scenebuilder.tools.driver.polyline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.Gesture;
import com.gluonhq.jfxapps.core.api.content.gesture.DiscardGesture;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.api.control.EditCurveGuide.Tunable;
import com.oracle.javafx.scenebuilder.api.control.handles.AbstractCurveHandles;
import com.oracle.javafx.scenebuilder.kit.editor.panel.content.gesture.mouse.EditCurveGesture;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polyline;

/**
*
* Polyline handles: <br><img src="doc-files/polyline-handles.png" alt="polyline handles"><br>
* Appears when resizing a
* <a href="https://openjfx.io/javadoc/15/javafx.graphics/javafx/scene/shape/Polyline.html">javafx.scene.shape.Polyline</a>
* in the Editor<br>
* Subclasses will use the same handles until a more specialized one has been registered
*/
@Prototype
public class PolylineHandles extends AbstractCurveHandles<Polyline> {

    private final List<Circle> verticesHandle = new ArrayList<>();
    private final List<Line> linesHandle = new ArrayList<>();
    private final EditCurveGesture.Factory editCurveGestureFactory;

    public PolylineHandles(
            Workspace workspace,
            ApplicationInstanceEvents documentManager,
            DiscardGesture.Factory discardGestureFactory,
            EditCurveGesture.Factory editCurveGestureFactory) {
        super(workspace, documentManager, discardGestureFactory, Polyline.class);

        this.editCurveGestureFactory = editCurveGestureFactory;
    }

    @Override
    public void initialize() {
        final List<Node> rootNodeChildren = getRootNode().getChildren();
        setupHandles(rootNodeChildren);
    }

    public FXOMInstance getFxomInstance() {
        return (FXOMInstance) getFxomObject();
    }

    /*
     * AbstractCurveHandles
     */
    @Override
    protected void layoutDecoration() {
        final Polyline l = getSceneGraphObject();
        final boolean snapToPixel = true;

        if (l.getPoints().size() != verticesHandle.size() * 2) {
            setupHandles(getRootNode().getChildren());
        }
        if (l.getPoints().size() % 2 != 0) {
            return;
        }
        AtomicInteger counter = new AtomicInteger();
        IntStream.range(0, l.getPoints().size() / 2)
            .mapToObj(i -> l.getPoints().subList(i * 2, 2 * (i + 1)))
            .map(list -> sceneGraphObjectToDecoration(list.get(0), list.get(1), snapToPixel))
            .forEach(p -> {
                Circle c = verticesHandle.get(counter.getAndIncrement());
                c.setCenterX(p.getX());
                c.setCenterY(p.getY());
            });
        IntStream.range(0, verticesHandle.size() - 1)
                .forEach(i -> {
                    Circle c1 = verticesHandle.get(i);
                    Circle c2 = verticesHandle.get(i + 1);
                    Line line = linesHandle.get(i);
                    line.setStartX(c1.getCenterX());
                    line.setStartY(c1.getCenterY());
                    line.setEndX(c2.getCenterX());
                    line.setEndY(c2.getCenterY());
                });

    }

    @Override
    protected void startListeningToSceneGraphObject() {
        super.startListeningToSceneGraphObject();

        final Polyline l = getSceneGraphObject();
        l.getPoints().addListener(pointsListener);
    }

    @Override
    protected void stopListeningToSceneGraphObject() {
        super.stopListeningToSceneGraphObject();

        final Polyline l = getSceneGraphObject();
        l.getPoints().removeListener(pointsListener);
    }

    @Override
    public Gesture findGesture(Node node) {
        final EditCurveGesture result;

        if (node instanceof Circle && verticesHandle.contains(node)) {
            result = editCurveGestureFactory.getGesture(getFxomInstance(), Tunable.VERTEX);
            result.getTunableMap().put(Tunable.VERTEX, verticesHandle.indexOf(node));
        } else if (node instanceof Line && linesHandle.contains(node)) {
            result = editCurveGestureFactory.getGesture(getFxomInstance(), Tunable.SIDE);
            result.getTunableMap().put(Tunable.SIDE, linesHandle.indexOf(node));
        } else {
            result = null;
        }

        return result;
    }

    @Override
    public void enabledDidChange() {
        verticesHandle.forEach(this::setupHandleState);
    }

    /*
     * Private
     */

    private void setupHandles(final List<Node> rootNodeChildren) {
        verticesHandle.clear();
        linesHandle.clear();
        rootNodeChildren.clear();
        final Polyline l = getSceneGraphObject();
        IntStream.range(0, l.getPoints().size() / 2 - 1)
                .mapToObj(i -> new Line())
                .forEach(line -> {
                    linesHandle.add(line);
                    line.getStyleClass().add(SELECTION_PIPE);
                    line.setCursor(Cursor.CROSSHAIR);
                    setupHandles(line);
                    rootNodeChildren.add(line);
                });
        IntStream.range(0, l.getPoints().size() / 2)
                .mapToObj(i -> new Circle(SELECTION_HANDLES_SIZE / 2.0))
                .forEach(c -> {
                    verticesHandle.add(c);
                    setupHandleState(c);
                    setupHandles(c);
                    rootNodeChildren.add(c);
                });
    }

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
