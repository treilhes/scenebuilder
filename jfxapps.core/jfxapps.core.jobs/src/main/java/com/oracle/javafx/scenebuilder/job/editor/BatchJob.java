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
package com.oracle.javafx.scenebuilder.job.editor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Lazy;
import com.oracle.javafx.scenebuilder.core.context.annotation.Prototype;
import com.oracle.javafx.scenebuilder.core.context.annotation.Singleton;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

/**
 * Allow to group multiple jobs into one job logic unit. Child jobs are executed
 * in the same order they were inserted Allow only one notification for
 * selection change and/or document change
 */
@Prototype
public final class BatchJob extends AbstractJob {

    private final List<AbstractJob> subJobs = new ArrayList<>();
    private boolean shouldRefreshSceneGraph;
    private boolean shouldUpdateSelection;
    private String description;
    private final FXOMDocument omDocument;
    private final Selection selection;

    // @formatter:off
    protected BatchJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection) {
    // @formatter:on
        super(extensionFactory);
        this.omDocument = documentManager.fxomDocument().get();
        this.selection = selection;
    }

    protected void setJobParameters(String description, boolean shouldRefreshSceneGraph,
            boolean shouldUpdateSelection) {
        this.description = description == null ? getClass().getSimpleName() : description;
        this.shouldRefreshSceneGraph = shouldRefreshSceneGraph;
        this.shouldUpdateSelection = shouldUpdateSelection;
    }

    public void addSubJob(AbstractJob subJob) {
        assert subJob != null;
        this.subJobs.add(subJob);
    }

    public void addSubJobs(List<AbstractJob> subJobs) {
        assert subJobs != null;
        this.subJobs.addAll(subJobs);
    }

    public void prependSubJob(AbstractJob subJob) {
        assert subJob != null;
        this.subJobs.add(0, subJob);
    }

    public List<AbstractJob> getSubJobs() {
        return Collections.unmodifiableList(subJobs);
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return subJobs.isEmpty() == false;
    }

    @Override
    public void doExecute() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.beginUpdate();
        }
        for (AbstractJob subJob : subJobs) {
            subJob.execute();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public void doUndo() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.beginUpdate();
        }
        for (int i = subJobs.size() - 1; i >= 0; i--) {
            subJobs.get(i).undo();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public void doRedo() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.beginUpdate();
        }
        for (AbstractJob subJob : subJobs) {
            subJob.redo();
        }
        if (shouldRefreshSceneGraph) {
            omDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Singleton
    @Lazy
    public static class Factory extends JobFactory<BatchJob> {
        public Factory(SbContext sbContext) {
            super(sbContext);
        }

        /**
         * Create a {@link BatchJob} job
         *
         * @param description             the job description (or class name if null)
         * @param shouldRefreshSceneGraph if true wrap jobs execution between
         *                                {@link FXOMDocument#beginUpdate()} /
         *                                {@link FXOMDocument#endUpdate()}
         * @param shouldUpdateSelection   if true wrap jobs execution between
         *                                {@link Selection#beginUpdate()} /
         *                                {@link Selection#endUpdate()}
         * @return the job to execute
         */
        public BatchJob getJob(String description, boolean shouldRefreshSceneGraph, boolean shouldUpdateSelection) {
            return create(BatchJob.class,
                    j -> j.setJobParameters(description, shouldRefreshSceneGraph, shouldUpdateSelection));
        }

        /**
         * Create a {@link BatchJob} job
         *
         * @param description             the job description (or class name if null)
         * @param shouldRefreshSceneGraph if true wrap jobs execution between
         *                                {@link FXOMDocument#beginUpdate()} /
         *                                {@link FXOMDocument#endUpdate()}
         * @return the job to execute
         */
        public BatchJob getJob(String description, boolean shouldRefreshSceneGraph) {
            return create(BatchJob.class,
                    j -> j.setJobParameters(description, shouldRefreshSceneGraph, true));
        }

        /**
         * Create a {@link BatchJob} job and notify {@link FXOMDocument} and
         * {@link Selection} updates
         *
         * @param description the job description (or class name if null)
         * @return the job to execute
         */
        public BatchJob getJob(String description) {
            return getJob(description, true, true);
        }

        /**
         * Create a default {@link BatchJob} job and notify {@link FXOMDocument} and
         * {@link Selection} updates
         *
         * @return the job to execute
         */
        public BatchJob getJob() {
            return getJob(null, true, true);
        }
    }
}
