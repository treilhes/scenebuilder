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
package com.oracle.javafx.scenebuilder.fxml.selection.job;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.editor.selection.SelectionGroupAccessor;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.Job;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

/**
 * This job manages the deletion of the objects contained in either an
 * ObjectSelectionGroup or a GridSelectionGroup depending on the selection.<br/>
 * For {@link ObjectSelectionGroup} delegates to {@link DeleteObjectSelectionJob}<br/>
 * For {@link GridSelectionGroup} delegates to {@link DeleteGridSelectionJob}<br/>
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteSelectionJob extends AbstractJob {

    private Job subJob;
    private Selection selection;
    private SelectionGroupAccessor selectionGroupAccessor;
//    private DeleteObjectSelectionJob.Factory deleteObjectSelectionJobFactory;
//    private DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory;

    // @formatter:off
    protected DeleteSelectionJob(
            JobExtensionFactory extensionFactory,
            Selection selection
//            ,DeleteObjectSelectionJob.Factory deleteObjectSelectionJobFactory,
//            DeleteGridSelectionJob.Factory deleteGridSelectionJobFactory
            ) {
    // @formatter:on
        super(extensionFactory);
        this.selection = selection;
        this.selectionGroupAccessor = new SelectionGroupAccessor(selection.getGroup());

//        this.deleteObjectSelectionJobFactory = deleteObjectSelectionJobFactory;
//        this.deleteGridSelectionJobFactory = deleteGridSelectionJobFactory;
    }

    protected void setJobParameters() {
        buildSubJobs();
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return subJob != null && subJob.isExecutable();
    }

    @Override
    public void doExecute() {
        subJob.execute();
    }

    @Override
    public void doUndo() {
        subJob.undo();
    }

    @Override
    public void doRedo() {
        subJob.redo();
    }

    @Override
    public String getDescription() {
        return subJob.getDescription();
    }

    Job getSubJob() {
        return subJob;
    }

    /*
     * Private
     */
    private void buildSubJobs() {
        subJob = selectionGroupAccessor.makeDeleteJob();
        if (subJob == null) {
            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup();
        }
//        if (selection.getGroup() instanceof ObjectSelectionGroup) {
//            subJob = deleteObjectSelectionJobFactory.getJob();
//        } else if (selection.getGroup() instanceof GridSelectionGroup) {
//            subJob = deleteGridSelectionJobFactory.getJob();
//        } else {
//            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup();
//        }
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<DeleteSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link DeleteSelectionJob} job
         *
         * @return the job to execute
         */
        public DeleteSelectionJob getJob() {
            return create(DeleteSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
