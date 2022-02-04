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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

import javafx.scene.layout.GridPane;

/**
 * Delete job for GridSelectionGroup.
 * This job manages either RemoveRow or RemoveColumn jobs depending on the selection.
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class DeleteGridSelectionJob extends BatchSelectionJob {

    private final DeleteColumnJob.Factory deleteColumnJobFactory;
    private final DeleteRowJob.Factory deleteRowJobFactory;
    private final ObjectSelectionGroup.Factory objectSelectionGroupFactory;

    private FXOMObject targetGridPane;

    // @formatter:off
    protected DeleteGridSelectionJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            DeleteColumnJob.Factory deleteColumnJobFactory,
            DeleteRowJob.Factory deleteRowJobFactory,
            ObjectSelectionGroup.Factory objectSelectionGroupFactory) {
     // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.deleteColumnJobFactory = deleteColumnJobFactory;
        this.deleteRowJobFactory = deleteRowJobFactory;
        this.objectSelectionGroupFactory = objectSelectionGroupFactory;
    }

    protected void setJobParameters() {
    }

    @Override
    protected List<AbstractJob> makeSubJobs() {

        final List<AbstractJob> result = new ArrayList<>();
        final Selection selection = getSelection();
        assert selection.getGroup() instanceof GridSelectionGroup;

        final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
        targetGridPane = gsg.getAncestor();
        switch (gsg.getType()) {
            case COLUMN:
                result.add(deleteColumnJobFactory.getJob());
                break;
            case ROW:
                result.add(deleteRowJobFactory.getJob());
                break;
            default:
                assert false;
                break;
        }
        return result;
    }

    @Override
    protected String makeDescription() {
        return getSubJobs().get(0).getDescription();
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        // Selection goes to the GridPane
        final Set<FXOMObject> newObjects = new HashSet<>();
        newObjects.add(targetGridPane);
        return objectSelectionGroupFactory.getGroup(newObjects, targetGridPane, null);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<DeleteGridSelectionJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link  DeleteGridSelectionJob} job
         * @return the job to execute
         */
        public DeleteGridSelectionJob getJob() {
            return create(DeleteGridSelectionJob.class, j -> j.setJobParameters());
        }
    }
}
