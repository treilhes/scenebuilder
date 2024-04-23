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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.api.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.job.JobManager;
import com.oracle.javafx.scenebuilder.api.ui.menu.ContextMenu;
import com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit;
import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.api.Hierarchy;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.hierarchy.display.MetadataInfoDisplayOption;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.HierarchyTreeViewController;
import com.oracle.javafx.scenebuilder.document.hierarchy.treeview.TreeItemFactory;

import io.reactivex.disposables.Disposable;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

/**
 * Hierarchy panel controller based on the TreeView control.
 */
@Component
@Scope(value = SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class HierarchyController implements Hierarchy {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyController.class);

    public static final String CSS_CLASS_HIERARCHY_PROMPT_LABEL = "hierarchy-prompt-label";

    private final FxmlDocumentManager documentManager;
    private final Selection selection;
    private final InlineEdit inlineEdit;
    private final TreeItemFactory rootTreeItemFactory;


    private final ContextMenu contextMenu;

    private final ObjectProperty<DisplayOption> displayOptionProperty;

    /**
     * @treatAsPrivate
     */
    protected final ListChangeListener<TreeItem<HierarchyItem>> treeItemSelectionListener = change -> treeItemSelectionDidChange();

    private Disposable selectionSubscription;

    private Label promptLabel;

    private final HierarchyCellAssignment cellAssignments;
    private final HierarchyParentRing parentRing;
    private final HierarchyTreeViewController hierarchyTreeView;
    private final HierarchyDNDController dndController;

    private TreeItem<HierarchyItem> rootTreeItem;

    public HierarchyController(
            ContextMenu contextMenu,
            FxmlDocumentManager documentManager,
            HierarchyCellAssignment cellAssignments,
            HierarchyDNDController dndController,
            HierarchyParentRing parentRing,
            HierarchyTreeViewController hierarchyTreeView,
            InlineEdit inlineEdit,
            JobManager jobManager,
            MetadataInfoDisplayOption defaultDisplayOptions,
            Selection selection,
            TreeItemFactory rootTreeItemFactory
            ) {

        this.cellAssignments = cellAssignments;
        this.contextMenu = contextMenu;
        this.dndController = dndController;
        this.documentManager = documentManager;
        this.hierarchyTreeView = hierarchyTreeView;
        this.inlineEdit = inlineEdit;
        this.parentRing = parentRing;
        this.rootTreeItemFactory = rootTreeItemFactory;
        this.selection = selection;

        displayOptionProperty = new SimpleObjectProperty<>(defaultDisplayOptions);

        documentManager.fxomDocument().subscribe(fd -> fxomDocumentDidChange(fd));
        documentManager.sceneGraphRevisionDidChange().subscribe(c -> sceneGraphRevisionDidChange());
        documentManager.cssRevisionDidChange().subscribe(c -> cssRevisionDidChange());
        jobManager.revisionProperty().addListener((ob, o, n) -> jobManagerRevisionDidChange());



        initializePanel();

        startListeningToEditorSelection();
    }

    protected void initializePanel() {
        assert hierarchyTreeView != null;

        // Panel may be either a TreeView or a TreeTableView
        assert hierarchyTreeView.getTreeView() != null;

        TreeView<HierarchyItem> treeView = hierarchyTreeView.getTreeView();
        // Drag events
        // ----------------------------------------------------------------------
        // DRAG_DONE event received when drag gesture
        // started from the hierarchy panel ends
        treeView.setOnDragDone(event -> dndController.handleTreeOnDragDone(event));
        treeView.setOnDragDropped(event -> dndController.handleTreeOnDragDropped(event));
        treeView.setOnDragEntered(event -> dndController.handleTreeOnDragEntered(event));
        treeView.setOnDragExited(event -> dndController.handleTreeOnDragExited(event));
        treeView.setOnDragOver(event -> dndController.handleTreeOnDragOver(event));

        // Key events
        // ----------------------------------------------------------------------
        treeView.setOnKeyPressed(event -> handleTreeOnKeyPressed(event));

        // Mouse events
        // ----------------------------------------------------------------------
        // DRAG_DETECTED event received when drag gesture
        // starts from the hierarchy panel
        treeView.setOnDragDetected(event -> dndController.handleTreeOnDragDetected(event));
        treeView.setOnMousePressed(event -> handleTreeOnMousePressed(event));

        // Setup the context menu
        treeView.setContextMenu(contextMenu.getContextMenu());

    }

    private Label getPromptLabel() {
        if (promptLabel == null) {
            promptLabel = new Label();
            promptLabel.getStyleClass().add(CSS_CLASS_HIERARCHY_PROMPT_LABEL);
            promptLabel.setMouseTransparent(true);
        }
        return promptLabel;
    }

    /**
     * Returns the display option property.
     *
     * @return the display option property.
     */
    @Override
    public ObjectProperty<DisplayOption> displayOptionProperty() {
        return displayOptionProperty;
    }

    @Override
    public void updatePanel() {

        if (hierarchyTreeView.getTreeView() != null) {
            // First update rootTreeItem + children TreeItems
            updateTreeItems();
            // Then update the TreeTableView with the updated rootTreeItem
            stopListeningToTreeItemSelection();
            hierarchyTreeView.setRooItem(rootTreeItem);
            startListeningToTreeItemSelection();
        }
    }

    protected void startListeningToTreeItemSelection() {
        logger.debug("Start listening treeview selection events");
        hierarchyTreeView.getSelectedItems().addListener(treeItemSelectionListener);
    }

    protected void stopListeningToTreeItemSelection() {
        logger.debug("Stop listening treeview selection events");
        hierarchyTreeView.getSelectedItems().removeListener(treeItemSelectionListener);
    }

    private void startListeningToEditorSelection() {
        selectionSubscription = documentManager.selectionDidChange().subscribe(c -> editorSelectionDidChange());
    }

    private void stopListeningToEditorSelection() {
        if (!selectionSubscription.isDisposed()) {
            selectionSubscription.dispose();
        }
    }

    /*
     * AbstractPanelController
     *
     * @treatAsPrivate
     */
    protected void fxomDocumentDidChange(FXOMDocument oldDocument) {
        // Clear the map containing the TreeItems expanded property values
        rootTreeItemFactory.clearExpandedMapCache();
        updatePanel();
    }

    /**
     * @treatAsPrivate
     */
    protected void sceneGraphRevisionDidChange() {
        if (hierarchyTreeView.getTreeView() != null) {
            // Update the map containing the TreeItems expanded property values
            // This map will be used after rebuilding the tree,
            // in order to update the TreeItems expanded property to their previous value
            rootTreeItemFactory.updateExpandedMapCache();

            // FXOM document has rebuilt the scene graph. Tree items must all
            // be updated because:
            // - classes of scene graph objects may have mutated
            // - infos displayed in the tree items may be obsoletes
            updatePanel();
            editorSelectionDidChange();
        }
    }

    /**
     * @treatAsPrivate
     */
    protected void cssRevisionDidChange() {
        sceneGraphRevisionDidChange();
    }

    /**
     * @treatAsPrivate
     */
    protected void jobManagerRevisionDidChange() {
        // FXOMDocument has been modified by a job.
        // Tree items must all be updated.
        sceneGraphRevisionDidChange();
    }

    /**
     * @treatAsPrivate
     */
    protected void editorSelectionDidChange() {
        final List<FXOMObject> selectedFxomObjects = new ArrayList<>();

        if (hierarchyTreeView.getTreeView() != null) {
            if (!selection.isEmpty()) {
                selectedFxomObjects.addAll(selection.getGroup().getItems());
            }

            // Update selected items
            stopListeningToTreeItemSelection();
            hierarchyTreeView.clearSelection();
            // Root TreeItem may be null
            if (rootTreeItem != null && selectedFxomObjects.isEmpty() == false) {
                List<TreeItem<HierarchyItem>> selectedTreeItems = hierarchyTreeView.lookupTreeItem(selectedFxomObjects);

                // TODO check selection of grid pane in document when column/row selected
//                if (selectedTreeItems.isEmpty() == false) {
//                    selectedTreeItems = lookupTreeItem(List.of(selection.getGroup().getAncestor()));
//                }

                if (selectedTreeItems.isEmpty() == false) {
                    hierarchyTreeView.select(selectedTreeItems);
                    // Scroll to the last TreeItem
                    final TreeItem<HierarchyItem> lastTreeItem = selectedTreeItems.get(selectedTreeItems.size() - 1);
                    // Call scrollTo only if the item is not visible.
                    // This avoid unexpected scrolling to occur in the hierarchy
                    // TreeView / TreeTableView while changing some property in the inspector.
                    if (hierarchyTreeView.isVisible(lastTreeItem) == false) {
                        hierarchyTreeView.scrollTo(lastTreeItem);
                    }
                }
            }
            startListeningToTreeItemSelection();

            // Update parent ring when selection did change
            parentRing.update();
        }
    }

    private void treeItemSelectionDidChange() {

        /*
         * Before updating the selection, we test if a text session is on-going and can
         * be completed cleanly. If not, we do not update the selection.
         */
        if (inlineEdit.canGetFxmlText()) {
            final Set<FXOMObject> selectedFxomObjects = new HashSet<>();
            for (TreeItem<HierarchyItem> selectedItem : hierarchyTreeView.getSelectedItems()) {
                // TreeItems may be null when selection is updating
                if (selectedItem != null) {
                    final FXOMObject fxomObject = selectedItem.getValue().getFxomObject();
                    // Placeholders may have a null fxom object
                    if (fxomObject != null) {
                        selectedFxomObjects.add(fxomObject);
                    }
                }
            }

            // Update selection

            // TODO ensure there won't be an infinite loop here by commenting the start/stop
            // listen
            stopListeningToEditorSelection();
            selection.select(selectedFxomObjects);
            startListeningToEditorSelection();

            // Update parent ring when selection did change
            parentRing.update();
        } /*
           * If a text session is on-going and cannot be completed cleanly, we go back to
           * previous TreeItem selection.
           */ else {
            editorSelectionDidChange();
        }
    }

    protected void updateTreeItems() {
        assert hierarchyTreeView.getTreeView() != null;
        final Parent parent = hierarchyTreeView.getTreeView().getParent();
        assert parent instanceof Pane;
        final Pane pane = (Pane) parent;
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        final Label label = getPromptLabel();
        if (fxomDocument == null || fxomDocument.getFxomRoot() == null) {
            rootTreeItem = null;
            // Add place holder to the parent
            if (fxomDocument == null) {
                label.setText(I18N.getString("contant.label.status.fxomdocument.null"));
            } else {
                label.setText(I18N.getString("content.label.status.invitation"));
            }
            if (pane.getChildren().contains(label) == false) {
                // This may occur when closing en empty document
                // => we switch from null FXOM root to null FXOM document
                pane.getChildren().add(label);
            }
        } else {
            rootTreeItem = rootTreeItemFactory.makeRootItem(fxomDocument.getFxomRoot());
            rootTreeItem.setExpanded(true);
            // Remove place holder from the parent
            ((Pane) parent).getChildren().remove(label);
        }
    }


    private void handleTreeOnKeyPressed(final KeyEvent event) {
        switch (event.getCode()) {

        // Handle Inline editing
        case ENTER:
            startEditingDisplayInfo();
            break;

        // Handle collapse all
        case LEFT:
            if (event.isAltDown()) {
                final List<TreeItem<HierarchyItem>> treeItems = hierarchyTreeView.getSelectedItems();
                if (!treeItems.isEmpty()) {
                    for (TreeItem<HierarchyItem> treeItem : treeItems) {
                        hierarchyTreeView.collapseAllTreeItems(treeItem);
                    }
                }
            }
            break;

        // Handle expand all
        case RIGHT:
            if (event.isAltDown()) {
                final List<TreeItem<HierarchyItem>> treeItems = hierarchyTreeView.getSelectedItems();
                if (!treeItems.isEmpty()) {
                    for (TreeItem<HierarchyItem> treeItem : treeItems) {
                        hierarchyTreeView.expandAllTreeItems(treeItem);
                    }
                }
            }
            break;

        default:
            break;
        }
    }
    private void handleTreeOnMousePressed(final MouseEvent event) {

        if (event.getButton() == MouseButton.SECONDARY) {
            // The context menu items depend on the selection so
            // we need to rebuild it each time it is invoked.
            contextMenu.updateContextMenuItems();
        }
    }

    private void startEditingDisplayInfo() {
        // Start inline editing the display info on ENTER key
        final List<TreeItem<HierarchyItem>> selectedTreeItems = hierarchyTreeView.getSelectedItems();
        if (selectedTreeItems.size() == 1) {
            final TreeItem<HierarchyItem> selectedTreeItem = selectedTreeItems.get(0);
            final HierarchyItem item = selectedTreeItem.getValue();
            final DisplayOption option = getDisplayOption();
            if (item != null && !option.isReadOnly(item.getMask())) {
                cellAssignments.getCell(selectedTreeItem).ifPresent(HierarchyCell::startEditingDisplayOption);
            }
        }
    }


    @Override
    public Parent getRoot() {
        return hierarchyTreeView.getRoot();
    }

}
