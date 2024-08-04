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
package com.gluonhq.jfxapps.core.ui.dialog;

import java.net.URL;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.dialog.AbstractModalDialog;
import com.gluonhq.jfxapps.core.api.ui.dialog.Alert;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.Window;

/**
 *
 *
 */
@Prototype
public class AlertDialog extends AbstractModalDialog  implements Alert {

    @FXML protected Label messageLabel;
    @FXML protected Label detailsLabel;

    private Runnable actionRunnable;

    public AlertDialog(
            I18N i18n,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            Window owner) {
        super(i18n, sceneBuilderManager, iconSetting, AlertDialog.class.getResource("AlertDialog.fxml"), owner);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        getStage().setResizable(false);
        setImageViewVisible(true);
        setImageViewImage(getDialogImage());
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

    /*
     * AbstractModalDialog
     */

    @Override
    public void controllerDidLoadContentFxml() {

        // Sanity checks
        assert messageLabel != null;
        assert detailsLabel != null;

        // Remove label text (inserted for design purpose)
        messageLabel.setText(null);
        detailsLabel.setText(null);
    }

    @Override
    public void okButtonPressed(ActionEvent e) {
        getStage().close();
    }

    @Override
    public void cancelButtonPressed(ActionEvent e) {
        getStage().close();
    }

    @Override
    public void actionButtonPressed(ActionEvent e) {
        if (actionRunnable != null) {
            actionRunnable.run();
        } else {
            getStage().close();
        }
    }



    /*
     * Private
     */

    private Label getMessageLabel() {
        getContentRoot(); // Force content fxml loading
        return messageLabel;
    }


    private Label getDetailsLabel() {
        getContentRoot(); // Force content fxml loading
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
