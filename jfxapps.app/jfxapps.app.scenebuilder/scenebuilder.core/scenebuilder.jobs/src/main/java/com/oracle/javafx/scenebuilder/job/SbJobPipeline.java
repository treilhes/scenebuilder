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
package com.oracle.javafx.scenebuilder.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobPipeline;
import com.gluonhq.jfxapps.core.api.job.base.BatchJob;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPath;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector;
import com.gluonhq.jfxapps.core.fxom.collector.ExpressionCollector.UndeclaredExpressionReference;
import com.gluonhq.jfxapps.core.fxom.collector.FXOMCollector;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.util.ToggleGroupHelper;

@ApplicationInstanceSingleton
public class SbJobPipeline implements JobPipeline {

    private UndeclaredExpressionReference undefinedReferenceCollector = ExpressionCollector
            .allUndeclaredExpressionReferences();

    private final BatchJob.Factory batchJobFactory;
    private final SbJobsFactory sbJobsFactory;

    public SbJobPipeline(BatchJob.Factory batchJobFactory, SbJobsFactory sbJobsFactory) {
        super();
        this.sbJobsFactory = sbJobsFactory;
        this.batchJobFactory = batchJobFactory;
    }

    @Override
    public Map<String, FXOMCollector<?>> preExecutionCollectors() {
        return Map.of();
    }

    @Override
    public Map<String, FXOMCollector<?>> postExecutionCollectors() {
        return Map.of(undefinedReferenceCollector.toString(), undefinedReferenceCollector);
    }

    @Override
    public Job preExecutionJob(Map<String, List<FXOMObject>> preIdMap) {
        return null;
    }

    /**
     * {@inheritDoc}
     * <br/><br/>
     * <b>Specific:</b><br/>
     * Look for undefined toggleGroup references and create a new ToggleGroup
     * instance if the referee does not exist or switch the reference and the
     * referee if the referee exists. If more thn one reference exist for the same
     * referee only the first reference in the order of declaration in the current
     * document will be switched/created.
     */
    @Override
    public Job postExecutionJob(Map<String, List<FXOMObject>> postIdMap) {
        final var batchJob = batchJobFactory.getJob();
        final var undefined = undefinedReferenceCollector.getCollected();

        final List<String> processedIds = new ArrayList<>();
        final List<FXOMNode> nodes = new ArrayList<>(undefined.keySet());
        FXOMPath.sort(nodes);

        for (var refNode : nodes) {
            String refId = undefined.get(refNode);
            if (!processedIds.contains(refId) && ToggleGroupHelper.isToggleGroupReference(refNode)) {
                final var fixJob = sbJobsFactory.fixToggleGroupReference(refNode);
                fixJob.execute();
                batchJob.addSubJob(fixJob);
            }
        }

        return batchJob;
    }

}
