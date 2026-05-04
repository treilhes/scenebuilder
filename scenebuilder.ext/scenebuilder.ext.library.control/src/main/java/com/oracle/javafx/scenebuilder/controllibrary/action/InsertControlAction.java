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
package com.oracle.javafx.scenebuilder.controllibrary.action;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.selection.SbSelectionJobsFactory;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryListCell;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstancePrototype;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.emc4j.boot.api.context.annotation.Lazy;
import com.treilhes.jfxplace.core.api.action.AbstractAction;
import com.treilhes.jfxplace.core.api.action.ActionExtensionFactory;
import com.treilhes.jfxplace.core.api.action.ActionFactory;
import com.treilhes.jfxplace.core.api.action.ActionMeta;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.FxomSelection;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.SelectionJobsFactory;
import com.treilhes.jfxplace.core.api.fxom.editor.selection.TargetSelection;
import com.treilhes.jfxplace.core.api.fxom.library.LibraryItem;
import com.treilhes.jfxplace.core.api.fxom.mask.Accessory;
import com.treilhes.jfxplace.core.api.fxom.mask.FXOMObjectMask;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.job.Job;
import com.treilhes.jfxplace.core.api.job.JobManager;
import com.treilhes.jfxplace.core.api.subjects.ApplicationInstanceEvents;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuBuilder;
import com.treilhes.jfxplace.core.api.ui.controller.menu.MenuProvider;
import com.treilhes.jfxplace.core.api.ui.controller.menu.PositionRequest;
import com.treilhes.jfxplace.core.fxom.FXOMDocument;
import com.treilhes.jfxplace.core.fxom.FXOMObject;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@ApplicationInstancePrototype
@ActionMeta(nameKey = "action.name.reveal.custom.folder", descriptionKey = "action.description.reveal.custom.folder")
public class InsertControlAction extends AbstractAction {

    private static Logger logger = LoggerFactory.getLogger(InsertControlAction.class);

    private final SelectionJobsFactory selectionJobsFactory;
    private final SbSelectionJobsFactory sbSelectionJobsFactory;
    private final JobManager jobManager;
    private final ApplicationInstanceEvents documentManager;
    private final FxomSelection selection;
    private final TargetSelection<?> targetSelection;
    private final FXOMObjectMask.Factory designMaskFactory;

    private LibraryItem libraryItem;



    public InsertControlAction(
            I18N i18n,
            ActionExtensionFactory extensionFactory,
            ApplicationInstanceEvents documentManager,
            FxomSelection selection,
            TargetSelection<?> targetSelection,
            JobManager jobManager,
            FXOMObjectMask.Factory designMaskFactory,
            SbSelectionJobsFactory sbSelectionJobsFactory,
            SelectionJobsFactory selectionJobsFactory) {
        super(i18n, extensionFactory);
        this.sbSelectionJobsFactory = sbSelectionJobsFactory;
        this.selectionJobsFactory = selectionJobsFactory;
        this.documentManager = documentManager;
        this.selection = selection;
        this.targetSelection = targetSelection;
        this.jobManager = jobManager;
        this.designMaskFactory = designMaskFactory;
    }

    protected LibraryItem getLibraryItem() {
        return libraryItem;
    }

    public void setLibraryItem(LibraryItem libraryItem) {
        this.libraryItem = libraryItem;
    }


    /**
     * Returns true if the 'insert' action is permitted with the specified library
     * item.
     *
     * @return true if the 'insert' action is permitted.
     */
    @Override
    public boolean canPerform() {
        final FXOMObject targetCandidate;
        final boolean result;
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        if (fxomDocument == null) {
            result = false;
        } else {
            assert (fxomDocument.getClassLoader() != null);
            // TODO classloader provided by fxmlDocument, good or not?
            final FXOMDocument newItemDocument = libraryItem.instantiate(fxomDocument.getClassLoader());
            if (newItemDocument == null) {
                // For some reason, library is unable to instantiate this item
                result = false;
            } else {
                final FXOMObject newItemRoot = newItemDocument.getFxomRoot();
                newItemRoot.moveToFxomDocument(fxomDocument);
                assert newItemDocument.getFxomRoot() == null;
                final FXOMObject rootObject = fxomDocument.getFxomRoot();
                if (rootObject == null) { // Empty document
                    final Job job = sbSelectionJobsFactory.setDocumentRoot(newItemRoot, true /* usePredefinedSize */);
                    job.setDescription("unused"); // NOI18N
                    result = job.isExecutable();
                } else {

                    if (selection.isEmpty() || selection.isSelected(rootObject)) {
                        // No selection or root is selected -> we insert below root
                        targetCandidate = rootObject;
                    } else {
                        // Let's use the common parent of the selected objects.
                        // It might be null if selection holds some non FXOMObject entries
                        targetCandidate = selection.getAncestor();
                    }

                    Accessory targetAccessory = targetSelection.getTargetAccessory();
                    if (targetAccessory == null && targetCandidate != null) {
                        targetAccessory = designMaskFactory.getMask(targetCandidate).getMainAccessory();
                    }

                    final Job job = selectionJobsFactory.insertAsAccessory(newItemRoot, targetCandidate, targetAccessory);
                    result = job.isExecutable();
                }
            }
        }

        return result;
    }

    /**
     * Performs the 'insert' edit action. This action creates an object matching the
     * specified library item and insert it in the document (according the selection
     * state).
     *
     */
    @Override
    public ActionStatus doPerform() {
        final Job job;
        final FXOMObject target;

        assert canPerform(); // (1)

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

     // TODO classloader provided by fxmlDocument, good or not?
        final FXOMDocument newItemDocument = libraryItem.instantiate(fxomDocument.getClassLoader());
        assert newItemDocument != null; // Because (1)
        final FXOMObject newObject = newItemDocument.getFxomRoot();
        assert newObject != null;
        newObject.moveToFxomDocument(fxomDocument);
        final FXOMObject rootObject = fxomDocument.getFxomRoot();
        if (rootObject == null) { // Empty document
            final String description = getI18n().getString("drop.job.insert.library.item", libraryItem.getName());
            job = sbSelectionJobsFactory.setDocumentRoot(newObject, true /* usePredefinedSize */);
            job.setDescription(description);

        } else {
            if (selection.isEmpty() || selection.isSelected(rootObject)) {
                // No selection or root is selected -> we insert below root
                target = rootObject;
            } else {
                // Let's use the common parent of the selected objects.
                // It might be null if selection holds some non FXOMObject entries
                target = selection.getAncestor();
            }

            var targetAccessory = targetSelection.getTargetAccessory();
            if (targetAccessory == null) {
                targetAccessory = designMaskFactory.getMask(target).getMainAccessory();
            }

            job = selectionJobsFactory.insertAsAccessory(newObject, target, targetAccessory);
        }

        jobManager.push(job);

        // TODO remove comment
        // WarnThemeAlert.showAlertIfRequired(this, newObject, ownerWindow);

        return ActionStatus.DONE;
    }


    @ApplicationInstanceSingleton
    // FIXME : need to implement the controls update on library update
    public class InsertMenuProvider implements MenuProvider {

        public static final String MENU_ID = "insertMenu";
        public static final String CUSTOM_MENU_ID = "insertCustomMenu";

        private final ControlLibrary library;

        private final MenuBuilder menuBuilder;
        private final ActionFactory actionFactory;

        public InsertMenuProvider(
                MenuBuilder menuBuilder,
                ActionFactory actionFactory,
                @Lazy ControlLibrary library
                ) {
            this.library = library;
            this.menuBuilder = menuBuilder;
            this.actionFactory = actionFactory;
        }

        @Override
        public List<MenuAttachment> menus() {
            Menu insertMenu = menuBuilder.menu().id(MENU_ID).title("menu.title.insert").build();
            populate(insertMenu);
            MenuAttachment attachment = MenuAttachment.create(insertMenu, DefaultMenu.View.ID, PositionRequest.AsNextSibling);
            return Arrays.asList(attachment);
        }

        /**
         * @param insertMenu
         */
        private void populate(Menu insertMenu) {
            insertMenu.getItems().clear();

            Map<String, List<LibraryItemImpl>> map = library.getItems().stream()
                .collect(Collectors.groupingBy(LibraryItem::getSection));

            map.keySet().stream().sorted().forEach(k -> {
                Menu menu = menuBuilder.menu().title(k).build();
                map.get(k).stream().sorted(Comparator.comparing(LibraryItemImpl::getName)).forEach(l -> {
                    InsertControlAction action = actionFactory.create(InsertControlAction.class);
                    action.setLibraryItem(l);

                    String label = l.getName() + LibraryListCell.makeQualifierLabel(l.getQualifier());

                    MenuItem mi = menuBuilder.menuItem().action(action).title(label).build();

                    try {
                        Image image = new Image(l.getIconURL().openStream());
                        ImageView imageView = new ImageView(image);
                        mi.setGraphic(imageView);
                    } catch (IOException e) {
                        logger.error("Unable to iconize control {}", l.getName(), e);
                    }
                    menu.getItems().add(mi);
                });
                insertMenu.getItems().add(menu);
            });

        }

    }
}