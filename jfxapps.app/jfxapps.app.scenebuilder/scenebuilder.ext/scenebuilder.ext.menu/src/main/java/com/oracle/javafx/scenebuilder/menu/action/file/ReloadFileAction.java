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
package com.oracle.javafx.scenebuilder.menu.action.file;

import java.io.IOException;
import java.net.URL;

import org.scenebuilder.fxml.api.SbEditor;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.SbContext;
import com.oracle.javafx.scenebuilder.api.action.AbstractAction;
import com.oracle.javafx.scenebuilder.api.action.ActionExtensionFactory;
import com.oracle.javafx.scenebuilder.api.action.ActionMeta;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstance;
import com.oracle.javafx.scenebuilder.api.editors.ApplicationInstanceWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
@ActionMeta(nameKey = "action.name.save", descriptionKey = "action.description.save")
public class ReloadFileAction extends AbstractAction {

    private final ApplicationInstance document;
    private final FxmlDocumentManager documentManager;
    private final Dialog dialog;
    private final ApplicationInstanceWindow documentWindow;
    private final SbEditor editor;

    public ReloadFileAction(
            ActionExtensionFactory extensionFactory,
            @Autowired ApplicationInstance document,
            @Autowired FxmlDocumentManager documentManager,
            @Autowired ApplicationInstanceWindow documentWindow,
            @Autowired SbEditor editor,
            @Autowired Dialog dialog) {
        super(extensionFactory);
        this.document = document;
        this.documentManager = documentManager;
        this.editor = editor;
        this.dialog = dialog;
        this.documentWindow = documentWindow;
    }

    @Override
    public boolean canPerform() {
        return true;
    }

    @Override
    public ActionStatus doPerform() {

        final FXOMDocument fxomDocument = documentManager.fxomDocument().get();

        assert (fxomDocument != null) && (fxomDocument.getLocation() != null);
        final URL fxmlURL = fxomDocument.getLocation();
        try {
            final String fxmlText = FXOMDocument.readContentFromURL(fxmlURL);
            editor.setFxmlTextAndLocation(fxmlText, fxmlURL, true);
            document.updateLoadFileTime();
            // Here we do not invoke updateStageTitleAndPreferences() neither
            // watchingController.update()
        } catch (IOException e) {
            dialog.showErrorAndWait(I18N.getString("alert.title.open"),
                    I18N.getString("alert.open.failure1.message", documentWindow.getStage().getTitle()),
                    I18N.getString("alert.open.failure1.details"), e);
            return ActionStatus.FAILED;
        }
        return ActionStatus.DONE;
    }

}