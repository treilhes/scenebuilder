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

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.HierarchyMask;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.base.BatchDocumentJob;
import com.gluonhq.jfxapps.core.api.mask.DesignHierarchyMask;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;

import javafx.scene.layout.GridPane;

/**
 * This job move the gridPaneObject columns content at the provided movingRowIndex
 * of the rowIndexDelta offset
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final  class MoveRowContentJob extends BatchDocumentJob {

    private final IntegerPropertyMetadata rowIndexMeta =
            new IntegerPropertyMetadata.Builder()
                .name(new PropertyName("rowIndex", GridPane.class)) //NOCHECK
                .readWrite(true)
                .defaultValue(0)
                .inspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance gridPaneObject;
    private int movingRowIndex;
    private int rowIndexDelta;

    private final DesignHierarchyMask.Factory GridPaneHierarchyMask;

    private final MoveCellContentJob.Factory moveCellContentJobFactory;

 // @formatter:off
    protected MoveRowContentJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            MoveCellContentJob.Factory moveCellContentJobFactory,
            DesignHierarchyMask.Factory GridPaneHierarchyMask) {
    // @formatter:on
        super(extensionFactory, documentManager);
        this.moveCellContentJobFactory = moveCellContentJobFactory;
        this.GridPaneHierarchyMask = GridPaneHierarchyMask;
    }

    protected void setJobParameters(FXOMObject gridPaneObject, int movingRowIndex, int rowIndexDelta) {
        assert gridPaneObject instanceof FXOMInstance;
        assert gridPaneObject.getSceneGraphObject() instanceof GridPane;
        assert movingRowIndex >= 0;

        this.gridPaneObject = (FXOMInstance)gridPaneObject;
        this.movingRowIndex = movingRowIndex;
        this.rowIndexDelta = rowIndexDelta;
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
            if (rowIndexMeta.getValue(child) == movingRowIndex) {
                // child belongs to column at movingRowIndex
                final AbstractJob subJob = moveCellContentJobFactory.getJob(child, 0, rowIndexDelta);
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
    public final static class Factory extends JobFactory<MoveRowContentJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link MoveRowContentJob} job.
         *
         * @param gridPaneObject the grid pane object
         * @param movingRowIndex the column index
         * @param rowIndexDelta the offset to apply on the rowIndex value
         * @return the job to execute
         */
        public MoveRowContentJob getJob(FXOMObject gridPaneObject, int movingRowIndex, int rowIndexDelta) {
            return create(MoveRowContentJob.class, j -> j.setJobParameters(gridPaneObject, movingRowIndex, rowIndexDelta));
        }
    }
}
