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
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.job.editor.PrunePropertiesJob;
import com.oracle.javafx.scenebuilder.job.editor.UsePredefinedSizeJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.SetFxomRootJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

/**
 * This Job updates the FXOM document at execution time.
 * It set the root of a document {@link FXOMDocument} with the provided {@link FXOMObject}<br/>
 * The provided {@link FXOMObject} is cleaned from obsolete properties {@link FXOMProperty}<br/>
 * and resized according user preferences.<br/>
 * Subjob {@link PrunePropertiesJob}<br/>
 * Subjob {@link SetFxomRootJob}<br/>
 * Subjob {@link UsePredefinedSizeJob}<br/>
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class SetDocumentRootJob extends BatchSelectionJob {

    private FXOMObject newRoot;
    private boolean usePredefinedSize;
    private String description;

    private final FXOMDocument fxomDocument;
    private final PrunePropertiesJob.Factory prunePropertiesJobFactory;
    private final SetFxomRootJob.Factory setFxomRootJobFactory;
    private final UsePredefinedSizeJob.Factory usePredefinedSizeJobFactory;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    protected SetDocumentRootJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            PrunePropertiesJob.Factory prunePropertiesJobFactory,
            SetFxomRootJob.Factory setFxomRootJobFactory,
            UsePredefinedSizeJob.Factory usePredefinedSizeJobFactory,
            DesignHierarchyMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;

        this.prunePropertiesJobFactory = prunePropertiesJobFactory;
        this.setFxomRootJobFactory = setFxomRootJobFactory;
        this.usePredefinedSizeJobFactory = usePredefinedSizeJobFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters(FXOMObject newRoot, boolean usePredefinedSize, String description) {
        assert (newRoot == null) || (newRoot.getFxomDocument() == fxomDocument);
        assert description != null;

        this.newRoot = newRoot;
        this.usePredefinedSize = usePredefinedSize;
        this.description = description == null ? this.getClass().getName() : description;
    }

    public FXOMObject getNewRoot() {
        return newRoot;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();
        if (newRoot != fxomDocument.getFxomRoot()) {
            // Before setting newRoot as the root of the fxom document,
            // we must remove its static properties.
            // We create a RemovePropertyJob for each existing static property
            if (newRoot != null) {
                result.add(prunePropertiesJobFactory.getJob(newRoot, null));
            }

            // Adds job that effectively modifes the root
            result.add(setFxomRootJobFactory.getJob(newRoot));

            // If need, we add a job for resizing the root object
            if ((newRoot != null) && usePredefinedSize) {
                final HierarchyMask mask = designMaskFactory.getMask(newRoot);
                if (mask.needResizeWhenTopElement()) {
                    result.add(usePredefinedSizeJobFactory.getJob(Size.SIZE_DEFAULT, newRoot));
                }
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return description;
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        if (newRoot == null) {
            return null;
        }
        List<FXOMObject> newObjects = new ArrayList<>();
        newObjects.add(newRoot);
        return objectSelectionGroupFactory.getGroup(newObjects, newRoot, null);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<SetDocumentRootJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link SetDocumentRootJob} job
         * @param newRoot the {@link FXOMObject} menat to be the new root of the current document
         * @param usePredefinedSize if true, newRoot will be resized according user predefined size
         * @param description the job description
         * @return the job to execute
         */
        public SetDocumentRootJob getJob(FXOMObject newRoot, boolean usePredefinedSize, String description) {
            return create(SetDocumentRootJob.class, j -> j.setJobParameters(newRoot, usePredefinedSize, description));
        }

        /**
         * Create an {@link SetDocumentRootJob} job.<br/>
         * With default description (class name) and usePredefinedSize = false
         * @param newRoot the {@link FXOMObject} menat to be the new root of the current document
         * @return the job to execute
         */
        public SetDocumentRootJob getJob(FXOMObject newRoot) {
            return create(SetDocumentRootJob.class, j -> j.setJobParameters(newRoot, false, null));
        }
    }
}
