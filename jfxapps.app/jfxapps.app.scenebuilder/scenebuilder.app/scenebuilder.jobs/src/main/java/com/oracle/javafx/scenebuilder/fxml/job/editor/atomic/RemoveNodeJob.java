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
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNode;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;

/**
 * This Job updates the FXOM document at execution time.
 * It delete the provided {@link FXOMNode} from its owner<br/>
 * If {@link FXOMObject}, delegates to {@link RemoveObjectJob}<br/>
 * If {@link FXOMProperty}, delegates to {@link RemovePropertyJob}<br/>
 * else bug
 * Use the dedicated {@link Factory} to create an instance
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RemoveNodeJob extends AbstractJob {

    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final RemovePropertyJob.Factory removePropertyJobFactory;

    private AbstractJob subJob;

    public RemoveNodeJob(
            JobExtensionFactory extensionFactory,
            RemoveObjectJob.Factory removeObjectJobFactory,
            RemovePropertyJob.Factory removePropertyJobFactory) {
        super(extensionFactory);
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.removePropertyJobFactory = removePropertyJobFactory;
    }

    protected void setJobParameters(FXOMNode targetNode) {
        assert (targetNode instanceof FXOMObject) || (targetNode instanceof FXOMProperty);

        if (targetNode instanceof FXOMObject) {
            subJob = removeObjectJobFactory.getJob((FXOMObject)targetNode);
        } else {
            assert targetNode instanceof FXOMProperty;
            subJob = removePropertyJobFactory.getJob((FXOMProperty)targetNode);
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
    public static class Factory extends JobFactory<RemoveNodeJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link RemoveNodeJob} job
         * @param targetNode the {@link FXOMNode} to delete
         * @return the job to execute
         */
        public RemoveNodeJob getJob(FXOMNode targetNode) {
            return create(RemoveNodeJob.class, j -> j.setJobParameters(targetNode));
        }
    }
}
