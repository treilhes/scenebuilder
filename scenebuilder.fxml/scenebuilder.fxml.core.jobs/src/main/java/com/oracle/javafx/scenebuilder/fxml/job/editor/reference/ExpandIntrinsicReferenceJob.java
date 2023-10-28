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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCloner;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.collector.FxIdCollector;
import com.oracle.javafx.scenebuilder.fxml.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.ReplaceObjectJob;

/**
 * This job find the reference id contained in source attribute of an {@link FXOMIntrinsic}
 * then replace it by cloning the referee using the provided {@link FXOMCloner}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ExpandIntrinsicReferenceJob extends InlineDocumentJob {

    private FXOMIntrinsic reference;
    private FXOMCloner cloner;
    private final FXOMDocument fxomDocument;
    private final ReplaceObjectJob.Factory replaceObjectJobFactory;

    // @formatter:off
    protected ExpandIntrinsicReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            ReplaceObjectJob.Factory replaceObjectJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.replaceObjectJobFactory = replaceObjectJobFactory;
    }

    protected void setJobParameters(FXOMIntrinsic reference, FXOMCloner cloner) {
        assert reference != null;
        assert cloner != null;
        assert reference.getFxomDocument() == fxomDocument;
        assert cloner.getTargetDocument() == fxomDocument;

        this.reference = reference;
        this.cloner = cloner;
    }

    /*
     * InlineDocumentJob
     */
    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> result = new LinkedList<>();

        // 1) clone the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);
        final FXOMObject refereeClone = cloner.clone(referee);

        // 2) replace the reference by the referee clone
        final AbstractJob replaceJob = replaceObjectJobFactory.getJob(reference, refereeClone);
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
        return ((reference.getType() == FXOMIntrinsic.Type.FX_COPY) ||
                (reference.getType() == FXOMIntrinsic.Type.FX_REFERENCE)) &&
               ((reference.getParentProperty() != null) ||
                (reference.getParentCollection() != null));
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<ExpandIntrinsicReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ExpandIntrinsicReferenceJob} job.
         *
         * @param reference the {@link FXOMIntrinsic} reference
         * @param cloner the cloner
         * @return the job to execute
         */
        public ExpandIntrinsicReferenceJob getJob(FXOMIntrinsic reference, FXOMCloner cloner) {
            return create(ExpandIntrinsicReferenceJob.class, j -> j.setJobParameters(reference, cloner));
        }
    }

}
