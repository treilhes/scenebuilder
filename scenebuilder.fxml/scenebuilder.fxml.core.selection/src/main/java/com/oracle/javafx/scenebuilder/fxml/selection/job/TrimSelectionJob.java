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
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMFxIdIndex;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyFxControllerJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.RemoveObjectJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ToggleFxRootJob;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.ClearSelectionJob;

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
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class TrimSelectionJob extends BatchSelectionJob {

    private final FXOMDocument fxomDocument;
    private final ToggleFxRootJob.Factory toggleFxRootJobFactory;
    private final ModifyFxControllerJob.Factory modifyFxControllerJobFactory;
    private final RemoveObjectJob.Factory removeObjectJobFactory;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;

    // @formatter:off
    protected TrimSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager<FXOMDocument> documentManager,
            Selection selection,
            ToggleFxRootJob.Factory toggleFxRootJobFactory,
            ModifyFxControllerJob.Factory modifyFxControllerJobFactory,
            RemoveObjectJob.Factory removeObjectJobFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.toggleFxRootJobFactory = toggleFxRootJobFactory;
        this.modifyFxControllerJobFactory = modifyFxControllerJobFactory;
        this.removeObjectJobFactory = removeObjectJobFactory;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

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
                final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                result.add(fxRootJob);
            }
            if (fxController != null) {
                final AbstractJob fxControllerJob = modifyFxControllerJobFactory.getJob(oldRoot, null);
                result.add(fxControllerJob);
            }

            final AbstractJob deleteNewRoot = removeObjectJobFactory.getJob(candidateRoot);
            result.add(deleteNewRoot);

            final AbstractJob setDocumentRoot = setDocumentRootJobFactory.getJob(candidateRoot);
            result.add(setDocumentRoot);

            // Finally add the fx:controller/fx:root to the new root object
            if (isFxRoot) {
                final AbstractJob fxRootJob = toggleFxRootJobFactory.getJob();
                result.add(fxRootJob);
            }
            if (fxController != null) {
                final AbstractJob fxControllerJob = modifyFxControllerJobFactory.getJob(candidateRoot, fxController);
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
    protected AbstractSelectionGroup getNewSelectionGroup() {
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<TrimSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link TrimSelectionJob} job
         * @return the job to execute
         */
        public TrimSelectionJob getJob() {
            return create(TrimSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
