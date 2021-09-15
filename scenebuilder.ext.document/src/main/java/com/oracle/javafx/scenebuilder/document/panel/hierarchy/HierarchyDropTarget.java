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
package com.oracle.javafx.scenebuilder.document.panel.hierarchy;

import java.util.List;
import java.util.Objects;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.editors.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.InsertAsAccessoryJob;
import com.oracle.javafx.scenebuilder.job.editor.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ReIndexObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveObjectJob;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 *
 */
public class HierarchyDropTarget extends AbstractDropTarget {

    private final FXOMInstance targetContainer;
    private Accessory accessory;
    private final FXOMObject beforeChild;

    public HierarchyDropTarget(FXOMInstance targetContainer, Accessory accessory, FXOMObject beforeChild) {
        assert targetContainer != null;
        this.targetContainer = targetContainer;
        this.accessory = accessory;
        this.beforeChild = beforeChild;
    }
    
    public Accessory getAccessory() {
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
        
        final DesignHierarchyMask m = new DesignHierarchyMask(targetContainer);
        
        if (accessory == null){
            accessory = m.getMainAccessory();
        }
        if (accessory == null){
            return false;
        }
        if (dragSource.getDraggedObjects().size() == 0) {
            return false;
        }
        if (!accessory.isCollection() && dragSource.getDraggedObjects().size() > 1) {
            return false;
        }
        
        for (FXOMObject fxomObject:dragSource.getDraggedObjects()) {
            boolean accepted = m.isAcceptingAccessory(accessory, fxomObject)
                    && m.getAccessory(accessory) == null;
            
            if (!accepted){
                return false;
            }
        }

        return true;
    }

    @Override
    public Job makeDropJob(ApplicationContext context, DragSource dragSource, Editor editorController) {
        assert acceptDragSource(dragSource);
        assert editorController != null;

        final boolean shouldRefreshSceneGraph = true;
        final BatchJob result = new BatchJob(context, editorController,
                shouldRefreshSceneGraph, dragSource.makeDropJobDescription());

        // TODO recode below if/else, it is a merge between Accessory/ContainerZ Target yet
        if (accessory.isCollection()){
            final List<FXOMObject> draggedObjects = dragSource.getDraggedObjects();
            final FXOMObject currentParent = draggedObjects.get(0).getParentObject();

            if (currentParent == targetContainer) {
                // It's a re-indexing job
                for (FXOMObject draggedObject : dragSource.getDraggedObjects()) {
                    result.addSubJob(new ReIndexObjectJob(context,
                            draggedObject, beforeChild, editorController).extend());
                }
            } else {
                // It's a reparening job :
                //  - remove drag source objects from their current parent (if any)
                //  - add drag source objects to this drop target

                if (currentParent != null) {
                    for (FXOMObject draggedObject : draggedObjects) {
                        result.addSubJob(new RemoveObjectJob(context, draggedObject,
                                editorController).extend());
                    }
                }
                int targetIndex;
                if (beforeChild == null) {
                    final DesignHierarchyMask m = new DesignHierarchyMask(targetContainer);
                    targetIndex = m.getSubComponentCount();
                } else {
                    targetIndex = beforeChild.getIndexInParentProperty();
                    assert targetIndex != -1;
                }
                for (FXOMObject draggedObject : draggedObjects) {
                    final Job j = new InsertAsSubComponentJob(context, draggedObject,
                            targetContainer, targetIndex++, editorController).extend();
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
                result.addSubJob(new RemoveObjectJob(context, draggedObject, editorController).extend());
            }
            final Job j = new InsertAsAccessoryJob(context, draggedObject,
                    targetContainer, accessory, editorController).extend();
            result.addSubJob(j);

            if ((targetContainer.getSceneGraphObject() instanceof BorderPane)
                    && (draggedObject instanceof FXOMInstance)) {

                // We add a job which sets BorderPane.alignment=CENTER on draggedObject
                final FXOMInstance draggedInstance
                        = (FXOMInstance) draggedObject;
                final PropertyName alignmentName
                        = new PropertyName("alignment", BorderPane.class); //NOCHECK
                final EnumerationPropertyMetadata alignmentMeta
                        = new EnumerationPropertyMetadata(alignmentName, Pos.class,
                        "UNUSED", true /* readWrite */, InspectorPath.UNUSED); //NOCHECK
                final Job alignmentJob
                        = new ModifyObjectJob(context, draggedInstance, alignmentMeta,
                                Pos.CENTER.toString(), editorController).extend();
                result.addSubJob(alignmentJob);
            }
        }
        

        assert result.extend().isExecutable();

        return result.extend();
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


}
