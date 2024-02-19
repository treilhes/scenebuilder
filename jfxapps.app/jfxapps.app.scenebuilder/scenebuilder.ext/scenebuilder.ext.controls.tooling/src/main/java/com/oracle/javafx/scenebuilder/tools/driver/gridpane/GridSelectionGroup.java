/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.GroupFactory;
import com.oracle.javafx.scenebuilder.api.job.Job;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.collector.FxIdCollector;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.DeleteGridSelectionJob;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class GridSelectionGroup extends AbstractSelectionGroup {

    static private final PropertyName rowConstraintsName = new PropertyName("rowConstraints");
    static private final PropertyName columnConstraintsName = new PropertyName("columnConstraints");

    public enum Type {
        ROW, COLUMN
    };

    private final DesignHierarchyMask.Factory designHierarchyMaskFactory;
    private final DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;
    private final DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory;
    private FXOMObject parentObject;
    private Type type;
    private final Set<Integer> indexes = new HashSet<>();
    protected final Set<FXOMObject> innerItems = new HashSet<>();
    // @formatter:off
    protected GridSelectionGroup(
            DesignHierarchyMask.Factory designHierarchyMaskFactory,
            DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory,
            DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory) {
     // @formatter:on
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.deleteGridSelectionJobFactory = deleteGridSelectionJobFactory;
    }

    protected void setGroupParameters(FXOMObject parentObject, Type type, Set<Integer> indexes) {
        assert parentObject != null;
        assert parentObject.getSceneGraphObject() instanceof GridPane;
        assert indexes != null;
        assert indexes.isEmpty() == false;

        this.parentObject = parentObject;
        this.type = type;
        this.indexes.addAll(indexes);
        this.items.add(parentObject);
        this.innerItems.addAll(collectConstraintInstances());
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

    private List<FXOMInstance> collectSiblingConstraintInstances() {
        final List<FXOMInstance> result;

        switch (type) {
        case ROW:
            result = collect(rowConstraintsName, RowConstraints.class, null);
            break;
        case COLUMN:
            result = collect(columnConstraintsName, ColumnConstraints.class, null);
            break;
        default:
            throw new RuntimeException("Bug");
        }

        return result;
    }

    private List<FXOMInstance> collectConstraintInstances() {
        final List<FXOMInstance> result;

        switch (type) {
        case ROW:
            result = collectRowConstraintsInstances();
            break;
        case COLUMN:
            result = collectColumnConstraintsInstances();
            break;
        default:
            throw new RuntimeException("Bug");
        }

        return result;
    }

    public List<FXOMObject> collectSelectedObjects() {
        final List<FXOMObject> result;

        switch (type) {
        case ROW:
            result = collectSelectedObjectsInRow();
            break;
        case COLUMN:
            result = collectSelectedObjectsInColumn();
            break;
        default:
            throw new RuntimeException("Bug");
        }

        return result;
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

    /*
     * Private
     */

    private List<FXOMInstance> collectRowConstraintsInstances() {
        return collect(rowConstraintsName, RowConstraints.class, indexes);
//        final List<FXOMInstance> result = new ArrayList<>();
//        final FXOMInstance gridPaneInstance = (FXOMInstance) parentObject;
//        final FXOMProperty fxomProperty = gridPaneInstance.getProperties().get(rowConstraintsName);
//
//        if (fxomProperty != null) {
//            assert fxomProperty instanceof FXOMPropertyC;
//            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
//            int index = 0;
//            for (FXOMObject v : fxomPropertyC.getChildren()) {
//                assert v.getSceneGraphObject() instanceof RowConstraints;
//                assert v instanceof FXOMInstance;
//                if (indexes.contains(index++)) {
//                    result.add((FXOMInstance) v);
//                }
//            }
//        }
//
//        return result;
    }

    private List<FXOMInstance> collectColumnConstraintsInstances() {
        return collect(columnConstraintsName, ColumnConstraints.class, indexes);
//
//        final List<FXOMInstance> result = new ArrayList<>();
//
//        final FXOMInstance gridPaneInstance
//                = (FXOMInstance) parentObject;
//        final FXOMProperty fxomProperty
//                = gridPaneInstance.getProperties().get(columnConstraintsName);
//        if (fxomProperty != null) {
//            assert fxomProperty instanceof FXOMPropertyC;
//            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
//            int index = 0;
//            for (FXOMObject v : fxomPropertyC.getChildren()) {
//                assert v.getSceneGraphObject() instanceof ColumnConstraints;
//                assert v instanceof FXOMInstance;
//                if (indexes.contains(index++)) {
//                    result.add((FXOMInstance)v);
//                }
//            }
//        }
//
//        return result;
    }

    /**
     * Collect all {@link FXOMInstance} which have a scenegraph object of the
     * specified expected class
     *
     * @param name
     * @param extepectedClass
     * @param selected
     * @return
     */
    private List<FXOMInstance> collect(PropertyName name, Class<?> extepectedClass, Set<Integer> selected) {
        final List<FXOMInstance> result = new ArrayList<>();

        final FXOMInstance gridPaneInstance = (FXOMInstance) parentObject;
        final FXOMProperty fxomProperty = gridPaneInstance.getProperties().get(name);
        if (fxomProperty != null) {
            assert fxomProperty instanceof FXOMPropertyC;
            final FXOMPropertyC fxomPropertyC = (FXOMPropertyC) fxomProperty;
            int index = 0;
            for (FXOMObject v : fxomPropertyC.getChildren()) {
                if (v.getSceneGraphObject() != null
                        && extepectedClass.isAssignableFrom(v.getSceneGraphObject().getClass())) {
                    assert v instanceof FXOMInstance;
                    if (selected == null || selected.contains(index++)) {
                        result.add((FXOMInstance) v);
                    }
                }
            }
        }

        return result;
    }

    private static final IntegerPropertyMetadata columnIndexMeta = new IntegerPropertyMetadata.Builder()
            .withName(new PropertyName("columnIndex", GridPane.class)) // NOCHECK
            .withReadWrite(true).withDefaultValue(0).withInspectorPath(InspectorPath.UNUSED).build();

    private List<FXOMObject> collectSelectedObjectsInColumn() {
        final List<FXOMObject> result = new ArrayList<>();

        final DesignHierarchyMask m = designHierarchyMaskFactory.getMask(parentObject);
        assert m.getMainAccessory() != null;

        for (FXOMObject childObject:m.getSubComponents(m.getMainAccessory(), false)) {
            if (childObject instanceof FXOMInstance) {
                final FXOMInstance childInstance = (FXOMInstance) childObject;
                if (indexes.contains(columnIndexMeta.getValue(childInstance))) {
                    // child belongs to a selected column
                    result.add(childInstance);
                }
            }
        }

        return result;
    }

    private static final IntegerPropertyMetadata rowIndexMeta = new IntegerPropertyMetadata.Builder()
            .withName(new PropertyName("rowIndex", GridPane.class)) // NOCHECK
            .withReadWrite(true).withDefaultValue(0).withInspectorPath(InspectorPath.UNUSED).build();

    private List<FXOMObject> collectSelectedObjectsInRow() {
        final List<FXOMObject> result = new ArrayList<>();

        final DesignHierarchyMask m = designHierarchyMaskFactory.getMask(parentObject);
        assert m.getMainAccessory() != null;

        for (FXOMObject childObject:m.getSubComponents(m.getMainAccessory(), false)) {
            if (childObject instanceof FXOMInstance) {
                final FXOMInstance childInstance = (FXOMInstance) childObject;
                if (indexes.contains(rowIndexMeta.getValue(childInstance))) {
                    // child belongs to a selected column
                    result.add(childInstance);
                }
            }
        }

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends GroupFactory<GridSelectionGroup> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public GridSelectionGroup getGroup(FXOMObject parentObject, Type type, Set<Integer> indexes) {
            return create(GridSelectionGroup.class, j -> j.setGroupParameters(parentObject, type, indexes));
        }

        public GridSelectionGroup getGroup(FXOMObject parentObject, Type type, int index) {
            return create(GridSelectionGroup.class, j -> j.setGroupParameters(parentObject, type, Set.of(index)));
        }
    }

    @Override
    protected Job makeDeleteJob() {
        return deleteGridSelectionJobFactory.getJob();
    }

    /**
     * Toggle. Adds/removes the specified column/row to/from the selected items.
     *
     * @param toggleGroup the toggle group
     * @return the abstract selection group
     */
    @Override
    protected AbstractSelectionGroup toggle(AbstractSelectionGroup toggleGroup) {
        if (toggleGroup.getClass() == getClass()) {
            GridSelectionGroup gridToggleGroup = (GridSelectionGroup) toggleGroup;

            assert gridToggleGroup.getAncestor() != null;
            assert gridToggleGroup.getAncestor().getSceneGraphObject() instanceof GridPane;

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
    protected boolean isSelected(AbstractSelectionGroup group) {
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
    protected Node getCheckedHitNode() {
        return null;
    }

    @Override
    protected boolean isMovable() {
        return false;
    }

    @Override
    public List<FXOMObject> getSiblings() {
        return Collections.unmodifiableList((List<FXOMObject>)(List)collectSiblingConstraintInstances());
    }

    @Override
    public AbstractSelectionGroup selectAll() {
        List<FXOMObject> siblings = this.getSiblings();
        if (siblings.size() <= 1) {
            return this;
        }
        Set<Integer> allIndexes = IntStream.range(0, siblings.size()).boxed().collect(Collectors.toSet());
        return gridSelectionGroupFactory.getGroup(this.getAncestor(), this.getType(), allIndexes);
    }

    @Override
    public AbstractSelectionGroup selectNext() {
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
    public AbstractSelectionGroup selectPrevious() {
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

    @Override
    public Map<String, FXOMObject> collectSelectedFxIds() {
        // Collects fx:ids in selected objects and their descendants.
        final Map<String, FXOMObject> fxIdMap = new HashMap<>();
        for (FXOMObject selectedObject : collectSelectedObjects()) {
            fxIdMap.putAll(selectedObject.collect(FxIdCollector.fxIdsMap()));
        }
        return fxIdMap;
    }
}
