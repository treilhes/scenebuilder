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

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMProperty} from his parent instance provided by {@link FXOMProperty#getParentInstance()}
 * Use the dedicated {@link Factory} to create an instance
 */
@Prototype
public final class RemovePropertyJob extends AbstractJob {

    private FXOMProperty targetProperty;

    private FXOMElement parentInstance;
    private int indexInParentInstance;

    private FXOMDocument fxomDocument;

    private final FxomJobsFactory fxomJobsFactory;

    protected RemovePropertyJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory);
        this.fxomJobsFactory = fxomJobsFactory;
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    public void setJobParameters(FXOMProperty targetProperty) {
        assert targetProperty != null;
        this.targetProperty = targetProperty;

        setDescription(String.format("%s[%s]", getClass().getSimpleName(), targetProperty.getName())); // NOCHECK
    }

    public FXOMProperty getTargetProperty() {
        return targetProperty;
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return targetProperty.getParentInstance() != null;
    }

    @Override
    public void doExecute() {
        assert parentInstance == null;
        assert isExecutable();

        parentInstance = targetProperty.getParentInstance();
        indexInParentInstance = targetProperty.getIndexInParentInstance();
        redo();
    }

    @Override
    public void doUndo() {
        assert targetProperty.getParentInstance() == null;

        fxomDocument.beginUpdate();
        targetProperty.addToParentInstance(indexInParentInstance, parentInstance);
        fxomDocument.endUpdate();

        assert targetProperty.getParentInstance() == parentInstance;
        assert targetProperty.getIndexInParentInstance() == indexInParentInstance;
    }

    @Override
    public void doRedo() {
        assert targetProperty.getParentInstance() == parentInstance;
        assert targetProperty.getIndexInParentInstance() == indexInParentInstance;

        fxomDocument.beginUpdate();
        targetProperty.removeFromParentInstance();
        fxomDocument.endUpdate();

        assert targetProperty.getParentInstance() == null;
    }
}
