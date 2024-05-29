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

package com.oracle.javafx.scenebuilder.tools.driver.gridpane;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.control.droptarget.AbstractDropTarget;
import com.gluonhq.jfxapps.core.api.control.droptarget.DropTargetFactory;
import com.gluonhq.jfxapps.core.api.dnd.DragSource;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.Deprecation;
import com.gluonhq.jfxapps.core.job.editor.atomic.RemoveObjectJob;
import com.gluonhq.jfxapps.util.GridBounds;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.fxml.selection.job.ClearSelectionJob;
import com.oracle.javafx.scenebuilder.fxml.selection.job.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.fxml.selection.job.UpdateSelectionJob;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridSnapshot;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertColumnJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.InsertRowJob;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.MoveCellContentJob;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class GridPaneDropTarget extends AbstractDropTarget {

    public enum ColumnArea {
        LEFT, CENTER, RIGHT
    }

    public enum RowArea {
        TOP, CENTER, BOTTOM
    }

    private final DesignHierarchyMask.Factory designMaskFactory;
    private final BatchJob.Factory batchJobFactory;
    private final ClearSelectionJob.Factory clearSelectionJobFactory;
    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final InsertColumnJob.Factory insertColumnJobFactory;
    private final InsertRowJob.Factory insertRowJobFactory;
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;
    private final MoveCellContentJob.Factory moveCellContentJobFactory;
    private final UpdateSelectionJob.Factory updateSelectionJobFactory;

    private FXOMObject targetGridPane;
    private int targetColumnIndex;
    private int targetRowIndex;
    private ColumnArea targetColumnArea;
    private RowArea targetRowArea;

    // @formatter:off
    protected GridPaneDropTarget(
            DesignHierarchyMask.Factory designMaskFactory,
            BatchJob.Factory batchJobFactory,
            ClearSelectionJob.Factory clearSelectionJobFactory,
            RemoveObjectJob.Factory removeObjectJobFactory,
            InsertColumnJob.Factory insertColumnJobFactory,
            InsertRowJob.Factory insertRowJobFactory,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory,
            MoveCellContentJob.Factory moveCellContentJobFactory,
            UpdateSelectionJob.Factory updateSelectionJobFactory) {
     // @formatter:on
        this.designMaskFactory = designMaskFactory;
        this.batchJobFactory = batchJobFactory;
        this.clearSelectionJobFactory = clearSelectionJobFactory;
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.insertColumnJobFactory = insertColumnJobFactory;
        this.insertRowJobFactory = insertRowJobFactory;
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;
        this.moveCellContentJobFactory = moveCellContentJobFactory;
        this.updateSelectionJobFactory = updateSelectionJobFactory;
    }

    protected void setDropTargetParameters(FXOMObject targetGridPane, int columnIndex, int rowIndex,
            ColumnArea targetColumnArea, RowArea targetRowArea) {
        assert targetGridPane != null;
        assert targetGridPane.getSceneGraphObject() instanceof GridPane;
        assert columnIndex >= 0;
        assert rowIndex >= 0;

        this.targetGridPane = targetGridPane;
        this.targetColumnIndex = columnIndex;
        this.targetRowIndex = rowIndex;
        this.targetColumnArea = targetColumnArea;
        this.targetRowArea = targetRowArea;
    }

    public int getTargetColumnIndex() {
        return targetColumnIndex;
    }

    public int getTargetRowIndex() {
        return targetRowIndex;
    }

    public ColumnArea getTargetColumnArea() {
        return targetColumnArea;
    }

    public RowArea getTargetRowArea() {
        return targetRowArea;
    }

    /*
     * AbstractDropTarget
     */
    @Override
    public FXOMObject getTargetObject() {
        return targetGridPane;
    }

    @Override
    public boolean acceptDragSource(DragSource dragSource) {
        assert dragSource != null;

        final boolean result;
        if (dragSource.getDraggedObjects().isEmpty()) {
            result = false;
        } else {
            final HierarchyMask m = designMaskFactory.getMask(targetGridPane);
            if (m.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                final FXOMObject draggedObject0 = dragSource.getDraggedObjects().get(0);
                assert draggedObject0.getSceneGraphObject() instanceof Node;

                final Node draggedNode0 = (Node) draggedObject0.getSceneGraphObject();
                final Integer columIndexObj = GridPane.getColumnIndex(draggedNode0);
                final Integer rowIndexObj = GridPane.getRowIndex(draggedNode0);
                final int currentColumnIndex = (columIndexObj == null) ? 0 : columIndexObj;
                final int currentRowIndex = (rowIndexObj == null) ? 0 : rowIndexObj;

                final boolean sameContainer = targetGridPane == draggedObject0.getParentObject();
                final boolean sameColumnIndex = targetColumnIndex == currentColumnIndex;
                final boolean sameRowIndex = targetRowIndex == currentRowIndex;
                final boolean sameArea = (targetColumnArea == ColumnArea.CENTER) && (targetRowArea == RowArea.CENTER);

                result = (sameContainer == false) || (sameColumnIndex == false) || (sameRowIndex == false)
                        || (sameArea == false);
            } else {
                result = false;
            }
        }

        return result;
    }

    @Override
    public AbstractJob makeDropJob(DragSource dragSource) {
        assert acceptDragSource(dragSource); // (1)

        final boolean shouldRefreshSceneGraph = true;
        final BatchJob result = batchJobFactory.getJob(dragSource.makeDropJobDescription(), shouldRefreshSceneGraph);

        final List<FXOMObject> draggedObjects = dragSource.getDraggedObjects();
        final FXOMObject hitObject = dragSource.getHitObject();
        final FXOMObject currentParent = hitObject.getParentObject();
        final boolean reparenting = (currentParent != targetGridPane);
        final GridPane gridPane = (GridPane) targetGridPane.getSceneGraphObject();

        // Steps:
        //
        // 1) snapshot grid related properties of dragged objects
        // => this must be done here because they will be lost by #1
        // 2) clear the selection
        // 3) remove drag source objects from their current parent (if any)
        // 4) add new columns/rows in target grip pane as needed
        // 5) add drag source objects to this drop target
        // 6) restore grid related properties
        // 7) select the dragged objects
        //
        // Note: if source and target parents are the same, skip #2,#3,#5,#7 and #8

        // Step #1
        final GridSnapshot gridSnapshot;
        if ((currentParent != null) && (currentParent.getSceneGraphObject() instanceof GridPane)) {
            gridSnapshot = new GridSnapshot(draggedObjects);
        } else {
            gridSnapshot = new GridSnapshot(draggedObjects, 1);
        }

        if (reparenting) {

            // Step #2
            result.addSubJob(clearSelectionJobFactory.getJob());

            // Step #3
            if (currentParent != null) {
                for (FXOMObject draggedObject : draggedObjects) {
                    result.addSubJob(removeObjectJobFactory.getJob(draggedObject));
                }
            }
        }

        // Step #4
        final GridBounds snapshotBounds = gridSnapshot.getBounds();
        final int hitColumnIndex = gridSnapshot.getColumnIndex(hitObject);
        final int hitRowIndex = gridSnapshot.getRowIndex(hitObject);
        final int destColumnIndex = (targetColumnArea == ColumnArea.RIGHT) ? targetColumnIndex + 1 : targetColumnIndex;
        final int destRowIndex = (targetRowArea == RowArea.BOTTOM) ? targetRowIndex + 1 : targetRowIndex;
        final int columnDelta = destColumnIndex - hitColumnIndex;
        final int rowDelta = destRowIndex - hitRowIndex;
        final GridBounds adjustedBounds = snapshotBounds.move(columnDelta, rowDelta);

        // Step #4.1 : columns
        switch (targetColumnArea) {
        case LEFT:
        case RIGHT: { // Insert columns at destColumnIndex
            final int insertCount = snapshotBounds.getColumnSpan();
            result.addSubJob(insertColumnJobFactory.getJob(targetGridPane, destColumnIndex, insertCount));
            break;
        }
        case CENTER: {// Insert columns at right (first) and left ends if needed
            final int targetColumnCount = Deprecation.getGridPaneColumnCount(gridPane);
            if (adjustedBounds.getMaxColumnIndex() > targetColumnCount) {
                final int insertCount = adjustedBounds.getMaxColumnIndex() - targetColumnCount;
                result.addSubJob(insertColumnJobFactory.getJob(targetGridPane, targetColumnCount, insertCount));
            }
            if (adjustedBounds.getMinColumnIndex() < 0) {
                final int insertCount = -adjustedBounds.getMinColumnIndex();
                result.addSubJob(insertColumnJobFactory.getJob(targetGridPane, 0, insertCount));
            }
            break;
        }
        }

        // Step #4.2 : rows
        switch (targetRowArea) {
        case TOP:
        case BOTTOM: { // Insert rows at destRowIndex
            final int insertCount = snapshotBounds.getRowSpan();
            result.addSubJob(insertRowJobFactory.getJob(targetGridPane, destRowIndex, insertCount));
            break;
        }
        case CENTER: { // Insert rows at bottom (first) and top ends if needed
            final int targetRowCount = Deprecation.getGridPaneRowCount(gridPane);
            if (adjustedBounds.getMaxRowIndex() > targetRowCount) {
                final int insertCount = adjustedBounds.getMaxRowIndex() - targetRowCount;
                result.addSubJob(insertRowJobFactory.getJob(targetGridPane, targetRowCount, insertCount));
            }
            if (adjustedBounds.getMinRowIndex() < 0) {
                final int insertCount = -adjustedBounds.getMinRowIndex();
                result.addSubJob(insertRowJobFactory.getJob(targetGridPane, 0, insertCount));
            }
            break;
        }
        }

        if (reparenting) {

            // Step #5
            for (FXOMObject draggedObject : draggedObjects) {
                final AbstractJob j = insertAsSubComponentJobFactory.getJob(draggedObject, targetGridPane, -1);
                result.addSubJob(j);
            }
        }

        // Step #6
        for (FXOMObject draggedObject : draggedObjects) {
            assert draggedObject instanceof FXOMInstance; // Because (1)
            result.addSubJob(moveCellContentJobFactory.getJob((FXOMInstance) draggedObject, columnDelta, rowDelta));
        }

        if (reparenting) {

            // Step #7
            result.addSubJob(updateSelectionJobFactory.getJob(draggedObjects));
        }

        assert result.isExecutable();

        return result;
    }

    @Override
    public boolean isSelectRequiredAfterDrop() {
        return true;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends DropTargetFactory<GridPaneDropTarget> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public GridPaneDropTarget getDropTarget(FXOMObject targetGridPane, int columnIndex, int rowIndex,
                ColumnArea targetColumnArea, RowArea targetRowArea) {
            return create(GridPaneDropTarget.class, j -> j.setDropTargetParameters(targetGridPane, columnIndex,
                    rowIndex, targetColumnArea, targetRowArea));
        }

    }
}
