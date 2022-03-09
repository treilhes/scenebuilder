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
package com.oracle.javafx.scenebuilder.core.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.menu.MenuBarObjectConfigurator;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.ViewMenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menu.ViewMenuItemProvider;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class AnnotatedActionViewMenuItemProvider implements ViewMenuItemProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedActionViewMenuItemProvider.class);

    private final SceneBuilderBeanFactory context;
    private final MenuBarObjectConfigurator builder;

    private List<ViewMenuItemAttachment> menuItemsCache;

    public AnnotatedActionViewMenuItemProvider(SceneBuilderBeanFactory context,
            MenuBarObjectConfigurator menuBarObjectConfigurator) {
        super();
        this.context = context;
        this.builder = menuBarObjectConfigurator;
    }

    @Override
    public List<ViewMenuItemAttachment> menuItems() {

        if (menuItemsCache != null) {
            return menuItemsCache;
        }

        return context
                .getBeanClassesForAnnotation(
                        com.oracle.javafx.scenebuilder.api.menu.annotation.ViewMenuItemAttachment.class)
                .stream().map(this::makeMenuItemAttachment).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

//        return menuItemsCache;
    }

    @SuppressWarnings("unchecked")
    private List<ViewMenuItemAttachment> makeMenuItemAttachment(Class<?> cls) {

        List<ViewMenuItemAttachment> result = new ArrayList<>();

        try {
            if (!AbstractAction.class.isAssignableFrom(cls)) {
                logger.error("ViewMenuItemAttachment annotation can only be used on Action, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractAction> actionClass = (Class<AbstractAction>) cls;

            final com.oracle.javafx.scenebuilder.api.menu.annotation.ViewMenuItemAttachment annotation = actionClass
                    .getAnnotation(com.oracle.javafx.scenebuilder.api.menu.annotation.ViewMenuItemAttachment.class);

            assert annotation != null;

            MenuItem menuItem = builder.menuItem().withId(annotation.id()).withActionClass(actionClass)
                    .withTitle(annotation.label()).withViewClass(annotation.viewClass()).withToggleClass(annotation.toggleClass()).build();

            ViewMenuItemAttachment menuAttachment = ViewMenuItemAttachment.create(menuItem, annotation.targetMenuId(),
                    annotation.positionRequest(), annotation.viewClass());

            result.add(menuAttachment);

            if (annotation.separatorBefore()) {
                SeparatorMenuItem separator = builder.separator().withId("separatorBefore_" + annotation.id()).build();
                ViewMenuItemAttachment attachment = ViewMenuItemAttachment.create(separator, annotation.id(),
                        PositionRequest.AsPreviousSibling, annotation.viewClass());
                result.add(attachment);
            }

            if (annotation.separatorAfter()) {
                SeparatorMenuItem separator = builder.separator().withId("separatorAfter_" + annotation.id()).build();
                ViewMenuItemAttachment attachment = ViewMenuItemAttachment.create(separator, annotation.id(),
                        PositionRequest.AsNextSibling, annotation.viewClass());
                result.add(attachment);
            }
        } catch (Exception e) {
            logger.error("Unable to create a menu attachment for action : {}", cls, e);
        }
        return result;
    }

}