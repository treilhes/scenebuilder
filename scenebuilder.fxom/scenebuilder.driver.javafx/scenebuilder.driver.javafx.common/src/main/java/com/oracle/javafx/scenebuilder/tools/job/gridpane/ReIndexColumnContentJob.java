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

import com.oracle.javafx.scenebuilder.metadata.custom.SbMetadata;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.fxom.job.base.BatchJob;
import com.treilhes.jfxplace.core.api.fxom.jobs.FxomJobsFactory;
import com.treilhes.jfxplace.core.api.fxom.subjects.FxomEvents;
import com.treilhes.jfxplace.core.api.job.JobExtensionFactory;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.core.api.job.base.AbstractJob;
import com.treilhes.jfxplace.core.fxom.FXOMDocument;
import com.treilhes.jfxplace.core.fxom.FXOMInstance;
import com.treilhes.jfxplace.core.fxom.FXOMObject;
import com.treilhes.jfxplace.core.fxom.util.PropertyName;

/**
 * Job invoked when re-indexing columns content.
 * Update the value of the static property GridPane.columnIndex by adding an offset
 * Only the content with a GridPane.columnIndex contained in the targeted indexes are updated
 * IMPORTANT: This job cannot extends BatchDocumentJob because its sub jobs list
 * cannot be initialized lazily.
 */
@ApplicationInstancePrototype
public final class ReIndexColumnContentJob extends AbstractJob {

    private AbstractJob subJob;
    private int offset;
    private FXOMObject targetGridPane;
    private final List<Integer> targetIndexes = new ArrayList<>();

    private final FXOMDocument fxomDocument;
    private final SbMetadata metadata;
    private final BatchJob.Factory batchJobFactory;
    private final FxomJobsFactory fxomJobsFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    // @formatter:off
    protected ReIndexColumnContentJob(
            JobExtensionFactory extensionFactory,
            FxomEvents documentManager,
            SbMetadata metadata,
            BatchJob.Factory batchJobFactory,
            FxomJobsFactory fxomJobsFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
    // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
        this.batchJobFactory = batchJobFactory;
        this.fxomJobsFactory = fxomJobsFactory;
        this.maskFactory = maskFactory;
    }

    protected void setJobParameters(int offset, FXOMObject targetGridPane, List<Integer> targetIndexes) {
        this.offset = offset;
        this.targetGridPane = targetGridPane;
        this.targetIndexes.addAll(targetIndexes);
        buildSubJobs();
    }

    @Override
    public boolean isExecutable() {
        // When the columns are empty, there is no content to move and the
        // sub job list may be empty.
        // => we do not invoke subJob.isExecutable() here.
        return subJob != null;
    }

    @Override
    public void doExecute() {
        assert isExecutable();
        fxomDocument.beginUpdate();
        subJob.execute();
        fxomDocument.endUpdate();
    }

    @Override
    public void doUndo() {
        fxomDocument.beginUpdate();
        subJob.undo();
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        fxomDocument.beginUpdate();
        subJob.redo();
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return "ReIndex Column Content"; // NOCHECK
    }

    private void buildSubJobs() {

        // Create sub job
        BatchJob batchJob = batchJobFactory.getJob();

        assert targetIndexes.isEmpty() == false;

        final GridPaneHierarchyMask targetGridPaneMask = maskFactory.getMask(targetGridPane);
        final PropertyName propertyName = new PropertyName("columnIndex", javafx.scene.layout.GridPane.class); // NOCHECK

        for (int targetIndex : targetIndexes) {
            final List<FXOMObject> children = targetGridPaneMask.getColumnContentAtIndex(targetIndex);
            for (FXOMObject child : children) {
                assert child instanceof FXOMInstance;
                final FXOMInstance childInstance = (FXOMInstance) child;
                final var vpm = metadata.queryValueProperty(childInstance, propertyName);
                int newIndexValue = targetIndex + offset;
                final var modifyJob = fxomJobsFactory.modifyObject(childInstance, vpm, newIndexValue);
                batchJob.addSubJob(modifyJob);
            }
        }

        subJob = batchJob;
    }

    @ApplicationInstanceSingleton
    public static class Factory extends JobFactory<ReIndexColumnContentJob> {
        public Factory(EmContext sbContext) {
            super(sbContext);
        }

        /**
         * Create a {@link ReIndexColumnContentJob} job
         *
         * @param offset the offset to apply
         * @param targetGridPane the gridpane targeted by the job
         * @param targetIndexes column indexes on which the offset must be applied
         * @return the job to execute
         */
        public ReIndexColumnContentJob getJob(int offset, FXOMObject targetGridPane, List<Integer> targetIndexes) {
            return create(ReIndexColumnContentJob.class,
                    j -> j.setJobParameters(offset, targetGridPane, targetIndexes));
        }

        /**
         * Create a {@link ReIndexColumnContentJob} job
         *
         * @param offset the offset to apply
         * @param targetGridPane the gridpane targeted by the job
         * @param targetIndex column index on which the offset must be applied
         * @return the job to execute
         */
        public ReIndexColumnContentJob getJob(int offset, FXOMObject targetGridPane, int targetIndex) {
            return create(ReIndexColumnContentJob.class,
                    j -> j.setJobParameters(offset, targetGridPane, List.of(targetIndex)));
        }
    }
}
