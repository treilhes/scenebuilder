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

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.ErrorReport;
import com.oracle.javafx.scenebuilder.api.ErrorReport.ErrorReportEntry;
import com.oracle.javafx.scenebuilder.api.HierarchyMask;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.InlineEdit.Type;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.images.ImageUtils;
import com.oracle.javafx.scenebuilder.api.factory.AbstractFactory;
import com.oracle.javafx.scenebuilder.api.util.StringUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.document.api.DisplayOption;
import com.oracle.javafx.scenebuilder.document.api.Hierarchy;
import com.oracle.javafx.scenebuilder.document.api.HierarchyCell;
import com.oracle.javafx.scenebuilder.document.api.HierarchyItem;
import com.oracle.javafx.scenebuilder.document.api.HierarchyPanel;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyCellAssignment;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyDNDController.DroppingMouseLocation;
import com.oracle.javafx.scenebuilder.document.hierarchy.HierarchyParentRing;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.WeakChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;

/**
 * TreeCells used by the hierarchy TreeView.
 *
 * p
 *
 * @param <T>
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class HierarchyTreeCell<T extends HierarchyItem> extends TreeCell<HierarchyItem> implements HierarchyCell {

    private static final Logger logger = LoggerFactory.getLogger(HierarchyTreeCell.class);

    private HierarchyPanel panelController;

    static final String TREE_CELL_GRAPHIC = "tree-cell-graphic";
    public static final String HIERARCHY_FIRST_CELL = "hierarchy-first-cell";
    static final String HIERARCHY_PLACE_HOLDER_LABEL = "hierarchy-place-holder-label";
    static final String HIERARCHY_READWRITE_LABEL = "hierarchy-readwrite-label";
    // Style class used for lookup
    static final String HIERARCHY_TREE_CELL = "hierarchy-tree-cell";


    public static final String CSS_CLASS_PARENT_UNSELECTED = "cell-no-selection";
    public static final String CSS_CLASS_PARENT_SELECTION_START = "parent-cell-selection-start";
    public static final String CSS_CLASS_PARENT_SELECTION_MIDDLE = "parent-cell-selection-middle";
    public static final String CSS_CLASS_PARENT_SELECTION_END = "parent-cell-selection-end";
    public static final String CSS_CLASS_PARENT_SELECTION_SINGLE = "cell-selection-single";
    public static final String CSS_CLASS_INSERT_LINE = "cell-insert-here";


    private final HBox graphic = new HBox();
    private final Label placeHolderLabel = new Label();
    private final Label classNameInfoLabel = new Label();
    private final Label displayInfoLabel = new Label();
    private final ImageView placeHolderImageView = new ImageView();
    private final ImageView classNameImageView = new ImageView();
    private final ImageView warningBadgeImageView = new ImageView();
    private final ImageView includedFileImageView = new ImageView();
    // Stack used to add badges over the top of the node icon
    private final StackPane iconsStack = new StackPane();
    // We use a label to set a tooltip over the node icon
    // (StackPane does not allow to set tooltips)
    private final Label iconsLabel = new Label();
    private final Tooltip warningBadgeTooltip = new Tooltip();

    // Listener for the display option used to update the display info label
    final ChangeListener<DisplayOption> displayOptionListener = (ov, t, t1) -> {
        // Update display info for non empty cells
        if (!isEmpty() && getItem() != null && !getItem().isEmpty() && t1 != null) {

            final boolean hasInfo = t1.hasValue(getItem().getMask());
            String displayInfo = t1.getResolvedValue(getItem().getMask());

            displayInfo = StringUtils.firstLine(displayInfo, "...");

            displayInfoLabel.setText(displayInfo);
            displayInfoLabel.setManaged(hasInfo);
            displayInfoLabel.setVisible(hasInfo);
        }
    };

    private final InlineEdit inlineEdit;
    private final ErrorReport errorReport;
    private final Hierarchy hierarchy;
    private final HierarchyCellAssignment cellAssignment;
    private final HierarchyParentRing parentRing;
    private final HierarchyDNDController dndController;

    protected HierarchyTreeCell(
            InlineEdit inlineEdit,
            ErrorReport errorReport,
            Hierarchy hierarchy,
            HierarchyCellAssignment cellAssignment,
            HierarchyParentRing parentRing,
            HierarchyDNDController dndController) {
        super();
        this.inlineEdit = inlineEdit;
        this.errorReport = errorReport;
        this.cellAssignment = cellAssignment;
        this.parentRing = parentRing;
        this.dndController = dndController;
        this.hierarchy = hierarchy;

        iconsStack.getChildren().setAll(
                classNameImageView,
                warningBadgeImageView);
        iconsLabel.setGraphic(iconsStack);
        // RT-31645 : we cannot dynamically update the HBox graphic children
        // in the cell.updateItem method.
        // We set once the graphic children, then we update the managed property
        // of the children depending on the cell item.
        graphic.getChildren().setAll(
                includedFileImageView,
                placeHolderImageView,
                iconsLabel,
                placeHolderLabel,
                classNameInfoLabel,
                displayInfoLabel);

        // Add style class used when invoking lookupAll
        this.getStyleClass().add(HIERARCHY_TREE_CELL);

        // CSS
        graphic.getStyleClass().add(TREE_CELL_GRAPHIC);
        displayInfoLabel.getStyleClass().add(HIERARCHY_READWRITE_LABEL);
        placeHolderLabel.getStyleClass().add(HIERARCHY_PLACE_HOLDER_LABEL);
        // Layout
        classNameInfoLabel.setMinWidth(Region.USE_PREF_SIZE);
        displayInfoLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(displayInfoLabel, Priority.ALWAYS);


        // Key events
        //----------------------------------------------------------------------
        final EventHandler<KeyEvent> keyEventHandler = e -> filterKeyEvent(e);
        this.addEventFilter(KeyEvent.ANY, keyEventHandler);

        // Mouse events
        //----------------------------------------------------------------------
        final EventHandler<MouseEvent> mouseEventHandler = e -> filterMouseEvent(e);
        this.addEventFilter(MouseEvent.ANY, mouseEventHandler);

    }

    private void setCellParameters(HierarchyPanel owner) {
        panelController = owner;

        hierarchy.displayOptionProperty().addListener(new WeakChangeListener<>(displayOptionListener));

        // Drag events
        //----------------------------------------------------------------------

        setOnDragDropped(event -> dndController.handleCellOnDragDropped(HierarchyTreeCell.this, event));
        setOnDragEntered(event -> dndController.handleCellOnDragEntered(HierarchyTreeCell.this, event));
        setOnDragExited(event -> dndController.handleCellOnDragExited(HierarchyTreeCell.this, event));
        setOnDragOver(event -> dndController.handleCellOnDragOver(HierarchyTreeCell.this, event));

    }

    @Override
    public void updateItem(HierarchyItem item, boolean empty) {
        super.updateItem(item, empty);

        cellAssignment.assign(getTreeItem(), this);

        // The cell is not empty (TreeItem is not null)
        // AND the TreeItem value is not null
        if (!empty && item != null) {
            updateLayout(item);
            setGraphic(graphic);
            setText(null);
            // Update parent ring when scrolling / resizing vertically / expanding and collapsing
            parentRing.update();
        } else {
            assert item == null;
            setGraphic(null);
            setText(null);
            // Clear CSS for empty cells
            this.clearBorders();
        }


    }

    private void filterKeyEvent(final KeyEvent ke) {
        // empty
    }

    private void filterMouseEvent(final MouseEvent me) {

        if (me.getEventType() == MouseEvent.MOUSE_PRESSED
                && me.getButton() == MouseButton.PRIMARY) {

            // Mouse pressed on a non empty cell :
            // => we may start inline editing
            if (isEmpty() == false) { // (1)
                if (me.getClickCount() >= 2) {
                    // Start inline editing the display info on double click OVER the display info label
                    // Double click over the class name label will end up with the native expand/collapse behavior
                    final HierarchyItem item = getItem();
                    assert item != null; // Because of (1)
                    final DisplayOption option = hierarchy.getDisplayOption();
                    if (item != null && !option.isReadOnly(item.getMask()) && displayInfoLabel.isHover()) {
                        logger.debug("Request edition of item {}/{}", item.getMask().getFxomObject(), item.getMask().getFxomObject().hashCode());
                        startEditingDisplayOption();
                        // Consume the event so the native expand/collapse behavior is not performed
                        me.consume();
                    }
                }
            } //
            // Mouse pressed on an empty cell
            // => we perform select none
            else {
                // We clear the TreeView selection.
                // Note that this is not the same as invoking selection.clear().
                // Indeed, when empty BorderPane place holders are selected,
                // the SB selection is empty whereas the TreeView selection is not.
                getTreeView().getSelectionModel().clearSelection();
            }
        }
        updateCursor(me);
    }

    private void updateCursor(final MouseEvent me) {
        final Scene scene = getScene();

        if (scene == null) {
            // scene may be null when tree view is collapsed
            return;
        }
        // When another window is focused (just like the preview window),
        // we use default cursor
        if (!getScene().getWindow().isFocused()) {
            scene.setCursor(Cursor.DEFAULT);
            return;
        }
        if (isEmpty()) {
            scene.setCursor(Cursor.DEFAULT);
        } else {
            final TreeItem<HierarchyItem> rootTreeItem = getTreeView().getRoot();
            final HierarchyItem item = getTreeItem().getValue();
            assert item != null;
            boolean isRoot = getTreeItem() == rootTreeItem;
            boolean isEmpty = item.isEmpty();

            if (me.getEventType() == MouseEvent.MOUSE_ENTERED) {
                if (!me.isPrimaryButtonDown()) {
                    // Cannot DND root or place holder items
                    if (isRoot || isEmpty) {
                        setCursor(Cursor.DEFAULT);
                    } else {
                        setCursor(Cursor.OPEN_HAND);
                    }
                }
            } else if (me.getEventType() == MouseEvent.MOUSE_PRESSED) {
                // Cannot DND root or place holder items
                if (isRoot || isEmpty) {
                    setCursor(Cursor.DEFAULT);
                } else {
                    setCursor(Cursor.CLOSED_HAND);
                }
            } else if (me.getEventType() == MouseEvent.MOUSE_RELEASED) {
                // Cannot DND root or place holder items
                if (isRoot || isEmpty) {
                    setCursor(Cursor.DEFAULT);
                } else {
                    setCursor(Cursor.OPEN_HAND);
                }
            } else if (me.getEventType() == MouseEvent.MOUSE_EXITED) {
                setCursor(Cursor.DEFAULT);
            }
        }
    }

    /**
     * *************************************************************************
     * Inline editing
     *
     * We cannot use the FX inline editing because it occurs on selection +
     * simple mouse click
     * *************************************************************************
     */
    @Override
    public void startEditingDisplayOption() {

        DisplayOption displayOption = hierarchy.getDisplayOption();
        final HierarchyMask mask = getItem().getMask();

        assert displayOption != null;
        assert mask != null;
        assert displayOption.hasValue(mask);
        assert !displayOption.isReadOnly(mask);

        final TextInputControl editor;

        logger.debug("Request edition of item {}/{}", mask.getFxomObject(), mask.hashCode());

        // display option may use either a TextField or a TextArea
        final Type type = displayOption.isMultiline(mask) ? Type.TEXT_AREA : Type.TEXT_FIELD;
        final String initialValue = displayOption.getValue(mask);

        editor = inlineEdit.createTextInputControl(type, displayInfoLabel, initialValue);
        // CSS
        final ObservableList<String> styleSheets = panelController.getRoot().getStylesheets();
        editor.getStylesheets().addAll(styleSheets);
        editor.getStyleClass().add("theme-presets"); //NOCHECK
        editor.getStyleClass().add(InlineEdit.INLINE_EDITOR_CLASS);

        // 2) Build the COMMIT Callback
        // This callback will be invoked to commit the new value
        // It returns true if the value is unchanged or if the commit succeeded,
        // false otherwise
        //----------------------------------------------------------------------
        final Callback<String, Boolean> requestCommit = newValue -> {
            // 1) Check the input value is valid
            // 2) If valid, commit the new value and return true
            // 3) Otherwise, return false
            final HierarchyItem item = getItem();
            // Item may be null when invoking UNDO while inline editing session is on going
            if (item != null) {
                assert newValue != null;

                final DisplayOption option = hierarchy.getDisplayOption();
                option.setValue(mask, newValue);
            }
            logger.debug("Validate new value of {}/{}", mask.getFxomObject(), mask.getFxomObject().hashCode());
            return true;
        };
        logger.debug("startEditingSession of {}/{}", getItem().getMask().getFxomObject(), getItem().getMask().getFxomObject().hashCode());
        inlineEdit.startEditingSession(editor, displayInfoLabel, requestCommit, null);
    }

    private void updateLayout(HierarchyItem item) {

        assert item != null;
        final FXOMObject fxomObject = item.getFxomObject();

        // Update styling
        this.getStyleClass().removeAll(HIERARCHY_FIRST_CELL);
        if (fxomObject != null && fxomObject.getParentObject() == null) {
            this.getStyleClass().add(HIERARCHY_FIRST_CELL);
        }

        // Update ImageViews
        final Image placeHolderImage = item.getPlaceHolderImage();
        placeHolderImageView.setImage(placeHolderImage);
        placeHolderImageView.setManaged(placeHolderImage != null);

        final Image classNameImage = item.getClassNameIcon();
        classNameImageView.setImage(classNameImage);
        classNameImageView.setManaged(classNameImage != null);

        // Included file
        if (fxomObject instanceof FXOMIntrinsic
                && ((FXOMIntrinsic) fxomObject).getType() == FXOMIntrinsic.Type.FX_INCLUDE) {
            final URL resource = ImageUtils.getNodeIconURL("Included.png");
            includedFileImageView.setImage(ImageUtils.getImage(resource));
            includedFileImageView.setManaged(true);
        } else {
            includedFileImageView.setImage(null);
            includedFileImageView.setManaged(false);
        }

        final List<ErrorReportEntry> entries = getErrorReportEntries(item);
        if (entries != null) {
            assert !entries.isEmpty();
            // Update tooltip with the first entry
            final ErrorReportEntry entry = entries.get(0);
            warningBadgeTooltip.setText(getErrorReport(entry));
            warningBadgeImageView.setImage(ImageUtils.getWarningBadgeImage());
            warningBadgeImageView.setManaged(true);
            iconsLabel.setTooltip(warningBadgeTooltip);
        } else {
            warningBadgeTooltip.setText(null);
            warningBadgeImageView.setImage(null);
            warningBadgeImageView.setManaged(false);
            iconsLabel.setTooltip(null);
        }

        // Update Labels
        final String placeHolderInfo = item.getPlaceHolderInfo();
        placeHolderLabel.setText(placeHolderInfo);
        placeHolderLabel.setManaged(item.isEmpty());
        placeHolderLabel.setVisible(item.isEmpty());

        final String classNameInfo = item.getClassNameInfo();
        classNameInfoLabel.setText(classNameInfo);
        classNameInfoLabel.setManaged(classNameInfo != null);
        classNameInfoLabel.setVisible(classNameInfo != null);

        final DisplayOption option = hierarchy.getDisplayOption();

        HierarchyMask mask = getItem().getMask();
        final boolean hasInfo = option.hasValue(mask);
        String displayInfo = option.getResolvedValue(mask);

        displayInfo = StringUtils.firstLine(displayInfo, "...");

        // Do not allow inline editing of the I18N value
        if (option.isReadOnly(mask)) {
            displayInfoLabel.getStyleClass().removeAll(HIERARCHY_READWRITE_LABEL);
        } else {
            if (displayInfoLabel.getStyleClass().contains(HIERARCHY_READWRITE_LABEL) == false) {
                displayInfoLabel.getStyleClass().add(HIERARCHY_READWRITE_LABEL);
            }
        }
        displayInfoLabel.setText(displayInfo);
        displayInfoLabel.setManaged(hasInfo);
        displayInfoLabel.setVisible(hasInfo);
    }

    /**
     * @param entry
     * @return
     */
    private String getErrorReport(ErrorReportEntry entry) {
        return errorReport.getText(entry);
    }

    private List<ErrorReportEntry> getErrorReportEntries(HierarchyItem item) {
        if (item == null || item.isEmpty()) {
            return null;
        }
        final FXOMObject fxomObject = item.getFxomObject();
        assert fxomObject != null;
        return errorReport.query(fxomObject, !getTreeItem().isExpanded());
    }

    @Override
    public DroppingMouseLocation getDroppingMouseLocation(final DragEvent event) {
        final DroppingMouseLocation location;
        if (this.getTreeItem() != null) {
            if ((getHeight() * 0.25) > event.getY()) {
                location = DroppingMouseLocation.TOP;
            } else if ((getHeight() * 0.75) < event.getY()) {
                location = DroppingMouseLocation.BOTTOM;
            } else {
                location = DroppingMouseLocation.CENTER;
            }
        } else {
            location = DroppingMouseLocation.BOTTOM;
        }
        return location;
    }

    @Override
    public void setBorder(BorderSide side) {
        removeBorderClasses();

//        boolean isFirstCell = cell.getStyleClass() != null
//                && cell.getStyleClass().contains(HIERARCHY_FIRST_CELL);
        //final Border border;
        final String cssClass;
        switch (side) {
            case BOTTOM:
                //border = isFirstCell ? firstCellBottomBorder : bottomBorder;
                cssClass = CSS_CLASS_INSERT_LINE;
                break;
            case RIGHT_BOTTOM_LEFT:
                //border = isFirstCell ? firstCellRightBottomLeftBorder : rightBottomLeftBorder;
                cssClass = CSS_CLASS_PARENT_SELECTION_END;
                break;
            case RIGHT_LEFT:
                //border = isFirstCell ? firstCellRightLeftBorder : rightLeftBorder;
                cssClass = CSS_CLASS_PARENT_SELECTION_MIDDLE;
                break;
            case TOP_RIGHT_BOTTOM_LEFT:
                //border = isFirstCell ? firstCellTopRightBottomLeftBorder : topRightBottomLeftBorder;
                cssClass = CSS_CLASS_PARENT_SELECTION_SINGLE;
                break;
            case TOP_RIGHT_LEFT:
                //border = isFirstCell ? firstCellTopRightLeftBorder : topRightLeftBorder;
                cssClass = CSS_CLASS_PARENT_SELECTION_START;
                break;
            default:
                cssClass = null;
                assert false;
                break;
        }
        //assert border != null;
        assert cssClass != null;
        //cell.setBorder(border);
        if (cssClass != null) {
            getStyleClass().add(cssClass);
        }

    }

    private void removeBorderClasses() {
        getStyleClass().removeAll(CSS_CLASS_PARENT_SELECTION_START,
                CSS_CLASS_PARENT_SELECTION_MIDDLE, CSS_CLASS_PARENT_SELECTION_END, CSS_CLASS_PARENT_SELECTION_SINGLE,
                CSS_CLASS_INSERT_LINE, CSS_CLASS_PARENT_UNSELECTED);
    }

    /**
     *
     *
     */
    @Override
    public void clearBorders() {
        setTransparentBorder();
    }

    private void setTransparentBorder() {
        removeBorderClasses();
        getStyleClass().add(CSS_CLASS_PARENT_UNSELECTED);
    }



    @Override
    public String toString() {
        String superToString = super.toString();
        String localToString = getItem() == null ? "" : getItem().toString();
        return String.format("%s, item=%s, index=%s", superToString, localToString, getIndex());
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    @SuppressWarnings({"unchecked", "rawtypes"})
    public final static class Factory extends AbstractFactory<HierarchyTreeCell> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }

        public TreeCell<HierarchyItem> newCell(HierarchyPanel owner) {
            return create(HierarchyTreeCell.class, c -> c.setCellParameters(owner));
        }
    }
}
