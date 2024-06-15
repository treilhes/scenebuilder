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
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMObject} from his parent property provided by {@link FXOMObject#getParentProperty()}
 * regardless if the parent property is a collection or not.<br/>
 * If single value property, delegates to {@link RemovePropertyValueJob}<br/>
 * If collection property, delegates to {@link RemoveCollectionItemJob}<br/>
 * Use the dedicated {@link Factory} to create an instance
 */
@Prototype
public final class RemoveObjectJob extends AbstractJob {

    private final FxomJobsFactory fxomJobsFactory;
    private Job subJob;


    protected RemoveObjectJob(
            JobExtensionFactory extensionFactory,
            FxomJobsFactory fxomJobsFactory) {
        super(extensionFactory);
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters(FXOMObject targetObject) {
        assert targetObject != null;
        assert (targetObject.getParentProperty() != null) || (targetObject.getParentCollection() != null);

        if (targetObject.getParentProperty() != null) {
            subJob = fxomJobsFactory.removePropertyValue(targetObject);
        } else {
            assert targetObject.getParentCollection() != null;
            subJob = fxomJobsFactory.removeCollectionItem(targetObject);
        }
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
}
