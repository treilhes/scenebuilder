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
package com.gluonhq.jfxapps.core.fs.action.impl;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstance;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert.ButtonID;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class RevertAction extends AbstractAction {

    public final static String MENU_ID = "revertMenu";

    private final ApplicationInstance document;
    private final ApplicationInstanceEvents documentManager;
    private final Dialog dialog;
    private final MainInstanceWindow documentWindow;
    private final ActionFactory actionFactory;

    public RevertAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ApplicationInstance document,
            ApplicationInstanceEvents documentManager,
            MainInstanceWindow documentWindow,
            Dialog dialog,
            ActionFactory actionFactory) {
        super(i18n, extensionFactory);
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
        d.setMessage(getI18n().getString("alert.revert.question.message", documentWindow.getStage().getTitle()));
        d.setDetails(getI18n().getString("alert.revert.question.details"));
        d.setOKButtonTitle(getI18n().getString("label.revert"));

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