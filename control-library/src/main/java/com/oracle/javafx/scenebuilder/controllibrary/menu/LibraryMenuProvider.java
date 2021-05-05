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
package com.oracle.javafx.scenebuilder.controllibrary.menu;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.controls.DefaultSectionNames;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuProvider;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.controllibrary.controller.LibraryController;
import com.oracle.javafx.scenebuilder.controllibrary.tmp.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.tobeclassed.BuiltinSectionComparator;
import com.oracle.javafx.scenebuilder.controllibrary.tobeclassed.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.controllibrary.tobeclassed.LibraryItemNameComparator;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class LibraryMenuProvider implements MenuProvider {

    private final static String VIEW_MENU_ID = "viewMenu";
    private final static String INSERT_MENU_ID = "insertMenu";
    
    private final LibraryController libraryMenuController;
    private final ControlLibrary library;

    public LibraryMenuProvider(
            @Autowired  @Lazy LibraryController libraryMenuController,
            @Autowired  @Lazy ControlLibrary library
            ) {
        this.library = library;
        this.libraryMenuController = libraryMenuController;
    }

    @Override
    public List<MenuAttachment> menus() {
        return Arrays.asList(new InsertMenuAttachment());
    }

    public class InsertMenuAttachment implements MenuAttachment {

        private Menu insertMenu = null;
        private Menu insertCustomMenu = null;

        public InsertMenuAttachment() {
        }

        @Override
        public String getTargetId() {
            return VIEW_MENU_ID;
        }

        @Override
        public PositionRequest getPositionRequest() {
            return PositionRequest.AsNextSibling;
        }

        @Override
        public Menu getMenu() {

            if (insertMenu != null) {
                return insertMenu;
            }

            insertMenu = new Menu(I18N.getString("menu.title.insert"));
            insertMenu.setId(INSERT_MENU_ID);
            
            /*
             * Insert menu: we set what is statically known.
             */
            constructBuiltinPartOfInsertMenu();
            constructCustomPartOfInsertMenu();

            // The handler for Insert menu deals only with Custom sub-menu.
            insertMenu.setOnMenuValidation(onCustomPartOfInsertMenuValidationHandler);
            
            return insertMenu;
        }
        

        /*
         * Private (insert menu)
         */
        private final EventHandler<Event> onCustomPartOfInsertMenuValidationHandler
                = t -> {
            assert t.getSource() == insertMenu;
            updateCustomPartOfInsertMenu();
        };

        private void updateCustomPartOfInsertMenu() {
            assert insertMenu != null;
            assert insertCustomMenu != null;

            if (library != null) {
                
                Set<LibraryItem> sectionItems = new TreeSet<>(new LibraryItemNameComparator());

                // Collect custom items
                for (LibraryItem li : library.getItems()) {
                    if (li.getSection().equals(DefaultSectionNames.TAG_USER_DEFINED)) {
                        sectionItems.add(li);
                    }
                }

                // Make custom items visible and accessible via custom menu.
                if (sectionItems.size() > 0) {
                    insertCustomMenu.getItems().clear();

                    for (LibraryItem li : sectionItems) {
                        insertCustomMenu.getItems().add(makeMenuItemForLibraryItem(li));
                    }

                    insertCustomMenu.setVisible(true);
                } else {
                    insertCustomMenu.setVisible(false);
                }
            }
        }

        // At constructing time we dunno if we've custom items then we keep it hidden.
        private void constructCustomPartOfInsertMenu() {
            assert insertMenu != null;
            insertCustomMenu = makeMenuForLibrarySection(DefaultSectionNames.TAG_USER_DEFINED);
            insertMenu.getItems().add(0, insertCustomMenu);
            insertCustomMenu.setVisible(false);
        }

        // We consider the content of built-in library is static: it cannot change
        // unless its implementation is modified.
        private void constructBuiltinPartOfInsertMenu() {
            assert insertMenu != null;
            insertMenu.getItems().clear();

            final Map<String, Set<LibraryItem>> sectionMap
                    = new TreeMap<>(new BuiltinSectionComparator());

            for (LibraryItem li : library.getItems()) {
                Set<LibraryItem> sectionItems = sectionMap.get(li.getSection());
                if (sectionItems == null) {
                    sectionItems = new TreeSet<>(new LibraryItemNameComparator());
                    sectionMap.put(li.getSection(), sectionItems);
                }
                // Add all builtin Library items except the ContextMenu (see DTL-6831)
                if (!ContextMenu.class.getSimpleName().equals(li.getName())) {
                    sectionItems.add(li);
                }
            }

            for (Map.Entry<String, Set<LibraryItem>> e : sectionMap.entrySet()) {
                final Menu sectionMenu = makeMenuForLibrarySection(e.getKey());
                insertMenu.getItems().add(sectionMenu);
                for (LibraryItem li : e.getValue()) {
                    sectionMenu.getItems().add(makeMenuItemForLibraryItem(li));
                }
            }
        }

        private Menu makeMenuForLibrarySection(String section) {
            final Menu result = new Menu();
            result.setText(section);
            result.setOnShowing(t -> updateInsertMenuState(result));
            return result;
        }

        private MenuItem makeMenuItemForLibraryItem(final LibraryItem li) {
            final MenuItem result = new MenuItem();

            result.setText(li.getName());
            result.setUserData(li);
            result.setOnAction(t -> handleInsertMenuAction(li));
            return result;
        }

        private void updateInsertMenuState(Menu sectionMenu) {
            if (library != null) {

                for (MenuItem menuItem : sectionMenu.getItems()) {
                    assert menuItem.getUserData() instanceof LibraryItemImpl;
                    final LibraryItemImpl li = (LibraryItemImpl) menuItem.getUserData();
                    final boolean enabled = libraryMenuController.canPerformInsert(li);
                    menuItem.setDisable(!enabled);
                }
            } else {
                // See DTL-6017 and DTL-6554.
                // This case is relevant on Mac only; on Win and Linux the top menu
                // bar is part of the document window then even if some other non-modal
                // window is opened (Preferences, Skeleton, Preview) one has to give
                // focus to the document window to become able to open the Insert menu.
                for (MenuItem menuItem : sectionMenu.getItems()) {
                    assert menuItem.getUserData() instanceof LibraryItemImpl;
                    menuItem.setDisable(true);
                }
            }
        }

        private void handleInsertMenuAction(LibraryItem li) {
            if (library != null) {
                libraryMenuController.performInsert(li);
            }
        }

    }
}
