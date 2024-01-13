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

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.api.ui.dock.Dock;
import com.oracle.javafx.scenebuilder.api.ui.dock.DockType;
import com.oracle.javafx.scenebuilder.api.ui.dock.View;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.menu.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.ViewMenuItemProvider;

import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

@Prototype
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

public class ChangeDockTypeAction extends AbstractAction {

    public final static String MENU_ID = "changeViewTypeMenu";
    public final static String MENU_ITEM_ID = "changeViewTypeMenu-%s";

    private final DocumentManager documentManager;

    private DockType<?> dockType;

    public ChangeDockTypeAction(
            ActionExtensionFactory extensionFactory,
            DocumentManager documentManager) {
        super(extensionFactory);
        this.documentManager = documentManager;
    }

    /**
     * @param dockType
     */
    public void setDockType(DockType<?> dockType) {
        this.dockType = dockType;
    }
    @Override
    public boolean canPerform() {
        View view = documentManager.focusedView().get();
        return view != null && view.parentDockProperty().isNotNull().get();
    }

    @Override
    public ActionStatus doPerform() {
        View view = documentManager.focusedView().get();
        Dock dock = view.parentDockProperty().get();
        dock.dockTypeProperty().set(dockType);
        dock.focusedProperty().set(view);
        return ActionStatus.DONE;
    }

    @ApplicationInstanceSingleton
    public class MenuProvider implements ViewMenuItemProvider {

        private final ActionFactory actionFactory;
        private final MenuBuilder menuBuilder;
        private final List<DockType<?>> dockTypes;

        public MenuProvider(
                ActionFactory actionFactory,
                MenuBuilder menuBuilder,
                List<DockType<?>> dockTypes) {
            super();
            this.actionFactory = actionFactory;
            this.menuBuilder = menuBuilder;
            this.dockTypes = dockTypes;
        }

        @Override
        public List<ViewMenuItemAttachment> menuItems() {
            List<ViewMenuItemAttachment> result = new ArrayList<>();

            Menu menu = menuBuilder.menu().withTitle("view.menu.title.dock").withId(MENU_ID).build();
            ViewMenuItemAttachment attachment = ViewMenuItemAttachment
                    .create(menu, null, PositionRequest.AsFirstChild, AbstractFxmlViewController.class);
            result.add(attachment);

            ToggleGroup typeToggle = new ToggleGroup();

            String target = MENU_ID;
            PositionRequest positionRequest = PositionRequest.AsLastChild;

            for (DockType<?> dockType:dockTypes) {
                final ChangeDockTypeAction changeViewTypeAction = actionFactory.create(ChangeDockTypeAction.class);
                changeViewTypeAction.setDockType(dockType);

                final String title = dockType.getNameKey();
                final String menuId = String.format(MENU_ITEM_ID, title);
                RadioMenuItem mi = new RadioMenuItem();
                mi.setToggleGroup(typeToggle);

                mi = menuBuilder.radioMenuItem().withMenuItem(mi)
                        .withId(menuId).withAction(changeViewTypeAction)
                        .withTitle(title).build();

                ViewMenuItemAttachment subAttachment = ViewMenuItemAttachment
                        .create(mi, target, positionRequest, AbstractFxmlViewController.class);

                target = menuId;
                positionRequest = PositionRequest.AsNextSibling;

                result.add(subAttachment);
            }

            return result;
        }

    }

}