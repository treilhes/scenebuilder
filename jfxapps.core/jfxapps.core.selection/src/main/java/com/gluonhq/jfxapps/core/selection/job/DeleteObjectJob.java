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
package com.gluonhq.jfxapps.core.selection.job;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.InlineDocumentJob;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMVirtual;

import javafx.scene.Scene;
import javafx.scene.chart.Axis;

/**
 *
 */
@Prototype
//FIXME This class prevents the deletion of Axis and Scene from their parent object
public final class DeleteObjectJob extends InlineDocumentJob {

    private final FxomJobsFactory fxomJobsFactory;
    private final SelectionJobsFactory selectionJobsFactory;

    private FXOMObject targetFxomObject;

    protected DeleteObjectJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory) {
        super(extensionFactory, documentManager);
        this.fxomJobsFactory = fxomJobsFactory;
        this.selectionJobsFactory = selectionJobsFactory;
    }

    public void setJobParameters(FXOMObject fxomObject) {
        assert fxomObject != null;

        this.targetFxomObject = fxomObject;
    }

    @Override
    public boolean isExecutable() {

        final boolean result;

        if (targetFxomObject == targetFxomObject.getFxomDocument().getFxomRoot()) {
            // targetFxomObject is the root
            result = true;
        } else if (targetFxomObject.getSceneGraphObject().isInstanceOf(Axis.class)) {
            // Axis cannot be deleted from their parent Chart
            result = false;
        } else if (targetFxomObject.hasParent() &&
                targetFxomObject.getParentObject().getSceneGraphObject().isInstanceOf(Scene.class)) {
            // Scene root cannot be deleted
            result = false;
        } else {
            result = (targetFxomObject.getParentProperty() != null);
        }

        return result;
    }

    @Override
    protected List<Job> makeAndExecuteSubJobs() {

        final List<Job> result = new ArrayList<>();
        if ((targetFxomObject.getParentProperty() == null) &&
            (targetFxomObject.getParentCollection() == null)) {
            /*
             * targetFxomObject is the root object
             * => we reset the root object to null
             */
            final Job setRootJob = selectionJobsFactory.setDocumentRoot(null);
            setRootJob.execute();
            result.add(setRootJob);

        } else {

            /*
             * targetFxomObject is not the root object
             * => we delegate to ObjectDeleter
             * => this class will take care of references
             */

            // TODO functional change : to check
            final Job deleteRefereeObjectJob = fxomJobsFactory.deleteRefereeObject(targetFxomObject);
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
            final Object sceneGraphObject = targetFxomObject.getSceneGraphObject().get();
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

}
