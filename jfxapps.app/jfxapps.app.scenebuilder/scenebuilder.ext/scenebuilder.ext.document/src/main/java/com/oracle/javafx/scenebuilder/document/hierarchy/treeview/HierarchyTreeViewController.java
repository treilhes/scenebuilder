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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.mask.HierarchyMask.Accessory;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.api.HierarchyPanel;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.item.HierarchyItemAccessory;

import javafx.collections.ObservableList;
import javafx.event.EventTarget;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Cell;
import javafx.scene.control.Control;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * Hierarchy panel controller based on the TreeView control.
 */
@Component
@Scope(value = SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class HierarchyTreeViewController extends AbstractFxmlController implements HierarchyPanel {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyTreeViewController.class);

    @FXML
    protected TreeView<HierarchyItem> treeView;

	private final HierarchyTreeCell.Factory hierarchyTreeCellFactory;
	private final HierarchyCellAssignment cellAssignments;

    public HierarchyTreeViewController(
            SceneBuilderManager scenebuilderManager,
            FxmlDocumentManager documentManager,
            HierarchyCellAssignment cellAssignments,
            HierarchyTreeCell.Factory hierarchyTreeCellFactory,
            MetadataInfoDisplayOption defaultDisplayOptions) {
        super(scenebuilderManager, documentManager, HierarchyTreeViewController.class.getResource("HierarchyTreeView.fxml"), I18N.getBundle());

        this.hierarchyTreeCellFactory = hierarchyTreeCellFactory;
        this.cellAssignments = cellAssignments;

    }

    protected void initializePanel() {
        assert treeView != null;

        // Panel may be either a TreeView or a TreeTableView
        assert getTreeView() != null;

        // Set default parent ring color
        //setParentRingColor(DEFAULT_PARENT_RING_COLOR);

        // Initialize and configure tree view
        treeView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        // Cell factory
        treeView.setCellFactory(p -> hierarchyTreeCellFactory.newCell(HierarchyTreeViewController.this));
        // We do not use the platform editing feature because
        // editing is started on selection + simple click instead of double click
        treeView.setEditable(false);

    }

    @Override
    public TreeView<HierarchyItem> getTreeView() {
        return treeView;
    }

    /**
     * Returns the panel control scrollbar for the specified orientation.
     *
     * @param orientation the scrollbar orientation
     * @return the panel control scrollbar for the specified orientation
     * @treatAsPrivate
     */
    public ScrollBar getScrollBar(final Orientation orientation) {
        final Control panelControl = getTreeView();
        final Set<Node> scrollBars = panelControl.lookupAll(".scroll-bar"); //NOCHECK
        for (Node node : scrollBars) {
            if (node instanceof ScrollBar) {
                final ScrollBar scrollBar = (ScrollBar) node;
                if (scrollBar.getOrientation() == orientation) {
                    return scrollBar;
                }
            }
        }
        return null;
    }

    public void setRooItem(TreeItem<HierarchyItem> newRoot) {

        if (treeView != null) {
            cellAssignments.clear();
            treeView.setRoot(newRoot);
        }
    }

    public ObservableList<TreeItem<HierarchyItem>> getSelectedItems() {
        return treeView.getSelectionModel().getSelectedItems();
    }

    public void clearSelection() {
        assert treeView != null;
        treeView.getSelectionModel().clearSelection();
    }

    /**
     * @param treeItems the TreeItems
     * @treatAsPrivate
     */
    public void select(final List<TreeItem<HierarchyItem>> treeItems) {
        for (TreeItem<HierarchyItem> treeItem : treeItems) {
            select(treeItem);
        }
    }

    public void select(final TreeItem<HierarchyItem> treeItem) {
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

    /**
     * @param treeItem the TreeItem
     * @return true if visible
     * @treatAsPrivate
     */
    public boolean isVisible(final TreeItem<HierarchyItem> treeItem) {
        return cellAssignments.getCell(treeItem).map(HierarchyCell::isVisible).orElse(false);
    }

    /**
     * Returns the Y coordinate of the panel content TOP. Used to define the
     * zone for auto scrolling.
     *
     * @return the Y coordinate of the panel content TOP
     * @treatAsPrivate
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
     * @treatAsPrivate
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


    /**
     * @treatAsPrivate
     */
    @Override
    public void controllerDidLoadFxml() {
        assert getTreeView() != null;

        // Initialize and configure the hierarchy panel
        initializePanel();

    }

    public List<TreeItem<HierarchyItem>> lookupTreeItem(List<FXOMObject> fxomObjects) {
        final List<TreeItem<HierarchyItem>> result = new ArrayList<>();
        for (FXOMObject fxomObject : fxomObjects) {
            final TreeItem<HierarchyItem> treeItem = lookupTreeItem(fxomObject);
            // TreeItem may be null when selecting a GridPane column/row
            // constraint in content panel
            if (treeItem != null) {
                result.add(treeItem);
            }
        }
        return result;
    }

    /**
     * @param fxomObject the FXOMObject
     * @return the TreeItem corresponding to the specified FXOMObject
     * @treatAsPrivate
     */
    @Override
    public TreeItem<HierarchyItem> lookupTreeItem(FXOMObject fxomObject) {
        return lookupTreeItem(fxomObject, getRootItem());
    }

    private TreeItem<HierarchyItem> lookupTreeItem(FXOMObject fxomObject, TreeItem<HierarchyItem> fromTreeItem) {
        TreeItem<HierarchyItem> result;
        assert fxomObject != null;

        // ROOT TreeItem may be null when no document is loaded
        if (fromTreeItem != null) {
            assert fromTreeItem.getValue() != null;
            if (fromTreeItem.getValue().getFxomObject() == fxomObject) {
                result = fromTreeItem;
            } else {
                Iterator<TreeItem<HierarchyItem>> it = fromTreeItem.getChildren().iterator();
                result = null;
                while ((result == null) && it.hasNext()) {
                    TreeItem<HierarchyItem> childItem = it.next();
                    result = lookupTreeItem(fxomObject, childItem);
                }
            }
        } else {
            result = null;
        }
        return result;
    }

    /**
     * Returns the list of all descendant from the specified parent TreeItem.
     * The specified parent TreeItem is excluded from the returned list.
     *
     * @param <T> type
     * @param parentTreeItem the parent TreeItem
     * @return the list of all descendant
     */
    private <T> List<TreeItem<T>> getAllTreeItems(final TreeItem<T> parentTreeItem) {
        assert parentTreeItem != null;
        final List<TreeItem<T>> treeItems = new ArrayList<>();
        for (TreeItem<T> child : parentTreeItem.getChildren()) {
            treeItems.add(child);
            treeItems.addAll(getAllTreeItems(child));
        }
        return treeItems;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeItem<HierarchyItem> getLastVisibleTreeItem() {
        return getLastVisibleTreeItem(getRootItem());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeItem<HierarchyItem> getLastVisibleTreeItem(final TreeItem<HierarchyItem> parentTreeItem) {
        assert parentTreeItem != null;
        TreeItem<HierarchyItem> result = parentTreeItem;
        int size = result.getChildren().size();
        while (size != 0) {
            if (result.isExpanded()) {
                result = result.getChildren().get(size - 1);
                size = result.getChildren().size();
            } else {
                size = 0;
            }
        }
        return result;
    }

    /**
     * Returns the next visible TreeItem of the specified TreeItem.
     *
     * @param <T> type
     * @param treeItem the TreeItem
     * @return the next visible TreeItem
     * @treatAsPrivate
     */
    public <T> TreeItem<T> getNextVisibleTreeItem(final TreeItem<T> treeItem) {
        assert treeItem != null;
        if (treeItem == getRootItem()) {
            // Root TreeItem has no next TreeItem
            return null;
        } else if (treeItem.isExpanded() && !treeItem.getChildren().isEmpty()) {
            // Return first child
            return treeItem.getChildren().get(0);
        } else {
            TreeItem<T> parentTreeItem = treeItem.getParent();
            TreeItem<T> result = treeItem.nextSibling();
            while (result == null && parentTreeItem != getRootItem()) {
                result = parentTreeItem.nextSibling();
                parentTreeItem = parentTreeItem.getParent();
            }
            return result;
        }
    }

    /**
     * Returns the previous visible TreeItem of the specified TreeItem.
     *
     * @param <T> type
     * @param treeItem the TreeItem
     * @return the previous visible TreeItem
     * @treatAsPrivate
     */
    public <T> TreeItem<T> getPreviousVisibleTreeItem(final TreeItem<T> treeItem) {
        assert treeItem != null;
        if (treeItem == getRootItem()) {
            // Root TreeItem has no previous TreeItem
            return null;
        } else {
            TreeItem<T> parentTreeItem = treeItem.getParent();
            TreeItem<T> result = treeItem.previousSibling();
            while (result == null && parentTreeItem != getRootItem()) {
                result = parentTreeItem.previousSibling();
                parentTreeItem = parentTreeItem.getParent();
            }
            return result;
        }
    }

    public <T> void expandAllTreeItems(final TreeItem<T> parentTreeItem) {
        assert parentTreeItem != null;
        parentTreeItem.setExpanded(true);
        final List<TreeItem<T>> treeItems = getAllTreeItems(parentTreeItem);
        assert treeItems != null;
        for (TreeItem<T> treeItem : treeItems) {
            treeItem.setExpanded(true);
        }
    }

    public <T> void collapseAllTreeItems(final TreeItem<T> parentTreeItem) {
        assert parentTreeItem != null;
        parentTreeItem.setExpanded(false);
        final List<TreeItem<T>> treeItems = getAllTreeItems(parentTreeItem);
        assert treeItems != null;
        for (TreeItem<T> treeItem : treeItems) {
            treeItem.setExpanded(false);
        }
    }

    /**
     * Returns the cell ancestor of the specified event target. Indeed,
     * depending on the mouse click position, the event target may be the cell
     * node itself, the cell graphic or the cell labeled text.
     *
     * @param target
     * @return
     */
    public Cell<?> lookupCell(EventTarget target) {
        assert target instanceof Node;
        Node node = (Node) target;
        while ((node instanceof Cell) == false) {
            node = node.getParent();
        }
        return (Cell<?>) node;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeItem<HierarchyItem> getCommonParentTreeItem(final List<TreeItem<HierarchyItem>> treeItems) {

        assert treeItems != null && !treeItems.isEmpty();

        // TreeItems contains ROOT
        // => return ROOT as the common parent
        for (TreeItem<HierarchyItem> treeItem : treeItems) {
            if (treeView.getTreeItemLevel(treeItem) == 0) {
                return treeItem;
            }
        }

        // TreeItem single selection
        // => the common parent is the single TreeItem parent
        if (treeItems.size() == 1) {
            return treeItems.get(0).getParent();
        } //
        // TreeItem multi selection
        else {
            assert treeItems.size() >= 2;
            TreeItem<HierarchyItem> parent = null;
            TreeItem<HierarchyItem> child = treeItems.get(0);
            for (int index = 1; index < treeItems.size(); index++) {
                parent = getCommonParentTreeItem(treeView, child, treeItems.get(index));
                // We reached the ROOT level
                // => common parent is ROOT TreeItem
                if (treeView.getTreeItemLevel(parent) == 0) {
                    break;
                } else {
                    child = parent;
                }
            }
            return parent;
        }
    }

    private static <T> TreeItem<T> getCommonParentTreeItem(
            final TreeView<T> treeView,
            final TreeItem<T> child1,
            final TreeItem<T> child2) {

        assert child1 != null && child2 != null;

        int child1Level = treeView.getTreeItemLevel(child1);
        int child2Level = treeView.getTreeItemLevel(child2);
        // Neither child1 nor child2 is ROOT TreeItem
        assert child1Level > 0 && child2Level > 0;

        TreeItem<T> parent1 = child1.getParent();
        TreeItem<T> parent2 = child2.getParent();

        if (child1Level < child2Level) {
            while (child1Level < child2Level) {
                parent2 = parent2.getParent();
                child2Level--;
            }
            // We reached the common parent TreeItem
            if (parent1 == parent2) {
                return parent1;
            } else {
                // At this step, parent1 and parent2 have same node level
                // within the TreeView
                while (parent1 != parent2) {
                    parent1 = parent1.getParent();
                    parent2 = parent2.getParent();
                }
                return parent1;
            }
        } else {
            while (child1Level > child2Level) {
                parent1 = parent1.getParent();
                child1Level--;
            }
            // We reached the common parent TreeItem
            if (parent1 == parent2) {
                return parent1;
            } else {
                // At this step, parent1 and parent2 have same node level
                // within the TreeView
                while (parent1 != parent2) {
                    parent1 = parent1.getParent();
                    parent2 = parent2.getParent();
                }
                return parent1;
            }
        }
    }

    @Override
    public TreeItem<HierarchyItem> getRootItem() {
        return treeView.getRoot();
    }

    public Map<ExpandedKey, Boolean> makeExpandedMap() {
        final Map<ExpandedKey, Boolean> treeItemsExpandedMap = new HashMap<>();

        if (getRootItem() != null) {
            updateTreeItemsExpandedMap(getRootItem(), treeItemsExpandedMap);
        }

        return treeItemsExpandedMap;
    }

    private void updateTreeItemsExpandedMap(TreeItem<HierarchyItem> treeItem,
            Map<ExpandedKey, Boolean> treeItemsExpandedMap) {
        assert treeItem != null;
        final HierarchyItem item = treeItem.getValue();

        if (item.isPlaceHolder()) {
            HierarchyItemAccessory accessoryItem = (HierarchyItemAccessory) item;
            TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
            final FXOMObject fxomObject = parentTreeItem.getValue().getFxomObject();
            assert fxomObject != null;
            final Accessory accessory = accessoryItem.getAccessory();
            assert accessory != null;
            treeItemsExpandedMap.put(new ExpandedKey(fxomObject, accessory), treeItem.isExpanded());
        } else {
            if (!item.isEmpty()) {
                final FXOMObject fxomObject = item.getFxomObject();
                assert fxomObject != null;
                treeItemsExpandedMap.put(new ExpandedKey(fxomObject, null), treeItem.isExpanded());
            }
        }

        // Inspect TreeItem chidren
        for (TreeItem<HierarchyItem> treeItemChild : treeItem.getChildren()) {
            updateTreeItemsExpandedMap(treeItemChild, treeItemsExpandedMap);
        }
    }

}
