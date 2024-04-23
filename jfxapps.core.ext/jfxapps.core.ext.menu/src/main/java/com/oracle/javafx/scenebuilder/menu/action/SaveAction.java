/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
import java.nio.file.Path;
import java.nio.file.Paths;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.application.ApplicationInstance;
import com.oracle.javafx.scenebuilder.api.application.InstanceWindow;
import com.oracle.javafx.scenebuilder.api.application.InstancesManager;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Alert.ButtonID;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.api.ui.misc.InlineEdit;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.fs.preference.global.WildcardImportsPreference;

@Prototype
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class SaveAction extends AbstractAction {

    private final ApplicationInstance document;
    private final InlineEdit inlineEdit;
    private final Dialog dialog;
    private final InstanceWindow documentWindow;
    private final MessageLogger messageLogger;
    private final InstancesManager editor;
    private final DocumentManager documentManager;
    private final FileSystem fileSystem;

    // @formatter:off
    public SaveAction(
            ActionExtensionFactory extensionFactory,
            ApplicationInstance document,
            DocumentManager documentManager,
            InstanceWindow documentWindow,
            InstancesManager editor,
            InlineEdit inlineEdit,
            Dialog dialog,
            MessageLogger messageLogger,
            FileSystem fileSystem,
            WildcardImportsPreference wildcardImportsPreference) {
     // @formatter:on
        super(extensionFactory);
        this.document = document;
        this.documentManager = documentManager;
        this.inlineEdit = inlineEdit;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
        this.messageLogger = messageLogger;
        this.fileSystem = fileSystem;
    }

    @Override
    public boolean canPerform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        boolean locationSet = fxomDocument != null && fxomDocument.getLocation() != null;
        return locationSet;
    }

    @Override
    public ActionStatus doPerform() {
        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();
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
                if (fileSystem.checkLoadFileTime()) {
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

                        fileSystem.save();

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

}