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
package com.oracle.javafx.scenebuilder.fs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.action.Action;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.fs.FileSystemActionFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

@ApplicationInstancePrototype
public class OpenRecentProvider implements MenuItemProvider {

    public final static String MENU_ID = "openRecentMenu";
    public final static String CLEARRECENT_MENU_ID = "clearRecentMenu";

    private I18N i18n;
    private final MenuBuilder builder;
    private final FileSystem fileSystem;
    private final FileSystemActionFactory fileSystemActionFactory;

    public OpenRecentProvider(
            I18N i18n,
            MenuBuilder menuBuilder,
            FileSystem fileSystem,
            FileSystemActionFactory fileSystemActionFactory
            ) {
        this.i18n = i18n;
        this.builder = menuBuilder;
        this.fileSystem = fileSystem;
        this.fileSystemActionFactory = fileSystemActionFactory;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(new OpenRecentAttachment());
    }

    public class OpenRecentAttachment implements MenuItemAttachment {

        /**
         *
         */
        private static final String CLEAR_RECENT_TITLE = "menu.title.open.recent.clear";
        private static final String OPEN_RECENT_TITLE = "menu.title.open.recent";

        private Menu openRecentMenu;

        public OpenRecentAttachment() {
        }

        @Override
        public String getTargetId() {
            return OpenAction.OPEN_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (openRecentMenu != null) {
                return openRecentMenu;
            }

            openRecentMenu = new Menu();
            openRecentMenu.setId(MENU_ID);
            openRecentMenu.setText(i18n.getStringOrDefault(OPEN_RECENT_TITLE, OPEN_RECENT_TITLE));
            openRecentMenu.setOnShowing(t -> updateOpenRecentMenuItems());
            openRecentMenu.getItems().add(new MenuItem("lazyUpdateOpenRecentMenuItemsTrigger")); //NOCHECK
            return openRecentMenu;
        }

        //TODO this action must not administrates the recentItemsPreference, need to delegate to some core object like filesystem
        private void updateOpenRecentMenuItems() {

            final List<MenuItem> menuItems = new ArrayList<>();

            fileSystem.cleanupRecentItems();
            final List<String> recentItems = fileSystem.getRecentItems();

            final MenuItem clearMenuItem = builder.menuItem()
                    .title(CLEAR_RECENT_TITLE)
                    .id(CLEARRECENT_MENU_ID)
                    .action(fileSystemActionFactory.clearRecentItems()).build();

            if (!recentItems.isEmpty()) {

                final Map<String, Integer> recentItemsNames = new HashMap<>();

                // First pass to build recentItemsNames and recentItemsToRemove
                for (String recentItem : recentItems) {
                    final File recentItemFile = new File(recentItem);
                    if (recentItemFile.exists()) {
                        final String name = recentItemFile.getName();
                        if (recentItemsNames.containsKey(name)) {
                            recentItemsNames.replace(name, recentItemsNames.get(name) + 1);
                        } else {
                            recentItemsNames.put(name, 1);
                        }
                    }
                }
                // Second pass to build MenuItems
                for (String recentItem : recentItems) {
                    final File recentItemFile = new File(recentItem);
                    if (recentItemFile.exists()) {
                        final String name = recentItemFile.getName();
                        assert recentItemsNames.keySet().contains(name);
                        final MenuItem mi;
                        Action action = fileSystemActionFactory.openFiles(List.of(recentItemFile));
                        if (recentItemsNames.get(name) > 1) {
                            // Several files with same name : display full path
                            mi = builder.menuItem().title(recentItem).action(action).build();
                        } else {
                            // Single file with this name : display file name only
                            assert recentItemsNames.get(name) == 1;
                            mi = builder.menuItem().title(name).action(action).build();
                        }
                        mi.setMnemonicParsing(false);
                        menuItems.add(mi);
                    }
                }

                menuItems.add(new SeparatorMenuItem());
                menuItems.add(clearMenuItem);
            }

            openRecentMenu.getItems().setAll(menuItems);
        }

    }

}
