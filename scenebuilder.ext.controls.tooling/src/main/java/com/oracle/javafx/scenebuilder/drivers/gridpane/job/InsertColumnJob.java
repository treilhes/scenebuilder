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
import java.util.Collections;
import java.util.List;

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
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.ColumnConstraintsListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.drivers.gridpane.GridSelectionGroup;

import javafx.scene.layout.GridPane;

/**
 * Insert "insertCount" columns into the provided {@link GridPane} at the
 * specified "columnIndex"<br/>
 * Specific to {@link GridPane}
 */
public final class InsertColumnJob extends BatchSelectionJob {

    private static final ColumnConstraintsListPropertyMetadata columnContraintsMeta = new ColumnConstraintsListPropertyMetadata.Builder()
            .withName(new PropertyName("columnConstraints")) // NOCHECK
            .withReadWrite(true).withDefaultValue(Collections.emptyList()).withInspectorPath(InspectorPath.UNUSED)
            .build();

    private FXOMInstance gridPaneObject;
    private int columnIndex;
    private int insertCount;

    private final InsertColumnConstraintsJob.Factory insertColumnConstraintsJobFactory;
    private final MoveColumnContentJob.Factory moveColumnContentJobFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;

    // @formatter:off
    protected InsertColumnJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            Selection selection,
            InsertColumnConstraintsJob.Factory insertColumnConstraintsJobFactory,
            MoveColumnContentJob.Factory moveColumnContentJobFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.insertColumnConstraintsJobFactory = insertColumnConstraintsJobFactory;
        this.moveColumnContentJobFactory = moveColumnContentJobFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
    }

    protected void setJobParameters(FXOMObject gridPaneObject, int columnIndex, int insertCount) {
        assert gridPaneObject instanceof FXOMInstance;
        assert gridPaneObject.getSceneGraphObject() instanceof GridPane;
        assert columnIndex >= 0;
        assert columnIndex <= columnContraintsMeta.getValue((FXOMInstance) gridPaneObject).size();
        assert insertCount >= 1;

        this.gridPaneObject = (FXOMInstance) gridPaneObject;
        this.columnIndex = columnIndex;
        this.insertCount = insertCount;
    }

    /*
     * CompositeJob
     */

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        final AbstractJob insertJob = insertColumnConstraintsJobFactory.getJob(gridPaneObject, columnIndex,
                insertCount);
        result.add(insertJob);

        final int lastColumnIndex = columnContraintsMeta.getValue(gridPaneObject).size() - 1;
        for (int c = lastColumnIndex; c >= columnIndex; c--) {
            final AbstractJob moveJob = moveColumnContentJobFactory.getJob(gridPaneObject, c, insertCount);
            if (moveJob.isExecutable()) {
                result.add(moveJob);
            } // else column is empty : no children to move
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName();
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        return gridSelectionGroupFactory.getGroup(gridPaneObject, GridSelectionGroup.Type.COLUMN, columnIndex);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<InsertColumnJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link InsertColumnJob} job.
         *
         * @param gridPaneObject the target grid pane object
         * @param columnIndex    the column index where the insertion will take place
         * @param insertCount    the number of column to insert
         * @return the job to execute
         */
        public InsertColumnJob getJob(FXOMObject gridPaneObject, int columnIndex, int insertCount) {
            return create(InsertColumnJob.class, j -> j.setJobParameters(gridPaneObject, columnIndex, insertCount));
        }
    }
}
