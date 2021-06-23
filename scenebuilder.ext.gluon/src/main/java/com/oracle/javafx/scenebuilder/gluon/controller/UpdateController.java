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

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.Main;
import com.oracle.javafx.scenebuilder.api.alert.SBAlert;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.gluon.dialog.UpdateSceneBuilderDialog;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.IgnoreVersionPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.ShowUpdateDialogDatePreference;
import com.oracle.javafx.scenebuilder.gluon.setting.VersionSetting;

import javafx.application.Platform;

@Component
@Lazy
public class UpdateController {
    
    private final Main main;
    private final ApplicationContext context;
    private final VersionSetting versionSetting;
    private final IgnoreVersionPreference ignoreVersionPreference;
    private final ShowUpdateDialogDatePreference showUpdateDialogDatePreference;
    
    public UpdateController(
            @Autowired ApplicationContext context, 
            @Autowired Main main, 
            @Autowired IgnoreVersionPreference ignoreVersionPreference,
            @Autowired ShowUpdateDialogDatePreference showUpdateDialogDatePreference,
            @Autowired VersionSetting versionSetting) {
        super();
        this.context = context;
        this.main = main;
        this.ignoreVersionPreference = ignoreVersionPreference;
        this.showUpdateDialogDatePreference = showUpdateDialogDatePreference;
        this.versionSetting = versionSetting;

    }
    
    public void checkUpdates() {
        versionSetting.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                Platform.runLater(() -> {
                    SBAlert alert = new SBAlert(javafx.scene.control.Alert.AlertType.ERROR, 
                            main.getFrontDocumentWindow().getDocumentWindow().getStage());
                    alert.setTitle(I18N.getString("check_for_updates.alert.error.title"));
                    alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                    alert.setContentText(I18N.getString("check_for_updates.alert.error.message"));
                    alert.showAndWait();
                });
            }
            try {
                if (versionSetting.isCurrentVersionLowerThan(latestVersion)) {
                //if (true) {
                    Platform.runLater(() -> {
                        UpdateSceneBuilderDialog dialog = context.getBean(UpdateSceneBuilderDialog.class);
                        dialog.showAndWait();
                    });
                } else {
                    SBAlert alert = new SBAlert(javafx.scene.control.Alert.AlertType.INFORMATION, 
                            main.getFrontDocumentWindow().getDocumentWindow().getStage());
                    alert.setTitle(I18N.getString("check_for_updates.alert.up_to_date.title"));
                    alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
                    alert.setContentText(I18N.getString("check_for_updates.alert.up_to_date.message"));
                    alert.showAndWait();
                }
            } catch (NumberFormatException ex) {
                Platform.runLater(() -> showVersionNumberFormatError(context.getBean(DocumentWindow.class)));
            }
        });
    }

    public void showUpdateDialogIfRequired(DocumentWindow dwc, Runnable runAfterUpdateDialog) {
        versionSetting.getLatestVersion(latestVersion -> {
            if (latestVersion == null) {
                // This can be because the url was not reachable so we don't show the update dialog.
                return;
            }
            try {
                boolean showUpdateDialog = true;
                if (versionSetting.isCurrentVersionLowerThan(latestVersion)) {
                    if (isVersionToBeIgnored(latestVersion)) {
                        showUpdateDialog = false;
                    }

                    if (!isUpdateDialogDateReached()) {
                        showUpdateDialog = false;
                    }
                } else {
                    showUpdateDialog = false;
                }
                
                if (showUpdateDialog) {
                    Platform.runLater(() -> {
                        UpdateSceneBuilderDialog dialog = context.getBean(UpdateSceneBuilderDialog.class);
                        dialog.setOnHidden(event -> runAfterUpdateDialog.run());
                        dialog.showAndWait();
                    });
                } else {
                    runAfterUpdateDialog.run();
                }
            } catch (NumberFormatException ex) {
                Platform.runLater(() -> showVersionNumberFormatError(dwc));
            }
        });
    }
    
    private boolean isVersionToBeIgnored(String latestVersion) {
        String ignoreVersion = getIgnoreVersion();
        return latestVersion.equals(ignoreVersion);
    }

    private boolean isUpdateDialogDateReached() {
        LocalDate dialogDate = getShowUpdateDialogDate();
        if (dialogDate == null) {
            return true;
        } else if (dialogDate.isBefore(LocalDate.now())) {
            return true;
        } else {
            return false;
        }
    }
    
    private void showVersionNumberFormatError(DocumentWindow dwc) {
        SBAlert alert = new SBAlert(javafx.scene.control.Alert.AlertType.ERROR, dwc.getStage());
        // The version number format is not supported and this is most probably only happening
        // in development so we don't localize the strings
        alert.setTitle("Error");
        alert.setHeaderText(I18N.getString("check_for_updates.alert.headertext"));
        alert.setContentText("Version number format not supported. Maybe using SNAPSHOT or RC versions.");
        alert.showAndWait();
    }
    
    public void setIgnoreVersion(String ignoreVersion) {
        this.ignoreVersionPreference.setValue(ignoreVersion).writeToJavaPreferences();
    }

    public String getIgnoreVersion() {
        return ignoreVersionPreference.getValue();
    }
    
    public void setShowUpdateDialogAfter(LocalDate showUpdateDialogDate) {
        this.showUpdateDialogDatePreference.setValue(showUpdateDialogDate).writeToJavaPreferences();
    }

    public LocalDate getShowUpdateDialogDate() {
        return showUpdateDialogDatePreference.getValue();
    }

}
