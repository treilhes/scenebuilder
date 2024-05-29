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
package com.oracle.javafx.scenebuilder.sb.menu;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.core.action.editor.KeyboardModifier;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.sb.menu.controller.SceneBuilderMenuController;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

@ApplicationInstanceSingleton
public class SceneBuilderMenuProvider implements MenuItemProvider {

    private final static String FILE_MENU_ID = "fileMenu";
    private final static String CLOSE_WINDOW_ID = "closeMenuItem";
    private final static String EXIT_ID = "exitMenuItem";

    private final SceneBuilderMenuController sceneBuilderMenuController;

    public SceneBuilderMenuProvider(
            @Lazy SceneBuilderMenuController sceneBuilderMenuController) {
        this.sceneBuilderMenuController = sceneBuilderMenuController;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        List<MenuItemAttachment> menus = new ArrayList<>();
        menus.add(new CloseWindowAttachment());

        if (!JfxAppsPlatform.IS_MAC) {
            menus.add(new ExitSceneBuilderAttachment());
        }
        return menus;
    }

    public class CloseWindowAttachment implements MenuItemAttachment {

        private MenuItem menu = null;

        public CloseWindowAttachment() {
        }

        @Override
        public String getTargetId() {
            return FILE_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.close"));
            menu.setId(CLOSE_WINDOW_ID);
            menu.setAccelerator(new KeyCodeCombination(KeyCode.W, KeyboardModifier.control()));
            menu.setOnAction((e) -> sceneBuilderMenuController.performCloseCurrentDocument());
            return menu;
        }
    }

    public class ExitSceneBuilderAttachment implements MenuItemAttachment {

        private MenuItem menu = null;

        public ExitSceneBuilderAttachment() {
        }

        @Override
        public String getTargetId() {
            return FILE_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.quit"));
            menu.setId(EXIT_ID);
            menu.setOnAction((e) -> sceneBuilderMenuController.performCloseSceneBuilder());

            return menu;
        }
    }
}
