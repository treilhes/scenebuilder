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

import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNode;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.job.internal.FitToParentObjectJob;
import com.oracle.javafx.scenebuilder.job.internal.RelocateSelectionJob;
import com.oracle.javafx.scenebuilder.job.internal.UseComputedSizesObjectJob;
import com.oracle.javafx.scenebuilder.job.internal.UsePredefinedSizeJob;
import com.oracle.javafx.scenebuilder.job.internal.UseSizeJob;
import com.oracle.javafx.scenebuilder.job.internal.atomic.RelocateNodeJob;
import com.oracle.javafx.scenebuilder.job.internal.reference.FixToggleGroupExpressionReferenceJob;
import com.oracle.javafx.scenebuilder.job.internal.reference.FixToggleGroupIntrinsicReferenceJob;
import com.oracle.javafx.scenebuilder.job.internal.reference.FixToggleGroupReferenceJob;

import javafx.geometry.Point2D;

public class SbJobsFactoryImpl extends JobFactory<Job> implements SbJobsFactory {
    public SbJobsFactoryImpl(JfxAppContext context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link FitToParentObjectJob} job
     */
    @Override
    public Job fitToParentObject(FXOMInstance fxomInstance) {
        return create(FitToParentObjectJob.class, j -> j.setJobParameters(fxomInstance));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link RelocateSelectionJob} job.
     */
    @Override
    public Job relocateSelection(Map<FXOMObject, Point2D> locationMap) {
        return create(RelocateSelectionJob.class, j -> j.setJobParameters(locationMap));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UseComputedSizesObjectJob} job
     */
    @Override
    public Job useComputedSizesObject(FXOMInstance fxomInstance) {
        return create(UseComputedSizesObjectJob.class, j -> j.setJobParameters(fxomInstance));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UsePredefinedSizeJob} job
     */
    @Override
    public Job usePredefinedSize(Size size, FXOMObject fxomObject) {
        return create(UsePredefinedSizeJob.class, j -> j.setJobParameters(size, fxomObject));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UsePredefinedSizeJob} job that target
     * {@link FXOMDocument#getFxomRoot()}
     */
    @Override
    public Job usePredefinedSize(Size size) {
        return create(UsePredefinedSizeJob.class, j -> j.setJobParameters(size, null));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UseSizeJob} job
     */
    @Override
    public Job useSize(double width, double height, FXOMObject fxomObject) {
        return create(UseSizeJob.class, j -> j.setJobParameters(width, height, fxomObject));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link UseSizeJob} job that target
     * {@link FXOMDocument#getFxomRoot()}
     */
    @Override
    public Job useSize(double width, double height) {
        return create(UseSizeJob.class, j -> j.setJobParameters(width, height, null));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link RelocateNodeJob} job.
     */
    @Override
    public Job relocateNode(FXOMInstance fxomInstance, double newLayoutX, double newLayoutY) {
        return create(RelocateNodeJob.class, j -> j.setJobParameters(fxomInstance, newLayoutX, newLayoutY));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link FixToggleGroupExpressionReferenceJob} job.
     */
    @Override
    public Job fixToggleGroupExpressionReference(FXOMPropertyT reference) {
        return create(FixToggleGroupExpressionReferenceJob.class, j -> j.setJobParameters(reference));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link FixToggleGroupIntrinsicReferenceJob} job.
     */
    @Override
    public Job fixToggleGroupIntrinsicReference(FXOMIntrinsic reference) {
        return create(FixToggleGroupIntrinsicReferenceJob.class, j -> j.setJobParameters(reference));
    }

    /**
     * {@inheritDoc}
     *
     * Create an {@link FixToggleGroupReferenceJob} job.
     */
    @Override
    public Job fixToggleGroupReference(FXOMNode reference) {
        return create(FixToggleGroupReferenceJob.class, j -> j.setJobParameters(reference));
    }
}
