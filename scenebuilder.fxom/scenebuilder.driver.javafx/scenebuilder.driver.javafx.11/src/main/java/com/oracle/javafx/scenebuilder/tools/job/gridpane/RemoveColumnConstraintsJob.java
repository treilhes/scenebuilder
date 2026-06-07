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
package com.oracle.javafx.scenebuilder.tools.job.gridpane;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.fxom.api.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.fxom.api.job.base.BatchDocumentJob;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMInstance;
import com.treilhes.jfxplace.fxom.model.FXOMObject;

/**
 * Job invoked when removing column constraints.
 */
@ApplicationInstancePrototype
public final class RemoveColumnConstraintsJob extends BatchDocumentJob {

    private FXOMObject targetGridPane;
    private List<Integer> targetIndexes;
    private final GridPaneHierarchyMask.Factory maskFactory;
    private final SelectionJobsFactory selectionJobsFactory;

    protected RemoveColumnConstraintsJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            SelectionJobsFactory selectionJobsFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
        super(extensionFactory, documentManager);
        this.maskFactory = maskFactory;
        this.selectionJobsFactory = selectionJobsFactory;

    }

    protected void setJobParameters(FXOMObject targetGridPane, List<Integer> targetIndexes) {
        assert targetGridPane != null;
        assert targetIndexes != null;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = targetIndexes;
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();

        // Remove column constraints job
        assert targetGridPane instanceof FXOMInstance;
        assert targetIndexes.isEmpty() == false;

        final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);
        for (int targetIndex : targetIndexes) {
            final FXOMObject targetConstraints
                    = mask.getColumnConstraintsAtIndex(targetIndex);
            // The target index is associated to an existing constraints value :
            // => we remove the constraints value
            if (targetConstraints != null) {
                final Job removeValueJob = selectionJobsFactory.deleteObject(targetConstraints);
                result.add(removeValueJob);
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Remove Column Constraints"; //NOCHECK
    }

    @ApplicationInstanceSingleton
    public static class Factory extends JobFactory<RemoveColumnConstraintsJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link RemoveColumnConstraintsJob} job
         * @param targetGridPane the target grid pane
         * @param targetIndexes the column indexes to delete
         * @return the job to execute
         */
        public RemoveColumnConstraintsJob getJob(FXOMObject targetGridPane, List<Integer> targetIndexes) {
            return create(RemoveColumnConstraintsJob.class, j -> j.setJobParameters(targetGridPane, targetIndexes));
        }
    }
}
