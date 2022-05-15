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
package com.oracle.javafx.scenebuilder.core.clipboard.controller;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.clipboard.ClipboardHandler;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardEncoder;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.CutSelectionJob;
import com.oracle.javafx.scenebuilder.selection.job.PasteIntoJob;
import com.oracle.javafx.scenebuilder.selection.job.PasteJob;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ClipboardController implements com.oracle.javafx.scenebuilder.api.clipboard.Clipboard {

    private final DocumentWindow documentWindow;
    //private final Editor editorController;
    private final Content contentPanelController;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Selection selection;
    private final ActionFactory actionFactory;
    private final CutSelectionJob.Factory cutSelectionJobFactory;
    private final PasteJob.Factory pasteJobFactory;
    private final PasteIntoJob.Factory pasteIntoJobFactory;

    private final JobManager jobManager;

    public ClipboardController(
            DocumentWindow documentWindow,
            InlineEdit inlineEdit,
            Content contentPanelController,
            DocumentManager documentManager,
            Selection selection,
            ActionFactory actionFactory,
            CutSelectionJob.Factory cutSelectionJobFactory,
            PasteJob.Factory pasteJobFactory,
            PasteIntoJob.Factory pasteIntoJobFactory,
            JobManager jobManager) {
        super();
        this.documentWindow = documentWindow;
        this.selection = selection;
        this.contentPanelController = contentPanelController;
        this.inlineEdit = inlineEdit;
        this.documentManager = documentManager;
        this.actionFactory = actionFactory;
        this.pasteJobFactory = pasteJobFactory;
        this.pasteIntoJobFactory = pasteIntoJobFactory;
        this.cutSelectionJobFactory = cutSelectionJobFactory;
        this.jobManager = jobManager;
    }

    @Override
    public boolean canPerformCopy() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        //} else if (isCssRulesEditing(focusOwner) || isCssTextEditing(focusOwner)) {
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformCopy();
        } else {
            result = selection.getGroup() instanceof ObjectSelectionGroup;
        }
        return result;
    }

    @Override
    public void performCopy() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.copy();
        //} else if (isCssRulesEditing(focusOwner)) {
            //cssPanelController.copyRules();
        //} else if (isCssTextEditing(focusOwner)) {
            // CSS text pane is a WebView
            // Let the WebView handle the copy action natively
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performCopy();
        } else {
            assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
            final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();

            final ClipboardEncoder encoder = new ClipboardEncoder(osg.getSortedItems());
            assert encoder.isEncodable();
            Clipboard.getSystemClipboard().setContent(encoder.makeEncoding());
        }
    }

    @Override
    public boolean canPerformCut() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformCut();
        } else {
            final CutSelectionJob job = cutSelectionJobFactory.getJob();
            result = job.isExecutable();
        }
        return result;
    }

    @Override
    public void performCut() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.cut();
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performCut();
        } else {
            final CutSelectionJob job = cutSelectionJobFactory.getJob();
            jobManager.push(job);
        }
    }

    @Override
    public boolean canPerformPaste() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformPaste();
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            result = Clipboard.getSystemClipboard().hasString();
        } else {
            PasteJob job = pasteJobFactory.getJob();
            result = job.isExecutable();
        }
        return result;
    }

    @Override
    public void performPaste() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performPaste();
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)){
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.paste();
        } else {
            PasteJob job = pasteJobFactory.getJob();
            jobManager.push(job);
        }
    }

    @Override
    public boolean canPerformPasteInto() {
        return pasteIntoJobFactory.getJob().isExecutable();
    }

    @Override
    public void performPasteInto() {
        pasteIntoJobFactory.getJob().execute();
    }


}
