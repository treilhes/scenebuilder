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
package com.gluonhq.jfxapps.core.menu.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionGroup;
import com.gluonhq.jfxapps.core.api.job.JobManager;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.Attachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenuItemProvider;

import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Controller used to handle contextual menu in hierarchy and content view.
 */
@ApplicationInstanceSingleton
public class ContextMenuController implements com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenu {

    private static final Logger logger = LoggerFactory.getLogger(ContextMenuController.class);

    private ContextMenu contextMenu; // Initialized lazily

    private final ChangeListener<Number> jobManagerRevisionListener = (ob, o, n) -> jobManagerRevisionDidChange();

    private final List<ContextMenuItemProvider> contextMenuItemProviders;
    private final Selection selection;

    public ContextMenuController(JobManager jobManager, Selection selection,
            @Autowired(required = false) List<ContextMenuItemProvider> contextMenuItemProviders) {
        this.contextMenuItemProviders = contextMenuItemProviders;
        this.selection = selection;
        jobManager.revisionProperty().addListener(jobManagerRevisionListener);
    }

    @Override
    public ContextMenu getContextMenu() {
        if (contextMenu == null) {
            // Initialization of context menu
            contextMenu = new ContextMenu();
            contextMenu.setConsumeAutoHidingEvents(false);
        }
        return contextMenu;
    }

    private void addToMenuMap(Map<String, MenuItem> menuMap, MenuItem m) {
        if (m.getId() != null && !m.getId().isEmpty()) {
            if (menuMap.containsKey(m.getId())) {
                logger.error("Duplicate id in view menu map : {}", m.getId());
            } else {
                menuMap.put(m.getId(), m);
            }
        }

        if (m instanceof Menu) {
            ((Menu) m).getItems().forEach(mi -> {
                addToMenuMap(menuMap, mi);
            });
        }

    }

    public void buildMenu(SelectionGroup selectionGroup, ContextMenu contextMenu) {

        Map<String, MenuItem> menuMap = new HashMap<>();

        if (contextMenuItemProviders != null && !contextMenuItemProviders.isEmpty()) {

            List<ContextMenuItemProvider> validProviders = contextMenuItemProviders.stream()
                    .filter(mip -> mip != null && mip.contextMenuItems() != null && !mip.contextMenuItems().isEmpty())
                    .collect(Collectors.toList());

//           List<ContextMenuItemAttachment> validAttachments = validProviders.stream()
//                   .flatMap(mip -> mip.contextMenuItems().stream())
//                   .filter(ma -> ma.getSelectionGroup().isAssignableFrom(selectionGroup.getClass()))
//                   .filter(ma -> ma != null && ma.getPositionRequest() != null && ma.getMenuItem() != null)
//                   .sorted(Comparator.comparing(Attachment::getWeight))
//                   .collect(Collectors.toList());

            Map<String, List<ContextMenuItemAttachment>> validAttachments = validProviders.stream()
                    .flatMap(mip -> mip.contextMenuItems().stream())
                    .filter(ma -> ma.getSelectionGroup().isAssignableFrom(selectionGroup.getClass()))
                    .filter(ma -> ma != null && ma.getPositionRequest() != null && ma.getMenuItem() != null)
                    // .sorted(Comparator.comparing(Attachment::getWeight))
                    .collect(Collectors.groupingBy(m -> m.getTargetId() == null ? "" : m.getTargetId(), HashMap::new, Collectors.toList()));

            boolean hasInvalidProviders = contextMenuItemProviders.size() > validProviders.size();
            List<ContextMenuItemProvider> invalidProviders = hasInvalidProviders
                    ? contextMenuItemProviders.stream().filter(m -> !validProviders.contains(m))
                            .collect(Collectors.toList())
                    : null;

            boolean atLeastOneInserted = true;
            long iteration = 0;

            List<String> keys = Arrays.asList("", (String) null);
            List<String> nextKeys = new ArrayList<>();
            while (!validAttachments.isEmpty() && atLeastOneInserted) {
                atLeastOneInserted = false;

                for (String key : keys) {

                    List<ContextMenuItemAttachment> attachments = validAttachments.remove(key);
                    if (attachments == null) {
                        continue;
                    }

                    Collections.sort(attachments, Comparator.comparing(Attachment::getWeight));

                    for (ContextMenuItemAttachment ma : attachments) {
                        iteration++;
                        boolean inserted = false;
                        // ContextMenuItemAttachment ma = it.next();
                        MenuItem target = menuMap.get(ma.getTargetId());
                        boolean rootTarget = ma.getTargetId() == null || ma.getTargetId().isBlank();
                        if (target != null || rootTarget) {

                            if (ma.getMenuItem().getId() == null) {
                                ma.getMenuItem().setId(ma.getClass().getSimpleName());
                            }

                            try {
                                switch (ma.getPositionRequest()) {
                                case AsFirstSibling: {
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
                                    items.add(0, ma.getMenuItem());
                                    inserted = true;
                                    break;
                                }
                                case AsLastSibling: {
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
                                    items.add(ma.getMenuItem());
                                    inserted = true;
                                    break;
                                }
                                case AsPreviousSibling: {
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
                                    int index = items.indexOf(target);
                                    items.add(index, ma.getMenuItem());
                                    inserted = true;
                                    break;
                                }
                                case AsNextSibling: {
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
                                    int index = items.indexOf(target);
                                    items.add(index + 1, ma.getMenuItem());
                                    inserted = true;
                                    break;
                                }
                                case AfterPreviousSeparator: {
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
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
                                    ObservableList<MenuItem> items = rootTarget || target.getParentMenu() == null
                                            ? contextMenu.getItems()
                                            : target.getParentMenu().getItems();
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
                                    if (target instanceof Menu || rootTarget) {
                                        if (rootTarget) {
                                            contextMenu.getItems().add(0, ma.getMenuItem());
                                        } else {
                                            Menu m = (Menu) target;
                                            m.getItems().add(0, ma.getMenuItem());
                                        }
                                        inserted = true;
                                    }
                                    break;
                                }
                                case AsLastChild: {
                                    if (target instanceof Menu || rootTarget) {
                                        if (rootTarget) {
                                            contextMenu.getItems().add(ma.getMenuItem());
                                        } else {
                                            Menu m = (Menu) target;
                                            m.getItems().add(ma.getMenuItem());
                                        }
                                        inserted = true;
                                    }
                                    break;
                                }
                                default:
                                    throw new RuntimeException("Invalid position request for menuItem");

                                }
                            } catch (Exception e) {
                                logger.error("Unable to add the provided menuItem in the view", e);
                            }

                            if (inserted) {
                                addToMenuMap(menuMap, ma.getMenuItem());
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
                    logger.error("Invalid ContextMenuItemProviders submitted {}", mip.getClass().getName());
                });
            }
            if (validAttachments.size() > 0) {
                logger.error("Unable to add all the provided menuItem in the context menu");
                validAttachments.values().stream().flatMap(l -> l.stream()).forEach(ma -> {
                    logger.error("Unable to attach {} to id {} using {}", ma.getClass().getName(), ma.getTargetId(),
                            ma.getPositionRequest());
                });
            }

            logger.debug("Creation of the context menu completed after {} iterations", iteration);

            // link menu button showing event to menu item validation event
            contextMenu.setOnShowing((e) -> {
                for (MenuItem item : contextMenu.getItems()) {
                    EventHandler<Event> handler = item.getOnMenuValidation();
                    if (handler != null) {
                        handler.handle(e);
                    }
                }
            });
        }

    }

    /**
     * Updates the context menu items depending on the selection.
     */
    @Override
    public void updateContextMenuItems() {

        getContextMenu().getItems().clear();

        if (selection.isEmpty() == false) {
            final SelectionGroup asg = selection.getGroup();
            buildMenu(asg, contextMenu);
        }
    }

    private void jobManagerRevisionDidChange() {
        // FXOMDocument has been modified by a job.
        if (contextMenu != null && contextMenu.isShowing()) {
            contextMenu.hide();
        }
    }

}