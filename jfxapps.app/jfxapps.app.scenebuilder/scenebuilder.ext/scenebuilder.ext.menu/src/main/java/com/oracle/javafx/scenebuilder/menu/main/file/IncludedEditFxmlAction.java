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
package com.oracle.javafx.scenebuilder.menu.main.file;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fxom.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.fxom.ui.controller.ctxmenu.annotation.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.SbEditor;
import com.oracle.javafx.scenebuilder.menu.main.modify.UseComputedSizeAction;

/**
 * @deprecated include will use multiple fx:include instead of a single file included
 */
@ApplicationInstancePrototype
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = IncludedEditFxmlAction.MENU_ID,
        targetMenuId = IncludeFxmlAction.MENU_ID,
        label = IncludedEditFxmlAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@ContextMenuItemAttachment(
        selectionGroup = ObjectSelectionGroup.class,
        id = IncludedEditFxmlAction.MENU_ID,
        targetMenuId = UseComputedSizeAction.MENU_ID,
        label = IncludedEditFxmlAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@Deprecated
public class IncludedEditFxmlAction extends AbstractAction {

    public final static String MENU_ID = "editIncludedFxmlMenu"; //NOCHECK
    public final static String TITLE = "menu.title.edit.included.default";

    private final FileSystem fileSystem;
    private final SbEditor editor;
    private final Dialog dialog;

    public IncludedEditFxmlAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            Optional<SbEditor> editor,
            Dialog dialog) {
        super(i18n, extensionFactory);
        this.fileSystem = fileSystem;
        this.editor = editor.orElse(null);
        this.dialog = dialog;

    }

    @Override
    public boolean canPerform() {
        if (editor == null) {
            return false;
        }
        return editor.getIncludedFile() != null;
    }

    @Override
    public ActionStatus doPerform() {
        assert canPerform(); // (1)
        final File includedFile = editor.getIncludedFile();
        assert includedFile != null; // Because of (1)
        try {
            fileSystem.open(includedFile.getAbsolutePath());
        } catch (IOException ioe) {
            dialog.showErrorAndWait(getI18n().getString("error.file.open.title"),
                    getI18n().getString("error.file.open.message", includedFile.getAbsolutePath()), "", ioe);
        }
        return ActionStatus.DONE;
    }

    public String getTitle() {

        String title = getI18n().getString("menu.title.edit.included.default");
        final File file = editor.getIncludedFile();
        if (file != null) {
            title = getI18n().getString("menu.title.edit.included", file.getName());
        }
        return title;
    }
}