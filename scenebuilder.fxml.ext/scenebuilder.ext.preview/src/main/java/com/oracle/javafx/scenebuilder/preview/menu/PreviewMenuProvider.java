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
package com.oracle.javafx.scenebuilder.preview.menu;

import java.util.Arrays;
import java.util.List;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.StringUtils;
import com.oracle.javafx.scenebuilder.preview.actions.ShowPreviewDialogAction;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class PreviewMenuProvider implements MenuItemProvider {

    private final MenuBuilder menuBuilder;
    private final FxmlDocumentManager documentManager;
    private final PreviewWindowController previewWindowController;

    public PreviewMenuProvider(
            MenuBuilder menuBuilder,
            FxmlDocumentManager documentManager,
            @Lazy PreviewWindowController previewWindowController) {
        this.menuBuilder = menuBuilder;
        this.documentManager = documentManager;
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
            return DefaultMenu.PREVIEW_MENU_ID;
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
            menu = new Menu(I18N.getString("menu.title.preview.size"));

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
                mi.setText(I18N.getString("menu.title.size.preferred.with.value",
                        StringUtils.getStringFromDouble(previewWindowController.getRoot().prefWidth(-1)),
                        StringUtils.getStringFromDouble(previewWindowController.getRoot().prefHeight(-1))));
            });

            documentManager.fxomDocument().subscribe(fd -> {
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
