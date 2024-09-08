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
package com.oracle.javafx.scenebuilder.menu.action.file;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.api.action.Action.ActionStatus;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")

@MenuItemAttachment(
        id = RevealFxmlFileAction.MENU_ID,
        targetMenuId = RevertAction.MENU_ID,
        label = "#this.getRevealMenuItemText()",
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
public class RevealFxmlFileAction extends AbstractAction {

    public final static String MENU_ID = "revealMenu";

    private final FxmlDocumentManager documentManager;
    private final ApplicationInstanceWindow documentWindow;
    private final FileSystem fileSystem;
    private final Dialog dialog;

    public RevealFxmlFileAction(
            ActionExtensionFactory extensionFactory,
            @Autowired FxmlDocumentManager documentManager,
            @Autowired ApplicationInstanceWindow documentWindow,
            @Autowired FileSystem fileSystem,
            @Autowired Dialog dialog) {
        super(extensionFactory);
        this.documentManager = documentManager;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        return fxomDocument != null && fxomDocument.getLocation() != null;
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final URL location = fxomDocument.getLocation();

        try {
            fileSystem.revealInFileBrowser(new File(location.toURI()));
        } catch (IOException | URISyntaxException x) {
            dialog.showErrorAndWait("",
                    I18N.getString("alert.reveal.failure.message", documentWindow.getStage().getTitle()),
                    I18N.getString("alert.reveal.failure.details"), x);
            return ActionStatus.FAILED;
        }

        return ActionStatus.DONE;
    }

    public String getRevealMenuItemText() {

        /*
         * Setup title of the Reveal menu item according the underlying o/s.
         */
        final String revealMenuKey;
        if (JfxAppsPlatform.IS_MAC) {
            revealMenuKey = "menu.title.reveal.mac";
        } else if (JfxAppsPlatform.IS_WINDOWS) {
            revealMenuKey = "menu.title.reveal.win.mnemonic";
        } else {
            assert JfxAppsPlatform.IS_LINUX;
            revealMenuKey = "menu.title.reveal.linux";
        }
        return I18N.getString(revealMenuKey);
    }
}