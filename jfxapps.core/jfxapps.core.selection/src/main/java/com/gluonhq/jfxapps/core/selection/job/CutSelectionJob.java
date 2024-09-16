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
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardEncoder;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineSelectionJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

import javafx.scene.input.Clipboard;

/**
 * Store the selected {@link FXOMObject} objects into {@link Clipboard} and
 * remove them from the current {@link FXOMDocument}
 */
@Prototype
public final class CutSelectionJob extends InlineSelectionJob {

    private final SelectionJobsFactory selectionJobsFactory;
    private final ClipboardEncoder clipboardEncoder;
    private Job deleteSelectionSubJob;

    // @formatter:off
    protected CutSelectionJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory,
            ClipboardEncoder clipboardEncoder) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.selectionJobsFactory = selectionJobsFactory;
        this.clipboardEncoder = clipboardEncoder;
    }

    public void setJobParameters() {
    }

    @Override
    public boolean isExecutable() {
        return getSelection().getGroup() instanceof ObjectSelectionGroup;
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        // Selection emptied
        return null;
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {

        final List<Job> result = new ArrayList<>();
        if (isExecutable()) {
            // Update clipboard with current selection BEFORE EXECUTING DELETE job
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) getSelection().getGroup();

            assert clipboardEncoder.isEncodable(osg.getSortedItems());

            Clipboard.getSystemClipboard().setContent(clipboardEncoder.makeEncoding(osg.getSortedItems()));

            deleteSelectionSubJob = selectionJobsFactory.deleteSelection();
            if (deleteSelectionSubJob.isExecutable()) {
                deleteSelectionSubJob.execute();
                result.add(deleteSelectionSubJob);
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        assert deleteSelectionSubJob != null;
        return deleteSelectionSubJob.getDescription();
    }

}
