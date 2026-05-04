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
package com.oracle.javafx.scenebuilder.menu.main.view;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;

import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.DockActionFactory;
import com.treilhes.jfxplace.core.api.ui.controller.dock.DockViewController;
import com.treilhes.jfxplace.core.api.ui.controller.dock.View;
import com.treilhes.jfxplace.core.api.ui.controller.dock.ViewAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuBuilder;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuItemAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuItemProvider;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@ApplicationInstanceSingleton
public class ViewMenuProvider implements MenuItemProvider {

    private static final Logger logger = LoggerFactory.getLogger(ViewMenuProvider.class);

    public static final String MENU_ID = DefaultMenu.View.SHOW_VIEWS_ID;
    public static final String TARGET_MENU_ID = DefaultMenu.View.ID;

    private final DockViewController dockViewController;
    private final MenuBuilder menuBuiler;

    private final I18N i18n;
    private final DockActionFactory dockActionFactory;

    public ViewMenuProvider(
            I18N i18n,
            @Lazy DockViewController dockViewController,
            MenuBuilder menuBuiler,
            DockActionFactory dockActionFactory
            ) {
        this.i18n = i18n;
        this.dockViewController = dockViewController;
        this.menuBuiler = menuBuiler;
        this.dockActionFactory = dockActionFactory;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(new ViewsAttachment());
    }

    public class ViewsAttachment implements MenuItemAttachment {

        private Menu menu = null;

        @Override
        public String getTargetId() {
            return TARGET_MENU_ID;
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

            menu = new Menu(i18n.getString("menu.title.show.view"));
            menu.setId(MENU_ID);

            dockViewController.getViewItems().stream()
            .filter(Predicate.not(ViewAttachment::isDebug))
            .sorted(Comparator.comparing(view -> i18n.getStringOrDefault(view.getName(), view.getName())))
            .forEach(vi -> {


                var action = dockActionFactory.toggleViewVisibility(vi.getViewClass());

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