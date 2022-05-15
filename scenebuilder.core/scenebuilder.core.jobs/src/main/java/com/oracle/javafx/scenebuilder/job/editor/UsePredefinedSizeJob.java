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
package com.oracle.javafx.scenebuilder.job.editor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.preferences.global.RootContainerHeightPreference;
import com.oracle.javafx.scenebuilder.job.preferences.global.RootContainerWidthPreference;

/**
 * Job to use for setting the size of the given FXOMObject; when not provided
 * deal with the top level item of the layout. The job will set the preferred
 * width and height to the given value while min and max width and height are
 * set to Region.USE_PREF_SIZE.
 * No action is taken unless the FXOMObject is an instance of Region or WebView.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class UsePredefinedSizeJob extends AbstractJob {

    private AbstractJob subJob;
    private final UseSizeJob.Factory useSizeJobFactory;
    private final RootContainerHeightPreference rootContainerHeightPreference;
    private final RootContainerWidthPreference rootContainerWidthPreference;

    protected UsePredefinedSizeJob(
            JobExtensionFactory extensionFactory,
            UseSizeJob.Factory useSizeJobFactory,
            RootContainerHeightPreference rootContainerHeightPreference,
            RootContainerWidthPreference rootContainerWidthPreference) {
        super(extensionFactory);
        this.useSizeJobFactory = useSizeJobFactory;
        this.rootContainerHeightPreference = rootContainerHeightPreference;
        this.rootContainerWidthPreference = rootContainerWidthPreference;
    }


    protected void setJobParameters(Size size, FXOMObject fxomObject ) {
        final int width = getWidthFromSize(size);
        final int height = getHeightFromSize(size);
        subJob = useSizeJobFactory.getJob(width, height, fxomObject);
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
        subJob.doRedo();
    }

    @Override
    public String getDescription() {
        return subJob.getDescription();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends JobFactory<UsePredefinedSizeJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link UsePredefinedSizeJob} job
         *
         * @param size the new size
         * @param fxomObject the target {@link FXOMObject}, if null the target is {@link FXOMDocument#getFxomRoot()}
         * @return the job to execute
         */
        public UsePredefinedSizeJob getJob(Size size, FXOMObject fxomObject) {
            return create(UsePredefinedSizeJob.class, j -> j.setJobParameters(size, fxomObject));
        }

        /**
         * Create an {@link UsePredefinedSizeJob} job that target {@link FXOMDocument#getFxomRoot()}
         *
         * @param size the new size
         * @return the job to execute
         */
        public UsePredefinedSizeJob getJob(Size size) {
            return create(UsePredefinedSizeJob.class, j -> j.setJobParameters(size, null));
        }
    }
}
