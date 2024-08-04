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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.boot.maven.client.api.Repository;
import com.gluonhq.jfxapps.boot.maven.client.api.RepositoryType;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.maven.MavenClient;
import com.gluonhq.jfxapps.core.api.maven.RepositoryTypeProvider;
import com.gluonhq.jfxapps.core.api.settings.MavenSetting;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.InstanceWindow;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.maven.preferences.global.MavenRepositoriesPreferences;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

@Prototype
public class AddEditRepositoryDialogController extends AbstractFxmlWindowController {

    private static final String I18N_REPOSITORY_DIALOG_UPDATE_TOOLTIP = "repository.dialog.update.tooltip";
    private static final String I18N_REPOSITORY_DIALOG_UPDATE = "repository.dialog.update";
    private static final String I18N_REPOSITORY_DIALOG_TITLE_UPDATE = "repository.dialog.title.update";
    private static final String I18N_REPOSITORY_DIALOG_ADD_TOOLTIP = "repository.dialog.add.tooltip";
    private static final String I18N_REPOSITORY_DIALOG_ADD = "repository.dialog.add";
    private static final String I18N_REPOSITORY_DIALOG_TITLE_ADD = "repository.dialog.title.add";
    private static final String I18N_REPOSITORY_DIALOG_RESULT = "repository.dialog.result";
    private static final String STYLE_LABEL_ERROR = "label-error";
    private final InstanceWindow owner;
    private final MessageLogger messageLogger;
    private final MavenClient mavenClient;
    private Repository oldRepository;
    private final Service<String> testService;
    private final Optional<List<RepositoryTypeProvider>> repositoryTypes;

    @FXML
    private AnchorPane MavenDialog;

    @FXML
    private TextField nameIDTextfield;

    @FXML
    private ComboBox<Class<? extends RepositoryType>> typeCombo;

    @FXML
    private Button searchButton;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button cancelButton;

    @FXML
    private Button addButton;

    @FXML
    private TextField urlTextfield;

    @FXML
    private CheckBox privateCheckBox;

    @FXML
    private Label userLabel;

    @FXML
    private TextField userTextfield;

    @FXML
    private Label passwordLabel;

    @FXML
    private PasswordField passwordTextfield;

    @FXML
    private Button testButton;

    @FXML
    private Label resultLabel;

 // @formatter:off
    protected AddEditRepositoryDialogController(
            I18N i18n,
            MavenClient mavenClient,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            MessageLogger messageLogger,
            MavenSetting mavenSetting,
            MavenRepositoriesPreferences repositoryPreferences,
            Optional<List<RepositoryTypeProvider>> repositoryTypes,
            InstanceWindow owner) {
     // @formatter:on
        super(i18n, sceneBuilderManager, iconSetting,
                AddEditRepositoryDialogController.class.getResource("AddEditRepositoryDialog.fxml"), owner);
        this.owner = owner;
        this.mavenClient = mavenClient;
        this.messageLogger = messageLogger;
        this.repositoryTypes = repositoryTypes;

        testService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        Repository repository = Repository.builder().id(nameIDTextfield.getText())
                                // .type(typeTextfield.getText())
                                .url(urlTextfield.getText()).user(userTextfield.getText())
                                .password(passwordTextfield.getText()).build();

                        return mavenClient.validate(repository);
                    }
                };
            }
        };

        testService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                String result = testService.getValue();
                if (result.isEmpty()) {
                    if (resultLabel.getStyleClass().contains(STYLE_LABEL_ERROR)) {
                        resultLabel.getStyleClass().remove(STYLE_LABEL_ERROR);
                    }
                    result = getI18n().getString(I18N_REPOSITORY_DIALOG_RESULT);
                } else {
                    if (!resultLabel.getStyleClass().contains(STYLE_LABEL_ERROR)) {
                        resultLabel.getStyleClass().add(STYLE_LABEL_ERROR);
                    }
                }
                resultLabel.setText(result);
                resultLabel.setTooltip(new Tooltip(result));
            }
        });
    }

    @FXML
    public void initialize() {
        Set<Class<? extends RepositoryType>> types = new HashSet<>();
        types.addAll(mavenClient.repositoryTypes());
        repositoryTypes.ifPresent(l -> l.forEach(p -> types.addAll(p.repositoryTypes())));
        typeCombo.getItems().addAll(types);
        typeCombo.getSelectionModel().select(0);
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

    @FXML
    void cancel() {
        closeWindow();

        nameIDTextfield.clear();
        urlTextfield.clear();
        userTextfield.clear();
        passwordTextfield.clear();
        privateCheckBox.setSelected(false);
        resultLabel.setText("");
    }

    @FXML
    void addRepository() {
        Repository repository;
        if (privateCheckBox.isSelected()) {
            repository = Repository.builder().id(nameIDTextfield.getText())
                    .type(typeCombo.getSelectionModel().getSelectedItem()).url(urlTextfield.getText())
                    .user(userTextfield.getText()).password(passwordTextfield.getText()).build();
        } else {
            repository = Repository.builder().id(nameIDTextfield.getText())
                    .type(typeCombo.getSelectionModel().getSelectedItem()).url(urlTextfield.getText()).build();
        }
        if (oldRepository != null) {
            mavenClient.remove(repository);
        }
        mavenClient.add(repository);
        logInfoMessage(oldRepository == null ? "log.user.repository.added" : "log.user.repository.updated",
                repository.getId());
        cancel();
    }

    @FXML
    void test() {
        testService.restart();
    }

    @Override
    public void onCloseRequest() {
        cancel();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void openWindow() {
        super.openWindow();

        userLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        userTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        progress.visibleProperty().bind(testService.runningProperty());
        resultLabel.setText("");
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, getI18n().getBundle(), args);
    }

    public void setRepository(Repository repository) {
        oldRepository = repository;
        if (repository == null) {
            super.getStage().setTitle(getI18n().getString(I18N_REPOSITORY_DIALOG_TITLE_ADD));
            addButton.setText(getI18n().getString(I18N_REPOSITORY_DIALOG_ADD));
            addButton.setTooltip(new Tooltip(getI18n().getString(I18N_REPOSITORY_DIALOG_ADD_TOOLTIP)));

            addButton.disableProperty().bind(nameIDTextfield.textProperty().isEmpty().or(typeCombo.getSelectionModel()
                    .selectedItemProperty().isNull().or(urlTextfield.textProperty().isEmpty())));

            return;
        }

        nameIDTextfield.setText(repository.getId());
        typeCombo.getSelectionModel().select(repository.getType());
        urlTextfield.setText(repository.getUrl());

        userTextfield.clear();
        if (repository.getUser() != null) {
            userTextfield.setText(repository.getUser());
            privateCheckBox.setSelected(true);
        }
        passwordTextfield.clear();
        if (repository.getPassword() != null) {
            passwordTextfield.setText(repository.getPassword());
            privateCheckBox.setSelected(true);
        }

        addButton.disableProperty()
                .bind(nameIDTextfield.textProperty().isEqualTo(repository.getId())
                        .and(typeCombo.getSelectionModel().selectedItemProperty().isEqualTo(repository.getType())
                                .and(urlTextfield.textProperty().isEqualTo(repository.getUrl()).and(userTextfield
                                        .textProperty().isEqualTo(repository.getUser())
                                        .and(passwordTextfield.textProperty().isEqualTo(repository.getPassword()))))));
        super.getStage().setTitle(getI18n().getString(I18N_REPOSITORY_DIALOG_TITLE_UPDATE));
        addButton.setText(getI18n().getString(I18N_REPOSITORY_DIALOG_UPDATE));
        addButton.setTooltip(new Tooltip(getI18n().getString(I18N_REPOSITORY_DIALOG_UPDATE_TOOLTIP)));
    }
}
