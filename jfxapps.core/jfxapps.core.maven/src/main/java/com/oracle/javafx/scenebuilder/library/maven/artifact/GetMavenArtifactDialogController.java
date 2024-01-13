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
package com.oracle.javafx.scenebuilder.library.maven.artifact;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.maven.client.api.ResolvedArtifact;
import com.gluonhq.jfxapps.boot.maven.client.api.UniqueArtifact;
import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.maven.ArtefactHandler;
import com.oracle.javafx.scenebuilder.api.maven.GetMavenArtifactDialog;
import com.oracle.javafx.scenebuilder.api.maven.MavenClient;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;

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
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;
import javafx.util.Callback;

/**
 * Controller for the JAR Maven dialog.
 */
@ApplicationInstanceSingleton
public class GetMavenArtifactDialogController extends AbstractFxmlWindowController implements GetMavenArtifactDialog {

    @FXML
    private TextField groupIDTextfield;

    @FXML
    private TextField artifactIDTextfield;

    @FXML
    private ComboBox<UniqueArtifact> versionsCombo;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Button installButton;

    private final MavenClient mavenClient;
    private Service<ObservableList<UniqueArtifact>> versionsService;
    private final Service<ResolvedArtifact> installService;
    private final SceneBuilderWindow owner;
    private final MessageLogger messageLogger;

//    private final ChangeListener<MavenArtifact> comboBoxListener = (obs, ov, nv) -> {
//        selectedArtifact = nv;
//    };

    private final ChangeListener<Boolean> serviceListener = (obs, ov, nv) -> {
        if (!nv) {
            callVersionsService();
        }
    };

    private String lastLoadedKey = null;

    // @formatter:off
    public GetMavenArtifactDialogController(
            MavenClient mavenClient,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            MessageLogger messageLogger,
            MavenSetting mavenSetting,
            MavenRepositoriesPreferences repositoryPreferences,
            SceneBuilderWindow owner) {
     // @formatter:on
        super(sceneBuilderManager, iconSetting,
                GetMavenArtifactDialogController.class.getResource("GetMavenArtifactDialog.fxml"), I18N.getBundle(),
                owner);
        this.mavenClient = mavenClient;
        this.owner = owner;
        this.messageLogger = messageLogger;

        versionsService = new Service<ObservableList<UniqueArtifact>>() {
            @Override
            protected Task<ObservableList<UniqueArtifact>> createTask() {
                return new Task<ObservableList<UniqueArtifact>>() {
                    @Override
                    protected ObservableList<UniqueArtifact> call() throws Exception {
                        return FXCollections.observableArrayList(getVersions());
                    }
                };
            }
        };

        versionsService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                boolean showing = versionsCombo.isShowing();

                if (showing) {
                    versionsCombo.hide();
                }

                versionsCombo.getItems().setAll(versionsService.getValue().sorted(Comparator.reverseOrder()));
                versionsCombo.getSelectionModel().select(0);

                if (showing) {
                    versionsCombo.show();
                }
            }
        });

        installService = new Service<ResolvedArtifact>() {
            @Override
            protected Task<ResolvedArtifact> createTask() {
                return new Task<ResolvedArtifact>() {
                    @Override
                    protected ResolvedArtifact call() throws Exception {
                        return resolveArtifacts();
                    }
                };
            }
        };
    }

    @FXML
    public void initialize() {
        Callback<ListView<UniqueArtifact>, ListCell<UniqueArtifact>> cellFactory = p -> new ListCell<UniqueArtifact>() {
            @Override
            protected void updateItem(UniqueArtifact item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    if (item.getRepository() != null) {
                        setText(item.getVersion() + " [" + item.getRepository().getId() + "]");
                    } else {
                        setText(item.getVersion());
                    }
                } else {
                    setText(null);
                }
            }
        };

        versionsCombo.setButtonCell(cellFactory.call(null));
        versionsCombo.setCellFactory(cellFactory);
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
    public void onFocus() {
    }

    @Override
    public void openWindow(ArtefactHandler handler) {
        installService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                final UniqueArtifact mavenArtifact = getArtifact();
                final ResolvedArtifact resolved = installService.getValue();

                boolean invalidResult = resolved == null || !resolved.hasPath()
                        || resolved.getDependencies().stream().anyMatch(d -> !d.hasPath());

                if (invalidResult) {
                    logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
                } else {
                    List<File> files = new ArrayList<>();
                    files.add(resolved.getPath().toFile());

                    files.addAll(resolved.getDependencies().stream()
                            .map(p -> p.getPath().toFile())
                            .collect(Collectors.toList()));

                    handler.handle(resolved, files);

                    this.onCloseRequest();
                }
            } else if (nv.equals(Worker.State.CANCELLED) || nv.equals(Worker.State.FAILED)) {
                logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
            }
        });
        openWindow();
    }

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(I18N.getString("maven.dialog.title"));
        installButton.disableProperty().bind(groupIDTextfield.textProperty().isEmpty().or(artifactIDTextfield
                .textProperty().isEmpty().or(versionsCombo.getSelectionModel().selectedIndexProperty().lessThan(0))));
        installButton.setTooltip(new Tooltip(I18N.getString("maven.dialog.install.tooltip")));

        groupIDTextfield.focusedProperty().addListener(serviceListener);
        groupIDTextfield.setOnAction(e -> callVersionsService());
        artifactIDTextfield.focusedProperty().addListener(serviceListener);
        artifactIDTextfield.setOnAction(e -> callVersionsService());

        progress.visibleProperty().bind(versionsService.runningProperty().or(installService.runningProperty()));
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
        versionsCombo.getItems().clear();

        closeWindow();
    }

    private void callVersionsService() {
        if (groupIDTextfield.getText().isEmpty() || artifactIDTextfield.getText().isEmpty()) {
            return;
        }

        if (versionsService.isRunning()) {
            return;
        }

        String newLoadedKey = groupIDTextfield.getText() + "_" + artifactIDTextfield.getText();

        if (newLoadedKey.equals(lastLoadedKey)) {
            return;
        } else {
            lastLoadedKey = newLoadedKey;
        }

        versionsCombo.getItems().clear();
        versionsService.restart();
    }

    private List<UniqueArtifact> getVersions() {
        String groupId = groupIDTextfield.getText();
        String artifactid = artifactIDTextfield.getText();
        return mavenClient.getAvailableVersions(groupId, artifactid);
    }

    private ResolvedArtifact resolveArtifacts() {
        UniqueArtifact selected = getArtifact();
        return mavenClient.resolveWithDependencies(selected).get();
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, I18N.getBundle(), args);
    }

    private String getArtifactCoordinates() {
        UniqueArtifact selected = getArtifact();
        if (selected != null) {
            return selected.getCoordinates();
        } else {
            return groupIDTextfield.getText() + ":" + artifactIDTextfield.getText() + ":?";
        }
    }

    private UniqueArtifact getArtifact() {
        return versionsCombo.getSelectionModel().getSelectedItem();
    }

}