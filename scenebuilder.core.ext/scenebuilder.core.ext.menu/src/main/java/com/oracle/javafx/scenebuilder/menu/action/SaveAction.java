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
package com.oracle.javafx.scenebuilder.menu.action;

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

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.InlineEdit;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.fs.preference.global.WildcardImportsPreference;
import com.oracle.javafx.scenebuilder.om.api.OMDocument;

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

    public SaveAction(ActionExtensionFactory extensionFactory, @Autowired Document document,
            @Autowired DocumentManager documentManager, @Autowired DocumentWindow documentWindow,
            @Autowired Editor editor, @Autowired InlineEdit inlineEdit, @Autowired Dialog dialog,
            @Autowired MessageLogger messageLogger, @Autowired WildcardImportsPreference wildcardImportsPreference) {
        super(extensionFactory);
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
        final OMDocument<?> fxomDocument = documentManager.omDocument().get();
        boolean locationSet = fxomDocument != null && fxomDocument.getLocation() != null;
        return locationSet;
    }

    @Override
    public ActionStatus doPerform() {
        final OMDocument<?> fxomDocument = documentManager.omDocument().get();
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

                        messageLogger.logInfoMessage("log.info.save.confirmation", fileName);
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

    /**
     *
     * loadFileTime == null => fxml file does not exist => TRUE
     * or
     * loadFileTime != null => fxml file does/did exist
     * - then currentFileTime == null => fxml file no longer exists => TRUE
     * - then currentFileTime != null => fxml file still exists => loadFileTime.compare(currentFileTime) == 0
     *
     * @return
     * @throws IOException
     */
    private boolean checkLoadFileTime() throws IOException {
        assert editor.getFxmlLocation() != null;

        FileTime loadFileTime = document.getLoadFileTime();

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