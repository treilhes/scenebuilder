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
package com.oracle.javafx.scenebuilder.library.editor.panel.library.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Api;
import com.oracle.javafx.scenebuilder.api.Editor;
import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.panel.util.dialog.AbstractModalDialog.ButtonID;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.ImportWindowController;
import com.oracle.javafx.scenebuilder.library.editor.panel.library.LibraryPanelController;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;
import com.oracle.javafx.scenebuilder.library.user.UserLibrary;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.stage.Stage;


/**
 * Controller for the JAR Maven dialog.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class MavenDialogController extends AbstractFxmlWindowController {

    @FXML
    private TextField groupIDTextfield;

    @FXML
    private TextField artifactIDTextfield;

    @FXML
    private ComboBox<Version> versionsCombo;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button installButton;

    private final UserLibrary userLibrary;

    private MavenRepositorySystem maven;
    private RemoteRepository remoteRepository;
    private Service<ObservableList<Version>> versionsService;
    private final Service<MavenArtifact> installService;
    private final SceneBuilderWindow owner;
    private final Editor editorController;

    private final ChangeListener<Version> comboBoxListener = (obs, ov, nv) -> {
        remoteRepository = maven.getRemoteRepository(nv);
    };

    private final ChangeListener<Boolean> serviceListener = (obs, ov, nv) -> {
        if (!nv) {
            callVersionsService();
        }
    };

	private final MavenArtifactsPreferences mavenPreferences;

    private final ApplicationContext context;

    public MavenDialogController(
            Api api,
    		Editor editorController,
    		LibraryPanelController libraryPanelController,
    		MavenSetting mavenSetting,
    		MavenArtifactsPreferences mavenPreferences,
    		MavenRepositoriesPreferences repositoryPreferences,
    		SceneBuilderWindow owner) {
        super(api, LibraryPanelController.class.getResource("MavenDialog.fxml"), I18N.getBundle(), owner); //NOI18N
        this.context = api.getContext();
        this.userLibrary = (UserLibrary) api.getApiDoc().getLibrary();
        this.owner = owner;
        this.editorController = editorController;
        this.mavenPreferences = mavenPreferences;
        
        maven = new MavenRepositorySystem(false, mavenSetting, repositoryPreferences);

        versionsService = new Service<ObservableList<Version>>() {
            @Override
            protected Task<ObservableList<Version>> createTask() {
                return new Task<ObservableList<Version>>() {
                    @Override
                    protected ObservableList<Version> call() throws Exception {
                        return FXCollections.observableArrayList(getVersions());
                    }
                };
            }
        };

        versionsService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                versionsCombo.getItems().setAll(versionsService.getValue()
                        .sorted((v1, v2) -> v2.compareTo(v1)));
                versionsCombo.setCellFactory(p -> new ListCell<Version>() {
                    @Override
                    protected void updateItem(Version item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null && !empty) {
                            final RemoteRepository remote = maven.getRemoteRepository(item);
                            setText(item + " [" + remote.getId() + "]");
                        } else {
                            setText(null);
                        }
                    }

                });
                versionsCombo.getSelectionModel().selectedItemProperty().addListener(comboBoxListener);
                versionsCombo.setDisable(false);
            } else if (nv.equals(Worker.State.CANCELLED) || nv.equals(Worker.State.FAILED)) {
                versionsCombo.setDisable(false);
            }
        });

        installService = new Service<MavenArtifact>() {
            @Override
            protected Task<MavenArtifact> createTask() {
                return new Task<MavenArtifact>() {
                    @Override
                    protected MavenArtifact call() throws Exception {
                        return resolveArtifacts();
                    }
                };
            }
        };

        installService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                final MavenArtifact mavenArtifact = installService.getValue();
                if (mavenArtifact == null || mavenArtifact.getPath().isEmpty() ||
                        !new File(mavenArtifact.getPath()).exists()) {
                    logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
                } else {
                    List<File> files = new ArrayList<>();
                    files.add(new File(mavenArtifact.getPath()));
                    if (!mavenArtifact.getDependencies().isEmpty()) {
                        files.addAll(Stream
                                .of(mavenArtifact.getDependencies().split(File.pathSeparator))
                                .map(File::new)
                                .collect(Collectors.toList()));
                    }

                    final ImportWindowController iwc
                            = context.getBean(ImportWindowController.class, api, libraryPanelController,files, mavenPreferences,
                            (Stage)installButton.getScene().getWindow(), false,mavenPreferences.getArtifactsFilter());
                    //iwc.setToolStylesheet(editorController.getToolStylesheet());
                    ButtonID userChoice = iwc.showAndWait();
                    if (userChoice == ButtonID.OK) {
                        mavenArtifact.setFilter(iwc.getNewExcludedItems());
                        updatePreferences(mavenArtifact);
                        logInfoMessage("log.user.maven.installed", getArtifactCoordinates());
                    }
                    this.onCloseRequest();
                }
            } else if (nv.equals(Worker.State.CANCELLED) || nv.equals(Worker.State.FAILED)) {
                logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
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

    @Override
    public void onCloseRequest() {
        cancel();
    }

    @Override
    public void onFocus() {}

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(I18N.getString("maven.dialog.title"));
        installButton.disableProperty().bind(groupIDTextfield.textProperty().isEmpty().or(
                      artifactIDTextfield.textProperty().isEmpty().or(
                      versionsCombo.getSelectionModel().selectedIndexProperty().lessThan(0))));
        installButton.setTooltip(new Tooltip(I18N.getString("maven.dialog.install.tooltip")));

        versionsCombo.setDisable(true);

        groupIDTextfield.focusedProperty().addListener(serviceListener);
        groupIDTextfield.setOnAction(e -> callVersionsService());
        artifactIDTextfield.focusedProperty().addListener(serviceListener);
        artifactIDTextfield.setOnAction(e -> callVersionsService());

        progress.visibleProperty().bind(versionsService.runningProperty()
                .or(installService.runningProperty()));
    }

    @FXML
    private void installJar() {
        installService.restart();
    }

    @FXML
    private void cancel() {
        groupIDTextfield.focusedProperty().removeListener(serviceListener);
        artifactIDTextfield.focusedProperty().removeListener(serviceListener);
        installButton.disableProperty().unbind();
        progress.visibleProperty().unbind();

        groupIDTextfield.clear();
        artifactIDTextfield.clear();
        versionsCombo.getSelectionModel().selectedItemProperty().removeListener(comboBoxListener);
        versionsCombo.getItems().clear();
        versionsCombo.setDisable(true);

        closeWindow();
    }

    private void callVersionsService() {
        if (groupIDTextfield.getText().isEmpty() || artifactIDTextfield.getText().isEmpty()) {
            return;
        }
        versionsCombo.getSelectionModel().selectedItemProperty().removeListener(comboBoxListener);
        versionsCombo.getItems().clear();
        versionsCombo.setDisable(true);
        versionsService.restart();
    }

    private List<Version> getVersions() {
        Artifact artifact = new DefaultArtifact(groupIDTextfield.getText() + ":" +
                artifactIDTextfield.getText() + ":[0,)");

        return maven.findVersions(artifact);
    }

    private MavenArtifact resolveArtifacts() {
        if (remoteRepository == null) {
            return null;
        }

        String[] coordinates = getArtifactCoordinates().split(":");
        Artifact jarArtifact = new DefaultArtifact(coordinates[0],
                coordinates[1], "", "jar", coordinates[2]);

        Artifact javadocArtifact = new DefaultArtifact(coordinates[0],
                coordinates[1], "javadoc", "jar", coordinates[2]);

        Artifact pomArtifact = new DefaultArtifact(coordinates[0],
                coordinates[1], "", "pom", coordinates[2]);

        MavenArtifact mavenArtifact = new MavenArtifact(getArtifactCoordinates());
        mavenArtifact.setPath(maven.resolveArtifacts(remoteRepository, jarArtifact, javadocArtifact, pomArtifact));
        mavenArtifact.setDependencies(maven.resolveDependencies(remoteRepository, jarArtifact));

        return mavenArtifact;
    }

    private void logInfoMessage(String key, Object... args) {
        editorController.getMessageLog().logInfoMessage(key, I18N.getBundle(), args);
    }

    private String getArtifactCoordinates() {
        return groupIDTextfield.getText() + ":" + artifactIDTextfield.getText() + ":" +
                versionsCombo.getSelectionModel().getSelectedItem().toString();
    }

    private void updatePreferences(MavenArtifact mavenArtifact) {
        if (mavenArtifact == null) {
            return;
        }

        userLibrary.stopWatching();

        // Update record artifact
        mavenPreferences.getRecordArtifact(mavenArtifact).writeToJavaPreferences();

        userLibrary.startWatching();
    }

}