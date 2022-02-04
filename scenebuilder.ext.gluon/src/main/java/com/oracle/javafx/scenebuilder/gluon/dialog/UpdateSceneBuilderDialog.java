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

package com.oracle.javafx.scenebuilder.gluon.dialog;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.DocumentWindow;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.IgnoreVersionPreference;
import com.oracle.javafx.scenebuilder.gluon.preferences.global.ShowUpdateDialogDatePreference;
import com.oracle.javafx.scenebuilder.gluon.setting.VersionSetting;

import javafx.application.HostServices;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class UpdateSceneBuilderDialog extends Dialog<ButtonType> {
	
    private String latestVersion;
    
    public UpdateSceneBuilderDialog(
    		@Autowired HostServices hostServices,
    		@Autowired ShowUpdateDialogDatePreference showUpdateDialogDate,
    		@Autowired IgnoreVersionPreference ignoreVersion,
    		@Autowired VersionSetting versionSetting,
    		@Autowired IconSetting windowIconSetting,
    		@Autowired DocumentWindow owner
    		) {
    	
    	versionSetting.getLatestVersion((v) -> latestVersion = v);
    	String latestVersionTextString = versionSetting.getLatestVersionText();
    	String announcementURL = versionSetting.getLatestVersionAnnouncementURL();
        initOwner(owner.getStage());
        setTitle(I18N.getString("download.scene.builder.title"));
        Label header = new Label(I18N.getString("download.scene.builder.header.label"));
        Label currentVersionTextLabel = new Label(I18N.getString("download.scene.builder.current.version.label"));
        Label latestVersionTextLabel = new Label(I18N.getString("download.scene.builder.last.version.number.label"));
        Label currentVersionLabel = new Label(versionSetting.getSceneBuilderVersion());
        Label latestVersionLabel = new Label(latestVersion);
        GridPane gridPane = new GridPane();
        gridPane.add(currentVersionTextLabel, 0, 0);
        gridPane.add(currentVersionLabel, 1, 0);
        gridPane.add(latestVersionTextLabel, 0, 1);
        gridPane.add(latestVersionLabel, 1, 1);

        Label latestVersionText = new Label(latestVersionTextString);

        gridPane.getColumnConstraints().add(new ColumnConstraints(100));

        VBox contentContainer = new VBox();
        contentContainer.getChildren().addAll(header, gridPane);
        BorderPane mainContainer = new BorderPane();
        mainContainer.setCenter(contentContainer);
        ImageView imageView = new ImageView(UpdateSceneBuilderDialog.class.getResource("computerDownload.png").toExternalForm());
        mainContainer.setRight(imageView);
        mainContainer.setBottom(latestVersionText);

        getDialogPane().setContent(mainContainer);

        mainContainer.getStyleClass().add("main-container");
        contentContainer.getStyleClass().add("content-container");
        getDialogPane().getStyleClass().add("download_scenebuilder-dialog");
        header.getStyleClass().add("header");
        latestVersionText.getStyleClass().add("latest-version-text");

        ButtonType downloadButton = new ButtonType(I18N.getString("download.scene.builder.download.label"), ButtonBar.ButtonData.OK_DONE);
        ButtonType ignoreThisUpdate = new ButtonType(I18N.getString("download.scene.builder.ignore.label"));
        ButtonType remindLater = new ButtonType(I18N.getString("download.scene.builder.remind.later.label"), ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType learnMore = new ButtonType(I18N.getString("download.scene.builder.learn.mode.label"));
        getDialogPane().getButtonTypes().addAll(learnMore, downloadButton, ignoreThisUpdate, remindLater);

        getDialogPane().getStylesheets().add(UpdateSceneBuilderDialog.class.getResource("css/UpdateSceneBuilderDialog.css").toString());

        resultProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == downloadButton) {
                hostServices.showDocument(VersionSetting.DOWNLOAD_URL);
            } else if (newValue == remindLater) {
                showUpdateDialogDate.setValue(LocalDate.now().plusWeeks(1)).writeToJavaPreferences();;
            } else if (newValue == ignoreThisUpdate) {
                ignoreVersion.setValue(latestVersion).writeToJavaPreferences();;
            } else if (newValue == learnMore) {
                hostServices.showDocument(announcementURL);
            }
        });

        windowIconSetting.setWindowIcon((Stage)getDialogPane().getScene().getWindow());
    }
}
