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
package com.oracle.javafx.scenebuilder.menu.action.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuBarObjectConfigurator;
import com.oracle.javafx.scenebuilder.api.menubar.MenuProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.menu.action.OpenFilesAction;

import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class FileSystemMenuProvider implements MenuProvider {

    private static final KeyCombination.Modifier modifier = KeyboardModifier.control();

    private final MenuBarObjectConfigurator builder;
    private final RecentItemsPreference recentItemsPreference;

    private final Editor editor;

    private final DocumentManager documentManager;

    private final ActionFactory actionFactory;

    //TODO need an update here, completely outdated, use actions
    public FileSystemMenuProvider(
            MenuBarObjectConfigurator menuBarObjectConfigurator,
            RecentItemsPreference recentItemsPreference,
            Editor editor,
            DocumentManager documentManager,
            ActionFactory actionFactory
            ) {
        this.builder = menuBarObjectConfigurator;
        this.recentItemsPreference = recentItemsPreference;
        this.editor = editor;
        this.documentManager = documentManager;
        this.actionFactory = actionFactory;
    }

    @Override
    public List<MenuAttachment> menus() {
        return Arrays.asList(new FileMenuAttachment());
    }

    public class FileMenuAttachment implements MenuAttachment {

        @FXML private Menu fileMenu;
        @FXML private MenuItem newMenuItem;
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

            fileMenu.setId(DefaultMenu.FILE_MENU_ID);

            //newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, modifier));
            //newMenuItem.setOnAction(e -> fileSystemMenuController.performNew());

            newMenuItem = builder.menuItem().withMenuItem(newMenuItem).withActionClass(NewAction.class).build();

//            openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, modifier));
//            openMenuItem.setOnAction(e -> fileSystemMenuController.performOpen());

            openMenuItem = builder.menuItem().withMenuItem(openMenuItem).withActionClass(OpenAction.class).build();

            openRecentMenu.setOnShowing(t -> updateOpenRecentMenuItems());

            //saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, modifier));
            //saveMenuItem.setOnAction(e -> fileSystemMenuController.performSave());
            saveMenuItem = builder.menuItem().withMenuItem(saveMenuItem).withActionClass(SaveOrSaveAsAction.class).build();

            //saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, modifier));
            //saveAsMenuItem.setOnAction(e -> fileSystemMenuController.performSaveAs());

            saveAsMenuItem = builder.menuItem().withMenuItem(saveAsMenuItem).withActionClass(SaveAsAction.class).build();

            revertMenuItem = builder.menuItem().withMenuItem(revertMenuItem).withActionClass(RevertAction.class).build();

            revealMenuItem = builder.menuItem().withMenuItem(revealMenuItem).withActionClass(RevealFxmlFileAction.class)
                    .withTitle(getRevealMenuItemText()).build();

            //revealMenuItem.setOnAction(e -> fileSystemMenuController.performReveal());

            importFxmlMenuItem = builder.menuItem().withMenuItem(importFxmlMenuItem).withActionClass(ImportFxmlAction.class).build();
            //importFxmlMenuItem.setOnAction(e -> fileSystemMenuController.performImportFxml());

            importMediaMenuItem = builder.menuItem().withMenuItem(importMediaMenuItem).withActionClass(ImportMediaAction.class).build();
            //importMediaMenuItem.setOnAction(e -> fileSystemMenuController.performImportMedia());

            includeFileMenuItem = builder.menuItem().withMenuItem(includeFileMenuItem).withActionClass(IncludeFxmlAction.class).build();
            //includeFileMenuItem.setOnAction(e -> fileSystemMenuController.performIncludeFxml());

            editIncludedFileMenuItem = builder.menuItem().withMenuItem(editIncludedFileMenuItem).withActionClass(EditIncludedFxmlAction.class)
                    .withTitleFunction((a) -> ((EditIncludedFxmlAction)a).getTitle()).build();

            revealIncludedFileMenuItem = builder.menuItem().withMenuItem(revealIncludedFileMenuItem).withActionClass(RevealIncludedFxmlAction.class)
                    .withTitleFunction((a) -> ((RevealIncludedFxmlAction)a).getTitle()).build();
//
//
//            documentManager.dirty().subscribe(dirty -> updateStates());
//            documentManager.fxomDocument().subscribe(fxomDocument -> updateStates());

            return fileMenu;
        }
//
//        private void updateStates() {
//            saveMenuItem.setDisable(!actionFactory.create(SaveOrSaveAsAction.class).canPerform());
//            revertMenuItem.setDisable(!actionFactory.create(RevertAction.class).canPerform());
//            revealMenuItem.setDisable(!actionFactory.create(RevealFxmlFileAction.class).canPerform());
//            importFxmlMenuItem.setDisable(!actionFactory.create(ImportFxmlAction.class).canPerform());
//            importMediaMenuItem.setDisable(!actionFactory.create(ImportMediaAction.class).canPerform());
//            includeFileMenuItem.setDisable(!actionFactory.create(IncludeFxmlAction.class).canPerform());
//        }

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

            final MenuItem clearMenuItem = builder.menuItem().withTitle("menu.title.open.recent.clear")
                    .withActionClass(ClearRecentItemsAction.class).build();

            if (!recentItems.isEmpty()) {

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
                        OpenFilesAction action = actionFactory.create(OpenFilesAction.class);
                        if (recentItemsNames.get(name) > 1) {
                            // Several files with same name : display full path
                            action.setFxmlFile(Arrays.asList(recentItemFile));
                            mi = builder.menuItem().withTitle(recentItem).withAction(action).build();
                        } else {
                            // Single file with this name : display file name only
                            assert recentItemsNames.get(name) == 1;
                            action.setFxmlFile(Arrays.asList(recentItemFile));
                            mi = builder.menuItem().withTitle(name).withAction(action).build();
                        }
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
