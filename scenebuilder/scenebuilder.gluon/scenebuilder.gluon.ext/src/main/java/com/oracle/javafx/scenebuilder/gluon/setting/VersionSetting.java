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
package com.oracle.javafx.scenebuilder.gluon.setting;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.function.Consumer;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.About;

@Component
@Lazy
public class VersionSetting {

    public static final String LATEST_VERSION_CHECK_URL = "http://download.gluonhq.com/scenebuilder/settings.properties";

    public static final String LATEST_VERSION_NUMBER_PROPERTY = "latestversion";

    public static final String LATEST_VERSION_INFORMATION_URL = "http://download.gluonhq.com/scenebuilder/version.json";

    public static final String DOWNLOAD_URL = "https://gluonhq.com/products/scene-builder/";

    private String latestVersion;

    private String latestVersionText;
    private String latestVersionAnnouncementURL;

    private JsonReaderFactory readerFactory = Json.createReaderFactory(null);
    private final About about;

    public VersionSetting(@Autowired @Lazy About about) {
        this.about = about;
    }

    public String getSceneBuilderVersion() {
        return about.getBuildVersion();
    }

    public boolean isCurrentVersionLowerThan(String version) {
        String[] versionNumbers = version.split("\\.");
        String[] currentVersionNumbers = getSceneBuilderVersion().split("\\.");
        for (int i = 0; i < versionNumbers.length; ++i) {
            int number = Integer.parseInt(versionNumbers[i]);
            int currentVersionNumber = Integer.parseInt(currentVersionNumbers[i]);
            if (number > currentVersionNumber) {
                return true;
            } else if (number < currentVersionNumber) {
                return false;
            }
        }
        return false;
    }

    public void getLatestVersion(Consumer<String> consumer) {

        if (latestVersion == null) {
            new Thread(() -> {
                Properties prop = new Properties();
                String onlineVersionNumber = null;

                URL url = null;
                try {
                    url = new URL(LATEST_VERSION_CHECK_URL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try (InputStream inputStream = url.openStream()) {
                    prop.load(inputStream);
                    onlineVersionNumber = prop.getProperty(LATEST_VERSION_NUMBER_PROPERTY);

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                latestVersion = onlineVersionNumber;
                consumer.accept(latestVersion);
            }, "GetLatestVersion").start();
        } else {
            consumer.accept(latestVersion);
        }
    }

    public String getLatestVersionText() {
        if (latestVersionText == null) {
            updateLatestVersionInfo();
        }
        return latestVersionText;
    }

    private void updateLatestVersionInfo() {
        try {
            URL url = new URL(LATEST_VERSION_INFORMATION_URL);

            try (JsonReader reader = readerFactory.createReader(new InputStreamReader(url.openStream()))) {
                JsonObject object = reader.readObject();
                JsonObject announcementObject = object.getJsonObject("announcement");
                latestVersionText = announcementObject.getString("text");
                latestVersionAnnouncementURL = announcementObject.getString("url");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public String getLatestVersionAnnouncementURL() {
        if (latestVersionAnnouncementURL == null) {
            updateLatestVersionInfo();
        }
        return latestVersionAnnouncementURL;
    }

}
