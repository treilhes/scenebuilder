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
package com.oracle.javafx.scenebuilder.fxml.job.editor.atomic;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;

/**
 * Job used to enable/disable fx:root on the fxom document associated
 * to the editor controller (if any).
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ToggleFxRootJob extends AbstractJob {

    private final FXOMDocument fxomDocument;

    // @formatter:off
    protected ToggleFxRootJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager) {
    // @formatter:on
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    protected void setJobParameters() {

    }
    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return (fxomDocument != null) && (fxomDocument.getFxomRoot() instanceof FXOMInstance);
    }

    @Override
    public void doExecute() {
        doRedo();
    }

    @Override
    public void doUndo() {
        doRedo();
    }

    @Override
    public void doRedo() {
        assert fxomDocument != null;
        assert fxomDocument.getFxomRoot() instanceof FXOMInstance;

        final FXOMInstance rootInstance = (FXOMInstance) fxomDocument.getFxomRoot();
        rootInstance.toggleFxRoot();
    }

    @Override
    public String getDescription() {
        return I18N.getString("job.toggle.fx.root");
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends JobFactory<ToggleFxRootJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ToggleFxRootJob} job.
         * @return the job to execute
         */
        public ToggleFxRootJob getJob() {
            return create(ToggleFxRootJob.class, j -> j.setJobParameters());
        }

    }
}