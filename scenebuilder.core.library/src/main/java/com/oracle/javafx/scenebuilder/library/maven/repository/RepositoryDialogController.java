/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.IconSetting;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.library.maven.MavenRepositorySystem;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;

@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class RepositoryDialogController extends AbstractFxmlWindowController {

    @FXML
    private AnchorPane MavenDialog;

    @FXML
    private TextField nameIDTextfield;

    @FXML
    private TextField typeTextfield;

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
//
    private final SceneBuilderWindow owner;
    private final MessageLogger messageLogger;
    private MavenRepositorySystem maven;
    private Repository oldRepository;
    private final Service<String> testService;

    private final MavenSetting mavenSetting;
    private final MavenRepositoriesPreferences repositoryPreferences;


    protected RepositoryDialogController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
    		MessageLogger messageLogger,
    		MavenSetting mavenSetting,
    		MavenRepositoriesPreferences repositoryPreferences,
    		SceneBuilderWindow owner) {
        super(sceneBuilderManager, iconSetting, RepositoryDialogController.class.getResource("RepositoryDialog.fxml"), I18N.getBundle(), owner);
        this.owner = owner;
        this.messageLogger = messageLogger;
        this.mavenSetting = mavenSetting;
        this.repositoryPreferences = repositoryPreferences;

        testService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        return maven.validateRepository(new Repository(nameIDTextfield.getText(),
                            typeTextfield.getText(), urlTextfield.getText(),
                            userTextfield.getText(), passwordTextfield.getText()));
                    }
                };
            }
        };

        testService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                String result = testService.getValue();
                if (result.isEmpty()) {
                    if (resultLabel.getStyleClass().contains("label-error")) {
                        resultLabel.getStyleClass().remove("label-error");
                    }
                    result = I18N.getString("repository.dialog.result");
                } else {
                    if (!resultLabel.getStyleClass().contains("label-error")) {
                        resultLabel.getStyleClass().add("label-error");
                    }
                }
                resultLabel.setText(result);
                resultLabel.setTooltip(new Tooltip(result));
            }
        });
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
        typeTextfield.clear();
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
            repository = new Repository(nameIDTextfield.getText(),
                typeTextfield.getText(), urlTextfield.getText(),
                userTextfield.getText(), passwordTextfield.getText());
        } else {
            repository = new Repository(nameIDTextfield.getText(),
                typeTextfield.getText(), urlTextfield.getText());
        }
        if (oldRepository != null) {
        	repositoryPreferences.removeRecord(oldRepository.getId());
        }
        updatePreferences(repository);
        logInfoMessage(oldRepository == null ? "log.user.repository.added" :
                "log.user.repository.updated", repository.getId());
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
    public void onFocus() {}

    @Override
    public void openWindow() {
        super.openWindow();

        userLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        userTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordLabel.disableProperty().bind(privateCheckBox.selectedProperty().not());
        passwordTextfield.disableProperty().bind(privateCheckBox.selectedProperty().not());
        progress.visibleProperty().bind(testService.runningProperty());
        resultLabel.setText("");
        maven = new MavenRepositorySystem(false, mavenSetting, repositoryPreferences);
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, I18N.getBundle(), args);
    }

    private void updatePreferences(Repository repository) {
        if (repository == null) {
            return;
        }

        // Update record repository
        repositoryPreferences.getRecordRepository(repository).writeToJavaPreferences();

    }

    public void setRepository(Repository repository) {
        oldRepository = repository;
        if (repository == null) {
            super.getStage().setTitle(I18N.getString("repository.dialog.title.add"));
            addButton.setText(I18N.getString("repository.dialog.add"));
            addButton.setTooltip(new Tooltip(I18N.getString("repository.dialog.add.tooltip")));

            addButton.disableProperty().bind(nameIDTextfield.textProperty().isEmpty().or(
                      typeTextfield.textProperty().isEmpty().or(
                      urlTextfield.textProperty().isEmpty())));

            return;
        }

        nameIDTextfield.setText(repository.getId());
        typeTextfield.setText(repository.getType());
        urlTextfield.setText(repository.getURL());

        userTextfield.clear();
        if (repository.getUser() != null) {
            userTextfield.setText(repository.getUser());
            privateCheckBox.setSelected(true);
        }
        passwordTextfield.clear();
        if (repository.getPassword()!= null) {
            passwordTextfield.setText(repository.getPassword());
            privateCheckBox.setSelected(true);
        }

        addButton.disableProperty().bind(nameIDTextfield.textProperty().isEqualTo(repository.getId()).and(
                  typeTextfield.textProperty().isEqualTo(repository.getType()).and(
                  urlTextfield.textProperty().isEqualTo(repository.getURL()).and(
                  userTextfield.textProperty().isEqualTo(repository.getUser()).and(
                  passwordTextfield.textProperty().isEqualTo(repository.getPassword()))))));
        super.getStage().setTitle(I18N.getString("repository.dialog.title.update"));
        addButton.setText(I18N.getString("repository.dialog.update"));
        addButton.setTooltip(new Tooltip(I18N.getString("repository.dialog.update.tooltip")));
    }
}
