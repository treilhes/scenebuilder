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
package com.oracle.javafx.scenebuilder.api.menubar;

import java.util.UUID;
import java.util.function.BooleanSupplier;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public interface MenuItemAttachment {

    String getTargetId();

    PositionRequest getPositionRequest();

    MenuItem getMenuItem();

    static MenuItemAttachment separator(String targetId, PositionRequest positionRequest) {
        return separator(targetId, positionRequest, UUID.randomUUID().toString());
    }

    static MenuItemAttachment separator(String targetId, PositionRequest positionRequest, String separatorId) {
        return bindSeparator(new SeparatorMenuItem(), targetId, positionRequest, separatorId);
    }

    static MenuItemAttachment bindSeparator(SeparatorMenuItem separator, String targetId,
            PositionRequest positionRequest) {
        return bindSeparator(separator, targetId, positionRequest, null);
    }

    static MenuItemAttachment bindSeparator(SeparatorMenuItem separator, String targetId,
            PositionRequest positionRequest, String separatorId) {
        return new MenuItemAttachment() {

            @Override
            public String getTargetId() {
                return targetId;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return positionRequest;
            }

            @Override
            public MenuItem getMenuItem() {
                SeparatorMenuItem mi = separator;
                if (separatorId != null) {
                    mi.setId(separatorId);
                }
                return mi;
            }
        };
    }

    static MenuItemAttachment single(String targetId, PositionRequest positionRequest, String titleKey, String menuId,
            ActionFactory actionFactory, Class<? extends AbstractAction> actionClass) {
        return single(targetId, positionRequest, titleKey, menuId, (e) -> actionFactory.create(actionClass).perform(),
                () -> actionFactory.create(actionClass).canPerform());
    }

    static MenuItemAttachment single(String targetId, PositionRequest positionRequest, String titleKey, String menuId,
            EventHandler<ActionEvent> menuAction, BooleanSupplier enableStateSupplier) {
        return bindSingle(new MenuItem(), targetId, positionRequest, titleKey, menuId, menuAction, enableStateSupplier);
    }

    static MenuItemAttachment bindSingle(MenuItem menuItem, String targetId, PositionRequest positionRequest,
            String titleKey, String menuId, ActionFactory actionFactory, Class<? extends AbstractAction> actionClass) {
        return bindSingle(menuItem, targetId, positionRequest, titleKey, menuId,
                (e) -> actionFactory.create(actionClass).perform(),
                () -> actionFactory.create(actionClass).canPerform());
    }

    static MenuItemAttachment bindSingle(MenuItem menuItem, String targetId, PositionRequest positionRequest,
            String titleKey, String menuId, EventHandler<ActionEvent> menuAction, BooleanSupplier enableStateSupplier) {
        return new MenuItemAttachment() {
            private MenuItem menu = null;

            @Override
            public String getTargetId() {
                return targetId;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return positionRequest;
            }

            @Override
            public MenuItem getMenuItem() {
                if (menu != null) {
                    return menu;
                }

                MenuItem newMenu = menuItem;
                newMenu.setText(I18N.getString(titleKey) + "X");
                newMenu.setId(menuId);
                newMenu.setOnAction(menuAction);

                if (enableStateSupplier != null) {
                    newMenu.setDisable(!enableStateSupplier.getAsBoolean());
                    newMenu.setOnMenuValidation((e) -> newMenu.setDisable(!enableStateSupplier.getAsBoolean()));
                }

                menu = newMenu;
                return menu;
            }
        };
    }

    static MenuItemAttachment toggle(String targetId, PositionRequest positionRequest, String titleOnKey,
            String titleOffKey, String menuId, ActionFactory actionFactory, Class<? extends AbstractAction> actionClass,
            BooleanSupplier onOffStateSupplier) {
        return toggle(targetId, positionRequest, titleOnKey, titleOffKey, menuId,
                (e) -> actionFactory.create(actionClass).perform(), onOffStateSupplier,
                () -> actionFactory.create(actionClass).canPerform());
    }

    static MenuItemAttachment toggle(String targetId, PositionRequest positionRequest, String titleOnKey,
            String titleOffKey, String menuId, EventHandler<ActionEvent> menuAction,
            BooleanSupplier onOffStateSupplier) {
        return toggle(targetId, positionRequest, titleOnKey, titleOffKey, menuId, menuAction, onOffStateSupplier, null);
    }

    static MenuItemAttachment toggle(String targetId, PositionRequest positionRequest, String titleOnKey,
            String titleOffKey, String menuId, EventHandler<ActionEvent> menuAction, BooleanSupplier onOffStateSupplier,
            BooleanSupplier enableStateSupplier) {
        return bindToggle(new MenuItem(), targetId, positionRequest, titleOnKey, titleOffKey, menuId, menuAction, onOffStateSupplier, enableStateSupplier);
    }

    static MenuItemAttachment bindToggle(MenuItem menuItem, String targetId, PositionRequest positionRequest,
            String titleOnKey, String titleOffKey, String menuId, ActionFactory actionFactory,
            Class<? extends AbstractAction> actionClass, BooleanSupplier onOffStateSupplier) {
        return bindToggle(menuItem, targetId, positionRequest, titleOnKey, titleOffKey, menuId,
                (e) -> actionFactory.create(actionClass).perform(), onOffStateSupplier,
                () -> actionFactory.create(actionClass).canPerform());
    }

    static MenuItemAttachment bindToggle(MenuItem menuItem, String targetId, PositionRequest positionRequest,
            String titleOnKey, String titleOffKey, String menuId, EventHandler<ActionEvent> menuAction,
            BooleanSupplier onOffStateSupplier) {
        return bindToggle(menuItem, targetId, positionRequest, titleOnKey, titleOffKey, menuId, menuAction,
                onOffStateSupplier, null);
    }

    static MenuItemAttachment bindToggle(MenuItem menuItem, String targetId, PositionRequest positionRequest,
            String titleOnKey, String titleOffKey, String menuId, EventHandler<ActionEvent> menuAction,
            BooleanSupplier onOffStateSupplier, BooleanSupplier enableStateSupplier) {
        return new MenuItemAttachment() {
            private MenuItem menu = null;

            @Override
            public String getTargetId() {
                return targetId;
            }

            @Override
            public PositionRequest getPositionRequest() {
                return positionRequest;
            }

            @Override
            public MenuItem getMenuItem() {
                if (menu != null) {
                    return menu;
                }

                MenuItem newMenu = menuItem;
                newMenu.setText(getTitle());
                newMenu.setId(menuId);
                newMenu.setOnAction(menuAction);

                if (enableStateSupplier != null) {
                    newMenu.setDisable(!enableStateSupplier.getAsBoolean());
                }

                newMenu.setOnMenuValidation((e) -> {
                    newMenu.setText(getTitle());
                    if (enableStateSupplier != null) {
                        newMenu.setDisable(!enableStateSupplier.getAsBoolean());
                    }
                });

                menu = newMenu;
                return menu;
            }

            public String getTitle() {
                if (onOffStateSupplier == null) {
                    return I18N.getString(titleOffKey);
                }
                boolean on = onOffStateSupplier.getAsBoolean();

                if (on) {
                    return I18N.getString(titleOnKey);
                } else {
                    return I18N.getString(titleOffKey);
                }

            }
        };
    }
}
