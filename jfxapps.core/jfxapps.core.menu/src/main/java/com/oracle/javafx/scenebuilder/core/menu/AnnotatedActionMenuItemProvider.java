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
package com.oracle.javafx.scenebuilder.core.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

@ApplicationInstanceSingleton
public class AnnotatedActionMenuItemProvider implements MenuItemProvider, MenuProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedActionMenuItemProvider.class);

    private final SbContext context;
    private final MenuBuilder builder;

    private List<MenuItemAttachment> menuItemsCache;
    private List<MenuAttachment> menuCache;

    public AnnotatedActionMenuItemProvider(SbContext context,
            MenuBuilder menuBuilder) {
        super();
        this.context = context;
        this.builder = menuBuilder;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        if (menuItemsCache != null) {
            return menuItemsCache;
        }

        menuItemsCache = context
                .getBeanClassesForAnnotation(
                        com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment.class)
                .stream().map(this::makeMenuItemAttachment).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return menuItemsCache;
    }

    @Override
    public List<MenuAttachment> menus() {
        if (menuCache != null) {
            return menuCache;
        }
        menuCache = context
                .getBeanClassesForAnnotation(com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuAttachment.class)
                .stream().map(this::makeMenuAttachment).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());
        return menuCache;
    }

    @SuppressWarnings("unchecked")
    private List<MenuItemAttachment> makeMenuItemAttachment(Class<?> cls) {

        List<MenuItemAttachment> result = new ArrayList<>();

        try {
            if (!AbstractAction.class.isAssignableFrom(cls)) {
                logger.error("MenuItemAttachment annotation can only be used on Action, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractAction> actionClass = (Class<AbstractAction>) cls;

            final com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment annotation = actionClass
                    .getAnnotation(com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuItemAttachment.class);

            assert annotation != null;

            MenuItem menuItem = builder.menuItem().withId(annotation.id()).withActionClass(actionClass).withTitle(annotation.label()).withToggleClass(annotation.toggleClass()).build();
            MenuItemAttachment menuAttachment = MenuItemAttachment.create(menuItem, annotation.targetMenuId(),
                    annotation.positionRequest());
            result.add(menuAttachment);

            if (annotation.separatorBefore()) {
                SeparatorMenuItem separator = builder.separator().withId("separatorBefore_" + annotation.id()).build();
                MenuItemAttachment attachment = MenuItemAttachment.create(separator, annotation.id(),
                        PositionRequest.AsPreviousSibling);
                result.add(attachment);
            }

            if (annotation.separatorAfter()) {
                SeparatorMenuItem separator = builder.separator().withId("separatorAfter_" + annotation.id()).build();
                MenuItemAttachment attachment = MenuItemAttachment.create(separator, annotation.id(),
                        PositionRequest.AsNextSibling);
                result.add(attachment);
            }
        } catch (Exception e) {
            logger.error("Unable to create a menu attachment for action : {}", cls, e);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private List<MenuAttachment> makeMenuAttachment(Class<?> cls) {

        List<MenuAttachment> result = new ArrayList<>();

        if (!AbstractAction.class.isAssignableFrom(cls)) {
            logger.error("MenuAttachment annotation can only be used on Action, discarding {}", cls.getName());
            return null;
        }
        final Class<AbstractAction> actionClass = (Class<AbstractAction>) cls;

        final com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuAttachment annotation = actionClass
                .getAnnotation(com.oracle.javafx.scenebuilder.api.ui.menu.annotation.MenuAttachment.class);

        assert annotation != null;

        Menu menu = builder.menu().withId(annotation.id()).withTitle(annotation.label()).build();
        MenuAttachment menuAttachment = MenuAttachment.create(menu, annotation.targetMenuId(),
                annotation.positionRequest());
        result.add(menuAttachment);

        return result;
    }

}
