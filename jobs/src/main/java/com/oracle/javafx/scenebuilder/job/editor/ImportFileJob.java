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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.editor.job.BatchSelectionJob;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.metadata.util.DesignHierarchyMask;

/**
 *
 */
public class ImportFileJob extends BatchSelectionJob {

    private final File file;
    private FXOMObject newObject, targetObject;
    private final FXOMDocument fxomDocument;

    public ImportFileJob(ApplicationContext context, File file, Editor editor) {
        super(context, editor);
        DocumentManager documentManager = context.getBean(DocumentManager.class);
        this.fxomDocument = documentManager.fxomDocument().get();
        
        assert file != null;
        this.file = file;
    }

    public FXOMObject getTargetObject() {
        return targetObject;
    }

    @Override
    protected List<Job> makeSubJobs() {
        final List<Job> result = new ArrayList<>();

        final FXOMDocument targetDocument = fxomDocument;

        try {
            newObject = FXOMNodes.newObject(targetDocument, file);

            // newObject is null when file is empty
            if (newObject != null) {
                // If the document is empty (root object is null), then we
                // insert the new object as root.
                // Otherwise, we insert the new object under the common parent
                // of the selected objects.
                final FXOMObject rootObject = targetDocument.getFxomRoot();

                if (rootObject == null) {
                    result.add(new SetDocumentRootJob(getContext(), newObject, getEditorController()).extend());
                } else {
                    final Selection selection = getEditorController().getSelection();
                    if (selection.isEmpty() || selection.isSelected(rootObject)) {
                        // No selection or root is selected -> we insert below root
                        targetObject = rootObject;
                    } else {
                        // Let's use the common parent of the selected objects.
                        // It might be null if selection holds some non FXOMObject entries
                        targetObject = selection.getAncestor();
                    }
                    // Build InsertAsSubComponent jobs
                    final DesignHierarchyMask targetMask = new DesignHierarchyMask(targetObject);
                    if (targetMask.isAcceptingSubComponent(newObject)) {
                        result.add(new InsertAsSubComponentJob(getContext(),
                                newObject,
                                targetObject,
                                targetMask.getSubComponentCount(),
                                getEditorController()).extend());
                    }
                }
            }
        } catch (IOException ex) {
        }

        return result;
    }

    @Override
    protected String makeDescription() {
        return I18N.getString("import.from.file", file.getName());
    }

    @Override
    protected AbstractSelectionGroup getNewSelectionGroup() {
        final List<FXOMObject> fxomObjects = new ArrayList<>();
        fxomObjects.add(newObject);
        return new ObjectSelectionGroup(fxomObjects, newObject, null);
    }
}
