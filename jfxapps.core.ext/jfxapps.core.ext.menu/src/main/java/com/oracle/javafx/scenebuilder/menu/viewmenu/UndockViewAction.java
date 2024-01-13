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
package com.oracle.javafx.scenebuilder.menu.viewmenu;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.ViewMenuItemAttachment;

@Prototype
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

@ViewMenuItemAttachment(
        id = UndockViewAction.MENU_ID,
        label = "view.menu.title.undock",
        targetMenuId = MoveToDockAction.MENU_ID,
        positionRequest = PositionRequest.AsNextSibling,
        viewClass = AbstractFxmlViewController.class)
@Accelerator(accelerator = "CTRL+U", whenFocusing = AbstractFxmlViewController.class)
public class UndockViewAction extends AbstractAction {

    public final static String MENU_ID = "undockViewMenu";

    private final DockViewController viewMenuController;
    private final DocumentManager documentManager;

    public UndockViewAction(
            ActionExtensionFactory extensionFactory,
            DocumentManager documentManager,
            DockViewController viewMenuController) {
        super(extensionFactory);
        this.viewMenuController = viewMenuController;
        this.documentManager = documentManager;
    }

    @Override
    public boolean canPerform() {
        return documentManager.focusedView().get() != null;
    }

    @Override
    public ActionStatus doPerform() {
        viewMenuController.performUndock(documentManager.focusedView().get());
        return ActionStatus.DONE;
    }

}