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
package com.oracle.javafx.scenebuilder.tools.driver.common;

import org.scenebuilder.fxml.api.Content;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.dnd.DropTarget;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.util.CoordinateHelper;
import com.gluonhq.jfxapps.core.core.dnd.droptarget.AccessoryDropTarget;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.util.MathUtils;
import com.oracle.javafx.scenebuilder.api.control.tring.AbstractNodeTring;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.shape.Line;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class GenericParentTring extends AbstractNodeTring<Parent> {

    private static final double CRACK_MIN_WIDTH = 6;

    private final FXOMObjectMask.Factory maskFactory;

    private int targetIndex = Integer.MIN_VALUE;
    private final Line crackLine = new Line();

    public GenericParentTring(
            FXOMObjectMask.Factory maskFactory,
            Content contentPanelController,
            FxmlDocumentManager documentManager) {
        super(contentPanelController, documentManager, Parent.class);
        this.maskFactory = maskFactory;

        crackLine.getStyleClass().add(TARGET_CRACK_CLASS);
        crackLine.setMouseTransparent(true);
        getRootNode().getChildren().add(crackLine);
    }

    @Override
    public void defineDropTarget(DropTarget dropTarget) {
        //final ContainerZDropTarget zDropTarget = (ContainerZDropTarget) dropTarget;
        final AccessoryDropTarget zDropTarget = (AccessoryDropTarget) dropTarget;
        final int targetIndex;
        if (zDropTarget.getBeforeChild() == null) {
            targetIndex = -1;
        } else {
            targetIndex = zDropTarget.getBeforeChild().getIndexInParentProperty();
        }
        assert targetIndex >= -1;
        this.targetIndex = targetIndex;
    }

    @Override
    public void initialize() {
        assert targetIndex >= -1;
    }

    public static int lookupCrackIndex(HierarchyMask fxomObjectMask, double sceneX, double sceneY) {
        assert fxomObjectMask != null;
        FXOMObject fxomObject = fxomObjectMask.getFxomObject();

        assert fxomObject != null;
        assert fxomObject.getSceneGraphObject().isInstanceOf(Parent.class);

        final Parent parent = fxomObject.getSceneGraphObject().getAs(Parent.class);
        final Point2D hitPoint = CoordinateHelper.sceneToLocal(fxomObject, sceneX, sceneY, true /* rootScene */);
        final int childCount = fxomObjectMask.getSubComponentCount(fxomObjectMask.getMainAccessory(), false);

        final int targetIndex;
        if (childCount == 0) {
            // No children : we append
            targetIndex = -1;

        } else {
            assert childCount >= 1;

            final double hitX = hitPoint.getX();
            final double hitY = hitPoint.getY();
            double minDistance = Double.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0, count = childCount; i < count; i++) {
                final Bounds cb = GenericParentTring.computeCrackBounds(fxomObjectMask, i);
                final double midX = (cb.getMinX() + cb.getMaxX()) / 2.0;
                final double midY = (cb.getMinY() + cb.getMaxY()) / 2.0;
                final double d = MathUtils.distance(hitX, hitY, midX, midY);
                if (d < minDistance) {
                    minIndex = i;
                    minDistance = d;
                }
            }

            final Bounds cb
                    = GenericParentTring.computeCrackBounds(fxomObjectMask, -1);
            final double midX = (cb.getMinX() + cb.getMaxX()) / 2.0;
            final double midY = (cb.getMinY() + cb.getMaxY()) / 2.0;
            final double d = MathUtils.distance(hitX, hitY, midX, midY);
            if (d < minDistance) {
                minIndex = -1;
            }

            targetIndex = minIndex;
        }

        return targetIndex;
    }

    /*
     * AbstractGenericTring
     */

    @Override
    protected void layoutDecoration() {

        super.layoutDecoration();

        final HierarchyMask m = maskFactory.getMask(getFxomObject());
        final int childCount = m.getSubComponentCount(m.getMainAccessory(), false);

        if (childCount == 0) {
            // No crack line
            crackLine.setVisible(false);

        } else {
            // Computes the crack x
            final Bounds crackBounds = computeCrackBounds(m, targetIndex);
            final double crackX = (crackBounds.getMinX() + crackBounds.getMaxX()) / 2.0;
            final double crackY0 = crackBounds.getMinY();
            final double crackY1 = crackBounds.getMaxY();
            final double strokeWidth = crackBounds.getWidth();

            // Updates the crack line
            final boolean snapToPixel = true;
            final Point2D p0 = sceneGraphObjectToDecoration(crackX, crackY0, snapToPixel);
            final Point2D p1 = sceneGraphObjectToDecoration(crackX, crackY1, snapToPixel);

            crackLine.setVisible(true);
            crackLine.setStartX(p0.getX());
            crackLine.setStartY(p0.getY());
            crackLine.setEndX(p1.getX());
            crackLine.setEndY(p1.getY());
            crackLine.setStrokeWidth(strokeWidth);
        }
    }

    /*
     * Private
     */

    private static Bounds computeCrackBounds(HierarchyMask m, int childIndex) {
        assert m != null;
        assert m.hasMainAccessory();
        assert childIndex >= -1;
        assert childIndex < m.getSubComponentCount(m.getMainAccessory(), false);


        final double crackX, crackY0, crackY1, crackWidth;
        final int childCount = m.getSubComponentCount(m.getMainAccessory(), false);
        final Node child, skinParent;
        if (childIndex == -1) {
            child = getChildNode(m, childCount-1);
            skinParent = child.getParent();
            final Bounds cb = child.localToParent(child.getLayoutBounds());
            crackX = cb.getMaxX();
            crackY0 = cb.getMinY();
            crackY1 = cb.getMaxY();
            crackWidth = CRACK_MIN_WIDTH;
        } else if (childIndex == 0) {
            child = getChildNode(m, 0);
            skinParent = child.getParent();
            final Bounds cb = child.localToParent(child.getLayoutBounds());
            crackX = cb.getMinX();
            crackY0 = cb.getMinY();
            crackY1 = cb.getMaxY();
            crackWidth = CRACK_MIN_WIDTH;
        } else {

            /*        child at                  child at
             *      targetIndex-1             targetIndex
             *
             * y0                           +--------------+   crackY0
             * y1 +---------------+         |              |
             *    |               |         |              |
             *    |   prevBounds  |         |    bounds    |
             *    |               |         |              |
             * y2 +---------------+         |              |
             *                              |              |
             * y3                           +--------------+   crackY1
             *                   x0  crackX x1
             *
             *
             */
            assert (1 <= childIndex) && (childIndex < childCount);
            child = getChildNode(m, childIndex);
            final Node prevChild = getChildNode(m, childIndex-1);

            /*
             * child and prevChild may or may not be visible.
             * For example, when a ToolBar is shrinked, it automatically
             * hides the rightmost children. Those nodes are disconnected
             * from the skin and have a null parent.
             */

            final double x0, x1, y0, y2, y1, y3;
            if ((child.getParent() != null) && (prevChild.getParent() != null)) {
                assert child.getParent() == prevChild.getParent();
                skinParent = child.getParent();

                final Bounds prevBounds = prevChild.getBoundsInParent();
                final Bounds bounds = child.getBoundsInParent();
                x0 = prevBounds.getMaxX();
                x1 = bounds.getMinX();
                y0 = bounds.getMinY();
                y1 = prevBounds.getMinY();
                y2 = prevBounds.getMaxY();
                y3 = bounds.getMaxY();
            } else if (child.getParent() != null) {
                skinParent = child.getParent();
                final Bounds bounds = child.getBoundsInParent();
                x0 = x1 = bounds.getMinX();
                y0 = y1 = bounds.getMinY();
                y2 = y3 = bounds.getMaxY();
            } else if (prevChild.getParent() != null) {
                skinParent = prevChild.getParent();
                final Bounds prevBounds = prevChild.getBoundsInParent();
                x0 = x1 = prevBounds.getMaxX();
                y0 = y1 = prevBounds.getMinY();
                y2 = y3 = prevBounds.getMaxY();
            } else {
                // Both children are disconnect from the skin :(
                x0 = x1 = y0 = y1 = y2 = y3 = 0.0; // To generate empty bounds
                skinParent = null;
            }

            if (x0 <= x1) {
                crackX = (x0 + x1) / 2.0;
                crackY0 = Math.min(y0, y1);
                crackY1 = Math.max(y2, y3);
                crackWidth = Math.max(CRACK_MIN_WIDTH, x1 - x0);
            } else {
                crackX = x1;
                crackY0 = y0;
                crackY1 = y3;
                crackWidth = CRACK_MIN_WIDTH;
            }
        }

        assert m.getFxomObject().getSceneGraphObject().isInstanceOf(Parent.class);
        final Parent parent = m.getFxomObject().getSceneGraphObject().getAs(Parent.class);
        final double pCrackX, pCrackY0, pCrackY1;
        if (parent != skinParent) {
            // m.getFxomObject() is a skinned component : so its fxom children
            // are not the direct Node children.
            if (skinParent != null) {
                final Point2D p0 = CoordinateHelper.localToLocal(skinParent, crackX, crackY0, parent);
                final Point2D p1 = CoordinateHelper.localToLocal(skinParent, crackX, crackY1, parent);
                assert MathUtils.equals(p0.getX(), p1.getX());
                pCrackX = p0.getX();
                pCrackY0 = p0.getY();
                pCrackY1 = p1.getY();
            } else {
                // Child at childIndex is inside a skin and is currently not
                // visible : we generate and return empty bounds.
                pCrackX = 0.0;
                pCrackY0 = 0.0;
                pCrackY1 = 0.0;
            }

        } else {
            pCrackX = crackX;
            pCrackY0 = crackY0;
            pCrackY1 = crackY1;
        }

        return new BoundingBox(pCrackX - crackWidth /2, pCrackY0, crackWidth, pCrackY1 - pCrackY0);
    }


    private static Node getChildNode(HierarchyMask m, int childIndex) {
        assert m != null;
        assert m.hasMainAccessory();
        assert 0 <= childIndex;
        assert childIndex < m.getSubComponentCount(m.getMainAccessory(), false);

        final FXOMObject childObject = m.getSubComponentAtIndex(m.getMainAccessory(), childIndex, false);
        assert childObject.getSceneGraphObject().isInstanceOf(Node.class);

        return childObject.getSceneGraphObject().getAs(Node.class);
    }
}
