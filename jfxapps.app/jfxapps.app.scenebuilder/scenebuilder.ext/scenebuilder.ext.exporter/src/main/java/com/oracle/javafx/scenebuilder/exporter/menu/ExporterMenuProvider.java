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
package com.oracle.javafx.scenebuilder.exporter.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.exporter.controller.ExporterMenuController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@ApplicationInstanceSingleton
public class ExporterMenuProvider implements MenuItemProvider {

    private final static String IMPORT_MENU_ID = "importMenu";
    private final static String EXPORT_MENU_ID = "exportMenu";
    private final static String SELECTION_EXPORT_MENU_ID = "selectionExportMenu";
    private final static String SCENE_EXPORT_MENU_ID = "sceneExportMenu";

    private final ExporterMenuController exporterMenuController;
    private final Selection selection;
    private final I18N i18n;

    public ExporterMenuProvider(
            I18N i18n,
            @Autowired  @Lazy ExporterMenuController exporterMenuController,
            @Autowired  @Lazy Selection selection
            ) {
        this.i18n = i18n;
        this.exporterMenuController = exporterMenuController;
        this.selection = selection;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(new ExporteMenuAttachment());
    }

    public class ExporteMenuAttachment implements MenuItemAttachment {

        private MenuItem menu = null;

        public ExporteMenuAttachment() {
        }

        @Override
        public String getTargetId() {
            return IMPORT_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            Menu exportMenu = new Menu(i18n.getString("menu.title.export"));
            exportMenu.setId(EXPORT_MENU_ID);

            MenuItem selectionMenu = new MenuItem(i18n.getString("menu.title.export.selection"));
            selectionMenu.setId(SELECTION_EXPORT_MENU_ID);
            selectionMenu.setOnAction((e) -> exporterMenuController.performExportSelection());
            selectionMenu.setDisable(!exporterMenuController.hasSelectionExportFormat());

            MenuItem sceneMenu = new MenuItem(i18n.getString("menu.title.export.scene"));
            sceneMenu.setId(SCENE_EXPORT_MENU_ID);
            sceneMenu.setOnAction((e) -> exporterMenuController.performExportScene());
            sceneMenu.setDisable(!exporterMenuController.hasSceneExportFormat());

            exportMenu.getItems().add(sceneMenu);
            exportMenu.getItems().add(selectionMenu);
            exportMenu.setOnShowing((e) -> selectionMenu.setDisable(selection.getGroup() == null));

            menu = exportMenu;
            return menu;
        }
    }
}
