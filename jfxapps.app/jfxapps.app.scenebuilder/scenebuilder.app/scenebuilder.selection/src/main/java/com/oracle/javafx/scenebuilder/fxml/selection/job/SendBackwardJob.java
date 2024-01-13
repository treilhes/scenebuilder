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
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ReIndexObjectJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

/**
 * This job bring the selected objects one step backward in the parent collection (index - 1)
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class SendBackwardJob extends InlineDocumentJob {

    private final Selection selection;
    private final ReIndexObjectJob.Factory reIndexObjectJobFactory;

    // @formatter:off
    protected SendBackwardJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            ReIndexObjectJob.Factory reIndexObjectJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
        this.reIndexObjectJobFactory = reIndexObjectJobFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    public boolean isExecutable() {

        if (selection.getGroup() instanceof ObjectSelectionGroup == false) {
            return false;
        }
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
        for (FXOMObject item : osg.getSortedItems()) {
            final FXOMObject previousSlibing = item.getPreviousSlibing();
            if (previousSlibing == null) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {

        assert isExecutable(); // (1)
        final List<AbstractJob> result = new ArrayList<>();

        assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
        final List<FXOMObject> candidates = osg.getSortedItems();

        for (FXOMObject candidate : candidates) {
            final FXOMObject previousSlibing = candidate.getPreviousSlibing();
            if (previousSlibing != null) {
                final AbstractJob subJob = reIndexObjectJobFactory.getJob(candidate, previousSlibing);
                if (subJob.isExecutable()) {
                    subJob.execute();
                    result.add(subJob);
                }
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        switch (getSubJobs().size()) {
            case 0:
                result = "Unexecutable Send Backward"; // NO18N
                break;
            case 1: // one arrange Z order
                result = getSubJobs().get(0).getDescription();
                break;
            default:
                result = makeMultipleSelectionDescription();
                break;
        }
        return result;
    }

    private String makeMultipleSelectionDescription() {
        final StringBuilder result = new StringBuilder();
        result.append("Send Backward ");
        result.append(getSubJobs().size());
        result.append(" Objects");
        return result.toString();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<SendBackwardJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link SendBackwardJob} job
         * @return the job to execute
         */
        public SendBackwardJob getJob() {
            return create(SendBackwardJob.class, j -> j.setJobParameters());
        }
    }
}
