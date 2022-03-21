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
package com.oracle.javafx.scenebuilder.menu.viewmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.dock.DockViewController;
import com.oracle.javafx.scenebuilder.api.dock.View;
import com.oracle.javafx.scenebuilder.api.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menu.ViewMenuItemProvider;
import com.oracle.javafx.scenebuilder.api.subjects.DockManager;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.core.dock.DockNameHelper;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.save",
        descriptionKey = "action.description.save")

public class MoveToDockAction extends AbstractAction {

    public final static String MENU_ID = "viewMoveToMenu";
    public final static String MENU_ITEM_ID = "viewMoveToMenu-%s";

    private final DocumentManager documentManager;

    private UUID targetDockId;
    private final DockViewController dockViewController;

    public MoveToDockAction(
            ActionExtensionFactory extensionFactory,
            DocumentManager documentManager,
            DockViewController dockViewController) {
        super(extensionFactory);
        this.documentManager = documentManager;
        this.dockViewController = dockViewController;
    }

    /**
     */
    public void setTargetDockId(UUID targetDockId) {
        this.targetDockId = targetDockId;
    }
    @Override
    public boolean canPerform() {
        View view = documentManager.focusedView().get();
        return view != null && view.parentDockProperty().isNotNull().get() && !view.parentDockProperty().get().getId().equals(targetDockId);
    }

    @Override
    public ActionStatus doPerform() {
        View view = documentManager.focusedView().get();
        dockViewController.performDock(view, targetDockId);
        return ActionStatus.DONE;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    public class MenuProvider implements ViewMenuItemProvider {

        private final ActionFactory actionFactory;
        private final MenuBuilder menuBuilder;
        private final DockManager dockManager;
        private final DockNameHelper dockNameHelper;

        public MenuProvider(
                ActionFactory actionFactory,
                MenuBuilder menuBuilder,
                DockManager dockManager,
                DockNameHelper dockNameHelper) {
            super();
            this.actionFactory = actionFactory;
            this.menuBuilder = menuBuilder;
            this.dockManager = dockManager;
            this.dockNameHelper = dockNameHelper;
        }

        @Override
        public List<ViewMenuItemAttachment> menuItems() {
            List<ViewMenuItemAttachment> result = new ArrayList<>();

            Menu menu = menuBuilder.menu().withTitle("view.menu.title.move").withId(MENU_ID).build();
            ViewMenuItemAttachment attachment = ViewMenuItemAttachment
                    .create(menu, ChangeDockTypeAction.MENU_ID, PositionRequest.AsNextSibling, AbstractFxmlViewController.class);
            result.add(attachment);

            dockManager.availableDocks().subscribe(docks -> {
                menu.getItems().clear();
                docks.forEach(dock ->{
                    final String title = dockNameHelper.getName(dock.getId());
                    final String menuId = String.format(MENU_ITEM_ID, title);

                    final MoveToDockAction moveToAction = actionFactory.create(MoveToDockAction.class);
                    moveToAction.setTargetDockId(dock.getId());

                    MenuItem mi = menuBuilder.menuItem()
                            .withId(menuId).withAction(moveToAction)
                            .withTitle(title).build();
                    menu.getItems().add(mi);
                });
            });

            return result;
        }

    }

}