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
package com.oracle.javafx.scenebuilder.ext.menu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeManager;
import com.oracle.javafx.scenebuilder.ext.actions.ApplyCssContentAction;
import com.oracle.javafx.scenebuilder.ext.theme.document.ThemeDocumentPreference;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

@ApplicationInstanceSingleton
public class ThemeMenuProvider implements MenuItemProvider {

    // private final static String FIRST_SEPARATOR_ID = "firstSeparator";
    private final static String FIRST_SEPARATOR_ID = "previewMenu";

    private final I18N i18n;
    private final ThemeManager themeManager;
    private final ThemeDocumentPreference themePreference;
    private final ActionFactory actionFactory;

    //@formatter:off
    public ThemeMenuProvider(
            I18N i18n,
            ActionFactory actionFactory,
            @Lazy ThemeDocumentPreference themePreference,
            ThemeManager themeManager
            ) {
        //@formatter:on
        this.i18n = i18n;
        this.actionFactory = actionFactory;
        this.themeManager = themeManager;
        this.themePreference = themePreference;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(new ThemeAttachment());
    }

    public class ThemeAttachment implements MenuItemAttachment {

        private Menu theme = null;

        public ThemeAttachment() {
        }

        @Override
        public String getTargetId() {
            return FIRST_SEPARATOR_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        @SuppressWarnings("unchecked")
        @Override
        public MenuItem getMenuItem() {

            if (theme != null) {
                return theme;
            }

            theme = new Menu(i18n.getString("menu.title.theme"));
            theme.setId("themeMenu");
            Map<String, List<Theme>> groups = themeManager.getAll().stream()
                    .collect(Collectors.groupingBy(t -> t.getGroup().getName()));

            ToggleGroup tg = new ToggleGroup();

            groups.keySet().stream().sorted().forEach(k -> {

                if (!theme.getItems().isEmpty()) {
                    SeparatorMenuItem sep = new SeparatorMenuItem();
                    sep.setId(k);
                    theme.getItems().add(sep);
                }

                groups.get(k).stream().sorted((t1, t2) -> name(i18n, t1).compareTo(name(i18n, t2))).forEach(t -> {
                    RadioMenuItem mi = new RadioMenuItem(name(i18n, t));
                    mi.setToggleGroup(tg);
                    mi.setSelected(themePreference.getValue() == t.getClass());
                    mi.setUserData(t.getClass());
                    mi.setOnAction((e) -> themePreference.setValue((Class<? extends Theme>) mi.getUserData()));
                    theme.getItems().add(mi);
                });
            });

            themePreference.getObservableValue().addListener((ob, o, n) -> {
                theme.getItems().stream().filter(mi -> RadioMenuItem.class.isAssignableFrom(mi.getClass())).forEach(
                        mi -> ((RadioMenuItem) mi).setSelected(themePreference.getValue() == mi.getUserData()));
                actionFactory.create(ApplyCssContentAction.class).checkAndPerform();
            });
            return theme;
        }

        private String name(I18N i18n, Theme t) {
            return i18n.getStringOrDefault(t.getName(), t.getName());
        }
    }
}
