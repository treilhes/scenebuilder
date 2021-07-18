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
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fs.preference.global.RecentItemsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class RevertAction extends AbstractAction {

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
    
    public RevertAction(
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
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        final Alert d = dialog.customAlert(documentWindow.getStage());
        d.setMessage(I18N.getString("alert.revert.question.message", documentWindow.getStage().getTitle()));
        d.setDetails(I18N.getString("alert.revert.question.details"));
        d.setOKButtonTitle(I18N.getString("label.revert"));

        if (d.showAndWait() == AbstractModalDialog.ButtonID.OK) {
            ActionStatus result = actionFactory.create(ReloadFileAction.class).perform();
            if (result == ActionStatus.FAILED) {
                main.documentWindowRequestClose(document);
            }
            return result;
        }
        
        return ActionStatus.DONE;
    }
}