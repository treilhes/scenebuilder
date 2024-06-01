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
package com.gluonhq.jfxapps.app.manager.main.ui.cmp;

import com.gluonhq.jfxapps.app.manager.main.api.ApplicationCard;
import com.gluonhq.jfxapps.app.manager.main.api.ExtensionCard;
import com.gluonhq.jfxapps.app.manager.main.api.MainContent;
import com.gluonhq.jfxapps.app.manager.main.api.Model;
import com.gluonhq.jfxapps.app.manager.model.AppModel;
import com.gluonhq.jfxapps.app.manager.model.Application;
import com.gluonhq.jfxapps.app.manager.model.Extension;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlPanelController;

import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

@Prototype
public class MainContentController extends AbstractFxmlPanelController implements MainContent{

    @FXML
    private ListView<Application> applicationListView;

    @FXML
    private ListView<Extension> coreExtensionListView;


    private ApplicationCard.Factory applicationCardFactory;

    private ExtensionCard.Factory extensionCardFactory;

    private Model model;

    public MainContentController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            ApplicationCard.Factory applicationCardFactory,
            ExtensionCard.Factory extensionCardFactory,
            Model model
            ) {
        super(scenebuilderManager, documentManager, MainContentController.class.getResource("MainContent.fxml"), I18N.getBundle());

        this.applicationCardFactory = applicationCardFactory;
        this.extensionCardFactory = extensionCardFactory;
        this.model = model;
    }

    @Override
    public void controllerDidLoadFxml() {
        AppModel appModel = model.getModel();
//        setupExtensionListView();
//        setupApplicationListView();
//        Bindings.bindContentBidirectional(coreExtensionListView.getItems(), appModel.getApi().getExtensions());
//        Bindings.bindContentBidirectional(applicationListView.getItems(), appModel.getApplications());
    }

    private void setupApplicationListView() {
        applicationListView.setCellFactory(lv -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(Application item, boolean empty) {
                    super.updateItem(item, empty);
                    getChildren().clear();
                    if (!empty) {
                        ApplicationCard card = applicationCardFactory.get(item);
                        getChildren().add(card.getRoot());
                    }
                }
            };
        });
    }

    private void setupExtensionListView() {
        coreExtensionListView.setCellFactory(lv -> {
            return new ListCell<>() {

                @Override
                protected void updateItem(Extension item, boolean empty) {
                    super.updateItem(item, empty);
                    getChildren().clear();
                    if (!empty) {
                        ExtensionCard card = extensionCardFactory.get(item);
                        getChildren().add(card.getRoot());
                    }
                }
            };
        });
    }
}
