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
package com.gluonhq.jfxapps.core.api.editor.selection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPath;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;

import javafx.scene.Node;

/**
 *
 *
 */
@Prototype
public class ObjectSelectionGroup implements SelectionGroup {

    /** The design mask factory. */
    private final FXOMObjectMask.Factory designMaskFactory;

    /** The object selection group factory. */
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    /** The selection job factory. */
    private final SelectionJobsFactory selectionJobsFactory;


    protected final Set<FXOMObject> items = new HashSet<>();

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
            FXOMObjectMask.Factory designMaskFactory,
            SelectionJobsFactory selectionJobsFactory
            ) {
        super();
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.selectionJobsFactory = selectionJobsFactory;
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
        this.hitSceneGraphObject = fxomObject.getSceneGraphObject().get();
        this.hitNode = hitNode;
    }

    /**
     * Sets the group parameters.
     *
     * @param fxomObjects the fxom objects
     * @param hitItem the hit item
     * @param hitNode the hit node
     */
    protected void setGroupParameters(Collection<? extends FXOMObject> fxomObjects, FXOMObject hitItem, Node hitNode) {
        assert fxomObjects != null;
        assert hitItem != null;
        assert fxomObjects.contains(hitItem);
        this.items.addAll(fxomObjects);
        this.hitItem = hitItem;
        this.hitSceneGraphObject = this.hitItem.getSceneGraphObject().get();
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

    @Override
    public Set<FXOMObject> getItems() {
        return Collections.unmodifiableSet(items);
    }


    /**
     * Checks if is expired.
     *
     * @return true, if is expired
     */
    public boolean isExpired() {
        return hitItem.getSceneGraphObject().get() != hitSceneGraphObject;
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

        if (hitItem.getSceneGraphObject().isNode()) {
            result = hitItem.getSceneGraphObject().getAs(Node.class);
        } else {
            final FXOMObject closestNodeObject = hitItem.getClosestNode();
            if (closestNodeObject != null) {
                result = closestNodeObject.getSceneGraphObject().getAs(Node.class);;
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
        assert items.isEmpty() == false;
        return FXOMPath.commonAncestor(items);
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
     * Make delete job.
     *
     * @return the job
     */
    @Override
    public Job makeDeleteJob() {
        return selectionJobsFactory.deleteObjectSelection();
    }

    /**
     * Toggle.
     *
     * @param toggleGroup the toggle group
     * @return the abstract selection group
     */
    @Override
    public SelectionGroup toggle(SelectionGroup toggleGroup) {
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

                Node hitNode = current.getSceneGraphObject().isNode() ? current.getSceneGraphObject().getAs(Node.class) : null;
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
    public boolean isSelected(SelectionGroup group) {
        final boolean result;

        if (group instanceof ObjectSelectionGroup) {
            result = getItems().containsAll(group.getItems());
        } else {
            result = false;
        }

        return result;
    }

    @Override
    public List<FXOMObject> getSiblings() {
        Set<FXOMObject> selectedItems = getItems();

        boolean containsRoot = selectedItems.stream().anyMatch(i -> i == i.getFxomDocument().getFxomRoot());

        if (!containsRoot && selectedItems.size() > 0) {
            FXOMObject item = selectedItems.iterator().next();

            if (item.getParentCollection() != null) {
                FXOMCollection parent = item.getParentCollection();
                boolean sameParent = selectedItems.stream().allMatch(i -> parent.equals(i.getParentCollection()));
                return sameParent ? parent.getChildObjects() : Collections.emptyList();
            }

//            if (item.getParentDefine() != null) {
//                FXOMDefine parent = item.getParentDefine();
//                boolean sameParent = selectedItems.stream().allMatch(i -> parent.equals(i.getParentDefine()));
//                return sameParent ? parent.getChildObjects() : Collections.emptyList();
//            }

            if (item.getParentProperty() != null) {
                FXOMProperty parent = item.getParentProperty();
                boolean sameParent = selectedItems.stream().allMatch(i -> parent.equals(i.getParentProperty()));
                return sameParent ? parent.getChildren() : Collections.emptyList();
            }

            // at this step, siblings may be single objects across single item accessories
            FXOMObject parent = item.getParentObject();
            boolean sameParent = selectedItems.stream().allMatch(i -> parent.equals(i.getParentObject()));
            if (sameParent && item.getParentObject() != null) {


                final FXOMObjectMask m = designMaskFactory.getMask(parent);
                try {
                    return m.getAccessories().stream()
                        .peek(a -> {
                            if (a.isCollection()) {
                                List<FXOMObject> list = m.getAccessories(a, true);
                                List<FXOMObject> remaining = new ArrayList<>(list);
                                remaining.removeAll(selectedItems);

                                if (list.size() != remaining.size()) {
                                    throw new RuntimeException("Only single accessory items must be selected");
                                }
                            }
                        })
                        .filter(a -> a.isCollection() == false)
                        .flatMap(a -> m.getAccessories(a, true).stream())
                        .filter(fx -> fx != null)
                        .collect(Collectors.toList());
                } catch (Exception e) {
                    return Collections.emptyList();
                }
            }
        }
        return Collections.emptyList();
    }


    @Override
    public SelectionGroup selectAll() {
        return objectSelectionGroupFactory.getGroup(this.getSiblings());
    }

    @Override
    public SelectionGroup selectNext() {
        Set<FXOMObject> localIitems = this.getItems();

        if (localIitems.size() != 1) {
            return this;
        }

        List<FXOMObject> siblings = this.getSiblings();
        if (siblings.size() <= 1) {
            return this;
        }

        FXOMObject item = localIitems.iterator().next();

        int index = siblings.indexOf(item) + 1;

        if (index >= siblings.size()) {
            index = 0;
        }

        FXOMObject newSelected = siblings.get(index);

        return objectSelectionGroupFactory.getGroup(newSelected, null);
    }

    @Override
    public SelectionGroup selectPrevious() {
        Set<FXOMObject> localIitems = this.getItems();

        if (localIitems.size() != 1) {
            return this;
        }

        List<FXOMObject> siblings = this.getSiblings();
        if (siblings.size() <= 1) {
            return this;
        }

        FXOMObject item = localIitems.iterator().next();

        int index = siblings.indexOf(item) - 1;

        if (index < 0) {
            index = siblings.size() - 1;
        }

        FXOMObject newSelected = siblings.get(index);

        return objectSelectionGroupFactory.getGroup(newSelected, null);
    }

    @Override
    public Set<Object> getInnerItems() {
        return Collections.emptySet();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T collect(FXOMCollector<T> collector) {
        for (FXOMObject selectedObject : getItems()) {
            selectedObject.collect(collector);

            if (collector.endCollection()) {
                break;
            }
        }
        return collector.getCollected();
    }


    /**
     * The Class Factory.
     */
    @ApplicationSingleton
    public static class Factory extends SelectionGroupFactory<ObjectSelectionGroup> {

        /**
         * Instantiates a new factory.
         *
         * @param sbContext the sb context
         */
        public Factory(JfxAppContext sbContext, SelectionGroupFactoryRegistry registry) {
            super(sbContext, registry);
            // we register the ObjectSelectionGroup factory for Object
            // it will be the default factory
            registry.registerImplementationClass(Object.class, Factory.class);
        }

        /**
         * Gets the group.
         *
         * @param fxomObjects the fxom objects
         * @param hitItem the hit item
         * @param hitNode the hit node
         * @return the group
         */
        @Override
        public ObjectSelectionGroup getGroup(Collection<? extends FXOMObject> fxomObjects, FXOMObject hitItem, Node hitNode) {
            assert fxomObjects == null || fxomObjects.stream().allMatch(FXOMObject.class::isInstance);
            assert hitItem == null || FXOMObject.class.isInstance(hitItem);
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomObjects, hitItem, hitNode));
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

        public ObjectSelectionGroup empty() {
            FXOMObject hitItem = null;
            Collection<FXOMObject> fxomObjects = Set.of();
            return create(ObjectSelectionGroup.class, j -> j.setGroupParameters(fxomObjects, hitItem, null));
        }


    }
}
