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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryPanelController;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.ObjectSelectionGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ActionMeta(nameKey = "action.name.import.selection", descriptionKey = "action.description.import.selection")
@ViewMenuItemAttachment(
        id = ImportSelectionAsControlAction.MENU_ID,
        targetMenuId = ManageJarFxmlAction.MENU_ID,
        label = "library.panel.menu.import.selection",
        positionRequest = PositionRequest.AsNextSibling,
        viewClass = LibraryPanelController.class,
        separatorAfter = true)
@Accelerator(accelerator = "SHIFT+I", whenFocusing = LibraryPanelController.class)
public class ImportSelectionAsControlAction extends AbstractAction {

    public final static String MENU_ID = "importSelectionMenu";

    private final Selection selection;
    private final LibraryPanelController libraryPanelController;

    public ImportSelectionAsControlAction(
            ActionExtensionFactory extensionFactory,
            Selection selection,
            LibraryPanelController libraryPanelController) {
        super(extensionFactory);
        this.selection = selection;
        this.libraryPanelController = libraryPanelController;
    }

    @Override
    public boolean canPerform() {
        // This method cannot be called if there is not a valid selection, a selection
        // eligible for being dropped onto Library panel.

        if (selection.isEmpty()) {
            return false;
        }

        AbstractSelectionGroup asg = selection.getGroup();

        if (asg instanceof ObjectSelectionGroup) {
            if (((ObjectSelectionGroup) asg).getItems().size() >= 1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public ActionStatus doPerform() {
        AbstractSelectionGroup asg = selection.getGroup();
        ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
        assert !osg.getItems().isEmpty();
        List<FXOMObject> selection = new ArrayList<>(osg.getItems());
        libraryPanelController.performImportSelection(selection);
        return ActionStatus.DONE;
    }
}