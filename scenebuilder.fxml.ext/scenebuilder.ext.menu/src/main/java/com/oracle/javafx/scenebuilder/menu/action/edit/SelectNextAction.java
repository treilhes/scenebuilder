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
package com.oracle.javafx.scenebuilder.menu.action.edit;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.AbstractSelectionGroup;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.menu.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menu.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.shortcut.annotation.Accelerator;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about")

@MenuItemAttachment(
        id = SelectNextAction.MENU_ID,
        targetMenuId = SelectParentAction.MENU_ID,
        label = "menu.title.select.next",
        positionRequest = PositionRequest.AsNextSibling)
@Accelerator(accelerator = "CTRL+RIGHT")
public class SelectNextAction extends AbstractAction {

    public final static String MENU_ID = "selectNextMenu";

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final Selection selection;

    public SelectNextAction(
            ActionExtensionFactory extensionFactory,
            DocumentWindow documentWindow,
            DocumentManager documentManager,
            InlineEdit inlineEdit,
            Selection selection) {
        super(extensionFactory);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.selection = selection;
    }

    /**
     * Returns true if the selection is single and the container of the selected
     * object container contains a child next to the selected one.
     *
     * @return if the selection is single and the container of the selected object
     *         container contains a child next to the selected one.
     */
    @Override
    public boolean canPerform() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        if (fxomDocument == null || fxomDocument.getFxomRoot() == null || selection.isEmpty()) {
            return false;
        }
        final AbstractSelectionGroup asg = selection.getGroup();
        if (asg.getItems().size() != 1) {
            return false;
        }
        return asg.getSiblings().size() > 1;
//
//        if (asg instanceof ObjectSelectionGroup) {
//            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
//            final Set<FXOMObject> items = osg.getItems();
//            if (items.size() != 1) {
//                return false;
//            }
//            final FXOMObject selectedObject = items.iterator().next();
//            return selectedObject.getNextSlibing() != null;
//        } else if (asg instanceof GridSelectionGroup) {
//            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
//            final Set<Integer> indexes = gsg.getIndexes();
//            if (indexes.size() != 1) {
//                return false;
//            }
//            final FXOMObject gridPane = gsg.getHitItem();
//            final GridPaneHierarchyMask mask = gridPaneHierarchyMaskFactory.getMask(gridPane);
//            int size = 0;
//            switch (gsg.getType()) {
//            case ROW:
//                size = mask.getRowsSize();
//                break;
//            case COLUMN:
//                size = mask.getColumnsSize();
//                break;
//            default:
//                assert false;
//                break;
//            }
//            final int index = indexes.iterator().next();
//            return index < size - 1;
//        } else {
//            assert selection.getGroup() == null : "Add implementation for " + selection.getGroup(); // NOCHECK
//        }
//
//        return false;
    }

    /**
     * Performs the select next control action.
     */
    @Override
    public ActionStatus doPerform() {
        assert canPerform(); // (1)

        selection.selectNext();
//        final AbstractSelectionGroup asg = selection.getGroup();
//        if (asg instanceof ObjectSelectionGroup) {
//            final ObjectSelectionGroup osg = (ObjectSelectionGroup) asg;
//            final Set<FXOMObject> items = osg.getItems();
//            assert items.size() == 1; // Because of (1)
//            final FXOMObject selectedObject = items.iterator().next();
//            final FXOMObject nextSibling = selectedObject.getNextSlibing();
//            assert nextSibling != null; // Because of (1)
//            selection.select(nextSibling);
//        } else {
//            assert asg instanceof GridSelectionGroup; // Because of (1)
//            final GridSelectionGroup gsg = (GridSelectionGroup) asg;
//            final FXOMObject gridPane = gsg.getHitItem();
//            final GridPaneHierarchyMask mask = gridPaneHierarchyMaskFactory.getMask(gridPane);
//            assert gridPane instanceof FXOMInstance;
//            final Set<Integer> indexes = gsg.getIndexes();
//            assert indexes.size() == 1; // Because of (1)
//            int selectedIndex = indexes.iterator().next();
//            int nextIndex = selectedIndex + 1;
//            int size = 0;
//            switch (gsg.getType()) {
//            case ROW:
//                size = mask.getRowsSize();
//                break;
//            case COLUMN:
//                size = mask.getColumnsSize();
//                break;
//            default:
//                assert false;
//                break;
//            }
//            assert nextIndex < size; // Because of (1)
//            selection.select(gridSelectionGroupFactory.getGroup(gridPane, gsg.getType(), nextIndex));
//        }

        return ActionStatus.DONE;
    }
}