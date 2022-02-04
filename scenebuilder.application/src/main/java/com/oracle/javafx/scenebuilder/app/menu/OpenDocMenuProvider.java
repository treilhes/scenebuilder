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
package com.oracle.javafx.scenebuilder.app.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.app.action.ShowDocumentationAction;

import javafx.scene.control.MenuItem;

/**
 * @deprecated {@link ShowDocumentationAction} now use {@link com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment} annotation
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@Deprecated(forRemoval = true)
public class OpenDocMenuProvider implements MenuItemProvider {

    private final static String HELP_MENU_ID = "helpMenu"; //NOCHECK
    private final static String DOCUMENTATION_MENU_ID = "documentationMenu"; //NOCHECK

    private final ActionFactory actionFactory;

    public OpenDocMenuProvider(
            @Autowired ActionFactory actionFactory
            ) {
        this.actionFactory = actionFactory;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        MenuItemAttachment about = MenuItemAttachment.single(HELP_MENU_ID, PositionRequest.AsLastChild,
                "menu.title.scene.builder.help", DOCUMENTATION_MENU_ID, actionFactory, ShowDocumentationAction.class);

        about = new MenuItemAttachment() {
            private MenuItem menu = null;

            @Override
            public String getTargetId() {
                return HELP_MENU_ID;
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

                AbstractAction action = actionFactory.create(ShowDocumentationAction.class);

                MenuItem newMenu = new MenuItem();
                newMenu.setText(I18N.getString("menu.title.scene.builder.help") + "X");
                newMenu.setId(DOCUMENTATION_MENU_ID);
                newMenu.setOnAction((e) -> action.perform());
                newMenu.setDisable(!action.canPerform());
                newMenu.setOnMenuValidation((e) -> newMenu.setDisable(!action.canPerform()));

                menu = newMenu;
                return menu;
            }
        };

        return Arrays.asList(about);
    }

}
