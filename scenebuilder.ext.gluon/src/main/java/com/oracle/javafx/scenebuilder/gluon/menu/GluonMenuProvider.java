/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.gluon.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemePreference;
import com.oracle.javafx.scenebuilder.gluon.controller.UpdateController;
import com.oracle.javafx.scenebuilder.gluon.preferences.document.GluonSwatchPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.GluonSwatchPreference.GluonSwatch;
import com.oracle.javafx.scenebuilder.gluon.theme.GluonThemesList;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class GluonMenuProvider implements MenuItemProvider {

    private final static String THEME_MENU_ID = "themeMenu";
    private final static String ABOUT_MENU_ID = "aboutMenuItem";
    private final GluonSwatchPreference gluonSwatchPreference;
    private final ThemePreference themePreference;
    private final SceneBuilderBeanFactory context;
    private final UpdateController updateController;

    public GluonMenuProvider(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired UpdateController updateController,
            @Autowired @Lazy ThemePreference themePreference,
            @Autowired @Lazy GluonSwatchPreference gluonSwatchPreference) {
        this.context = context;
        this.gluonSwatchPreference = gluonSwatchPreference;
        this.themePreference = themePreference;
        this.updateController = updateController;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(
                new GluonThemeAttachment(),
                new GluonCheckUpdateAttachment());
    }

    public class GluonCheckUpdateAttachment implements MenuItemAttachment {

        private MenuItem checkMenu = null;

        public GluonCheckUpdateAttachment() {
        }

        @Override
        public String getTargetId() {
            return ABOUT_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsPreviousSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (checkMenu != null) {
                return checkMenu;
            }

            checkMenu = new Menu(I18N.getString("menu.title.check.updates"));
            checkMenu.setOnAction((e) -> updateController.checkUpdates());

            return checkMenu;
        }
    }

    public class GluonThemeAttachment implements MenuItemAttachment {

        private Menu gluonSwatchMenu = null;

        public GluonThemeAttachment() {
        }

        @Override
        public String getTargetId() {
            return THEME_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (gluonSwatchMenu != null) {
                return gluonSwatchMenu;
            }

            gluonSwatchMenu = new Menu(I18N.getString("menu.title.gluon.swatch"));

            ToggleGroup tg = new ToggleGroup();

            boolean disabled = isMenuDisabled();

            Arrays.stream(GluonSwatch.values()).forEach(g -> {
                RadioMenuItem mi = new RadioMenuItem(g.toString());
                mi.setToggleGroup(tg);
                mi.setUserData(g);
                mi.setGraphic(g.createGraphic());
                mi.setDisable(disabled);
                mi.setSelected(gluonSwatchPreference.getValue() == g);
                mi.setOnAction((e) -> gluonSwatchPreference.setValue((GluonSwatch) mi.getUserData()));
                gluonSwatchMenu.getItems().add(mi);
            });

            gluonSwatchPreference.getObservableValue().addListener((ob, o, n) -> {
                updateMenu();
                context.getBean(ApplyCssContentAction.class).extend().checkAndPerform();
            });
            themePreference.getObservableValue().addListener((ob, o, n) -> {
                updateMenu();
            });
            return gluonSwatchMenu;
        }

        private void updateMenu() {
            boolean disable = isMenuDisabled();
            gluonSwatchMenu.getItems().stream().filter(mi -> RadioMenuItem.class.isAssignableFrom(mi.getClass()))
                    .forEach(mi -> {
                        RadioMenuItem rmi = (RadioMenuItem) mi;
                        rmi.setDisable(disable);
                        rmi.setSelected(gluonSwatchPreference.getValue() == mi.getUserData());
                    });
        }
    }

    public boolean isMenuDisabled() {
        Class<? extends Theme> theme = themePreference.getValue();
        return theme != GluonThemesList.GluonMobileLight.class && theme != GluonThemesList.GluonMobileDark.class;
    }
}
