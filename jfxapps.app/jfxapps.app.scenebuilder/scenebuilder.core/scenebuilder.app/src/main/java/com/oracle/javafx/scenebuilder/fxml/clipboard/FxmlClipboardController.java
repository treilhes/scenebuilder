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
package com.oracle.javafx.scenebuilder.fxml.clipboard;

import org.scenebuilder.fxml.api.Content;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.clipboard.ClipboardHandler;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editors.ApplicationInstanceWindow;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.ui.misc.InlineEdit;
import com.gluonhq.jfxapps.core.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.clipboard.controller.ClipboardController;
import com.oracle.javafx.scenebuilder.core.clipboard.internal.ClipboardEncoder;
import com.oracle.javafx.scenebuilder.fxml.selection.job.CutSelectionJob;
import com.oracle.javafx.scenebuilder.fxml.selection.job.PasteIntoJob;
import com.oracle.javafx.scenebuilder.fxml.selection.job.PasteJob;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class FxmlClipboardController extends ClipboardController {

    private final ApplicationInstanceWindow documentWindow;
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

    public FxmlClipboardController(
            ApplicationInstanceWindow documentWindow,
            InlineEdit inlineEdit,
            Content contentPanelController,
            DocumentManager documentManager,
            Selection selection,
            ActionFactory actionFactory,
            CutSelectionJob.Factory cutSelectionJobFactory,
            PasteJob.Factory pasteJobFactory,
            PasteIntoJob.Factory pasteIntoJobFactory,
            JobManager jobManager) {
        super(documentWindow, inlineEdit, documentManager);
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
    public boolean editorCanPerformCopy() {
        return selection.getGroup() instanceof ObjectSelectionGroup;
    }

    @Override
    public void editorPerformCopy() {
        assert selection.getGroup() instanceof ObjectSelectionGroup; // Because of (1)
        final ObjectSelectionGroup osg = (ObjectSelectionGroup) selection.getGroup();

        final ClipboardEncoder encoder = new ClipboardEncoder(osg.getSortedItems());
        assert encoder.isEncodable();
        Clipboard.getSystemClipboard().setContent(encoder.makeEncoding());
    }

    @Override
    public boolean editorCanPerformCut() {
        final CutSelectionJob job = cutSelectionJobFactory.getJob();
        return job.isExecutable();
    }

    @Override
    public void editorPerformCut() {
        final CutSelectionJob job = cutSelectionJobFactory.getJob();
        jobManager.push(job);
    }

    @Override
    public boolean editorCanPerformPaste() {
        final PasteJob job = pasteJobFactory.getJob();
        return job.isExecutable();
    }

    @Override
    public void editorPerformPaste() {
        final PasteJob job = pasteJobFactory.getJob();
        jobManager.push(job);
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
