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
package com.oracle.javafx.scenebuilder.editor.fxml.actions;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.job.editor.UsePredefinedSizeJob;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.set.root.size", descriptionKey = "action.description.set.root.size")
public class SetRootSizeAction extends AbstractAction {

    private final UsePredefinedSizeJob.Factory usePredefinedSizeJobFactory;
    private final JobManager jobManager;

    private Size size;

    public SetRootSizeAction(
            ActionExtensionFactory extensionFactory,
            JobManager jobManager,
            UsePredefinedSizeJob.Factory usePredefinedSizeJobFactory) {
        super(extensionFactory);
        this.jobManager = jobManager;
        this.usePredefinedSizeJobFactory = usePredefinedSizeJobFactory;
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

        final AbstractJob job = usePredefinedSizeJobFactory.getJob(size);
        return job.isExecutable();
    }

    @Override
    public ActionStatus doPerform() {
        final AbstractJob job = usePredefinedSizeJobFactory.getJob(size);
        jobManager.push(job);
        return ActionStatus.DONE;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
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

                menu = menuBuilder.menu().withId(SET_ROOT_SIZE_MENU_ID).withTitle("menu.title.size").build();

                for (Size size : Size.values()) {
                    if (size == Size.SIZE_DEFAULT || size == Size.SIZE_PREFERRED) {
                        continue;
                    }

                    SetRootSizeAction action = actionFactory.create(SetRootSizeAction.class);
                    action.setSize(size);
                    MenuItem mi = menuBuilder.menuItem().withTitle(size.toString()).withAction(action).build();
                    menu.getItems().add(mi);
                }

                return menu;
            }
        }
    }
}