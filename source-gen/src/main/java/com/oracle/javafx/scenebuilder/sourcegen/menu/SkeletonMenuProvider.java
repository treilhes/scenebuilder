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
package com.oracle.javafx.scenebuilder.sourcegen.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.sourcegen.controller.SkeletonMenuController;

import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class SkeletonMenuProvider implements MenuItemProvider {

    private final static String VIEW_MENU_ID = "viewMenu";
    private final static String SHOW_CONTROLLER_IN_WINDOW_ID = "showSampleControllerMenuItem";
    
    private final SkeletonMenuController skeletonMenuController;

    public SkeletonMenuProvider(
            @Autowired  @Lazy SkeletonMenuController skeletonMenuController
            ) {
        this.skeletonMenuController = skeletonMenuController;
    }

    @Override
    public List<MenuAttachment> menuItems() {
        return Arrays.asList(new OpenSkeletonWindowAttachment());
    }

    public class OpenSkeletonWindowAttachment implements MenuAttachment {

        private MenuItem menu = null;

        public OpenSkeletonWindowAttachment() {
        }

        @Override
        public String getTargetId() {
            return VIEW_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.show.sample.controller.skeleton"));
            menu.setId(SHOW_CONTROLLER_IN_WINDOW_ID);
            menu.setAccelerator(new KeyCodeCombination(KeyCode.G, KeyboardModifier.control()));
            menu.setOnAction((e) -> skeletonMenuController.performOpenSkeletonWindow());
            return menu;
        }
    }
}
