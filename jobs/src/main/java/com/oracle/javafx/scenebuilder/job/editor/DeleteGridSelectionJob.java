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
package com.oracle.javafx.scenebuilder.job.editor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.DeleteColumnJob;
import com.oracle.javafx.scenebuilder.job.editor.gridpane.DeleteRowJob;

/**
 * Delete job for GridSelectionGroup.
 * This job manages either RemoveRow or RemoveColumn jobs depending on the selection.
 */
public class DeleteGridSelectionJob extends BatchSelectionJob {

    private FXOMObject targetGridPane;

    public DeleteGridSelectionJob(ApplicationContext context, Editor editor) {
        super(context, editor);
    }

    @Override
    protected List<Job> makeSubJobs() {

        final List<Job> result = new ArrayList<>();
        final Selection selection = getEditorController().getSelection();
        assert selection.getGroup() instanceof GridSelectionGroup;

        final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
        targetGridPane = gsg.getAncestor();
        switch (gsg.getType()) {
            case COLUMN:
                result.add(new DeleteColumnJob(getContext(), getEditorController()).extend());
                break;
            case ROW:
                result.add(new DeleteRowJob(getContext(), getEditorController()).extend());
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
        return new ObjectSelectionGroup(newObjects, targetGridPane, null);
    }
}