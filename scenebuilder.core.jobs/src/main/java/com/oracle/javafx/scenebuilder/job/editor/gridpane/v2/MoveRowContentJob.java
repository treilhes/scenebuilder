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

package com.oracle.javafx.scenebuilder.job.editor.gridpane.v2;

import java.util.ArrayList;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchDocumentJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.IntegerPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPath;

import javafx.scene.layout.GridPane;

/**
 *
 */
public class MoveRowContentJob extends BatchDocumentJob {

    private final IntegerPropertyMetadata rowIndexMeta =
            new IntegerPropertyMetadata.Builder()
                .withName(new PropertyName("rowIndex", GridPane.class)) //NOCHECK
                .withReadWrite(true)
                .withDefaultValue(0)
                .withInspectorPath(InspectorPath.UNUSED).build();

    private final FXOMInstance gridPaneObject;
    private final int movingRowIndex;
    private final int rowIndexDelta;

    public MoveRowContentJob(SceneBuilderBeanFactory context, FXOMObject gridPaneObject, int movingRowIndex, int rowIndexDelta, Editor editor) {
        super(context, editor);
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
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        final DesignHierarchyMask m = new DesignHierarchyMask(gridPaneObject);
        assert m.isAcceptingSubComponent();

        for (int i = 0, count = m.getSubComponentCount(); i <  count; i++) {
            assert m.getSubComponentAtIndex(i) instanceof FXOMInstance; // Because children of GridPane are nodes
            final FXOMInstance child = (FXOMInstance) m.getSubComponentAtIndex(i);
            if (rowIndexMeta.getValue(child) == movingRowIndex) {
                // child belongs to column at movingRowIndex
                final Job subJob
                        = new MoveCellContentJob(getContext(), child, 0, rowIndexDelta, getEditorController()).extend();
                result.add(subJob);
            }
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return getClass().getSimpleName();
    }

}
