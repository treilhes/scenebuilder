package com.oracle.javafx.scenebuilder.core.clipboard.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Content;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.action.Action;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.clipboard.ClipboardHandler;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.clipboard.action.CopyAction;
import com.oracle.javafx.scenebuilder.core.clipboard.action.CutAction;
import com.oracle.javafx.scenebuilder.core.clipboard.action.PasteAction;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.Clipboard;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
public class ClipboardController implements com.oracle.javafx.scenebuilder.api.clipboard.Clipboard {
    
    private final DocumentWindow documentWindow;
    private final Editor editorController;
    private final Content contentPanelController;
    private final InlineEdit inlineEdit;
    private final DocumentManager documentManager;
    private final ActionFactory actionFactory;
    
    public ClipboardController(
            @Autowired DocumentWindow documentWindow, 
            @Autowired Editor editorController,
            @Autowired InlineEdit inlineEdit,
            @Autowired Content contentPanelController,
            @Autowired DocumentManager documentManager,
            @Autowired ActionFactory actionFactory) {
        super();
        this.documentWindow = documentWindow;
        this.editorController = editorController;
        this.contentPanelController = contentPanelController;
        this.inlineEdit = inlineEdit;
        this.documentManager = documentManager;
        this.actionFactory = actionFactory;
    }

    @Override
    public boolean canPerformCopy() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        //} else if (isCssRulesEditing(focusOwner) || isCssTextEditing(focusOwner)) {
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformCopy();
        } else {
            result = actionFactory.create(CopyAction.class).canPerform();
        }
        return result;
    }

    @Override
    public void performCopy() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.copy();
        //} else if (isCssRulesEditing(focusOwner)) {
            //cssPanelController.copyRules();
        //} else if (isCssTextEditing(focusOwner)) {
            // CSS text pane is a WebView
            // Let the WebView handle the copy action natively
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performCopy();
        } else {
            actionFactory.create(CopyAction.class).perform();
        }
    }

    @Override
    public boolean canPerformCut() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isPopupEditing(focusOwner)) {
            return false;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            result = tic.getSelectedText() != null && !tic.getSelectedText().isEmpty();
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformCut();
        } else {
            result = actionFactory.create(CutAction.class).canPerform();
        }
        return result;
    }

    @Override
    public void performCut() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.cut();
        } else if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performCut();
        } else {
            actionFactory.create(CutAction.class).perform();
        }
    }

    @Override
    public boolean canPerformPaste() {
        boolean result;
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            result = cphandler.canPerformPaste();
        } else if (actionFactory.create(PasteAction.class).canPerform()) {
            result = true;
        } else if (inlineEdit.isTextInputControlEditing(focusOwner)) {
            result = Clipboard.getSystemClipboard().hasString();
        } else {
            result = false;
        }
        return result;
    }

    @Override
    public void performPaste() {
        final Node focusOwner = documentWindow.getScene().getFocusOwner();
        final Object focusComponent = documentManager.focused().get();
        final Action pasteAction = actionFactory.create(PasteAction.class);
        // If there is FXML in the clipboard, we paste the FXML whatever the focus owner
        // is
        if (focusComponent != null && focusComponent instanceof ClipboardHandler) {
            ClipboardHandler cphandler = (ClipboardHandler)focusComponent;
            cphandler.performPaste();
        } else if (pasteAction.canPerform()) {
            pasteAction.perform();
            // Give focus to content panel
            contentPanelController.getGlassLayer().requestFocus();
        } else {
            assert inlineEdit.isTextInputControlEditing(focusOwner);
            final TextInputControl tic = inlineEdit.getTextInputControl(focusOwner);
            tic.paste();
        }
    }
    

}
