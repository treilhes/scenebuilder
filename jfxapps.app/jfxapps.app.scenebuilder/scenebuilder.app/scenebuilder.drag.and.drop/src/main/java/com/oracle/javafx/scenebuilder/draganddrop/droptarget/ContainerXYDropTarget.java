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
package com.oracle.javafx.scenebuilder.draganddrop.droptarget;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.scenebuilder.fxml.api.HierarchyMask;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.control.droptarget.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.api.control.droptarget.DropTargetFactory;
import com.oracle.javafx.scenebuilder.api.dnd.DragSource;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.util.CoordinateHelper;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.fxml.selection.job.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveObjectJob;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ContainerXYDropTarget extends AbstractDropTarget {

    private final DesignHierarchyMask.Factory designMaskFactory;
    private final BatchJob.Factory batchJobFactory;
    private final RelocateNodeJob.Factory relocateNodeJobFactory;
    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;

    private FXOMInstance targetContainer;
    private double sceneX;
    private double sceneY;

    // @formatter:off
    protected ContainerXYDropTarget(
            DesignHierarchyMask.Factory designMaskFactory,
            BatchJob.Factory batchJobFactory,
            RelocateNodeJob.Factory relocateNodeJobFactory,
            RemoveObjectJob.Factory removeObjectJobFactory,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory) {
     // @formatter:on
        this.designMaskFactory = designMaskFactory;
        this.batchJobFactory = batchJobFactory;
        this.relocateNodeJobFactory = relocateNodeJobFactory;
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
    }

    protected void setDropTargetParameters(FXOMInstance targetContainer, double sceneX, double sceneY) {
        assert targetContainer != null;
        assert targetContainer.getSceneGraphObject() instanceof Parent;
        this.targetContainer = targetContainer;
        this.sceneX = sceneX;
        this.sceneY = sceneY;
    }

    public double getSceneX() {
        return sceneX;
    }

    public double getSceneY() {
        return sceneY;
    }

    /*
     * AbstractDropTarget
     */
    @Override
    public FXOMObject getTargetObject() {
        return targetContainer;
    }

    @Override
    public boolean acceptDragSource(DragSource dragSource) {
        assert dragSource != null;

        final boolean result;
        if (dragSource.getDraggedObjects().isEmpty()) {
            result = false;
        } else {
            boolean containsIntrinsic = false;
            for (FXOMObject draggedObject : dragSource.getDraggedObjects()) {
                if (draggedObject instanceof FXOMIntrinsic) {
                    containsIntrinsic = true;
                }
            }
            if (containsIntrinsic) {
                result = false;
            } else {
                final HierarchyMask m = designMaskFactory.getMask(targetContainer);
                result = m.isAcceptingSubComponent(dragSource.getDraggedObjects());
            }
        }

        return result;
    }

    @Override
    public AbstractJob makeDropJob(DragSource dragSource) {
        assert acceptDragSource(dragSource);

        final List<FXOMObject> draggedObjects = dragSource.getDraggedObjects();
        final FXOMObject hitObject = dragSource.getHitObject();
        final double hitX = dragSource.getHitX();
        final double hitY = dragSource.getHitY();
        final FXOMObject currentParent = hitObject.getParentObject();

        final BatchJob result;
        if (currentParent == targetContainer) {
            // It's a relocating job
            assert hitObject.getSceneGraphObject() instanceof Node;
            assert hitObject instanceof FXOMInstance;

            final boolean shouldRefreshSceneGraph = false;
            result = batchJobFactory.getJob(dragSource.makeDropJobDescription(), shouldRefreshSceneGraph);

            final Point2D dxy = computeRelocationDXY((FXOMInstance) hitObject, hitX, hitY);
            for (FXOMObject draggedObject : dragSource.getDraggedObjects()) {
                assert draggedObject.getSceneGraphObject() instanceof Node;
                assert draggedObject instanceof FXOMInstance;
                final Node draggedNode = (Node) draggedObject.getSceneGraphObject();
                final double newLayoutX = Math.round(draggedNode.getLayoutX() + dxy.getX());
                final double newLayoutY = Math.round(draggedNode.getLayoutY() + dxy.getY());
                result.addSubJob(relocateNodeJobFactory.getJob((FXOMInstance) draggedObject, newLayoutX, newLayoutY));
            }
        } else {
            // It's a reparening job :
            // - remove drag source objects from their current parent (if any)
            // - add drag source objects to this drop target
            // - relocate the drag source objects
            // - adjust toggle group declaration (if any)

            final boolean shouldRefreshSceneGraph = true;
            result = batchJobFactory.getJob(dragSource.makeDropJobDescription(), shouldRefreshSceneGraph);

            if (currentParent != null) {
                for (FXOMObject draggedObject : draggedObjects) {
                    result.addSubJob(removeObjectJobFactory.getJob(draggedObject));
                }
            }
            for (FXOMObject draggedObject : draggedObjects) {
                result.addSubJob(insertAsSubComponentJobFactory.getJob(draggedObject, targetContainer, -1));
            }

            // Computes dragged object positions relatively to hitObject
            assert hitObject.getSceneGraphObject() instanceof Node;
            final Node hitNode = (Node) hitObject.getSceneGraphObject();
            final double layoutX0 = hitNode.getLayoutX();
            final double layoutY0 = hitNode.getLayoutY();
            final Map<FXOMObject, Point2D> layoutDXY = new HashMap<>();
            for (FXOMObject draggedObject : draggedObjects) {
                assert draggedObject.getSceneGraphObject() instanceof Node;
                final Node draggedNode = (Node) draggedObject.getSceneGraphObject();
                final double layoutDX = draggedNode.getLayoutX() - layoutX0;
                final double layoutDY = draggedNode.getLayoutY() - layoutY0;
                layoutDXY.put(draggedObject, new Point2D(layoutDX, layoutDY));
            }

            final Point2D targetCenter = CoordinateHelper.sceneToLocal(targetContainer, sceneX, sceneY,
                    true /* rootScene */);
            final Bounds layoutBounds = hitNode.getLayoutBounds();
            final Point2D currentOrigin = hitNode.localToParent(0.0, 0.0);
            final Point2D currentCenter = hitNode.localToParent((layoutBounds.getMinX() + layoutBounds.getMaxX()) / 2.0,
                    (layoutBounds.getMinY() + layoutBounds.getMaxY()) / 2.0);
            final double currentDX = currentOrigin.getX() - currentCenter.getX();
            final double currentDY = currentOrigin.getY() - currentCenter.getY();
            final double targetOriginX = targetCenter.getX() + currentDX;
            final double targetOriginY = targetCenter.getY() + currentDY;

            for (FXOMObject draggedObject : draggedObjects) {
                assert draggedObject instanceof FXOMInstance;
                final Point2D dxy = layoutDXY.get(draggedObject);
                assert dxy != null;
                final double newLayoutX = Math.round(targetOriginX + dxy.getX());
                final double newLayoutY = Math.round(targetOriginY + dxy.getY());
                result.addSubJob(relocateNodeJobFactory.getJob((FXOMInstance) draggedObject, newLayoutX, newLayoutY));
            }
        }

        assert result.isExecutable();

        return result;
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        return true;
    }

    /*
     * Objects
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.targetContainer);
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.sceneX) ^ (Double.doubleToLongBits(this.sceneX) >>> 32));
        hash = 53 * hash + (int) (Double.doubleToLongBits(this.sceneY) ^ (Double.doubleToLongBits(this.sceneY) >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ContainerXYDropTarget other = (ContainerXYDropTarget) obj;
        if (!Objects.equals(this.targetContainer, other.targetContainer)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sceneX) != Double.doubleToLongBits(other.sceneX)) {
            return false;
        }
        if (Double.doubleToLongBits(this.sceneY) != Double.doubleToLongBits(other.sceneY)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContainerXYDropTarget{" + "targetContainer=" + targetContainer + ", sceneX=" + sceneX + ", sceneY="
                + sceneY + '}'; // NOCHECK
    }

    /*
     * Private
     */

    private Point2D computeRelocationDXY(FXOMInstance hitObject, double hitX, double hitY) {
        assert hitObject != null;
        assert hitObject.getSceneGraphObject() instanceof Node;

        /*
         * Converts (hitX, hitY) in hitObject parent coordinate space.
         */
        final Node sceneGraphNode = (Node) hitObject.getSceneGraphObject();
        final Point2D currentHit = sceneGraphNode.localToParent(hitX, hitY);

        /*
         * Computes drop target location in hitObject parent coordinate space
         */
        final Point2D newHit = CoordinateHelper.sceneToLocal(hitObject.getParentObject(), sceneX, sceneY,
                true /* rootScene */);

        final double dx = newHit.getX() - currentHit.getX();
        final double dy = newHit.getY() - currentHit.getY();
        return new Point2D(dx, dy);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends DropTargetFactory<ContainerXYDropTarget> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public ContainerXYDropTarget getDropTarget(FXOMInstance targetContainer, double sceneX, double sceneY) {
            return create(ContainerXYDropTarget.class, j -> j.setDropTargetParameters(targetContainer, sceneX, sceneY));
        }
    }
}
