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
package com.oracle.javafx.scenebuilder.fs.menu;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.fs.controller.FileSystemMenuController;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class FileSystemMenuProvider implements MenuProvider {
    
    private static final KeyCombination.Modifier modifier = KeyboardModifier.control();

    private final static String FILE_MENU_ID = "fileMenu";
    
    private final FileSystemMenuController fileSystemMenuController;
    private final RecentItemsPreference recentItemsPreference;

    private final Editor editor;

    private final DocumentManager documentManager;

    public FileSystemMenuProvider(
            @Autowired  @Lazy FileSystemMenuController fileSystemMenuController,
            @Autowired RecentItemsPreference recentItemsPreference,
            @Autowired Editor editor,
            @Autowired DocumentManager documentManager
            ) {
        this.fileSystemMenuController = fileSystemMenuController;
        this.recentItemsPreference = recentItemsPreference;
        this.editor = editor;
        this.documentManager = documentManager;
    }

    @Override
    public List<MenuAttachment> menus() {
        return Arrays.asList(new FileMenuAttachment());
    }

    public class FileMenuAttachment implements MenuAttachment {

        @FXML private Menu fileMenu;
        @FXML private MenuItem newMenuItem;
        @FXML private MenuItem newTemplateMenuItem;
        @FXML private MenuItem openMenuItem;
        @FXML private Menu openRecentMenu;
        @FXML private MenuItem saveMenuItem;
        @FXML private MenuItem saveAsMenuItem;
        @FXML private MenuItem revertMenuItem;
        @FXML private MenuItem revealMenuItem;
        @FXML private Menu importMenu;
        @FXML private MenuItem importFxmlMenuItem;
        @FXML private MenuItem importMediaMenuItem;
        @FXML private Menu includeMenu;
        @FXML private MenuItem includeFileMenuItem;
        @FXML private MenuItem editIncludedFileMenuItem;
        @FXML private MenuItem revealIncludedFileMenuItem;

        public FileMenuAttachment() {
        }

        @Override
        public String getTargetId() {
            return null;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsFirstChild;
        }

        @Override
        public Menu getMenu() {

            if (fileMenu != null) {
                return fileMenu;
            }
            
            FXMLUtils.load(this, "FileMenu.fxml");
            
            fileMenu.setId(FILE_MENU_ID);
            
            newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, modifier));
            newMenuItem.setOnAction(e -> fileSystemMenuController.performNew());
            
            openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, modifier));
            openMenuItem.setOnAction(e -> fileSystemMenuController.performOpen());
            
            openRecentMenu.setOnShowing(t -> updateOpenRecentMenuItems());
            
            saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, modifier));
            saveMenuItem.setOnAction(e -> fileSystemMenuController.performSave());
            
            saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, modifier));
            saveAsMenuItem.setOnAction(e -> fileSystemMenuController.performSaveAs());
            
            revertMenuItem.setOnAction(e -> fileSystemMenuController.performRevert());
            
            
            revealMenuItem.setText(getRevealMenuItemText());
            revealMenuItem.setOnAction(e -> fileSystemMenuController.performReveal());
            
            importFxmlMenuItem.setOnAction(e -> fileSystemMenuController.performImportFxml());
            importMediaMenuItem.setOnAction(e -> fileSystemMenuController.performImportMedia());

            includeFileMenuItem.setOnAction(e -> fileSystemMenuController.performIncludeFxml());
            
            editIncludedFileMenuItem.setOnAction(e -> fileSystemMenuController.performEditIncludedFxml());
            editIncludedFileMenuItem.setOnMenuValidation(e -> {
                String title = I18N.getString("menu.title.edit.included.default");
                final File file = editor.getIncludedFile();
                if (file != null) {
                    title = I18N.getString("menu.title.edit.included", file.getName());
                }
                editIncludedFileMenuItem.setText(title);
                editIncludedFileMenuItem.setDisable(file == null);
            });
            
            revealIncludedFileMenuItem.setOnAction(e -> fileSystemMenuController.performRevealIncludeFxml());
            revealIncludedFileMenuItem.setOnMenuValidation(e -> {
                String title = I18N.getString("menu.title.reveal.included.default");
                final File file = editor.getIncludedFile();
                if (file != null) {
                    if (EditorPlatform.IS_MAC) {
                        title = I18N.getString("menu.title.reveal.included.finder", file.getName());
                    } else {
                        title = I18N.getString("menu.title.reveal.included.explorer", file.getName());
                    }
                }
                revealIncludedFileMenuItem.setText(title);
                revealIncludedFileMenuItem.setDisable(file == null);
            });
            
            documentManager.dirty().subscribe(dirty -> {
                FXOMDocument fxomDocument = documentManager.fxomDocument().get();
                
                saveMenuItem.setDisable(!dirty);
                revertMenuItem.setDisable(!dirty || fxomDocument == null || fxomDocument.getLocation() == null);
                revealMenuItem.setDisable(fxomDocument != null && fxomDocument.getLocation() != null);
                importFxmlMenuItem.setDisable(fxomDocument != null && fxomDocument.getFxomRoot() != null &&  fxomDocument.getLocation() != null);
            });
            
            return fileMenu;
        }

        private String getRevealMenuItemText() {
            
            /*
             * Setup title of the Reveal menu item according the underlying o/s.
             */
            final String revealMenuKey;
            if (EditorPlatform.IS_MAC) {
                revealMenuKey = "menu.title.reveal.mac";
            } else if (EditorPlatform.IS_WINDOWS) {
                revealMenuKey = "menu.title.reveal.win.mnemonic";
            } else {
                assert EditorPlatform.IS_LINUX;
                revealMenuKey = "menu.title.reveal.linux";
            }
            return I18N.getString(revealMenuKey);
        }

        private void updateOpenRecentMenuItems() {

            final List<MenuItem> menuItems = new ArrayList<>();

            final List<String> recentItems = recentItemsPreference.getValue();

            final MenuItem clearMenuItem = new MenuItem(I18N.getString("menu.title.open.recent.clear"));
            clearMenuItem.setOnAction(e -> fileSystemMenuController.performClearOpenRecent());

            if (recentItems.isEmpty()) {
                clearMenuItem.setDisable(true);
                menuItems.add(clearMenuItem);
            } else {
                clearMenuItem.setDisable(false);

                final Map<String, Integer> recentItemsNames = new HashMap<>();
                final List<String> recentItemsToRemove = new ArrayList<>();

                // First pass to build recentItemsNames and recentItemsToRemove
                for (String recentItem : recentItems) {
                    final File recentItemFile = new File(recentItem);
                    if (recentItemFile.exists()) {
                        final String name = recentItemFile.getName();
                        if (recentItemsNames.containsKey(name)) {
                            recentItemsNames.replace(name, recentItemsNames.get(name) + 1);
                        } else {
                            recentItemsNames.put(name, 1);
                        }
                    } else {
                        // recent item file is still in preferences DB but has been removed from disk
                        recentItemsToRemove.add(recentItem);
                    }
                }
                // Second pass to build MenuItems
                for (String recentItem : recentItems) {
                    final File recentItemFile = new File(recentItem);
                    if (recentItemFile.exists()) {
                        final String name = recentItemFile.getName();
                        assert recentItemsNames.keySet().contains(name);
                        final MenuItem mi;
                        if (recentItemsNames.get(name) > 1) {
                            // Several files with same name : display full path
                            mi = new MenuItem(recentItem);
                        } else {
                            // Single file with this name : display file name only
                            assert recentItemsNames.get(name) == 1;
                            mi = new MenuItem(name);
                        }
                        mi.setOnAction(t -> {
                            final File file = new File(recentItem);
                            fileSystemMenuController.performOpenRecent(file);
                        });
                        mi.setMnemonicParsing(false);
                        menuItems.add(mi);
                    }
                }

                // Cleanup recent items preferences if needed
                if (recentItemsToRemove.isEmpty() == false) {
                    recentItemsPreference.removeRecentItems(recentItemsToRemove);
                }

                menuItems.add(new SeparatorMenuItem());
                menuItems.add(clearMenuItem);
            }

            openRecentMenu.getItems().setAll(menuItems);
        }
    }
}
