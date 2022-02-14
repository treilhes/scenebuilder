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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.menubar.PositionRequest;
import com.oracle.javafx.scenebuilder.api.menubar.annotation.MenuItemAttachment;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.BorderPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(
        nameKey = "action.name.show.about",
        descriptionKey = "action.description.show.about",
        accelerator = "CTRL+A")

@MenuItemAttachment(
        id = SelectAllAction.MENU_ID,
        targetMenuId = DeleteAction.MENU_ID,
        label = "menu.title.select.all",
        positionRequest = PositionRequest.AsNextSibling,
        separatorBefore = true)
public class SelectAllAction extends AbstractAction {

    public final static String MENU_ID = "selectAllMenu";

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final DesignHierarchyMask.Factory designMaskFactory;
    private final GridPaneHierarchyMask.Factory gridMaskFactory;
    private final BorderPaneHierarchyMask.Factory borderPaneMaskFactory;
    private final Selection selection;

    public SelectAllAction(
            ActionExtensionFactory extensionFactory,
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager,
            @Autowired InlineEdit inlineEdit,
            DesignHierarchyMask.Factory designMaskFactory,
            GridPaneHierarchyMask.Factory gridMaskFactory,
            BorderPaneHierarchyMask.Factory borderPaneMaskFactory,
            Selection selection) {
        super(extensionFactory);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.designMaskFactory = designMaskFactory;
        this.gridMaskFactory = gridMaskFactory;
        this.borderPaneMaskFactory = borderPaneMaskFactory;
        this.selection = selection;
    }

    @Override
    public boolean canPerform() {
        final boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            final String text = tic.getText();
            final String selectedText = tic.getSelectedText();
            if (text == null || text.isEmpty()) {
                result = false;
            } else {
                // Check if the TextInputControl is not already ALL selected
                result = selectedText == null || selectedText.length() < tic.getText().length();
            }
        } else {
            FXOMDocument fd = documentManager.fxomDocument().get();

            if (fd == null || fd.getFxomRoot() == null) {
                return false;
            }
            if (selection.isEmpty()) { // (1)
                return true;
            } else {
                  final FXOMObject rootObject = fd.getFxomRoot();
                  // Cannot select all if root is selected
                  if (selection.isSelected(rootObject)) { // (1)
                      return false;
                  } else {
                      List<FXOMObject> siblings = selection.getGroup().getSiblings();
                      return !siblings.isEmpty() && selection.getGroup().getItems().stream().anyMatch(i -> selection.isSelected(i));
                  }
            }
//            } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
//                final FXOMObject rootObject = fd.getFxomRoot();
//                // Cannot select all if root is selected
//                if (selection.isSelected(rootObject)) { // (1)
//                    return false;
//                } else {
//                    // Cannot select all if all sub components are already selected
//                    final FXOMObject ancestor = selection.getAncestor();
//                    assert ancestor != null; // Because of (1)
//                    final BorderPaneHierarchyMask mask = borderPaneMaskFactory.getMask(ancestor);
//                    // BorderPane special case : use accessories
//                    // TODO find a way to remove this special case
//                    if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
//                        final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
//                        final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
//                        final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
//                        final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
//                        final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
//                        for (FXOMObject bpAccessoryObject : new FXOMObject[] {
//                            top, left, center, right, bottom}) {
//                            if (bpAccessoryObject != null
//                                    && selection.isSelected(bpAccessoryObject) == false) {
//                                return true;
//                            }
//                        }
//                    } else if (mask.isAcceptingSubComponent()) {
//                        for (FXOMObject subComponentObject : mask.getSubComponents()) {
//                            if (selection.isSelected(subComponentObject) == false) {
//                                return true;
//                            }
//                        }
//                    }
//                }
//            } else if (selection.getGroup() != null) {
//                // GridSelectionGroup => at least 1 row/column is selected
//                // SelectionGroup => at least 1 item is selected
//                return selection.getGroup().getItems().stream().anyMatch(i -> selection.isSelected(i));
//            } else {
//                assert selection.getGroup() == null :
//                        "Add implementation for " + selection.getGroup(); //NOCHECK
//            }
//            return false;

        }
        return result;
    }

    /**
     * Performs the select all control action.
     * Select all sub components of the selection common ancestor.
     */
    @Override
    public ActionStatus doPerform() {
        assert canPerform();
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.selectAll();
        } else {
            selection.selectAll();
//
//            FXOMDocument fd = documentManager.fxomDocument().get();
//            Selection selection = documentManager.selectionDidChange().get().getSelection();
//
//            final FXOMObject rootObject = fd.getFxomRoot();
//            if (selection.isEmpty()) { // (1)
//                // If the current selection is empty, we select the root object
//                selection.select(rootObject);
//            } else {
//                List<FXOMObject> siblings = selection.getGroup().getSiblings();
//                if (!siblings.isEmpty()) {
//                    selection.select(siblings);
//                }
//            }
//            } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
//                // Otherwise, select all sub components of the common ancestor ??
//                final FXOMObject ancestor = selection.getAncestor();
//                assert ancestor != null; // Because of (1)
//                final BorderPaneHierarchyMask mask = borderPaneMaskFactory.getMask(ancestor);
//                final Set<FXOMObject> selectableObjects = new HashSet<>();
//                // BorderPane special case : use accessories
//                // TODO find a way to remove this special case
//                if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
//                    final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
//                    final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
//                    final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
//                    final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
//                    final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
//                    for (FXOMObject accessoryObject : new FXOMObject[]{
//                        top, left, center, right, bottom}) {
//                        if (accessoryObject != null) {
//                            selectableObjects.add(accessoryObject);
//                        }
//                    }
//                } else {
//                    assert mask.isAcceptingSubComponent(); // Because of (1)
//                    selectableObjects.addAll(mask.getSubComponents());
//                }
//                selection.select(selectableObjects);
//            } else if (selection.getGroup() instanceof GridSelectionGroup) {
//                // Select ALL rows / columns
//                final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
//                final FXOMObject gridPane = gsg.getHitItem();
//                assert gridPane instanceof FXOMInstance;
//                final GridPaneHierarchyMask gridPaneMask = gridMaskFactory.getMask(gridPane);
//                int size = 0;
//                switch (gsg.getType()) {
//                    case ROW:
//                        size = gridPaneMask.getRowsSize();
//                        break;
//                    case COLUMN:
//                        size = gridPaneMask.getColumnsSize();
//                        break;
//                    default:
//                        assert false;
//                        break;
//                }
//                // Select first index
//                selection.select((FXOMInstance) gridPane, gsg.getType(), 0);
//                for (int index = 1; index < size; index++) {
//                    selection.toggleSelection((FXOMInstance) gridPane, gsg.getType(), index);
//                }
//            } else {
//                assert selection.getGroup() == null :
//                        "Add implementation for " + selection.getGroup(); //NOCHECK
//
//            }

        }

        return ActionStatus.DONE;
    }
}