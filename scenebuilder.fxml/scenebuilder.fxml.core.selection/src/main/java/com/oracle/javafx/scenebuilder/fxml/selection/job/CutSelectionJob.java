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

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardEncoder;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.InlineSelectionJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.input.Clipboard;

/**
 * Store the selected {@link FXOMObject} objects into {@link Clipboard} and
 * remove them from the current {@link FXOMDocument}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class CutSelectionJob extends InlineSelectionJob {

    private AbstractJob deleteSelectionSubJob;
    private DeleteSelectionJob.Factory deleteSelectionJobFactory;

    // @formatter:off
    protected CutSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            DeleteSelectionJob.Factory deleteSelectionJobFactory) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.deleteSelectionJobFactory = deleteSelectionJobFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    public boolean isExecutable() {
        return getSelection().getGroup() instanceof ObjectSelectionGroup;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        // Selection emptied
        return null;
    }

    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();
        if (isExecutable()) {
            // Update clipboard with current selection BEFORE EXECUTING DELETE job
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) getSelection().getGroup();

            final ClipboardEncoder encoder = new ClipboardEncoder(osg.getSortedItems());
            assert encoder.isEncodable();
            Clipboard.getSystemClipboard().setContent(encoder.makeEncoding());

            deleteSelectionSubJob = deleteSelectionJobFactory.getJob();
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<CutSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link CutSelectionJob} job
         *
         * @return the job to execute
         */
        public CutSelectionJob getJob() {
            return create(CutSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
