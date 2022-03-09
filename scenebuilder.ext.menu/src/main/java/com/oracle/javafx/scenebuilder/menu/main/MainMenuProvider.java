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
package com.oracle.javafx.scenebuilder.menu.main;

import static com.oracle.javafx.scenebuilder.api.menu.PositionRequest.AsFirstChild;
import static com.oracle.javafx.scenebuilder.api.menu.PositionRequest.AsNextSibling;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.menu.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menu.MenuBarObjectConfigurator;
import com.oracle.javafx.scenebuilder.api.menu.MenuProvider;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;

import javafx.scene.control.Menu;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class MainMenuProvider implements MenuProvider {

    private final MenuBarObjectConfigurator menuBarObjectConfigurator;

    public MainMenuProvider(MenuBarObjectConfigurator menuBarObjectConfigurator) {
        super();
        this.menuBarObjectConfigurator = menuBarObjectConfigurator;
    }


    private MenuAttachment newMenu(String targetId, PositionRequest positionRequest, String menuId, String titleKey) {
        Menu menu = menuBarObjectConfigurator.menu().withId(menuId).withTitle(titleKey).build();
        return MenuAttachment.create(menu, targetId, positionRequest);
    }

    @Override
    public List<MenuAttachment> menus() {
        return Arrays.asList(
                newMenu(null, AsFirstChild, DefaultMenu.FILE_MENU_ID, "menu.title.file"),
                newMenu(DefaultMenu.FILE_MENU_ID, AsNextSibling, DefaultMenu.EDIT_MENU_ID, "menu.title.edit"),
                newMenu(DefaultMenu.EDIT_MENU_ID, AsNextSibling, DefaultMenu.VIEW_MENU_ID, "menu.title.view"),
                //newMenu(DefaultMenu.VIEW_MENU_ID, AsNextSibling, DefaultMenu.INSERT_MENU_ID, "menu.title.insert"),
                newMenu(DefaultMenu.VIEW_MENU_ID, AsNextSibling, DefaultMenu.MODIFY_MENU_ID, "menu.title.modify"),
                newMenu(DefaultMenu.MODIFY_MENU_ID, AsNextSibling, DefaultMenu.ARRANGE_MENU_ID, "menu.title.arrange"),
                newMenu(DefaultMenu.ARRANGE_MENU_ID, AsNextSibling, DefaultMenu.PREVIEW_MENU_ID, "menu.title.preview"),
                newMenu(DefaultMenu.PREVIEW_MENU_ID, AsNextSibling, DefaultMenu.WINDOW_MENU_ID, "menu.title.window"),
                newMenu(DefaultMenu.WINDOW_MENU_ID, AsNextSibling, DefaultMenu.HELP_MENU_ID, "menu.title.help")
                );
    }

}