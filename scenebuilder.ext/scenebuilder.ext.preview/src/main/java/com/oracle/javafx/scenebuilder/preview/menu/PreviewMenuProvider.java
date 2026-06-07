/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.preview.menu;

import java.util.Arrays;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.preview.actions.ShowPreviewDialogAction;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.jfxplace.core.api.Size;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuBuilder;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuItemAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuItemProvider;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.api.util.StringUtils;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

@ApplicationInstanceSingleton
public class PreviewMenuProvider implements MenuItemProvider {

    private final I18N i18n;
    private final MenuBuilder menuBuilder;
    private final FxomEvents fxomEvents;
    private final PreviewWindowController previewWindowController;

    //@formatter:off
    public PreviewMenuProvider(
            I18N i18n,
            MenuBuilder menuBuilder,
            FxomEvents fxomEvents,
            @Lazy PreviewWindowController previewWindowController) {
        //@formatter:on
        this.i18n = i18n;
        this.menuBuilder = menuBuilder;
        this.fxomEvents = fxomEvents;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {

        return Arrays.asList(
                MenuItemAttachment.create(menuBuilder.separator().build(), ShowPreviewDialogAction.SHOW_PREVIEW_IN_DIALOG_ID, PositionRequest.AsNextSibling),
                new ChangePreviewSizeAttachment());
    }

    public class ChangePreviewSizeAttachment implements MenuItemAttachment {

        private Menu menu = null;

        public ChangePreviewSizeAttachment() {
        }

        @Override
        public String getTargetId() {
            return DefaultMenu.Preview.ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsLastChild;
        }

        // TODO use corresponding action here
        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            ToggleGroup sizeToggle = new ToggleGroup();
            menu = new Menu(i18n.getString("menu.title.preview.size"));

            RadioMenuItem mi = createSizeMenu(Size.SIZE_PREFERRED, sizeToggle);
            mi.setSelected(true);

            menu.getItems().add(mi);
            menu.getItems().add(new SeparatorMenuItem());

            for (Size s : Size.values()) {
                if (s != Size.SIZE_DEFAULT && s != Size.SIZE_PREFERRED) {
                    menu.getItems().add(createSizeMenu(s, sizeToggle));
                }
            }

            menu.setOnMenuValidation((e) -> {
                mi.setText(i18n.getString("menu.title.size.preferred.with.value",
                        StringUtils.getStringFromDouble(previewWindowController.getRoot().prefWidth(-1)),
                        StringUtils.getStringFromDouble(previewWindowController.getRoot().prefHeight(-1))));
            });

            fxomEvents.fxomDocument().subscribe(fd -> {
                boolean disabled = fd == null;

                if (disabled) {
                    menu.getItems().forEach(m -> m.setDisable(disabled));
                } else {
                    menu.getItems().forEach(m -> {
                        Size size = (Size) m.getUserData();
                        boolean previewIsValid = previewWindowController.getStage().isShowing() && !fd.is3D() && fd.isNode()
                                && previewWindowController.sizeDoesFit(size);
                        m.setDisable(!previewIsValid);
                    });
                }
            });

            return menu;
        }

        private RadioMenuItem createSizeMenu(Size size, ToggleGroup sizeToggle) {
            RadioMenuItem mi = new RadioMenuItem(size.toString());
            mi.setToggleGroup(sizeToggle);
            mi.setOnAction(e -> previewWindowController.setSize(size));
            mi.setUserData(size);
            return mi;
        }
    }
}
