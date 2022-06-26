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
package com.oracle.javafx.scenebuilder.fxml.selection.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMVirtual;
import com.oracle.javafx.scenebuilder.job.editor.InlineDocumentJob;
import com.oracle.javafx.scenebuilder.job.editor.reference.DeleteRefereeObjectJob;

import javafx.scene.Scene;
import javafx.scene.chart.Axis;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteObjectJob extends InlineDocumentJob {

    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final DeleteRefereeObjectJob.Factory deleteRefereeObjectJobFactory;

    private FXOMObject targetFxomObject;

    protected DeleteObjectJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            DeleteRefereeObjectJob.Factory deleteRefereeObjectJobFactory) {
        super(extensionFactory, documentManager);
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.deleteRefereeObjectJobFactory = deleteRefereeObjectJobFactory;
    }

    protected void setJobParameters(FXOMObject fxomObject) {
        assert fxomObject != null;

        this.targetFxomObject = fxomObject;
    }

    @Override
    public boolean isExecutable() {
        final boolean result;

        if (targetFxomObject == targetFxomObject.getFxomDocument().getFxomRoot()) {
            // targetFxomObject is the root
            result = true;
        } else if (targetFxomObject.getSceneGraphObject() instanceof Axis) {
            // Axis cannot be deleted from their parent Chart
            result = false;
        } else if (targetFxomObject.getParentObject() != null &&
                targetFxomObject.getParentObject().getSceneGraphObject() instanceof Scene) {
            // Scene root cannot be deleted
            result = false;
        } else {
            result = (targetFxomObject.getParentProperty() != null);
        }

        return result;
    }

    @Override
    protected List<AbstractJob> makeAndExecuteSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();
        if ((targetFxomObject.getParentProperty() == null) &&
            (targetFxomObject.getParentCollection() == null)) {
            /*
             * targetFxomObject is the root object
             * => we reset the root object to null
             */
            final AbstractJob setRootJob = setDocumentRootJobFactory.getJob(null);
            setRootJob.execute();
            result.add(setRootJob);

        } else {

            /*
             * targetFxomObject is not the root object
             * => we delegate to ObjectDeleter
             * => this class will take care of references
             */

            // TODO functional change : to check
            final DeleteRefereeObjectJob deleteRefereeObjectJob = deleteRefereeObjectJobFactory.getJob(targetFxomObject);
            deleteRefereeObjectJob.execute();
            result.add(deleteRefereeObjectJob);
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Delete ");

        if (targetFxomObject instanceof FXOMInstance) {
            final Object sceneGraphObject = targetFxomObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                sb.append(sceneGraphObject.getClass().getSimpleName());
            } else {
                sb.append("Unresolved Object");
            }
        } else if (targetFxomObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else if (targetFxomObject instanceof FXOMIntrinsic || targetFxomObject instanceof FXOMVirtual) {
            sb.append(targetFxomObject.getGlueElement().getTagName());
        } else {
            assert false;
            sb.append(targetFxomObject.getClass().getSimpleName());
        }

        return sb.toString();
    }

    FXOMObject getTargetFxomObject() {
        return targetFxomObject;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends JobFactory<DeleteObjectJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link DeleteObjectJob} job
         * @param fxomObject the object to delete
         * @return the job to execute
         */
        public DeleteObjectJob getJob(FXOMObject fxomObject) {
            return create(DeleteObjectJob.class, j -> j.setJobParameters(fxomObject));
        }
    }
}
