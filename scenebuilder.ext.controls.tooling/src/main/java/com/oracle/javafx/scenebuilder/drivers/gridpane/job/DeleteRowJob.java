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
package com.oracle.javafx.scenebuilder.drivers.gridpane.job;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridSelectionGroup;

import javafx.scene.layout.GridPane;

/**
 * Job invoked when removing rows.
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteRowJob extends BatchSelectionJob {

    private final List<Integer> targetIndexes = new ArrayList<>();

    private final RemoveRowConstraintsJob.Factory removeRowConstraintsJobFactory;
    private final RemoveRowContentJob.Factory removeRowContentJobFactory;
    private final ReIndexRowContentJob.Factory reIndexRowContentJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    private FXOMObject targetGridPane;

    // @formatter:off
    protected DeleteRowJob(JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            GridPaneHierarchyMask.Factory maskFactory,
            RemoveRowConstraintsJob.Factory removeRowConstraintsJobFactory,
            RemoveRowContentJob.Factory removeRowContentJobFactory,
            ReIndexRowContentJob.Factory reIndexRowContentJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.removeRowConstraintsJobFactory = removeRowConstraintsJobFactory;
        this.removeRowContentJobFactory = removeRowContentJobFactory;
        this.reIndexRowContentJobFactory = reIndexRowContentJobFactory;
        this.maskFactory = maskFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        if (GridPaneJobUtils.canPerformRemove(getSelection())) { // (1)

            // Retrieve the target GridPane
            final Selection selection = getSelection();
            final AbstractSelectionGroup asg = selection.getGroup();
            assert asg instanceof GridSelectionGroup; // Because of (1)
            final GridSelectionGroup gsg = (GridSelectionGroup) asg;

            targetGridPane = gsg.getHitItem();
            targetIndexes.addAll(gsg.getIndexes());

            // Add sub jobs
            // First remove the row constraints
            final AbstractJob removeConstraints = removeRowConstraintsJobFactory.getJob(targetGridPane, targetIndexes);
            result.add(removeConstraints);
            // Then remove the row content
            final AbstractJob removeContent = removeRowContentJobFactory.getJob(targetGridPane, targetIndexes);
            result.add(removeContent);
            // Finally shift the row content
            result.addAll(moveRowContent());
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        String result;
        switch (targetIndexes.size()) {
            case 0:
                result = "Unexecutable Delete"; //NO18N
                break;
            case 1:
                result = "Delete Row"; //NO18N
                break;
            default:
                result = makeMultipleSelectionDescription();
                break;
        }
        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        // Selection emptied
        return null;
    }

    private List<AbstractJob> moveRowContent() {

        final List<AbstractJob> result = new ArrayList<>();

        final GridPaneHierarchyMask targetGridPaneMask = maskFactory.getMask(targetGridPane);
        final int rowsSize = targetGridPaneMask.getRowsSize();
        final Iterator<Integer> iterator = targetIndexes.iterator();

        int shiftIndex = 0;
        int targetIndex, nextTargetIndex;
        targetIndex = iterator.next();
        while (targetIndex != -1) {
            // Move the rows content :
            // - from the target index
            // - to the next target index if any or the last row index otherwise
            int fromIndex, toIndex;

            // fromIndex excluded
            // toIndex excluded
            fromIndex = targetIndex + 1;
            if (iterator.hasNext()) {
                nextTargetIndex = iterator.next();
                toIndex = nextTargetIndex - 1;
            } else {
                nextTargetIndex = -1;
                toIndex = rowsSize - 1;
            }

            // When we delete 2 consecutive rows
            // => no content to move between the 2 rows
            // When we delete the last row
            // => no row content to move below the last row
            if (nextTargetIndex != (targetIndex + 1)
                    && fromIndex < rowsSize) {
                final int offset = -1 + shiftIndex;
                final List<Integer> indexes
                        = GridPaneJobUtils.getIndexes(fromIndex, toIndex);
                final AbstractJob reIndexJob = reIndexRowContentJobFactory.getJob(offset, targetGridPane, indexes);
                result.add(reIndexJob);
            }

            targetIndex = nextTargetIndex;
            shiftIndex--;
        }
        return result;
    }

    private String makeMultipleSelectionDescription() {
        final StringBuilder result = new StringBuilder();

        result.append("Delete ");
        result.append(targetIndexes.size());
        result.append(" Rows");

        return result.toString();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<DeleteRowJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  DeleteRowJob} job
         * @return the job to execute
         */
        public DeleteRowJob getJob() {
            return create(DeleteRowJob.class, j -> j.setJobParameters());
        }
    }
}
