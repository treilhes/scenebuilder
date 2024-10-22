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
package com.oracle.javafx.scenebuilder.menu.action.edit;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.action.Action.ActionStatus;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.editor.selection.ObjectSelectionGroup;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;

@ApplicationInstancePrototype
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = SelectParentAction.MENU_ID,
        targetMenuId = SelectNoneAction.MENU_ID,
        label = SelectParentAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling)
@ContextMenuItemAttachment(
        selectionGroup = ObjectSelectionGroup.class,
        id = SelectParentAction.MENU_ID,
        targetMenuId = DeleteAction.MENU_ID,
        label = SelectParentAction.TITLE,
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
@Accelerator(accelerator = "CTRL+UP")

public class SelectParentAction extends AbstractAction {

    public final static String MENU_ID = "selectParentMenu";
    public final static String TITLE = "menu.title.select.parent";

    private final FxmlDocumentManager documentManager;
    private final Selection selection;

    public SelectParentAction(
            ActionExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Selection selection) {
        super(extensionFactory);
        this.documentManager = documentManager;
        this.selection = selection;
    }

    /**
     * Returns true if the selection is not empty and the root object is not
     * selected.
     *
     * @return if the selection is not empty and the root object is not selected.
     */
    @Override
    public boolean canPerform() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        if (fxomDocument == null || fxomDocument.getFxomRoot() == null) {
            return false;
        }
        final FXOMObject rootObject = fxomDocument.getFxomRoot();
        return !selection.isEmpty() && !selection.isSelected(rootObject);
    }

    /**
     * Performs the select parent control action. If the selection is multiple, we
     * select the common ancestor.
     */
    @Override
    public ActionStatus doPerform() {
        assert canPerform(); // (1)
        final FXOMObject ancestor = selection.getAncestor();
        assert ancestor != null; // Because of (1)
        selection.select(ancestor);
        return ActionStatus.DONE;
    }
}