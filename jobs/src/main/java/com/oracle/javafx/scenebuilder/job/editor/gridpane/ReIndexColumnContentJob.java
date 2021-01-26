/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.job.editor.gridpane;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.Metadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.job.editor.BatchJob;
import com.oracle.javafx.scenebuilder.job.editor.atomic.ModifyObjectJob;

/**
 * Job invoked when re-indexing columns content.
 *
 * IMPORTANT:
 * This job cannot extends BatchDocumentJob because its sub jobs list cannot be initialized lazily.
 */
public class ReIndexColumnContentJob extends Job {

    private Job subJob;
    private final int offset;
    private final FXOMObject targetGridPane;
    private final List<Integer> targetIndexes;
    private FXOMDocument fxomDocument;

    public ReIndexColumnContentJob(ApplicationContext context,
            final Editor editor,
            final int offset,
            final FXOMObject targetGridPane,
            final List<Integer> targetIndexes) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.offset = offset;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = targetIndexes;
        buildSubJobs();
    }

    public ReIndexColumnContentJob(ApplicationContext context,
            final Editor editor,
            final int offset,
            final FXOMObject targetGridPane,
            final int targetIndex) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.offset = offset;
        this.targetGridPane = targetGridPane;
        this.targetIndexes = new ArrayList<>();
        this.targetIndexes.add(targetIndex);
        buildSubJobs();
    }

    @Override
    public boolean isExecutable() {
        // When the columns are empty, there is no content to move and the
        // sub job list may be empty.
        // => we do not invoke subJob.isExecutable() here.
        return subJob != null;
    }

    @Override
    public void execute() {
        assert isExecutable();
        fxomDocument.beginUpdate();
        subJob.execute();
        fxomDocument.endUpdate();
    }

    @Override
    public void undo() {
        fxomDocument.beginUpdate();
        subJob.undo();
        fxomDocument.endUpdate();
    }

    @Override
    public void redo() {
        fxomDocument.beginUpdate();
        subJob.redo();
        fxomDocument.endUpdate();
    }

    @Override
    public String getDescription() {
        return "ReIndex Column Content"; //NOI18N
    }

    private void buildSubJobs() {

        // Create sub job
    	BatchJob batchJob = new BatchJob(getContext(), getEditorController(),
                true /* shouldRefreshSceneGraph */, null);

        assert targetIndexes.isEmpty() == false;
        final DesignHierarchyMask targetGridPaneMask
                = new DesignHierarchyMask(targetGridPane);
        final PropertyName propertyName = new PropertyName(
                "columnIndex", javafx.scene.layout.GridPane.class); //NOI18N

        for (int targetIndex : targetIndexes) {
            final List<FXOMObject> children
                    = targetGridPaneMask.getColumnContentAtIndex(targetIndex);
            for (FXOMObject child : children) {
                assert child instanceof FXOMInstance;
                final FXOMInstance childInstance = (FXOMInstance) child;
                final ValuePropertyMetadata vpm = Metadata.getMetadata().
                        queryValueProperty(childInstance, propertyName);
                int newIndexValue = targetIndex + offset;
                final Job modifyJob = new ModifyObjectJob(getContext(),
                        childInstance, vpm, newIndexValue, getEditorController()).extend();
                batchJob.addSubJob(modifyJob);
            }
        }

        subJob = batchJob.extend();
    }
}
