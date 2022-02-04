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
package com.oracle.javafx.scenebuilder.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.UseComputedSizesObjectJob;

/**
 * This job apply the constant USE_COMPUTED_SIZE on width and height on the
 * currently selected objects
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UseComputedSizesSelectionJob extends BatchDocumentJob {

    private static Logger logger = LoggerFactory.getLogger(UseComputedSizesSelectionJob.class);

    private final Selection selection;
    private final UseComputedSizesObjectJob.Factory useComputedSizesObjectJobFactory;

    // @formatter:off
    protected UseComputedSizesSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            UseComputedSizesObjectJob.Factory useComputedSizesObjectJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.selection = selection;
        this.useComputedSizesObjectJobFactory = useComputedSizesObjectJobFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        final Set<FXOMInstance> candidates = new HashSet<>();

        if (selection.getGroup() != null) {
            for (FXOMObject fxomObject : selection.getGroup().getItems()) {
                if (fxomObject instanceof FXOMInstance) {
                    candidates.add((FXOMInstance) fxomObject);
                }
            }
        } else {
            logger.warn("selection.getGroup() is null");
        }

        for (FXOMInstance candidate : candidates) {
            final AbstractJob subJob = useComputedSizesObjectJobFactory.getJob(candidate);
            if (subJob.isExecutable()) {
                result.add(subJob);
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        switch (getSubJobs().size()) {
        case 0:
            result = "Unexecutable Use Computed Sizes"; // NO18N
            break;
        case 1:
            result = getSubJobs().get(0).getDescription();
            break;
        default:
            result = makeMultipleSelectionDescription();
            break;
        }
        return result;
    }

    private String makeMultipleSelectionDescription() {
        final StringBuilder result = new StringBuilder();
        result.append("Use Computed Sizes on ");
        result.append(getSubJobs().size());
        result.append(" Objects");
        return result.toString();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<UseComputedSizesSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UseComputedSizesSelectionJob} job
         *
         * @return the job to execute
         */
        public UseComputedSizesSelectionJob getJob() {
            return create(UseComputedSizesSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
