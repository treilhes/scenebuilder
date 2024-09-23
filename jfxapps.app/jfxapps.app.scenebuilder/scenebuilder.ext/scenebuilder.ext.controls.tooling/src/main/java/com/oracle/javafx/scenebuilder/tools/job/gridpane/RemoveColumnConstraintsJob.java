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
package com.oracle.javafx.scenebuilder.tools.job.gridpane;

import java.util.ArrayList;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.ApplicationInstanceEvents;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.selection.job.DeleteObjectJob;
import com.oracle.javafx.scenebuilder.tools.mask.GridPaneHierarchyMask;

/**
 * Job invoked when removing column constraints.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class RemoveColumnConstraintsJob extends BatchDocumentJob {

    private FXOMObject targetGridPane;
    private List<Integer> targetIndexes;
    private final GridPaneHierarchyMask.Factory maskFactory;
    private final DeleteObjectJob.Factory deleteObjectJobFactory;

    protected RemoveColumnConstraintsJob(
            JobExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            DeleteObjectJob.Factory deleteObjectJobFactory,
            GridPaneHierarchyMask.Factory maskFactory) {
        super(extensionFactory, documentManager);
        this.maskFactory = maskFactory;
        this.deleteObjectJobFactory = deleteObjectJobFactory;

    }

    protected void setJobParameters(FXOMObject targetGridPane, List<Integer> targetIndexes) {
        assert targetGridPane != null;
        assert targetIndexes != null;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = targetIndexes;
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();

        // Remove column constraints job
        assert targetGridPane instanceof FXOMInstance;
        assert targetIndexes.isEmpty() == false;

        final GridPaneHierarchyMask mask = maskFactory.getMask(targetGridPane);
        for (int targetIndex : targetIndexes) {
            final FXOMObject targetConstraints
                    = mask.getColumnConstraintsAtIndex(targetIndex);
            // The target index is associated to an existing constraints value :
            // => we remove the constraints value
            if (targetConstraints != null) {
                final AbstractJob removeValueJob = deleteObjectJobFactory.getJob(targetConstraints);
                result.add(removeValueJob);
            }
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return "Remove Column Constraints"; //NOCHECK
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends JobFactory<RemoveColumnConstraintsJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }
        /**
         * Create an {@link RemoveColumnConstraintsJob} job
         * @param targetGridPane the target grid pane
         * @param targetIndexes the column indexes to delete
         * @return the job to execute
         */
        public RemoveColumnConstraintsJob getJob(FXOMObject targetGridPane, List<Integer> targetIndexes) {
            return create(RemoveColumnConstraintsJob.class, j -> j.setJobParameters(targetGridPane, targetIndexes));
        }
    }
}
