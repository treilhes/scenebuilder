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

import org.scenebuilder.fxml.api.HierarchyMask.Accessory;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.dnd.Drag;
import com.oracle.javafx.scenebuilder.api.dnd.DragSource;
import com.oracle.javafx.scenebuilder.api.dnd.DropTarget;
import com.oracle.javafx.scenebuilder.api.editor.selection.DefaultSelectionGroupFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit;
import com.oracle.javafx.scenebuilder.core.dnd.droptarget.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.core.dnd.droptarget.RootDropTarget;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.DocumentDragSource;
import com.oracle.javafx.scenebuilder.core.editor.drag.source.ExternalDragSource;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell.BorderSide;
import com.oracle.javafx.scenebuilder.document.api.HierarchyDND;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.item.HierarchyItemAccessory;
import com.oracle.javafx.scenebuilder.document.hierarchy.item.HierarchyItemBase;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeViewController;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Orientation;
import javafx.geometry.Point2D;
import javafx.scene.control.Cell;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

/**
 * Controller for all drag and drop gestures in hierarchy panel. This class does
 * not depend on the TreeView or TreeTableView control and handles only
 * TreeItems.
 *
 * @treatAsPrivate
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class HierarchyDNDController implements HierarchyDND {

    // When DND few pixels of the top or bottom of the Hierarchy
    // the user can cause it to auto-scroll until the desired target node
    private static final double AUTO_SCROLLING_ZONE_HEIGHT = 40.0;

    private final Drag drag;
    private final RootDropTarget.Factory rootDropTargetFactory;
    private final DesignHierarchyMask.Factory designHierarchyMaskFactory;
    private final AccessoryDropTarget.Factory accessoryDropTargetFactory;

    private final HierarchyTreeViewController hierarchyTreeView;
    private HierarchyTaskScheduler scheduler;
    private final FxmlDocumentManager documentManager;
    private final HierarchyAnimationScheduler animationScheduler;
    private final HierarchyInsertLine insertLine;
    private final HierarchyParentRing parentRing;
    private final HierarchyCellAssignment cellAssignments;
    private final Selection selection;
    private final InlineEdit inlineEdit;

    private final DocumentDragSource.Factory documentDragSourceFactory;
    private final ExternalDragSource.Factory externalDragSourceFactory;

    /**
     * Defines the mouse location within the cell when the dropping gesture
     * occurs.
     *
     * @treatAsPrivate
     */
    public enum DroppingMouseLocation {

        BOTTOM, CENTER, TOP
    }

    private boolean shouldEndOnExit;

    protected HierarchyDNDController(
            Drag drag,
            FxmlDocumentManager documentManager,
            Selection selection,
            InlineEdit inlineEdit,
            HierarchyTreeViewController hierarchyTreeView,
            HierarchyAnimationScheduler animationScheduler,
            HierarchyTaskScheduler taskScheduler,
            HierarchyInsertLine insertLine,
            HierarchyParentRing parentRing,
            HierarchyCellAssignment cellAssignments,
            RootDropTarget.Factory rootDropTargetFactory,
            DesignHierarchyMask.Factory designHierarchyMaskFactory,
            AccessoryDropTarget.Factory accessoryDropTargetFactory,
            DocumentDragSource.Factory documentDragSourceFactory,
            ExternalDragSource.Factory externalDragSourceFactory

            ) {
        this.drag = drag;
        this.documentManager = documentManager;
        this.animationScheduler = animationScheduler;
        this.hierarchyTreeView = hierarchyTreeView;
        this.scheduler = taskScheduler;
        this.insertLine = insertLine;
        this.selection = selection;
        this.inlineEdit = inlineEdit;
        this.parentRing = parentRing;
        this.cellAssignments = cellAssignments;
        this.rootDropTargetFactory = rootDropTargetFactory;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
        this.accessoryDropTargetFactory = accessoryDropTargetFactory;
        this.documentDragSourceFactory = documentDragSourceFactory;
        this.externalDragSourceFactory = externalDragSourceFactory;

    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     */
    public void handleCellOnDragDropped(
            final HierarchyCell treeCell,
            final DragEvent event) {
        // Cancel timer if any
        scheduler.cancelTimer();

        // CSS
        treeCell.clearBorders();

        // Remove insert line indicator
        insertLine.clearLine();
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     */
    public void handleCellOnDragEntered(
            final HierarchyCell treeCell,
            final DragEvent event) {

        TreeItem<HierarchyItem> treeItem = treeCell.getTreeItem();
        // Cancel timer if any
        scheduler.cancelTimer();

        if (treeItem == null) {
            return;
        }

        // Auto scrolling timeline has been started :
        // do not schedule any other task
        if (animationScheduler.isTimelineRunning()) {
            return;
        }

        // Schedule expanding job for collapsed TreeItems
        if (!treeItem.isExpanded() && !treeItem.isLeaf()) {
            scheduler.scheduleExpandTask(treeItem);
        }
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     * @param location the location
     */
    public void handleCellOnDragExited(
            final HierarchyCell treeCell,
            final DragEvent event) {

        final Bounds bounds = treeCell.getLayoutBounds();
        final Point2D point = treeCell.localToScene(bounds.getMinX(), bounds.getMinY(), true /* rootScene */);
        final DroppingMouseLocation location;
        if (event.getSceneY() <= point.getY()) {
            location = DroppingMouseLocation.TOP;
        } else {
            location = DroppingMouseLocation.BOTTOM;
        }

        // Cancel timer if any
        scheduler.cancelTimer();

        //TODO nothing to remove anymore
//        // Remove empty tree item graphic if previously added by the scheduler
//        // when we exit the empty graphic TreeItem by the bottom
//        if (treeItem != null) {
//            final HierarchyItem item = treeItem.getValue();
//            assert item != null;
//            if (item instanceof HierarchyItemGraphic
//                    && item.isEmpty()
//                    && location == DroppingMouseLocation.BOTTOM) {
//                final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
//                parentTreeItem.getChildren().remove(treeItem);
//            }
//        }

        // CSS
        treeCell.clearBorders();

        // Remove insert line indicator
        insertLine.clearLine();
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     * @param location the location
     */
    public void handleCellOnDragOver(
            final HierarchyCell treeCell,
            final DragEvent event) {

        final TreeItem<HierarchyItem> treeItem = treeCell.getTreeItem();
        final Drag dragController = drag;
        final DroppingMouseLocation location = treeCell.getDroppingMouseLocation(event);

      //nothing to remove anymore
//      // Remove empty tree item graphic if previously added by the scheduler
//      // when we hover the empty graphic owner TreeItem on the top area
//      if (treeItem != null) {
//          final HierarchyItem item = treeItem.getValue();
//          assert item != null;
//          final TreeItem<HierarchyItem> graphicTreeItem = getEmptyGraphicTreeItemFor(treeItem);
//          if (graphicTreeItem != null && location == DroppingMouseLocation.TOP) {
//              treeItem.getChildren().remove(graphicTreeItem);
//          }
//      }

      // First update drop target
      final DropTarget dropTarget = makeDropTarget(treeItem, location);
      drag.setDropTarget(dropTarget);
System.out.println();
      // Then update transfer mode
      event.acceptTransferModes(drag.getAcceptedTransferModes());

      //TODO nothing to add anymore
//      // Schedule adding empty graphic place holder job :
//      // The drop target must be a GRAPHIC AccessoryDropTarget
//      if (dragController.isDropAccepted()
//              && dropTarget instanceof AccessoryDropTarget
//              && ((AccessoryDropTarget) dropTarget).getAccessory() == Accessory.GRAPHIC) {
//          // Retrieve the GRAPHIC accessory owner
//          final TreeItem<HierarchyItem> graphicOwnerTreeItem;
//          if (treeItem != null) {
//              if (treeItem.getValue().isEmpty() == false) {
//                  graphicOwnerTreeItem = treeItem;
//              } else {
//                  // Empty graphic place holder
//                  // => the graphic owner is the parent
//                  graphicOwnerTreeItem = treeItem.getParent();
//              }
//          } else {
//              // TreeItem is null when dropping below the datas
//              // => the graphic owner is the root
//              graphicOwnerTreeItem = panelController.getRootItem();
//          }
//          assert graphicOwnerTreeItem != null;
//          assert graphicOwnerTreeItem.getValue().isEmpty() == false;
//          // Schedule adding empty graphic place holder if :
//          // - an empty graphic place holder has not yet been added
//          // - an empty graphic place holder has not yet been scheduled
//          if (getEmptyGraphicTreeItemFor(graphicOwnerTreeItem) == null
//                  && scheduler.isAddEmptyGraphicTaskScheduled() == false) {
//              scheduler.scheduleAddEmptyGraphicTask(graphicOwnerTreeItem);
//          }
//      }

        parentRing.clear();

        // Remove insert line indicator
        insertLine.clearLine();

        // If an animation timeline is running
        // (auto-scroll when DND to the top or bottom of the Hierarchy),
        // we do not display insert indicators.
        if (animationScheduler.isTimelineRunning()) {
            return;
        }

        // Drop target has been updated because of (1)
        if (dragController.isDropAccepted()) {

            final FXOMObject dropTargetObject = dropTarget.getTargetObject();
            final TreeItem<HierarchyItem> rootTreeItem = hierarchyTreeView.getRootItem();

            if (dropTargetObject == null) {
                // No visual feedback in case of dropping the root node or no target defined
                return;
            }

            final TreeItem<HierarchyItem> accessoryOwnerTreeItem = hierarchyTreeView.lookupTreeItem(dropTargetObject);

            //==========================================================
            // ACCESSORIES :
            //
            // No need to handle the insert line indicator.
            // Border is set either on the accessory place holder cell
            // or on the accessory owner cell.
            //==========================================================

            Accessory targetAccessory = dropTarget instanceof AccessoryDropTarget ? ((AccessoryDropTarget) dropTarget).findTargetAccessory(drag.getDragSource().getDraggedObjects()) : null;

            // TreeItem is null when dropping below the datas
            // => the drop target is the root
            if (treeItem == null) {
                final Optional<HierarchyCell> cell = cellAssignments.getCell(rootTreeItem);
                cell.ifPresent(c -> c.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT));
            } else if (dropTarget instanceof AccessoryDropTarget && targetAccessory != null && treeItem.getValue().isPlaceHolder()) {

                final HierarchyItem item = treeItem.getValue();

                //final AccessoryDropTarget accessoryDropTarget = (AccessoryDropTarget) dropTarget;
                final Optional<HierarchyCell> cell;


                assert item != null;

                if (item.isPlaceHolder()) {
                    cell = Optional.of(treeCell);

                    //TODO nothing to do like below
//                } else if (accessoryDropTarget.getAccessory() == Accessory.GRAPHIC) {
//                    // Check if an empty graphic TreeItem has been added
//                    final TreeItem<HierarchyItem> graphicTreeItem
//                            = dndController.getEmptyGraphicTreeItemFor(treeItem);
//                    if (graphicTreeItem != null) {
//                        cell = HierarchyTreeViewUtils.getTreeCell(getTreeView(), graphicTreeItem);
//                    } else {
//                        cell = HierarchyTreeViewUtils.getTreeCell(getTreeView(), accessoryOwnerTreeItem1);
//                    }
                } else {
                    cell = cellAssignments.getCell(accessoryOwnerTreeItem);
                }

                cell.ifPresent(c -> c.setBorder(BorderSide.TOP_RIGHT_BOTTOM_LEFT));
            }//
            //==========================================================
            // SUB COMPONENTS :
            //
            // Need to handle the insert line indicator.
            //==========================================================
            else {
                insertLine.showForItem(accessoryOwnerTreeItem, treeItem, location);
            }
        }
    }

//    /**
//     * Returns the empty graphic TreeItem (if any) of the specified TreeItem,
//     * null otherwise.
//     *
//     * @param treeItem the TreeItem
//     * @return the empty graphic TreeItem
//     */
//    public TreeItem<HierarchyItem> getEmptyGraphicTreeItemFor(final TreeItem<HierarchyItem> treeItem) {
//        assert treeItem != null;
//        for (TreeItem<HierarchyItem> childTreeItem : treeItem.getChildren()) {
//            final HierarchyItem child = childTreeItem.getValue();
//            if (child instanceof HierarchyItemGraphic && child.isEmpty()) {
//                return childTreeItem;
//            }
//        }
//        return null;
//    }

    private DropTarget makeDropTarget(
            final TreeItem<HierarchyItem> treeItem,
            final DroppingMouseLocation location) {

        assert location != null;


        final FXOMObject dropTargetObject;
        final DropTarget result;
        Accessory accessory = null; // Used if we insert as accessory (drop over a place holder)
        int targetIndex = -1; // Used if we insert as sub components

        final FXOMDocument document = documentManager.fxomDocument().get();
        if (document == null || document.getFxomRoot() == null) {
            return rootDropTargetFactory.getDropTarget();
        }
        // TreeItem is null when dropping below the datas
        // => the drop target is the root
        if (treeItem == null) {
            dropTargetObject = document.getFxomRoot();

        } else {
            final HierarchyItem item = treeItem.getValue();
            assert item != null;

            boolean isRootTarget = item.getFxomObject() == document.getFxomRoot();

            // When the TreeItem is a place holder :
            //      the drop target is the place holder parent
            //      the accessory is set to the place holder value
            //      - if the place holder is empty
            //        whatever the location value is. Insert at end
            //      - otherwise
            //        the target index is set depending on the location value
            //------------------------------------------------------------------
            if (item.isPlaceHolder()) { // (1)

                assert !isRootTarget;
                assert item instanceof HierarchyItemAccessory;

                // Set the drop target
                final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                assert parentTreeItem != null; // Because of (1)
                dropTargetObject = parentTreeItem.getValue().getFxomObject();
                // Set the accessory
                if (item instanceof HierarchyItemAccessory) {
                    accessory = ((HierarchyItemAccessory) item).getAccessory();
                } else {
                    accessory = item.getMask().getAccessories().get(0);
                }

                if (item.isEmpty()) {
                    targetIndex = -1;
                } else {
                    // Set the target index
                    switch (location) {
                        case CENTER:
                        case TOP:
                            targetIndex = -1; // Insert at last position
                            break;
                        case BOTTOM:
                            if (treeItem.isLeaf() || !treeItem.isExpanded()) {
                                targetIndex = -1; // Insert at last position
                            } else {
                                targetIndex = 0; // Insert at first position
                            }
                            break;
                        default:
                            assert false;
                            break;

                    }
                }
            }
            //
            // TreeItem is not a place holder:
            // we set the drop target, accessory and target index
            // depending on the mouse location value
            //------------------------------------------------------------------
            else {
                switch (location) {

                    // REPARENTING
                    case CENTER:
                        dropTargetObject = item.getFxomObject();
                        targetIndex = -1; // Insert at last position
                        break;

                    // REORDERING ABOVE
                    case TOP:
                        // Dropping on TOP of the root TreeItem
                        if (isRootTarget) { // (2)
                            dropTargetObject = item.getFxomObject();
                            targetIndex = -1; // Insert at last position
                        } else {
                            final DragSource dragSource = drag.getDragSource();
                            final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                            assert parentTreeItem != null; // Because of (2)

                            HierarchyItem parentItem = parentTreeItem.getValue();

                            if (parentItem.isPlaceHolder()) {
                                //TODO new case to test
                                // If it is a placeholder get the parent
                                final TreeItem<HierarchyItem> realParentTreeItem = parentTreeItem.getParent();
                                final FXOMObject parentObject = realParentTreeItem.getValue().getFxomObject();
                                final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);

                                HierarchyItemAccessory accessoryItem = (HierarchyItemAccessory)parentItem;
                                accessory = accessoryItem.getAccessory();

                                if (parentMask.isAcceptingAccessory(accessory, dragSource.getDraggedObjects())) {
                                    dropTargetObject = parentObject;
                                    targetIndex = item.getFxomObject().getIndexInParentProperty();
                                }
//                                // Otherwise, attempt to set an accessory on the current TreeItem
                                else {
                                    dropTargetObject = item.getFxomObject();
                                }

                            } else {
                                // If the parent accepts sub components,
                                // this is a reordering gesture and the target is the parent
                                final FXOMObject parentObject = parentTreeItem.getValue().getFxomObject();
                                final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);
                                if (parentMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                                    dropTargetObject = parentObject;
                                    targetIndex = item.getFxomObject().getIndexInParentProperty();
                                } // Otherwise, attempt to set an accessory on the current TreeItem
                                else {
                                    dropTargetObject = item.getFxomObject();
                                }
                            }

                        }
                        break;

                    // REORDERING BELOW
                    case BOTTOM:
                        // Dropping on BOTTOM of the root TreeItem
                        if (isRootTarget) { // (3)
                            dropTargetObject = item.getFxomObject();
                            targetIndex = 0; // Insert at first position
                        } else {
                            if (treeItem.isLeaf() || !treeItem.isExpanded()) {
                                // If the parent accepts sub components,
                                // this is a reordering gesture and the target is the parent

                                final DragSource dragSource = drag.getDragSource();
                                final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                                assert parentTreeItem != null; // Because of (3)

                                HierarchyItem parentItem = parentTreeItem.getValue();

                                if (parentItem.isPlaceHolder()) {
                                    //TODO new case to test
                                    // If it is a placeholder get the parent
                                    final TreeItem<HierarchyItem> realParentTreeItem = parentTreeItem.getParent();
                                    final FXOMObject parentObject = realParentTreeItem.getValue().getFxomObject();
                                    final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);

                                    HierarchyItemAccessory accessoryItem = (HierarchyItemAccessory)parentItem;
                                    accessory = accessoryItem.getAccessory();

                                    if (parentMask.isAcceptingAccessory(accessory, dragSource.getDraggedObjects())) {
                                        dropTargetObject = parentObject;
                                        targetIndex = item.getFxomObject().getIndexInParentProperty() + 1;
                                    }
//                                    // Otherwise, attempt to set an accessory on the current TreeItem
                                    else {
                                        dropTargetObject = item.getFxomObject();
                                    }
                                } else {
                                    final FXOMObject parentObject = parentTreeItem.getValue().getFxomObject();
                                    final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);
                                    if (parentMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                                        dropTargetObject = parentTreeItem.getValue().getFxomObject();
                                        targetIndex = item.getFxomObject().getIndexInParentProperty() + 1;
                                    } // Otherwise, attempt to set an accessory on the current TreeItem
                                    else {
                                        dropTargetObject = item.getFxomObject();
                                    }
                                }

                            } else {
                                dropTargetObject = item.getFxomObject();
                                targetIndex = 0; // Insert at first position
                            }
                        }
                        break;
                    default:
                        assert false;
                        dropTargetObject = null;
                        break;
                }
            }
        }

        result = makeDropTarget(dropTargetObject, accessory, targetIndex);
        return result;
    }

    private DropTarget makeDropTarget(
            final FXOMObject dropTargetObject,
            final Accessory accessory,
            int targetIndex) {

        DropTarget result = null;

        if (dropTargetObject instanceof FXOMElement) {

            final DragSource dragSource = drag.getDragSource();
            assert dragSource != null;
            final FXOMElement dropTargetInstance = (FXOMElement) dropTargetObject;
            final DesignHierarchyMask dropTargetMask = designHierarchyMaskFactory.getMask(dropTargetInstance);

            if (accessory != null) {
             // Check if the drop target accepts sub components
                if (dropTargetMask.isAcceptingAccessory(accessory, dragSource.getDraggedObjects())) {
                    final FXOMObject beforeChild;
                    if (targetIndex == -1) {
                        beforeChild = null;
                    } else {
                        // targetIndex is the last sub component
                        if (targetIndex == dropTargetMask.getSubComponentCount(accessory, true)) {
                            beforeChild = null;
                        } else {
                            beforeChild = dropTargetMask.getSubComponentAtIndex(accessory, targetIndex, true);
                        }
                    }
                    result = accessoryDropTargetFactory.getDropTarget(dropTargetInstance, accessory, beforeChild);
                }
            } else {

                // Check if the drop target accepts sub components
                if (dropTargetMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                    final FXOMObject beforeChild;
                    if (targetIndex == -1) {
                        beforeChild = null;
                    } else {
                        // targetIndex is the last sub component
                        if (targetIndex == dropTargetMask.getSubComponentCount(dropTargetMask.getMainAccessory(), true)) {
                            beforeChild = null;
                        } else {
                            beforeChild = dropTargetMask.getSubComponentAtIndex(dropTargetMask.getMainAccessory(), targetIndex, true);
                        }
                    }
                    result = accessoryDropTargetFactory.getDropTarget(dropTargetInstance, beforeChild);
                } //
                // Check if the drop target accepts accessories
                else {
                    // Check if there is an accessory that can be accepted by the drop target.
                    // First we build the list of accessories that can be set by DND gesture.
//                    final Accessory[] accessories = {
//                        Accessory.TOP,
//                        Accessory.LEFT,
//                        Accessory.CENTER,
//                        Accessory.RIGHT,
//                        Accessory.BOTTOM,
//                        Accessory.CONTENT,
//                        Accessory.CONTEXT_MENU,
//                        Accessory.GRAPHIC,
//                        Accessory.TOOLTIP,
//                        Accessory.HEADER,
//                        Accessory.DP_GRAPHIC,
//                        Accessory.DP_CONTENT,
//                        Accessory.EXPANDABLE_CONTENT,
//                        Accessory.SCENE
//                    };
                    for (Accessory a : dropTargetMask.getAccessories()) {
                        final AccessoryDropTarget dropTarget = accessoryDropTargetFactory.getDropTarget(dropTargetInstance, a);
                        // If the accessory drop target accepts the dragged objects,
                        // we return this drop target.
                        // Otherwise, we look for the next accessory.
                        if (dropTarget.acceptDragSource(dragSource)) {
                            result = dropTarget;
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    public void handleTreeOnDragDone(final DragEvent event) {
        // DragController update
        assert shouldEndOnExit == false;
        drag.end();
        event.getDragboard().clear();
    }

    public void handleTreeOnDragDropped(final DragEvent event) {
        // If there is no document loaded
        // Should we allow to start with empty document in SB 2.0 ?
        if (documentManager.fxomDocument().get() == null) {
            return;
        }

        // DragController update
        drag.commit();
        // Do not invoke dragController.end here because we always receive a
        // DRAG_EXITED event which will perform the termination
        event.setDropCompleted(true);

        // Give the focus to the hierarchy
        hierarchyTreeView.getTreeView().requestFocus();
    }

    public void handleTreeOnDragEntered(final DragEvent event) {

        // When starting a DND gesture, disable parent ring updates
        parentRing.disable();

        // DragController update
        // The drag source is null if the drag gesture
        // has been started from outside (from the explorer / finder)
        if (drag.getDragSource() == null) { // Drag started externally
            // Build drag source
            final ExternalDragSource dragSource = externalDragSourceFactory.getDragSource(event.getDragboard());
            assert dragSource.isAcceptable();
            drag.begin(dragSource);
            shouldEndOnExit = true;
        }
    }

    public void handleTreeOnDragExited(final DragEvent event) {
        // When ending a DND gesture, enable parent ring updates
        parentRing.enable();

        // Cancel timeline animation if any
        animationScheduler.stopTimeline();

        // Retrieve the vertical scroll bar value before updating the TreeItems
        double verticalScrollBarValue = 0.0;
        final ScrollBar scrollBar = hierarchyTreeView.getScrollBar(Orientation.VERTICAL);
        if (scrollBar != null) {
            verticalScrollBarValue = scrollBar.getValue();
        }

        // DragController update
        drag.setDropTarget(null);
        if (shouldEndOnExit) {
            drag.end();
            shouldEndOnExit = false;
        }
        // Set back the vertical scroll bar value after the TreeItems have been updated
        if (scrollBar != null) {
            scrollBar.setValue(verticalScrollBarValue);
        }
    }

    public void handleTreeOnDragOver(final DragEvent event) {
        final ScrollBar verticalScrollBar = hierarchyTreeView.getScrollBar(Orientation.VERTICAL);

        // By dragging and hovering the cell within a few pixels
        // of the top or bottom of the Hierarchy,
        // the user can cause it to auto-scroll until the desired cell is in view.
        if (verticalScrollBar != null && verticalScrollBar.isVisible()) {
            final double eventY = event.getY();
            final double topY = hierarchyTreeView.getContentTopY();
            final double bottomY = hierarchyTreeView.getContentBottomY();

            // TOP auto scrolling zone
            if (topY <= eventY && eventY < topY + AUTO_SCROLLING_ZONE_HEIGHT) {
                // Start the timeline if not already playing
                if (!animationScheduler.isTimelineRunning()) {
                    animationScheduler.playDecrementAnimation(verticalScrollBar);
                }
            } // BOTTOM auto scrolling zone
            else if (bottomY >= eventY && eventY > bottomY - AUTO_SCROLLING_ZONE_HEIGHT) {
                // Start the timeline if not already playing
                if (!animationScheduler.isTimelineRunning()) {
                    animationScheduler.playIncrementAnimation(verticalScrollBar);
                }
            } else if (animationScheduler.isTimelineRunning()) {
                animationScheduler.stopTimeline();
            }
        }
    }

    public void handleTreeOnDragDetected(final MouseEvent event) {
        final ObservableList<TreeItem<HierarchyItem>> selectedTreeItems = hierarchyTreeView.getSelectedItems();

        // Do not start a DND gesture if there is an editing session on-going
        if (!inlineEdit.canGetFxmlText()) {
            return;
        }

        if (selection.isEmpty() == false) { // (1)
            if (selection.getGroup() instanceof DefaultSelectionGroupFactory) {
                // A set of regular component (ie fxom objects) are selected
                final DefaultSelectionGroupFactory osg = (DefaultSelectionGroupFactory) selection.getGroup();

                // Abort dragging an empty place holder
                for (TreeItem<HierarchyItem> selectedTreeItem : selectedTreeItems) {
                    final HierarchyItem item = selectedTreeItem.getValue();
                    if (item.isEmpty()) {
                        return;
                    }
                }
                // Retrieve the hit object
                final Cell<?> cell = hierarchyTreeView.lookupCell(event.getTarget());
                final Object item = cell.getItem();
                assert item instanceof HierarchyItemBase;
                final HierarchyItemBase hierarchyItem = (HierarchyItemBase) item;
                final FXOMObject hitObject = hierarchyItem.getFxomObject();
                assert (hitObject != null); // Because we cannot drag placeholders
                // Build drag source

                final DocumentDragSource dragSource = documentDragSourceFactory.getDragSource(osg.getSortedItems(),
                        hitObject);

                if (dragSource.isAcceptable()) {
                    // Start drag and drop
                    final Dragboard db = hierarchyTreeView.getTreeView().startDragAndDrop(TransferMode.COPY_OR_MOVE);
                    db.setContent(dragSource.makeClipboardContent());
                    db.setDragView(dragSource.makeDragView());
                    // DragController.begin
                    assert drag.getDragSource() == null;
                    drag.begin(dragSource);
                }

            } else {
                // Emergency code : a new type of AbstractSelectionGroup
                // exists but is not managed by this code yet.
                assert false : "Add implementation for " + selection.getGroup().getClass();
            }
        }
    }







}
