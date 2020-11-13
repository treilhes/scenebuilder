/*
 * Copyright (c) 2016, Gluon and/or its affiliates.
 * Copyright (c) 2012, 2014, Oracle and/or its affiliates, 2016, Gluon.
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
package com.oracle.javafx.scenebuilder.app.registration;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationEmailPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationHashPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RegistrationOptInPreference;
import com.oracle.javafx.scenebuilder.app.tracking.Tracking;
import com.oracle.javafx.scenebuilder.kit.editor.panel.util.AbstractFxmlWindowController;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

/**
 *
 */
@Component
@Lazy
public class RegistrationWindowController extends AbstractFxmlWindowController {

    private static final Pattern emailPattern = Pattern.compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+"); //NOI18N

    @FXML
    private Label lbAlert;
    @FXML
    private TextField tfEmail;
    @FXML
    private CheckBox cbOptIn;

    final private Window owner;
    private final Tracking tracking;

	private final RegistrationHashPreference registrationHashPreference;

	private final RegistrationEmailPreference registrationEmailPreference;

	private final RegistrationOptInPreference registrationOptInPreference;
    
    public RegistrationWindowController(
    		@Autowired DocumentWindowController window,
    		@Autowired Tracking tracking,
    		@Autowired RegistrationHashPreference registrationHashPreference,
    		@Autowired RegistrationEmailPreference registrationEmailPreference,
    		@Autowired RegistrationOptInPreference registrationOptInPreference) {
        super(RegistrationWindowController.class.getResource("Registration.fxml"), //NOI18N
                I18N.getBundle(), window.getStage());
        this.owner = window.getStage();
        this.tracking = tracking;
        this.registrationHashPreference = registrationHashPreference;
        this.registrationEmailPreference = registrationEmailPreference;
        this.registrationOptInPreference = registrationOptInPreference;
    }

    @Override
    public void onCloseRequest(WindowEvent event) {
        cancelUserRegistration();

        event.consume();
    }
    
    @Override 
    public void onFocus() {}
    

    /*
     * AbstractWindowController
     */
    @Override
    protected void controllerDidCreateStage() {
        assert getRoot() != null;
        assert getRoot().getScene() != null;
        assert getRoot().getScene().getWindow() != null;

        getStage().setTitle(I18N.getString("registration.title"));

        if (this.owner == null) {
            // Window will be application modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Window will be window modal
            getStage().initOwner(this.owner);
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert lbAlert != null;
        assert tfEmail != null;
        assert cbOptIn != null;
    }

    private boolean isEmailAddressValid() {
        String email = tfEmail.getText();
        return email != null && !email.isEmpty() && emailPattern.matcher(email).matches();
    }

    @FXML
    public void cancelUserRegistration() {
        if (registrationHashPreference.getValue() == null) {
            String hash = getUniqueId();
            registrationHashPreference.setValue(hash).writeToJavaPreferences();
            tracking.sendTrackingInfo(Tracking.SCENEBUILDER_TYPE, hash, "", false, false);
        }

        closeWindow();
    }
    
    @FXML
    public void trackUserRegistration() {
        if (!isEmailAddressValid()) {
            lbAlert.setVisible(true);
            return;
        }

        boolean update = registrationHashPreference.getValue() != null;
        String hash = update ? registrationHashPreference.getValue() : getUniqueId();
        String email = tfEmail.getText();
        boolean optIn = cbOptIn.isSelected();
                
        // Update preferences
        registrationHashPreference.setValue(hash).writeToJavaPreferences();
        registrationEmailPreference.setValue(email).writeToJavaPreferences();
        registrationOptInPreference.setValue(optIn).writeToJavaPreferences();

        tracking.sendTrackingInfo(Tracking.SCENEBUILDER_TYPE, hash, email, optIn, update);

        closeWindow();
    }

    private String getUniqueId(){
        String uniqueId = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(address);
            if (ni != null) {
                byte[] macAddress = ni.getHardwareAddress();
                if (macAddress != null) {
                    uniqueId = computeHash(macAddress);
                }
            }
        } catch (UnknownHostException | SocketException e) {
        }

        if (uniqueId.isEmpty()) {
            uniqueId = UUID.randomUUID().toString();
        }

        return uniqueId;
    }

    private String computeHash(byte[] buffer) {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");

            messageDigest.reset();
            messageDigest.update(buffer);
            byte[] digest = messageDigest.digest();

            // Convert the byte to hex format
            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString((digest[i] & 0xff) + 0x100, 16).substring(1);
            }

            return hexStr;
        } catch (NoSuchAlgorithmException e) {
        }

        return "";
    }
}
