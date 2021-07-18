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
package com.oracle.javafx.scenebuilder.library.maven.search;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import com.oracle.javafx.scenebuilder.core.di.SbPlatform;
import com.oracle.javafx.scenebuilder.core.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.ui.AbstractFxmlWindowController;
import com.oracle.javafx.scenebuilder.library.api.AbstractLibrary;
import com.oracle.javafx.scenebuilder.library.maven.MavenArtifact;
import com.oracle.javafx.scenebuilder.library.maven.MavenRepositorySystem;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenArtifactsPreferences;
import com.oracle.javafx.scenebuilder.library.preferences.global.MavenRepositoriesPreferences;

import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.stage.Modality;


/**
 * Controller for the JAR Maven dialog.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class SearchMavenDialogController extends AbstractFxmlWindowController {

    @FXML
    private TextField searchTextfield;

    @FXML
    private Button searchButton;

    @FXML
    private ListView<DefaultArtifact> resultsListView;

    @FXML
    private ProgressIndicator progress;

    @FXML
    private Label installLabel;

    @FXML
    private Button installButton;

    private final Editor editorController;
    private final MavenArtifactsPreferences mavenPreferences;
    private final AbstractLibrary<?, ?> userLibrary;

    private MavenRepositorySystem maven;
    private RemoteRepository remoteRepository;
    private final SearchService searchService;
    private final Service<MavenArtifact> installService;
    private DefaultArtifact artifact;
    private final SceneBuilderWindow owner;

    private final ApplicationContext context;


    protected SearchMavenDialogController(
            Api api,
    		Editor editorController,
    		AbstractLibrary<?, ?> library,
    		MavenSetting mavenSetting,
    		MavenArtifactsPreferences mavenPreferences,
    		MavenRepositoriesPreferences repositoryPreferences,
    		SceneBuilderWindow owner) {
        super(api, SearchMavenDialogController.class.getResource("SearchMavenDialog.fxml"), I18N.getBundle(), owner);
        this.context = api.getContext();
        this.userLibrary = library;
        this.owner = owner;
        this.editorController = editorController;
        this.mavenPreferences = mavenPreferences;

        maven = new MavenRepositorySystem(true, mavenSetting, repositoryPreferences); // only releases

        searchService = new SearchService(mavenSetting.getUserM2Repository());
        searchService.getResult().addListener((ListChangeListener.Change<? extends Artifact> c) -> {
            while (c.next()) {
                resultsListView.getItems().setAll(searchService.getResult()
                        .stream()
                        .sorted((a1, a2) -> a1.toString().compareTo(a2.toString()))
                        .collect(Collectors.toList()));
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
            if (ov.equals(Worker.State.RUNNING)) {
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
                        
                        if (userLibrary.performAddArtifact(mavenArtifact)) {//, files)) {
                            updatePreferences(mavenArtifact);
                            logInfoMessage("log.user.maven.installed", getArtifactCoordinates());
                        }
                        
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
        super.getStage().setTitle(I18N.getString("search.maven.dialog.title"));
        installButton.setDisable(true);
        installButton.setTooltip(new Tooltip(I18N.getString("search.maven.dialog.install.tooltip")));

        searchButton.setDisable(true);
        searchTextfield.textProperty().addListener((obs, ov, nv) -> searchButton.setDisable(nv.isEmpty()));
        searchTextfield.setOnAction(e -> searchButton.fire());
        searchButton.setOnAction(e -> {
            if (progress.isVisible()) {
                searchService.cancelSearch();
            } else {
                searchService.setQuery(searchTextfield.getText());
                searchService.restart();
            }
        });

        resultsListView.setCellFactory(p -> new ListCell<DefaultArtifact>() {
            @Override
            protected void updateItem(DefaultArtifact item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !empty) {
                    String suffix = "";
                    if (item.getProperties() != null && item.getProperties().containsKey("Repository")) {
                        suffix = "  [" + item.getProperties().get("Repository").toString() + "]";
                    }
                    //TODO uncomment for details
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
            }
        });

        searchButton.textProperty().bind(Bindings.when(searchService.searchingProperty())
                .then(I18N.getString("search.maven.dialog.button.cancel"))
                .otherwise(I18N.getString("search.maven.dialog.button.search")));
        searchButton.tooltipProperty().bind(Bindings.when(searchService.searchingProperty())
                .then(new Tooltip(I18N.getString("search.maven.dialog.button.cancel.tooltip")))
                .otherwise(new Tooltip(I18N.getString("search.maven.dialog.button.search.tooltip"))));

        progress.visibleProperty().bind(installService.runningProperty()
                        .or(searchService.searchingProperty()));
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
        searchService.cancelSearch();
        searchService.cancel();
        installService.cancel();
        progress.visibleProperty().unbind();

        searchTextfield.clear();
        resultsListView.getItems().clear();

        closeWindow();
    }

    private MavenArtifact resolveArtifacts() {
        if (artifact == null) {
            return null;
        }

        if (artifact.getVersion().equals(Search.MIN_VERSION)) {
            addVersion();
            SbPlatform.runLater(() ->
                installLabel.setText(I18N.getString("search.maven.dialog.installing",
                        getArtifactCoordinates())));
        }

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
        if (artifact == null) {
            return "";
        }

        return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
    }

    private void updatePreferences(MavenArtifact mavenArtifact) {
        if (mavenArtifact == null) {
            return;
        }
//TODO something
        //userLibrary.stopWatching();
        //userLibrary.releaseLocks();

        // Update record artifact
        mavenPreferences.getRecordArtifact(mavenArtifact).writeToJavaPreferences();

        //userLibrary.startWatching();
        //userLibrary.enableLocks();
    }

    private void addVersion() {
        Version version = maven.findLatestVersion(artifact);
        if (version == null) {
            return;
        }

        final Map<String, String> map = new HashMap<>();
        map.put("Repository", maven.getRemoteRepository(version).getId());
        artifact = new DefaultArtifact(artifact.getGroupId()+ ":" + artifact.getArtifactId() + ":" +version.toString(), map);
        remoteRepository = maven.getRemoteRepository(version);
    }
}