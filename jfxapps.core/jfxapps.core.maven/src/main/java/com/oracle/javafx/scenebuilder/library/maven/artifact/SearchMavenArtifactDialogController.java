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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.api.SceneBuilderWindow;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.maven.ArtefactHandler;
import com.oracle.javafx.scenebuilder.api.maven.MavenClient;
import com.oracle.javafx.scenebuilder.api.maven.SearchMavenArtifactDialog;
import com.oracle.javafx.scenebuilder.api.settings.MavenSetting;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.api.ui.misc.IconSetting;
import com.oracle.javafx.scenebuilder.api.ui.misc.MessageLogger;
import com.oracle.javafx.scenebuilder.core.context.annotation.Prototype;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifact;
import com.oracle.javafx.scenebuilder.maven.client.api.MavenArtifactId;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
@Prototype
public class SearchMavenArtifactDialogController extends AbstractFxmlWindowController
        implements SearchMavenArtifactDialog {

    @FXML
    private TextField searchTextfield;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<MavenArtifactId> resultsListView;

    @FXML
    private ComboBox<MavenArtifact> versionsCombo;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label installLabel;

    @FXML
    private Button installButton;

    private final MavenClient mavenClient;
    private final MessageLogger messageLogger;
    private Service<ObservableSet<MavenArtifactId>> searchService;
    private Service<ObservableList<MavenArtifact>> versionsService;
    private final Service<MavenArtifact> installService;
    private MavenArtifactId artifact;
    private final SceneBuilderWindow owner;
    private String lastLoadedKey = null;

 // @formatter:off
    protected SearchMavenArtifactDialogController(
            MavenClient mavenClient,
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            MessageLogger messageLogger,
            MavenSetting mavenSetting,
            MavenRepositoriesPreferences repositoryPreferences,
            SceneBuilderWindow owner) {
     // @formatter:on
        super(sceneBuilderManager, iconSetting,
                SearchMavenArtifactDialogController.class.getResource("SearchMavenArtifactDialog.fxml"),
                I18N.getBundle(), owner);
        this.mavenClient = mavenClient;
        this.owner = owner;
        this.messageLogger = messageLogger;

        searchService = new Service<ObservableSet<MavenArtifactId>>() {
            @Override
            protected Task<ObservableSet<MavenArtifactId>> createTask() {
                return new Task<ObservableSet<MavenArtifactId>>() {
                    @Override
                    protected ObservableSet<MavenArtifactId> call() throws Exception {
                        return FXCollections.observableSet(getArtifacts());
                    }
                };
            }
        };

        versionsService = new Service<ObservableList<MavenArtifact>>() {
            @Override
            protected Task<ObservableList<MavenArtifact>> createTask() {
                return new Task<ObservableList<MavenArtifact>>() {
                    @Override
                    protected ObservableList<MavenArtifact> call() throws Exception {
                        return FXCollections.observableArrayList(getVersions());
                    }
                };
            }
        };

        searchService.stateProperty().addListener((obs, ov, nv) -> {
            if (nv.equals(Worker.State.SUCCEEDED)) {
                resultsListView.getItems().setAll(searchService.getValue());
            }
        });

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
    }

    @FXML
    public void initialize() {
        Callback<ListView<MavenArtifact>, ListCell<MavenArtifact>> cellFactory = p -> new ListCell<MavenArtifact>() {
            @Override
            protected void updateItem(MavenArtifact item, boolean empty) {
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
            if (ov.equals(Worker.State.RUNNING)) {
                if (nv.equals(Worker.State.SUCCEEDED)) {
                    final MavenArtifact mavenArtifact = getArtifact();
                    final MavenArtifact resolved = installService.getValue();

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

                        handler.handle(mavenArtifact, files);

                        this.onCloseRequest();
                    }
                } else if (nv.equals(Worker.State.CANCELLED) || nv.equals(Worker.State.FAILED)) {
                    logInfoMessage("log.user.maven.failed", getArtifactCoordinates());
                }
                installButton.setDisable(false);
                searchButton.setDisable(false);
                resultsListView.setDisable(false);
                searchTextfield.setDisable(false);
                installLabel.setVisible(false);
            }
        });
        openWindow();
    }

    @Override
    public void openWindow() {
        super.openWindow();
        super.getStage().setTitle(I18N.getString("search.maven.dialog.title"));
        installButton.setDisable(true);
        installButton.setTooltip(new Tooltip(I18N.getString("search.maven.dialog.install.tooltip")));

        searchButton.setDisable(true);
        searchTextfield.textProperty().addListener((obs, ov, nv) -> searchButton.setDisable(nv.isEmpty()));
        searchTextfield.setOnAction(e -> searchButton.fire());
        searchButton.setOnAction(e -> {
            if (progress.isVisible()) {
                // searchService.cancelSearch();
            } else {
                searchService.restart();
            }
        });

        resultsListView.setCellFactory(p -> new ListCell<MavenArtifactId>() {
            @Override
            protected void updateItem(MavenArtifactId item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
//                    String suffix = "";
//                    if (item.getProperties() != null && item.getProperties().containsKey("Repository")) {
//                        suffix = "  [" + item.getProperties().get("Repository").toString() + "]";
//                    }
                    // TODO uncomment for details
                    setText(item.getGroupId() + ":" + item.getArtifactId());// + ":" + item.getBaseVersion() + suffix);
                } else {
                    setText(null);
                }
            }
        });

        resultsListView.getSelectionModel().selectedItemProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                installButton.setDisable(resultsListView.getSelectionModel().getSelectedIndex() < 0);
                artifact = resultsListView.getSelectionModel().getSelectedItem();
                callVersionsService();
            }
        });

        searchButton.textProperty()
                .bind(Bindings.when(mavenClient.searchingProperty())
                        .then(I18N.getString("search.maven.dialog.button.cancel"))
                        .otherwise(I18N.getString("search.maven.dialog.button.search")));
        searchButton.tooltipProperty()
                .bind(Bindings.when(mavenClient.searchingProperty())
                        .then(new Tooltip(I18N.getString("search.maven.dialog.button.cancel.tooltip")))
                        .otherwise(new Tooltip(I18N.getString("search.maven.dialog.button.search.tooltip"))));

        progress.visibleProperty().bind(installService.runningProperty().or(versionsService.runningProperty())
                .or(mavenClient.searchingProperty()));
    }

    @FXML
    private void installJar() {
        searchTextfield.setDisable(true);
        installButton.setDisable(true);
        searchButton.setDisable(true);
        resultsListView.setDisable(true);
        installLabel.setText("");
        installLabel.setVisible(true);
        installService.restart();
    }

    @FXML
    private void cancel() {
        // searchService.cancelSearch();
        searchService.cancel();
        installService.cancel();
        progress.visibleProperty().unbind();

        searchTextfield.clear();
        resultsListView.getItems().clear();

        closeWindow();
    }

    private void callVersionsService() {
        if (artifact.getGroupId().isEmpty() || artifact.getArtifactId().isEmpty()) {
            return;
        }

        if (versionsService.isRunning()) {
            return;
        }

        String newLoadedKey = artifact.getGroupId() + "_" + artifact.getArtifactId();

        if (newLoadedKey.equals(lastLoadedKey)) {
            return;
        } else {
            lastLoadedKey = newLoadedKey;
        }

        versionsCombo.getItems().clear();
        versionsService.restart();
    }

    private Set<MavenArtifactId> getArtifacts() {
        String searchTerm = searchTextfield.getText();
        return mavenClient.search(searchTerm);
    }

    private List<MavenArtifact> getVersions() {
        String groupId = artifact.getGroupId();
        String artifactid = artifact.getArtifactId();
        return mavenClient.getAvailableVersions(groupId, artifactid);
    }

    private MavenArtifact resolveArtifacts() {
        MavenArtifact selected = getArtifact();
        return mavenClient.resolveWithDependencies(selected);
    }

    private void logInfoMessage(String key, Object... args) {
        messageLogger.logInfoMessage(key, I18N.getBundle(), args);
    }

    private String getArtifactCoordinates() {
        MavenArtifact selected = getArtifact();
        if (selected != null) {
            return selected.getCoordinates();
        } else {
            return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":?";
        }
    }

    private MavenArtifact getArtifact() {
        return versionsCombo.getSelectionModel().getSelectedItem();
    }

}