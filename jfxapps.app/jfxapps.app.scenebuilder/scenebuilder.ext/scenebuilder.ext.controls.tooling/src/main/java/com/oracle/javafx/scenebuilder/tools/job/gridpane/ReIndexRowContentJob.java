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
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.job.editor.atomic.ModifyObjectJob;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

/**
 * Job invoked when re-indexing rows content.
 * Update the value of the static property GridPane.rowIndex by adding an offset
 * Only the content with a GridPane.rowIndex contained in the targeted indexes are updated
 * IMPORTANT: This job cannot extends BatchDocumentJob because its sub jobs list
 * cannot be initialized lazily.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ReIndexRowContentJob extends AbstractJob {

    private AbstractJob subJob;
    private int offset;
    private FXOMObject targetGridPane;
    private final List<Integer> targetIndexes = new ArrayList<>();

    private final FXOMDocument fxomDocument;
    private final IMetadata metadata;
    private final BatchJob.Factory batchJobFactory;
    private final ModifyObjectJob.Factory modifyObjectJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    // @formatter:off
    protected ReIndexRowContentJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            IMetadata metadata,
            BatchJob.Factory batchJobFactory,
            ModifyObjectJob.Factory modifyObjectJobFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
    // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.metadata = metadata;
        this.batchJobFactory = batchJobFactory;
        this.modifyObjectJobFactory = modifyObjectJobFactory;
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
        // When the rows are empty, there is no content to move and the
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
        return "ReIndex Row Content"; // NOCHECK
    }

    private void buildSubJobs() {

        // Create sub job
        BatchJob batchJob = batchJobFactory.getJob();

        assert targetIndexes.isEmpty() == false;

        final GridPaneHierarchyMask targetGridPaneMask = maskFactory.getMask(targetGridPane);
        final PropertyName propertyName = new PropertyName("rowIndex", javafx.scene.layout.GridPane.class); // NOCHECK

        for (int targetIndex : targetIndexes) {
            final List<FXOMObject> children = targetGridPaneMask.getRowContentAtIndex(targetIndex);
            for (FXOMObject child : children) {
                assert child instanceof FXOMInstance;
                final FXOMInstance childInstance = (FXOMInstance) child;
                final ValuePropertyMetadata vpm = metadata.queryValueProperty(childInstance, propertyName);
                int newIndexValue = targetIndex + offset;
                final AbstractJob modifyJob = modifyObjectJobFactory.getJob(null, childInstance, vpm, newIndexValue);
                batchJob.addSubJob(modifyJob);
            }
        }

        subJob = batchJob;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<ReIndexRowContentJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create a {@link ReIndexRowContentJob} job
         *
         * @param offset the offset to apply
         * @param targetGridPane the gridpane targeted by the job
         * @param targetIndexes column indexes on which the offset must be applied
         * @return the job to execute
         */
        public ReIndexRowContentJob getJob(int offset, FXOMObject targetGridPane, List<Integer> targetIndexes) {
            return create(ReIndexRowContentJob.class, j -> j.setJobParameters(offset, targetGridPane, targetIndexes));
        }

        /**
         * Create a {@link ReIndexRowContentJob} job
         *
         * @param offset the offset to apply
         * @param targetGridPane the gridpane targeted by the job
         * @param targetIndex column index on which the offset must be applied
         * @return the job to execute
         */
        public ReIndexRowContentJob getJob(int offset, FXOMObject targetGridPane, int targetIndex) {
            return create(ReIndexRowContentJob.class, j -> j.setJobParameters(offset, targetGridPane, List.of(targetIndex)));
        }
    }
}
