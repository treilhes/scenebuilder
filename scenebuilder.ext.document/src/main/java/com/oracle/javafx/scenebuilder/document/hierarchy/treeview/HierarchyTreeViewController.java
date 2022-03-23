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
package com.oracle.javafx.scenebuilder.document.hierarchy.treeview;

import static javafx.geometry.Orientation.HORIZONTAL;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.ContextMenu;
import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.JobManager;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.AbstractHierarchyPanelController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.preferences.document.ShowExpertByDefaultPreference;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Hierarchy panel controller based on the TreeView control.
 */
public class HierarchyTreeViewController extends AbstractHierarchyPanelController {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyTreeViewController.class);

    @FXML
    protected TreeView<HierarchyItem> treeView;

	private final HierarchyTreeCell.Factory hierarchyTreeCellFactory;


    public HierarchyTreeViewController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            InlineEdit inlineEdit,
            ContextMenu contextMenu,
            JobManager jobManager,
            Drag drag,
            Selection selection,
            ShowExpertByDefaultPreference showExpertByDefaultPreference,
            DocumentDragSource.Factory documentDragSourceFactory,
            ExternalDragSource.Factory externalDragSourceFactory,
            DesignHierarchyMask.Factory designHierarchyMaskFactory,
            HierarchyTreeCell.Factory hierarchyTreeCellFactory,
            HierarchyDNDController.Factory hierarchyDNDControllerFactory,
            MetadataInfoDisplayOption defaultDisplayOptions) {
        super(scenebuilderManager, documentManager, HierarchyTreeViewController.class.getResource("HierarchyTreeView.fxml"),
                inlineEdit, contextMenu,
                jobManager, drag, selection, showExpertByDefaultPreference, documentDragSourceFactory,
                externalDragSourceFactory, designHierarchyMaskFactory, hierarchyDNDControllerFactory, defaultDisplayOptions);
        this.hierarchyTreeCellFactory = hierarchyTreeCellFactory;
    }

    @FXML
    public void initialize() {
//    	setParentRingColor(parentRingColorPreference.getValue());
//
//    	parentRingColorPreference.getObservableValue().addListener((ob,o,n) -> setParentRingColor(n));
    }

    @Override
    public Control getPanelControl() {
        return treeView;
    }

    @Override
    public ObservableList<TreeItem<HierarchyItem>> getSelectedItems() {
        return treeView.getSelectionModel().getSelectedItems();
    }

    @Override
    protected void initializePanel() {
        assert treeView != null;
        super.initializePanel();
        // Initialize and configure tree view
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Cell factory
        treeView.setCellFactory(p -> hierarchyTreeCellFactory.newCell(HierarchyTreeViewController.this));
        // We do not use the platform editing feature because
        // editing is started on selection + simple click instead of double click
        treeView.setEditable(false);

    }

    @Override
    protected void updatePanel() {
        if (treeView != null) {
            // First update rootTreeItem + children TreeItems
            updateTreeItems();
            // Then update the TreeTableView with the updated rootTreeItem
            stopListeningToTreeItemSelection();
            treeView.setRoot(rootTreeItem);
            startListeningToTreeItemSelection();
        }
    }

    @Override
    protected void clearSelection() {
        assert treeView != null;
        treeView.getSelectionModel().clearSelection();
    }

    @Override
    protected void select(final TreeItem<HierarchyItem> treeItem) {
        assert treeView != null;
        // The select method of TreeView selection model will expand the selected TreeItem.
        // Keep the current expanded value to set it back after selection.
        boolean isExpanded = treeItem.isExpanded();
        treeView.getSelectionModel().select(treeItem);
        treeItem.setExpanded(isExpanded);
    }

    @Override
    public void scrollTo(final TreeItem<HierarchyItem> treeItem) {
        assert treeView != null;
        treeView.scrollTo(treeView.getRow(treeItem));
    }

    @Override
    public Cell<?> getCell(final TreeItem<?> treeItem) {
        assert treeView != null;
        final TreeCell<?> treeCell
                = HierarchyTreeViewUtils.getTreeCell(treeView, treeItem);
        return treeCell;
    }

    /**
     * Returns the Y coordinate of the panel content TOP. Used to define the
     * zone for auto scrolling.
     *
     * @return the Y coordinate of the panel content TOP
     */
    @Override
    public double getContentTopY() {
        final Bounds bounds = treeView.getLayoutBounds();
        final Point2D point = treeView.localToParent(bounds.getMinX(), bounds.getMinY());
        return point.getY();
    }

    /**
     * Returns the Y coordinate of the panel content BOTTOM. Used to define the
     * zone for auto scrolling.
     *
     * @return the Y coordinate of the panel content BOTTOM
     */
    @Override
    public double getContentBottomY() {
        final Bounds bounds = treeView.getLayoutBounds();
        final Point2D point = treeView.localToParent(bounds.getMinX(), bounds.getMinY());
        final double topY = point.getY();
        final double height = bounds.getHeight();
        final ScrollBar horizontalScrollBar = getScrollBar(HORIZONTAL);
        final double bottomY;
        if (horizontalScrollBar != null && horizontalScrollBar.isVisible()) {
            bottomY = topY + height - horizontalScrollBar.getLayoutBounds().getHeight();
        } else {
            bottomY = topY + height;
        }
        return bottomY;
    }

    @Override
    protected void startListeningToTreeItemSelection() {
        treeView.getSelectionModel().getSelectedItems().addListener(treeItemSelectionListener);
    }

    @Override
    protected void stopListeningToTreeItemSelection() {
        treeView.getSelectionModel().getSelectedItems().removeListener(treeItemSelectionListener);
    }

    @Override
    protected void startEditingDisplayInfo() {
        // Start inline editing the display info on ENTER key
        final List<TreeItem<HierarchyItem>> selectedTreeItems
                = treeView.getSelectionModel().getSelectedItems();
        if (selectedTreeItems.size() == 1) {
            final TreeItem<HierarchyItem> selectedTreeItem = selectedTreeItems.get(0);
            final HierarchyItem item = selectedTreeItem.getValue();
            final DisplayOption option = getDisplayOption();
            if (item != null && !option.isReadOnly(item.getMask())) {
                final TreeCell<?> tc = HierarchyTreeViewUtils.getTreeCell(treeView, selectedTreeItem);
                assert tc instanceof HierarchyTreeCell;
                final HierarchyTreeCell<?> htc = (HierarchyTreeCell<?>) tc;
                logger.debug("Request edition of item {}/{}", item.getMask().getFxomObject(), item.getMask().hashCode());
                htc.startEditingDisplayInfo();
            }
        }
    }

    /**
     * *************************************************************************
     * Parent ring
     * *************************************************************************
     */
    @Override
    public void clearBorderColor() {
        assert treeView != null;
        final Set<Node> cells = HierarchyTreeViewUtils.getTreeCells(treeView);
        assert cells != null;
        for (Node node : cells) {
            assert node instanceof Cell;
            clearBorderColor((Cell<?>) node);
        }
    }

    @Override
    public void updateParentRing() {
        assert treeView != null;

        // Do not update parent ring while performing some operations
        // like DND within the hierarchy panel
        if (isParentRingEnabled() == false) {
            return;
        }

        final Set<Node> treeCells = HierarchyTreeViewUtils.getTreeCells(treeView);
        final List<TreeItem<HierarchyItem>> selectedTreeItems = treeView.getSelectionModel().getSelectedItems();

        // First clear previous parent ring if any
        clearBorderColor();

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
            final TreeItem<HierarchyItem> treeItemTop
                    = HierarchyTreeViewUtils.getCommonParentTreeItem(selectedTreeItems);
            final TreeCell<?> treeCellTop
                    = HierarchyTreeViewUtils.getTreeCell(treeCells, treeItemTop);
            if (treeCellTop != null) {
                setBorder(treeCellTop, BorderSide.TOP_RIGHT_LEFT);
                treeCellTopIndex = treeCellTop.getIndex();
            } else {
                treeCellTopIndex = 0;
            }

            // BOTTOM TreeItem is the last child of the common parent TreeItem
            final int size = treeItemTop.getChildren().size();
            assert size >= 1;
            final TreeItem<HierarchyItem> treeItemBottom = treeItemTop.getChildren().get(size - 1);
            final TreeCell<?> treeCellBottom = HierarchyTreeViewUtils.getTreeCell(treeCells, treeItemBottom);
            if (treeCellBottom != null) {
                setBorder(treeCellBottom, BorderSide.RIGHT_BOTTOM_LEFT);
                treeCellBottomIndex = treeCellBottom.getIndex();
            } else {
                treeCellBottomIndex = treeCells.size() - 1;
            }

            // MIDDLE TreeItems
            for (Node node : treeCells) {
                assert node instanceof TreeCell;
                final TreeCell<?> treeCell = (TreeCell<?>) node;
                final int index = treeCell.getIndex();
                if (index > treeCellTopIndex && index < treeCellBottomIndex) {
                    setBorder(treeCell, BorderSide.RIGHT_LEFT);
                }
            }
        }
    }

    @Override
    public void updatePlaceHolder() {
        assert treeView != null;
        final Set<Node> cells = HierarchyTreeViewUtils.getTreeCells(treeView);
        assert cells != null;
        for (Node node : cells) {
            assert node instanceof HierarchyTreeCell;
            final HierarchyTreeCell<?> cell = (HierarchyTreeCell<?>) node;
            cell.updatePlaceHolder();
        }
    }

}
