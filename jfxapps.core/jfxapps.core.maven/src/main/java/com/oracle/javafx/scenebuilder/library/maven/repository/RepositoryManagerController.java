/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.library.maven.repository;

import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.maven.MavenClient;
import com.oracle.javafx.scenebuilder.api.maven.RepositoryManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.annotation.Prototype;
import com.oracle.javafx.scenebuilder.maven.client.api.Repository;

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

    @FXML
    private ListView<RepositoryListItem> repositoryListView;

    private final MessageLogger messageLogger;
    private final SceneBuilderWindow owner;

    private ObservableList<RepositoryListItem> listItems;

    private final MavenClient mavenClient;
    private final AddEditRepositoryDialogController repositoryDialogController;
    private final SbContext context;


    // @formatter:off
    protected RepositoryManagerController(
            MavenClient mavenClient,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            SbContext context,
            MessageLogger messageLogger,
            AddEditRepositoryDialogController repositoryDialogController,
            SceneBuilderWindow owner) {
     // @formatter:on
        super(sceneBuilderManager, iconSetting, RepositoryManagerController.class.getResource("RepositoryManager.fxml"),
                I18N.getBundle(), owner);
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
        super.getStage().setTitle(I18N.getString("repository.manager.title"));
        loadRepositoryList();
    }

    private void loadRepositoryList() {
        if (listItems == null) {
            listItems = FXCollections.observableArrayList();
        }
        listItems.clear();
        repositoryListView.setItems(listItems);
        repositoryListView.setCellFactory(param -> new RepositoryManagerListCell());

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
        logInfoMessage("log.user.repository.removed", repository.getId());
        mavenClient.remove(repository);
        loadRepositoryList();
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, I18N.getBundle(), args);
    }

}