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
package com.oracle.javafx.scenebuilder.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.GroupFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyPath;
import com.oracle.javafx.scenebuilder.selection.job.DeleteObjectSelectionJob;

import javafx.scene.Node;

// TODO: Auto-generated Javadoc
/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class ObjectSelectionGroup extends AbstractSelectionGroup {

    /** The design mask factory. */
    private final DesignHierarchyMask.Factory designMaskFactory;

    /** The object selection group factory. */
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    /** The delete object selection job factory. */
    private final DeleteObjectSelectionJob.Factory deleteObjectSelectionJobFactory;


    /** The hit item. */
    private FXOMObject hitItem;

    /** The hit scene graph object. */
    private Object hitSceneGraphObject;

    /** The hit node. */
    private Node hitNode;

    /**
     * Instantiates a new object selection group.
     *
     * @param objectSelectionGroupFactory the object selection group factory
     * @param designMaskFactory the design mask factory
     * @param deleteObjectSelectionJobFactory the delete object selection job factory
     */
    protected ObjectSelectionGroup(
            ObjectSelectionGroup.Factory objectSelectionGroupFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            DeleteObjectSelectionJob.Factory deleteObjectSelectionJobFactory
            ) {
        super();
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.deleteObjectSelectionJobFactory = deleteObjectSelectionJobFactory;
    }

    /**
     * Sets the group parameters.
     *
     * @param fxomObject the fxom object
     * @param hitNode the hit node
     */
    protected void setGroupParameters(FXOMObject fxomObject, Node hitNode) {
        assert fxomObject != null;
        this.items.add(fxomObject);
        this.hitItem = fxomObject;
        this.hitSceneGraphObject = fxomObject.getSceneGraphObject();
        this.hitNode = hitNode;
    }

    /**
     * Sets the group parameters.
     *
     * @param fxomObjects the fxom objects
     * @param hitItem the hit item
     * @param hitNode the hit node
     */
    protected void setGroupParameters(Collection<FXOMObject> fxomObjects, FXOMObject hitItem, Node hitNode) {
        assert fxomObjects != null;
        assert hitItem != null;
        assert fxomObjects.contains(hitItem);
        this.items.addAll(fxomObjects);
        this.hitItem = hitItem;
        this.hitSceneGraphObject = this.hitItem.getSceneGraphObject();
        this.hitNode = hitNode;
    }



    /**
     * Gets the hit item.
     *
     * @return the hit item
     */
    @Override
    public FXOMObject getHitItem() {
        return hitItem;
    }

    /**
     * Gets the hit node.
     *
     * @return the hit node
     */
    public Node getHitNode() {
        return hitNode;
    }

    /**
     * Checks if is expired.
     *
     * @return true, if is expired
     */
    public boolean isExpired() {
        return hitItem.getSceneGraphObject() != hitSceneGraphObject;
    }

    /**
     * Gets the checked hit node.
     *
     * @return the checked hit node
     */
    @Override
    public Node getCheckedHitNode() {
        final Node result;

        if ((hitNode == null) || isExpired()) {
            result = getFallbackHitNode();
        } else {
            result = hitNode;
        }

        return result;
    }

    /**
     * Gets the fallback hit node.
     *
     * @return the fallback hit node
     */
    public Node getFallbackHitNode() {
        final Node result;

        if (hitItem.isNode()) {
            result = (Node) hitItem.getSceneGraphObject();
        } else {
            final FXOMObject closestNodeObject = hitItem.getClosestNode();
            if (closestNodeObject != null) {
                result = (Node) closestNodeObject.getSceneGraphObject();
            } else {
                result = null;
            }
        }

        return result;
    }

    /**
     * Gets the flatten items.
     *
     * @return the flatten items
     */
    public Set<FXOMObject> getFlattenItems() {
        return FXOMNodes.flatten(items);
    }

    /**
     * Gets the sorted items.
     *
     * @return the sorted items
     */
    public List<FXOMObject> getSortedItems() {
        return FXOMNodes.sort(items);
    }

    /**
     * Checks for single parent.
     *
     * @return true, if successful
     */
    public boolean hasSingleParent() {
        final boolean result;

        if (items.size() == 1) {
            result = true;
        } else {
            final Set<FXOMObject> parents = new HashSet<>();
            final Set<FXOMProperty> parentProperties = new HashSet<>();
            for (FXOMObject i : items) {
                parents.add(i.getParentObject());
                parentProperties.add(i.getParentProperty());
            }
            result = parents.size() == 1 && parentProperties.size() == 1;
        }

        return result;
    }

    /*
     * AbstractSelectionGroup
     */

    /**
     * {@inheritDoc}
     *
     */
    @Override
    public FXOMObject getAncestor() {
        final FXOMObject result;

        assert items.isEmpty() == false;

        switch(items.size()) {

            case 0:
                result = null;
                break;

            case 1:
                result = items.iterator().next().getParentObject();
                break;

            default:
                DesignHierarchyPath commonPath = null;
                for (FXOMObject i : items) {
                    final FXOMObject parent = i.getParentObject();
                    if (parent != null) {
                        final DesignHierarchyPath dph = new DesignHierarchyPath(parent);
                        if (commonPath == null) {
                            commonPath = dph;
                        } else {
                            commonPath = commonPath.getCommonPathWith(dph);
                        }
                    }
                }
                assert commonPath != null; // Else it would mean root is selected twice
                result = commonPath.getLeaf();
                break;
        }

        return result;
    }

    /**
     * Checks if is valid.
     *
     * @param fxomDocument the fxom document
     * @return true, if is valid
     */
    @Override
    public boolean isValid(FXOMDocument fxomDocument) {
        assert fxomDocument != null;

        boolean result;
        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        if (fxomRoot == null) {
            result = false;
        } else {
            result = true;
            for (FXOMObject i : items) {
                final boolean ok = (i == fxomRoot) || i.isDescendantOf(fxomRoot);
                if (ok == false) {
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Clone.
     *
     * @return the object selection group
     * @throws CloneNotSupportedException the clone not supported exception
     */
    /*
     * Cloneable
     */
    @Override
    public ObjectSelectionGroup clone() throws CloneNotSupportedException {
        return (ObjectSelectionGroup)super.clone();
    }


    /**
     * Hash code.
     *
     * @return the int
     */
    /*
     * Object
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 41 * hash + Objects.hashCode(this.items);
        hash = 41 * hash + Objects.hashCode(this.hitItem);
        hash = 41 * hash + Objects.hashCode(this.hitNode);
        return hash;
    }

    /**
     * Equals.
     *
     * @param obj the obj
     * @return true, if successful
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ObjectSelectionGroup other = (ObjectSelectionGroup) obj;
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        if (!Objects.equals(this.hitItem, other.hitItem)) {
            return false;
        }
        if (this.hitNode != other.hitNode) {
            return false;
        }
        return true;
    }

    /**
     * The Class Factory.
     */
    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends GroupFactory<ObjectSelectionGroup> {

        /**
         * Instantiates a new factory.
         *
         * @param sbContext the sb context
         */
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Gets the group.
         *
         * @param fxomObject the fxom object
         * @param hitNode the hit node
         * @return the group
         */
        public ObjectSelectionGroup getGroup(FXOMObject fxomObject, Node hitNode) {
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomObject, hitNode));
        }

        /**
         * Gets the group.
         *
         * @param fxomObjects the fxom objects
         * @param hitItem the hit item
         * @param hitNode the hit node
         * @return the group
         */
        public ObjectSelectionGroup getGroup(Collection<FXOMObject> fxomObjects, FXOMObject hitItem, Node hitNode) {
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomObjects, hitItem, hitNode));
        }

        /**
         * Gets the group.
         *
         * @param fxomObjects the fxom objects
         * @return the group
         */
        public ObjectSelectionGroup getGroup(Collection<FXOMObject> fxomObjects) {
            FXOMObject hitItem = fxomObjects.isEmpty() ? null : fxomObjects.iterator().next();
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomObjects, hitItem, null));
        }

        /**
         * Gets the group.
         *
         * @param fxomInstance the fxom instance
         * @return the group
         */
        public ObjectSelectionGroup getGroup(FXOMInstance fxomInstance) {
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomInstance, null));
        }
    }

    /**
     * Make delete job.
     *
     * @return the job
     */
    @Override
    protected Job makeDeleteJob() {
        return deleteObjectSelectionJobFactory.getJob();
    }

    /**
     * Toggle.
     *
     * @param toggleGroup the toggle group
     * @return the abstract selection group
     */
    @Override
    protected AbstractSelectionGroup toggle(AbstractSelectionGroup toggleGroup) {
        if (toggleGroup.getClass() == getClass()) {
            ObjectSelectionGroup objToggleGroup = (ObjectSelectionGroup)toggleGroup;

            assert objToggleGroup.getItems() != null;

            Set<FXOMObject> toggledObjects = objToggleGroup.getItems();

            final Set<FXOMObject> currentItems = getItems();

            for (FXOMObject obj:toggledObjects) {
                if (currentItems.contains(obj)) {
                    currentItems.remove(obj);
                } else {
                    currentItems.add(obj);
                }
            }

            if (currentItems.isEmpty()) {
                return null;
            } else {
                FXOMObject current = currentItems.contains(objToggleGroup.getHitItem()) ? objToggleGroup.getHitItem() : currentItems.iterator().next();
                Node hitNode = current.isNode() ? (Node)current.getSceneGraphObject() : null;
                return objectSelectionGroupFactory.getGroup(currentItems, current, hitNode);
            }
        }
        return toggleGroup;
    }

    /**
     * Checks if is selected.
     *
     * @param group the group
     * @return true, if is selected
     */
    @Override
    protected boolean isSelected(AbstractSelectionGroup group) {
        final boolean result;

        if (group instanceof ObjectSelectionGroup) {
            result = getItems().containsAll(group.getItems());
        } else {
            result = false;
        }

        return result;
    }

    /**
     * Selection can be moved if:
     * 1) it's an object selection (group instanceof ObjectSelectionGroup)
     * 2) selected objects have a single parent
     * 3) single parent supports free child positioning
     *
     * => all selected items are Node.
     */
    @Override
    protected boolean isMovable() {
        if (hasSingleParent()) {
            final FXOMObject parent = getAncestor();
            final HierarchyMask m = designMaskFactory.getMask(parent);
            PropertyName parentPropertyName = getHitItem().getParentProperty().getName();
            Accessory parentAccessory = m.getAccessory(parentPropertyName);
            return parentAccessory.isFreeChildPositioning();
        }
        return false;
    }

    @Override
    public List<FXOMObject> getSiblings() {
        Set<FXOMObject> selectedItems = getItems();

        if (selectedItems.size() > 0) {
            FXOMObject item = selectedItems.iterator().next();
            FXOMObject parent = item.getParentObject();
            FXOMCollection parentCollection = item.getParentCollection();
            if (selectedItems.stream().allMatch(it -> it.getParentObject() == parent && it.getParentCollection() == parentCollection)) {

                if (parentCollection != null) {
                    return new ArrayList<>(parentCollection.getChildObjects());
                } else {

                    final HierarchyMask m = designMaskFactory.getMask(parent);
                    return m.getAccessories().stream()
                        .filter(a -> a.isCollection() == false)
                        .map(a -> m.getAccessory(a))
                        .filter(fx -> fx != null)
                        .collect(Collectors.toList());
                }
            }
        }
        return Collections.emptyList();
    }


}
