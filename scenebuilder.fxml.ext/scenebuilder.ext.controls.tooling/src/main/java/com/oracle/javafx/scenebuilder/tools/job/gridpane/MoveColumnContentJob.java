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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.layout.GridPane;

/**
 * This job move the gridPaneObject columns content at the provided movingColumnIndex
 * of the columnIndexDelta offset
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class MoveColumnContentJob extends BatchDocumentJob {

    private final IntegerPropertyMetadata columnIndexMeta =
            new IntegerPropertyMetadata.Builder()
                .withName(new PropertyName("columnIndex", GridPane.class)) //NOCHECK
                .withReadWrite(true)
                .withDefaultValue(0)
                .withInspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance gridPaneObject;
    private int movingColumnIndex;
    private int columnIndexDelta;

    private final DesignHierarchyMask.Factory GridPaneHierarchyMask;

    private final MoveCellContentJob.Factory moveCellContentJobFactory;

    // @formatter:off
    protected MoveColumnContentJob(
            JobExtensionFactory extensionFactory,
            DocumentManager documentManager,
            MoveCellContentJob.Factory moveCellContentJobFactory,
            DesignHierarchyMask.Factory GridPaneHierarchyMask) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.moveCellContentJobFactory = moveCellContentJobFactory;
        this.GridPaneHierarchyMask = GridPaneHierarchyMask;
    }

    protected void setJobParameters(FXOMObject gridPaneObject, int movingColumnIndex, int columnIndexDelta) {
        assert gridPaneObject instanceof FXOMInstance;
        assert gridPaneObject.getSceneGraphObject() instanceof GridPane;
        assert movingColumnIndex >= 0;

        this.gridPaneObject = (FXOMInstance)gridPaneObject;
        this.movingColumnIndex = movingColumnIndex;
        this.columnIndexDelta = columnIndexDelta;
    }

    /*
     * CompositeJob
     */

    @Override
    protected List<AbstractJob> makeSubJobs() {
        final List<AbstractJob> result = new ArrayList<>();

        final HierarchyMask m = GridPaneHierarchyMask.getMask(gridPaneObject);
        assert m.hasMainAccessory();

        for (FXOMObject childObject:m.getAccessories(m.getMainAccessory(), false)) {
            assert childObject instanceof FXOMInstance; // Because children of GridPane are nodes
            final FXOMInstance child = (FXOMInstance) childObject;
            if (columnIndexMeta.getValue(child) == movingColumnIndex) {
                // child belongs to column at movingColumnIndex
                final AbstractJob subJob = moveCellContentJobFactory.getJob(child, columnIndexDelta, 0);
                result.add(subJob);
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @Lazy
    public final static class Factory extends JobFactory<MoveColumnContentJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link MoveColumnContentJob} job.
         *
         * @param gridPaneObject the grid pane object
         * @param movingColumnIndex the column index
         * @param columnIndexDelta the offset to apply on the columnIndex value
         * @return the job to execute
         */
        public MoveColumnContentJob getJob(FXOMObject gridPaneObject, int movingColumnIndex, int columnIndexDelta) {
            return create(MoveColumnContentJob.class, j -> j.setJobParameters(gridPaneObject, movingColumnIndex, columnIndexDelta));
        }
    }
}
