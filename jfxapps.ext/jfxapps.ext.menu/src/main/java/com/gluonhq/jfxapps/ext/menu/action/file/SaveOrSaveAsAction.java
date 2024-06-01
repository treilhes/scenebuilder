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
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.ext.menu.action.SaveAction;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = SaveOrSaveAsAction.MENU_ID,
        targetMenuId = OpenRecentProvider.MENU_ID,
        label = "menu.title.save",
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
@Accelerator(accelerator = "CTRL+S")
public class SaveOrSaveAsAction extends AbstractAction {

    public final static String MENU_ID = "saveMenu";

    private final DocumentManager documentManager;
    private final ActionFactory actionFactory;

    public SaveOrSaveAsAction(
            ActionExtensionFactory extensionFactory,
            DocumentManager documentManager,
            ActionFactory actionFactory) {
        super(extensionFactory);
        this.documentManager = documentManager;
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument omDocument = documentManager.fxomDocument().get();
        boolean locationSet = omDocument != null && omDocument.getLocation() != null;
        boolean dirty = documentManager.dirty().get();
        return !locationSet || dirty;
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument omDocument = documentManager.fxomDocument().get();
        if (omDocument.getLocation() == null) {
            return actionFactory.create(SaveAsAction.class).perform();
        } else {
            return actionFactory.create(SaveAction.class).perform();
        }
    }
}