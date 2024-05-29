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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

/**
 * Find a reference in the provided {@link FXOMNode} then replace it by cloning the referee using the provided {@link FXOMCloner}
 * For {@link FXOMIntrinsic} delegates to {@link ExpandIntrinsicReferenceJob}
 * For {@link FXOMPropertyT} delegates to {@link ExpandExpressionReferenceJob}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ExpandReferenceJob  extends AbstractJob {

    private AbstractJob subJob;
    private final ExpandIntrinsicReferenceJob.Factory expandIntrinsicReferenceJobFactory;
    private final ExpandExpressionReferenceJob.Factory expandExpressionReferenceJobFactory;

 // @formatter:off
    protected ExpandReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            ExpandIntrinsicReferenceJob.Factory expandIntrinsicReferenceJobFactory,
            ExpandExpressionReferenceJob.Factory expandExpressionReferenceJobFactory) {
    // @formatter:on
        super(extensionFactory);
        this.expandIntrinsicReferenceJobFactory = expandIntrinsicReferenceJobFactory;
        this.expandExpressionReferenceJobFactory = expandExpressionReferenceJobFactory;
    }

    protected void setJobParameters(FXOMNode reference, FXOMCloner cloner) {
        if (reference instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) reference;
            subJob = expandIntrinsicReferenceJobFactory.getJob(fxomIntrinsic, cloner);
        } else if (reference instanceof FXOMPropertyT) {
            final FXOMPropertyT fxomProperty = (FXOMPropertyT) reference;
            subJob = expandExpressionReferenceJobFactory.getJob(fxomProperty, cloner);
        } else {
            throw new RuntimeException("Bug"); //NOCHECK
        }
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<ExpandReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ExpandReferenceJob} job.
         *
         * @param reference the {@link FXOMNode} containing the reference
         * @param cloner the cloner
         * @return the job to execute
         */
        public ExpandReferenceJob getJob(FXOMNode reference, FXOMCloner cloner) {
            return create(ExpandReferenceJob.class, j -> j.setJobParameters(reference, cloner));
        }
    }

}
