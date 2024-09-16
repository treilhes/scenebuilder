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
package com.gluonhq.jfxapps.core.selection.job;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 * Delete all object in the current selection for ObjectSelectionGroup.
 * If one object can't be deleted then none are
 * Delegate to {@link DeleteObjectJob}
 */
@Prototype
public final class DeleteObjectSelectionJob extends BatchSelectionJob {

    private static final String I18N_LABEL_ACTION_EDIT_DELETE_N = "label.action.edit.delete.n";

    private final SelectionJobsFactory selectionJobsFactory;

    private final I18N i18n;

    // @formatter:off
    protected DeleteObjectSelectionJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.i18n = i18n;
        this.selectionJobsFactory = selectionJobsFactory;
    }

    public void setJobParameters() {
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();
        final Selection selection = getSelection();
        assert selection.getGroup() instanceof ObjectSelectionGroup;

        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();

        // Next we make one DeleteObjectJob for each selected objects
        int cannotDeleteCount = 0;
        for (FXOMObject candidate : osg.getFlattenItems()) {
            final Job subJob = selectionJobsFactory.deleteObject(candidate);

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
                result = i18n.getString(I18N_LABEL_ACTION_EDIT_DELETE_N, subJobCount);
                break;
        }

        return result;
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        // Selection emptied
        return null;
    }

}
