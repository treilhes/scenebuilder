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

import org.scenebuilder.fxml.api.SbEditor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ContextMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.menu.action.modify.UseComputedSizeAction;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = EditIncludedFxmlAction.MENU_ID,
        targetMenuId = IncludeFxmlAction.MENU_ID,
        label = EditIncludedFxmlAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@ContextMenuItemAttachment(
        selectionGroup = ObjectSelectionGroup.class,
        id = EditIncludedFxmlAction.MENU_ID,
        targetMenuId = UseComputedSizeAction.MENU_ID,
        label = EditIncludedFxmlAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
public class EditIncludedFxmlAction extends AbstractAction {

    public final static String MENU_ID = "editIncludedFxmlMenu"; //NOCHECK
    public final static String TITLE = "menu.title.edit.included.default";

    private final FileSystem fileSystem;
    private final SbEditor editor;
    private final Dialog dialog;

    public EditIncludedFxmlAction(
            ActionExtensionFactory extensionFactory,
            FileSystem fileSystem,
            SbEditor editor,
            Dialog dialog) {
        super(extensionFactory);
        this.fileSystem = fileSystem;
        this.editor = editor;
        this.dialog = dialog;

    }

    @Override
    public boolean canPerform() {
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
            dialog.showErrorAndWait(I18N.getString("error.file.open.title"),
                    I18N.getString("error.file.open.message", includedFile.getAbsolutePath()), "", ioe);
        }
        return ActionStatus.DONE;
    }

    public String getTitle() {

        String title = I18N.getString("menu.title.edit.included.default");
        final File file = editor.getIncludedFile();
        if (file != null) {
            title = I18N.getString("menu.title.edit.included", file.getName());
        }
        return title;
    }
}