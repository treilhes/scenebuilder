/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.job.editor.atomic;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 * This Job updates the FXOM document at execution time.
 * It set the root of a document {@link FXOMDocument} with the provided {@link FXOMObject}
 */
@Prototype
public final class SetFxomRootJob extends AbstractJob {

    private FXOMObject newRoot;
    private FXOMObject oldRoot;

    private final FXOMDocument fxomDocument;

    protected SetFxomRootJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
    }

    protected void setJobParameters(FXOMObject newRoot) {
        assert (newRoot == null) || (newRoot.getFxomDocument() == fxomDocument);

        this.newRoot = newRoot;
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return newRoot != fxomDocument.getFxomRoot();
    }

    @Override
    public void doExecute() {
        assert oldRoot == null;

        // Saves the current root
        oldRoot = fxomDocument.getFxomRoot();

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(newRoot);
        fxomDocument.endUpdate();

        // TODO ensure the new framework show this alter
        //WarnThemeAlert.showAlertIfRequired(getEditorController(), newRoot, getEditorController().getOwnerWindow());
    }

    @Override
    public void doUndo() {
        assert fxomDocument.getFxomRoot() == newRoot;

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(oldRoot);
        fxomDocument.endUpdate();

        assert fxomDocument.getFxomRoot() == oldRoot;
    }

    @Override
    public void doRedo() {
        assert fxomDocument.getFxomRoot() == oldRoot;

        fxomDocument.beginUpdate();
        fxomDocument.setFxomRoot(newRoot);
        fxomDocument.endUpdate();

        assert fxomDocument.getFxomRoot() == newRoot;
    }

    @Override
    public String getDescription() {
        // Not expected to reach the user
        return getClass().getSimpleName();
    }

	public FXOMObject getNewRoot() {
		return newRoot;
	}

	public FXOMObject getOldRoot() {
		return oldRoot;
	}

	@Singleton
    public static class Factory extends JobFactory<SetFxomRootJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link SetFxomRootJob} job
         * @param newRoot the new root of current {@link FXOMDocument}
         * @return the job to execute
         */
        public SetFxomRootJob getJob(FXOMObject newRoot) {
            return create(SetFxomRootJob.class, j -> j.setJobParameters(newRoot));
        }
    }
}
