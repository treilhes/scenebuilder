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

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.job.JobExtensionFactory;
import com.gluonhq.jfxapps.core.api.job.JobFactory;
import com.gluonhq.jfxapps.core.api.job.base.AbstractJob;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.InspectorPath;

import javafx.scene.Node;
import javafx.scene.layout.GridPane;

/**
 * Move an {@link FXOMObject} in a {@link GridPane}.<br/>
 * The columnIndex/rowIndex of an object is updated of the specified columnIndexDelta/rowIndexDelta
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public final class MoveCellContentJob extends AbstractJob {

    private static final IntegerPropertyMetadata columnIndexMeta =
            new IntegerPropertyMetadata.Builder()
                .name(new PropertyName("columnIndex", GridPane.class)) //NOCHECK
                .readWrite(true)
                .defaultValue(0)
                .inspectorPath(InspectorPath.UNUSED).build();

    private static final IntegerPropertyMetadata rowIndexMeta =
            new IntegerPropertyMetadata.Builder()
                .name(new PropertyName("rowIndex", GridPane.class)) //NOCHECK
                .readWrite(true)
                .defaultValue(0)
                .inspectorPath(InspectorPath.UNUSED).build();

    private FXOMInstance fxomObject;
    private int columnIndexDelta;
    private int rowIndexDelta;
    private int oldColumnIndex = -1;
    private int oldRowIndex = -1;
    private final FXOMDocument fxomDocument;

    protected MoveCellContentJob(
            JobExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager) {
        super(extensionFactory);
        this.fxomDocument = documentManager.fxomDocument().get();
    }

    protected void setJobParameters(FXOMInstance fxomObject, int columnIndexDelta, int rowIndexDelta) {
        assert fxomObject != null;
        assert fxomObject.getSceneGraphObject().isInstanceOf(Node.class);

        this.fxomObject = fxomObject;
        this.columnIndexDelta = columnIndexDelta;
        this.rowIndexDelta = rowIndexDelta;
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
        oldColumnIndex = columnIndexMeta.getValue(fxomObject);
        oldRowIndex = rowIndexMeta.getValue(fxomObject);

        assert oldColumnIndex + columnIndexDelta >= 0;
        assert oldRowIndex + rowIndexDelta >= 0;

        // Now same as redo()
        doRedo();
    }

    @Override
    public void doUndo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        columnIndexMeta.setValue(fxomObject, oldColumnIndex);
        rowIndexMeta.setValue(fxomObject, oldRowIndex);
        fxomDocument.endUpdate();
    }

    @Override
    public void doRedo() {
        assert isExecutable();

        fxomDocument.beginUpdate();
        columnIndexMeta.setValue(fxomObject, oldColumnIndex + columnIndexDelta);
        rowIndexMeta.setValue(fxomObject, oldRowIndex + rowIndexDelta);
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return getClass().getSimpleName();
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public final static class Factory extends JobFactory<MoveCellContentJob> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        /**
         * Create an {@link MoveCellContentJob} job.
         *
         * @param fxomObject the fxom object
         * @param columnIndexDelta the index offset applied to columnIndex
         * @param rowIndexDelta the index offset applied to rowIndex
         * @return the job to execute
         */
        public MoveCellContentJob getJob(FXOMInstance fxomObject, int columnIndexDelta, int rowIndexDelta) {
            return create(MoveCellContentJob.class, j -> j.setJobParameters(fxomObject, columnIndexDelta, rowIndexDelta));
        }
    }
}
