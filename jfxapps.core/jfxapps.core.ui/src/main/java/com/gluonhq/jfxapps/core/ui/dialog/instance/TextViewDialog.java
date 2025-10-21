/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.core.ui.dialog.instance;

import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractInstanceUiController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 * A modal dialog which displays a piece of text and provides a Close button
 * and a Copy button.
 *
 *
 */
@Prototype
public class TextViewDialog extends AbstractInstanceUiController {

    private final ModalWindow modalWindow;

    @FXML
    private TextArea textArea;

    /*
     * Protected
     */

    protected TextViewDialog(
            JfxAppPlatform jfxAppPlatform,
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents instanceEvents,
            IconSetting iconSetting,
            ModalWindow modalWindow) {
        super(i18n, applicationEvents, instanceEvents, TextViewDialog.class.getResource("TextViewDialog.fxml"));
        this.modalWindow = modalWindow;
    }

    public void setText(String text) {
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }

    @Override
    public void controllerDidLoadFxml() {
        //modalWindow..setResizable(false);

        // Sanity checks
        assert textArea != null;

        modalWindow.setContent(this.getRoot());

        modalWindow.setOKButtonVisible(false);
        modalWindow.setActionButtonVisible(true);
        modalWindow.setCancelButtonTitle(getI18n().getString("label.close"));
        modalWindow.setActionButtonTitle(getI18n().getString("label.copy"));

        modalWindow.setOnOkButtonPressed(this::okButtonPressed);
        modalWindow.setOnCancelButtonPressed(this::cancelButtonPressed);
        modalWindow.setOnActionButtonPressed(this::actionButtonPressed);
    }

    private void okButtonPressed(ActionEvent e) {
        // Should not be called because ok button is hidden
        throw new IllegalStateException();
    }

    private void cancelButtonPressed(ActionEvent e) {
        modalWindow.close();
    }

    private void actionButtonPressed(ActionEvent e) {
        final Map<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT, getText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void showAndWait() {
        modalWindow.showAndWait();
    }

    public void show() {
        modalWindow.show();
    }

    public ModalWindow getModalWindow() {
        return modalWindow;
    }

    public void close() {
        modalWindow.close();
    }

}
