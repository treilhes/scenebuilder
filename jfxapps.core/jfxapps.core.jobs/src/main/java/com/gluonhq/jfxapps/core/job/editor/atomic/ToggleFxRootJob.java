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
package com.gluonhq.jfxapps.core.job.editor.atomic;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;

/**
 * Job used to enable/disable fx:root on the fxom document associated
 * to the editor controller (if any).
 */
@Prototype
public final class ToggleFxRootJob extends AbstractJob {

    private static final String I18N_JOB_TOGGLE_FX_ROOT = "job.toggle.fx.root";

    private final FXOMDocument fxomDocument;

    private final I18N i18n;

    // @formatter:off
    protected ToggleFxRootJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager) {
    // @formatter:on
        super(extensionFactory);
        this.i18n = i18n;
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters() {
        setDescription(i18n.getString(I18N_JOB_TOGGLE_FX_ROOT));
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
}
