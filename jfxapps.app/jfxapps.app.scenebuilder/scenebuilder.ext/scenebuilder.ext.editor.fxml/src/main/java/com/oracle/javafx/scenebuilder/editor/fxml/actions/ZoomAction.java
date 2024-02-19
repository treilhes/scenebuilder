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
package com.oracle.javafx.scenebuilder.editor.fxml.actions;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.ui.misc.Workspace;
import com.oracle.javafx.scenebuilder.util.MathUtils;

import javafx.scene.control.Menu;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(nameKey = "action.name.zoom", descriptionKey = "action.description.zoom")
public class ZoomAction extends AbstractAction {

    public final static String ZOOM_MENU_ID = "zoomMenu"; // NOCHECK
    public final static String ZOOM_PERCENT_MENU_ID = "zoomPercentMenu"; // NOCHECK

    private final Workspace workspace;
    private double scaleValue;

    public ZoomAction(ActionExtensionFactory extensionFactory, Workspace workspace) {
        super(extensionFactory);
        this.workspace = workspace;

    }

    public void setScaleValue(double scaleValue) {
        this.scaleValue = scaleValue;
    }

    @Override
    public boolean canPerform() {
        return !MathUtils.equals(scaleValue, workspace.getScaling());
    }

    @Override
    public ActionStatus doPerform() {
        assert canPerform();

        workspace.setScaling(scaleValue);

        return ActionStatus.DONE;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    public static class MenuProvider implements MenuItemProvider {

        private final ActionFactory actionFactory;
        private final MenuBuilder menuBuilder;

        public MenuProvider(ActionFactory actionFactory, MenuBuilder menuBuilder) {
            super();
            this.actionFactory = actionFactory;
            this.menuBuilder = menuBuilder;
        }

        @Override
        public List<MenuItemAttachment> menuItems() {
            List<MenuItemAttachment> result = new ArrayList<>();

            Menu menu = menuBuilder.menu().withId(ZOOM_MENU_ID).withTitle("menu.title.zoom").build();
            MenuItemAttachment menuAttachment = MenuItemAttachment.create(menu, ToggleGuidesVisibilityAction.MENU_ID,
                    PositionRequest.AfterNextSeparator);
            result.add(menuAttachment);

            ToggleGroup zoomToggle = new ToggleGroup();

            String target = ZOOM_MENU_ID;
            PositionRequest positionRequest = PositionRequest.AsLastChild;

            for (Double scaling : ZoomFeatureConfig.scalingTable) {
                final ZoomAction zAction = actionFactory.create(ZoomAction.class);
                zAction.setScaleValue(scaling);

                final String title = String.format("%.0f%%", scaling * 100); // NOCHECK
                final String menuId = ZOOM_PERCENT_MENU_ID + "_" + title;
                RadioMenuItem mi = new RadioMenuItem();
                mi.setToggleGroup(zoomToggle);

                mi = menuBuilder.radioMenuItem().withMenuItem(mi).withId(menuId).withAction(zAction)
                        .withTitle(title).build();

                MenuItemAttachment attachment = MenuItemAttachment.create(mi, target, positionRequest);

                target = menuId;
                positionRequest = PositionRequest.AsNextSibling;

                result.add(attachment);
            }

            return result;
        }

    }
}