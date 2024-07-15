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
package com.oracle.javafx.scenebuilder.draganddrop.droptarget;

import java.util.Objects;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.dnd.AbstractDropTarget;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.dnd.DropTargetFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchJob;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;

/**
 *
 */
@ApplicationInstancePrototype
public final class ContainerZDropTargetBck extends AbstractDropTarget {

    private final SbFXOMObjectMask.Factory designMaskFactory;
    private final BatchJob.Factory batchJobFactory;
    private final SelectionJobsFactory selectionJobsFactory;
    private final FxomJobsFactory fxomJobsFactory;
    private final SbJobsFactory sbJobsFactory;

    private FXOMInstance targetContainer;
    private FXOMObject beforeChild;

    protected ContainerZDropTargetBck(
            SbFXOMObjectMask.Factory designMaskFactory,
            BatchJob.Factory batchJobFactory,
            SelectionJobsFactory selectionJobsFactory,
            FxomJobsFactory fxomJobsFactory,
            SbJobsFactory sbJobsFactory) {
        this.designMaskFactory = designMaskFactory;
        this.batchJobFactory = batchJobFactory;
        this.selectionJobsFactory = selectionJobsFactory;
        this.fxomJobsFactory = fxomJobsFactory;
        this.sbJobsFactory = sbJobsFactory;
    }

    protected void setDropTargetParameters(FXOMInstance targetContainer, FXOMObject beforeChild) {
        assert targetContainer != null;
        this.targetContainer = targetContainer;
        this.beforeChild = beforeChild;
    }


    public FXOMObject getBeforeChild() {
        return beforeChild;
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
            final HierarchyMask m = designMaskFactory.getMask(targetContainer);
            if (m.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                final FXOMObject draggedObject0 = dragSource.getDraggedObjects().get(0);
                final boolean sameContainer
                        = targetContainer == draggedObject0.getParentObject();
                final boolean sameIndex
                        = (beforeChild == draggedObject0)
                        || (beforeChild == draggedObject0.getNextSlibing());

                result = (sameContainer == false) || (sameIndex == false);
            } else {
                result = false;
            }
        }

        return result;
    }

    @Override
    public AbstractJob makeDropJob(DragSource dragSource) {
        assert dragSource != null;
        assert dragSource.getDraggedObjects().isEmpty() == false;

        final boolean shouldRefreshSceneGraph = true;
        final BatchJob result = batchJobFactory.getJob(shouldRefreshSceneGraph);
        result.setDescription(dragSource.makeDropJobDescription());

        final var draggedObjects = dragSource.getDraggedObjects();
        final FXOMObject currentParent = draggedObjects.get(0).getParentObject();

        if (currentParent == targetContainer) {
            // It's a re-indexing job
            for (FXOMObject draggedObject : dragSource.getDraggedObjects()) {
                result.addSubJob(fxomJobsFactory.reIndexObject(draggedObject, beforeChild));
            }
        } else {
            // It's a reparening job :
            //  - remove drag source objects from their current parent (if any)
            //  - add drag source objects to this drop target

            if (currentParent != null) {
                for (FXOMObject draggedObject : draggedObjects) {
                    result.addSubJob(fxomJobsFactory.removeObject(draggedObject));
                }
            }
            int targetIndex;
            if (beforeChild == null) {
                final var m = designMaskFactory.getMask(targetContainer);
                targetIndex = m.getSubComponentCount(true);
            } else {
                targetIndex = beforeChild.getIndexInParentProperty();
                assert targetIndex != -1;
            }
            for (FXOMObject draggedObject : draggedObjects) {
                final Job j = selectionJobsFactory.insertAsSubComponent(draggedObject,targetContainer, targetIndex++);
                result.addSubJob(j);
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
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.targetContainer);
        hash = 97 * hash + Objects.hashCode(this.beforeChild);
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
        final ContainerZDropTargetBck other = (ContainerZDropTargetBck) obj;
        if (!Objects.equals(this.targetContainer, other.targetContainer)) {
            return false;
        }
        if (!Objects.equals(this.beforeChild, other.beforeChild)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ContainerZDropTarget{" + "targetContainer=" + targetContainer + ", beforeChild=" + beforeChild + '}'; //NOCHECK
    }

    @ApplicationInstanceSingleton
    public static class Factory extends DropTargetFactory<ContainerZDropTargetBck> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        public ContainerZDropTargetBck getDropTarget(FXOMInstance targetContainer, FXOMObject beforeChild) {
            return create(ContainerZDropTargetBck.class, j -> j.setDropTargetParameters(targetContainer, beforeChild));
        }
    }
}
