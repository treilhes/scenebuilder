/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.DocumentScope;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;
import com.oracle.javafx.scenebuilder.api.application.ApplicationInstance;
import com.oracle.javafx.scenebuilder.api.application.InstancesManager;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.menu.Attachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuItemProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuProvider;
import com.oracle.javafx.scenebuilder.api.util.FXMLUtils;

import jakarta.annotation.PostConstruct;
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
import javafx.scene.layout.StackPane;

/**
 *
 */
@com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton
//@Conditional(EditorPlatform.IS_MAC_CONDITION.class)
public class MenuBarController implements com.oracle.javafx.scenebuilder.api.ui.menu.MenuBar {
    private final static Logger logger = LoggerFactory.getLogger(MenuBarController.class);

    private static MenuBarController systemMenuBarController; // For Mac only

    @FXML
    private MenuBar menuBar;
    @FXML
    private Menu windowMenu;

    Map<String, MenuItem> menuMap = null;

    private final SceneBuilderManager sceneBuilderManager;
    private final Optional<List<MenuProvider>> menuProviders;
    private final Optional<List<MenuItemProvider>> menuItemProviders;

    private final InstancesManager main;

    public MenuBarController(
            SceneBuilderManager sceneBuilderManager,
            Optional<List<MenuProvider>> menuProviders,
            Optional<List<MenuItemProvider>> menuItemProviders,
            InstancesManager main) {
        this.sceneBuilderManager = sceneBuilderManager;
        this.menuProviders = menuProviders;
        this.menuItemProviders = menuItemProviders;
        this.main = main;
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
                logger.error("Duplicate id in menu map : {}", m.getId());
            } else {
                menuMap.put(m.getId(), m);
            }
        }

        if (m instanceof Menu) {
            ((Menu) m).getItems().forEach(mi -> {
                addToMenuMap(mi);
            });
        }

    }

    @PostConstruct
    public void init() throws Exception {
        systemMenuBarController = this;
        // setupMenuBar()
    }

    private void setupMenuBar() {
        MenuBar menuBar = getMenuBar();
        buildMenuMap(menuBar);
        populateMenus();
        populateMenuItems();
    }

    private void populateMenus() {
        if (menuProviders.isPresent() && !menuProviders.get().isEmpty()) {

            List<MenuProvider> providers = menuProviders.get();
            List<MenuProvider> validProviders = providers.stream()
                    .filter(mp -> mp != null && mp.menus() != null && !mp.menus().isEmpty())
                    .collect(Collectors.toList());

            List<MenuAttachment> validAttachments = validProviders.stream().flatMap(mp -> mp.menus().stream())
                    .filter(ma -> ma != null && ma.getPositionRequest() != null && ma.getMenu() != null)
                    .sorted(Comparator.comparing(Attachment::getWeight)).collect(Collectors.toList());

            boolean hasInvalidProviders = providers.size() > validProviders.size();
            List<MenuProvider> invalidProviders = hasInvalidProviders
                    ? providers.stream().filter(m -> !validProviders.contains(m)).collect(Collectors.toList())
                    : null;

            boolean atLeastOneInserted = true;
            long iteration = 0;
            while (!validAttachments.isEmpty() && atLeastOneInserted) {
                atLeastOneInserted = false;

                ListIterator<MenuAttachment> it = validAttachments.listIterator();
                while (it.hasNext()) {
                    iteration++;
                    boolean inserted = false;
                    MenuAttachment ma = it.next();
                    MenuItem targetCandidate = menuMap.get(ma.getTargetId());

                    if (targetCandidate != null && !Menu.class.isAssignableFrom(targetCandidate.getClass())) {
                        continue;
                    }

                    Menu target = (Menu) targetCandidate;

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
                        case BeforePreviousSeparator:
                        case AfterPreviousSeparator:
                        case BeforeNextSeparator:
                        case AfterNextSeparator: {
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
                        logger.error("Unable to add all the provided menu in the menuBar", e);
                    }

                    if (inserted) {
                        addToMenuMap(menu);
                        it.remove();
                        atLeastOneInserted = true;
                    }

                }
            }

            if (invalidProviders != null) {
                invalidProviders.forEach(mip -> {
                    logger.error("Invalid MenuProviders submitted {}", mip.getClass().getName());
                });
            }
            if (validAttachments.size() > 0) {
                logger.error("Unable to add all the provided menu in the menuBar");
                validAttachments.forEach(ma -> {
                    logger.error("Unable to attach {} to id {} using {}", ma.getMenu().getId(), ma.getTargetId(),
                            ma.getPositionRequest());
                });
            }

            logger.debug("Creation of the menu bar first level completed after {} iterations", iteration);
        }
    }

    private void populateMenuItems() {
        if (menuItemProviders.isPresent() && !menuItemProviders.get().isEmpty()) {

            List<MenuItemProvider> providers = menuItemProviders.get();
            List<MenuItemProvider> validProviders = providers.stream()
                    .filter(mip -> mip != null && mip.menuItems() != null && !mip.menuItems().isEmpty())
                    .collect(Collectors.toList());

//           List<MenuItemAttachment> validAttachments = validProviders.stream()
//                   .flatMap(mip -> mip.menuItems().stream())
//                   .filter(ma -> ma != null && ma.getTargetId() != null && ma.getPositionRequest() != null && ma.getMenuItem() != null)
//                   .sorted(Comparator.comparing(Attachment::getWeight))
//                   .collect(Collectors.toList());

            Map<String, List<MenuItemAttachment>> validAttachments = validProviders.stream()
                    .flatMap(mip -> mip.menuItems().stream())
                    .filter(ma -> ma != null && ma.getTargetId() != null && ma.getPositionRequest() != null
                            && ma.getMenuItem() != null)
                    // .sorted(Comparator.comparing(Attachment::getWeight))
                    .collect(Collectors.groupingBy(m -> m.getTargetId() == null ? "" : m.getTargetId(), HashMap::new,
                            Collectors.toList()));

            boolean hasInvalidProviders = providers.size() > validProviders.size();
            List<MenuItemProvider> invalidProviders = hasInvalidProviders
                    ? providers.stream().filter(m -> !validProviders.contains(m)).collect(Collectors.toList())
                    : null;

            boolean atLeastOneInserted = true;
            long iteration = 0;

            List<String> keys = menuBar.getMenus().stream().map(Menu::getId).collect(Collectors.toList());
            List<String> nextKeys = new ArrayList<>();
            while (!validAttachments.isEmpty() && atLeastOneInserted) {
                atLeastOneInserted = false;

                for (String key : keys) {

                    List<MenuItemAttachment> attachments = validAttachments.remove(key);
                    if (attachments == null) {
                        continue;
                    }

                    Collections.sort(attachments, Comparator.comparing(Attachment::getWeight));

                    for (MenuItemAttachment ma : attachments) {
                        iteration++;
                        boolean inserted = false;
                        // MenuItemAttachment ma = it.next();
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
                                case BeforePreviousSeparator: {
                                    ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                    int index = items.indexOf(target);
                                    int insertAt = 0;
                                    for (int i = index; i >= 0; i--) {
                                        if (SeparatorMenuItem.class.isAssignableFrom(items.get(i).getClass())) {
                                            insertAt = i;
                                            break;
                                        }
                                    }
                                    items.add(insertAt, ma.getMenuItem());
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
                                case AfterNextSeparator: {
                                    ObservableList<MenuItem> items = target.getParentMenu().getItems();
                                    int index = items.indexOf(target);
                                    int insertAt = items.size();
                                    for (int i = index; i < items.size(); i++) {
                                        if (SeparatorMenuItem.class.isAssignableFrom(items.get(i).getClass())) {
                                            insertAt = i + 1;
                                            break;
                                        }
                                    }
                                    items.add(insertAt, ma.getMenuItem());
                                    inserted = true;
                                    break;
                                }
                                case AsFirstChild: {
                                    if (target instanceof Menu) {
                                        Menu m = (Menu) target;
                                        m.getItems().add(0, ma.getMenuItem());
                                        inserted = true;
                                    }
                                    break;
                                }
                                case AsLastChild: {
                                    if (target instanceof Menu) {
                                        Menu m = (Menu) target;
                                        m.getItems().add(ma.getMenuItem());
                                        inserted = true;
                                    }
                                    break;
                                }
                                default:
                                    throw new RuntimeException("Invalid position request for menuItem");

                                }
                            } catch (Exception e) {
                                logger.error("Unable to add the provided menuItem in the menuBar", e);
                            }

                            if (inserted) {
                                addToMenuMap(ma.getMenuItem());
                                nextKeys.add(ma.getMenuItem().getId());
                                atLeastOneInserted = true;
                            }
                        }
                    }
                }

                keys = nextKeys;
                nextKeys = new ArrayList<>();
            }

            if (invalidProviders != null) {
                invalidProviders.forEach(mip -> {
                    logger.error("Invalid MenuItemProviders submitted {}", mip.getClass().getName());
                });
            }
            if (validAttachments.size() > 0) {
                logger.error("Unable to add all the provided menuItem in the menuBar");
                validAttachments.values().stream().flatMap(l -> l.stream()).forEach(ma -> {
                    logger.error("Unable to attach {} to id {} using {}", ma.getMenuItem().getId(), ma.getTargetId(),
                            ma.getPositionRequest());
                });
            }
            logger.debug("Creation of the menu bar completed after {} iterations", iteration);
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
        // TODO uncomment below and springify
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
        assert windowMenu != null;

        /*
         * To make MenuBar.fxml editable with SB 1.1, the menu bar is enclosed in a
         * StackPane. This stack pane is useless now. So we unwrap the menu bar and make
         * it the panel root.
         */
        final StackPane rootStackPane = (StackPane) menuBar.getParent();
        rootStackPane.getChildren().remove(menuBar);

        /*
         * On Mac, move the menu bar on the desktop and remove the Quit item from the
         * File menu
         */
        if (JfxAppsPlatform.IS_MAC) {
            menuBar.setUseSystemMenuBar(true);
            // SB-269
            menuBar.useSystemMenuBarProperty().addListener((obs, ov, nv) -> {
                if (!nv) {
                    // Restore System MenuBar
                    menuBar.setUseSystemMenuBar(true);
                }
            });
        }

        windowMenu.setOnMenuValidation(onWindowMenuValidationHandler);

        setupMenuBar();
    }

    /*
     * Private (window menu)
     */
    private final EventHandler<Event> onWindowMenuValidationHandler = t -> {
        assert t.getSource() == windowMenu;
        handleOnWindowMenuValidation();
    };

    private void handleOnWindowMenuValidation() {
        windowMenu.getItems().clear();

        final List<ApplicationInstance> documentWindowControllers = main.getInstances();
        if (documentWindowControllers.isEmpty()) {
            // Adds the "No window" menu item
            windowMenu.getItems().add(makeWindowMenuItem(null));
        } else {
            final List<ApplicationInstance> sortedControllers = new ArrayList<>(documentWindowControllers);
            Collections.sort(sortedControllers, new ApplicationInstance.TitleComparator());

            for (ApplicationInstance dwc : sortedControllers) {
                windowMenu.getItems().add(makeWindowMenuItem(dwc));
            }
        }
    }

    private MenuItem makeWindowMenuItem(final ApplicationInstance dwc) {
        final RadioMenuItem result = new RadioMenuItem();
        if (dwc != null) {
            result.setText(dwc.getDocumentWindow().getStage().getTitle());
            result.setDisable(false);
            result.setSelected(dwc.getDocumentWindow().getStage().isFocused());
            result.setOnAction(new WindowMenuEventHandler(dwc, sceneBuilderManager));
        } else {
            result.setText(I18N.getString("menu.title.no.window"));
            result.setDisable(true);
            result.setSelected(false);
        }

        return result;
    }

    private static class WindowMenuEventHandler implements EventHandler<ActionEvent> {

        private final ApplicationInstance dwc;
        private final SceneBuilderManager sceneBuilderManager;

        public WindowMenuEventHandler(ApplicationInstance dwc, SceneBuilderManager sceneBuilderManager) {
            this.dwc = dwc;
            this.sceneBuilderManager = sceneBuilderManager;
        }

        @Override
        public void handle(ActionEvent t) {
            DocumentScope.setCurrentScope(dwc); // TODO realy necessary ?, check if onFocus is not sufficient
            sceneBuilderManager.documentScoped().onNext(dwc);
            dwc.getDocumentWindow().getStage().toFront();
        }
    }

}
