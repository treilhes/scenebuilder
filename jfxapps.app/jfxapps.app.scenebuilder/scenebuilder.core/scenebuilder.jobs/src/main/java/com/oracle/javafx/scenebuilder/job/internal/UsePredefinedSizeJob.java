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
package com.oracle.javafx.scenebuilder.job.internal;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.job.preference.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.job.preference.RootContainerWidthPreference;

/**
 * Job to use for setting the size of the given FXOMObject; when not provided
 * deal with the top level item of the layout. The job will set the preferred
 * width and height to the given value while min and max width and height are
 * set to Region.USE_PREF_SIZE.
 * No action is taken unless the FXOMObject is an instance of Region or WebView.
 */
@Prototype
public final class UsePredefinedSizeJob extends AbstractJob {

    private Job subJob;
    private final SbJobsFactory sbJobsFactory;
    private final RootContainerHeightPreference rootContainerHeightPreference;
    private final RootContainerWidthPreference rootContainerWidthPreference;

    protected UsePredefinedSizeJob(
            JobExtensionFactory extensionFactory,
            SbJobsFactory sbJobsFactory,
            RootContainerHeightPreference rootContainerHeightPreference,
            RootContainerWidthPreference rootContainerWidthPreference) {
        super(extensionFactory);
        this.sbJobsFactory = sbJobsFactory;
        this.rootContainerHeightPreference = rootContainerHeightPreference;
        this.rootContainerWidthPreference = rootContainerWidthPreference;
    }


    public void setJobParameters(Size size, FXOMObject fxomObject ) {
        final int width = getWidthFromSize(size);
        final int height = getHeightFromSize(size);
        subJob = sbJobsFactory.useSize(width, height, fxomObject);
    }

    private int getWidthFromSize(Size size) {
        assert size != Size.SIZE_PREFERRED;

        if (size == Size.SIZE_DEFAULT) {
            return rootContainerWidthPreference.getValue().intValue();
        }
        return size.getWidth();
    }

    private int getHeightFromSize(Size size) {
        assert size != Size.SIZE_PREFERRED;

        if (size == Size.SIZE_DEFAULT) {
            return rootContainerHeightPreference.getValue().intValue();
        }

        return size.getHeight();
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return subJob.isExecutable();
    }

    @Override
    public void doExecute() {
        subJob.execute();
    }

    @Override
    public void doUndo() {
        subJob.undo();
    }

    @Override
    public void doRedo() {
        subJob.redo();
    }

    @Override
    public String getDescription() {
        return subJob.getDescription();
    }
}
