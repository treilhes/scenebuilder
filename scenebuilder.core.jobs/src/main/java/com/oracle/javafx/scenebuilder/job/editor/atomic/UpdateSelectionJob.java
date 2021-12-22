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

package com.oracle.javafx.scenebuilder.job.editor.atomic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;

/**
 *
 */
public class UpdateSelectionJob extends Job {

    private AbstractSelectionGroup oldSelectionGroup;
    private final AbstractSelectionGroup newSelectionGroup;
    private FXOMDocument fxomDocument;
    private Selection selection;

    public UpdateSelectionJob(SceneBuilderBeanFactory context, AbstractSelectionGroup group, Editor editor) {
        super(context, editor);
        newSelectionGroup = group;
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();
    }

    public UpdateSelectionJob(SceneBuilderBeanFactory context, FXOMObject newSelectedObject, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();

        assert newSelectedObject != null;
        final List<FXOMObject> newSelectedObjects = new ArrayList<>();
        newSelectedObjects.add(newSelectedObject);
        newSelectionGroup = new ObjectSelectionGroup(newSelectedObjects, newSelectedObject, null);
    }

    public UpdateSelectionJob(SceneBuilderBeanFactory context, Collection<FXOMObject> newSelectedObjects, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        this.selection = documentManager.selectionDidChange().get().getSelection();

        assert newSelectedObjects != null; // But possibly empty
        if (newSelectedObjects.isEmpty()) {
            newSelectionGroup = null;
        } else {
            newSelectionGroup = new ObjectSelectionGroup(newSelectedObjects, newSelectedObjects.iterator().next(), null);
        }
    }

    /*
     * Job
     */

    @Override
    public boolean isExecutable() {
        return true;
    }

    @Override
    public void execute() {
        // Saves the current selection
        try {
            if (selection.getGroup() == null) {
                this.oldSelectionGroup = null;
            } else {
                this.oldSelectionGroup = selection.getGroup().clone();
            }
        } catch(CloneNotSupportedException x) {
            throw new RuntimeException("Bug", x);
        }

        // Now same as redo()
        redo();
    }

    @Override
    public void undo() {
        selection.select(oldSelectionGroup);
        assert selection.isValid(fxomDocument);
    }

    @Override
    public void redo() {
        selection.select(newSelectionGroup);
        assert selection.isValid(fxomDocument);
    }

    @Override
    public String getDescription() {
        // Not expected to reach the user
        return getClass().getSimpleName();
    }

}
