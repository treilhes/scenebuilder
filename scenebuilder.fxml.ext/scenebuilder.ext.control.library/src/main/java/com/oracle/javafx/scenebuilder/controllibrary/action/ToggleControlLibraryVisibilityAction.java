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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.controllibrary.panel.ControlLibraryPanel;
import com.oracle.javafx.scenebuilder.menu.action.AbstractToggleViewVisibilityAction;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
@MenuItemAttachment(
        id = ToggleControlLibraryVisibilityAction.MENU_ID,
        targetMenuId = ToggleControlLibraryVisibilityAction.GOTO_CODE_MENU_ID,
        label = "#this.getTitle()",
        positionRequest = PositionRequest.AsNextSibling)
@Accelerator(accelerator = "CTRL+4")
@Accelerator(accelerator = "CTRL+Numpad 4")
public class ToggleControlLibraryVisibilityAction extends AbstractToggleViewVisibilityAction {

    /**
     * This is the menu id where the insertion will take place
     * It is a copy to prevent adding a direct dependecy to FocusCodeTabAction in  scenebuilder.ext.inspector module
     * but is it the right choice, i'm wondering ?
     */
    // TODO reevaluate adding a direct dependency
    public final static String GOTO_CODE_MENU_ID = "gotoCodeMenuItem"; //NOCHECK

    public final static String MENU_ID = "toggleControlLibraryVisibilityMenuItem"; //NOCHECK

    public ToggleControlLibraryVisibilityAction(
            ActionExtensionFactory extensionFactory,
            DockViewController dockViewController,
            ControlLibraryPanel controlLibrary) {
        super(extensionFactory, dockViewController);
        setView(controlLibrary);
    }

    public String getTitle() {
        final String title;
        if (getView().isVisible() && !getView().getParentDock().isMinimized()) {
            title = "menu.title.hide.library.panel";
        } else {
            title = "menu.title.show.library.panel";
        }
        return title;
    }

}