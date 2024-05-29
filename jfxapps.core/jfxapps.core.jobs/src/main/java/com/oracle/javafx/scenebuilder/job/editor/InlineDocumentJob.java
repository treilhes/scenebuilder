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

import java.util.Collections;
import java.util.List;

import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.CompositeJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

/**
 * This Job updates the FXOM document at execution time. The selection is not
 * updated.
 *
 * Each sub job is created AND executed before processing the next one.
 */
public abstract class InlineDocumentJob extends CompositeJob {

    private List<AbstractJob> subJobs;
    private final FXOMDocument omDocument;

    protected InlineDocumentJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.omDocument = documentManager.fxomDocument().get();
    }

    @Override
    public final List<AbstractJob> getSubJobs() {
        return subJobs;
    }

    @Override
    public void doExecute() {
        omDocument.beginUpdate();
        subJobs = Collections.unmodifiableList(makeAndExecuteSubJobs());
        omDocument.endUpdate();
    }

    @Override
    public void doUndo() {
        omDocument.beginUpdate();
        for (int i = getSubJobs().size() - 1; i >= 0; i--) {
            getSubJobs().get(i).undo();
        }
        omDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        omDocument.beginUpdate();
        for (AbstractJob subJob : getSubJobs()) {
            subJob.redo();
        }
        omDocument.endUpdate();
    }

    protected abstract List<AbstractJob> makeAndExecuteSubJobs();
}
