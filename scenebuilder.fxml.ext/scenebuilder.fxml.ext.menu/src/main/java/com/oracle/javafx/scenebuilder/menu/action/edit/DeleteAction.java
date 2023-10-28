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
package com.oracle.javafx.scenebuilder.menu.action.edit;

import java.util.Map;

import org.scenebuilder.fxml.api.SbEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ContextMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMNodes;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.selection.job.DeleteSelectionJob;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = DeleteAction.MENU_ID,
        targetMenuId = DuplicateAction.MENU_ID,
        label = DeleteAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@ContextMenuItemAttachment(
        selectionGroup = ObjectSelectionGroup.class,
        id = DeleteAction.MENU_ID,
        targetMenuId = DuplicateAction.MENU_ID,
        label = DeleteAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@Accelerator(accelerator = "Delete")
@Accelerator(accelerator = "Backspace")
public class DeleteAction extends AbstractAction {

    public final static String MENU_ID = "deleteMenu";
    public final static String TITLE = "menu.title.delete";

    private final EditorInstanceWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final SbEditor editor;
    private final JobManager jobManager;
    private final Selection selection;
    private final DeleteSelectionJob.Factory deleteSelectionJobFactory;

    public DeleteAction(
            ActionExtensionFactory extensionFactory,
            EditorInstanceWindow documentWindow,
            InlineEdit inlineEdit,
            SbEditor editor,
            JobManager jobManager,
            Selection selection,
            Dialog dialog,
            DeleteSelectionJob.Factory deleteSelectionJobFactory) {
        super(extensionFactory);
        this.documentWindow = documentWindow;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.jobManager = jobManager;
        this.dialog = dialog;
        this.selection = selection;
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
            final Map<String, FXOMObject> fxIdMap = selection.collectSelectedFxIds();
            // We filter out toggle groups because their fx:ids are managed automatically.
            FXOMNodes.removeToggleGroups(fxIdMap);

            // Checks if deleted objects have some fx:ids and ask for confirmation.
            final boolean deleteConfirmed;
            if (fxIdMap.isEmpty()) {
                deleteConfirmed = true;
            } else {
                final String message;

                if (fxIdMap.size() == 1) {
                    message = I18N.getString("alert.delete.fxid1ofN.message");
                } else {
                    message = I18N.getString("alert.delete.fxidKofN.message");
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