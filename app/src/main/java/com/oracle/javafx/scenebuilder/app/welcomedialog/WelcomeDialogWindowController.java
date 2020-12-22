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

package com.oracle.javafx.scenebuilder.app.welcomedialog;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.app.DocumentWindowController;
import com.oracle.javafx.scenebuilder.app.MainController;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsPreference;
import com.oracle.javafx.scenebuilder.app.preferences.global.RecentItemsSizePreference;
import com.oracle.javafx.scenebuilder.app.settings.WindowIconSetting;
import com.oracle.javafx.scenebuilder.kit.template.Template;
import com.oracle.javafx.scenebuilder.kit.template.TemplatesBaseWindowController;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.WindowEvent;

@Component
@Lazy
public class WelcomeDialogWindowController extends TemplatesBaseWindowController {

    @FXML
    private VBox recentDocuments;

    @FXML
    private Button emptyApp;

    private final MainController sceneBuilderApp;

	private final RecentItemsPreference recentItemsPreference;

	private final RecentItemsSizePreference recentItemsSizePreference;

    private final WindowIconSetting windowIconSetting;

    private WelcomeDialogWindowController(
            @Autowired SceneBuilderManager sceneBuilderManager,
    		@Autowired MainController sceneBuilderApp,
    		@Autowired WindowIconSetting windowIconSetting,
    		@Autowired RecentItemsPreference recentItemsPreference,
    		@Autowired RecentItemsSizePreference recentItemsSizePreference) {
        super(sceneBuilderManager, WelcomeDialogWindowController.class.getResource("WelcomeWindow.fxml"), //NOI18N
                I18N.getBundle(),
                null); // We want it to be a top level window so we're setting the owner to null.

        this.sceneBuilderApp = sceneBuilderApp;
        this.recentItemsPreference = recentItemsPreference;
        this.recentItemsSizePreference = recentItemsSizePreference;
        this.windowIconSetting = windowIconSetting;
    }


    @Override
    public void onCloseRequest(WindowEvent event) {
        getStage().hide();
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

        windowIconSetting.setWindowIcon(this.getStage());
        getStage().setTitle(I18N.getString("welcome.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert recentDocuments != null;

        List<String> recentItems = recentItemsPreference.getValue();
        int recentItemsSize = recentItemsSizePreference.getValue();

        if (recentItems.size() == 0) {
            Label noRecentItems = new Label(I18N.getString("welcome.recent.items.no.recent.items"));
            noRecentItems.getStyleClass().add("no-recent-items-label");
            recentDocuments.getChildren().add(noRecentItems);
        }
        for (int row = 0; row < recentItemsSize; ++row) {
            if (recentItems.size() < row + 1) {
                break;
            }

            String recentItem = recentItems.get(row);
            File recentItemFile = new File(recentItems.get(row));
            String recentItemTitle = recentItemFile.getName();
            Button recentDocument = new Button(recentItemTitle);
            recentDocument.getStyleClass().add("recent-document");
            recentDocument.setMaxWidth(Double.MAX_VALUE);
            recentDocument.setAlignment(Pos.BASELINE_LEFT);
            recentDocuments.getChildren().add(recentDocument);

            recentDocument.setOnAction(event -> fireOpenRecentProject(event, recentItem));
            recentDocument.setTooltip(new Tooltip(recentItem));
        }

        emptyApp.setUserData(Template.EMPTY_APP);

        setOnTemplateChosen(sceneBuilderApp::performNewTemplate);
        setupTemplateButtonHandlers();
    }

//    public static WelcomeDialogWindowController getInstance() {
//        if (instance == null){
//            instance = new WelcomeDialogWindowController();
//            windowIconSetting.setWindowIcon((Stage)instance.getStage());
//        }
//        return instance;
//    }

    private void fireOpenRecentProject(ActionEvent event, String projectPath) {
        sceneBuilderApp.handleOpenFilesAction(Arrays.asList(projectPath));
        getStage().hide();
    }

    @FXML
    private void openDocument() {
        // Right now there is only one window open by default
        DocumentWindowController documentWC = sceneBuilderApp.getDocumentWindowControllers().get(0);
        sceneBuilderApp.performControlAction(MainController.ApplicationControlAction.OPEN_FILE, documentWC);
        getStage().hide();
    }
}

