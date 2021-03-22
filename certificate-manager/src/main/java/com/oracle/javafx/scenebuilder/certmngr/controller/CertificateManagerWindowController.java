/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.certmngr.controller;

import java.security.cert.X509Certificate;
import java.util.Stack;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.NetworkManager;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlWindowController;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

/**
 *
 */
@Component
//@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class CertificateManagerWindowController extends AbstractFxmlWindowController {

    @FXML
    private TextArea textArea;
    
    private final NetworkManager networkManager;
    
    private final Stack<X509Certificate[]> pendingCertificates = new Stack<>();
    
    private X509Certificate[] currentCertificates = null;

    public CertificateManagerWindowController(
            @Autowired Api api) {
        super(api, CertificateManagerWindowController.class.getResource("CertificateManagerWindow.fxml"), I18N.getBundle(),
                null); // NOI18N
        this.networkManager = api.getNetworkManager();
    }
    

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert textArea != null;

        networkManager.trustRequest().observeOn(JavaFxScheduler.platform()).subscribe(certificates -> {
            pendingCertificates.add(certificates);
            if (!this.getStage().isShowing()) {
                this.getStage(true).initOwner(getApi().getContext().getBean(DocumentWindow.class).getStage());
                this.openWindow();
            }
        });
    }
    
    private void update() {
        if (currentCertificates == null && pendingCertificates.isEmpty()) {
            onCloseRequest();
            return;
        }
        
        if (currentCertificates != null) {
            return;
        }
        
        currentCertificates = pendingCertificates.pop();
        
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < currentCertificates.length; i++) {
            X509Certificate certificate = currentCertificates[i];
            builder.append(certificate.getSubjectX500Principal().toString()).append("\n");
        }
        
        textArea.setText(builder.toString());
    }
    
    @FXML
    void cancelAction(ActionEvent event) {
        if (currentCertificates != null) {
            networkManager.untrusted().set(currentCertificates);
            currentCertificates = null;
        }
        update();
    }

    @FXML
    void trustAction(ActionEvent event) {
        if (currentCertificates != null) {
            networkManager.trustedPermanently().set(currentCertificates);
            currentCertificates = null;
        }
        update();
    }

    @FXML
    void trustNowAction(ActionEvent event) {
        if (currentCertificates != null) {
            networkManager.trustedTemporarily().set(currentCertificates);
            currentCertificates = null;
        }
        update();
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
        update();
    }

}
