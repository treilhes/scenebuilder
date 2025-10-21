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

import java.net.URL;

import com.gluonhq.jfxapps.boot.api.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractInstanceUiController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow.ButtonID;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;

/**
 *
 *
 */
@Prototype("alertDialog")
public class AlertDialog extends AbstractInstanceUiController implements Alert {

    private final ModalWindow modalWindow;
    @FXML
    protected Label messageLabel;
    @FXML
    protected Label detailsLabel;

    private Runnable actionRunnable;

    //@formatter:off
    public AlertDialog(
            JfxAppPlatform jfxAppPlatform,
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents instanceEvents,
            IconSetting iconSetting,
            ModalWindow modalWindow) {
        //@formatter:on
        super(i18n, applicationEvents, instanceEvents, AlertDialog.class.getResource("AlertDialog.fxml"));
        this.modalWindow = modalWindow;
    }

    @Override
    public void controllerDidLoadFxml() {
        //modalWindow..setResizable(false);

        // Sanity checks
        assert messageLabel != null;
        assert detailsLabel != null;

        // Remove label text (inserted for design purpose)
        messageLabel.setText(null);
        detailsLabel.setText(null);

        modalWindow.setImageViewImage(getDialogImage());
        modalWindow.setImageViewVisible(true);

        modalWindow.setContent(this.getRoot());
        modalWindow.setOnOkButtonPressed(this::okButtonPressed);
        modalWindow.setOnCancelButtonPressed(this::cancelButtonPressed);
        modalWindow.setOnActionButtonPressed(this::actionButtonPressed);
    }

    public String getMessage() {
        return getMessageLabel().getText();
    }

    @Override
    public void setMessage(String message) {
        getMessageLabel().setText(message);
    }

    public String getDetails() {
        return getDetailsLabel().getText();
    }

    @Override
    public void setDetails(String details) {
        getDetailsLabel().setText(details);
    }

    public void setActionRunnable(Runnable runnable) {
        this.actionRunnable = runnable;
    }


    @Override
    public ButtonID showAndWait() {
        return modalWindow.showAndWait();
    }

    @Override
    public void show() {
        modalWindow.show();
    }

    @Override
    public void close() {
        modalWindow.close();
    }

    @Override
    public ModalWindow getModalWindow() {
        return modalWindow;
    }

    /*
     * Private
     */

    private void okButtonPressed(ActionEvent e) {
        modalWindow.close();
    }

    private void cancelButtonPressed(ActionEvent e) {
        modalWindow.close();
    }

    private void actionButtonPressed(ActionEvent e) {
        if (actionRunnable != null) {
            actionRunnable.run();
        } else {
            modalWindow.close();
        }
    }

    private Label getMessageLabel() {
        return messageLabel;
    }

    private Label getDetailsLabel() {
        return detailsLabel;
    }

    private static Image dialogImage;

    private static synchronized Image getDialogImage() {
        if (dialogImage == null) {
            final URL dialogImageURL = AlertDialog.class.getResource("alert-question-mark.png");
            dialogImage = new Image(dialogImageURL.toExternalForm());
        }
        return dialogImage;
    }

}
