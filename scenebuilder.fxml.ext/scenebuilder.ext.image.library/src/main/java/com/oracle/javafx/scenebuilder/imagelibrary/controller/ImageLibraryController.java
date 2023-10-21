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
package com.oracle.javafx.scenebuilder.imagelibrary.controller;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;
import com.oracle.javafx.scenebuilder.imagelibrary.drag.source.ImageLibraryDragSource;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.LibraryListCell;
import com.oracle.javafx.scenebuilder.imagelibrary.panel.LibraryListItem;
import com.oracle.javafx.scenebuilder.selection.job.InsertAsSubComponentJob;
import com.oracle.javafx.scenebuilder.selection.job.SetDocumentRootJob;

import javafx.scene.control.ListView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ImageLibraryController {

    private final JobManager jobManager;
    private final Drag drag;
    private final ImageLibraryDragSource.Factory libraryDragSourceFactory;
    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory;

    private FXOMDocument fxomDocument;
    private SelectionState selectionState;


    public ImageLibraryController(
            @Autowired JobManager jobManager,
            @Autowired Drag drag,
            @Autowired @Lazy FxmlDocumentManager documentManager,
            ImageLibraryDragSource.Factory libraryDragSourceFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            InsertAsSubComponentJob.Factory insertAsSubComponentJobFactory) {

        this.jobManager = jobManager;
        this.drag = drag;
        this.libraryDragSourceFactory = libraryDragSourceFactory;
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.insertAsSubComponentJobFactory = insertAsSubComponentJobFactory;

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
        final AbstractJob job;
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
            job = setDocumentRootJobFactory.getJob(newObject, true /* usePredefinedSize */, description);

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
            job = insertAsSubComponentJobFactory.getJob(newObject, target, -1);
        }

        jobManager.push(job);

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
                    final AbstractJob job = setDocumentRootJobFactory.getJob(newItemRoot, true /* usePredefinedSize */, "unused"); // NOI18N
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
                    final AbstractJob job = insertAsSubComponentJobFactory.getJob(newItemRoot, targetCandidate, -1);
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

                final ImageLibraryDragSource dragSource = libraryDragSourceFactory.getDragSource(item);
                assert drag.getDragSource() == null;
                assert dragSource.isAcceptable();
                drag.begin(dragSource);

                db.setContent(dragSource.makeClipboardContent());
                db.setDragView(dragSource.makeDragView());
            }
        }

    }
}
