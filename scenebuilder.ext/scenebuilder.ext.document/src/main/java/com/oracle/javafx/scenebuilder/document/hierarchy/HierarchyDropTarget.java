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
package com.oracle.javafx.scenebuilder.document.hierarchy;

import java.util.Objects;
import java.util.function.Predicate;

import com.oracle.javafx.scenebuilder.api.mask.SbAccessory;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.base.AbstractJob;
import com.treilhes.jfxplace.core.metadata.property.value.EnumerationPropertyMetadata;
import com.treilhes.jfxplace.fxom.api.dnd.AbstractDropTarget;
import com.treilhes.jfxplace.fxom.api.dnd.DragSource;
import com.treilhes.jfxplace.fxom.api.dnd.DropTargetFactory;
import com.treilhes.jfxplace.fxom.api.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.fxom.api.job.base.BatchJob;
import com.treilhes.jfxplace.fxom.api.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.fxom.api.mask.HierarchyMask;
import com.treilhes.jfxplace.fxom.model.FXOMInstance;
import com.treilhes.jfxplace.fxom.model.FXOMObject;
import com.treilhes.jfxplace.fxom.model.util.PropertyName;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 *
 */
@ApplicationInstancePrototype
public final class HierarchyDropTarget extends AbstractDropTarget {

    private final SbFXOMObjectMask.Factory designMaskFactory;
    private final FxomJobsFactory fxomJobsFactory;
    private final SelectionJobsFactory selectionJobsFactory;
    private final BatchJob.Factory batchJobFactory;

    private FXOMInstance targetContainer;
    private SbAccessory accessory;
    private FXOMObject beforeChild;

    protected HierarchyDropTarget(
            SbFXOMObjectMask.Factory designMaskFactory,
            BatchJob.Factory batchJobFactory,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory) {
        this.designMaskFactory = designMaskFactory;
        this.batchJobFactory = batchJobFactory;
        this.fxomJobsFactory = fxomJobsFactory;
        this.selectionJobsFactory = selectionJobsFactory;

    }

    protected void setDropTargetParameters(FXOMInstance targetContainer, SbAccessory accessory, FXOMObject beforeChild) {
        assert targetContainer != null;
        this.targetContainer = targetContainer;
        this.accessory = accessory;
        this.beforeChild = beforeChild;
    }

    public SbAccessory getAccessory() {
        return accessory;
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

        final var m = designMaskFactory.getMask(targetContainer);

        if (accessory == null){
            accessory = m.getMainAccessory();
        }
        if (accessory == null){
            return false;
        }
        if (dragSource.getDraggedObjects().size() == 0) {
            return false;
        }
        if (!accessory.isCollection() && dragSource.getDraggedObjects().stream().filter(Predicate.not(FXOMObject::isVirtual)).count() > 1) {
            return false;
        }

        return m.isAcceptingAccessory(accessory, dragSource.getDraggedObjects());
    }

    @Override
    public AbstractJob makeDropJob(DragSource dragSource) {
        assert acceptDragSource(dragSource);

        final boolean shouldRefreshSceneGraph = true;
        final BatchJob result = batchJobFactory.getJob(shouldRefreshSceneGraph);
        result.setDescription(dragSource.makeDropJobDescription());
        // TODO recode below if/else, it is a merge between Accessory/ContainerZ Target yet
        if (accessory.isCollection()){
            final var draggedObjects = dragSource.getDraggedObjects();
            final var currentParent = draggedObjects.get(0).getParentObject();

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
                    final HierarchyMask m = designMaskFactory.getMask(targetContainer);
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
        } else {
            final FXOMObject draggedObject = dragSource.getDraggedObjects().get(0);
            final FXOMObject currentParent = draggedObject.getParentObject();

            // Two steps :
            //  - remove drag source object from its current parent (if any)
            //  - set the drag source object as accessory of the drop target

            if (currentParent != null) {
                result.addSubJob(fxomJobsFactory.removeObject(draggedObject));
            }
            final var j = selectionJobsFactory.insertAsAccessory(draggedObject,targetContainer, accessory);
            result.addSubJob(j);

            if ((targetContainer.getSceneGraphObject().isInstanceOf(BorderPane.class))
                    && (draggedObject instanceof FXOMInstance)) {

                // We add a job which sets BorderPane.alignment=CENTER on draggedObject
                final var draggedInstance = (FXOMInstance) draggedObject;
                final var alignmentName = new PropertyName("alignment", BorderPane.class); //NOCHECK
                final var alignmentMeta = new EnumerationPropertyMetadata.Builder<>(Pos.class)
                            .name(alignmentName)
                            .readWrite(true)
                            .nullEquivalent("UNUSED")//NOCHECK
                            .build();

                final var alignmentJob = fxomJobsFactory.modifyObject(draggedInstance, alignmentMeta, Pos.CENTER.toString());
                result.addSubJob(alignmentJob);
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
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.targetContainer);
        hash = 97 * hash + (this.accessory != null ? this.accessory.hashCode() : 0);
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
        final HierarchyDropTarget other = (HierarchyDropTarget) obj;
        if (!Objects.equals(this.targetContainer, other.targetContainer)) {
            return false;
        }
        if (this.accessory != other.accessory) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HierarchyDropTarget{" + "targetContainer=" + targetContainer + ", accessory=" + accessory + '}'; //NOCHECK
    }

    @ApplicationSingleton
    public static class Factory extends DropTargetFactory<HierarchyDropTarget> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }

        public HierarchyDropTarget getDropTarget(FXOMInstance targetContainer, SbAccessory accessory, FXOMObject beforeChild) {
            return create(HierarchyDropTarget.class, j -> j.setDropTargetParameters(targetContainer, accessory, beforeChild));
        }
    }
}
