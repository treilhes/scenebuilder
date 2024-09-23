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

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.ui.DockActionFactory;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockType;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenuItemProvider;

import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

@ApplicationInstanceSingleton
public class ChangeDockTypeViewMenuProvider implements ViewMenuItemProvider {

    public final static String MENU_ID = "changeViewTypeMenu";
    public final static String MENU_ITEM_ID = "changeViewTypeMenu-%s";

    private final DockActionFactory dockActionFactory;
    private final MenuBuilder menuBuilder;
    private final List<DockType<?>> dockTypes;

    public ChangeDockTypeViewMenuProvider(
            DockActionFactory dockActionFactory,
            MenuBuilder menuBuilder,
            List<DockType<?>> dockTypes) {
        super();
        this.dockActionFactory = dockActionFactory;
        this.menuBuilder = menuBuilder;
        this.dockTypes = dockTypes;
    }

    @Override
    public List<ViewMenuItemAttachment> menuItems() {
        List<ViewMenuItemAttachment> result = new ArrayList<>();

        Menu menu = menuBuilder.menu().title("view.menu.title.dock").id(MENU_ID).build();
        ViewMenuItemAttachment attachment = ViewMenuItemAttachment
                .create(menu, null, PositionRequest.AsFirstChild, AbstractFxmlViewController.class);
        result.add(attachment);

        ToggleGroup typeToggle = new ToggleGroup();

        String target = MENU_ID;
        PositionRequest positionRequest = PositionRequest.AsLastChild;

        for (DockType<?> dockType:dockTypes) {
            final var changeViewTypeAction = dockActionFactory.changeDockType(dockType);

            final String title = dockType.getNameKey();
            final String menuId = String.format(MENU_ITEM_ID, title);
            RadioMenuItem mi = new RadioMenuItem();
            mi.setToggleGroup(typeToggle);

            mi = menuBuilder.radioMenuItem().menuItem(mi)
                    .id(menuId).action(changeViewTypeAction)
                    .title(title).build();

            ViewMenuItemAttachment subAttachment = ViewMenuItemAttachment
                    .create(mi, target, positionRequest, AbstractFxmlViewController.class);

            target = menuId;
            positionRequest = PositionRequest.AsNextSibling;

            result.add(subAttachment);
        }

        return result;
    }

}