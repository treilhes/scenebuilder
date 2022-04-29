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
package com.oracle.javafx.scenebuilder.job.editor.atomic;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMVirtual;

/**
 * Add an {@link FXOMObject} before 'beforeObject' into 'beforeObject''s parent
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class ReIndexObjectJob extends AbstractJob {

    private FXOMObject reindexedObject;
    private FXOMObject beforeObject;
    private FXOMObject oldBeforeObject;
    private String description;
    private FXOMDocument fxomDocument;

    protected ReIndexObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    protected void setJobParameters(FXOMObject reindexedObject, FXOMObject beforeObject) {
        assert reindexedObject != null;

        this.reindexedObject = reindexedObject;
        this.beforeObject = beforeObject;
        this.oldBeforeObject = reindexedObject.getNextSlibing();
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return (beforeObject != oldBeforeObject);
    }

    @Override
    public void doExecute() {
        doRedo();
    }

    @Override
    public void doUndo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        reindexedObject.moveBeforeSibling(oldBeforeObject);
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        reindexedObject.moveBeforeSibling(beforeObject);
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        if (description == null) {
            final StringBuilder sb = new StringBuilder();

            sb.append("Move ");

            if (reindexedObject instanceof FXOMInstance) {
                final Object sceneGraphObject = reindexedObject.getSceneGraphObject();
                if (sceneGraphObject != null) {
                    sb.append(sceneGraphObject.getClass().getSimpleName());
                } else {
                    sb.append("Unresolved Object");
                }
            } else if (reindexedObject instanceof FXOMCollection) {
                sb.append("Collection");
            } else if (reindexedObject instanceof FXOMVirtual) {
                sb.append(reindexedObject.getClass().getSimpleName());
            } else {
                assert false;
                sb.append(reindexedObject.getClass().getSimpleName());
            }
            description = sb.toString();
        }
        return description;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends JobFactory<ReIndexObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ReIndexObjectJob} job.
         *
         * @param reindexedObject the reindexed object
         * @param beforeObject the object before which the reindexed object will be inserted
         * @return the job to execute
         */
        public ReIndexObjectJob getJob(FXOMObject reindexedObject, FXOMObject beforeObject) {
            return create(ReIndexObjectJob.class, j -> j.setJobParameters(reindexedObject, beforeObject));
        }

    }
}
