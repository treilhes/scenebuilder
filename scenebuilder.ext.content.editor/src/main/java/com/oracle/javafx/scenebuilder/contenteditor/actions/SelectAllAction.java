package com.oracle.javafx.scenebuilder.contenteditor.actions;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.GridSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.ObjectSelectionGroup;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMObject;
import com.oracle.javafx.scenebuilder.core.mask.BorderPaneHierarchyMask;
import com.oracle.javafx.scenebuilder.core.mask.GridPaneHierarchyMask;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.BorderPane;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class SelectAllAction extends AbstractAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    
    public SelectAllAction(
            @Autowired Api api,
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager,
            @Autowired InlineEdit inlineEdit) {
        super(api);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
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
            Selection selection = documentManager.selectionDidChange().get().getSelection();
            
            if (fd == null || fd.getFxomRoot() == null) {
                return false;
            }
            if (selection.isEmpty()) { // (1)
                return true;
            } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
                final FXOMObject rootObject = fd.getFxomRoot();
                // Cannot select all if root is selected
                if (selection.isSelected(rootObject)) { // (1)
                    return false;
                } else {
                    // Cannot select all if all sub components are already selected
                    final FXOMObject ancestor = selection.getAncestor();
                    assert ancestor != null; // Because of (1)
                    final BorderPaneHierarchyMask mask = new BorderPaneHierarchyMask(ancestor);
                    // BorderPane special case : use accessories
                    if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
                        final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
                        final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
                        final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
                        final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
                        final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
                        for (FXOMObject bpAccessoryObject : new FXOMObject[] {
                            top, left, center, right, bottom}) {
                            if (bpAccessoryObject != null
                                    && selection.isSelected(bpAccessoryObject) == false) {
                                return true;
                            }
                        }
                    } else if (mask.isAcceptingSubComponent()) {
                        for (FXOMObject subComponentObject : mask.getSubComponents()) {
                            if (selection.isSelected(subComponentObject) == false) {
                                return true;
                            }
                        }
                    }
                }
            } else if (selection.getGroup() instanceof GridSelectionGroup) {
                final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
                // GridSelectionGroup => at least 1 row/column is selected
                assert gsg.getIndexes().isEmpty() == false;
                return true;
            } else {
                assert selection.getGroup() == null :
                        "Add implementation for " + selection.getGroup(); //NOCHECK
            }
            return false;
        
        }
        return result;
    }

    /**
     * Performs the select all control action.
     * Select all sub components of the selection common ancestor.
     */
    @Override
    public ActionStatus perform() {
        assert canPerform();
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.selectAll();
        } else {

            FXOMDocument fd = documentManager.fxomDocument().get();
            Selection selection = documentManager.selectionDidChange().get().getSelection();
            
            final FXOMObject rootObject = fd.getFxomRoot();
            if (selection.isEmpty()) { // (1)
                // If the current selection is empty, we select the root object
                selection.select(rootObject);
            } else if (selection.getGroup() instanceof ObjectSelectionGroup) {
                // Otherwise, select all sub components of the common ancestor ??
                final FXOMObject ancestor = selection.getAncestor();
                assert ancestor != null; // Because of (1)
                final BorderPaneHierarchyMask mask = new BorderPaneHierarchyMask(ancestor);
                final Set<FXOMObject> selectableObjects = new HashSet<>();
                // BorderPane special case : use accessories
                if (mask.getFxomObject().getSceneGraphObject() instanceof BorderPane) {
                    final FXOMObject top = mask.getAccessory(mask.getTopAccessory());
                    final FXOMObject left = mask.getAccessory(mask.getLeftAccessory());
                    final FXOMObject center = mask.getAccessory(mask.getCenterAccessory());
                    final FXOMObject right = mask.getAccessory(mask.getRightAccessory());
                    final FXOMObject bottom = mask.getAccessory(mask.getBottomAccessory());
                    for (FXOMObject accessoryObject : new FXOMObject[]{
                        top, left, center, right, bottom}) {
                        if (accessoryObject != null) {
                            selectableObjects.add(accessoryObject);
                        }
                    }
                } else {
                    assert mask.isAcceptingSubComponent(); // Because of (1)
                    selectableObjects.addAll(mask.getSubComponents());
                }
                selection.select(selectableObjects);
            } else if (selection.getGroup() instanceof GridSelectionGroup) {
                // Select ALL rows / columns
                final GridSelectionGroup gsg = (GridSelectionGroup) selection.getGroup();
                final FXOMObject gridPane = gsg.getHitItem();
                assert gridPane instanceof FXOMInstance;
                final GridPaneHierarchyMask gridPaneMask = new GridPaneHierarchyMask(gridPane);
                int size = 0;
                switch (gsg.getType()) {
                    case ROW:
                        size = gridPaneMask.getRowsSize();
                        break;
                    case COLUMN:
                        size = gridPaneMask.getColumnsSize();
                        break;
                    default:
                        assert false;
                        break;
                }
                // Select first index
                selection.select((FXOMInstance) gridPane, gsg.getType(), 0);
                for (int index = 1; index < size; index++) {
                    selection.toggleSelection((FXOMInstance) gridPane, gsg.getType(), index);
                }
            } else {
                assert selection.getGroup() == null :
                        "Add implementation for " + selection.getGroup(); //NOCHECK

            }
        
        }
    
        return ActionStatus.DONE;
    }
}