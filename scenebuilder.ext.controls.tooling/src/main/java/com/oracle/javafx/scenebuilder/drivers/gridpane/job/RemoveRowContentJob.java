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
package com.oracle.javafx.scenebuilder.drivers.gridpane.job;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.selection.job.DeleteObjectJob;

import javafx.scene.layout.GridPane;

/**
 * Job invoked when removing row content.
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RemoveRowContentJob extends BatchDocumentJob {

    private final DeleteObjectJob.Factory deleteObjectJobFactory;
    private final GridPaneHierarchyMask.Factory maskFactory;

    private FXOMObject targetGridPane;
    private List<Integer> targetIndexes;

    // @formatter:off
    protected RemoveRowContentJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            DeleteObjectJob.Factory deleteObjectJobFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.deleteObjectJobFactory = deleteObjectJobFactory;
        this.maskFactory = maskFactory;

    }

    protected void setJobParameters(final FXOMObject targetGridPane, final List<Integer> targetIndexes) {
        assert targetGridPane != null;
        assert targetIndexes != null;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = targetIndexes;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        assert targetGridPane instanceof FXOMInstance;
        assert targetIndexes.isEmpty() == false;

        final GridPaneHierarchyMask targetGridPaneMask = maskFactory.getMask(targetGridPane);
        for (int targetIndex : targetIndexes) {
            final List<FXOMObject> children
                    = targetGridPaneMask.getRowContentAtIndex(targetIndex);
            for (FXOMObject child : children) {
                final AbstractJob removeChildJob = deleteObjectJobFactory.getJob(child);
                result.add(removeChildJob);
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return "Remove Row Content"; //NOCHECK
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<RemoveRowContentJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  RemoveRowContentJob} job.
         *
         * @param targetGridPane the target grid pane
         * @param targetIndexes the target indexes
         * @return the job to execute
         */
        public RemoveRowContentJob getJob(final FXOMObject targetGridPane, final List<Integer> targetIndexes) {
            return create(RemoveRowContentJob.class, j -> j.setJobParameters(targetGridPane, targetIndexes));
        }
    }
}