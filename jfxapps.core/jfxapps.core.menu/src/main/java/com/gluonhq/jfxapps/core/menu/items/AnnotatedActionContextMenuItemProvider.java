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
package com.gluonhq.jfxapps.core.menu.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.action.AbstractAction;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBuilder;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.PositionRequest;

import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

@ApplicationInstanceSingleton
public class AnnotatedActionContextMenuItemProvider implements ContextMenuItemProvider {

    private static Logger logger = LoggerFactory.getLogger(AnnotatedActionContextMenuItemProvider.class);

    private final JfxAppContext context;
    private final MenuBuilder builder;

    private List<ContextMenuItemAttachment> menuItemsCache;

    public AnnotatedActionContextMenuItemProvider(JfxAppContext context,
            MenuBuilder menuBuilder) {
        super();
        this.context = context;
        this.builder = menuBuilder;
    }

    @Override
    public List<ContextMenuItemAttachment> contextMenuItems() {

        if (menuItemsCache != null) {
            return menuItemsCache;
        }

        menuItemsCache = context
                .getBeanClassesForAnnotation(
                        com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ContextMenuItemAttachment.class)
                .stream().map(this::makeContextMenuItemAttachment).flatMap(l -> l.stream()).filter(Objects::nonNull)
                .collect(Collectors.toList());

        return menuItemsCache;
    }

    @SuppressWarnings("unchecked")
    private List<ContextMenuItemAttachment> makeContextMenuItemAttachment(Class<?> cls) {

        List<ContextMenuItemAttachment> result = new ArrayList<>();

        try {
            if (!AbstractAction.class.isAssignableFrom(cls)) {
                logger.error("ContextMenuItemAttachment annotation can only be used on Action, discarding {}", cls.getName());
                return null;
            }
            final Class<AbstractAction> actionClass = (Class<AbstractAction>) cls;

            final com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ContextMenuItemAttachment annotation = actionClass
                    .getAnnotation(com.gluonhq.jfxapps.core.api.ui.controller.menu.annotation.ContextMenuItemAttachment.class);

            assert annotation != null;

            MenuItem menuItem = builder.menuItem().id(annotation.id()).actionClass(actionClass).title(annotation.label()).toggleClass(annotation.toggleClass()).build();
            ContextMenuItemAttachment menuAttachment = ContextMenuItemAttachment.create(menuItem, annotation.selectionGroup(), annotation.targetMenuId(),
                    annotation.positionRequest());
            result.add(menuAttachment);

            if (annotation.separatorBefore()) {
                SeparatorMenuItem separator = builder.separator().id("separatorBefore_" + annotation.id()).build();
                ContextMenuItemAttachment attachment = ContextMenuItemAttachment.create(separator, annotation.selectionGroup(),annotation.id(),
                        PositionRequest.AsPreviousSibling);
                result.add(attachment);
            }

            if (annotation.separatorAfter()) {
                SeparatorMenuItem separator = builder.separator().id("separatorAfter_" + annotation.id()).build();
                ContextMenuItemAttachment attachment = ContextMenuItemAttachment.create(separator, annotation.selectionGroup(),annotation.id(),
                        PositionRequest.AsNextSibling);
                result.add(attachment);
            }
        } catch (Exception e) {
            logger.error("Unable to create a menu attachment for action : {}", cls, e);
        }
        return result;
    }

}
