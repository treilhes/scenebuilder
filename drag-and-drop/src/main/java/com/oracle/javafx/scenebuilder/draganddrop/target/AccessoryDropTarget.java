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
package com.oracle.javafx.scenebuilder.draganddrop.target;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.editors.drag.target.AbstractDropTarget;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.InsertAsAccessoryJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ReIndexObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveObjectJob;

import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;

/**
 *
 */
public class AccessoryDropTarget extends AbstractDropTarget {
    
    private static final Logger logger = LoggerFactory.getLogger(AccessoryDropTarget.class);

    private final FXOMInstance targetContainer;
    private Accessory accessory;
    private final FXOMObject beforeChild;
    private DesignHierarchyMask mask;
    
    public AccessoryDropTarget(FXOMInstance targetContainer, Accessory accessory, FXOMObject beforeChild) {
        assert targetContainer != null;
        this.targetContainer = targetContainer;
        this.accessory = accessory;
        this.beforeChild = beforeChild;
    }
    
    public AccessoryDropTarget(FXOMInstance targetContainer, Accessory accessory) {
        this(targetContainer, accessory, null);
    }
    
    public AccessoryDropTarget(FXOMInstance targetContainer, FXOMObject beforeChild) {
        this(targetContainer, null, beforeChild);
    }
    
    /**
     * Instantiates a new accessory drop target.
     * The target accessory will be discovered at runtime
     * It will be the first accessory accepting the object in the following order:
     * mainAccessory > accessories iteration
     * @param targetContainer the target container
     */
    public AccessoryDropTarget(FXOMInstance targetContainer) {
        this(targetContainer, null, null);
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

    private DesignHierarchyMask getMask() {
        if (mask == null) {
            mask = new DesignHierarchyMask(targetContainer);
        }
        return mask;
    }
    @Override
    public boolean acceptDragSource(DragSource dragSource) {
        assert dragSource != null;
        assert dragSource.getDraggedObjects() != null;
        
        Accessory targetAccessory = findTargetAccessory(dragSource.getDraggedObjects());
        
        boolean result = targetAccessory != null; // we found an accessory accepting the drop
        
        if (!result) {
            return false;
        }
        
        if (targetAccessory.isCollection()) {
            final FXOMObject draggedObject0 = dragSource.getDraggedObjects().get(0);
            final boolean sameContainer
                    = targetContainer == draggedObject0.getParentObject();
            final boolean sameIndex
                    = (beforeChild == draggedObject0)
                    || (beforeChild == draggedObject0.getNextSlibing());
        
            result &= ((sameContainer == false) || (sameIndex == false));
        }
        
        if (logger.isDebugEnabled()) {
            logger.debug("Drag source accepted {} with initial accessory {} and resolved accessory {} for objects {}", result,
                    accessory == null ? "null": accessory.getName().getName(), 
                    targetAccessory == null ? "null": targetAccessory.getName().getName(),
                    dragSource.getDraggedObjects().stream()
                        .map(o -> o.getSceneGraphObject().getClass().getSimpleName()).collect(Collectors.toList()));
        }
        
        return result;
    }

    @Override
    public Job makeDropJob(ApplicationContext context, DragSource dragSource, Editor editorController) {
        assert acceptDragSource(dragSource);
        assert editorController != null;

        final Accessory targetAccessory = findTargetAccessory(dragSource.getDraggedObjects());
        
        assert targetAccessory != null;
        
        final boolean shouldRefreshSceneGraph = true;
        
        final BatchJob result = new BatchJob(context, editorController,
                shouldRefreshSceneGraph, dragSource.makeDropJobDescription());

        final FXOMObject draggedObject = dragSource.getDraggedObjects().get(0);
        final FXOMObject currentParent = draggedObject.getParentObject();

        // Two steps :
        //  - remove drag source object from its current parent (if any)
        //  - set the drag source object as accessory of the drop target

        if (currentParent == targetContainer && targetAccessory.getName().equals(draggedObject.getParentProperty().getName())) {
            // It's a re-indexing job
            for (FXOMObject draggedObj : dragSource.getDraggedObjects()) {
                result.addSubJob(new ReIndexObjectJob(context,
                        draggedObj, beforeChild, editorController).extend());
            }
        } else {
            if (currentParent != null) {
                for (FXOMObject draggedObj : dragSource.getDraggedObjects()) {
                    result.addSubJob(new RemoveObjectJob(context, draggedObj, editorController).extend());
                }
            }
            
            for (FXOMObject draggedObj : dragSource.getDraggedObjects()) {
                final Job j = new InsertAsAccessoryJob(context, draggedObj,
                        targetContainer, targetAccessory, editorController).extend();
                result.addSubJob(j);

                // TODO why specifying a default alignment
                if ((targetContainer.getSceneGraphObject() instanceof BorderPane)
                        && (draggedObject instanceof FXOMInstance)) {

                    // We add a job which sets BorderPane.alignment=CENTER on draggedObject
                    final FXOMInstance draggedInstance
                            = (FXOMInstance) draggedObject;
                    final PropertyName alignmentName
                            = new PropertyName("alignment", BorderPane.class); //NOI18N
                    final EnumerationPropertyMetadata alignmentMeta
                            = new EnumerationPropertyMetadata(alignmentName, Pos.class,
                            "UNUSED", true /* readWrite */, InspectorPath.UNUSED); //NOI18N
                    final Job alignmentJob
                            = new ModifyObjectJob(context, draggedInstance, alignmentMeta,
                                    Pos.CENTER.toString(), editorController).extend();
                    result.addSubJob(alignmentJob);
                }
            }
        }
        
        
        

        assert result.extend().isExecutable();

        return result.extend();
    }

    public Accessory findTargetAccessory(List<FXOMObject> draggedObject) {
        
        if (draggedObject.isEmpty()) {
            return null;
        } 
        boolean needCollectionAccessory = draggedObject.size() > 1;
        
        Accessory targetAccessory = accessory;
        
        if (targetAccessory != null) { // accessory was provided so check it 
            if (needCollectionAccessory && !targetAccessory.isCollection()) {
                return null;
            }

            if (!targetAccessory.isCollection() && getMask().getAccessory(targetAccessory) != null ) {
                return null;
            }
            final Accessory testAccessory = targetAccessory;
            if (draggedObject.stream().anyMatch(o -> !testAccessory.isAccepting(o.getSceneGraphObject().getClass()))) {
                return null;
            }
        }
        
        if (targetAccessory == null) { //targeting main accessory
            // check if main accessory is a valid candidate
            if (getMask().getMainAccessory() != null
                    && (!needCollectionAccessory || (needCollectionAccessory && getMask().getMainAccessory().isCollection()))) {
                // check if droped objects are all compatible
                if (getMask().isAcceptingSubComponent(draggedObject)) {
                    targetAccessory = getMask().getMainAccessory();
                }
            }
        }
        
        if (targetAccessory == null) { //targeting the first valid accessory accepting the drop
            for (Accessory candidate:getMask().getAccessories()) {
                // check if accessory is a valid candidate
                if (!needCollectionAccessory || (needCollectionAccessory && candidate.isCollection())) {
                    // definition is ok, but is there some space left
                    if (candidate.isCollection() || getMask().getAccessory(candidate) == null) {
                        // check if droped objects are all compatible
                        if (getMask().isAcceptingAccessory(candidate ,draggedObject)) {
                            targetAccessory = candidate;
                            break;
                        }
                    }
                }
            }
        }
        
        return targetAccessory;
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
        final AccessoryDropTarget other = (AccessoryDropTarget) obj;
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
        return "AccessoryDropTarget{" + "targetContainer=" + targetContainer + ", accessory=" + accessory + '}'; //NOI18N
    }


}
