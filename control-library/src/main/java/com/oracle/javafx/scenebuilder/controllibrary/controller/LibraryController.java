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
package com.oracle.javafx.scenebuilder.controllibrary.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.editor.job.Job;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.controllibrary.editor.drag.source.LibraryDragSource;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.library.LibraryListCell;
import com.oracle.javafx.scenebuilder.controllibrary.editor.panel.library.LibraryListItem;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.job.editor.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.job.editor.SetDocumentRootJob;

import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Window;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class LibraryController {

    private final Editor editor;
    private final ApplicationContext context;
    private final JobManager jobManager;
    private final Drag drag;
    private FXOMDocument fxomDocument;
    private SelectionState selectionState;


    public LibraryController(
            @Autowired ApplicationContext context, 
            @Autowired Editor editor,
            @Autowired JobManager jobManager, 
            @Autowired Drag drag,
            @Autowired @Lazy DocumentManager documentManager) {

        this.context = context;
        this.editor = editor;
        this.jobManager = jobManager;
        this.drag = drag;
        documentManager.fxomDocument().subscribe(fxom -> this.fxomDocument = fxom);
        documentManager.selectionDidChange().subscribe(s -> this.selectionState = s);
    }

    /**
     * Performs the 'insert' edit action. This action creates an object matching the
     * specified library item and insert it in the document (according the selection
     * state).
     *
     * @param libraryItem the library item describing the object to be inserted.
     */
    public void performInsert(LibraryItem libraryItem) {
        final Job job;
        final FXOMObject target;

        assert canPerformInsert(libraryItem); // (1)

     // TODO classloader provided by fxmlDocument, good or not?
        final FXOMDocument newItemDocument = libraryItem.instantiate(fxomDocument.getClassLoader());
        assert newItemDocument != null; // Because (1)
        final FXOMObject newObject = newItemDocument.getFxomRoot();
        assert newObject != null;
        newObject.moveToFxomDocument(fxomDocument);
        final FXOMObject rootObject = fxomDocument.getFxomRoot();
        if (rootObject == null) { // Empty document
            final String description = I18N.getString("drop.job.insert.library.item", libraryItem.getName());
            job = new SetDocumentRootJob(context, newObject, true /* usePredefinedSize */, description, editor);

        } else {
            Selection selection = selectionState.getSelection();
            if (selection.isEmpty() || selection.isSelected(rootObject)) {
                // No selection or root is selected -> we insert below root
                target = rootObject;
            } else {
                // Let's use the common parent of the selected objects.
                // It might be null if selection holds some non FXOMObject entries
                target = selection.getAncestor();
            }
            job = new InsertAsSubComponentJob(context, newObject, target, -1, editor);
        }

        jobManager.push(job.extend());

        // TODO remove comment
        // WarnThemeAlert.showAlertIfRequired(this, newObject, ownerWindow);
    }

    /**
     * Returns true if the 'insert' action is permitted with the specified library
     * item.
     *
     * @param libraryItem the library item describing the object to be inserted.
     * @return true if the 'insert' action is permitted.
     */
    public boolean canPerformInsert(LibraryItem libraryItem) {
        final FXOMObject targetCandidate;
        final boolean result;

        if (fxomDocument == null || selectionState == null || selectionState.getSelection().isEmpty()) {
            result = false;
        } else {
            assert (fxomDocument.getClassLoader() != null);
            // TODO classloader provided by fxmlDocument, good or not?
            final FXOMDocument newItemDocument = libraryItem.instantiate(fxomDocument.getClassLoader());
            if (newItemDocument == null) {
                // For some reason, library is unable to instantiate this item
                result = false;
            } else {
                final FXOMObject newItemRoot = newItemDocument.getFxomRoot();
                newItemRoot.moveToFxomDocument(fxomDocument);
                assert newItemDocument.getFxomRoot() == null;
                final FXOMObject rootObject = fxomDocument.getFxomRoot();
                if (rootObject == null) { // Empty document
                    final Job job = new SetDocumentRootJob(context, newItemRoot, true /* usePredefinedSize */, "unused",
                            editor).extend(); // NOI18N
                    result = job.isExecutable();
                } else {
                    Selection selection = selectionState.getSelection();
                    if (selection.isEmpty() || selection.isSelected(rootObject)) {
                        // No selection or root is selected -> we insert below root
                        targetCandidate = rootObject;
                    } else {
                        // Let's use the common parent of the selected objects.
                        // It might be null if selection holds some non FXOMObject entries
                        targetCandidate = selection.getAncestor();
                    }
                    final Job job = new InsertAsSubComponentJob(context, newItemRoot, targetCandidate, -1, editor)
                            .extend();
                    result = job.isExecutable();
                }
            }
        }

        return result;
    }

    public void performDragDetected(LibraryListCell cell) {

//      System.out.println("LibraryListCell - setOnDragDetected.handle");
        final LibraryListItem listItem = cell.getItem();

        if ((listItem != null) && (fxomDocument != null)) {
            final LibraryItem item = cell.getItem().getLibItem();
            if (item != null) {
                final ListView<LibraryListItem> list = cell.getListView();
                final Dragboard db = list.startDragAndDrop(TransferMode.COPY);

                final Window ownerWindow = cell.getScene().getWindow();
                final LibraryDragSource dragSource = new LibraryDragSource(item, fxomDocument, ownerWindow);
                assert drag.getDragSource() == null;
                assert dragSource.isAcceptable();
                drag.begin(dragSource);

                db.setContent(dragSource.makeClipboardContent());
                db.setDragView(dragSource.makeDragView());
            }
        }

    }
}
