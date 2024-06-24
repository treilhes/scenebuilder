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
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;

/**
 * This Job updates the FXOM document at execution time.
 * Replace the reference id from the {@link FXOMIntrinsic} by the original referee {@link FXOMObject}<br/>
 * The referee is moved from his original location
 */
@Prototype
public final class CombineIntrinsicReferenceJob extends InlineDocumentJob {

    private FXOMIntrinsic reference;
    private final FXOMDocument fxomDocument;
    private final FxomJobsFactory fxomJobsFactory;

    protected CombineIntrinsicReferenceJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory, documentManager);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(FXOMIntrinsic reference) {
        assert reference != null;
        assert reference.getFxomDocument() == fxomDocument;
        this.reference = reference;
    }
    /*
     * InlineDocumentJob
     */
    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> result = new LinkedList<>();

        // 1) Locate the referee
        final String fxId = FXOMNodes.extractReferenceSource(reference);
        final FXOMObject referee = fxomDocument.collect(FxCollector.fxIdFindFirst(fxId)).orElse(null);

        // 2) Remove the referee
        // FIXME i think removing the referee is not a valid move if the reference is an fx:copy, a test/check must be done
        final Job removeJob = fxomJobsFactory.removeObject(referee);
        removeJob.execute();
        result.add(removeJob);

        // 3) Replace ther reference by the referee
        final Job replaceJob = fxomJobsFactory.replaceObject(reference, referee);
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
}
