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

import java.io.File;
import java.net.URL;

import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.emc4j.boot.api.context.annotation.Prototype;
import com.treilhes.emc4j.boot.api.platform.EmcPlatform;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.fs.FileSystemActionFactory;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.treilhes.jfxplace.core.api.ui.dialog.Dialog;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = RevealFxmlFileAction.MENU_ID,
        targetMenuId = RevertAction.MENU_ID,
        label = "#this.getRevealMenuItemText()",
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
public class RevealFxmlFileAction extends AbstractAction {

    public static final String MENU_ID = DefaultMenu.File.REVEAL_ID;

    private final FxomEvents applicationInstanceEvents;
    private final FileSystemActionFactory fileSystemActionFactory;
    private final Dialog dialog;

    private final MainInstanceWindow mainInstanceWindow;

    public RevealFxmlFileAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FxomEvents applicationInstanceEvents,
            FileSystemActionFactory fileSystemActionFactory,
            Dialog dialog,
            @Lazy MainInstanceWindow mainInstanceWindow) {
        super(i18n, extensionFactory);
        this.applicationInstanceEvents = applicationInstanceEvents;
        this.fileSystemActionFactory = fileSystemActionFactory;
        this.mainInstanceWindow = mainInstanceWindow;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument fxomDocument = applicationInstanceEvents.fxomDocument().get();
        if (fxomDocument != null && fxomDocument.getLocation() != null) {
            try {
                var file = new File(fxomDocument.getLocation().toURI());
                return fileSystemActionFactory.reveal(file).canPerform();
            } catch (Exception e) {}
        }
        return false;
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument fxomDocument = applicationInstanceEvents.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final URL location = fxomDocument.getLocation();

        try {
            var file = new File(location.toURI());
            return fileSystemActionFactory.reveal(file).perform();
        } catch (Exception x) {
            dialog.showErrorAndWait("",
                    getI18n().getString("alert.reveal.failure.message", mainInstanceWindow.getStage().getTitle()),
                    getI18n().getString("alert.reveal.failure.details"), x);
            return ActionStatus.FAILED;
        }
    }

    public String getRevealMenuItemText() {

        /*
         * Setup title of the Reveal menu item according the underlying o/s.
         */
        final String revealMenuKey;
        if (EmcPlatform.IS_MAC) {
            revealMenuKey = "menu.title.reveal.mac";
        } else if (EmcPlatform.IS_WINDOWS) {
            revealMenuKey = "menu.title.reveal.win.mnemonic";
        } else {
            assert EmcPlatform.IS_LINUX;
            revealMenuKey = "menu.title.reveal.linux";
        }
        return getI18n().getString(revealMenuKey);
    }
}