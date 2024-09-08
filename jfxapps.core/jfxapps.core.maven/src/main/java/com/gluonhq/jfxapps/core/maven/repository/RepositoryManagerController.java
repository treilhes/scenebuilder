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
package com.gluonhq.jfxapps.core.maven.repository;

import java.util.stream.Collectors;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.maven.MavenClient;
import com.gluonhq.jfxapps.core.api.maven.RepositoryManager;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Modality;

/**
 * Controller for the JAR/FXML Library dialog.
 */
@Prototype
public class RepositoryManagerController extends AbstractFxmlWindowController implements RepositoryManager {

    private static final String I18N_LOG_USER_REPOSITORY_REMOVED = "log.user.repository.removed";

    private static final String I18N_REPOSITORY_MANAGER_TITLE = "repository.manager.title";

    @FXML
    private ListView<RepositoryListItem> repositoryListView;

    private final MessageLogger messageLogger;
    private final InstanceWindow owner;

    private ObservableList<RepositoryListItem> listItems;

    private final MavenClient mavenClient;
    private final AddEditRepositoryDialogController repositoryDialogController;
    private final JfxAppContext context;

    // @formatter:off
    protected RepositoryManagerController(
            I18N i18n,
            MavenClient mavenClient,
            ApplicationEvents sceneBuilderManager,
            IconSetting iconSetting,
            JfxAppContext context,
            MessageLogger messageLogger,
            AddEditRepositoryDialogController repositoryDialogController,
            InstanceWindow owner) {
     // @formatter:on
        super(i18n, sceneBuilderManager, iconSetting,
                RepositoryManagerController.class.getResource("RepositoryManager.fxml"), owner);
        this.context = context;
        this.owner = owner;
        this.messageLogger = messageLogger;
        this.mavenClient = mavenClient;
        this.repositoryDialogController = repositoryDialogController;

    }

    @Override
    protected void controllerDidCreateStage() {
        if (this.owner == null) {
            // Dialog will be appliation modal
            getStage().initModality(Modality.APPLICATION_MODAL);
        } else {
            // Dialog will be window modal
            getStage().initOwner(this.owner.getStage());
            getStage().initModality(Modality.WINDOW_MODAL);
        }
    }

    @Override
    public void onCloseRequest() {
        close();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(getI18n().getString(I18N_REPOSITORY_MANAGER_TITLE));
        loadRepositoryList();
    }

    private void loadRepositoryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        repositoryListView.setItems(listItems);
        repositoryListView.setCellFactory(param -> new RepositoryManagerListCell(getI18n()));

        // custom repositories
//        listItems.addAll(repositoryPreferences.getRepositories()
//                .stream()
//                .map(r -> new CustomRepositoryListItem(this, r))
//                .collect(Collectors.toList()));

        // preset on top
        listItems.addAll(0, mavenClient.repositories().stream().map(r -> new RepositoryListItem(this, r))
                .collect(Collectors.toList()));
    }

    @FXML
    private void close() {
        repositoryListView.getItems().clear();
        closeWindow();
    }

    @FXML
    private void addRepository() {
        repositoryDialog(null);
    }

    private void repositoryDialog(Repository repository) {
        repositoryDialogController.openWindow();
        repositoryDialogController.setRepository(repository);
        repositoryDialogController.getStage().showingProperty().addListener(new InvalidationListener() {
            @Override
            public void invalidated(Observable observable) {
                if (!repositoryDialogController.getStage().isShowing()) {
                    loadRepositoryList();
                    repositoryDialogController.getStage().showingProperty().removeListener(this);
                }
            }
        });
    }

    @Override
    public void edit(Repository repository) {
        repositoryDialog(repository);
    }

    @Override
    public void delete(Repository repository) {
        // Remove repository
        logInfoMessage(I18N_LOG_USER_REPOSITORY_REMOVED, repository.getId());
        mavenClient.remove(repository);
        loadRepositoryList();
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, getI18n().getBundle(), args);
    }

}