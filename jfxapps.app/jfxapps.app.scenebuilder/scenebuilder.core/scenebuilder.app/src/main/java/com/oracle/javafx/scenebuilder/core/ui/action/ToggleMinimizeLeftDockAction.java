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
package com.oracle.javafx.scenebuilder.core.ui.action;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.shortcut.annotation.Accelerator;
import com.gluonhq.jfxapps.core.api.ui.DockActionFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.Docks;

@Prototype
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
@MenuItemAttachment(
        id = ToggleMinimizeLeftDockAction.MENU_ID,
        targetMenuId = ViewMenuProvider.MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AfterNextSeparator,
        separatorBefore = true)
@Accelerator(accelerator = "CTRL+L")
public class ToggleMinimizeLeftDockAction extends AbstractAction {

    public final static String MENU_ID = "toggleMinimizeLeftMenu";

    private final Dock targetDock;
    private final Action action;

    public ToggleMinimizeLeftDockAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            DockViewController dockViewController,
            DockActionFactory dockActionFactory) {
        super(i18n, extensionFactory);
        this.targetDock = dockViewController.getDock(Docks.LEFT_DOCK_UUID);
        this.action = dockActionFactory.toggleMinimized(this.targetDock);
    }

    @Override
    public boolean canPerform() {
        return action.canPerform();
    }

    @Override
    public ActionStatus doPerform() {
        return action.perform();
    }

    public String getTitle() {
        final String title;
        if (targetDock.isMinimized()) {
            title = "menu.title.maximize.dock.left";
        } else {
            title = "menu.title.minimize.dock.left";
        }
        return title;
    }
}