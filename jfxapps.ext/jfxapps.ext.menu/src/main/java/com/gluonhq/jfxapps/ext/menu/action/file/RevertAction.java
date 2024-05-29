/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.ext.menu.action.file;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.application.InstanceWindow;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert.ButtonID;
import com.gluonhq.jfxapps.core.api.ui.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = RevertAction.MENU_ID,
        targetMenuId = SaveAsAction.MENU_ID,
        label = "menu.title.revert",
        positionRequest = PositionRequest.AsNextSibling)
public class RevertAction extends AbstractAction {

    public final static String MENU_ID = "revertMenu";

    private final ApplicationInstance document;
    private final DocumentManager documentManager;
    private final Dialog dialog;
    private final InstanceWindow documentWindow;
    private final ActionFactory actionFactory;

    public RevertAction(
            ActionExtensionFactory extensionFactory,
            ApplicationInstance document,
            DocumentManager documentManager,
            InstanceWindow documentWindow,
            Dialog dialog,
            ActionFactory actionFactory) {
        super(extensionFactory);
        this.document = document;
        this.documentManager = documentManager;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument omDocument = documentManager.fxomDocument().get();
        boolean locationSet = omDocument != null && omDocument.getLocation() != null;
        boolean dirty = documentManager.dirty().get();
        return locationSet && dirty;
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument omDocument = documentManager.fxomDocument().get();
        assert omDocument != null;
        assert omDocument.getLocation() != null;

        final Alert d = dialog.customAlert(documentWindow.getStage());
        d.setMessage(I18N.getString("alert.revert.question.message", documentWindow.getStage().getTitle()));
        d.setDetails(I18N.getString("alert.revert.question.details"));
        d.setOKButtonTitle(I18N.getString("label.revert"));

        if (d.showAndWait() == ButtonID.OK) {
            ActionStatus result = actionFactory.create(ReloadFileAction.class).perform();
            if (result == ActionStatus.FAILED) {
                document.close();
            }
            return result;
        }

        return ActionStatus.DONE;
    }

}