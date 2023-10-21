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

package com.oracle.javafx.scenebuilder.fxml.job.editor.reference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

/**
 * This job look for all reference in an {@link FXOMDocument} and update them with the referee content
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UpdateReferencesJob extends AbstractJob {

    private AbstractJob subJob;
    private final List<AbstractJob> fixJobs = new ArrayList<>();
    private final FXOMDocument fxomDocument;
    private final ReferencesUpdaterJob.Factory referencesUpdaterJobFactory;

 // @formatter:off
    protected UpdateReferencesJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            ReferencesUpdaterJob.Factory referencesUpdaterJobFactory) {
    // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.referencesUpdaterJobFactory = referencesUpdaterJobFactory;
    }

    protected void setJobParameters(AbstractJob subJob) {
        this.subJob = subJob;
    }

    public AbstractJob getSubJob() {
        return subJob;
    }

    public List<AbstractJob> getFixJobs() {
        return Collections.unmodifiableList(fixJobs);
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return subJob.isExecutable();
    }

    @Override
    public void doExecute() {
        fxomDocument.beginUpdate();

        // First executes the subjob => references may become valid
        subJob.execute();

        // Now sorts the reference in the document and archives the sorting jobs
        final ReferencesUpdaterJob updater = referencesUpdaterJobFactory.getJob();
        updater.execute();
        fixJobs.add(updater);

        fxomDocument.endUpdate();
    }

    @Override
    public void doUndo() {
        fxomDocument.beginUpdate();
        for (int i = fixJobs.size() - 1; i >= 0; i--) {
            fixJobs.get(i).undo();
        }
        subJob.undo();
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        fxomDocument.beginUpdate();
        subJob.redo();
        for (AbstractJob fixJob : fixJobs) {
            fixJob.redo();
        }
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return subJob.getDescription();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<UpdateReferencesJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UpdateReferencesJob} job.
         *
         * @param subJob the sub job
         * @return the job to execute
         */
        public UpdateReferencesJob getJob(AbstractJob subJob) {
            return create(UpdateReferencesJob.class, j -> j.setJobParameters(subJob));
        }
    }
}
