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
package com.oracle.javafx.scenebuilder.controllibrary.action;

import java.io.IOException;

import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryPanelController;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.emc4j.boot.api.platform.EmcPlatform;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.fs.FileSystem;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.shortcut.annotation.Accelerator;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.ViewMenuItemAttachment;
import com.treilhes.jfxplace.core.api.ui.dialog.Dialog;

@ApplicationInstanceSingleton
@ActionMeta(nameKey = "action.name.reveal.custom.folder", descriptionKey = "action.description.reveal.custom.folder")
@ViewMenuItemAttachment(
        id = RevealControlFolderAction.MENU_ID,
        targetMenuId = LibraryFolderMenuProvider.MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsFirstChild,
        viewClass = LibraryPanelController.class)
@Accelerator(accelerator = "SHIFT+R", whenFocusing = LibraryPanelController.class)
public class RevealControlFolderAction extends AbstractAction {

    public static final String MENU_ID = "revealMenu";

    private final MainInstanceWindow documentWindowController;
    private final ControlLibrary userLibrary;
    private final FileSystem fileSystem;
    private final Dialog dialog;

    public RevealControlFolderAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            Dialog dialog,
            ControlLibrary userLibrary,
            @Lazy MainInstanceWindow documentWindowController) {
        super(i18n, extensionFactory);
        this.documentWindowController = documentWindowController;
        this.userLibrary = userLibrary;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {
        try {
            fileSystem.revealInFileBrowser(userLibrary.getPath());
        } catch (IOException x) {
            dialog.showErrorAndWait("",
                    getI18n().getString("alert.reveal.failure.message", documentWindowController.getStage().getTitle()),
                    getI18n().getString("alert.reveal.failure.details"), x);
            return ActionStatus.FAILED;
        }
        return ActionStatus.DONE;
    }

    public String getTitle() {
        final String revealMenuKey;
        if (EmcPlatform.IS_MAC) {
            revealMenuKey = "menu.title.reveal.mac";
        } else if (EmcPlatform.IS_WINDOWS) {
            revealMenuKey = "menu.title.reveal.win";
        } else {
            assert EmcPlatform.IS_LINUX;
            revealMenuKey = "menu.title.reveal.linux";
        }
        return getI18n().getStringOrDefault(revealMenuKey, revealMenuKey);
    }
}