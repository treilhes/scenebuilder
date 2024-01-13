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

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

/**
 * This Job updates the FXOM document at execution time.
 * It removes the provided {@link FXOMObject} from his parent property provided by {@link FXOMObject#getParentProperty()}
 * regardless if the parent property is a collection or not.<br/>
 * If single value property, delegates to {@link RemovePropertyValueJob}<br/>
 * If collection property, delegates to {@link RemoveCollectionItemJob}<br/>
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RemoveObjectJob extends AbstractJob {

    private AbstractJob subJob;
    private RemovePropertyValueJob.Factory removePropertyValueJobFactory;
    private RemoveCollectionItemJob.Factory removeCollectionItemJobFactory;

    protected RemoveObjectJob(
            JobExtensionFactory extensionFactory,
            RemovePropertyValueJob.Factory removePropertyValueJobFactory,
            RemoveCollectionItemJob.Factory removeCollectionItemJobFactory) {
        super(extensionFactory);
        this.removePropertyValueJobFactory = removePropertyValueJobFactory;
        this.removeCollectionItemJobFactory = removeCollectionItemJobFactory;
    }

    protected void setJobParameters(FXOMObject targetObject) {
        assert targetObject != null;
        assert (targetObject.getParentProperty() != null) || (targetObject.getParentCollection() != null);

        if (targetObject.getParentProperty() != null) {
            subJob = removePropertyValueJobFactory.getJob(targetObject);
        } else {
            assert targetObject.getParentCollection() != null;
            subJob = removeCollectionItemJobFactory.getJob(targetObject);
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

    @Override
    public String getDescription() {
        return getClass().getSimpleName(); // Should not reach end user
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<RemoveObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link RemoveObjectJob} job
         * @param targetObject the object to remove
         * @return the job to execute
         */
        public RemoveObjectJob getJob(FXOMObject targetObject) {
            return create(RemoveObjectJob.class, j -> j.setJobParameters(targetObject));
        }
    }
}