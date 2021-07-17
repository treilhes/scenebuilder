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
package com.oracle.javafx.scenebuilder.contenteditor.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.contenteditor.actions.ToggleGuidesVisibilityAction;
import com.oracle.javafx.scenebuilder.contenteditor.actions.ToggleOutlinesVisibilityAction;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ToggleMenuItemProvider implements MenuItemProvider {

    private final static String VIEW_MENU_ID = "viewMenu"; // NOCHECK
    private final static String TOGGLE_SEPARATOR_MENU_ID = "toggleSeparatorMenu"; // NOCHECK
    private final static String TOGGLE_GUIDES_MENU_ID = "toggleGuidesMenu"; // NOCHECK
    private final static String TOGGLE_OUTLINES_MENU_ID = "toggleOutlinesMenu"; // NOCHECK

    private final ActionFactory actionFactory;
    private final Content content;

    public ToggleMenuItemProvider(
            @Autowired ActionFactory actionFactory,
            @Autowired Content content) {
        this.actionFactory = actionFactory;
        this.content = content;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        MenuItemAttachment separator = MenuItemAttachment.separator(VIEW_MENU_ID, PositionRequest.AsLastChild,
                TOGGLE_SEPARATOR_MENU_ID);

        MenuItemAttachment toggleGuides = MenuItemAttachment.toggle(TOGGLE_SEPARATOR_MENU_ID,
                PositionRequest.AsNextSibling, "menu.title.enable.guides", "menu.title.disable.guides", 
                TOGGLE_GUIDES_MENU_ID, actionFactory, ToggleGuidesVisibilityAction.class,
                () -> content.isGuidesVisible());

        MenuItemAttachment toggleOutline = MenuItemAttachment.toggle(TOGGLE_GUIDES_MENU_ID,
                PositionRequest.AsNextSibling, "menu.title.show.outlines","menu.title.hide.outlines",
                TOGGLE_OUTLINES_MENU_ID, actionFactory, ToggleOutlinesVisibilityAction.class, 
                () -> content.isOutlinesVisible());

        return Arrays.asList(separator, toggleGuides, toggleOutline);
    }

}
