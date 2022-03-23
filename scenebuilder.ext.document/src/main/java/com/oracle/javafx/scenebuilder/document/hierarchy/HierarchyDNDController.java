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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Drag;
import com.oracle.javafx.scenebuilder.api.DragSource;
import com.oracle.javafx.scenebuilder.api.HierarchyMask.Accessory;
import com.oracle.javafx.scenebuilder.api.control.DropTarget;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;
import com.oracle.javafx.scenebuilder.api.mask.DesignHierarchyMask;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.document.api.HierarchyDND;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.AccessoryDropTarget;
import com.oracle.javafx.scenebuilder.draganddrop.droptarget.RootDropTarget;

import javafx.scene.control.TreeItem;
import javafx.scene.input.DragEvent;

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

    private final Drag drag;
    private final RootDropTarget.Factory rootDropTargetFactory;
    private final DesignHierarchyMask.Factory designHierarchyMaskFactory;
    private final AccessoryDropTarget.Factory accessoryDropTargetFactory;

    private AbstractHierarchyPanelController panelController;
    private HierarchyTaskScheduler scheduler;
    private final DocumentManager documentManager;


    /**
     * Defines the mouse location within the cell when the dropping gesture
     * occurs.
     *
     * @treatAsPrivate
     */
    public enum DroppingMouseLocation {

        BOTTOM, CENTER, TOP
    }

    protected HierarchyDNDController(
            Drag drag,
            DocumentManager documentManager,
            RootDropTarget.Factory rootDropTargetFactory,
            DesignHierarchyMask.Factory designHierarchyMaskFactory,
            AccessoryDropTarget.Factory accessoryDropTargetFactory
            ) {
        this.drag = drag;
        this.documentManager = documentManager;
        this.rootDropTargetFactory = rootDropTargetFactory;
        this.designHierarchyMaskFactory = designHierarchyMaskFactory;
        this.accessoryDropTargetFactory = accessoryDropTargetFactory;
    }

    private void setControllerParameters(final AbstractHierarchyPanelController panelController) {
        this.panelController = panelController;
        this.scheduler = new HierarchyTaskScheduler(panelController);
    }

    public HierarchyTaskScheduler getScheduler() {
        return scheduler;
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     */
    public void handleOnDragDropped(
            final TreeItem<HierarchyItem> treeItem,
            final DragEvent event) {

        // Cancel timer if any
        scheduler.cancelTimer();
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     */
    public void handleOnDragEntered(
            final TreeItem<HierarchyItem> treeItem,
            final DragEvent event) {

        // Cancel timer if any
        scheduler.cancelTimer();

        if (treeItem == null) {
            return;
        }

        // Auto scrolling timeline has been started :
        // do not schedule any other task
        if (panelController.isTimelineRunning()) {
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
    public void handleOnDragExited(
            final TreeItem<HierarchyItem> treeItem,
            final DragEvent event,
            final DroppingMouseLocation location) {

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
    }

    /**
     * Called by the TreeCell/TreeTableRow event handler.
     *
     * @param treeItem the TreeItem
     * @param event the event
     * @param location the location
     */
    public void handleOnDragOver(
            final TreeItem<HierarchyItem> treeItem,
            final DragEvent event,
            final DroppingMouseLocation location) {

        //nothing to remove anymore
//        // Remove empty tree item graphic if previously added by the scheduler
//        // when we hover the empty graphic owner TreeItem on the top area
//        if (treeItem != null) {
//            final HierarchyItem item = treeItem.getValue();
//            assert item != null;
//            final TreeItem<HierarchyItem> graphicTreeItem = getEmptyGraphicTreeItemFor(treeItem);
//            if (graphicTreeItem != null && location == DroppingMouseLocation.TOP) {
//                treeItem.getChildren().remove(graphicTreeItem);
//            }
//        }

        // First update drop target
        final DropTarget dropTarget = makeDropTarget(treeItem, location);
        drag.setDropTarget(dropTarget);

        // Then update transfer mode
        event.acceptTransferModes(drag.getAcceptedTransferModes());

        //TODO nothing to add anymore
//        // Schedule adding empty graphic place holder job :
//        // The drop target must be a GRAPHIC AccessoryDropTarget
//        if (dragController.isDropAccepted()
//                && dropTarget instanceof AccessoryDropTarget
//                && ((AccessoryDropTarget) dropTarget).getAccessory() == Accessory.GRAPHIC) {
//            // Retrieve the GRAPHIC accessory owner
//            final TreeItem<HierarchyItem> graphicOwnerTreeItem;
//            if (treeItem != null) {
//                if (treeItem.getValue().isEmpty() == false) {
//                    graphicOwnerTreeItem = treeItem;
//                } else {
//                    // Empty graphic place holder
//                    // => the graphic owner is the parent
//                    graphicOwnerTreeItem = treeItem.getParent();
//                }
//            } else {
//                // TreeItem is null when dropping below the datas
//                // => the graphic owner is the root
//                graphicOwnerTreeItem = panelController.getRootItem();
//            }
//            assert graphicOwnerTreeItem != null;
//            assert graphicOwnerTreeItem.getValue().isEmpty() == false;
//            // Schedule adding empty graphic place holder if :
//            // - an empty graphic place holder has not yet been added
//            // - an empty graphic place holder has not yet been scheduled
//            if (getEmptyGraphicTreeItemFor(graphicOwnerTreeItem) == null
//                    && scheduler.isAddEmptyGraphicTaskScheduled() == false) {
//                scheduler.scheduleAddEmptyGraphicTask(graphicOwnerTreeItem);
//            }
//        }
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

        final TreeItem<HierarchyItem> rootTreeItem = panelController.getRootItem();
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
            dropTargetObject = rootTreeItem.getValue().getFxomObject();

        } else {
            final HierarchyItem item = treeItem.getValue();
            assert item != null;

            // When the TreeItem is a place holder :
            // - if the place holder is empty
            //      the drop target is the place holder parent
            //      the accessory is set to the place holder value
            // whatever the location value is.
            // - otherwise
            //      the drop target is the place holder item
            //      the accessory is set to null
            //      the target index is set depending on the location value
            //------------------------------------------------------------------
            if (item.isPlaceHolder()) { // (1)

                assert treeItem != rootTreeItem;
                assert item instanceof HierarchyItemAccessory;
//                item instanceof HierarchyItemBorderPane
//                        || item instanceof HierarchyItemGraphic
//                        || item instanceof HierarchyItemDialogPane;

                if (item.isEmpty()) {
                    // Set the drop target
                    final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                    assert parentTreeItem != null; // Because of (1)
                    dropTargetObject = parentTreeItem.getValue().getFxomObject();
                    // Set the accessory
                    if (item instanceof HierarchyItemAccessory) {
                        accessory = ((HierarchyItemAccessory) item).getPosition();
//                    } else if (item instanceof HierarchyItemBorderPane) {
//                        accessory = ((HierarchyItemBorderPane) item).getPosition();
//                    } else if (item instanceof HierarchyItemDialogPane) {
//                        accessory = ((HierarchyItemDialogPane) item).getAccessory();
//                    } else if (item instanceof HierarchyItemExpansionPanel) {
//                        accessory = ((HierarchyItemExpansionPanel) item).getAccessory();
//                    } else if (item instanceof HierarchyItemExpandedPanel) {
//                        accessory = ((HierarchyItemExpandedPanel) item).getAccessory();
                    } else {
                        accessory = item.getMask().getAccessories().get(0);
                    }
                } else {
                    // Set the drop target
                    dropTargetObject = item.getFxomObject();
                    // Set the accessory
                    accessory = null;
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
            } //
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
                        if (treeItem == rootTreeItem) { // (2)
                            dropTargetObject = item.getFxomObject();
                            targetIndex = -1; // Insert at last position
                        } else {
                            // If the parent accepts sub components,
                            // this is a reordering gesture and the target is the parent

                            final DragSource dragSource = drag.getDragSource();
                            final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                            assert parentTreeItem != null; // Because of (2)
                            final FXOMObject parentObject = parentTreeItem.getValue().getFxomObject();
                            final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);
                            if (parentMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                                dropTargetObject = parentTreeItem.getValue().getFxomObject();
                                targetIndex = item.getFxomObject().getIndexInParentProperty();
                            } // Otherwise, attempt to set an accessory on the current TreeItem
                            else {
                                dropTargetObject = item.getFxomObject();
                            }
                        }
                        break;

                    // REORDERING BELOW
                    case BOTTOM:
                        // Dropping on BOTTOM of the root TreeItem
                        if (treeItem == rootTreeItem) { // (3)
                            dropTargetObject = item.getFxomObject();
                            targetIndex = 0; // Insert at first position
                        } else {
                            if (treeItem.isLeaf() || !treeItem.isExpanded()) {
                                // If the parent accepts sub components,
                                // this is a reordering gesture and the target is the parent

                                final DragSource dragSource = drag.getDragSource();
                                final TreeItem<HierarchyItem> parentTreeItem = treeItem.getParent();
                                assert parentTreeItem != null; // Because of (3)
                                final FXOMObject parentObject = parentTreeItem.getValue().getFxomObject();
                                final DesignHierarchyMask parentMask = designHierarchyMaskFactory.getMask(parentObject);
                                if (parentMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                                    dropTargetObject = parentTreeItem.getValue().getFxomObject();
                                    targetIndex = item.getFxomObject().getIndexInParentProperty() + 1;
                                } // Otherwise, attempt to set an accessory on the current TreeItem
                                else {
                                    dropTargetObject = item.getFxomObject();
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

        if (dropTargetObject instanceof FXOMInstance) {

            final DragSource dragSource = drag.getDragSource();
            assert dragSource != null;
            final FXOMInstance dropTargetInstance = (FXOMInstance) dropTargetObject;
            if (accessory != null) {
                result = accessoryDropTargetFactory.getDropTarget(dropTargetInstance, accessory);
            } else {
                final DesignHierarchyMask dropTargetMask = designHierarchyMaskFactory.getMask(dropTargetInstance);
                // Check if the drop target accepts sub components
                if (dropTargetMask.isAcceptingSubComponent(dragSource.getDraggedObjects())) {
                    final FXOMObject beforeChild;
                    if (targetIndex == -1) {
                        beforeChild = null;
                    } else {
                        // targetIndex is the last sub component
                        if (targetIndex == dropTargetMask.getSubComponentCount()) {
                            beforeChild = null;
                        } else {
                            beforeChild = dropTargetMask.getSubComponentAtIndex(targetIndex);
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

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static final class Factory extends AbstractFactory<HierarchyDNDController> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public HierarchyDNDController newDndController(AbstractHierarchyPanelController owner) {
            return create(HierarchyDNDController.class, c -> c.setControllerParameters(owner));
        }

    }
}
