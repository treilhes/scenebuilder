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

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.action.editor.EditorPlatform;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstanceWindow;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ViewMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryPanelController;

@ApplicationInstanceSingleton
@Lazy
@ActionMeta(nameKey = "action.name.reveal.custom.folder", descriptionKey = "action.description.reveal.custom.folder")
@ViewMenuItemAttachment(
        id = RevealControlFolderAction.MENU_ID,
        targetMenuId = LibraryFolderMenuProvider.MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsFirstChild,
        viewClass = LibraryPanelController.class)
@Accelerator(accelerator = "SHIFT+R", whenFocusing = LibraryPanelController.class)
public class RevealControlFolderAction extends AbstractAction {

    public final static String MENU_ID = "revealMenu";

    private final ApplicationInstanceWindow documentWindowController;
    private final ControlLibrary userLibrary;
    private final FileSystem fileSystem;
    private final Dialog dialog;

    public RevealControlFolderAction(
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            Dialog dialog,
            ControlLibrary userLibrary,
            @Lazy ApplicationInstanceWindow documentWindowController) {
        super(extensionFactory);
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
                    I18N.getString("alert.reveal.failure.message", documentWindowController.getStage().getTitle()),
                    I18N.getString("alert.reveal.failure.details"), x);
            return ActionStatus.FAILED;
        }
        return ActionStatus.DONE;
    }

    public String getTitle() {
        final String revealMenuKey;
        if (JfxAppsPlatform.IS_MAC) {
            revealMenuKey = "menu.title.reveal.mac";
        } else if (JfxAppsPlatform.IS_WINDOWS) {
            revealMenuKey = "menu.title.reveal.win";
        } else {
            assert JfxAppsPlatform.IS_LINUX;
            revealMenuKey = "menu.title.reveal.linux";
        }
        return I18N.getStringOrDefault(revealMenuKey, revealMenuKey);
    }
}