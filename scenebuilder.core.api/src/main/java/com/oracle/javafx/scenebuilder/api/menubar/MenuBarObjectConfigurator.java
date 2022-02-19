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
package com.oracle.javafx.scenebuilder.api.menubar;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerators;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;


@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class MenuBarObjectConfigurator {

    private static final Logger logger = LoggerFactory.getLogger(MenuBarObjectConfigurator.class);

    private final ActionFactory actionFactory;
    private final Accelerators acceleratorsController;
    private final SceneBuilderBeanFactory context;

    public MenuBarObjectConfigurator(
            ActionFactory actionFactory,
            Accelerators acceleratorsController,
            SceneBuilderBeanFactory context) {
        super();
        this.actionFactory = actionFactory;
        this.acceleratorsController = acceleratorsController;
        this.context = context;
    }

    public MenuBuilder menu() {
        return new MenuBuilder();
    }
    public MenuItemBuilder<MenuItem> menuItem() {
        return new MenuItemBuilder<MenuItem>();
    }
    public MenuItemBuilder<RadioMenuItem> radioMenuItem() {
        return new MenuItemBuilder<RadioMenuItem>();
    }
    public SeparatorMenuItemBuilder separator() {
        return new SeparatorMenuItemBuilder();
    }

    private String getText(String text, Object rootContext) {
        final boolean isExpression = context.isExpression(text);
        if (isExpression) {
            return context.parseExpression(text, rootContext).toString();
        } else {
            return text;
        }
    }

    public class MenuItemBuilder<T extends MenuItem> {
        T item = null;
        String title = null;
        Function<Action, String> titleFunction = null;
        String menuId = null;
        Action action = null;
        Class<? extends Action> actionClass = null;

        public MenuItemBuilder<T> withMenuItem(T item) {
            this.item = item;
            return this;
        }
        public MenuItemBuilder<T> withTitle(String title) {
            this.title = title;
            if (title != null) {
                titleFunction = null;
            }
            return this;
        }
        public MenuItemBuilder<T> withTitleFunction(Function<Action, String> titleFunction) {
            this.titleFunction = titleFunction;
            if (titleFunction != null) {
                title = null;
            }
            return this;
        }
        public MenuItemBuilder<T> withId(String menuId) {
            this.menuId = menuId;
            return this;
        }
        public MenuItemBuilder<T> withAction(Action action) {
            this.action = action;
            if (action != null) {
                actionClass = null;
            }
            return this;
        }
        public MenuItemBuilder<T> withActionClass(Class<? extends Action> actionClass) {
            this.actionClass = actionClass;
            if (actionClass != null) {
                action = null;
            }
            return this;
        }

        public T build() {
            if (item == null) {
                item = (T)new MenuItem();
            }

            if (actionClass != null) {
                action = actionFactory.create(actionClass);
            }

            final boolean customTitle = title != null || titleFunction != null;

            final String text = title == null ? "" : I18N.getStringOrDefault(title, title);

            item.setId(menuId);

            if (customTitle) {
                item.setText(titleFunction == null ? getText(text, action) : titleFunction.apply(action));
            }

            item.setMnemonicParsing(item.getText().contains("_"));

            item.setOnAction((e) -> {
                action.perform();
                if (customTitle) {
                    item.setText(titleFunction == null ? getText(text, action) : titleFunction.apply(action));
                }
                item.setMnemonicParsing(item.getText().contains("_"));
            });
            item.setDisable(!action.canPerform());

            item.parentMenuProperty().addListener((ob, o, n) ->{
                if (n != null) {
                    n.addEventHandler(Menu.ON_SHOWING, ev -> {
                        item.setDisable(!action.canPerform());
                        if (customTitle) {
                            item.setText(titleFunction == null ? getText(text, action) : titleFunction.apply(action));
                        }
                        item.setMnemonicParsing(item.getText().contains("_"));
                    });
                }
            });

            acceleratorsController.bind(action, item);

            return item;
        }
    }

    public class MenuBuilder {
        Menu item = null;
        String title = null;
        String menuId = null;

        public MenuBuilder withMenu(Menu item) {
            this.item = item;
            return this;
        }
        public MenuBuilder withTitle(String title) {
            this.title = title;
            return this;
        }
        public MenuBuilder withId(String menuId) {
            this.menuId = menuId;
            return this;
        }

        public Menu build() {
            if (item == null) {
                item = new Menu();
            }

            final String text = title == null ? "" : I18N.getStringOrDefault(title, title);

            item.setId(menuId);
            item.setText(text);
            item.setMnemonicParsing(item.getText().contains("_"));

            return item;
        }
    }

    public class SeparatorMenuItemBuilder {
        SeparatorMenuItem item = null;
        String menuId = null;

        public SeparatorMenuItemBuilder withSeparatorMenuItem(SeparatorMenuItem item) {
            this.item = item;
            return this;
        }
        public SeparatorMenuItemBuilder withId(String menuId) {
            this.menuId = menuId;
            return this;
        }

        public SeparatorMenuItem build() {
            if (item == null) {
                item = new SeparatorMenuItem();
            }
            item.setId(menuId);

            return item;
        }
    }
}
