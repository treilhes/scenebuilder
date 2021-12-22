/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMCollection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyC;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMPropertyT;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.AddPropertyValueJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemovePropertyJob;

/**
 * Job used to insert new FXOM objects into an accessory location.
 *
 */
public class InsertAsAccessoryJob extends BatchSelectionJob {

    private final FXOMObject newObject;
    private final FXOMObject targetObject;
    private Accessory accessory;
    private final int targetIndex;
    private final FXOMDocument fxomDocument;

    public InsertAsAccessoryJob(SceneBuilderBeanFactory context,
            FXOMObject newObject,
            FXOMObject targetObject,
            Accessory accessory,
            Editor editor) {
        this(context, newObject, targetObject, accessory, -1, editor);
    }
    public InsertAsAccessoryJob(SceneBuilderBeanFactory context,
            FXOMObject newObject,
            FXOMObject targetObject,
            Accessory accessory,
            int targetIndex,
            Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();

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
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (accessory == null) {
            return result;
        }

        if (targetObject instanceof FXOMInstance) {

            final DesignHierarchyMask mask = new DesignHierarchyMask(targetObject);

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
                    result.add(new RemovePropertyJob(getContext(), currentProperty, getEditorController()).extend());
                }

                /*
                 * AddPropertyValueJob
                 */
                final Job addValueJob
                        = new AddPropertyValueJob(getContext(), newObject,
                                targetProperty,
                                targetIndex,
                                getEditorController()).extend();
                result.add(addValueJob);

                /*
                 * AddPropertyJob
                 */
                if (targetProperty.getParentInstance() == null) {
                    assert targetObject instanceof FXOMInstance;
                    final Job addPropertyJob
                            = new AddPropertyJob(getContext(), targetProperty, targetInstance,
                            -1, getEditorController()).extend();
                    result.add(addPropertyJob);
                }

                /*
                 * PrunePropertiesJob
                 */
                final Job pruneJob = new PrunePropertiesJob(getContext(), newObject, targetObject,
                        getEditorController()).extend();
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

                final Job addValueJob
                        = new AddPropertyValueJob(getContext(), newObject,
                                (FXOMPropertyC) targetProperty,
                                -1,
                                getEditorController()).extend();
                result.add(addValueJob);

                if (targetProperty.getParentInstance() == null) {
                    assert targetObject instanceof FXOMInstance;
                    final Job addPropertyJob
                            = new AddPropertyJob(getContext(), targetProperty, targetInstance,
                                    -1, getEditorController()).extend();
                    result.add(addPropertyJob);
                }

                final Job pruneJob = new PrunePropertiesJob(getContext(), newObject, targetObject,
                        getEditorController()).extend();
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
        } else if (newObject instanceof FXOMCollection) {
            sb.append("Collection");
        } else {
            assert false;
            sb.append(newObject.getClass().getSimpleName());
        }
        result = sb.toString();
        return result;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final Set<FXOMObject> newObjects = new HashSet<>();
        newObjects.add(newObject);
        return new ObjectSelectionGroup(newObjects, newObject, null);
    }
}
