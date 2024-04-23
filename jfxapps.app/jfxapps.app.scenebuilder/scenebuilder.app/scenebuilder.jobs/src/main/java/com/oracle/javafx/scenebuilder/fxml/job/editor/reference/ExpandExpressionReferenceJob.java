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

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.FxIdCollector;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.fxml.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.fxml.job.editor.atomic.RemovePropertyJob;

/**
 * This job find the reference id contained in the provided {@link FXOMPropertyT}
 * then replace it by cloning the referee using the provided {@link FXOMCloner}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ExpandExpressionReferenceJob extends InlineDocumentJob {

    private FXOMPropertyT reference;
    private FXOMCloner cloner;
    private final FXOMDocument fxomDocument;
    private final RemovePropertyJob.Factory removePropertyJobFactory;
    private final AddPropertyJob.Factory addPropertyJobFactory;

    // @formatter:off
    protected ExpandExpressionReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            RemovePropertyJob.Factory removePropertyJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.removePropertyJobFactory = removePropertyJobFactory;
        this.addPropertyJobFactory = addPropertyJobFactory;
    }

    protected void setJobParameters(FXOMPropertyT reference, FXOMCloner cloner) {
        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;
        assert (cloner == null) || (cloner.getTargetDocument() == fxomDocument);

        this.reference = reference;
        this.cloner = cloner;
    }

    /*
     * InlineDocumentJob
     */
    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {
        final List<AbstractJob> result = new LinkedList<>();

        // 1) remove the reference
        final FXOMElement parentInstance = reference.getParentInstance();
        final AbstractJob removeReference = removePropertyJobFactory.getJob(reference);
        removeReference.execute();
        result.add(removeReference);

        // 2.1) clone the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.collect(FxIdCollector.findFirstById(fxId)).orElse(null);
        final FXOMObject refereeClone = cloner.clone(referee);

        // 3) insert the clone in place of the reference
        final FXOMPropertyC cloneProperty = new FXOMPropertyC(fxomDocument, reference.getName(), refereeClone);
        final AbstractJob addCloneJob = addPropertyJobFactory.getJob(cloneProperty, parentInstance, -1);
        addCloneJob.execute();
        result.add(addCloneJob);

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
    @Lazy
    public final static class Factory extends JobFactory<ExpandExpressionReferenceJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ExpandExpressionReferenceJob} job
         * @return the job to execute
         */
        public ExpandExpressionReferenceJob getJob(FXOMPropertyT reference, FXOMCloner cloner) {
            return create(ExpandExpressionReferenceJob.class, j -> j.setJobParameters(reference, cloner));
        }
    }
}
