/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.menu.main.file;

import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.instance.ApplicationInstanceUi;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.treilhes.jfxplace.core.api.ui.dialog.Alert;
import com.treilhes.jfxplace.core.api.ui.dialog.Dialog;
import com.treilhes.jfxplace.core.api.ui.dialog.ModalWindow;
import com.treilhes.jfxplace.fxom.api.document.DocumentActionFactory;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
@MenuItemAttachment(
        id = RevertAction.MENU_ID,
        targetMenuId = DefaultMenu.File.SAVE_AS_ID,
        label = "menu.title.revert",
        positionRequest = PositionRequest.AsNextSibling)
public class RevertAction extends AbstractAction {

    public static final String MENU_ID = DefaultMenu.File.REVERT_TO_SAVED_ID;

    private final DocumentActionFactory documentActionFactory;

    private final FxomEvents applicationInstanceEvents;

    private final Dialog dialog;

    private final MainInstanceWindow instanceWindow;

    private final ApplicationInstanceUi instance;

    public RevertAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FxomEvents applicationInstanceEvents,
            DocumentActionFactory documentActionFactory,
            Dialog dialog,
            @Lazy ApplicationInstanceUi instance,
            @Lazy MainInstanceWindow instanceWindow) {
        super(i18n, extensionFactory);
        this.documentActionFactory = documentActionFactory;
        this.applicationInstanceEvents = applicationInstanceEvents;
        this.dialog = dialog;
        this.instance = instance;
        this.instanceWindow = instanceWindow;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument omDocument = applicationInstanceEvents.fxomDocument().get();
        boolean locationSet = omDocument != null && omDocument.getLocation() != null;
        boolean dirty = applicationInstanceEvents.dirty().get();
        return locationSet && dirty && documentActionFactory.reload().canPerform();
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument omDocument = applicationInstanceEvents.fxomDocument().get();
        assert omDocument != null;
        assert omDocument.getLocation() != null;

        var stage = instanceWindow.getStage();
        final Alert d = dialog.customAlert(stage);
        d.setMessage(getI18n().getString("alert.revert.question.message", stage.getTitle()));
        d.setDetails(getI18n().getString("alert.revert.question.details"));
        d.getModalWindow().setOKButtonTitle(getI18n().getString("label.revert"));

        if (d.showAndWait() == ModalWindow.ButtonID.OK) {
            ActionStatus result = documentActionFactory.reload().perform();
            if (result == ActionStatus.FAILED) {
                instance.close();
            }
            return result;
        }

        return ActionStatus.DONE;
    }

}