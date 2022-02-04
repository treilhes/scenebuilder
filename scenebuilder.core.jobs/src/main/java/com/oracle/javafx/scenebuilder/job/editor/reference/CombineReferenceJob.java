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

package com.oracle.javafx.scenebuilder.job.editor.reference;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;

/**
 * This Job updates the FXOM document at execution time.
 * It replace the reference id from the {@link FXOMNode} by the original referee {@link FXOMObject}<br/>
 * The referee is moved from his original location
 * If {@link FXOMIntrinsic}, delegates to {@link CombineIntrinsicReferenceJob}<br/>
 * If {@link FXOMPropertyT}, delegates to {@link CombineExpressionReferenceJob}<br/>
 * else bug
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class CombineReferenceJob  extends AbstractJob {

    private AbstractJob subJob;
    private CombineIntrinsicReferenceJob.Factory combineIntrinsicReferenceJobFactory;
    private CombineExpressionReferenceJob.Factory combineExpressionReferenceJobFactory;

    protected CombineReferenceJob(
            JobExtensionFactory extensionFactory,
            CombineIntrinsicReferenceJob.Factory combineIntrinsicReferenceJobFactory,
            CombineExpressionReferenceJob.Factory combineExpressionReferenceJobFactory
            ) {
        super(extensionFactory);
        this.combineIntrinsicReferenceJobFactory = combineIntrinsicReferenceJobFactory;
        this.combineExpressionReferenceJobFactory = combineExpressionReferenceJobFactory;
    }

    protected void setJobParameters(FXOMNode reference) {
        if (reference instanceof FXOMIntrinsic) {
            final FXOMIntrinsic fxomIntrinsic = (FXOMIntrinsic) reference;
            subJob = combineIntrinsicReferenceJobFactory.getJob(fxomIntrinsic);
        } else if (reference instanceof FXOMPropertyT) {
            final FXOMPropertyT fxomProperty = (FXOMPropertyT) reference;
            subJob = combineExpressionReferenceJobFactory.getJob(fxomProperty);
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
    public static class Factory extends JobFactory<CombineReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link CombineReferenceJob} job
         * @param reference
         * @return the job to execute
         */
        public CombineReferenceJob getJob(FXOMNode reference) {
            return create(CombineReferenceJob.class, j -> j.setJobParameters(reference));
        }
    }

}
