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
package com.oracle.javafx.scenebuilder.contenteditor.actions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.job.DeleteSelectionJob;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class DeleteAction extends AbstractAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final Editor editor;
    private final JobManager jobManager;
    private final DeleteSelectionJob.Factory deleteSelectionJobFactory;

    public DeleteAction(
            ActionExtensionFactory extensionFactory,
            DocumentWindow documentWindow,
            DocumentManager documentManager,
            InlineEdit inlineEdit,
            Editor editor,
            JobManager jobManager,
            Dialog dialog,
            DeleteSelectionJob.Factory deleteSelectionJobFactory) {
        super(extensionFactory);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.jobManager = jobManager;
        this.dialog = dialog;
        this.deleteSelectionJobFactory = deleteSelectionJobFactory;
    }

    @Override
    public boolean canPerform() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getCaretPosition() < tic.getLength();
        } else {
            final AbstractJob job = deleteSelectionJobFactory.getJob();
            result = job.isExecutable();
        }
        return result;
    }

    @Override
    public ActionStatus doPerform() {
        assert canPerform();

        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.deleteNextChar();
        } else {
            final List<FXOMObject> selectedObjects = editor.getSelectedObjects();

            // Collects fx:ids in selected objects and their descendants.
            // We filter out toggle groups because their fx:ids are managed automatically.
            final Map<String, FXOMObject> fxIdMap = new HashMap<>();
            for (FXOMObject selectedObject : selectedObjects) {
                fxIdMap.putAll(selectedObject.collectFxIds());
            }
            FXOMNodes.removeToggleGroups(fxIdMap);

            // Checks if deleted objects have some fx:ids and ask for confirmation.
            final boolean deleteConfirmed;
            if (fxIdMap.isEmpty()) {
                deleteConfirmed = true;
            } else {
                final String message;

                if (fxIdMap.size() == 1) {
                    if (selectedObjects.size() == 1) {
                        message = I18N.getString("alert.delete.fxid1of1.message");
                    } else {
                        message = I18N.getString("alert.delete.fxid1ofN.message");
                    }
                } else {
                    if (selectedObjects.size() == fxIdMap.size()) {
                        message = I18N.getString("alert.delete.fxidNofN.message");
                    } else {
                        message = I18N.getString("alert.delete.fxidKofN.message");
                    }
                }

                final Alert d = dialog.customAlert(documentWindow.getStage());
                d.setMessage(message);
                d.setDetails(I18N.getString("alert.delete.fxid.details"));
                d.setOKButtonTitle(I18N.getString("label.delete"));

                deleteConfirmed = (d.showAndWait() == ButtonID.OK);
            }

            if (deleteConfirmed) {
                final AbstractJob job = deleteSelectionJobFactory.getJob();
                jobManager.push(job);
            }
        }

        return ActionStatus.DONE;
    }
}