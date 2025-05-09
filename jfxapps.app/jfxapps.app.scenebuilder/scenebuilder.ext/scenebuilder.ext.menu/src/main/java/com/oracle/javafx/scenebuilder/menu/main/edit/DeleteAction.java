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
package com.oracle.javafx.scenebuilder.menu.main.edit;

import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.gluonhq.jfxapps.core.api.fxom.ui.controller.ctxmenu.annotation.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert.ButtonID;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.FXOMNodes;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@ApplicationInstancePrototype
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

    public final static String MENU_ID = DefaultMenu.Edit.DELETE_ID;
    public final static String TITLE = "menu.title.delete";

    private final MainInstanceWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final JobManager jobManager;
    private final Selection selection;
    private final SelectionJobsFactory selectionJobsFactory;

    public DeleteAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            @Lazy MainInstanceWindow documentWindow,
            InlineEdit inlineEdit,
            JobManager jobManager,
            Selection selection,
            Dialog dialog,
            SelectionJobsFactory selectionJobsFactory) {
        super(i18n, extensionFactory);
        this.documentWindow = documentWindow;
        this.inlineEdit = inlineEdit;
        this.jobManager = jobManager;
        this.dialog = dialog;
        this.selection = selection;
        this.selectionJobsFactory = selectionJobsFactory;
    }

    @Override
    public boolean canPerform() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getCaretPosition() < tic.getLength();
        } else {
            final var job = selectionJobsFactory.deleteSelection();
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
                    message = getI18n().getString("alert.delete.fxid1ofN.message");
                } else {
                    message = getI18n().getString("alert.delete.fxidKofN.message");
                }

                final Alert d = dialog.customAlert(documentWindow.getStage());
                d.setMessage(message);
                d.setDetails(getI18n().getString("alert.delete.fxid.details"));
                d.setOKButtonTitle(getI18n().getString("label.delete"));

                deleteConfirmed = (d.showAndWait() == ButtonID.OK);
            }

            if (deleteConfirmed) {
                final var job = selectionJobsFactory.deleteSelection();
                jobManager.push(job);
            }
        }

        return ActionStatus.DONE;
    }
}