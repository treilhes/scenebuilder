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
package com.oracle.javafx.scenebuilder.selection.job;

import java.io.File;

import com.oracle.javafx.scenebuilder.api.selection.SbSelectionJobsFactory;
import com.oracle.javafx.scenebuilder.selection.extension.SetDocumentRootJob;
import com.treilhes.emc4j.boot.api.context.EmContext;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobFactory;
import com.treilhes.jfxplace.core.fxom.FXOMObject;

@ApplicationInstanceSingleton
public class SbSelectionJobsFactoryImpl extends JobFactory<Job> implements SbSelectionJobsFactory {
    public SbSelectionJobsFactoryImpl(EmContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link FitToParentSelectionJob} job
     */
    @Override
    public Job fitToParentSelection() {
        return create(FitToParentSelectionJob.class, j -> j.setJobParameters());
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UseComputedSizesSelectionJob} job
     */
    @Override
    public Job useComputedSizesSelection() {
        return create(UseComputedSizesSelectionJob.class, j -> j.setJobParameters());
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Job setDocumentRoot(FXOMObject newRoot, boolean usePredefinedSize) {
        return create(SetDocumentRootJob.class, j -> j.setJobParameters(newRoot, usePredefinedSize));
    }

    @Override
    public Job importFile(File file) {
        return create(ImportFileJob.class, j -> j.setJobParameters(file));
    }

    @Override
    public Job includeFile(File file) {
        return create(IncludeFileJob.class, j -> j.setJobParameters(file));
    }
}
