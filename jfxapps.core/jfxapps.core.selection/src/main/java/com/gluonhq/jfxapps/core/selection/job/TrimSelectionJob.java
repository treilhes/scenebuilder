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

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.FxomJobsFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.base.BatchSelectionJob;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMFxIdIndex;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.selection.ObjectSelectionGroup;

/**
 * This job replace the current document root by the selected {@link FXOMObject} discarding all ancestors and siblings
 *
 *  This job is composed of subjobs:<br/>
 *      0) Remove fx:controller/fx:root (if defined) from the old root object if any<br/>
 *      1) Unselect the candidate<br/>
 *          => {@link ClearSelectionJob}<br/>
 *      2) Disconnect the candidate from its existing parent<br/>
 *          => {@link DeleteObjectJob}<br/>
 *      3) Set the candidate as the root of the document<br/>
 *          => {@link SetDocumentRootJob}<br/>
 *      4) Add fx:controller/fx:root (if defined) to the new root object<br/>
 */
@Prototype
public final class TrimSelectionJob extends BatchSelectionJob {

    private final FXOMDocument fxomDocument;
    private final FxomJobsFactory fxomJobsFactory;
    private final SelectionJobsFactory selectionJobsFactory;

    // @formatter:off
    protected TrimSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory,
            FxomJobsFactory fxomJobsFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selectionJobsFactory = selectionJobsFactory;
        this.fxomJobsFactory = fxomJobsFactory;
    }

    public void setJobParameters() {
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        if (canTrim()) {

            assert getSelection().getGroup() instanceof ObjectSelectionGroup; // Because (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) getSelection().getGroup();
            assert osg.getItems().size() == 1;
            final FXOMObject oldRoot = fxomDocument.getFxomRoot();
            final FXOMObject candidateRoot = osg.getItems().iterator().next();

            /*
             *  This job is composed of subjobs:
             *      0) Remove fx:controller/fx:root (if defined) from the old root object if any
             *      1) Unselect the candidate
             *          => ClearSelectionJob
             *      2) Disconnect the candidate from its existing parent
             *          => DeleteObjectJob
             *      3) Set the candidate as the root of the document
             *          => SetDocumentRootJob
             *      4) Add fx:controller/fx:root (if defined) to the new root object
             */
            assert oldRoot instanceof FXOMInstance;
            boolean isFxRoot = ((FXOMInstance) oldRoot).isFxRoot();
            final String fxController = oldRoot.getFxController();
            // First remove the fx:controller/fx:root from the old root object
            if (isFxRoot) {
                final Job fxRootJob = fxomJobsFactory.toggleFxRoot();
                result.add(fxRootJob);
            }
            if (fxController != null) {
                final Job fxControllerJob = fxomJobsFactory.modifyFxController(oldRoot, null);
                result.add(fxControllerJob);
            }

            final Job deleteNewRoot = fxomJobsFactory.removeObject(candidateRoot);
            result.add(deleteNewRoot);

            final Job setDocumentRoot = selectionJobsFactory.setDocumentRoot(candidateRoot);
            result.add(setDocumentRoot);

            // Finally add the fx:controller/fx:root to the new root object
            if (isFxRoot) {
                final Job fxRootJob = fxomJobsFactory.toggleFxRoot();
                result.add(fxRootJob);
            }
            if (fxController != null) {
                final Job fxControllerJob = fxomJobsFactory.modifyFxController(candidateRoot, fxController);
                result.add(fxControllerJob);
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return I18N.getString("label.action.edit.trim");
    }

    @Override
    protected SelectionGroup getNewSelectionGroup() {
        // Selection unchanged
        return getOldSelectionGroup();
    }

    private boolean canTrim() {
        final Selection selection = getSelection();
        final boolean result;

        if (selection.getGroup() instanceof ObjectSelectionGroup) {
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();
            if (osg.getItems().size() == 1) {
                // We can trim if:
                //  - object is an FXOMInstance
                //  - object is not already the root
                //  - object is self contained
                final FXOMObject fxomObject = osg.getItems().iterator().next();
                if (fxomObject instanceof FXOMInstance) {
                    final FXOMDocument fxomDocument = fxomObject.getFxomDocument();
                    result = (fxomObject != fxomDocument.getFxomRoot())
                            && FXOMFxIdIndex.isSelfContainedObject(fxomObject);
                } else {
                    result = false;
                }
            } else {
                // Cannot trim when multiple objects are selected
                result = false;
            }
        } else {
            // selection.getGroup() instanceof GridSelectionGroup
            //      => cannot trim a selected row/column in a grid pane
            result = false;
        }

        return result;
    }

}
