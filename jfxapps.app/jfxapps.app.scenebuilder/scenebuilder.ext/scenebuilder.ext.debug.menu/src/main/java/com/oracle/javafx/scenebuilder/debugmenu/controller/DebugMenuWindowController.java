/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.util.FXOMDocumentUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 *
 */
@ApplicationInstanceSingleton
public class DebugMenuWindowController extends AbstractFxmlWindowController {

    @FXML
    TextArea textArea;

    private final DocumentManager documentManager;

    private FXOMDocument fxomDocument;
    private String documentName;
    private boolean dirty = false;


    public DebugMenuWindowController(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            InstanceWindow document,
            DocumentManager documentManager
            ) {
        super(i18n, sceneBuilderManager, iconSetting, DebugMenuWindowController.class.getResource("SourceWindow.fxml"), document); // NOI18N
        this.documentManager = documentManager;
    }

    private void setFxomDocument(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        this.fxomDocument = fxomDocument;
        this.documentName = FXOMDocumentUtils.makeTitle(getI18n(), fxomDocument);
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
        final String title = getI18n().getString("sourceview.window.title", documentName);
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
