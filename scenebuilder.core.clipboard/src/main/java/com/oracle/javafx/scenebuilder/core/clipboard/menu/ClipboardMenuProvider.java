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
package com.oracle.javafx.scenebuilder.core.clipboard.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.core.clipboard.action.CopyAction;
import com.oracle.javafx.scenebuilder.core.clipboard.action.CutAction;
import com.oracle.javafx.scenebuilder.core.clipboard.action.PasteAction;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ClipboardMenuProvider implements MenuItemProvider {

    private final static String EDIT_MENU_ID = "editMenu";
    private final static String CUT_ID = "cutMenu";
    private final static String COPY_ID = "copyMenu";
    private final static String PASTE_ID = "pasteMenu";

    private final ActionFactory actionFactory;

    public ClipboardMenuProvider(@Autowired ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        MenuItemAttachment cut = MenuItemAttachment.single(EDIT_MENU_ID, PositionRequest.AsLastChild, 
                "menu.title.cut", CUT_ID, (e) -> actionFactory.create(CutAction.class).perform(),
                () -> actionFactory.create(CutAction.class).canPerform());

        MenuItemAttachment copy = MenuItemAttachment.single(EDIT_MENU_ID, PositionRequest.AsLastChild,
                "menu.title.copy", COPY_ID, (e) -> actionFactory.create(CutAction.class).perform(),
                () -> actionFactory.create(CopyAction.class).canPerform());

        MenuItemAttachment paste = MenuItemAttachment.single(EDIT_MENU_ID, PositionRequest.AsLastChild,
                "menu.title.paste", PASTE_ID, (e) -> actionFactory.create(CutAction.class).perform(),
                () -> actionFactory.create(PasteAction.class).canPerform());

        return Arrays.asList(cut, copy, paste);
    }

}
