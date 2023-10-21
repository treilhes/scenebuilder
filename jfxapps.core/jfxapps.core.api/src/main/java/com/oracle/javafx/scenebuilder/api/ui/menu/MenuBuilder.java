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
package com.oracle.javafx.scenebuilder.api.ui.menu;

import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.shortcut.Accelerators;
import com.oracle.javafx.scenebuilder.api.ui.AbstractCommonUiController;
import com.oracle.javafx.scenebuilder.api.ui.dock.View;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Window;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;


@Window
public class MenuBuilder {

    private static final Logger logger = LoggerFactory.getLogger(MenuBuilder.class);

    private final ActionFactory actionFactory;
    private final Accelerators acceleratorsController;
    private final SbContext context;

    public MenuBuilder(
            ActionFactory actionFactory,
            Accelerators acceleratorsController,
            SbContext context) {
        super();
        this.actionFactory = actionFactory;
        this.acceleratorsController = acceleratorsController;
        this.context = context;
    }

    public InternalMenuBuilder menu() {
        return new InternalMenuBuilder();
    }
    public MenuItemBuilder<MenuItem> menuItem() {
        return new MenuItemBuilder<MenuItem>();
    }
    public MenuItemBuilder<RadioMenuItem> radioMenuItem() {
        return new MenuItemBuilder<RadioMenuItem>().withMenuItem(new RadioMenuItem());
    }
    public SeparatorMenuItemBuilder separator() {
        return new SeparatorMenuItemBuilder();
    }

    private String getText(String text, Object rootContext) {
        final boolean isExpression = context.isExpression(text);
        String outputText = null;
        if (isExpression) {
            outputText = context.parseExpression(text, rootContext).toString();
        } else {
            outputText = text;
        }
        return I18N.getStringOrDefault(outputText, outputText);
    }

    public class MenuItemBuilder<T extends MenuItem> {
        private T item = null;
        private String title = null;
        private Function<Action, String> titleFunction = null;
        private String menuId = null;
        private Action action = null;
        private Class<? extends Action> actionClass = null;
        private Class<? extends View> viewClass = null;
        private Class<? extends ToggleGroup> toggleClass;

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

        public MenuItemBuilder<T> withViewClass(Class<? extends View> viewClass) {
            this.viewClass = viewClass;
            return this;
        }

        public MenuItemBuilder<T> withToggleClass(Class<? extends ToggleGroup> toggleClass) {
            this.toggleClass = toggleClass;
            return this;
        }

        public T build() {

            ToggleGroup toggle = null;
            if (toggleClass != null && toggleClass != ToggleGroup.class) {
                toggle = context.getBean(toggleClass);
            }

            if (item == null) {
                if (toggle != null) {
                    RadioMenuItem rmi = new RadioMenuItem();
                    rmi.setToggleGroup(toggle);
                    item = (T)rmi;
                } else {
                    item = (T)new MenuItem();
                }
            } else {
                if (toggle != null) {
                    RadioMenuItem rmi = (RadioMenuItem)item;
                    rmi.setToggleGroup(toggle);
                }
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

            EventHandler<Event> updateMenuHandler = ev -> {
                item.setDisable(!action.canPerform());
                if (customTitle) {
                    item.setText(titleFunction == null ? getText(text, action) : titleFunction.apply(action));
                }
                item.setMnemonicParsing(item.getText().contains("_"));
            };

            item.parentMenuProperty().addListener((ob, o, n) ->{
                if (n != null) {
                    n.addEventHandler(Menu.ON_SHOWING, updateMenuHandler::handle);
                }
            });

            item.setOnMenuValidation(updateMenuHandler::handle);

            if (viewClass != null) {
                if (AbstractCommonUiController.class.isAssignableFrom(viewClass)) {
                    acceleratorsController.bind(action, item, (Class<? extends AbstractCommonUiController>)viewClass);
                } else {
                    logger.error("The view {} does not inherit from {}. View accelerators discarded !", viewClass, AbstractCommonUiController.class);
                }
            } else {
                acceleratorsController.bind(action, item);
            }


            return item;
        }
    }

    public class InternalMenuBuilder {
        Menu item = null;
        String title = null;
        String menuId = null;

        public InternalMenuBuilder withMenu(Menu item) {
            this.item = item;
            return this;
        }
        public InternalMenuBuilder withTitle(String title) {
            this.title = title;
            return this;
        }
        public InternalMenuBuilder withId(String menuId) {
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
