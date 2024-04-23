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
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.list.RowConstraintsListPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobExtensionFactory;
import com.oracle.javafx.scenebuilder.api.job.JobFactory;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

/**
 * Insert "insertCount" rows constraints into the provided {@link GridPane} at the specified "rowIndex"<br/>
 * Specific to {@link GridPane}
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class InsertRowConstraintsJob extends AbstractJob {

    private static final RowConstraintsListPropertyMetadata rowContraintsMeta =
            new RowConstraintsListPropertyMetadata.Builder()
                .withName(new PropertyName("rowConstraints")) //NOCHECK
                .withReadWrite(true)
                .withDefaultValue(Collections.emptyList())
                .withInspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance gridPaneObject;
    private int rowIndex;
    private int insertCount;

    protected InsertRowConstraintsJob(
            JobExtensionFactory extensionFactory) {
        super(extensionFactory);
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
        final List<RowConstraints> constraintsList
                = new ArrayList<>(rowContraintsMeta.getValue(gridPaneObject));
        assert rowIndex < constraintsList.size();
        for (int i = 0; i < insertCount; i++) {
            constraintsList.remove(rowIndex);
        }
        rowContraintsMeta.setValue(gridPaneObject, constraintsList);
    }

    @Override
    public void doRedo() {
        final List<RowConstraints> constraintsList
                = new ArrayList<>(rowContraintsMeta.getValue(gridPaneObject));
        final RowConstraints template;
        if (rowIndex >= 1) {
            template = constraintsList.get(rowIndex-1);
        } else {
            template = null;
        }
        for (int i = 0; i < insertCount; i++) {
            constraintsList.add(rowIndex, makeRowConstraints(template));
        }
        rowContraintsMeta.setValue(gridPaneObject, constraintsList);
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }


    /*
     * Private
     */

    private RowConstraints makeRowConstraints(RowConstraints template) {
        final RowConstraints result = new RowConstraints();
        if (rowIndex >= 1) {
            result.setFillHeight(template.isFillHeight());
            result.setValignment(template.getValignment());
            result.setVgrow(template.getVgrow());
            result.setMaxHeight(template.getMaxHeight());
            result.setMinHeight(template.getMinHeight());
            result.setPercentHeight(template.getPercentHeight());
            result.setPrefHeight(template.getPrefHeight());
        }
        return result;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<InsertRowConstraintsJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link InsertRowConstraintsJob} job.
         *
         * @param gridPaneObject the target grid pane object
         * @param rowIndex the row index where the insertion will take place
         * @param insertCount the number of rows constraints to insert
         * @return the job to execute
         */
        public InsertRowConstraintsJob getJob(FXOMObject gridPaneObject,int rowIndex, int insertCount) {
            return create(InsertRowConstraintsJob.class, j -> j.setJobParameters(gridPaneObject, rowIndex, insertCount));
        }
    }
}
