/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.clipboard.controller;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardEncoder;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardHandler;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;

@ApplicationInstanceSingleton
public class ClipboardController implements com.gluonhq.jfxapps.core.api.clipboard.Clipboard {

    private final MainInstanceWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final ApplicationInstanceEvents documentManager;
    private final Selection selection;
    private final SelectionJobsFactory selectionJobsFactory;
    private final JobManager jobManager;
    private final ClipboardEncoder clipboardEncoder;

    //@formatter:off
    public ClipboardController(
            MainInstanceWindow documentWindow,
            InlineEdit inlineEdit,
            ApplicationInstanceEvents documentManager,
            Selection selection,
            SelectionJobsFactory selectionJobsFactory,
            JobManager jobManager,
            ClipboardEncoder clipboardEncoder) {
      //@formatter:on
        super();
        this.documentWindow = documentWindow;
        this.inlineEdit = inlineEdit;
        this.documentManager = documentManager;
        this.selection = selection;
        this.selectionJobsFactory = selectionJobsFactory;
        this.jobManager = jobManager;
        this.clipboardEncoder = clipboardEncoder;
    }

    @Override
    public boolean canPerformCopy() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            return tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        //} else if (isCssRulesEditing(focusOwner) || isCssTextEditing(focusOwner)) {
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            return cphandler.canPerformCopy();
        } else {
            return selection.getGroup() instanceof ObjectSelectionGroup;
        }
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

            assert clipboardEncoder.isEncodable(osg.getSortedItems());
            var encoded = clipboardEncoder.makeEncoding(osg.getSortedItems());
            Clipboard.getSystemClipboard().setContent(encoded);
        }
    }

    @Override
    public boolean canPerformCut() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            return tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            return cphandler.canPerformCut();
        } else {
            return selectionJobsFactory.cutSelection().isExecutable();
        }
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
            final Job job = selectionJobsFactory.cutSelection();
            jobManager.push(job);
        }
    }

    @Override
    public boolean canPerformPaste() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            return cphandler.canPerformPaste();
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            return Clipboard.getSystemClipboard().hasString();
        } else {
            return selectionJobsFactory.pasteInto().isExecutable();
        }
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
            final Job job = selectionJobsFactory.pasteInto();
            jobManager.push(job);
        }
    }

    //FIXME Need to differenciate paste and pasteInto
    // pasteInto is used when the user wants to paste into the current selection
    // paste is used when the user wants to paste as the next sibling of the current selection
    @Override
    public boolean canPerformPasteInto() {
        return canPerformPaste();
    }

    @Override
    public void performPasteInto() {
        performPaste();
    }

}
