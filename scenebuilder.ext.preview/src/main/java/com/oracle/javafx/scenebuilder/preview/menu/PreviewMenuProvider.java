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
package com.oracle.javafx.scenebuilder.preview.menu;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Size;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewMenuController;
import com.oracle.javafx.scenebuilder.preview.controller.PreviewWindowController;

import javafx.scene.control.DialogPane;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class PreviewMenuProvider implements MenuItemProvider {

    private final static String PREVIEW_MENU_ID = "previewMenu";
    private final static String SHOW_PREVIEW_IN_WINDOW_ID = "showPreviewInWindow";
    private final static String SHOW_PREVIEW_IN_DIALOG_ID = "showPreviewInDialog";

    private final DocumentManager documentManager;
    private final PreviewMenuController previewMenuController;
    private final PreviewWindowController previewWindowController;

    public PreviewMenuProvider(
            @Autowired DocumentManager documentManager,
            @Autowired  @Lazy PreviewMenuController previewMenuController,
            @Autowired  @Lazy PreviewWindowController previewWindowController) {
        this.documentManager = documentManager;
        this.previewMenuController = previewMenuController;
        this.previewWindowController = previewWindowController;
    }

    @Override
    public List<MenuItemAttachment> menuItems() {
        return Arrays.asList(new LaunchPreviewWindowAttachment(), new LaunchPreviewDialogAttachment(),
                MenuItemAttachment.separator(SHOW_PREVIEW_IN_DIALOG_ID, PositionRequest.AsNextSibling),
                new ChangePreviewSizeAttachment());
    }

    public class LaunchPreviewWindowAttachment implements MenuItemAttachment {

        private MenuItem menu = null;

        public LaunchPreviewWindowAttachment() {
        }

        @Override
        public String getTargetId() {
            return PREVIEW_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsFirstChild;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.show.preview.in.window"));
            menu.setId(SHOW_PREVIEW_IN_WINDOW_ID);
            menu.setAccelerator(new KeyCodeCombination(KeyCode.P, KeyboardModifier.control()));
            menu.setOnAction((e) -> previewMenuController.performOpenPreviewWindow());
            
            documentManager.fxomDocument().subscribe(fd -> menu.setDisable(fd == null));
            return menu;
        }
    }

    public class LaunchPreviewDialogAttachment implements MenuItemAttachment {

        private MenuItem menu = null;

        public LaunchPreviewDialogAttachment() {
        }

        @Override
        public String getTargetId() {
            return SHOW_PREVIEW_IN_WINDOW_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public MenuItem getMenuItem() {

            if (menu != null) {
                return menu;
            }

            menu = new MenuItem(I18N.getString("menu.title.show.preview.in.dialog"));
            menu.setId(SHOW_PREVIEW_IN_DIALOG_ID);
            menu.setOnAction((e) -> previewMenuController.performOpenPreviewWindow());
            
            documentManager.fxomDocument().subscribe(fd -> menu.setDisable(fd == null || !(fd.getSceneGraphRoot() instanceof DialogPane)));
            return menu;
        }
    }

    public class ChangePreviewSizeAttachment implements MenuItemAttachment {

        private Menu menu = null;

        public ChangePreviewSizeAttachment() {
        }

        @Override
        public String getTargetId() {
            return PREVIEW_MENU_ID;
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
                        getStringFromDouble(previewWindowController.getRoot().prefWidth(-1)),
                        getStringFromDouble(previewWindowController.getRoot().prefHeight(-1))));
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

        // Returns a String with no trailing zero; if decimal part is non zero then
        // it is kept.
        private String getStringFromDouble(double value) {
            String res = Double.toString(value);
            if(res.endsWith(".0")) { //NOCHECK
                res = Integer.toString((int)value);
            }
            return res;
        }


        private RadioMenuItem createSizeMenu(Size size, ToggleGroup sizeToggle) {
            RadioMenuItem mi = new RadioMenuItem(size.toString());
            mi.setToggleGroup(sizeToggle);
            mi.setOnAction(e -> previewMenuController.performChangePreviewSize(size));
            mi.setUserData(size);
            return mi;
        }
    }
}
