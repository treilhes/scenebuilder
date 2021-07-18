package com.oracle.javafx.scenebuilder.fs.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class CloseFileAction extends AbstractAction {

    private final Document document;
    private final DocumentManager documentManager;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final DocumentWindow documentWindow;
    private final FileSystem fileSystem;
    private final MessageLogger messageLogger;
    private final RecentItemsPreference recentItemsPreference;
    private final Editor editor;
    private final Main main;
    private final ActionFactory actionFactory;
    
    public CloseFileAction(
            @Autowired Api api,
            @Autowired Document document,
            @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow,
            @Autowired Editor editor,
            @Autowired InlineEdit inlineEdit,
            @Autowired Dialog dialog,
            @Autowired FileSystem fileSystem,
            @Autowired ActionFactory actionFactory,
            @Autowired Main main,
            @Autowired MessageLogger messageLogger,
            @Autowired RecentItemsPreference recentItemsPreference) {
        super(api);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.fileSystem = fileSystem;
        this.actionFactory = actionFactory;
        this.main = main;
        this.messageLogger = messageLogger;
        this.recentItemsPreference = recentItemsPreference;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {

        // Makes sure that our window is front
        documentWindow.getStage().toFront();

        // Check if an editing session is on going
        if (inlineEdit.isTextEditingSessionOnGoing()) {
            // Check if we can commit the editing session
            if (!inlineEdit.canGetFxmlText()) {
                // Commit failed
                return ActionStatus.CANCELLED;
            }
        }

        // Checks if there are some pending changes
        final boolean closeConfirmed;
        if (documentManager.dirty().get()) {
            final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
            final Alert d = dialog.customAlert(documentWindow.getStage());
            d.setMessage(I18N.getString("alert.save.question.message", documentWindow.getStage().getTitle()));
            d.setDetails(I18N.getString("alert.save.question.details"));
            d.setOKButtonTitle(I18N.getString("label.save"));
            d.setActionButtonTitle(I18N.getString("label.do.not.save"));
            d.setActionButtonVisible(true);

            switch (d.showAndWait()) {
            default:
            case OK:
                closeConfirmed = (actionFactory.create(SaveOrSaveAsAction.class).checkAndPerform() == ActionStatus.DONE);
                break;
            case CANCEL:
                closeConfirmed = false;
                break;
            case ACTION: // Do not save
                closeConfirmed = true;
                break;
            }

        } else {
            // No pending changes
            closeConfirmed = true;
        }

        return closeConfirmed ? ActionStatus.DONE : ActionStatus.CANCELLED;
    }
}