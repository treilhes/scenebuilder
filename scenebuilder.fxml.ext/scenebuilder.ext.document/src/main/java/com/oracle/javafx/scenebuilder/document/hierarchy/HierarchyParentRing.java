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
package com.oracle.javafx.scenebuilder.document.hierarchy;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell.BorderSide;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.api.HierarchyPanel;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class HierarchyParentRing {

    private final HierarchyPanel panelController;

    private final HierarchyCellAssignment cellAssignments;

    private boolean parentRingEnabled;

    public HierarchyParentRing(
            HierarchyPanel panelController,
            HierarchyCellAssignment cellAssignments) {
        super();
        this.panelController = panelController;
        this.cellAssignments = cellAssignments;
    }

    /**
     * true if the parent ring is enabled
     *
     * @return true, if is enabled
     */
    public boolean isEnabled() {
        return parentRingEnabled;
    }

    /**
     * Disable the parent ring
     */
    public void disable() {
        clear();
        parentRingEnabled = false;
    }

    /**
     * Enable the parent ring
     */
    public void enable() {
        clear();
        parentRingEnabled = true;
    }


    /**
     * Clear the current parent ring
     */
    public void clear() {
        assert cellAssignments != null;
        cellAssignments.getCells().forEach(HierarchyCell::clearBorders);
    }

    /**
     * Update the parent ring.
     */
    public void update() {

        TreeView<HierarchyItem> treeView = panelController.getTreeView();
        assert treeView != null;

        // Do not update parent ring while performing some operations
        // like DND within the hierarchy panel
        if (!isEnabled()) {
            return;
        }

        final Set<HierarchyCell> treeCells = cellAssignments.getCells();
        final List<TreeItem<HierarchyItem>> selectedTreeItems = treeView.getSelectionModel().getSelectedItems();

        // First clear previous parent ring if any
        clear();

        // Dirty selection
        for (TreeItem<HierarchyItem> selectedTreeItem : selectedTreeItems) {
            if (selectedTreeItem == null) {
                return;
            }
        }

        // Then update parent ring if selection is not empty
        if (!selectedTreeItems.isEmpty()) {

            // Single selection is ROOT TreeItem => no parent ring
            final TreeItem<HierarchyItem> treeItemRoot = treeView.getRoot();
            if (selectedTreeItems.size() == 1 && selectedTreeItems.get(0) == treeItemRoot) {
                return;
            }

            int treeCellTopIndex, treeCellBottomIndex;

            // TOP TreeItem is the common parent TreeItem
            final TreeItem<HierarchyItem> treeItemTop = panelController.getCommonParentTreeItem(selectedTreeItems);
            final Optional<HierarchyCell> treeCellTop = cellAssignments.getCell(treeItemTop);

            treeCellTop.ifPresent(c -> c.setBorder(BorderSide.TOP_RIGHT_LEFT));

            treeCellTopIndex = treeCellTop.map(HierarchyCell::getIndex).orElse(0);

            // BOTTOM TreeItem is the last child of the common parent TreeItem
            final int size = treeItemTop.getChildren().size();
            assert size >= 1;
            final TreeItem<HierarchyItem> treeItemBottom = treeItemTop.getChildren().get(size - 1);
            final Optional<HierarchyCell> treeCellBottom = cellAssignments.getCell(treeItemBottom);

            treeCellBottom.ifPresent(c -> c.setBorder(BorderSide.RIGHT_BOTTOM_LEFT));

            treeCellBottomIndex = treeCellBottom.map(HierarchyCell::getIndex).orElse(treeCells.size() - 1);

            // MIDDLE TreeItems
            for (HierarchyCell cell : treeCells) {
                final int index = cell.getIndex();
                if (index > treeCellTopIndex && index < treeCellBottomIndex) {
                    cell.setBorder(BorderSide.RIGHT_LEFT);
                }
            }
        }
    }

}
