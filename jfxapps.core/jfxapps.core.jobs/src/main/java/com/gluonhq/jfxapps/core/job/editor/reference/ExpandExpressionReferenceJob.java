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

package com.gluonhq.jfxapps.core.job.editor.reference;

import java.util.LinkedList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;

/**
 * This job find the reference id contained in the provided {@link FXOMPropertyT}
 * then replace it by cloning the referee using the provided {@link FXOMCloner}
 */
@Prototype
public final class ExpandExpressionReferenceJob extends InlineDocumentJob {

    private FXOMPropertyT reference;
    private FXOMCloner cloner;
    private final FXOMDocument fxomDocument;
    private final FxomJobsFactory fxomJobsFactory;

    // @formatter:off
    protected ExpandExpressionReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(FXOMPropertyT reference, FXOMCloner cloner) {
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
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> result = new LinkedList<>();

        // 1) remove the reference
        final FXOMElement parentInstance = reference.getParentInstance();
        final Job removeReference = fxomJobsFactory.removeProperty(reference);
        removeReference.execute();
        result.add(removeReference);

        // 2.1) clone the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.collect(FxCollector.fxIdFindFirst(fxId)).orElse(null);
        final FXOMObject refereeClone = cloner.clone(referee);

        // 3) insert the clone in place of the reference
        final FXOMPropertyC cloneProperty = new FXOMPropertyC(fxomDocument, reference.getName(), refereeClone);
        final Job addCloneJob = fxomJobsFactory.addProperty(cloneProperty, parentInstance, -1);
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
}
