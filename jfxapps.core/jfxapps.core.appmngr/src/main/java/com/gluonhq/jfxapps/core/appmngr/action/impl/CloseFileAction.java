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
package com.gluonhq.jfxapps.core.appmngr.action.impl;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.fs.FileSystemActionFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.InlineEdit;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;

@ApplicationInstancePrototype
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")
public class CloseFileAction extends AbstractAction {

    private final ApplicationInstanceEvents documentManager;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final MainInstanceWindow documentWindow;
    private final FileSystemActionFactory fileSystemActionFactory;
    private final ApplicationInstance document;
    private boolean force = false;


    public CloseFileAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            ApplicationInstance document,
            MainInstanceWindow documentWindow,
            InlineEdit inlineEdit,
            Dialog dialog,
            FileSystemActionFactory fileSystemActionFactory) {
        super(i18n, extensionFactory);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystemActionFactory = fileSystemActionFactory;
    }

    public void setForce(boolean force) {
        this.force  = force;
    }
    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        // Makes sure that our window is front
        documentWindow.getStage().toFront();

        // Check if an editing session is on going
        if (inlineEdit.isTextEditingSessionOnGoing()) {
            // Check if we can commit the editing session
            if (!inlineEdit.canGetFxmlText()) {
                // Commit failed
                return ActionStatus.CANCELLED;
            }
        }

        // Checks if there are some pending changes
        final boolean closeConfirmed;
        if (!force && documentManager.dirty().get()) {

            final Alert d = dialog.customAlert(documentWindow.getStage());
            d.setMessage(getI18n().getString("alert.save.question.message", documentWindow.getStage().getTitle()));
            d.setDetails(getI18n().getString("alert.save.question.details"));
            d.setOKButtonTitle(getI18n().getString("label.save"));
            d.setActionButtonTitle(getI18n().getString("label.do.not.save"));
            d.setActionButtonVisible(true);

            switch (d.showAndWait()) {
            default:
            case OK:
                closeConfirmed = fileSystemActionFactory.saveOrSaveAs().checkAndPerform() == ActionStatus.DONE;
                break;
            case CANCEL:
                closeConfirmed = false;
                break;
            case ACTION: // Do not save
                closeConfirmed = true;
                break;
            }

        } else {
            // No pending changes
            closeConfirmed = true;
        }

        if (closeConfirmed) {
            document.close();
        }

        return closeConfirmed ? ActionStatus.DONE : ActionStatus.CANCELLED;
    }

}