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
package com.gluonhq.jfxapps.core.dnd.source;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardEncoder;
import com.gluonhq.jfxapps.core.api.dnd.AbstractDragSource;
import com.gluonhq.jfxapps.core.api.editor.images.ImageUtils;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.Axis;
import javafx.scene.chart.Chart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.TransferMode;

/**
 *
 */
@ApplicationInstancePrototype
public final class DocumentDragSource extends AbstractDragSource {

    //private final SbFXOMObjectMask.Factory sbFXOMObjectMaskFactory;
    private final ClipboardEncoder clipboardEncoder;

    private Image image;
    private final List<FXOMObject> draggedObjects = new ArrayList<>();
    private FXOMObject hitObject;
    private double hitX;
    private double hitY;

    protected DocumentDragSource(
            InstanceWindow window,
            ClipboardEncoder clipboardEncoder) {
        super(window.getStage());
        this.clipboardEncoder = clipboardEncoder;
    }

    public void setDragSourceParameters(Image image, List<FXOMObject> draggedObjects, FXOMObject hitObject, Double hitX, Double hitY) {
        assert draggedObjects != null;
        assert hitObject != null;
        assert draggedObjects.contains(hitObject);

        this.image = image;
        this.draggedObjects.addAll(draggedObjects);
        this.hitObject = hitObject;

        if (hitX == null && hitY == null) {
            final Point2D hitPoint = computeDefaultHit(hitObject);
            this.hitX = hitPoint.getX();
            this.hitY = hitPoint.getY();
        } else {
            this.hitX = hitX;
            this.hitY = hitY;
        }
    }

    private static Point2D computeDefaultHit(FXOMObject fxomObject) {
        final double hitX, hitY;
        if (fxomObject.getSceneGraphObject().isInstanceOf(Node.class)) {
            final Node sceneGraphNode = fxomObject.getSceneGraphObject().getAs(Node.class);
            final Bounds lb = sceneGraphNode.getLayoutBounds();
            hitX = (lb.getMinX() + lb.getMaxX()) / 2.0;
            hitY = (lb.getMinY() + lb.getMaxY()) / 2.0;
        } else {
            hitX = 0.0;
            hitY = 0.0;
        }

        return new Point2D(hitX,hitY);
    }

    /*
     * AbstractDragSource
     */

    @Override
    public boolean isAcceptable() {
        /*
         * Check if dragged objects contain any Axis.
         * If one axis has a Chart parent, then drag operation should not be possible
         * (because an Axis cannot be disconnected from its parent Chart).
         * In that case, this drag source is declared as 'non acceptable'.
         */

        boolean result = true;
        for (FXOMObject draggedObject : draggedObjects) {
            if (draggedObject.getSceneGraphObject().isInstanceOf(Axis.class)) {
                final FXOMObject parentObject = draggedObject.getParentObject();
                if ((parentObject != null) && (parentObject.getSceneGraphObject().isInstanceOf(Chart.class))) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }


    @Override
    public List<FXOMObject> getDraggedObjects() {
        return draggedObjects;
    }

    @Override
    public FXOMObject getHitObject() {
        return hitObject;
    }

    @Override
    public double getHitX() {
        return hitX;
    }

    @Override
    public double getHitY() {
        return hitY;
    }

    @Override
    public ClipboardContent makeClipboardContent() {

        // Encode the dragged objects in FXML
        assert clipboardEncoder.isEncodable(draggedObjects);
        final ClipboardContent result = clipboardEncoder.makeEncoding(draggedObjects);

        return result;
    }

    @Override
    public Image makeDragView() {
        final Image draggedImage;
        // Resource may be null for unresolved classes
        if (image == null) {
            draggedImage = ImageUtils.getNodeIcon("MissingIcon.png");
        } else {
            draggedImage = image;
        }

        final Label visualNode = new Label();
        visualNode.setGraphic(new ImageView(draggedImage));
//        visualNode.setText(mask.getClassNameInfo());
        visualNode.getStylesheets().add(AbstractDragSource.getStylesheet().toString());
        visualNode.getStyleClass().add("drag-preview"); //NOCHECK

        return ImageUtils.getImageFromNode(visualNode);
    }

    @Override
    public Node makeShadow() {
        final Group result = new Group();

        result.getStylesheets().add(AbstractDragSource.getStylesheet().toString());

        for (FXOMObject draggedObject : draggedObjects) {
            if (draggedObject.getSceneGraphObject().isInstanceOf(Node.class)) {
                final Node sceneGraphNode = draggedObject.getSceneGraphObject().getAs(Node.class);
                final DragSourceShadow shadowNode = new DragSourceShadow();
                shadowNode.setupForNode(sceneGraphNode);
//                assert shadowNode.getLayoutBounds().equals(sceneGraphNode.getLayoutBounds());
                shadowNode.getTransforms().add(sceneGraphNode.getLocalToParentTransform());
                result.getChildren().add(shadowNode);
            }
        }

        // Translate the group so that it renders (hitX, hitY) above (layoutX, layoutY).
        final Point2D hitPoint;
        if (hitObject.getSceneGraphObject().isInstanceOf(Node.class)) {
            final Node hitNode = hitObject.getSceneGraphObject().getAs(Node.class);
            hitPoint = hitNode.localToParent(hitX, hitY);
        } else {
            hitPoint = Point2D.ZERO;
        }
        result.setTranslateX(-hitPoint.getX());
        result.setTranslateY(-hitPoint.getY());

        return result;
    }

    @Override
    public String makeDropJobDescription() {
        final String result;

        if (draggedObjects.size() == 1) {
            final FXOMObject draggedObject = draggedObjects.get(0);
            final Object sceneGraphObject = draggedObject.getSceneGraphObject().get();
            if (sceneGraphObject == null) {
                result = I18N.getString("drop.job.move.single.unresolved");
            } else {
                result = I18N.getString("drop.job.move.single.resolved",
                        sceneGraphObject.getClass().getSimpleName());
            }
        } else {
            final Set<Class<?>> classes = new HashSet<>();
            int unresolvedCount = 0;
            for (FXOMObject o : draggedObjects) {
                if (!o.getSceneGraphObject().isEmpty()) {
                    classes.add(o.getSceneGraphObject().getObjectClass());
                } else {
                    unresolvedCount++;
                }
            }
            final boolean homogeneous = (classes.size() == 1) && (unresolvedCount == 0);

            if (homogeneous) {
                final Class<?> singleClass = classes.iterator().next();
                result = I18N.getString("drop.job.move.multiple.homogeneous",
                        draggedObjects.size(),
                        singleClass.getSimpleName());
            } else {
                result = I18N.getString("drop.job.move.multiple.heterogeneous",
                        draggedObjects.size());
            }
        }

        return result;
    }

//    @Override
//    public boolean isNodeOnly() {
//        return nodeOnly;
//    }
//
//    @Override
//    public boolean isSingleImageViewOnly() {
//        return singleImageViewOnly;
//    }
//
//    @Override
//    public boolean isSingleTooltipOnly() {
//        return singleTooltipOnly;
//    }
//
//    @Override
//    public boolean isSingleContextMenuOnly() {
//        return singleContextMenuOnly;
//    }

    /*
     * Object
     */

    @Override
    public String toString() {
        return getClass().getSimpleName() + ": hitObject=(" + hitObject + ")"; //NOCHECK
    }


    /*
     * Private
     */

    private boolean checkForNodeOnly() {
        int nonNodeCount = 0;
        for (FXOMObject draggedObject : draggedObjects) {
            if (draggedObject.getSceneGraphObject().isNode() == false) {
                nonNodeCount++;
            }
        }

        return nonNodeCount == 0;
    }

    @Override
    public boolean isEmpty() {
        return draggedObjects.isEmpty();
    }

    @Override
    public boolean isSingle() {
        return draggedObjects.size() == 1;
    }

    @Override
    public boolean isSingleType() {
        boolean sameType = true;
        Class<?> objectClass = null;
        for (FXOMObject draggedObject : draggedObjects) {
            if (objectClass == null) {
                objectClass = draggedObject.getSceneGraphObject().getObjectClass();
            } else {
                sameType &= objectClass.equals(draggedObject.getSceneGraphObject().getObjectClass());
            }
        }
        return sameType;
    }

    @Override
    public boolean isSingleType(Class<?> type) {
        int nonTypeCount = 0;
        for (FXOMObject draggedObject : draggedObjects) {
            if (draggedObject.getSceneGraphObject().isInstanceOf(type)) {
                nonTypeCount++;
            }
        }

        return nonTypeCount == 0;
    }

    @Override
    public TransferMode getTransferMode() {
        // TODO Auto-generated method stub
        return null;
    }
}