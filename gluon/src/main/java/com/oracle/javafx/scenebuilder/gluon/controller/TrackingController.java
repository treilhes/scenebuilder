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
package com.oracle.javafx.scenebuilder.gluon.controller;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.gluon.preferences.global.LastSentTrackingInfoDatePreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationEmailPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationHashPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.RegistrationOptInPreference;
import com.oracle.javafx.scenebuilder.gluon.setting.VersionSetting;

@Component
@Lazy
public class TrackingController {
    public static final String SCENEBUILDER_TYPE = "scenebuilder";
    public static final String SCENEBUILDER_USAGE_TYPE = "scenebuilder-usage";

    private final RegistrationEmailPreference registrationEmailPreference;
    private final RegistrationHashPreference registrationHashPreference;
    private final RegistrationOptInPreference registrationOptInPreference;
    private final LastSentTrackingInfoDatePreference lastSentTrackingInfoDatePreference;
    
    private VersionSetting versionSetting;
    
    public TrackingController(
            @Autowired VersionSetting versionSetting,
            @Autowired LastSentTrackingInfoDatePreference lastSentTrackingInfoDatePreference,
            @Autowired RegistrationEmailPreference registrationEmailPreference,
            @Autowired RegistrationHashPreference registrationHashPreference,
            @Autowired RegistrationOptInPreference registrationOptInPreference
            
            ) {
        this.versionSetting = versionSetting;
        this.lastSentTrackingInfoDatePreference = lastSentTrackingInfoDatePreference;
        this.registrationEmailPreference = registrationEmailPreference;
        this.registrationHashPreference = registrationHashPreference;
        this.registrationOptInPreference = registrationOptInPreference;
        
    }

    public void sendTrackingStartupInfo() {
        boolean sendTrackingInfo = shouldSendTrackingInfo();

        if (sendTrackingInfo) {
            boolean update = false;
            String hash = getRegistrationHash();
            String email = getRegistrationEmail();
            boolean optIn = isRegistrationOptIn();

            sendTrackingInfo(TrackingController.SCENEBUILDER_USAGE_TYPE, hash, email, optIn, update);
        }
    }

    private boolean shouldSendTrackingInfo() {
        LocalDate date = getLastSentTrackingInfoDate();
        boolean sendTrackingInfo = true;
        LocalDate now = LocalDate.now();

        if (date != null) {
            sendTrackingInfo = date.plusWeeks(1).isBefore(now);
            if (sendTrackingInfo) {
                setLastSentTrackingInfoDate(now);
            }
        } else {
            setLastSentTrackingInfoDate(now);
        }
        return sendTrackingInfo;
    }
    

	public void sendTrackingInfo(String type, String hash, String email, boolean optIn, boolean update) {
        new Thread(() -> {
            try {
                String java = System.getProperty("java.version");
                String os = System.getProperty("os.arch") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version");
                String urlParameters = "email=" + (email == null ? "" : URLEncoder.encode(email, "UTF-8"))
                        + "&subscribe=" + optIn
                        + "&os=" + URLEncoder.encode(os, "UTF-8")
                        + "&java=" + URLEncoder.encode(java, "UTF-8")
                        + "&type=" + type
                        + "&id=" + hash
                        + "&version=" + versionSetting.getSceneBuilderVersion()
                        + (update ? "&update=true" : "");

                URL url = new URL("http://usage.gluonhq.com/ul/log?" + urlParameters);

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                conn.setRequestMethod("GET");
                conn.setUseCaches(false);
                conn.connect();
                try (DataInputStream in = new DataInputStream(conn.getInputStream())) {
                    while (in.read() > -1) {
                    }
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }, "UserRegistrationThread").start();
    }
	
    public String getRegistrationHash() {
        return registrationHashPreference.getValue();
    }

    public void setRegistrationHash(String registrationHash) {
        this.registrationHashPreference.setValue(registrationHash);
    }

    public String getRegistrationEmail() {
        return registrationEmailPreference.getValue();
    }

    public void setRegistrationEmail(String registrationEmail) {
        this.registrationEmailPreference.setValue(registrationEmail);
    }

    public boolean isRegistrationOptIn() {
        return registrationOptInPreference.getValue();
    }

    public void setRegistrationOptIn(boolean registrationOptIn) {
        this.registrationOptInPreference.setValue(registrationOptIn);
    }
    
    public LocalDate getLastSentTrackingInfoDate() {
        return lastSentTrackingInfoDatePreference.getValue();
    }

    public void setLastSentTrackingInfoDate(LocalDate date) {
        lastSentTrackingInfoDatePreference.setValue(date).writeToJavaPreferences();
    }

}
