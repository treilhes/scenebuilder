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
package com.gluonhq.jfxapps.ext.menu.action.view;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionMeta;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.View;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.DefaultMenu;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.gluonhq.jfxapps.ext.menu.action.AbstractToggleViewVisibilityAction;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Prototype
@ActionMeta(nameKey = "action.name.toggle.view", descriptionKey = "action.description.toggle.view")
public class ToggleViewVisibilityAction extends AbstractToggleViewVisibilityAction {

    private final static Logger logger = LoggerFactory.getLogger(ToggleViewVisibilityAction.class);

    public ToggleViewVisibilityAction(
            ActionExtensionFactory extensionFactory,
            DockViewController dockViewController) {
        super(extensionFactory, dockViewController);
    }

    @ApplicationInstanceSingleton
    public class ViewMenuProvider implements MenuItemProvider {

        public final static String MENU_ID = "showViewsMenuItem";

        private final DockViewController dockViewController;
        private final MenuBuilder menuBuiler;

        private final ActionFactory actionFactory;
        private final JfxAppContext context;

        public ViewMenuProvider(
                ActionFactory actionFactory,
                JfxAppContext context,
                @Lazy DockViewController dockViewController,
                MenuBuilder menuBuiler
                ) {
            this.dockViewController = dockViewController;
            this.menuBuiler = menuBuiler;
            this.actionFactory = actionFactory;
            this.context = context;
        }

        @Override
        public List<MenuItemAttachment> menuItems() {
            return Arrays.asList(new ViewsAttachment());
        }

        public class ViewsAttachment implements MenuItemAttachment {

            private Menu menu = null;

            @Override
            public String getTargetId() {
                return DefaultMenu.VIEW_MENU_ID;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return PositionRequest.AsFirstChild;
            }

            @Override
            public MenuItem getMenuItem() {

                if (menu != null) {
                    return menu;
                }

                menu = new Menu(I18N.getString("menu.title.show.view"));
                menu.setId(MENU_ID);

                dockViewController.getViewItems().stream()
                .filter(Predicate.not(ViewAttachment::isDebug))
                .sorted(Comparator.comparing(view -> I18N.getStringOrDefault(view.getName(), view.getName())))
                .forEach(vi -> {

                    ToggleViewVisibilityAction action = actionFactory.create(ToggleViewVisibilityAction.class);
                    View view = context.getBean(vi.getViewClass());
                    action.setView(view);

                    MenuItem mi = menuBuiler.menuItem().title(vi.getName()).action(action).build();

                    URL icon = vi.getIcon();
                    if (icon == null) {
                        icon = View.VIEW_ICON_MISSING;
                    }
                    try {
                        Image image = new Image(icon.openStream());
                        ImageView imageView = new ImageView(image);
                        mi.setGraphic(imageView);
                    } catch (IOException e) {
                        logger.error("Unable to iconize view {}", vi.getId(), e);
                    }

                    menu.getItems().add(mi);

                });

                return menu;
            }
        }

    }
}