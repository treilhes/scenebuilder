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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.job.AbstractJob;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.list.ColumnConstraintsListPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;

import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;

/**
 * Insert "insertCount" columns constraints into the provided {@link GridPane} at the specified "columnIndex"<br/>
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class InsertColumnConstraintsJob extends AbstractJob {

    private static final ColumnConstraintsListPropertyMetadata columnContraintsMeta =
            new ColumnConstraintsListPropertyMetadata.Builder()
                .name(new PropertyName("columnConstraints")) //NOCHECK
                .readWrite(true)
                .defaultValue(Collections.emptyList())
                .inspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance gridPaneObject;
    private int columnIndex;
    private int insertCount;

    protected InsertColumnConstraintsJob(
            JobExtensionFactory extensionFactory) {
        super(extensionFactory);
    }

    protected void setJobParameters(FXOMObject gridPaneObject, int columnIndex, int insertCount) {
        assert gridPaneObject instanceof FXOMInstance;
        assert gridPaneObject.getSceneGraphObject() instanceof GridPane;
        assert columnIndex >= 0;
        assert columnIndex <= columnContraintsMeta.getValue((FXOMInstance)gridPaneObject).size();
        assert insertCount >= 1;

        this.gridPaneObject = (FXOMInstance)gridPaneObject;
        this.columnIndex = columnIndex;
        this.insertCount = insertCount;
    }

    /*
     * Job
     */
    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public void doExecute() {
        // Same as redo()
        doRedo();
    }

    @Override
    public void doUndo() {
        final List<ColumnConstraints> constraintsList
                = new ArrayList<>(columnContraintsMeta.getValue(gridPaneObject));
        assert columnIndex < constraintsList.size();
        for (int i = 0; i < insertCount; i++) {
            constraintsList.remove(columnIndex);
        }
        columnContraintsMeta.setValue(gridPaneObject, constraintsList);
    }

    @Override
    public void doRedo() {
        final List<ColumnConstraints> constraintsList
                = new ArrayList<>(columnContraintsMeta.getValue(gridPaneObject));
        final ColumnConstraints template;
        if (columnIndex >= 1) {
            template = constraintsList.get(columnIndex-1);
        } else {
            template = null;
        }
        for (int i = 0; i < insertCount; i++) {
            constraintsList.add(columnIndex, makeColumnConstraints(template));
        }
        columnContraintsMeta.setValue(gridPaneObject, constraintsList);
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }


    /*
     * Private
     */

    private ColumnConstraints makeColumnConstraints(ColumnConstraints template) {
        final ColumnConstraints result = new ColumnConstraints();
        if (columnIndex >= 1) {
            result.setFillWidth(template.isFillWidth());
            result.setHalignment(template.getHalignment());
            result.setHgrow(template.getHgrow());
            result.setMaxWidth(template.getMaxWidth());
            result.setMinWidth(template.getMinWidth());
            result.setPercentWidth(template.getPercentWidth());
            result.setPrefWidth(template.getPrefWidth());
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<InsertColumnConstraintsJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link InsertColumnConstraintsJob} job.
         *
         * @param gridPaneObject the target grid pane object
         * @param columnIndex the column index where the insertion will take place
         * @param insertCount the number of columns constraints to insert
         * @return the job to execute
         */
        public InsertColumnConstraintsJob getJob(FXOMObject gridPaneObject,int columnIndex, int insertCount) {
            return create(InsertColumnConstraintsJob.class, j -> j.setJobParameters(gridPaneObject, columnIndex, insertCount));
        }
    }
}
