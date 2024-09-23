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
package com.oracle.javafx.scenebuilder.tools.job.gridpane;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.AbstractSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup.Type;
import com.oracle.javafx.scenebuilder.tools.job.gridpane.GridPaneJobUtils.Position;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

/**
 * Job invoked when adding rows.
 *
 * This job handles multi-selection as follows :
 * - if multiple GridPanes are selected, no row can be selected.
 * We add the new row to each GP, either at first position (add above)
 * or last position (add below).
 * - if multiple rows are selected, a single GridPane is selected.
 * We add new rows for each selected row, either above or below.
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class AddRowJob extends BatchSelectionJob {

    private final AddRowConstraintsJob.Factory addRowConstraintsJobFactory;
    private final ReIndexRowContentJob.Factory reIndexRowContentJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;
    private final DSelectionGroupFactory.Factory objectSelectionGroupFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;

    /**
     * Key = target GridPane instance<br/>
     * Value = set of target column indexes for this GridPane
     */
    private final Map<FXOMObject, Set<Integer>> targetGridPanes = new HashMap<>();

    private Position position;

    // @formatter:off
    protected AddRowJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            AddRowConstraintsJob.Factory addRowConstraintsJobFactory,
            ReIndexRowContentJob.Factory reIndexRowContentJobFactory,
            GridPaneHierarchyMask.Factory maskFactory,
            DSelectionGroupFactory.Factory objectSelectionGroupFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.addRowConstraintsJobFactory = addRowConstraintsJobFactory;
        this.reIndexRowContentJobFactory = reIndexRowContentJobFactory;
        this.maskFactory = maskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;

    }

    protected void setJobParameters(Position position) {
        assert position == Position.ABOVE || position == Position.BELOW;
        this.position = position;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        if (GridPaneJobUtils.canPerformAdd(getSelection())) {

            // Populate the target GridPane map
            assert targetGridPanes.isEmpty() == true;
            final List<FXOMObject> objectList
                    = GridPaneJobUtils.getTargetGridPanes(getSelection());
            for (FXOMObject object : objectList) {
                final Set<Integer> indexList = getTargetRowIndexes(getSelection(), object);
                targetGridPanes.put(object, indexList);
            }

            // Add sub jobs
            // First add the new row constraints
            final AbstractJob addConstraints = addRowConstraintsJobFactory.getJob(position, targetGridPanes);
            result.add(addConstraints);
            // Then move the row content
            result.addAll(moveRowContent());
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Add Row " + position.name(); //NOCHECK
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final AbstractSelectionGroup asg;
        // Update new selection :
        // - if there is more than 1 GridPane, we select the GridPane instances
        // - if there is a single GridPane, we select the added rows
        if (targetGridPanes.size() > 1) {
            Set<FXOMObject> objects = targetGridPanes.keySet();
            asg = objectSelectionGroupFactory.getGroup(objects, objects.iterator().next(), null);
        } else {
            assert targetGridPanes.size() == 1;
            final FXOMInstance targetGridPane
                    = (FXOMInstance) targetGridPanes.keySet().iterator().next();
            final Set<Integer> targetIndexes = targetGridPanes.get(targetGridPane);
            assert targetIndexes.size() >= 1;
            final Set<Integer> addedIndexes
                    = GridPaneJobUtils.getAddedIndexes(targetIndexes, position);

            asg = gridSelectionGroupFactory.getGroup(targetGridPane, Type.ROW, addedIndexes);
        }
        return asg;
    }

    private List<AbstractJob> moveRowContent() {

        final List<AbstractJob> result = new ArrayList<>();

        for (FXOMObject targetGridPane : targetGridPanes.keySet()) {

            final Set<Integer> targetIndexes = targetGridPanes.get(targetGridPane);

            final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);
            final int rowsSize = mask.getRowsSize();
            final Iterator<Integer> iterator = targetIndexes.iterator();

            int shiftIndex = 0;
            int targetIndex = iterator.next();
            while (targetIndex != -1) {
                // Move the rows content :
                // - from the target index
                // - to the next target index if any or the last row index otherwise
                int fromIndex, toIndex;

                switch (position) {
                    case ABOVE:
                        // fromIndex included
                        // toIndex excluded
                        fromIndex = targetIndex;
                        if (iterator.hasNext()) {
                            targetIndex = iterator.next();
                            toIndex = targetIndex - 1;
                        } else {
                            targetIndex = -1;
                            toIndex = rowsSize - 1;
                        }
                        break;
                    case BELOW:
                        // fromIndex excluded
                        // toIndex included
                        fromIndex = targetIndex + 1;
                        if (iterator.hasNext()) {
                            targetIndex = iterator.next();
                            toIndex = targetIndex;
                        } else {
                            targetIndex = -1;
                            toIndex = rowsSize - 1;
                        }
                        break;
                    default:
                        assert false;
                        return result;
                }

                // If fromIndex >= rowsSize, we are below the last existing row
                // => no row content to move
                if (fromIndex < rowsSize) {
                    final int offset = 1 + shiftIndex;
                    final List<Integer> indexes
                            = GridPaneJobUtils.getIndexes(fromIndex, toIndex);
                    final AbstractJob reIndexJob = reIndexRowContentJobFactory.getJob(offset, targetGridPane,
                            indexes);
                    result.add(reIndexJob);
                }

                shiftIndex++;
            }
        }
        return result;
    }

    /**
     * Returns the list of target row indexes for the specified GridPane
     * instance.
     *
     * @return the list of target indexes
     */
    private Set<Integer> getTargetRowIndexes(
            final Selection selection,
            final FXOMObject targetGridPane) {

        final AbstractSelectionGroup asg = selection.getGroup();

        final Set<Integer> result = new LinkedHashSet<>();

        // Selection == GridPane rows
        // => return the list of selected rows
        if (asg instanceof GridSelectionGroup
                && ((GridSelectionGroup) asg).getType() == Type.ROW) {
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
            result.addAll(gsg.getIndexes());
        } //
        // Selection == GridPanes or Selection == GridPane columns
        // => return either the first (ABOVE) or the last (BELOW) row index
        else {
            switch (position) {
                case ABOVE:
                    result.add(0);
                    break;
                case BELOW:
                    final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);
                    final int size = mask.getRowsSize();
                    result.add(size - 1);
                    break;
                default:
                    assert false;
                    break;
            }
        }

        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<AddRowJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  AddRowJob} job
         * @param position position relative to selected grid pane rows or to the selected grid pane
         * @return the job to execute
         */
        public AddRowJob getJob(Position position) {
            return create(AddRowJob.class, j -> j.setJobParameters(position));
        }
    }
}
