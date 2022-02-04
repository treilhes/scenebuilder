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
package com.oracle.javafx.scenebuilder.api.menubar.annotation;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;

import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class AnnotatedActionMenuItemProvider implements MenuItemProvider  {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedActionMenuItemProvider.class);

    private final SceneBuilderBeanFactory context;
    private final ActionFactory actionFactory;

    public AnnotatedActionMenuItemProvider(
            SceneBuilderBeanFactory context,
            ActionFactory actionFactory) {
        super();
        this.context = context;
        this.actionFactory = actionFactory;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return context.getBeanClassesForAnnotation(com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment.class)
            .stream()
            .map(this::makeMenuItemAttachment)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }


    @SuppressWarnings("unchecked")
    private MenuItemAttachment makeMenuItemAttachment(Class<?> cls) {

        if (!AbstractAction.class.isAssignableFrom(cls)) {
            logger.error("MenuItemAttachment annotation can only be used on Action, discarding {}", cls.getName());
            return null;
        }
        final Class<AbstractAction> actionClass = (Class<AbstractAction>)cls;

        final com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment annotation =
                actionClass.getAnnotation(com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment.class);

        assert annotation != null;

        return new MenuItemAttachment() {
            private MenuItem menu = null;

            @Override
            public String getTargetId() {
                return annotation.targetMenuId();
            }

            @Override
            public PositionRequest getPositionRequest() {
                return annotation.positionRequest();
            }

            @Override
            public MenuItem getMenuItem() {
                if (menu != null) {
                    return menu;
                }

                Action action = actionFactory.create(actionClass);

                MenuItem newMenu = new MenuItem();
                newMenu.setText(I18N.getStringOrDefault(annotation.label(), annotation.label()));
                newMenu.setId(annotation.id());
                newMenu.setOnAction((e) -> action.perform());
                newMenu.setDisable(!action.canPerform());
                newMenu.setOnMenuValidation((e) -> newMenu.setDisable(!action.canPerform()));

                menu = newMenu;
                return menu;
            }
        };
    }
}
