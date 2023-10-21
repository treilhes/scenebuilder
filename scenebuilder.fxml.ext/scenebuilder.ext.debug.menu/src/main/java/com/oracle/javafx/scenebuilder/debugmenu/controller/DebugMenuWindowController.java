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
package com.oracle.javafx.scenebuilder.debugmenu.controller;

import java.util.HashMap;
import java.util.Map;

import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.api.editors.EditorInstanceWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.util.FXOMDocumentUtils;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class DebugMenuWindowController extends AbstractFxmlWindowController {

    @FXML
    TextArea textArea;

    private FXOMDocument fxomDocument;
    private String documentName;
    private boolean dirty = false;
    private final FxmlDocumentManager documentManager;

    public DebugMenuWindowController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            EditorInstanceWindow document,
            FxmlDocumentManager documentManager
            ) {
        super(sceneBuilderManager, iconSetting, DebugMenuWindowController.class.getResource("SourceWindow.fxml"), I18N.getBundle(),
                document); // NOI18N

        this.documentManager = documentManager;
    }

    private void setFxomDocument(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        this.fxomDocument = fxomDocument;
        this.documentName = FXOMDocumentUtils.makeTitle(fxomDocument);
        update();
    }
    @FXML
    private void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (textArea.getSelection().getLength() == 0) {
            content.put(DataFormat.PLAIN_TEXT, textArea.getText());
        } else {
            content.put(DataFormat.PLAIN_TEXT, textArea.getSelectedText());
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    @Override
    public void onCloseRequest() {
        getStage().close();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void openWindow() {
        super.openWindow();

        if (dirty) {
            update();
        }
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert textArea != null;

        documentManager.fxomDocument().subscribe(fx -> setFxomDocument(fx));
        documentManager.sceneGraphRevisionDidChange().subscribe(fx -> update());
    }

    private void updateTitle() {
        final String title = I18N.getString("sourceview.window.title", documentName);
        getStage().setTitle(title);
    }

    private void update() {
        assert fxomDocument != null;

        // No need to eat CPU if the skeleton window isn't opened
        if (getStage().isShowing()) {
            updateTitle();
            String fxml = fxomDocument.getFxmlText(false);
            textArea.setText(fxml);
            dirty = false;
        } else {
            dirty = true;
        }
    }
}
