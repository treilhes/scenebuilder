package com.oracle.javafx.scenebuilder.fs.action;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.fs.preference.global.WildcardImportsPreference;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class SaveAction extends AbstractAction {

    private final Document document;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final DocumentWindow documentWindow;
    private final MessageLogger messageLogger;
    private final WildcardImportsPreference wildcardImportsPreference;
    private final Editor editor;
    private final DocumentManager documentManager;
    
    public SaveAction(
            @Autowired Api api,
            @Autowired Document document,
            @Autowired DocumentManager documentManager,
            @Autowired DocumentWindow documentWindow,
            @Autowired Editor editor,
            @Autowired InlineEdit inlineEdit,
            @Autowired Dialog dialog,
            @Autowired MessageLogger messageLogger,
            @Autowired WildcardImportsPreference wildcardImportsPreference) {
        super(api);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.messageLogger = messageLogger;
        this.wildcardImportsPreference = wildcardImportsPreference;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus perform() {
        final FXOMDocument fxomDocument = getApi().getApiDoc().getDocumentManager().fxomDocument().get();
        assert fxomDocument != null;
        assert fxomDocument.getLocation() != null;

        ActionStatus result;
        if (inlineEdit.canGetFxmlText()) { // no editing session ongoing
            final Path fxmlPath;
            try {
                fxmlPath = Paths.get(fxomDocument.getLocation().toURI());
            } catch (URISyntaxException x) {
                // Should not happen
                throw new RuntimeException("Bug in " + getClass().getSimpleName(), x); // NOI18N
            }
            final String fileName = fxmlPath.getFileName().toString();

            try {
                final boolean saveConfirmed;
                if (checkLoadFileTime()) {
                    saveConfirmed = true;
                } else {
                    final Alert d = dialog.customAlert(documentWindow.getStage());
                    d.setMessage(I18N.getString("alert.overwrite.message", fileName));
                    d.setDetails(I18N.getString("alert.overwrite.details"));
                    d.setOKButtonVisible(true);
                    d.setOKButtonTitle(I18N.getString("label.overwrite"));
                    d.setDefaultButtonID(ButtonID.CANCEL);
                    d.setShowDefaultButton(true);
                    saveConfirmed = (d.showAndWait() == ButtonID.OK);
                }

                if (saveConfirmed) {
                    try {
                        // TODO remove after checking the new watching system is operational in
                        // EditorController or in filesystem
                        // watchingController.removeDocumentTarget();
                        final byte[] fxmlBytes = editor.getFxmlText(wildcardImportsPreference.getValue())
                                .getBytes(StandardCharsets.UTF_8); // NOI18N
                        Files.write(fxmlPath, fxmlBytes);
                        document.updateLoadFileTime();
                        
                        documentManager.dirty().set(false);
                        documentManager.saved().set(true);
                        
                        // TODO remove after checking the new watching system is operational in
                        // EditorController or in filesystem
                        // watchingController.update();

                        messageLogger.logInfoMessage("log.info.save.confirmation", I18N.getBundle(), fileName);
                        result = ActionStatus.DONE;
                    } catch (UnsupportedEncodingException x) {
                        // Should not happen
                        throw new RuntimeException("Bug", x); // NOI18N
                    }
                } else {
                    result = ActionStatus.CANCELLED;
                }
            } catch (IOException x) {
                dialog.showErrorAndWait(documentWindow.getStage(), null,
                        I18N.getString("alert.save.failure.message", fileName),
                        I18N.getString("alert.save.failure.details"), x);
                result = ActionStatus.CANCELLED;
            }
        } else {
            result = ActionStatus.CANCELLED;
        }

        return result;
    }
    
    private boolean checkLoadFileTime() throws IOException {
        assert editor.getFxmlLocation() != null;

        FileTime loadFileTime = document.getLoadFileTime();
        /*
         * loadFileTime == null => fxml file does not exist => TRUE
         *
         * loadFileTime != null => fxml file does/did exist
         *
         * currentFileTime == null => fxml file no longer exists => TRUE
         *
         * currentFileTime != null => fxml file still exists =>
         * loadFileTime.compare(currentFileTime) == 0
         */

        boolean result;
        if (loadFileTime == null) {
            // editorController.getFxmlLocation() does not exist yet
            result = true;
        } else {
            try {
                // editorController.getFxmlLocation() still exists
                // Check if its file time matches loadFileTime
                Path fxmlPath = Paths.get(editor.getFxmlLocation().toURI());
                FileTime currentFileTime = Files.getLastModifiedTime(fxmlPath);
                result = loadFileTime.compareTo(currentFileTime) == 0;
            } catch (NoSuchFileException x) {
                // editorController.getFxmlLocation() no longer exists
                result = true;
            } catch (URISyntaxException x) {
                throw new RuntimeException("Bug", x); // NOI18N
            }
        }

        return result;
    }
}