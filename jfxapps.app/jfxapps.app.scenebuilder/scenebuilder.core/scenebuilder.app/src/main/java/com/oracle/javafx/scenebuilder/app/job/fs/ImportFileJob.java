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
package com.oracle.javafx.scenebuilder.app.job.fs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

/**
 * Load the provided fxml {@link File}, if the current scoped document is empty
 * (root object is null), <br/>
 * then we insert the new object(s) as root.<br/>
 * Otherwise, we insert the new object under the common parent of the selected
 * objects.
 */
@Prototype
public final class ImportFileJob extends BatchSelectionJob {

    private final FXOMDocument fxomDocument;
    private final I18N i18n;
    private final SelectionJobsFactory selectionJobsFactory;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    private File file;
    private FXOMObject newObject, targetObject;

    // @formatter:off
    protected ImportFileJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            FxomJobsFactory fxomJobsFactory,
            SelectionJobsFactory selectionJobsFactory,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            FXOMObjectMask.Factory designMaskFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.i18n = i18n;
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selectionJobsFactory = selectionJobsFactory;
        this.designMaskFactory = designMaskFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters(File file) {
        assert file != null;
        this.file = file;
    }

    public FXOMObject getTargetObject() {
        return targetObject;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        final FXOMDocument targetDocument = fxomDocument;

        try {
            newObject = FXOMNodes.newObject(targetDocument, file);

            // newObject is null when file is empty
            if (newObject != null) {
                // If the document is empty (root object is null), then we
                // insert the new object as root.
                // Otherwise, we insert the new object under the common parent
                // of the selected objects.
                final FXOMObject rootObject = targetDocument.getFxomRoot();

                if (rootObject == null) {
                    result.add(selectionJobsFactory.setDocumentRoot(newObject));
                } else {
                    final Selection selection = getSelection();
                    if (selection.isEmpty() || selection.isSelected(rootObject)) {
                        // No selection or root is selected -> we insert below root
                        targetObject = rootObject;
                    } else {
                        // Let's use the common parent of the selected objects.
                        // It might be null if selection holds some non FXOMObject entries
                        targetObject = selection.getAncestor();
                    }
                    // Build InsertAsSubComponent jobs
                    final var targetMask = designMaskFactory.getMask(targetObject);
                    if (targetMask.isAcceptingSubComponent(newObject)) {
                        result.add(selectionJobsFactory.insertAsSubComponent(newObject, targetObject,
                                targetMask.getSubComponentCount(true)));
                    }
                }
            }
        } catch (IOException ex) {
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return i18n.getString("import.from.file", file.getName());
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        final List<FXOMObject> fxomObjects = new ArrayList<>();
        fxomObjects.add(newObject);
        return objectSelectionGroupFactory.getGroup(fxomObjects, newObject, null);
    }

    @ApplicationInstanceSingleton
    public static class Factory extends JobFactory<ImportFileJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link ImportFileJob} job
         *
         * @param file the file to import
         * @return the job to execute
         */
        public ImportFileJob getJob(File file) {
            return create(ImportFileJob.class, j -> j.setJobParameters(file));
        }
    }
}
