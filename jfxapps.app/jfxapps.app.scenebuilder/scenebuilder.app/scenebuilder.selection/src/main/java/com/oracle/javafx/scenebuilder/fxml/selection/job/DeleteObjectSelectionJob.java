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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

/**
 * Delete all object in the current selection for ObjectSelectionGroup.
 * If one object can't be deleted then none are
 * Delegate to {@link DeleteObjectJob}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteObjectSelectionJob extends BatchSelectionJob {

    private final DeleteObjectJob.Factory deleteObjectJobFactory;

    // @formatter:off
    protected DeleteObjectSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            DeleteObjectJob.Factory deleteObjectJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.deleteObjectJobFactory = deleteObjectJobFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final Selection selection = getSelection();
        assert selection.getGroup() instanceof ObjectSelectionGroup;
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
        final List<AbstractJob> result = new ArrayList<>();

        // Next we make one DeleteObjectJob for each selected objects
        int cannotDeleteCount = 0;
        for (FXOMObject candidate : osg.getFlattenItems()) {
            final AbstractJob subJob = deleteObjectJobFactory.getJob(candidate);

            if (subJob.isExecutable()) {
                result.add(subJob);
            } else {
                cannotDeleteCount++;
            }
        }

        // If some objects cannot be deleted, then we clear all to
        // make this job not executable.
        if (cannotDeleteCount >= 1) {
            result.clear();
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        final int subJobCount = getSubJobs().size();

        switch (subJobCount) {
            case 0:
                result = "Unexecutable Delete"; // NO18N
                break;
            case 1: // one delete
                result = getSubJobs().get(0).getDescription();
                break;
            default:
                result = I18N.getString("label.action.edit.delete.n", subJobCount);
                break;
        }

        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        // Selection emptied
        return null;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<DeleteObjectSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  DeleteObjectSelectionJob} job
         * @return the job to execute
         */
        public DeleteObjectSelectionJob getJob() {
            return create(DeleteObjectSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
