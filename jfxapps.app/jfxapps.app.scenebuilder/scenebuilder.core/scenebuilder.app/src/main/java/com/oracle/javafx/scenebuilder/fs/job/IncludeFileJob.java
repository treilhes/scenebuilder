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
package com.oracle.javafx.scenebuilder.fs.job;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMIntrinsic;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.util.URLUtils;

/**
 * Cannot include in non saved document<br/>
 * Cannot include same file as document one which will create cyclic reference<br/>
 * Cannot include as root <br/>
 * Link the provided fxml {@link File} using an fx:include<br/>
 * We insert the new object under the common parent of the selected objects or under root if nothing is selected
 */
@ApplicationInstancePrototype
public final class IncludeFileJob extends BatchSelectionJob {

    private final I18N i18n;
    private final FXOMDocument fxomDocument;
    private final SelectionJobsFactory selectionJobsFactory;
    private final FXOMObjectMask.Factory designMaskFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    private File file;
    private FXOMObject targetObject;
    private FXOMIntrinsic newInclude;

 // @formatter:off
    protected IncludeFileJob(
            I18N i18n,
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory,
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

        try {
            final FXOMDocument targetDocument = fxomDocument;
            final URL documentURL = fxomDocument.getLocation();
            final URL fileURL = file.toURI().toURL();

            // Cannot include in non saved document
            // Cannot include same file as document one which will create cyclic reference
            if (documentURL != null && URLUtils.equals(documentURL, fileURL) == false) {
                newInclude = FXOMNodes.newInclude(targetDocument, file);

                // newInclude is null when file is empty
                if (newInclude != null) {

                    // Cannot include as root
                    final FXOMObject rootObject = targetDocument.getFxomRoot();
                    if (rootObject != null) {
                        // We include the new object under the common parent
                        // of the selected objects.
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
                        final HierarchyMask targetMask = designMaskFactory.getMask(targetObject);
                        if (targetMask.isAcceptingSubComponent(newInclude)) {
                            result.add(selectionJobsFactory.insertAsSubComponent(newInclude,targetObject,targetMask.getSubComponentCount(true)));
                        }
                    }
                }
            }
        } catch (IOException ex) {
            result.clear();
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return i18n.getString("include.file", file.getName());
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        final List<FXOMObject> fxomObjects = new ArrayList<>();
        fxomObjects.add(newInclude);
        return objectSelectionGroupFactory.getGroup(fxomObjects, newInclude, null);
    }

    @ApplicationInstanceSingleton
    public static class Factory extends JobFactory<IncludeFileJob> {
        public Factory(JfxAppContext sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link IncludeFileJob} job
         *
         * @param file the file to include
         * @return the job to execute
         */
        public IncludeFileJob getJob(File file) {
            return create(IncludeFileJob.class, j -> j.setJobParameters(file));
        }
    }
}
