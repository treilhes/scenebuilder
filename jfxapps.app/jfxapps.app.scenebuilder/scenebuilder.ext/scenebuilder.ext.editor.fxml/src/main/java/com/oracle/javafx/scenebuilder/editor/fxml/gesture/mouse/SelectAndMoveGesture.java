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
package com.oracle.javafx.scenebuilder.editor.fxml.gesture.mouse;

import org.scenebuilder.fxml.api.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.content.gesture.AbstractMouseDragGesture;
import com.oracle.javafx.scenebuilder.api.content.gesture.GestureFactory;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class SelectAndMoveGesture extends AbstractMouseDragGesture {

    private final Drag drag;
    private final DocumentDragSource.Factory documentDragSourceFactory;
    private final Selection selection;

    protected SelectAndMoveGesture(
            @Autowired @Lazy Content contentPanelController,
            @Autowired Drag drag,
            Selection selection,
            DocumentDragSource.Factory documentDragSourceFactory) {
        super(contentPanelController);
        this.drag = drag;
        this.selection = selection;
        this.documentDragSourceFactory = documentDragSourceFactory;
    }

    private FXOMObject hitObject;
    private double hitSceneX;
    private double hitSceneY;

    public FXOMObject getHitObject() {
        return hitObject;
    }

    public void setHitObject(FXOMObject hitObject) {
        this.hitObject = hitObject;
    }

    public void setHitSceneX(double hitSceneX) {
        this.hitSceneX = hitSceneX;
    }

    public void setHitSceneY(double hitSceneY) {
        this.hitSceneY = hitSceneY;
    }

    /*
     * AbstractMouseDragGesture
     */

    @Override
    protected void mousePressed(MouseEvent e) {

        /*
         *             |      hitObject     |                hitObject                |
         *             |      selected      |                unselected               |
         *             |                    +--------------------+--------------------+
         *             |                    |     no selected    |     an ancestor    |
         *             |                    |       ancestor     |     is selected    |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |                    |  select hitObject  |                    |
         *   shift up  | start drag gesture | start drag gesture | start drag gesture |
         *             |                    |                    |                    |
         *             |         (A)        |         (B.1)      |       (B.2)        |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |  remove hitObject  |    add hitObject   |   remove ancestor  |
         *  shift down |   from selection   |    to selection    |   from selection   |
         *             | ignore drag gesture| start drag gesture | ignore drag gesture|
         *             |         (C)        |         (D.1)      |       (D.2)        |
         * ------------+--------------------+--------------------+--------------------+
         */

        final boolean extendKeyDown
                = EditorPlatform.isContinuousSelectKeyDown(e)
                || EditorPlatform.isNonContinousSelectKeyDown(e);

        if (selection.isSelected(hitObject)) {
            if (extendKeyDown) { // Case C
                selection.toggleSelection(hitObject);
            } // else Case A
        } else {
            final FXOMObject ancestor = selection.lookupSelectedAncestor(hitObject);
            if (ancestor == null) {
                if (extendKeyDown) { // Case D.1
                    selection.toggleSelection(hitObject);
                } else { // Case B.1
                    selection.select(hitObject);
                }
            } else {
                if (extendKeyDown) { // Case D.2
                    selection.toggleSelection(ancestor);
                } // else Case B.2
            }
        }
    }

    @Override
    protected void mouseDragDetected(MouseEvent e) {
        /*
         *             |      hitObject     |                hitObject                |
         *             |      selected      |                unselected               |
         *             |                    +--------------------+--------------------+
         *             |                    |     no selected    |     an ancestor    |
         *             |                    |       ancestor     |     is selected    |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |                    |  select hitObject  |                    |
         *   shift up  | start drag gesture | start drag gesture | start drag gesture |
         *             |                    |                    |                    |
         *             |         (A)        |         (B.1)      |       (B.2)        |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |  remove hitObject  |    add hitObject   |   remove ancestor  |
         *  shift down |   from selection   |    to selection    |   from selection   |
         *             | ignore drag gesture| start drag gesture | ignore drag gesture|
         *             |         (C)        |         (D.1)      |       (D.2)        |
         * ------------+--------------------+--------------------+--------------------+
         */

        final FXOMObject selectedHitObject;
        if (selection.isSelected(hitObject)) { // Case A, B.1 or D.1
            selectedHitObject = hitObject;
        } else {
            selectedHitObject = selection.lookupSelectedAncestor(hitObject); // Case B.2
        }

        if (selectedHitObject != null) {

            assert selection.getGroup() instanceof DefaultSelectionGroupFactory;

            final DefaultSelectionGroupFactory
                    osg = (DefaultSelectionGroupFactory) selection.getGroup();

            if (osg.hasSingleParent()) {

                final Point2D hitPoint = computeHitPoint(selectedHitObject);
                final DocumentDragSource dragSource = documentDragSourceFactory.getDragSource(osg.getSortedItems(),
                        selectedHitObject, hitPoint.getX(), hitPoint.getY());

                if (dragSource.isAcceptable()) {
                    final Node glassLayer = contentPanelController.getGlassLayer();
                    final Dragboard db = glassLayer.startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    db.setContent(dragSource.makeClipboardContent());
                    db.setDragView(dragSource.makeDragView());

                    assert drag.getDragSource() == null;
                    drag.begin(dragSource);
                }
            }
        }
    }

    private Point2D computeHitPoint(FXOMObject fxomObject) {

        final FXOMObject nodeObject = fxomObject.getClosestNode();
        final Node sceneGraphNode;
        if (nodeObject == null) {
            // Root object is not a node, there should be a display node
            FXOMDocument document = fxomObject.getFxomDocument();
            assert document.getDisplayNode() != null;
            sceneGraphNode = document.getDisplayNode();
            return sceneGraphNode.sceneToLocal(hitSceneX, hitSceneY, true /* rootScene */);
        } else {
            assert nodeObject.getSceneGraphObject() instanceof Node;
            return CoordinateHelper.sceneToLocal(nodeObject, hitSceneX, hitSceneY, true /* rootScene */);
        }

    }

    @Override
    protected void mouseReleased(MouseEvent e) {

        // Click but no move : in that case, we make sure that
        // the hit object *only* is selected when shift is up.

        final boolean extendKeyDown
                = EditorPlatform.isContinuousSelectKeyDown(e)
                || EditorPlatform.isNonContinousSelectKeyDown(e);
        if (extendKeyDown == false) {
            selection.select(hitObject);
        }

        /*
         *             |      hitObject     |                hitObject                |
         *             |      selected      |                unselected               |
         *             |                    +--------------------+--------------------+
         *             |                    |     no selected    |     an ancestor    |
         *             |                    |       ancestor     |     is selected    |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |                    |  select hitObject  |                    |
         *   shift up  | start drag gesture | start drag gesture | start drag gesture |
         *             |                    |                    |                    |
         *             |         (A)        |         (B.1)      |       (B.2)        |
         * ------------+--------------------+--------------------+--------------------+
         *             |                    |                    |                    |
         *             |  remove hitObject  |    add hitObject   |   remove ancestor  |
         *  shift down |   from selection   |    to selection    |   from selection   |
         *             | ignore drag gesture| start drag gesture | ignore drag gesture|
         *             |         (C)        |         (D.1)      |       (D.2)        |
         * ------------+--------------------+--------------------+--------------------+
         */

    }

    @Override
    protected void mouseExited(MouseEvent e) {
        // Should be not called because mouse should exit glass layer
        // during this gesture

        // Commenting the assertion : in some cases, this method is executed ;
        // is it related to DTL-6393 ?
//        assert false;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends GestureFactory<SelectAndMoveGesture> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        public SelectAndMoveGesture getGesture() {
            return create(SelectAndMoveGesture.class, null); // g -> g.setupGestureParameters());
        }
    }

}
