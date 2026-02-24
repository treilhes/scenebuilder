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
package com.oracle.javafx.scenebuilder.tools.driver.gridpane;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.gluonhq.jfxapps.core.api.fxom.editor.selection.FxomSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionGroupFactoryRegistry;
import com.gluonhq.jfxapps.core.api.fxom.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.DeleteGridSelectionJob;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.emc4j.boot.api.context.annotation.Prototype;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 *
 */
@Prototype
public final class GridSelectionGroup implements FxomSelectionGroup {

    static private final PropertyName rowConstraintsName = new PropertyName("rowConstraints");
    static private final PropertyName columnConstraintsName = new PropertyName("columnConstraints");

    private static final IntegerPropertyMetadata columnIndexMeta = new IntegerPropertyMetadata.Builder<Void>()
            .name(new PropertyName("columnIndex", GridPane.class)) // NOCHECK
            .readWrite(true).defaultValue(0).build();

    private static final IntegerPropertyMetadata rowIndexMeta = new IntegerPropertyMetadata.Builder<Void>()
            .name(new PropertyName("rowIndex", GridPane.class)) // NOCHECK
            .readWrite(true).defaultValue(0).build();

    public enum Type {
        ROW, COLUMN
    };

    private final FXOMObjectMask.Factory designHierarchyMaskFactory;
    private final DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;
    private FXOMObject parentObject;
    private Type type;
    private final Set<Integer> indexes = new HashSet<>();
    protected final Set<FXOMObject> items = new HashSet<>();
    protected final Set<FXOMObject> innerItems = new HashSet<>();
    // @formatter:off
    protected GridSelectionGroup(
            FXOMObjectMask.Factory designHierarchyMaskFactory,
            DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
     // @formatter:on
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.deleteGridSelectionJobFactory = deleteGridSelectionJobFactory;
    }

    protected void setGroupParameters(FXOMObject parentObject, Type type, Set<Integer> indexes) {
        assert parentObject != null;
        assert parentObject.getSceneGraphObject().isInstanceOf(GridPane.class);
        assert indexes != null;
        assert indexes.isEmpty() == false;

        this.parentObject = parentObject;
        this.type = type;
        this.indexes.addAll(indexes);
        this.items.add(parentObject);
        this.innerItems.addAll(parentObject.collect(GridCollector.constraints(type, indexes)));
    }

    @Override
    public FXOMObject getHitItem() {
        return parentObject;
    }

    public Type getType() {
        return type;
    }

    public Set<Integer> getIndexes() {
        return Collections.unmodifiableSet(indexes);
    }

    public List<FXOMObject> collectSelectedObjects() {
        return parentObject.collect(GridCollector.fxomObjects(type, indexes));
    }

    /*
     * AbstractSelectionGroup
     */

    @Override
    public FXOMObject getAncestor() {
        return parentObject;
    }

    @Override
    public boolean isValid(FXOMDocument fxomDocument) {
        assert fxomDocument != null;

        final boolean result;
        final FXOMObject fxomRoot = fxomDocument.getFxomRoot();
        if (fxomRoot == null) {
            result = false;
        } else {
            result = (parentObject == fxomRoot) || parentObject.isDescendantOf(fxomRoot);
        }

        return result;
    }

    /*
     * Cloneable
     */
    @Override
    public GridSelectionGroup clone() throws CloneNotSupportedException {
        return (GridSelectionGroup) super.clone();
    }

    /*
     * Object
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + Objects.hashCode(this.parentObject);
        hash = 47 * hash + Objects.hashCode(this.type);
        hash = 47 * hash + Objects.hashCode(this.indexes);
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
        final GridSelectionGroup other = (GridSelectionGroup) obj;
        if (!Objects.equals(this.parentObject, other.parentObject)) {
            return false;
        }
        if (this.type != other.type) {
            return false;
        }
        if (!Objects.equals(this.indexes, other.indexes)) {
            return false;
        }
        return true;
    }

    @Override
    public Job makeDeleteJob() {
        return deleteGridSelectionJobFactory.getJob();
    }

    /**
     * Toggle. Adds/removes the specified column/row to/from the selected items.
     *
     * @param toggleGroup the toggle group
     * @return the abstract selection group
     */
    @Override
    public SelectionGroup toggle(SelectionGroup toggleGroup) {
        if (toggleGroup.getClass() == getClass()) {
            GridSelectionGroup gridToggleGroup = (GridSelectionGroup) toggleGroup;

            assert gridToggleGroup.getAncestor() != null;
            assert gridToggleGroup.getAncestor().getSceneGraphObject().isInstanceOf(GridPane.class);

            FXOMObject ancestor = gridToggleGroup.getAncestor();
            Type toggledFeature = gridToggleGroup.getType();
            Set<Integer> toggledIndexes = gridToggleGroup.getIndexes();

            if (getType() == toggledFeature) {
                final Set<Integer> indexes = new HashSet<>(this.getIndexes());
                for (Integer index : toggledIndexes) {
                    if (indexes.contains(index)) {
                        indexes.remove(index);
                    } else {
                        indexes.add(index);
                    }
                }
                if (indexes.isEmpty()) {
                    // no more column/row selected
                    // GridSelectionGroup -> ObjectSelectionGroup
                    return objectSelectionGroupFactory.getGroup(ancestor, null);
                } else {
                    return gridSelectionGroupFactory.getGroup(ancestor, toggledFeature, indexes);
                }
            }
        }
        return toggleGroup;
    }

    /**
     * Returns true if the specified column/row is part of the selection. Conditions
     * must be met: 1) this selection should an GridSelectionGroup 2)
     * GridSelectionGroup.type matches feature 3) GridSelectionGroup.indexes
     * contains featureIndex
     *
     * @param gridPaneObject fxom object of the gridpane holding the column/row
     * @param feature        column/row
     * @param featureIndex   index of the column/row to be checked
     * @return true if this foxm object is selected.
     */
    @Override
    public boolean isSelected(SelectionGroup group) {
        final boolean result;
        if (group instanceof GridSelectionGroup) {
            final GridSelectionGroup gsg = (GridSelectionGroup) group;
            result = (getType() == gsg.getType()) && (getIndexes().containsAll(gsg.getIndexes()));
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public Node getCheckedHitNode() {
        return null;
    }

    @Override
    public List<FXOMObject> getSiblings() {
        return parentObject.collect(GridCollector.constraints(type));
    }

    @Override
    public SelectionGroup selectAll() {
        List<FXOMObject> siblings = this.getSiblings();
        if (siblings.size() <= 1) {
            return this;
        }
        Set<Integer> allIndexes = IntStream.range(0, siblings.size()).boxed().collect(Collectors.toSet());
        return gridSelectionGroupFactory.getGroup(this.getAncestor(), this.getType(), allIndexes);
    }

    @Override
    public SelectionGroup selectNext() {
        Set<? extends FXOMObject> localIitems = this.getInnerItems();

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

        return gridSelectionGroupFactory.getGroup(this.getAncestor(), this.getType(), index);
    }

    @Override
    public SelectionGroup selectPrevious() {
        Set<? extends FXOMObject> localIitems = this.getInnerItems();

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

        return gridSelectionGroupFactory.getGroup(this.getAncestor(), this.getType(), index);
    }

    @Override
    public Set<? extends FXOMObject> getInnerItems() {
        return innerItems;
    }

    public Map<String, FXOMObject> collectSelectedFxIds() {
        // Collects fx:ids in selected objects and their descendants.
        final Map<String, FXOMObject> fxIdMap = new HashMap<>();
        for (FXOMObject selectedObject : collectSelectedObjects()) {
            fxIdMap.putAll(selectedObject.collect(FxCollector.fxIdsUniqueMap()));
        }
        return fxIdMap;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T collect(FXOMCollector<T> collector) {
        return parentObject.collect(collector);
    }

    @Override
    public Set<FXOMObject> getItems() {
        return items;
    }


    @Override
    public SelectionGroup selectParent() {
        return objectSelectionGroupFactory.getGroup(getAncestor(), null);
    }

    @Override
    public boolean isEmpty() {
        return items != null ? items.isEmpty() : true;
    }

    @ApplicationInstanceSingleton
    public static class Factory extends SelectionGroupFactory<GridSelectionGroup> {

        public Factory(EmContext sbContext, SelectionGroupFactoryRegistry registry) {
            super(sbContext, registry);
            // we register the ObjectSelectionGroup factory for Object
            // it will be the default factory
            registry.registerImplementationClass(Object.class, Factory.class);
        }

        public GridSelectionGroup getGroup(FXOMObject parentObject, Type type, Set<Integer> indexes) {
            return create(GridSelectionGroup.class, j -> j.setGroupParameters(parentObject, type, indexes));
        }

        public GridSelectionGroup getGroup(FXOMObject parentObject, Type type, int index) {
            return create(GridSelectionGroup.class, j -> j.setGroupParameters(parentObject, type, Set.of(index)));
        }

        //FIXME implement me
        @Override
        public GridSelectionGroup getGroup(Collection<? extends FXOMObject> fxomObjects, FXOMObject hitItem,
                Node hitNode) {
            // TODO Auto-generated method stub
            return null;
        }
    }

}
