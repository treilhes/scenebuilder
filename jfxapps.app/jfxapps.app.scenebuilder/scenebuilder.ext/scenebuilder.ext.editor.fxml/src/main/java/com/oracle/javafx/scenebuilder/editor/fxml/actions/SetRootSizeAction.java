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
package com.oracle.javafx.scenebuilder.editor.fxml.actions;

import java.util.Arrays;
import java.util.List;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.job.Job;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.job.SbJobsFactory;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.set.root.size", descriptionKey = "action.description.set.root.size")
public class SetRootSizeAction extends AbstractAction {

    private final SbJobsFactory sbJobsFactory;
    private final JobManager jobManager;

    private Size size;

    // @formatter:off
    public SetRootSizeAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            SbJobsFactory sbJobsFactory) {
        // @formatter:on
        super(i18n, extensionFactory);
        this.jobManager = jobManager;
        this.sbJobsFactory = sbJobsFactory;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    @Override
    public boolean canPerform() {
        if (size == null) {
            return false;
        }

        final Job job = sbJobsFactory.usePredefinedSize(size);
        return job.isExecutable();
    }

    @Override
    public ActionStatus doPerform() {
        final Job job = sbJobsFactory.usePredefinedSize(size);
        jobManager.push(job);
        return ActionStatus.DONE;
    }

    @ApplicationInstanceSingleton
    public static class MenuProvider implements MenuItemProvider {

        private final static String TARGET_MENU_ID = DefaultMenu.MODIFY_MENU_ID;
        public final static String SET_ROOT_SIZE_MENU_ID = "setRootSize";

        private final ActionFactory actionFactory;
        private final MenuBuilder menuBuilder;

        public MenuProvider(ActionFactory actionFactory, MenuBuilder menuBuilder) {
            super();
            this.actionFactory = actionFactory;
            this.menuBuilder = menuBuilder;
        }

        @Override
        public List<MenuItemAttachment> menuItems() {

            return Arrays.asList(
                    MenuItemAttachment.create(menuBuilder.separator().build(), TARGET_MENU_ID, PositionRequest.AsLastChild),
                    new SetRootSizesMenuItemAttachment());
        }

        public class SetRootSizesMenuItemAttachment implements MenuItemAttachment {

            private Menu menu = null;

            public SetRootSizesMenuItemAttachment() {
                super();
            }

            @Override
            public String getTargetId() {
                return TARGET_MENU_ID;
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

                menu = menuBuilder.menu().id(SET_ROOT_SIZE_MENU_ID).title("menu.title.size").build();

                for (Size size : Size.values()) {
                    if (size == Size.SIZE_DEFAULT || size == Size.SIZE_PREFERRED) {
                        continue;
                    }

                    SetRootSizeAction action = actionFactory.create(SetRootSizeAction.class);
                    action.setSize(size);
                    MenuItem mi = menuBuilder.menuItem().title(size.toString()).action(action).build();
                    menu.getItems().add(mi);
                }

                return menu;
            }
        }
    }
}