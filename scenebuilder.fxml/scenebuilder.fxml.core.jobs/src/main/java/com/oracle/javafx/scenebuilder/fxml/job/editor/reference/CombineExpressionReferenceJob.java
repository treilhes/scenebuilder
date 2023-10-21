/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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

import java.util.LinkedList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.collector.FxIdCollector;
import com.oracle.javafx.scenebuilder.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemoveObjectJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.ReplacePropertyValueJobT;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;

/**
 * This Job updates the FXOM document at execution time.
 * Replace the reference id from the {@link FXOMPropertyT} by the original referee {@link FXOMObject}<br/>
 * The referee is moved from his original location
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class CombineExpressionReferenceJob extends InlineDocumentJob {

    private FXOMPropertyT reference;
    private final FXOMDocument fxomDocument;
    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final ReplacePropertyValueJobT.Factory replacePropertyValueJobTFactory;

    protected CombineExpressionReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            RemoveObjectJob.Factory removeObjectJobFactory,
            ReplacePropertyValueJobT.Factory replacePropertyValueJobTFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.replacePropertyValueJobTFactory = replacePropertyValueJobTFactory;
    }

    protected void setJobParameters(FXOMPropertyT reference) {
        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;
        this.reference = reference;
    }
    /*
     * InlineDocumentJob
     */
    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> result = new LinkedList<>();

        // 1) Locate the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);

        // 2) Remove the referee
        final AbstractJob removeJob = removeObjectJobFactory.getJob(referee);
        removeJob.execute();
        result.add(removeJob);

        // 3) Replace ther reference by the referee
        final AbstractJob replaceJob = replacePropertyValueJobTFactory.getJob(reference, referee);
        replaceJob.execute();
        result.add(replaceJob);

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName(); // Not expected to reach the user
    }

    @Override
    public boolean isExecutable() {
        final PrefixedValue pv = new PrefixedValue(reference.getValue());
        return pv.isExpression();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<CombineExpressionReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link CombineExpressionReferenceJob} job
         * @param reference the property containing the reference expression
         * @return the job to execute
         */
        public CombineExpressionReferenceJob getJob(FXOMPropertyT reference) {
            return create(CombineExpressionReferenceJob.class, j -> j.setJobParameters(reference));
        }
    }
}
