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

package com.oracle.javafx.scenebuilder.welcome.controller;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.oracle.javafx.scenebuilder.api.template.Template;
import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.treilhes.jfxplace.core.api.WelcomeDialog;
import com.treilhes.jfxplace.core.api.application.Application;
import com.treilhes.jfxplace.core.api.application.ApplicationActionFactory;
import com.treilhes.jfxplace.core.api.application.InstancesManager;
import com.treilhes.jfxplace.core.api.fs.RecentItems;
import com.treilhes.jfxplace.core.api.ui.controller.AbstractFxmlApplicationWindowController;
import com.treilhes.jfxplace.core.api.ui.controller.misc.IconSetting;
import com.treilhes.jfxplace.fxom.api.document.DocumentActionFactory;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

@ApplicationSingleton
public class WelcomeDialogWindowController extends AbstractFxmlApplicationWindowController implements WelcomeDialog {

    public static final Logger logger = LoggerFactory.getLogger(WelcomeDialogWindowController.class);

    @FXML
    private VBox recentDocuments;

    @FXML
    private Button emptyApp;

    @FXML
    private ScrollPane scrollPane;

    private final InstancesManager instancesManager;
    private final DocumentActionFactory documentActionFactory;
    private final ApplicationActionFactory applicationActionFactory;

	private final RecentItems recentItems;

	private final TemplatesSelectionController templateSelection;
	private final TemplateLoader templateLoader;

    private final IconSetting windowIconSetting;


    //@formatter:off
    private WelcomeDialogWindowController(
            Application application,
            InstancesManager instancesManager,
            DocumentActionFactory documentActionFactory,
            ApplicationActionFactory applicationActionFactory,
            IconSetting windowIconSetting,
            RecentItems recentItems,
            TemplateLoader templateLoader,
            TemplatesSelectionController templateSelection) {
        //@formatter:on
        super(application, WelcomeDialogWindowController.class.getResource("WelcomeWindow.fxml"));

        this.instancesManager = instancesManager;
        this.documentActionFactory = documentActionFactory;
        this.applicationActionFactory = applicationActionFactory;
        this.recentItems = recentItems;
        this.templateSelection = templateSelection;
        this.windowIconSetting = windowIconSetting;
        this.templateLoader = templateLoader;
    }


    @Override
    public void onCloseRequest() {
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
        getStage().setTitle(getI18n().getString("welcome.title"));
        getStage().initModality(Modality.APPLICATION_MODAL);
    }

    @Override
    public void controllerDidLoadFxml() {
        super.controllerDidLoadFxml();
        assert recentDocuments != null;

        List<String> items = recentItems.getRecentItems();

        if (items.size() == 0) {
            Label noRecentItems = new Label(getI18n().getString("welcome.recent.items.no.recent.items"));
            noRecentItems.getStyleClass().add("no-recent-items-label");
            recentDocuments.getChildren().add(noRecentItems);
        }
        for (int row = 0; row < items.size(); ++row) {
            if (items.size() < row + 1) {
                break;
            }

            String recentItem = items.get(row);
            File recentItemFile = new File(items.get(row));
            String recentItemTitle = recentItemFile.getName();
            Button recentDocument = new Button(recentItemTitle);
            recentDocument.getStyleClass().add("recent-document");
            recentDocument.setMaxWidth(Double.MAX_VALUE);
            recentDocument.setAlignment(Pos.BASELINE_LEFT);
            recentDocuments.getChildren().add(recentDocument);

            recentDocument.setOnAction(event -> fireOpenRecentProject(event, recentItem));
            recentDocument.setTooltip(new Tooltip(recentItem));
        }

    }

    @Override
    public void openWindow() {
        super.openWindow();
        templateSelection.clearFromParent();
        templateSelection.setOnTemplateChosen(this::loadTemplate);
        scrollPane.setContent(templateSelection.getRoot());
    }

    private void fireOpenRecentProject(ActionEvent event, String projectPath) {
        try {
            URL url = new File(projectPath).toURI().toURL();
            //instancesManager.open(List.of(url));
            var action = applicationActionFactory.lookupUnusedInstance(url, (instance) -> {
                documentActionFactory.loadURL(url, true).perform();
            });
            action.perform();

        } catch (MalformedURLException e) {
            logger.error("Unable to open recent project", e);
        }
        getStage().hide();
    }

    @FXML
    private void openDocument() {
        //instancesManager.open(List.of());
        applicationActionFactory.newInstance((instance) -> {
            documentActionFactory.loadBlank().perform();
        }).perform();

        getStage().hide();
    }

    @FXML
    private void openEmpty() {
        getStage().hide();
        //templateController.loadTemplateInCurrentWindow(null);
        //instancesManager.open(List.of());
        applicationActionFactory.newInstance((instance) -> {
            documentActionFactory.loadBlank().perform();
        }).perform();
    }

    private void loadTemplate(Template template) {
        getStage().hide();
        templateLoader.loadTemplate(template);
    }
}

