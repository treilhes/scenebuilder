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

import org.scenebuilder.fxml.api.HierarchyMask.Accessory;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.AbstractJob;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.library.LibraryItem;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.ui.menu.DefaultMenu;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuAttachment;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuBuilder;
import com.oracle.javafx.scenebuilder.api.ui.menu.MenuProvider;
import com.oracle.javafx.scenebuilder.api.ui.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.controllibrary.controller.LibraryController;
import com.oracle.javafx.scenebuilder.controllibrary.library.ControlLibrary;
import com.oracle.javafx.scenebuilder.controllibrary.library.builtin.LibraryItemImpl;
import com.oracle.javafx.scenebuilder.controllibrary.panel.LibraryListCell;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.selection.job.InsertAsAccessoryJob;
import com.oracle.javafx.scenebuilder.selection.job.SetDocumentRootJob;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(nameKey = "action.name.reveal.custom.folder", descriptionKey = "action.description.reveal.custom.folder")
public class InsertControlAction extends AbstractAction {

    private static Logger logger = LoggerFactory.getLogger(InsertControlAction.class);

    private final SetDocumentRootJob.Factory setDocumentRootJobFactory;
    private final InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory;
    private final JobManager jobManager;
    private final FxmlDocumentManager documentManager;
    private final Selection selection;
    private final DesignHierarchyMask.Factory designMaskFactory;

    private LibraryItem libraryItem;

    public InsertControlAction(
            ActionExtensionFactory extensionFactory,
            FxmlDocumentManager documentManager,
            Selection selection,
            JobManager jobManager,
            DesignHierarchyMask.Factory designMaskFactory,
            SetDocumentRootJob.Factory setDocumentRootJobFactory,
            InsertAsAccessoryJob.Factory insertAsAccessoryJobFactory) {
        super(extensionFactory);
        this.setDocumentRootJobFactory = setDocumentRootJobFactory;
        this.insertAsAccessoryJobFactory = insertAsAccessoryJobFactory;
        this.documentManager = documentManager;
        this.selection = selection;
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
                    final AbstractJob job = setDocumentRootJobFactory.getJob(newItemRoot, true /* usePredefinedSize */, "unused"); // NOI18N
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

                    Accessory targetAccessory = selection.getTargetAccessory();
                    if (targetAccessory == null && targetCandidate != null) {
                        targetAccessory = designMaskFactory.getMask(targetCandidate).getMainAccessory();
                    }

System.out.println();
                    final AbstractJob job = insertAsAccessoryJobFactory.getJob(newItemRoot, targetCandidate, targetAccessory);
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
        final AbstractJob job;
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
            final String description = I18N.getString("drop.job.insert.library.item", libraryItem.getName());
            job = setDocumentRootJobFactory.getJob(newObject, true /* usePredefinedSize */, description);

        } else {
            if (selection.isEmpty() || selection.isSelected(rootObject)) {
                // No selection or root is selected -> we insert below root
                target = rootObject;
            } else {
                // Let's use the common parent of the selected objects.
                // It might be null if selection holds some non FXOMObject entries
                target = selection.getAncestor();
            }

            Accessory targetAccessory = selection.getTargetAccessory();
            if (targetAccessory == null) {
                targetAccessory = designMaskFactory.getMask(target).getMainAccessory();
            }


            job = insertAsAccessoryJobFactory.getJob(newObject, target, targetAccessory);
        }

        jobManager.push(job);

        // TODO remove comment
        // WarnThemeAlert.showAlertIfRequired(this, newObject, ownerWindow);

        return ActionStatus.DONE;
    }


    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
    @Lazy
    // FIXME : need to implement the controls update on library update
    public class InsertMenuProvider implements MenuProvider {

        public final static String MENU_ID = "insertMenu";
        public final static String CUSTOM_MENU_ID = "insertCustomMenu";

        private final LibraryController libraryMenuController;
        private final ControlLibrary library;

        private final MenuBuilder menuBuilder;
        private final ActionFactory actionFactory;

        public InsertMenuProvider(
                MenuBuilder menuBuilder,
                ActionFactory actionFactory,
                @Lazy LibraryController libraryMenuController,
                @Lazy ControlLibrary library
                ) {
            this.library = library;
            this.menuBuilder = menuBuilder;
            this.libraryMenuController = libraryMenuController;
            this.actionFactory = actionFactory;
        }

        @Override
        public List<MenuAttachment> menus() {
            Menu insertMenu = menuBuilder.menu().withId(MENU_ID).withTitle("menu.title.insert").build();
            populate(insertMenu);
            MenuAttachment attachment = MenuAttachment.create(insertMenu, DefaultMenu.VIEW_MENU_ID, PositionRequest.AsNextSibling);
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
                Menu menu = menuBuilder.menu().withTitle(k).build();
                map.get(k).stream().sorted(Comparator.comparing(LibraryItemImpl::getName)).forEach(l -> {
                    InsertControlAction action = actionFactory.create(InsertControlAction.class);
                    action.setLibraryItem(l);

                    String label = l.getName() + LibraryListCell.makeQualifierLabel(l.getQualifier());

                    MenuItem mi = menuBuilder.menuItem().withAction(action).withTitle(label).build();

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