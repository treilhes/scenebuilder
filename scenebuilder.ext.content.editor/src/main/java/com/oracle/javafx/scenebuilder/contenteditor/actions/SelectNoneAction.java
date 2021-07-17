package com.oracle.javafx.scenebuilder.contenteditor.actions;

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
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.Selection;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.show.about", descriptionKey = "action.description.show.about")
public class SelectNoneAction extends AbstractAction {

    private final DocumentWindow documentWindow;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    
    public SelectNoneAction(
            @Autowired Api api,
            @Autowired DocumentWindow documentWindow,
            @Autowired DocumentManager documentManager,
            @Autowired InlineEdit inlineEdit) {
        super(api);
        this.documentWindow = documentWindow;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
    }

    /**
     * Returns true if the selection is not empty and no edition ongoing
     *
     * @return if the selection is not empty.
     */
    @Override
    public boolean canPerform() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else {
            Selection selection = documentManager.selectionDidChange().get().getSelection();
            result = selection.isEmpty() == false;
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
            tic.deselect();
        } else {
            assert canPerform();
            Selection selection = documentManager.selectionDidChange().get().getSelection();
            selection.clear();
        }
        return ActionStatus.DONE;
    }
}