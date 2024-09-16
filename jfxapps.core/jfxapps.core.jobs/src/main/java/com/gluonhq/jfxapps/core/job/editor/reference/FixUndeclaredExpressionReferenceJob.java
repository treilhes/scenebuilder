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
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;

import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMCloner;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPath;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.collector.CompositeCollector;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector.ExpressionReference;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector;
import com.gluonhq.jfxapps.core.fxom.collector.FxCollector.FxIdsMap;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;

/**
 * This job look for all reference expression ($someId) in an {@link FXOMDocument} and for each
 * reference r: <br/>
 * if r is a forward reference (the referenced id is declared after the reference itself)<br/>
 * we expand the reference<br/>
 *
 */
//FIXME NB: expanding the reference without alerting the user does not seem to be the right thing to do
@Singleton
public final class FixUndeclaredExpressionReferenceJob extends InlineDocumentJob {

    private final FXOMDocument fxomDocument;
    private final FXOMCloner cloner;

    private final FxomJobsFactory fxomJobsFactory;
    private Map<String, List<FXOMObject>> idMap;
    private List<FXOMPropertyT> expressions;

    // @formatter:off
    public FixUndeclaredExpressionReferenceJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            FxomJobsFactory fxomJobsFactory) {
     // @formatter:on
        super(extensionFactory, documentManager);
        this.fxomJobsFactory = fxomJobsFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        this.cloner = new FXOMCloner(this.fxomDocument);

    }

    /**
     * Set the parameters of the job
     * @param idMap the map of id to object, collected from the document if null
     * @param expressions the list of expression, collected from the document if null
     */
    public void setJobParameters(@Nullable Map<String, List<FXOMObject>> idMap, @Nullable List<FXOMPropertyT> expressions) {
        this.idMap = idMap;
        this.expressions = expressions;
        populateMissingParameters();
    }

    private void populateMissingParameters() {
        // handle if no params are provided, get the needed data from the document
        if (idMap != null || expressions != null) {
            CompositeCollector collector = CompositeCollector.of();

            ExpressionReference ref = null;
            FxIdsMap fxIds = null;

            if (idMap != null) {
                fxIds = FxCollector.fxIdMap();
                collector.add(fxIds);
            }
            if (expressions != null) {
                ref = ExpressionCollector.allExpressionReferences();
                collector.add(ref);
            }
            if (fxomDocument != null && !collector.isEmpty()) {
                fxomDocument.collect(collector);
                if (idMap != null) {
                    idMap = fxIds.getCollected();
                }
                if (expressions != null) {
                    expressions = ref.getCollected();
                }
            }
        }
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {
        final List<Job> executedJobs = new LinkedList<>();

        if (fxomDocument.getFxomRoot() != null) {

            // group expression by id
            Map<String, List<FXOMPropertyT>> groups = expressions.stream()
                    .collect(Collectors.groupingBy(p -> PrefixedValue.of(p.getValue()).getSuffix()));

            for (var fxId : groups.keySet()) {
                if (idMap.containsKey(fxId)) {
                    final var topProperty = FXOMPath.top(groups.get(fxId), p -> p.getParentInstance());
                    final var topObject = FXOMPath.top(idMap.get(fxId));

                    if (topObject.isAfter(FXOMPath.of(topProperty.getParentInstance()))) {
                        // reference expression comes before the object it references
                        // we will switch the reference and the object
                        final var expandJob = fxomJobsFactory.expandExpressionReference(topProperty, this.cloner);
                        expandJob.execute();
                        executedJobs.add(expandJob);
                    }
                } else {
                    // reference expression is not declared
                    // maybe generate a warning
                }
            }
        }

        return executedJobs;
    }

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    protected String makeDescription() {
        return this.getClass().getName();
    }
}
