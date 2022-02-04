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
package com.oracle.javafx.scenebuilder.library.maven.repository;

import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.library.maven.preset.MavenPresets;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;

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
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class RepositoryManagerController extends AbstractFxmlWindowController {

    @FXML
    private ListView<RepositoryListItem> repositoryListView;

    private final Editor editorController;
    private final SceneBuilderWindow owner;

    private ObservableList<RepositoryListItem> listItems;

    private final MavenRepositoriesPreferences repositoryPreferences;
    private final MavenSetting mavenSetting;

    private final SceneBuilderBeanFactory context;

    //private final SceneBuilderManager sceneBuilderManager;

    protected RepositoryManagerController(
            Api api,
    		Editor editorController,
    		MavenSetting mavenSetting,
    		MavenRepositoriesPreferences repositoryPreferences,
    		SceneBuilderWindow owner) {
        super(api, RepositoryManagerController.class.getResource("RepositoryManager.fxml"), I18N.getBundle(), owner);
        this.context = api.getContext();
        this.owner = owner;
        this.editorController = editorController;
        this.repositoryPreferences = repositoryPreferences;
        this.mavenSetting = mavenSetting;

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
    public void onFocus() {}

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
        listItems.addAll(repositoryPreferences.getRepositories()
                .stream()
                .map(r -> new CustomRepositoryListItem(this, r))
                .collect(Collectors.toList()));

        // preset on top
        listItems.addAll(0, MavenPresets.getPresetRepositories()
            .stream()
            .map(r -> new RepositoryListItem(this, r))
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
        RepositoryDialogController repositoryDialogController = context.getBean(RepositoryDialogController.class,
                getApi(), editorController, mavenSetting, repositoryPreferences, this);
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

    public void edit(RepositoryListItem item) {
        repositoryDialog(item.getRepository());
    }

    public void delete(RepositoryListItem item) {
        // Remove repository
        logInfoMessage("log.user.repository.removed", item.getRepository().getId());
        repositoryPreferences.removeRecordRepository(item.getRepository().getId());
        loadRepositoryList();
    }

    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }

}