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
package com.oracle.javafx.scenebuilder.selection.extension;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.FXOMProperty;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.mask.SbFXOMObjectMask;


/**
 * This Job updates the FXOM document at execution time.
 * It set the root of a document {@link FXOMDocument} with the provided {@link FXOMObject}<br/>
 * The provided {@link FXOMObject} is cleaned from obsolete properties {@link FXOMProperty}<br/>
 * and resized according user preferences.<br/>
 * Subjob {@link PrunePropertiesJob}<br/>
 * Subjob {@link SetFxomRootJob}<br/>
 * Subjob {@link UsePredefinedSizeJob}<br/>
 */
@Prototype
public final class SetDocumentRootJob extends BatchSelectionJob {

    private FXOMObject newRoot;
    private boolean usePredefinedSize;
    private String description;

    private final FXOMDocument fxomDocument;
    private final FxomJobsFactory fxomJobsFactory;
    private final SbJobsFactory sbJobsFactory;
    private final SbFXOMObjectMask.Factory sbFXOMObjectMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    protected SetDocumentRootJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            FxomJobsFactory fxomJobsFactory,
            SbJobsFactory sbJobsFactory,
            SbFXOMObjectMask.Factory sbFXOMObjectMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;

        this.fxomJobsFactory = fxomJobsFactory;
        this.sbJobsFactory = sbJobsFactory;
        this.sbFXOMObjectMaskFactory = sbFXOMObjectMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    public void setJobParameters(FXOMObject newRoot, boolean usePredefinedSize, String description) {
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
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();
        if (newRoot != fxomDocument.getFxomRoot()) {
            // Before setting newRoot as the root of the fxom document,
            // we must remove its static properties.
            // We create a RemovePropertyJob for each existing static property
            if (newRoot != null) {
                result.add(fxomJobsFactory.pruneProperties(newRoot, null));
            }

            // Adds job that effectively modifes the root
            result.add(fxomJobsFactory.setFxomRoot(newRoot));

            // If need, we add a job for resizing the root object
            if ((newRoot != null) && usePredefinedSize) {
                final var mask = sbFXOMObjectMaskFactory.getMask(newRoot);
                if (mask.needResizeWhenTopElement()) {
                    result.add(sbJobsFactory.usePredefinedSize(Size.SIZE_DEFAULT, newRoot));
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
    protected SelectionGroup getNewSelectionGroup() {
        if (newRoot == null) {
            return null;
        }
        List<FXOMObject> newObjects = new ArrayList<>();
        newObjects.add(newRoot);
        return objectSelectionGroupFactory.getGroup(newObjects, newRoot, null);
    }

}
