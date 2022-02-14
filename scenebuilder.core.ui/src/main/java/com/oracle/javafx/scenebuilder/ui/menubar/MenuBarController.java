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
package com.oracle.javafx.scenebuilder.ui.menubar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.ControlAction;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.Document.DocumentControlAction;
import com.oracle.javafx.scenebuilder.api.Document.DocumentEditAction;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.Editor.EditAction;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.Main.ApplicationControlAction;
import com.oracle.javafx.scenebuilder.api.action.editor.EditorPlatform;
import com.oracle.javafx.scenebuilder.api.di.DocumentScope;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.menubar.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemController;
import com.oracle.javafx.scenebuilder.api.menubar.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.menubar.MenuProvider;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.action.editor.KeyboardModifier;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.util.FXMLUtils;
import com.oracle.javafx.scenebuilder.ui.controller.DocumentWindowController;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.StackPane;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
//@Conditional(EditorPlatform.IS_MAC_CONDITION.class)
public class MenuBarController implements com.oracle.javafx.scenebuilder.api.MenuBar, InitializingBean {

    private static MenuBarController systemMenuBarController; // For Mac only

    @FXML
    private MenuBar menuBar;
    //@FXML
    //private Menu addEffectMenu;
//    @FXML
//    private Menu fileMenu; // Useless as soon as Preferences menu item is implemented
    @FXML
    private Menu previewMenu;
    @FXML
    private Menu windowMenu;

//    // File
//    @FXML
//    private MenuItem newMenuItem;
//    @FXML
//    private MenuItem newTemplateMenuItem;
//    @FXML
//    private MenuItem openMenuItem;
//    @FXML
//    private Menu openRecentMenu;
//    @FXML
//    private MenuItem saveMenuItem;
//    @FXML
//    private MenuItem saveAsMenuItem;
//    @FXML
//    private MenuItem revertMenuItem;
//    @FXML
//    private MenuItem closeMenuItem;
//    @FXML
//    private MenuItem revealMenuItem;
//    @FXML
//    private MenuItem importFxmlMenuItem;
//    @FXML
//    private MenuItem importMediaMenuItem;
//    @FXML
//    private MenuItem includeFileMenuItem;
//    @FXML
//    private MenuItem editIncludedFileMenuItem;
//    @FXML
//    private MenuItem revealIncludedFileMenuItem;
//    @FXML
//    private MenuItem showPreferencesMenuItem;
//    @FXML
//    private MenuItem exitMenuItem;

    // Edit
//    @FXML
//    private MenuItem undoMenuItem;
//    @FXML
//    private MenuItem redoMenuItem;
//    @FXML
//    private MenuItem copyMenuItem;
//    @FXML
//    private MenuItem cutMenuItem;
//    @FXML
//    private MenuItem pasteMenuItem;
//    @FXML
//    private MenuItem pasteIntoMenuItem;
//    @FXML
//    private MenuItem duplicateMenuItem;
//    @FXML
//    private MenuItem deleteMenuItem;
//    @FXML
//    private MenuItem selectAllMenuItem;
//    @FXML
//    private MenuItem selectNoneMenuItem;
//    @FXML
//    private MenuItem selectParentMenuItem;
//    @FXML
//    private MenuItem selectNextMenuItem;
//    @FXML
//    private MenuItem selectPreviousMenuItem;
//    @FXML
//    private MenuItem trimMenuItem;

    // View
//    @FXML
//    private MenuItem gotoContentMenuItem;
    @FXML
    private MenuItem gotoPropertiesMenuItem;
    @FXML
    private MenuItem gotoLayoutMenuItem;
    @FXML
    private MenuItem gotoCodeMenuItem;
    @FXML
    private MenuItem toggleLibraryPanelMenuItem;
    @FXML
    private MenuItem toggleHierarchyPanelMenuItem;
    @FXML
    private MenuItem toggleCSSPanelMenuItem;
    @FXML
    private MenuItem toggleLeftPanelMenuItem;
    @FXML
    private MenuItem toggleRightPanelMenuItem;
    @FXML
    private MenuItem toggleOutlinesMenuItem;
    @FXML
    private MenuItem toggleSampleDataMenuItem;
    @FXML
    private MenuItem toggleAlignmentGuidesMenuItem;
    @FXML
    private Menu zoomMenu;

    // Modify
    @FXML
    private MenuItem fitToParentMenuItem;
    @FXML
    private MenuItem useComputedSizesMenuItem;
    @FXML
    private MenuItem addContextMenuMenuItem;
    @FXML
    private MenuItem addTooltipMenuItem;
    @FXML
    private MenuItem moveRowAboveMenuItem;
    @FXML
    private MenuItem moveRowBelowMenuItem;
    @FXML
    private MenuItem moveColumnBeforeMenuItem;
    @FXML
    private MenuItem moveColumnAfterMenuItem;
    @FXML
    private MenuItem addRowAboveMenuItem;
    @FXML
    private MenuItem addRowBelowMenuItem;
    @FXML
    private MenuItem addColumnBeforeMenuItem;
    @FXML
    private MenuItem addColumnAfterMenuItem;
    @FXML
    private MenuItem increaseRowSpanMenuItem;
    @FXML
    private MenuItem decreaseRowSpanMenuItem;
    @FXML
    private MenuItem increaseColumnSpanMenuItem;
    @FXML
    private MenuItem decreaseColumnSpanMenuItem;
//    @FXML
//    private MenuItem phoneSetSizeMenuItem;
//    @FXML
//    private MenuItem tabletSetSizeMenuItem;
//    @FXML
//    private RadioMenuItem qvgaSetSizeMenuItem;
//    @FXML
//    private RadioMenuItem vgaSetSizeMenuItem;
//    @FXML
//    private RadioMenuItem touchSetSizeMenuItem;
//    @FXML
//    private RadioMenuItem hdSetSizeMenuItem;

    // Arrange
    @FXML
    private MenuItem bringToFrontMenuItem;
    @FXML
    private MenuItem sendToBackMenuItem;
    @FXML
    private MenuItem bringForwardMenuItem;
    @FXML
    private MenuItem sendBackwardMenuItem;
    @FXML
    private MenuItem wrapInAnchorPaneMenuItem;
    @FXML
    private MenuItem wrapInBorderPaneMenuItem;
    @FXML
    private MenuItem wrapInButtonBarMenuItem;
    @FXML
    private MenuItem wrapInDialogPaneMenuItem;
    @FXML
    private MenuItem wrapInFlowPaneMenuItem;
    @FXML
    private MenuItem wrapInGridPaneMenuItem;
    @FXML
    private MenuItem wrapInHBoxMenuItem;
    @FXML
    private MenuItem wrapInPaneMenuItem;
    @FXML
    private MenuItem wrapInScrollPaneMenuItem;
    @FXML
    private MenuItem wrapInSplitPaneMenuItem;
    @FXML
    private MenuItem wrapInStackPaneMenuItem;
    @FXML
    private MenuItem wrapInTabPaneMenuItem;
    @FXML
    private MenuItem wrapInTextFlowMenuItem;
    @FXML
    private MenuItem wrapInTilePaneMenuItem;
    @FXML
    private MenuItem wrapInTitledPaneMenuItem;
    @FXML
    private MenuItem wrapInToolBarMenuItem;
    @FXML
    private MenuItem wrapInVBoxMenuItem;
    @FXML
    private MenuItem wrapInGroupMenuItem;
    @FXML
    private MenuItem wrapInSceneMenuItem;
    @FXML
    private MenuItem wrapInStageMenuItem;
    @FXML
    private MenuItem unwrapMenuItem;

    // Preview

    // Window
    // Help
//    @FXML
//    private MenuItem helpMenuItem;

    private static final KeyCombination.Modifier modifier = KeyboardModifier.control();
    private final Map<KeyCombination, MenuItem> keyToMenu = new HashMap<>();

    //TODO delete when all the menuBar will be handled by extensions
    // for now it prevents extension's menu to be disabled by legacy code
    List<MenuItem> dynamicMenu = new ArrayList<>();

    Map<String, MenuItem> menuMap = null;

    private final SceneBuilderBeanFactory context;
    private final DocumentWindowController documentWindowController;
    private final Content content;
    private final Editor editor;
    private final Document document;
    private final List<MenuProvider> menuProviders;
    private final List<MenuItemProvider> menuItemProviders;

    //private final PreviewWindowController previewWindowController;

    private FXOMDocument fxomDocument;

    private final Main main;

    public MenuBarController(
            @Autowired SceneBuilderBeanFactory context,
            @Autowired DocumentManager documentManager,
            @Autowired Content content,
            @Autowired Editor editor,
            @Autowired(required = false) List<MenuProvider> menuProviders,
            @Autowired(required = false) List<MenuItemProvider> menuItemProviders,
            @Autowired @Lazy Document document,
            @Autowired @Lazy DocumentWindowController documentWindowController,
            @Autowired Main main
            ) {
        this.context = context;
        this.document = document;
        this.content = content;
        this.editor = editor;
        this.menuProviders = menuProviders;
        this.menuItemProviders = menuItemProviders;
        this.documentWindowController = documentWindowController;
        this.main = main;

        documentManager.fxomDocument().subscribe(fd -> fxomDocument = fd);
    }

    private void buildMenuMap(MenuBar menuBar) {
        if (menuMap != null) {
            return;
        }
        menuMap = new HashMap<>();
        menuBar.getMenus().forEach(m -> addToMenuMap(m));
    }

    private void addToMenuMap(MenuItem m) {
        if (m.getId() != null && !m.getId().isEmpty()) {
            if (menuMap.containsKey(m.getId())) {
                Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE, "Duplicate id in menu map : {0}", m.getId());
            } else {
                menuMap.put(m.getId(), m);
            }
        }

        if (m instanceof Menu) {
            ((Menu)m).getItems().forEach(mi -> {
                addToMenuMap(mi);
            });
        }

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        systemMenuBarController = this;
        //setupMenuBar()
    }

    private void setupMenuBar() {
        MenuBar menuBar = getMenuBar();
        buildMenuMap(menuBar);
        populateMenus();
        populateMenuItems();
    }

    private void populateMenus() {
        if (menuProviders != null && !menuProviders.isEmpty()) {

            List<MenuProvider> validProviders = menuProviders.stream()
                   .filter(mp -> mp != null && mp.menus() != null && !mp.menus().isEmpty())
                   .collect(Collectors.toList());

           List<MenuAttachment> validAttachments = validProviders.stream()
                   .flatMap(mp -> mp.menus().stream())
                   .filter(ma -> ma != null && ma.getPositionRequest() != null && ma.getMenu() != null)
                   .collect(Collectors.toList());

           boolean hasInvalidProviders = menuProviders.size() > validProviders.size();
           List<MenuProvider> invalidProviders = hasInvalidProviders
                   ? menuProviders.stream().filter(m -> !validProviders.contains(m)).collect(Collectors.toList())
                   : null;

           boolean atLeastOneInserted = true;
           while (!validAttachments.isEmpty() && atLeastOneInserted) {
               atLeastOneInserted = false;

               ListIterator<MenuAttachment> it = validAttachments.listIterator();
               while (it.hasNext()) {
                   boolean inserted = false;
                   MenuAttachment ma = it.next();
                   MenuItem targetCandidate = menuMap.get(ma.getTargetId());

                   if (targetCandidate != null && !Menu.class.isAssignableFrom(targetCandidate.getClass())) {
                       continue;
                   }

                   Menu target = (Menu)targetCandidate;

                   Menu menu = ma.getMenu();

                   if (menu.getId() == null) {
                       menu.setId(ma.getClass().getSimpleName());
                   }

                   try {
                    switch (ma.getPositionRequest()) {
                           case AsFirstSibling: {
                               menuBar.getMenus().add(0, menu);
                               inserted = true;
                               break;
                           }
                           case AsLastSibling: {
                               menuBar.getMenus().add(menu);
                               inserted = true;
                               break;
                           }
                           case AsPreviousSibling: {
                               if (target != null) {
                                   int index = menuBar.getMenus().indexOf(target);
                                   menuBar.getMenus().add(index, menu);
                                   inserted = true;
                               }
                               break;
                           }
                           case AsNextSibling: {
                               if (target != null) {
                                   int index = menuBar.getMenus().indexOf(target);
                                   menuBar.getMenus().add(index + 1, menu);
                                   inserted = true;
                               }
                               break;
                           }
                           case AfterPreviousSeparator: {
                               throw new RuntimeException("Invalid position request for menu");
                           }
                           case BeforeNextSeparator: {
                               throw new RuntimeException("Invalid position request for menu");
                           }
                           case AsFirstChild: {
                               if (target == null) {
                                   menuBar.getMenus().add(0, menu);
                                   inserted = true;
                               }
                               break;
                           }
                           case AsLastChild: {
                               if (target == null) {
                                   menuBar.getMenus().add(menu);
                                   inserted = true;
                               }
                               break;
                           }
                           default:
                               throw new RuntimeException("Invalid position request for menu");
                       }
                } catch (Exception e) {
                    Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                            "Unable to add all the provided menu in the menuBar", e);
                }

                   if (inserted) {
                       dynamicMenu.add(menu);
                       addToMenuMap(menu);
                       it.remove();
                       atLeastOneInserted = true;
                   }

               }
           }

           if (invalidProviders != null) {
               invalidProviders.forEach(mip -> {
                   Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                           "Invalid MenuProviders submitted {0}", mip.getClass().getName());
               });
           }
           if (validAttachments.size() > 0) {
               Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                       "Unable to add all the provided menu in the menuBar");
               validAttachments.forEach(ma -> {
                   Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                           "Unable to attach {0} to id {1} using {2}", new String[] { ma.getClass().getName(),
                                   ma.getTargetId(), ma.getPositionRequest().toString() });
               });
           }
       }
    }

    private void populateMenuItems() {
        if (menuItemProviders != null && !menuItemProviders.isEmpty()) {

            List<MenuItemProvider> validProviders = menuItemProviders.stream()
                   .filter(mip -> mip != null && mip.menuItems() != null && !mip.menuItems().isEmpty())
                   .collect(Collectors.toList());

           List<MenuItemAttachment> validAttachments = validProviders.stream()
                   .flatMap(mip -> mip.menuItems().stream())
                   .filter(ma -> ma != null && ma.getTargetId() != null && ma.getPositionRequest() != null && ma.getMenuItem() != null)
                   .collect(Collectors.toList());

           boolean hasInvalidProviders = menuItemProviders.size() > validProviders.size();
           List<MenuItemProvider> invalidProviders = hasInvalidProviders
                   ? menuItemProviders.stream().filter(m -> !validProviders.contains(m)).collect(Collectors.toList())
                   : null;

           boolean atLeastOneInserted = true;
           while (!validAttachments.isEmpty() && atLeastOneInserted) {
               atLeastOneInserted = false;

               ListIterator<MenuItemAttachment> it = validAttachments.listIterator();
               while (it.hasNext()) {
                   boolean inserted = false;
                   MenuItemAttachment ma = it.next();
                   MenuItem target = menuMap.get(ma.getTargetId());

                   if (target != null) {

                       if (ma.getMenuItem().getId() == null) {
                           ma.getMenuItem().setId(ma.getClass().getSimpleName());
                       }

                       try {
                        switch (ma.getPositionRequest()) {
                               case AsFirstSibling: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   items.add(0, ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case AsLastSibling: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   items.add(ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case AsPreviousSibling: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   int index = items.indexOf(target);
                                   items.add(index, ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case AsNextSibling: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   int index = items.indexOf(target);
                                   items.add(index + 1, ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case AfterPreviousSeparator: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   int index = items.indexOf(target);
                                   int insertAt = 0;
                                   for (int i = index; i >= 0; i--) {
                                       if (SeparatorMenuItem.class.isAssignableFrom(items.get(i).getClass())) {
                                           insertAt = i + 1;
                                           break;
                                       }
                                   }
                                   items.add(insertAt, ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case BeforeNextSeparator: {
                                   ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                   int index = items.indexOf(target);
                                   int insertAt = items.size();
                                   for (int i = index; i < items.size(); i++) {
                                       if (SeparatorMenuItem.class.isAssignableFrom(items.get(i).getClass())) {
                                           insertAt = i;
                                           break;
                                       }
                                   }
                                   items.add(insertAt, ma.getMenuItem());
                                   inserted = true;
                                   break;
                               }
                               case AsFirstChild: {
                                   if (target instanceof Menu) {
                                       Menu m = (Menu)target;
                                       m.getItems().add(0, ma.getMenuItem());
                                       inserted = true;
                                   }
                                   break;
                               }
                               case AsLastChild: {
                                   if (target instanceof Menu) {
                                       Menu m = (Menu)target;
                                       m.getItems().add(ma.getMenuItem());
                                       inserted = true;
                                   }
                                   break;
                               }
                               default:
                                   throw new RuntimeException("Invalid position request for menuItem");

                           }
                    } catch (Exception e) {
                        Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                                "Unable to add the provided menuItem in the menuBar", e);
                    }

                       if (inserted) {
                           dynamicMenu.add(ma.getMenuItem());
                           addToMenuMap(ma.getMenuItem());
                           it.remove();
                           atLeastOneInserted = true;
                       }
                   }
               }
           }

           if (invalidProviders != null) {
               invalidProviders.forEach(mip -> {
                   Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                           "Invalid MenuItemProviders submitted {0}", mip.getClass().getName());
               });
           }
           if (validAttachments.size() > 0) {
               Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                       "Unable to add all the provided menuItem in the menuBar");
               validAttachments.forEach(ma -> {
                   Logger.getLogger(MenuBarController.class.getName()).log(Level.SEVERE,
                           "Unable to attach {0} to id {1} using {2}", new String[] { ma.getClass().getName(),
                                   ma.getTargetId(), ma.getPositionRequest().toString() });
               });
           }
       }
    }

    @Override
    public MenuBar getMenuBar() {

        if (menuBar == null) {
            FXMLUtils.load(this, "MenuBar.fxml");
            controllerDidLoadFxml();
        }

        return menuBar;
    }

    public static synchronized MenuBarController getSystemMenuBarController() {
        assert systemMenuBarController != null;
        //TODO uncomment below and springify
//        if (systemMenuBarController == null) {
//            systemMenuBarController = new MenuBarController(null);
//        }
        return systemMenuBarController;
    }

    /*
     * Private
     */
    private void controllerDidLoadFxml() {

        assert menuBar != null;
        assert menuBar.getParent() instanceof StackPane;
        //assert addEffectMenu != null;
        //assert fileMenu != null;
        assert windowMenu != null;

//        assert newMenuItem != null;
//        assert newTemplateMenuItem != null;
//        assert openMenuItem != null;
//        assert openRecentMenu != null;
//        assert saveMenuItem != null;
//        assert saveAsMenuItem != null;
//        assert revertMenuItem != null;
//        //assert closeMenuItem != null;
//        assert revealMenuItem != null;
//        assert importFxmlMenuItem != null;
//        assert importMediaMenuItem != null;
//        assert includeFileMenuItem != null;
//        assert editIncludedFileMenuItem != null;
//        assert revealIncludedFileMenuItem != null;
        //assert showPreferencesMenuItem != null;
        //assert exitMenuItem != null;

//        assert undoMenuItem != null;
//        assert redoMenuItem != null;
//        assert copyMenuItem != null;
//        assert cutMenuItem != null;
//        assert pasteMenuItem != null;
//        assert pasteIntoMenuItem != null;
//        assert duplicateMenuItem != null;
//        assert deleteMenuItem != null;
//        assert selectAllMenuItem != null;
//        assert selectNoneMenuItem != null;
//        assert selectParentMenuItem != null;
//        assert selectNextMenuItem != null;
//        assert selectPreviousMenuItem != null;
//        assert trimMenuItem != null;

        //assert gotoContentMenuItem != null;
        assert gotoPropertiesMenuItem != null;
        assert gotoLayoutMenuItem != null;
        assert gotoCodeMenuItem != null;
        assert toggleLibraryPanelMenuItem != null;
        assert toggleHierarchyPanelMenuItem != null;
        assert toggleCSSPanelMenuItem != null;
        assert toggleLeftPanelMenuItem != null;
        assert toggleRightPanelMenuItem != null;
        assert toggleOutlinesMenuItem != null;
        assert toggleSampleDataMenuItem != null;
        assert toggleAlignmentGuidesMenuItem != null;
        assert zoomMenu != null;
        assert zoomMenu.getItems().isEmpty();

        assert fitToParentMenuItem != null;
        assert useComputedSizesMenuItem != null;
        assert addContextMenuMenuItem != null;
        assert addTooltipMenuItem != null;
        assert moveRowAboveMenuItem != null;
        assert moveRowBelowMenuItem != null;
        assert moveColumnBeforeMenuItem != null;
        assert moveColumnAfterMenuItem != null;
        assert addRowAboveMenuItem != null;
        assert addRowBelowMenuItem != null;
        assert addColumnBeforeMenuItem != null;
        assert addColumnAfterMenuItem != null;
        assert increaseRowSpanMenuItem != null;
        assert decreaseRowSpanMenuItem != null;
        assert increaseColumnSpanMenuItem != null;
        assert decreaseColumnSpanMenuItem != null;
//        assert phoneSetSizeMenuItem != null;
//        assert tabletSetSizeMenuItem != null;
//        assert qvgaSetSizeMenuItem != null;
//        assert vgaSetSizeMenuItem != null;
//        assert touchSetSizeMenuItem != null;
//        assert hdSetSizeMenuItem != null;

        assert bringToFrontMenuItem != null;
        assert sendToBackMenuItem != null;
        assert bringForwardMenuItem != null;
        assert sendBackwardMenuItem != null;
        assert wrapInAnchorPaneMenuItem != null;
        assert wrapInBorderPaneMenuItem != null;
        assert wrapInButtonBarMenuItem != null;
        assert wrapInDialogPaneMenuItem != null;
        assert wrapInFlowPaneMenuItem != null;
        assert wrapInGridPaneMenuItem != null;
        assert wrapInHBoxMenuItem != null;
        assert wrapInPaneMenuItem != null;
        assert wrapInScrollPaneMenuItem != null;
        assert wrapInSplitPaneMenuItem != null;
        assert wrapInStackPaneMenuItem != null;
        assert wrapInTabPaneMenuItem != null;
        assert wrapInTextFlowMenuItem != null;
        assert wrapInTilePaneMenuItem != null;
        assert wrapInTitledPaneMenuItem != null;
        assert wrapInToolBarMenuItem != null;
        assert wrapInVBoxMenuItem != null;
        assert wrapInGroupMenuItem != null;
        assert wrapInSceneMenuItem != null;
        assert wrapInStageMenuItem != null;
        assert unwrapMenuItem != null;

        //assert helpMenuItem != null;

        /*
         * To make MenuBar.fxml editable with SB 1.1, the menu bar is enclosed
         * in a StackPane. This stack pane is useless now.
         * So we unwrap the menu bar and make it the panel root.
         */
        final StackPane rootStackPane = (StackPane) menuBar.getParent();
        rootStackPane.getChildren().remove(menuBar);

        /*
         * On Mac, move the menu bar on the desktop and remove the Quit item
         * from the File menu
         */
        if (EditorPlatform.IS_MAC) {
            menuBar.setUseSystemMenuBar(true);
            // SB-269
            menuBar.useSystemMenuBarProperty().addListener((obs, ov, nv) -> {
                if (! nv) {
                    // Restore System MenuBar
                    menuBar.setUseSystemMenuBar(true);
                }
            });
        }

//        /*
//         * Setup title of the Reveal menu item according the underlying o/s.
//         */
//        final String revealMenuKey;
//        if (EditorPlatform.IS_MAC) {
//            revealMenuKey = "menu.title.reveal.mac";
//        } else if (EditorPlatform.IS_WINDOWS) {
//            revealMenuKey = "menu.title.reveal.win.mnemonic";
//        } else {
//            assert EditorPlatform.IS_LINUX;
//            revealMenuKey = "menu.title.reveal.linux";
//        }
//        revealMenuItem.setText(I18N.getString(revealMenuKey));
//
//        /*
//         * File menu
//         */
//        newMenuItem.setUserData(new ApplicationControlActionController(ApplicationControlAction.NEW_FILE));
//        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, modifier));
//        newTemplateMenuItem.setUserData(new ApplicationControlActionController(ApplicationControlAction.NEW_TEMPLATE));
//        openMenuItem.setUserData(new ApplicationControlActionController(ApplicationControlAction.OPEN_FILE));
//        openMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.O, modifier));
//        openRecentMenu.setOnShowing(t -> updateOpenRecentMenuItems());
//        saveMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.SAVE_FILE));
//        saveMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, modifier));
//        saveAsMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.SAVE_AS_FILE));
//        saveAsMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHIFT_DOWN, modifier));
//        revertMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.REVERT_FILE));
//        revealMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.REVEAL_FILE));
//        importFxmlMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.IMPORT_FXML));
//        importMediaMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.IMPORT_MEDIA));
//        includeFileMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.INCLUDE_FXML));
//        editIncludedFileMenuItem.setUserData(new ControlActionController(ControlAction.EDIT_INCLUDED_FILE) {
//
//            @Override
//            public String getTitle() {
//                String title = I18N.getString("menu.title.edit.included.default");
//                if (documentWindowController != null) {
//                    final File file = editor.getIncludedFile();
//                    if (file != null) {
//                        title = I18N.getString("menu.title.edit.included", file.getName());
//                    }
//                }
//                return title;
//            }
//        });
//        revealIncludedFileMenuItem.setUserData(new ControlActionController(ControlAction.REVEAL_INCLUDED_FILE) {
//
//            @Override
//            public String getTitle() {
//                String title = I18N.getString("menu.title.reveal.included.default");
//                if (documentWindowController != null) {
//                    final File file = editor.getIncludedFile();
//                    if (file != null) {
//                        if (EditorPlatform.IS_MAC) {
//                            title = I18N.getString("menu.title.reveal.included.finder", file.getName());
//                        } else {
//                            title = I18N.getString("menu.title.reveal.included.explorer", file.getName());
//                        }
//                    }
//                }
//                return title;
//            }
//        });
//        showPreferencesMenuItem.setUserData(new ApplicationControlActionController(ApplicationControlAction.SHOW_PREFERENCES));
//        showPreferencesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.COMMA, modifier));

        /*
         * Edit menu
         */
//        undoMenuItem.setUserData(new UndoActionController());
//        undoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, modifier));
//        redoMenuItem.setUserData(new RedoActionController());
//        if (EditorPlatform.IS_MAC) {
//            // Mac platforms.
//            redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Z, KeyCombination.SHIFT_DOWN, modifier));
//        } else {
//            // Windows and Linux platforms.
//            // http://windows.microsoft.com/en-US/windows7/Keyboard-shortcuts
//            redoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.Y, modifier));
//        }
//        copyMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.COPY));
//        copyMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.C, modifier));
//        cutMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.CUT));
//        cutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.X, modifier));
//        pasteMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.PASTE));
//        pasteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, modifier));
//        pasteIntoMenuItem.setUserData(new EditActionController(EditAction.PASTE_INTO));
//        pasteIntoMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHIFT_DOWN, modifier));
//        duplicateMenuItem.setUserData(new EditActionController(EditAction.DUPLICATE));
//        duplicateMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.D, modifier));
//        deleteMenuItem.setUserData(new DocumentEditActionController(DocumentEditAction.DELETE));
//        deleteMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DELETE));
//        selectAllMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.SELECT_ALL));
//        selectAllMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, modifier));
//        selectNoneMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.SELECT_NONE));
//        selectNoneMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.A, KeyCombination.SHIFT_DOWN, modifier));
//        selectParentMenuItem.setUserData(new ControlActionController(ControlAction.SELECT_PARENT));
//        selectParentMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.UP, modifier));
//        selectNextMenuItem.setUserData(new ControlActionController(ControlAction.SELECT_NEXT));
//        selectNextMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.RIGHT, modifier));
//        selectPreviousMenuItem.setUserData(new ControlActionController(ControlAction.SELECT_PREVIOUS));
//        selectPreviousMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.LEFT, modifier));
//        trimMenuItem.setUserData(new EditActionController(EditAction.TRIM));

        /*
         * View menu
         */
//        gotoContentMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.GOTO_CONTENT));
//        gotoContentMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT0, modifier));
        gotoPropertiesMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.GOTO_PROPERTIES));
        gotoPropertiesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT1, modifier));
        gotoLayoutMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.GOTO_LAYOUT));
        gotoLayoutMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT2, modifier));
        gotoCodeMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.GOTO_CODE));
        gotoCodeMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT3, modifier));

        toggleLibraryPanelMenuItem.setUserData(
                new DocumentControlActionController(DocumentControlAction.TOGGLE_LIBRARY_PANEL) {
                    @Override
                    public String getTitle() {
                        final String titleKey;
                        if (documentWindowController == null) {
                            titleKey = "menu.title.hide.library.panel";
                        //TODO uncomment and handle with the new view framework when ready
                        //} else if (documentWindowController.isLibraryPanelVisible()) {
                        //    titleKey = "menu.title.hide.library.panel";
                        } else {
                            titleKey = "menu.title.show.library.panel";
                        }
                        return I18N.getString(titleKey);
                    }
                });
        toggleLibraryPanelMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT4, modifier));
        toggleHierarchyPanelMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_DOCUMENT_PANEL) {
            @Override
            public String getTitle() {
                final String titleKey;
                if (documentWindowController == null) {
                    titleKey = "menu.title.hide.document.panel";
                  //TODO uncomment and handle with the new view framework when ready
                    //} else if (documentWindowController.isHierarchyPanelVisible()) {
                    //titleKey = "menu.title.hide.document.panel";
                } else {
                    titleKey = "menu.title.show.document.panel";
                }
                return I18N.getString(titleKey);
            }
        });
        toggleHierarchyPanelMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT5, modifier));
        toggleCSSPanelMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_CSS_PANEL) {
            @Override
            public String getTitle() {
                final String titleKey;
                if (documentWindowController == null) {
                    titleKey = "menu.title.hide.bottom.panel";
                  //TODO uncomment and handle with the new view framework when ready
                    //} else if (documentWindowController.isBottomPanelVisible()) {
                    //titleKey = "menu.title.hide.bottom.panel";
                } else {
                    titleKey = "menu.title.show.bottom.panel";
                }
                return I18N.getString(titleKey);
            }
        });
        toggleCSSPanelMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT6, modifier));
        toggleLeftPanelMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_LEFT_PANEL) {
            @Override
            public String getTitle() {
                final String titleKey;
                if (documentWindowController == null) {
                    titleKey = "menu.title.hide.left.panel";
                  //TODO uncomment and handle with the new view framework when ready
                    //} else if (documentWindowController.isLeftPanelVisible()) {
                    //titleKey = "menu.title.hide.left.panel";
                } else {
                    titleKey = "menu.title.show.left.panel";
                }
                return I18N.getString(titleKey);
            }
        });
        toggleLeftPanelMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT7, modifier));
        toggleRightPanelMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_RIGHT_PANEL) {
            @Override
            public String getTitle() {
                final String titleKey;
                if (documentWindowController == null) {
                    titleKey = "menu.title.hide.right.panel";
                  //TODO uncomment and handle with the new view framework when ready
                    //} else if (documentWindowController.isRightPanelVisible()) {
                    //titleKey = "menu.title.hide.right.panel";
                } else {
                    titleKey = "menu.title.show.right.panel";
                }
                return I18N.getString(titleKey);
            }
        });
        toggleRightPanelMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.DIGIT8, modifier));
//        toggleOutlinesMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_OUTLINES_VISIBILITY) {
//            @Override
//            public String getTitle() {
//                final String titleKey;
//                if (documentWindowController == null) {
//                    titleKey = "menu.title.hide.outlines";
//                } else if (content.isOutlinesVisible()) {
//                    titleKey = "menu.title.hide.outlines";
//                } else {
//                    titleKey = "menu.title.show.outlines";
//                }
//                return I18N.getString(titleKey);
//            }
//        });
//        toggleOutlinesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.E, modifier));
        toggleSampleDataMenuItem.setUserData(new ControlActionController(ControlAction.TOGGLE_SAMPLE_DATA) {
            @Override
            public String getTitle() {
                final String titleKey;
                if (documentWindowController == null) {
                    titleKey = "menu.title.hide.sample.data";
                } else if (editor.isSampleDataEnabled()) {
                    titleKey = "menu.title.hide.sample.data";
                } else {
                    titleKey = "menu.title.show.sample.data";
                }
                return I18N.getString(titleKey);
            }
        });
//        toggleAlignmentGuidesMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.TOGGLE_GUIDES_VISIBILITY) {
//            @Override
//            public String getTitle() {
//                final String titleKey;
//                if (documentWindowController == null) {
//                    titleKey = "menu.title.disable.guides";
//                } else {
//                    if (content.isGuidesVisible()) {
//                        titleKey = "menu.title.disable.guides";
//                    } else {
//                        titleKey = "menu.title.enable.guides";
//                    }
//                }
//                return I18N.getString(titleKey);
//            }
//        });
        //updateZoomMenu();

        /*
         * Insert menu: it uses specific handlers, which means we initialize it
         * later to avoid interfering with other menus.
         */

        /*
         * Modify menu
         */
        fitToParentMenuItem.setUserData(new EditActionController(EditAction.FIT_TO_PARENT));
        fitToParentMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.K, modifier));
        useComputedSizesMenuItem.setUserData(new EditActionController(EditAction.USE_COMPUTED_SIZES));
        useComputedSizesMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.K, KeyCombination.SHIFT_DOWN, modifier));
        addContextMenuMenuItem.setUserData(new EditActionController(EditAction.ADD_CONTEXT_MENU));
        addTooltipMenuItem.setUserData(new EditActionController(EditAction.ADD_TOOLTIP));
        moveRowAboveMenuItem.setUserData(new EditActionController(EditAction.MOVE_ROW_ABOVE));
        moveRowBelowMenuItem.setUserData(new EditActionController(EditAction.MOVE_ROW_BELOW));
        moveColumnBeforeMenuItem.setUserData(new EditActionController(EditAction.MOVE_COLUMN_BEFORE));
        moveColumnAfterMenuItem.setUserData(new EditActionController(EditAction.MOVE_COLUMN_AFTER));
        addRowAboveMenuItem.setUserData(new EditActionController(EditAction.ADD_ROW_ABOVE));
        addRowBelowMenuItem.setUserData(new EditActionController(EditAction.ADD_ROW_BELOW));
        addColumnBeforeMenuItem.setUserData(new EditActionController(EditAction.ADD_COLUMN_BEFORE));
        addColumnAfterMenuItem.setUserData(new EditActionController(EditAction.ADD_COLUMN_AFTER));
        increaseRowSpanMenuItem.setUserData(new EditActionController(EditAction.INCREASE_ROW_SPAN));
        decreaseRowSpanMenuItem.setUserData(new EditActionController(EditAction.DECREASE_ROW_SPAN));
        increaseColumnSpanMenuItem.setUserData(new EditActionController(EditAction.INCREASE_COLUMN_SPAN));
        decreaseColumnSpanMenuItem.setUserData(new EditActionController(EditAction.DECREASE_COLUMN_SPAN));

//        phoneSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_335x600) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_335x600);
//            }
//        });
//        tabletSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_900x600) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_900x600);
//            }
//        });
//        qvgaSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_320x240) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_320x240);
//            }
//        });
//        vgaSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_640x480) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_640x480);
//            }
//        });
//        touchSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_1280x800) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_1280x800);
//            }
//        });
//        hdSetSizeMenuItem.setUserData(new EditActionController(EditAction.SET_SIZE_1920x1080) {
//            @Override
//            public void perform() {
//                super.perform();
//                updatePreviewWindowSize(Size.SIZE_1920x1080);
//            }
//        });

        // Add Effect submenu
        //updateAddEffectMenu();

        /*
         * Arrange menu
         */
        bringToFrontMenuItem.setUserData(new EditActionController(EditAction.BRING_TO_FRONT));
        bringToFrontMenuItem.setAccelerator(new KeyCharacterCombination("]", //NOCHECK
                KeyCombination.SHIFT_DOWN, modifier));
        sendToBackMenuItem.setUserData(new EditActionController(EditAction.SEND_TO_BACK));
        sendToBackMenuItem.setAccelerator(new KeyCharacterCombination("[", //NOCHECK
                KeyCombination.SHIFT_DOWN, modifier));
        bringForwardMenuItem.setUserData(new EditActionController(EditAction.BRING_FORWARD));
        bringForwardMenuItem.setAccelerator(
                new KeyCharacterCombination("]", modifier)); //NOCHECK
        sendBackwardMenuItem.setUserData(new EditActionController(EditAction.SEND_BACKWARD));
        sendBackwardMenuItem.setAccelerator(
                new KeyCharacterCombination("[", modifier)); //NOCHECK
        wrapInAnchorPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_ANCHOR_PANE));
        wrapInBorderPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_BORDER_PANE));
        wrapInButtonBarMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_BUTTON_BAR));
        wrapInDialogPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_DIALOG_PANE));
        wrapInFlowPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_FLOW_PANE));
        wrapInGroupMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_GROUP));
        wrapInGridPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_GRID_PANE));
        wrapInHBoxMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_HBOX));
        wrapInPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_PANE));
        wrapInScrollPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_SCROLL_PANE));
        wrapInSplitPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_SPLIT_PANE));
        wrapInStackPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_STACK_PANE));
        wrapInTabPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_TAB_PANE));
        wrapInTextFlowMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_TEXT_FLOW));
        wrapInTilePaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_TILE_PANE));
        wrapInTitledPaneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_TITLED_PANE));
        wrapInToolBarMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_TOOL_BAR));
        wrapInVBoxMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_VBOX));
        wrapInGroupMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_GROUP));
        wrapInSceneMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_SCENE));
        wrapInStageMenuItem.setUserData(new EditActionController(EditAction.WRAP_IN_STAGE));
        unwrapMenuItem.setUserData(new EditActionController(EditAction.UNWRAP));
        unwrapMenuItem.setAccelerator(
                new KeyCodeCombination(KeyCode.U, modifier));

        /*
         * Window menu : it is setup after the other menus
         */

        /*
         * Help menu
         */
//        helpMenuItem.setUserData(new DocumentControlActionController(DocumentControlAction.HELP));
//        helpMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.F1));

        /*
         * Put some generic handlers on each Menu and MenuItem.
         * For Insert and Window menu, we override with specific handlers.
         */
        for (Menu m : menuBar.getMenus()) {
            setupMenuItemHandlers(m);
        }

        windowMenu.setOnMenuValidation(onWindowMenuValidationHandler);

        setupMenuBar();
    }


    /*
     * Generic menu and item handlers
     */
    private void setupMenuItemHandlers(MenuItem i) {
        if (i instanceof Menu) {
            final Menu m = (Menu) i;
            m.setOnMenuValidation(onMenuValidationEventHandler);
            for (MenuItem child : m.getItems()) {
                setupMenuItemHandlers(child);
            }
        } else {
            i.setOnAction(onActionEventHandler);
            if (i.getAccelerator() != null) {
                keyToMenu.put(i.getAccelerator(), i);
            }
        }
    }

    private final EventHandler<Event> onMenuValidationEventHandler
            = t -> {
        assert t.getSource() instanceof Menu;
        handleOnMenuValidation((Menu) t.getSource());
    };

    private void handleOnMenuValidation(Menu menu) {
        for (MenuItem i : menu.getItems()) {

            if (dynamicMenu.contains(i)) {
                continue;
            }

            final boolean disable, selected;
            final String title;
            if (i.getUserData() instanceof MenuItemController) {
                final MenuItemController c = (MenuItemController) i.getUserData();
                boolean canPerform;
                try {
                    canPerform = c.canPerform();
                } catch(RuntimeException x) {
                    // This catch is protection against a bug in canPerform().
                    // It avoids to block all the items in the menu in case
                    // of crash in canPerform() (see DTL-6164).
                    canPerform = false;
                    final Exception xx
                            = new Exception(c.getClass().getSimpleName()
                            + ".canPerform() did break for menu item " + i, x); //NOCHECK
                    xx.printStackTrace();
                }
                disable = !canPerform;
                title = c.getTitle();
                selected = c.isSelected();
            } else {
                if (i instanceof Menu) {
                    disable = false;
                    selected = false;
                    title = null;
                } else {
                    disable = true;
                    selected = false;
                    title = null;
                }
            }
            i.setDisable(disable);
            if (title != null) {
                i.setText(title);
            }
            if (i instanceof RadioMenuItem) {
                final RadioMenuItem ri = (RadioMenuItem) i;
                ri.setSelected(selected);
            }
        }
    }

    private final EventHandler<ActionEvent> onActionEventHandler
            = t -> {
        assert t.getSource() instanceof MenuItem;
        handleOnActionMenu((MenuItem) t.getSource());
    };

    private void handleOnActionMenu(MenuItem i) {
        assert i.getUserData() instanceof MenuItemController;
        final MenuItemController c = (MenuItemController) i.getUserData();
        c.perform();
    }

    /*
     * Private (zoom menu)
     */

//    final static double[] scalingTable = {0.25, 0.50, 0.75, 1.00, 1.50, 2.0, 4.0};
//
//    private void updateZoomMenu() {
//        final double[] scalingTable = {0.25, 0.50, 0.75, 1.00, 1.50, 2.0, 4.0};
//
////        final MenuItem zoomInMenuItem = new MenuItem(I18N.getString("menu.title.zoom.in"));
////        zoomInMenuItem.setUserData(new ZoomInActionController());
////        zoomInMenuItem.setAccelerator(new KeyCharacterCombination("+", modifier)); //NOCHECK
////        zoomMenu.getItems().add(zoomInMenuItem);
////
////        final MenuItem zoomOutMenuItem = new MenuItem(I18N.getString("menu.title.zoom.out"));
////        zoomOutMenuItem.setUserData(new ZoomOutActionController());
////        zoomOutMenuItem.setAccelerator(new KeyCharacterCombination("/", modifier));  //NOCHECK
////        zoomMenu.getItems().add(zoomOutMenuItem);
//
//        zoomMenu.getItems().add(new SeparatorMenuItem());
//
//        for (int i = 0; i < scalingTable.length; i++) {
//            final double scaling = scalingTable[i];
//            final String title = String.format("%.0f%%", scaling * 100); //NOCHECK
//            final RadioMenuItem mi = new RadioMenuItem(title);
//            mi.setUserData(new SetZoomActionController(scaling));
//            zoomMenu.getItems().add(mi);
//        }
//    }
//
//
//    private static int findZoomScaleIndex(double zoomScale) {
//        int result = -1;
//
//        for (int i = 0; i < scalingTable.length; i++) {
//            if (MathUtils.equals(zoomScale, scalingTable[i])) {
//                result = i;
//                break;
//            }
//        }
//
//        return result;
//    }

    /*
     * Private (window menu)
     */
    private final EventHandler<Event> onWindowMenuValidationHandler
            = t -> {
        assert t.getSource() == windowMenu;
        handleOnWindowMenuValidation();
    };

    private void handleOnWindowMenuValidation() {
        windowMenu.getItems().clear();

        final List<Document> documentWindowControllers
                = main.getDocumentWindowControllers();
        if (documentWindowControllers.isEmpty()) {
            // Adds the "No window" menu item
            windowMenu.getItems().add(makeWindowMenuItem(null));
        } else {
            final List<Document> sortedControllers
                    = new ArrayList<>(documentWindowControllers);
            Collections.sort(sortedControllers, new Document.TitleComparator());

            for (Document dwc : sortedControllers) {
                windowMenu.getItems().add(makeWindowMenuItem(dwc));
            }
        }
    }

    private MenuItem makeWindowMenuItem(final Document dwc) {
        final RadioMenuItem result = new RadioMenuItem();
        if (dwc != null) {
            result.setText(dwc.getDocumentWindow().getStage().getTitle());
            result.setDisable(false);
            result.setSelected(dwc.getDocumentWindow().getStage().isFocused());
            result.setOnAction(new WindowMenuEventHandler(dwc));
        } else {
            result.setText(I18N.getString("menu.title.no.window"));
            result.setDisable(true);
            result.setSelected(false);
        }

        return result;
    }

    private static class WindowMenuEventHandler implements EventHandler<ActionEvent> {

        private final Document dwc;

        public WindowMenuEventHandler(Document dwc) {
            this.dwc = dwc;
        }

        @Override
        public void handle(ActionEvent t) {
            DocumentScope.setCurrentScope(dwc); //TODO realy necessary ?, check if onFocus is not sufficient
            dwc.getDocumentWindow().getStage().toFront();
        }
    }


//    class UndoActionController extends MenuItemController {
//
//        @Override
//        public boolean canPerform() {
//            boolean result;
//            if (documentWindowController == null
//                    || documentWindowController.getStage().isFocused() == false) {
//                result = false;
//            } else {
//                result = editor.canUndo();
//            }
//            return result;
//        }
//
//        @Override
//        public void perform() {
//            assert canPerform();
//            editor.undo();
//        }
//
//        @Override
//        public String getTitle() {
//            final StringBuilder result = new StringBuilder();
//            result.append(I18N.getString("menu.title.undo"));
//            if (canPerform()) {
//                result.append(" "); //NOCHECK
//                result.append(editor.getUndoDescription());
//            }
//            return result.toString();
//        }
//    }
//
//    class RedoActionController extends MenuItemController {
//
//        @Override
//        public boolean canPerform() {
//            boolean result;
//            if (documentWindowController == null
//                    || documentWindowController.getStage().isFocused() == false) {
//                result = false;
//            } else {
//                result = editor.canRedo();
//            }
//            return result;
//        }
//
//        @Override
//        public void perform() {
//            assert canPerform();
//            editor.redo();
//        }
//
//        @Override
//        public String getTitle() {
//            final StringBuilder result = new StringBuilder();
//            result.append(I18N.getString("menu.title.redo"));
//            if (canPerform()) {
//                result.append(" "); //NOCHECK
//                result.append(editor.getRedoDescription());
//            }
//            return result.toString();
//        }
//    }

    class EditActionController extends MenuItemController {

        private final EditAction editAction;

        public EditActionController(EditAction editAction) {
            this.editAction = editAction;
        }

        @Override
        public boolean canPerform() {
            boolean result;
            if (documentWindowController == null
                    || documentWindowController.getStage().isFocused() == false) {
                result = false;
            } else {
                result = editor.canPerformEditAction(editAction);
            }
            return result;
        }

        @Override
        public void perform() {
            assert canPerform() : "editAction=" + editAction;
            editor.performEditAction(editAction);
        }

    }

    class ControlActionController extends MenuItemController {

        private final ControlAction controlAction;

        public ControlActionController(ControlAction controlAction) {
            this.controlAction = controlAction;
        }

        @Override
        public boolean canPerform() {
            boolean result;
            if (documentWindowController == null) {
                result = false;
            } else {
                result = editor.canPerformControlAction(controlAction);
            }
            return result;
        }

        @Override
        public void perform() {
            assert canPerform() : "controlAction=" + controlAction;
            editor.performControlAction(controlAction);
        }

    }

    class DocumentEditActionController extends MenuItemController {

        private final DocumentEditAction editAction;

        public DocumentEditActionController(DocumentEditAction editAction) {
            this.editAction = editAction;
        }

        @Override
        public boolean canPerform() {
            boolean result;
            if (documentWindowController == null
                    || documentWindowController.getStage().isFocused() == false) {
                result = false;
            } else {
                result = document.canPerformEditAction(editAction);
            }
            return result;
        }

        @Override
        public void perform() {
            assert canPerform() : "editAction=" + editAction;
            document.performEditAction(editAction);
        }

    }

    class DocumentControlActionController extends MenuItemController {

        private final DocumentControlAction controlAction;

        public DocumentControlActionController(DocumentControlAction controlAction) {
            this.controlAction = controlAction;
        }

        @Override
        public boolean canPerform() {
            boolean result;
            if (documentWindowController == null) {
                result = false;
            } else {
                result = document.canPerformControlAction(controlAction);
            }
            return result;
        }

        @Override
        public void perform() {
            assert canPerform() : "controlAction=" + controlAction;
            document.performControlAction(controlAction);
        }

    }

    class ApplicationControlActionController extends MenuItemController {

        private final ApplicationControlAction controlAction;

        public ApplicationControlActionController(ApplicationControlAction controlAction) {
            this.controlAction = controlAction;
        }

        @Override
        public boolean canPerform() {
            return main.canPerformControlAction(controlAction, document);
        }

        @Override
        public void perform() {
            main.performControlAction(controlAction, document);
        }

    }


//    class SetZoomActionController extends MenuItemController {
//
//        private final double scaling;
//
//        public SetZoomActionController(double scaling) {
//            this.scaling = scaling;
//        }
//
//        @Override
//        public boolean canPerform() {
//            return (documentWindowController != null);
//        }
//
//        @Override
//        public void perform() {
//            final double currentScaling = content.getScaling();
//            if (MathUtils.equals(currentScaling, scaling) == false) {
//                content.setScaling(scaling);
//            }
//        }
//
//        @Override
//        public boolean isSelected() {
//            boolean result;
//
//            if (documentWindowController == null) {
//                result = false;
//            } else {
//                final double currentScaling = content.getScaling();
//                result = MathUtils.equals(currentScaling, scaling);
//            }
//
//            return result;
//        }
//
//    }
//
//    class ZoomInActionController extends MenuItemController {
//
//        @Override
//        public boolean canPerform() {
//            boolean result;
//            if (documentWindowController == null) {
//                result = false;
//            } else {
//                final int currentScalingIndex = findZoomScaleIndex(content.getScaling());
//                result = currentScalingIndex+1 < scalingTable.length;
//            }
//            return result;
//        }
//
//        @Override
//        public void perform() {
//            final int currentScalingIndex = findZoomScaleIndex(content.getScaling());
//            final double newScaling = scalingTable[currentScalingIndex+1];
//            content.setScaling(newScaling);
//        }
//
//    }


//    class ZoomOutActionController extends MenuItemController {
//
//        @Override
//        public boolean canPerform() {
//            boolean result;
//            if (documentWindowController == null) {
//                result = false;
//            } else {
//                final int currentScalingIndex = findZoomScaleIndex(content.getScaling());
//                result = 0 <= currentScalingIndex-1;
//            }
//            return result;
//        }
//
//        @Override
//        public void perform() {
//            final int currentScalingIndex = findZoomScaleIndex(content.getScaling());
//            final double newScaling = scalingTable[currentScalingIndex-1];
//            content.setScaling(newScaling);
//        }
//
//    }

//    private void updatePreviewWindowSize(Size size) {
//        if (previewWindowController.getStage().isShowing()) {
//            previewWindowController.setSize(size);
//        }
//    }
//
//    class SetSizeActionController extends MenuItemController {
//
//        private final Size size;
//
//        public SetSizeActionController(Size size) {
//            this.size = size;
//        }
//
//        @Override
//        public boolean canPerform() {
//            boolean res = (fxomDocument != null)
//                    && previewWindowController.getStage().isShowing()
//                    && ! fxomDocument.is3D()
//                    && fxomDocument.isNode()
//                    && previewWindowController.sizeDoesFit(size);
//            return res;
//        }
//
//        @Override
//        public void perform() {
//            assert previewWindowController != null;
//            previewWindowController.setSize(size);
//        }
//
//        @Override
//        public boolean isSelected() {
//            boolean res;
//
//            if (previewWindowController == null) {
//                res = false;
//            } else {
//                Size currentSize = previewWindowController.getSize();
//                res = (size == currentSize)
//                        && previewWindowController.getStage().isShowing()
//                        && ! previewWindowController.userResizedPreviewWindow()
//                        && ! fxomDocument.is3D()
//                        && fxomDocument.isNode();
//            }
//
//            return res;
//        }
//
//        @Override
//        public String getTitle() {
//            if (documentWindowController == null) {
//                return null;
//            }
//
//            if (size == Size.SIZE_PREFERRED) {
//                String title = I18N.getString("menu.title.size.preferred");
//
//                if (previewWindowController != null
//                        && previewWindowController.getStage().isShowing()
//                        && ! fxomDocument.is3D()
//                        && fxomDocument.isNode()) {
//                        title = I18N.getString("menu.title.size.preferred.with.value",
//                                getStringFromDouble(previewWindowController.getRoot().prefWidth(-1)),
//                                getStringFromDouble(previewWindowController.getRoot().prefHeight(-1)));
//                }
//
//                return title;
//            } else {
//                return null;
//            }
//        }
//    }

    public MenuItem getMenuItem(KeyCombination key) {
        return keyToMenu.get(key);
    }

    @Override
    public Set<KeyCombination> getAccelerators() {
        return keyToMenu.keySet();
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

}
