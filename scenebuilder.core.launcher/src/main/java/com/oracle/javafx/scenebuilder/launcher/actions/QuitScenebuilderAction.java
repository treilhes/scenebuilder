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
package com.oracle.javafx.scenebuilder.launcher.actions;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Document;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.panel.util.dialog.Alert;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.lifecycle.DisposeWithSceneBuilder;

import javafx.application.Platform;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
@Lazy
@ActionMeta(nameKey = "action.name.toggle.dock", descriptionKey = "action.description.toggle.dock")
public class QuitScenebuilderAction extends AbstractAction {

    private final static Logger logger = LoggerFactory.getLogger(QuitScenebuilderAction.class);

    private final Main main;
    private final Dialog dialog;
    private final List<DisposeWithSceneBuilder> finalizations;
    private final FileSystem fileSystem;

    public QuitScenebuilderAction(
            ActionExtensionFactory extensionFactory,
            @Autowired Main main,
            @Autowired FileSystem fileSystem,
            @Autowired Dialog dialog,
            @Lazy @Autowired(required = false) List<DisposeWithSceneBuilder> finalizations) {
        super(extensionFactory);
        this.main = main;
        this.fileSystem = fileSystem;
        this.dialog = dialog;
        this.finalizations = finalizations;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {


        // Check if an editing session is on going
        for (Document dwc : main.getDocumentWindowControllers()) {
            if (dwc.getEditorController().isTextEditingSessionOnGoing()) {
                // Check if we can commit the editing session
                if (dwc.getEditorController().canGetFxmlText() == false) {
                    // Commit failed
                    return ActionStatus.CANCELLED;
                }
            }
        }

        // Collects the documents with pending changes
        final List<Document> pendingDocs = new ArrayList<>();
        for (Document dwc : main.getDocumentWindowControllers()) {
            if (dwc.isDocumentDirty()) {
                pendingDocs.add(dwc);
            }
        }

        // Notifies the user if some documents are dirty
        final boolean exitConfirmed;
        switch (pendingDocs.size()) {
        case 0: {
            exitConfirmed = true;
            break;
        }

        case 1: {
            final Document dwc0 = pendingDocs.get(0);
            exitConfirmed = dwc0.performCloseAction() == ActionStatus.DONE;
            break;
        }

        default: {
            assert pendingDocs.size() >= 2;

            final Alert d = dialog.customAlert();
            d.setMessage(I18N.getString("alert.review.question.message", pendingDocs.size()));
            d.setDetails(I18N.getString("alert.review.question.details"));
            d.setOKButtonTitle(I18N.getString("label.review.changes"));
            d.setActionButtonTitle(I18N.getString("label.discard.changes"));
            d.setActionButtonVisible(true);

            switch (d.showAndWait()) {
            default:
            case OK: { // Review
                int i = 0;
                ActionStatus status;
                do {
                    status = pendingDocs.get(i++).performCloseAction();
                } while ((status == ActionStatus.DONE) && (i < pendingDocs.size()));
                exitConfirmed = (status == ActionStatus.DONE);
                break;
            }
            case CANCEL: {
                exitConfirmed = false;
                break;
            }
            case ACTION: { // Do not review
                exitConfirmed = true;
                break;
            }
            }
            break;
        }
        }

        // Exit if confirmed
        if (exitConfirmed) {
            for (Document dwc : main.getDocumentWindowControllers()) {
                // Write to java preferences before closing
                dwc.updatePreferences();
                main.documentWindowRequestClose(dwc);
            }
            fileSystem.stopWatcher();

            finalizations.forEach(a -> a.dispose());
            // TODO (elp): something else here ?
            logger.info(I18N.getString("log.stop"));
            Platform.exit();
        }


        return ActionStatus.DONE;
    }

}