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
package com.oracle.javafx.scenebuilder.tools.action.wrap;

import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.DSelectionGroupFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.editor.fxml.actions.SendBackwardAction;

import javafx.scene.control.Menu;

/**
 *
 */
@ApplicationInstanceSingleton
public class WrapInMenuItemProvider implements MenuItemProvider, ContextMenuItemProvider {

    public final static String MENU_ID = "wrapInMenu";
    private final static String MENU_LABEL = "menu.title.wrap";

    private final MenuBuilder menuBuilder;

    public WrapInMenuItemProvider(MenuBuilder menuBuilder) {
        super();
        this.menuBuilder = menuBuilder;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        Menu menu = menuBuilder.menu().id(MENU_ID).title(MENU_LABEL).build();
        MenuItemAttachment attachment = MenuItemAttachment.create(menu, DefaultMenu.ARRANGE_MENU_ID, PositionRequest.AsLastChild);
        return List.of(
                attachment,
                MenuItemAttachment.create(menuBuilder.separator().build(), MENU_ID, PositionRequest.AsPreviousSibling)
                );
    }

    @Override
    public List<ContextMenuItemAttachment> contextMenuItems() {
        Menu menu = menuBuilder.menu().id(MENU_ID).title(MENU_LABEL).build();
        ContextMenuItemAttachment attachment = ContextMenuItemAttachment.create(menu, DSelectionGroupFactory.class, SendBackwardAction.MENU_ID, PositionRequest.AsNextSibling);
        return List.of(
                attachment,
                ContextMenuItemAttachment.create(menuBuilder.separator().build(), DSelectionGroupFactory.class, MENU_ID, PositionRequest.AsPreviousSibling)
                );
    }

}
