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
package com.oracle.javafx.scenebuilder.selection.job;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.PrunePropertiesJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

/**
 * Job used to insert new FXOM objects into an accessory location.
 * Insert newObject into accessory of targetObject at index targetindex
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class InsertAsAccessoryJob extends BatchSelectionJob {

    private FXOMObject newObject;
    private FXOMObject targetObject;
    private Accessory accessory;
    private int targetIndex;

    private final FXOMDocument fxomDocument;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final RemovePropertyJob.Factory removePropertyJobFactory;
    private final AddPropertyValueJob.Factory addPropertyValueJobFactory;
    private final AddPropertyJob.Factory addPropertyJobFactory;
    private final PrunePropertiesJob.Factory prunePropertiesJobFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    protected InsertAsAccessoryJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            DesignHierarchyMask.Factory designMaskFactory,
            RemovePropertyJob.Factory removePropertyJobFactory,
            AddPropertyValueJob.Factory addPropertyValueJobFactory,
            AddPropertyJob.Factory addPropertyJobFactory,
            PrunePropertiesJob.Factory prunePropertiesJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory
            ) {
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.designMaskFactory = designMaskFactory;
        this.removePropertyJobFactory = removePropertyJobFactory;
        this.addPropertyValueJobFactory = addPropertyValueJobFactory;
        this.addPropertyJobFactory = addPropertyJobFactory;
        this.prunePropertiesJobFactory = prunePropertiesJobFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters(FXOMObject newObject,FXOMObject targetObject,Accessory accessory,int targetIndex) {
        assert newObject != null;
        assert targetObject != null;
        assert accessory != null;
        assert targetIndex >= -1;
        assert newObject.getFxomDocument() == fxomDocument;
        assert targetObject.getFxomDocument() == fxomDocument;
        assert accessory != null;

        this.newObject = newObject;
        this.targetObject = targetObject;
        this.accessory = accessory;
        this.targetIndex = targetIndex;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        if (accessory == null) {
            return result;
        }

        if (targetObject instanceof FXOMInstance) {

            final HierarchyMask mask = designMaskFactory.getMask(targetObject);

            if (!mask.isAcceptingAccessory(accessory, newObject)) {
                return result;
            }

            final FXOMInstance targetInstance = (FXOMInstance) targetObject;
            final PropertyName accessoryName = mask.getPropertyNameForAccessory(accessory);
            assert accessoryName != null;

            // Property has no value yet because of (1) or is a collection

            if (accessory.isCollection()) {
                /*
                 * If accessory is a collection
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

                final FXOMProperty currentProperty
                    = targetInstance.getProperties().get(accessoryName);

                final FXOMPropertyC targetProperty;
                if (currentProperty instanceof FXOMPropertyC) {
                    targetProperty = (FXOMPropertyC) currentProperty;
                } else {
                    targetProperty = new FXOMPropertyC(fxomDocument, accessoryName);
                }

                /*
                 * RemovePropertyJob
                 */
                if (currentProperty instanceof FXOMPropertyT) {
                    result.add(removePropertyJobFactory.getJob(currentProperty));
                }

                /*
                 * AddPropertyValueJob
                 */
                final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(newObject,targetProperty,targetIndex);
                result.add(addValueJob);

                /*
                 * AddPropertyJob
                 */
                if (targetProperty.getParentInstance() == null) {
                    assert targetObject instanceof FXOMInstance;
                    final AbstractJob addPropertyJob = addPropertyJobFactory.getJob(targetProperty, targetInstance, -1);
                    result.add(addPropertyJob);
                }

                /*
                 * PrunePropertiesJob
                 */
                final AbstractJob pruneJob = prunePropertiesJobFactory.getJob(newObject, targetObject);
                if (pruneJob.isExecutable()) {
                    result.add(0, pruneJob);
                }

            } else {
                /*
                 * If accessory is not a collection
                 *      => a new FXOMProperty must created
                 *      => newObject must be added to this property using AddPropertyValueJob
                 *      => new property must be added to targetObject using AddPropertyJob
                 */
                FXOMProperty targetProperty = new FXOMPropertyC(fxomDocument, accessoryName);

                final AbstractJob addValueJob = addPropertyValueJobFactory.getJob(newObject,(FXOMPropertyC) targetProperty,-1);
                result.add(addValueJob);

                if (targetProperty.getParentInstance() == null) {
                    assert targetObject instanceof FXOMInstance;
                    final AbstractJob addPropertyJob = addPropertyJobFactory.getJob(targetProperty, targetInstance,-1);
                    result.add(addPropertyJob);
                }

                final AbstractJob pruneJob = prunePropertiesJobFactory.getJob(newObject, targetObject);
                if (pruneJob.isExecutable()) {
                    result.add(0, pruneJob);
                }
            }

        }
        return result;
    }

    @Override
    protected String makeDescription() {
        final String result;
        final StringBuilder sb = new StringBuilder();

        sb.append("Insert ");

        if (newObject instanceof FXOMInstance) {
            final Object sceneGraphObject = newObject.getSceneGraphObject();
            if (sceneGraphObject != null) {
                sb.append(sceneGraphObject.getClass().getSimpleName());
            } else {
                sb.append("Unresolved Object");
            }
        } else if (newObject instanceof FXOMIntrinsic) {
            sb.append(((FXOMIntrinsic)newObject).getType());
        } else if (newObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else {
            sb.append(newObject.getClass().getSimpleName());
        }
        result = sb.toString();
        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final Set<FXOMObject> newObjects = new HashSet<>();
        newObjects.add(newObject);
        return objectSelectionGroupFactory.getGroup(newObjects, newObject, null);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public static class Factory extends JobFactory<InsertAsAccessoryJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  InsertAsAccessoryJob} job
         * @param newObject the object to insert
         * @param targetObject the target object
         * @param accessory the target object accesory
         * @param targetIndex the accesory target index
         * @return the job to execute
         */
        public InsertAsAccessoryJob getJob(FXOMObject newObject,FXOMObject targetObject,Accessory accessory,int targetIndex) {
            return create(InsertAsAccessoryJob.class, j -> j.setJobParameters(newObject, targetObject, accessory, targetIndex));
        }

        /**
         * Create an {@link  InsertAsAccessoryJob} job
         * @param newObject the object to insert
         * @param targetObject the target object
         * @param accessory the target object accesory
         * @return the job to execute
         */
        public InsertAsAccessoryJob getJob(FXOMObject newObject,FXOMObject targetObject,Accessory accessory) {
            return create(InsertAsAccessoryJob.class, j -> j.setJobParameters(newObject, targetObject, accessory, -1));
        }
    }
}