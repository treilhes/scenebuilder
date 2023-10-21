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

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell.BorderSide;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.api.HierarchyPanel;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController.DroppingMouseLocation;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.control.SkinBase;
import javafx.scene.control.TreeItem;
import javafx.scene.shape.Line;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class HierarchyInsertLine {

    public static final String CSS_CLASS_PARENT_INDICATOR_LINE = "cell-parent-indicator-line";

    // Vertical line used when inserting an item in order to indicate
    // the parent into which the item will be inserted.
    // Horizontal lines are handled directly by the cell and are built using CSS
    // only.
    //
    // This line will be added to / removed from the skin of the panel control
    // during DND gestures.
    private final Line insertLineIndicator = new Line();

    private final HierarchyPanel panelController;

    private final HierarchyCellAssignment cellAssignment;

    public HierarchyInsertLine(HierarchyPanel panelController, HierarchyCellAssignment cellAssignment) {
        super();
        this.panelController = panelController;
        this.cellAssignment = cellAssignment;

        // Update vertical insert line indicator stroke width
        insertLineIndicator.setStrokeWidth(2.0);
    }

    public void clearLine() {
        removeFromPanelControlSkin(insertLineIndicator);
    }

    public void showForItem(TreeItem<HierarchyItem> parentItem, TreeItem<HierarchyItem> hoveredItem,
            DroppingMouseLocation location) {

        TreeItem<HierarchyItem> rootTreeItem = panelController.getTreeView().getRoot();
        Optional<HierarchyCell> optionalHoveredCell = cellAssignment.getCell(hoveredItem);

        assert optionalHoveredCell.isPresent();

        HierarchyCell hoveredCell = optionalHoveredCell.get();

        TreeItem<HierarchyItem> startTreeItem;
        Optional<HierarchyCell> startCell, stopCell;

        // TreeItem is null when dropping below the datas
        // => the drop target is the root
        if (hoveredItem == null) {
            if (rootTreeItem.isLeaf() || !rootTreeItem.isExpanded()) {
                cellAssignment.getCell(rootTreeItem).ifPresent(c -> c.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT));
            } else {
                final TreeItem<HierarchyItem> lastTreeItem = panelController.getLastVisibleTreeItem();
                final Optional<HierarchyCell> lastCell = cellAssignment.getCell(lastTreeItem);
                // As we are dropping below the datas, the last cell is visible
                assert lastCell.isPresent();
                lastCell.ifPresent(c -> c.setBorder(BorderSide.BOTTOM));

                // Update vertical insert line
                startTreeItem = rootTreeItem;
                startCell = cellAssignment.getCell(startTreeItem);
                stopCell = lastCell;

                updateInsertLineIndicator(hoveredCell, startCell, stopCell);
                addToPanelControlSkin(insertLineIndicator);
            }

        } else {
            final HierarchyItem item = hoveredItem.getValue();
            assert item != null;

            if (item.isPlaceHolder() || hoveredItem == parentItem) {
                // The place holder item is filled with a container
                // accepting sub components
                hoveredCell.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT);
            } else {
                // REORDERING :
                // To avoid visual movement of the horizontal border when
                // dragging from one cell to another,
                // we always set the border on the cell bottom location :
                // - if we handle REORDER BELOW gesture, just set the bottom
                // border on the current cell
                // - if we handle REORDER ABOVE gesture, we set the bottom
                // border on the previous cell
                //
                switch (location) {

                // REORDER ABOVE gesture
                case TOP:
                    if (hoveredItem == rootTreeItem) {
                        hoveredCell.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT);
                    } else {
                        // Retrieve the previous cell
                        // Note : we set the border on the bottom of the previous cell
                        // instead of using the top of the current cell in order to avoid
                        // visual gap when DND from one cell to another

                        final TreeItem<HierarchyItem> previousItem = hoveredItem.previousSibling() != null
                                ? hoveredItem.previousSibling()
                                : hoveredItem.getParent();

                        // The previous cell is null when the item is not visible
                        Optional<HierarchyCell> previousCell = cellAssignment.getCell(previousItem);

                        // The previous cell is null when the item is not visible
                        previousCell.ifPresent(c -> c.setBorder(BorderSide.BOTTOM));

                        // Update vertical insert line
                        startTreeItem = parentItem;
                        startCell = cellAssignment.getCell(startTreeItem);
                        stopCell = previousCell;
                        updateInsertLineIndicator(hoveredCell, startCell, stopCell);
                        addToPanelControlSkin(insertLineIndicator);
                    }
                    break;

                // REPARENT gesture
                case CENTER:
                    if (hoveredItem.isLeaf() || !hoveredItem.isExpanded()) {
                        hoveredCell.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT);
                    } else {
                        // Reparent to the treeItem as last child
                        final TreeItem<HierarchyItem> lastTreeItem = panelController.getLastVisibleTreeItem(hoveredItem);
                        Optional<HierarchyCell> lastCell = cellAssignment.getCell(lastTreeItem);

                        // Last cell is null when the item is not visible
                        lastCell.ifPresent(c -> c.setBorder(BorderSide.BOTTOM));

                        // Update vertical insert line
                        startTreeItem = hoveredItem;
                        startCell = cellAssignment.getCell(startTreeItem);
                        stopCell = lastCell;
                        updateInsertLineIndicator(hoveredCell, startCell, stopCell);
                        addToPanelControlSkin(insertLineIndicator);
                    }
                    break;

                // REORDER BELOW gesture
                case BOTTOM:
                    if (hoveredItem == rootTreeItem && (hoveredItem.isLeaf() || !hoveredItem.isExpanded())) {
                        hoveredCell.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT);
                    } else {
                        // Reparent to the treeItem as first child
                        hoveredCell.setBorder(BorderSide.BOTTOM);

                        // Update vertical insert line
                        startTreeItem = parentItem;
                        startCell = cellAssignment.getCell(startTreeItem);
                        stopCell = optionalHoveredCell;
                        updateInsertLineIndicator(hoveredCell, startCell, stopCell);
                        addToPanelControlSkin(insertLineIndicator);
                    }
                    break;

                default:
                    assert false;
                    break;
                }
            }
        }

    }

    private void updateInsertLineIndicator(final HierarchyCell overCell, final Optional<HierarchyCell> startCell,
            final Optional<HierarchyCell> stopCell) {

        final HierarchyCell startTreeCell = startCell.orElse(null);
        final HierarchyCell stopTreeCell = stopCell.orElse(null);

        // ----------------------------------------------------------------------
        // START POINT CALCULATION
        // ----------------------------------------------------------------------
        // Retrieve the disclosure node from which the vertical line will start
        double startX, startY;

        if (startTreeCell != null) {
            final Node disclosureNode = startTreeCell.getDisclosureNode();
            final Bounds startBounds = startTreeCell.getLayoutBounds();
            final Point2D startCellPoint = startTreeCell.localToParent(startBounds.getMinX(), startBounds.getMinY());

            final Bounds disclosureNodeBounds = disclosureNode.getLayoutBounds();
            final Point2D disclosureNodePoint = disclosureNode.localToParent(disclosureNodeBounds.getMinX(),
                    disclosureNodeBounds.getMinY());

            // Initialize start point to the disclosure node of the start cell
            startX = startCellPoint.getX() + disclosureNodePoint.getX() + disclosureNodeBounds.getWidth() / 2 + 1; // +1
                                                                                                                   // px
                                                                                                                   // tuning
            startY = startCellPoint.getY() + disclosureNodePoint.getY() + disclosureNodeBounds.getHeight() - 6; // -6 px
                                                                                                                // tuning
        } else {
            // The start cell is not visible :
            // x is set to the current cell graphic
            // y is set to the top of the TreeView / TreeTableView
            final Bounds graphicBounds = overCell.getGraphic().getLayoutBounds();
            final Point2D graphicPoint = overCell.getGraphic().localToParent(graphicBounds.getMinX(),
                    graphicBounds.getMinY());

            startX = graphicPoint.getX();
            startY = panelController.getContentTopY();
        }

        // ----------------------------------------------------------------------
        // END POINT CALCULATION
        // ----------------------------------------------------------------------
        double endX, endY;
        endX = startX;
        if (stopTreeCell != null) {
            final Bounds stopBounds = stopTreeCell.getLayoutBounds();
            final Point2D stopCellPoint = stopTreeCell.localToParent(stopBounds.getMinX(), stopBounds.getMinY());

            // Initialize end point to the end cell
            endY = stopCellPoint.getY() + stopBounds.getHeight() // Add the stop cell height
                    - 1; // -1 px tuning
        } else {
            // The stop cell is not visisble :
            // y is set to the bottom of the TreeView / TreeTableView
            endY = panelController.getContentBottomY();
        }

        insertLineIndicator.setStartX(startX);
        insertLineIndicator.setStartY(startY);
        insertLineIndicator.setEndX(endX);
        insertLineIndicator.setEndY(endY);
    }

    /**
     * @param node the node
     * @treatAsPrivate
     */
    private void addToPanelControlSkin(final Node node) {
        final Skin<?> skin = panelController.getTreeView().getSkin();
        assert skin instanceof SkinBase;
        final SkinBase<?> skinbase = (SkinBase<?>) skin;
        node.getStyleClass().add(CSS_CLASS_PARENT_INDICATOR_LINE);
        skinbase.getChildren().add(node);
    }

    /**
     * @param node the node
     * @treatAsPrivate
     */
    private void removeFromPanelControlSkin(final Node node) {
        final Skin<?> skin = panelController.getTreeView().getSkin();
        assert skin instanceof SkinBase;
        final SkinBase<?> skinbase = (SkinBase<?>) skin;
        node.getStyleClass().remove(CSS_CLASS_PARENT_INDICATOR_LINE);
        skinbase.getChildren().remove(node);
    }

}
