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
package com.gluonhq.jfxapps.core.api.job.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

/**
 * Allow to group multiple jobs into one job logic unit. Child jobs are executed
 * in the same order they were inserted Allow only one notification for
 * selection change and/or document change
 */
@Prototype
public final class BatchJob extends AbstractJob {

    private boolean shouldRefreshSceneGraph;
    private boolean shouldUpdateSelection;
    private final FXOMDocument fxomDocument;
    private final Selection selection;

    // @formatter:off
    public BatchJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection) {
    // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = selection;
    }

    protected void setJobParameters(boolean shouldRefreshSceneGraph,
            boolean shouldUpdateSelection) {
        this.shouldRefreshSceneGraph = shouldRefreshSceneGraph;
        this.shouldUpdateSelection = shouldUpdateSelection;
    }



    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return getSubJobs().isEmpty() == false;
    }

    @Override
    public void doExecute() {
        if (shouldUpdateSelection) {
            selection.beginUpdate();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.beginUpdate();
        }
        for (Job subJob : getSubJobs()) {
            subJob.execute();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
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
            fxomDocument.beginUpdate();
        }

        final var subJobs = getSubJobs();
        for (int i = subJobs.size() - 1; i >= 0; i--) {
            subJobs.get(i).undo();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
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
            fxomDocument.beginUpdate();
        }

        final var subJobs = getSubJobs();
        for (Job subJob : subJobs) {
            subJob.redo();
        }
        if (shouldRefreshSceneGraph) {
            fxomDocument.endUpdate();
        }
        if (shouldUpdateSelection) {
            selection.endUpdate();
        }
    }

    @Singleton
    @Lazy
    public static class Factory extends JobFactory<BatchJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        /**
         * Create a {@link BatchJob} job
         *
         * @param shouldRefreshSceneGraph if true wrap jobs execution between
         *                                {@link FXOMDocument#beginUpdate()} /
         *                                {@link FXOMDocument#endUpdate()}
         * @param shouldUpdateSelection   if true wrap jobs execution between
         *                                {@link Selection#beginUpdate()} /
         *                                {@link Selection#endUpdate()}
         * @return the job to execute
         */
        public BatchJob getJob(boolean shouldRefreshSceneGraph, boolean shouldUpdateSelection) {
            return create(BatchJob.class,
                    j -> j.setJobParameters(shouldRefreshSceneGraph, shouldUpdateSelection));
        }

        /**
         * Create a {@link BatchJob} job
         *
         * @param shouldRefreshSceneGraph if true wrap jobs execution between
         *                                {@link FXOMDocument#beginUpdate()} /
         *                                {@link FXOMDocument#endUpdate()}
         * @return the job to execute
         */
        public BatchJob getJob(boolean shouldRefreshSceneGraph) {
            return create(BatchJob.class,
                    j -> j.setJobParameters(shouldRefreshSceneGraph, true));
        }

        /**
         * Create a {@link BatchJob} job and notify {@link FXOMDocument} and
         * {@link Selection} updates
         *
         * @return the job to execute
         */
        public BatchJob getJob() {
            return getJob(true, true);
        }

    }
}
