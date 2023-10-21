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
import java.util.Collections;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.list.RowConstraintsListPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.tools.driver.gridpane.GridSelectionGroup;

import javafx.scene.layout.GridPane;

/**
 * Job invoked when inserting columns in a GridPane.
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class InsertRowJob extends BatchSelectionJob {

    private static final RowConstraintsListPropertyMetadata rowContraintsMeta =
            new RowConstraintsListPropertyMetadata.Builder()
                .withName(new PropertyName("rowConstraints")) //NOCHECK
                .withReadWrite(true)
                .withDefaultValue(Collections.emptyList())
                .withInspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance gridPaneObject;
    private int rowIndex;
    private int insertCount;

    private final InsertRowConstraintsJob.Factory insertRowConstraintsJobFactory;

    private final MoveRowContentJob.Factory moveRowContentJobFactory;
    private final GridSelectionGroup.Factory gridSelectionGroupFactory;
 // @formatter:off
    protected InsertRowJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Selection selection,
            InsertRowConstraintsJob.Factory insertRowConstraintsJobFactory,
            MoveRowContentJob.Factory moveRowContentJobFactory,
            GridSelectionGroup.Factory gridSelectionGroupFactory) {
    // @formatter:on
        super(extensionFactory, documentManager, selection);
        this.insertRowConstraintsJobFactory = insertRowConstraintsJobFactory;
        this.moveRowContentJobFactory = moveRowContentJobFactory;
        this.gridSelectionGroupFactory = gridSelectionGroupFactory;
    }

    protected void setJobParameters(FXOMObject gridPaneObject, int rowIndex, int insertCount) {
        assert gridPaneObject instanceof FXOMInstance;
        assert gridPaneObject.getSceneGraphObject() instanceof GridPane;
        assert rowIndex >= 0;
        assert rowIndex <= rowContraintsMeta.getValue((FXOMInstance)gridPaneObject).size();
        assert insertCount >= 1;

        this.gridPaneObject = (FXOMInstance)gridPaneObject;
        this.rowIndex = rowIndex;
        this.insertCount = insertCount;
    }

    /*
     * CompositeJob
     */

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        final AbstractJob insertJob
                = insertRowConstraintsJobFactory.getJob(gridPaneObject, rowIndex, insertCount);
        result.add(insertJob);

        final int lastRowIndex = rowContraintsMeta.getValue(gridPaneObject).size()-1;
        for (int r = lastRowIndex; r >= rowIndex; r--) {
            final AbstractJob moveJob = moveRowContentJobFactory.getJob(gridPaneObject, r, insertCount);
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
        return gridSelectionGroupFactory.getGroup(gridPaneObject, GridSelectionGroup.Type.ROW, rowIndex);
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<InsertRowJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link InsertRowJob} job.
         *
         * @param gridPaneObject the grid pane object
         * @param rowIndex the row index
         * @param insertCount the insert count
         * @return the job to execute
         */
        public InsertRowJob getJob(FXOMObject gridPaneObject, int rowIndex, int insertCount) {
            return create(InsertRowJob.class, j -> j.setJobParameters(gridPaneObject, rowIndex, insertCount));
        }
    }
}
