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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMCollection;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyC;
import com.gluonhq.jfxapps.core.fxom.FXOMPropertyT;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;

/**
 * Job used to insert new FXOM objects into a sub component location.
 * @deprecated in favor of generic usage of {@link InsertAsAccessoryJob}
 */
@Prototype
@Deprecated(forRemoval = true)
public final class InsertAsSubComponentJob extends BatchSelectionJob {

    private final FxomJobsFactory fxomJobsFactory;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;
    private final FXOMDocument fxomDocument;

    private FXOMObject newObject;
    private FXOMObject targetObject;
    private int targetIndex;

    // @formatter:off
    protected InsertAsSubComponentJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            FXOMObjectMask.Factory designMaskFactory,
            FxomJobsFactory fxomJobsFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.designMaskFactory = designMaskFactory;
        this.fxomJobsFactory = fxomJobsFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters(FXOMObject newObject, FXOMObject targetObject, int targetIndex) {
        assert newObject != null;
        assert targetObject != null;
        assert targetIndex >= -1;
        assert newObject.getFxomDocument() == fxomDocument;
        assert targetObject.getFxomDocument() == fxomDocument;

        this.newObject = newObject;
        this.targetObject = targetObject;
        this.targetIndex = targetIndex;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result;

        final boolean executable;
        if (targetObject instanceof FXOMInstance) {
            final HierarchyMask mask = designMaskFactory.getMask(targetObject);
            executable = mask.isAcceptingSubComponent(newObject);
        } else {
            // TODO(elp): someday we should support insering in FXOMCollection
            executable = false;
        }

        if (executable) {
            final FXOMDocument fxomDocument = targetObject.getFxomDocument();
            final FXOMInstance targetInstance = (FXOMInstance) targetObject;
            final HierarchyMask mask = designMaskFactory.getMask(targetObject);
            final PropertyName subComponentName = mask.getMainAccessory() == null ? null
                    : mask.getMainAccessory().getName();
            assert subComponentName != null;

            /*
             * Two cases:
             *  1) targetObject has no sub component yet
             *      => a new FXOMProperty must created
             *      => newObject must be added to this property using AddPropertyValueJob
             *      => new property must be added to targetObject using AddPropertyJob
             *  2) targetObject has already some sub components
             *      2.1) property is an FXOMPropertyC
             *          => newObject must be inserted amongst the existing values
             *      2.2) property is an empty FXOMPropertyT (see DTL-6206)
             *          => property must be replaced by an FXOMPropertyC
             *          => newObject must be inserted in the FXOMPropertyC
             */

            final FXOMProperty currentProperty = targetInstance.getProperties().get(subComponentName);

            final FXOMPropertyC targetProperty;
            if (currentProperty instanceof FXOMPropertyC) {
                targetProperty = (FXOMPropertyC) currentProperty;
            } else {
                targetProperty = new FXOMPropertyC(fxomDocument, subComponentName);
            }

            result = new ArrayList<>();

            /*
             * RemovePropertyJob
             */
            if (currentProperty instanceof FXOMPropertyT) {
                result.add(fxomJobsFactory.removeProperty(currentProperty));
            }

            /*
             * AddPropertyValueJob
             */
            final Job addValueJob = fxomJobsFactory.addPropertyValue(newObject, targetProperty, targetIndex);
            result.add(addValueJob);

            /*
             * AddPropertyJob
             */
            if (targetProperty.getParentInstance() == null) {
                assert targetObject instanceof FXOMInstance;
                final Job addPropertyJob = fxomJobsFactory.addProperty(targetProperty, targetInstance, -1);
                result.add(addPropertyJob);
            }

            /*
             * PrunePropertiesJob
             */
            final Job pruneJob = fxomJobsFactory.pruneProperties(newObject, targetObject);
            if (pruneJob.isExecutable()) {
                result.add(0, pruneJob);
            }

        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        final StringBuilder sb = new StringBuilder();

        sb.append("Insert ");

        if (newObject instanceof FXOMInstance) {
            final Object sceneGraphObject = newObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                sb.append(sceneGraphObject.getClass().getSimpleName());
            } else {
                sb.append("Unresolved Object");
            }
        } else if (newObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else {
            assert false;
            sb.append(newObject.getClass().getSimpleName());
        }

        return sb.toString();
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        final Set<FXOMObject> newObjects = new HashSet<>();
        newObjects.add(newObject);
        return objectSelectionGroupFactory.getGroup(newObjects, newObject, null);
    }

}
