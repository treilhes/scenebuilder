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
package com.oracle.javafx.scenebuilder.tools.job.wrap;

import static com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.OVERLAP_FUZZ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.fxml.selection.job.SetDocumentRootJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ToggleFxRootJob;
import com.oracle.javafx.scenebuilder.metadata.javafx.hidden.ColumnConstraintsMetadata;
import com.oracle.javafx.scenebuilder.metadata.javafx.hidden.RowConstraintsMetadata;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.BidimensionalComparator;
import com.oracle.javafx.scenebuilder.tools.job.wrap.FXOMObjectCourseComparator.GridCourse;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

/**
 * Job used to wrap selection in a GridPane.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class WrapInGridPaneJob extends AbstractWrapInSubComponentJob {

    private static final double DEFAULT_MIN_WIDTH = 10;
    private static final double DEFAULT_MIN_HEIGHT = 10;
    // Key = FXOM object
    // Value = 2 dimensions integer array for the COLUMN and ROW index
    private final Map<FXOMObject, int[]> indices = new HashMap<>();
    private final FXOMDocument fxomDocument;
    private final IMetadata metadata;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;

    protected WrapInGridPaneJob(JobExtensionFactory extensionFactory, FxmlDocumentManager documentManager,
            Selection selection, DesignHierarchyMask.Factory designMaskFactory, IMetadata metadata,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            DefaultSelectionGroupFactory.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection, designMaskFactory, metadata, addPropertyValueJobFactory,
                toggleFxRootJobFactory, modifyFxControllerJobFactory, setDocumentRootJobFactory, removePropertyValueJobFactory,
                removePropertyJobFactory, modifyObjectJobFactory, addPropertyJobFactory, objectSelectionGroupFactory);
        this.metadata = metadata;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        newContainerClass = GridPane.class;
    }

    @Override
    protected List<AbstractJob> modifyChildrenJobs(final List<FXOMObject> children) {
        final List<AbstractJob> jobs = super.modifyChildrenJobs(children);


        for (FXOMObject child : children) {

            ValuePropertyMetadata columnIndexMeta = metadata.queryValueProperty((FXOMInstance) child,
                    new PropertyName("columnIndex", GridPane.class));
            ValuePropertyMetadata rowIndexMeta = metadata.queryValueProperty((FXOMInstance) child,
                    new PropertyName("rowIndex", GridPane.class));
            int[] childIndices = indices.get(child);

            // Modify child column index
            final AbstractJob modifyColumnIndex = modifyObjectJobFactory.getJob((FXOMInstance) child, columnIndexMeta,
                    childIndices[GridCourse.COL_BY_COL.index()]);
            jobs.add(modifyColumnIndex);

            // Modify child row index
            final AbstractJob modifyRowIndex = modifyObjectJobFactory.getJob((FXOMInstance) child, rowIndexMeta,
                    childIndices[GridCourse.ROW_BY_ROW.index()]);

            jobs.add(modifyRowIndex);
        }
        return jobs;
    }

    @Override
    protected void modifyNewContainer(final List<FXOMObject> children) {
        super.modifyNewContainer(children);

        // Update the GridPane constraints depending on its children positionning
        // Find and set the column index for each element in the indices map.
        final int maxcol = computeIndexByCourse(children, GridCourse.COL_BY_COL, indices);
        // Find and set the row index for each element in the indices map.
        final int maxrow = computeIndexByCourse(children, GridCourse.ROW_BY_ROW, indices);
        final double[] columnWidth = new double[maxcol + 1];
        final double[] rowHeight = new double[maxrow + 1];
        computeSizes(children, indices, columnWidth, rowHeight);

        // COLUMNS
        for (int index = 0; index <= maxcol; index++) {
            final FXOMInstance constraint = makeConstraintsInstance(ColumnConstraints.class);
            ColumnConstraintsMetadata.hgrowPropertyMetadata.setValue(constraint, Priority.SOMETIMES.name());

            ColumnConstraintsMetadata.minWidthPropertyMetadata.setValue(constraint, DEFAULT_MIN_WIDTH);

            if (columnWidth[index] >= DEFAULT_MIN_WIDTH) {
                ColumnConstraintsMetadata.minWidthPropertyMetadata.setValue(constraint, DEFAULT_MIN_WIDTH);
            } else {
                ColumnConstraintsMetadata.minWidthPropertyMetadata.setValue(constraint, columnWidth[index]);
            }

            addColumnConstraints(fxomDocument, newContainer, constraint, index);
        }

        // ROWS
        for (int index = 0; index <= maxrow; index++) {
            final FXOMInstance constraint = makeConstraintsInstance(RowConstraints.class);

            RowConstraintsMetadata.vgrowPropertyMetadata.setValue(constraint, Priority.SOMETIMES.name());
            if (rowHeight[index] >= DEFAULT_MIN_HEIGHT) {
                RowConstraintsMetadata.minHeightPropertyMetadata.setValue(constraint, DEFAULT_MIN_HEIGHT);
            } else {
                RowConstraintsMetadata.minHeightPropertyMetadata.setValue(constraint, rowHeight[index]);
            }

            addRowConstraints(fxomDocument, newContainer, constraint, index);
        }
    }

    /**
     * This method computes either the ROW index or COLUMN index of each
     * element, by running through a would-be grid according to a given course.
     * For instance, when course==ROW_BY_ROW, this method first order the
     * elements row by row, and then sets their ROW index inside the indices
     * map. When course==COL_BY_COL, this method orders the elements column by
     * column, and then sets their COLUMN index inside the indices map. Note
     * that this method leaves the original elements and children list
     * unchanged. All it does is populating the indices map.
     *
     * @param fxomObjects The children of the would-be grid.
     * @param course The course for which this method runs.
     * @param indices The indices map.
     * @return the greater index.
     */
    private int computeIndexByCourse(
            final List<FXOMObject> fxomObjects,
            final GridCourse course,
            final Map<FXOMObject, int[]> indices) {

        final BidimensionalComparator comparator = new BidimensionalComparator(course);
        final List<FXOMObject> unsorted = new ArrayList<>(fxomObjects);
        Collections.sort(unsorted, comparator);
        FXOMObject lastObject = null;
        int rc = 0;
        int max = -1;
        for (int i = 0; i < unsorted.size(); i++) {
            FXOMObject currentObject = unsorted.get(i);
            int[] ind = indices.get(currentObject);
            if (ind == null) {
                ind = new int[2];
                indices.put(currentObject, ind);
            }
            if (lastObject != null) {
                if (comparator.compare(lastObject, currentObject) != 0) {
                    final Node lastNode = (Node) lastObject.getSceneGraphObject();
                    final Node currentNode = (Node) currentObject.getSceneGraphObject();
                    final Bounds lastBounds = lastNode.getBoundsInParent();
                    final Bounds currentBounds = currentNode.getBoundsInParent();
                    if (course.getMinY(currentBounds) >= course.getMaxY(lastBounds) - OVERLAP_FUZZ) {
                        rc++;
                    }
                }
            }
            ind[course.index()] = rc;
            max = Math.max(max, rc);
            lastObject = currentObject;
        }
        return max;
    }

    private void computeSizes(
            final List<FXOMObject> fxomObjects,
            final Map<FXOMObject, int[]> indices,
            double[] columnWidth, double[] rowHeight) {

        for (FXOMObject fxomObject : fxomObjects) {
            final Node node = (Node) fxomObject.getSceneGraphObject();
            final double width = node.getBoundsInLocal().getWidth();
            final double height = node.getBoundsInLocal().getHeight();
            final int[] ind = indices.get(fxomObject);
            final int col = ind[GridCourse.COL_BY_COL.index()];
            final int row = ind[GridCourse.ROW_BY_ROW.index()];
            columnWidth[col] = Math.max(columnWidth[col], width);
            rowHeight[row] = Math.max(rowHeight[row], height);
        }
    }

    private FXOMInstance makeConstraintsInstance(final Class<?> constraintsClass) {

        // Create new constraints instance
        final FXOMDocument newDocument = new FXOMDocument();
        final FXOMInstance result
                = new FXOMInstance(newDocument, constraintsClass);
        newDocument.setFxomRoot(result);
        result.moveToFxomDocument(fxomDocument);

        return result;
    }

    private static void addColumnConstraints(
            final FXOMDocument fxomDocument,
            final FXOMInstance gridPane,
            final FXOMInstance constraints, int index) {
        final PropertyName propertyName = new PropertyName("columnConstraints"); //NOCHECK
        FXOMProperty property = gridPane.getProperties().get(propertyName);
        if (property == null) {
            property = new FXOMPropertyC(fxomDocument, propertyName);
        }
        if (property.getParentInstance() == null) {
            property.addToParentInstance(-1, gridPane);
        }
        assert property instanceof FXOMPropertyC;
        constraints.addToParentProperty(index, property);
    }

    private static void addRowConstraints(
            final FXOMDocument fxomDocument,
            final FXOMInstance gridPane,
            final FXOMInstance constraints, int index) {
        final PropertyName propertyName = new PropertyName("rowConstraints"); //NOCHECK
        FXOMProperty property = gridPane.getProperties().get(propertyName);
        if (property == null) {
            property = new FXOMPropertyC(fxomDocument, propertyName);
        }
        if (property.getParentInstance() == null) {
            property.addToParentInstance(-1, gridPane);
        }
        assert property instanceof FXOMPropertyC;
        constraints.addToParentProperty(index, property);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<WrapInGridPaneJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link WrapInFlowPaneJob} job
         * @return the job to execute
         */
        public WrapInGridPaneJob getJob() {
            return create(WrapInGridPaneJob.class, null);
        }
    }
}
