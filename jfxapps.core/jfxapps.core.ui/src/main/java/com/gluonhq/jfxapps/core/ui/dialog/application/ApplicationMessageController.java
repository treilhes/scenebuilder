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
package com.gluonhq.jfxapps.core.ui.dialog.application;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import org.springframework.lang.NonNull;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationPrototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;

@ApplicationPrototype
public class ApplicationMessageController implements FxmlController{

    private Parent root;
    private final URL fxmlURL;
    private final I18N i18n;
    @FXML
    private Label detailsLabel;

    @FXML
    private Label errorLabel;
    @FXML
    private Label warningLabel;
    @FXML
    private Label infoLabel;

    @FXML
    private Label messageLabel;

    private ApplicationMessage message;


    public ApplicationMessageController(I18N i18n) {
        super();
        this.i18n = i18n;
        this.fxmlURL = getClass().getResource("ApplicationMessage.fxml");
    }

    public void setMessage(ApplicationMessage message) {
        Objects.requireNonNull(message, "message must not be null");

        this.message = message;

        messageLabel.setText(message.message());
        detailsLabel.setText(message.detail());

        errorLabel.setVisible(message.level() == ApplicationMessage.Level.ERROR);
        errorLabel.setManaged(message.level() == ApplicationMessage.Level.ERROR);
        warningLabel.setVisible(message.level() == ApplicationMessage.Level.WARNING);
        warningLabel.setManaged(message.level() == ApplicationMessage.Level.WARNING);
        infoLabel.setVisible(message.level() == ApplicationMessage.Level.INFO);
        infoLabel.setManaged(message.level() == ApplicationMessage.Level.INFO);

    }

    @Override
    public Parent getRoot() {
        return root;
    }

    @Override
    public void setRoot(Parent root) {
        this.root = root;
    }

    @Override
    @NonNull
    public URL getFxmlURL() {
        return fxmlURL;
    }

    @Override
    public ResourceBundle getResources() {
        return i18n.getBundle();
    }

    @Override
    public void controllerDidLoadFxml() {
        // TODO Auto-generated method stub

    }
}
