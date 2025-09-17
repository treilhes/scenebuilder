/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.app.manager.registries.controller;

import java.util.ArrayList;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.registries.model.Source;
import com.gluonhq.jfxapps.app.manager.registries.model.SourceModelController;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.maven.Artifact;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.UniqueArtifact;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistryArtifact;
import com.gluonhq.jfxapps.boot.api.registry.model.RegistrySourceInfo;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

@ApplicationInstancePrototype
public class EditSourceItemController extends AbstractFxmlController {

    private static final String LATEST = SourceModelController.LATEST;

    private static final UniqueArtifact LATEST_ARTIFACT = UniqueArtifact.builder().artifact(LATEST, LATEST).version(LATEST).build();

    private static final Logger LOGGER = LoggerFactory.getLogger(EditSourceItemController.class);

    @FXML
    private TextField groupId;

    @FXML
    private TextField artifactId;

    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<UniqueArtifact> version;

    @FXML
    private Button validate;

    @FXML
    private Button cancel;

    private Consumer<Source> onValidate;

    private Runnable onCancel;

    private Source source;

    private final RepositoryClient mavenClient;

    //@formatter:off
    protected EditSourceItemController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            RepositoryClient mavenClient) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, EditSourceItemController.class.getResource("EditSourceItem.fxml"));
        this.mavenClient = mavenClient;
    }

    @FXML
    public void initialize() {
        LOGGER.info("initialize EditSourceItemController");

        version.setConverter(new StringConverter<UniqueArtifact>() {
            @Override
            public String toString(UniqueArtifact object) {
                return object == null ? "" : object.getVersion();
            }

            @Override
            public UniqueArtifact fromString(String string) {
                return null;
            }
        });
        version.setCellFactory((o) -> {
            return new ListCell<>() {
                @Override
                protected void updateItem(UniqueArtifact item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getVersion());
                    }
                }
            };

        });
        version.disableProperty().bind(Bindings.isEmpty(groupId.textProperty()).or(Bindings.isEmpty(artifactId.textProperty())));
        version.setItems(FXCollections.observableArrayList(LATEST_ARTIFACT));
        version.getSelectionModel().selectFirst();

        validate.disableProperty().bind(Bindings.isEmpty(groupId.textProperty()).or(Bindings.isEmpty(artifactId.textProperty())));

    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(EditSourceItemController.class.getSimpleName());
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        Objects.requireNonNull(source, "sourceItem must not be null");
        this.source = source;

        var sourceGroupId = source.groupIdProperty().get();
        var sourceArtifactId = source.artifactIdProperty().get();
        var sourceVersion = source.versionProperty().get();

        groupId.setText(sourceGroupId);
        artifactId.setText(sourceArtifactId);

        if (sourceVersion.isBlank()) {
            version.getSelectionModel().select(LATEST_ARTIFACT);
        } else {
            var versionArtifact = UniqueArtifact.builder().artifact(groupId.getText(), artifactId.getText())
                    .version(sourceVersion).build();
            version.getItems().add(versionArtifact);
            version.getSelectionModel().select(versionArtifact);
        }

        errorLabel.textProperty().bind(source.errorMessageProperty());
    }

    public void setOnValidate(Consumer<Source> onValidate) {
        this.onValidate = onValidate;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    void validate(ActionEvent event) {
        var sourceInfo = source == null ? null : source.infoProperty().get();
        var registryInfo = sourceInfo == null ? null : sourceInfo.getRegistryInfo();

        var version = this.version.getSelectionModel().getSelectedItem().getVersion();
        version = version.equals(LATEST) ? "" : version;

        var artifact = new RegistryArtifact(groupId.getText(), artifactId.getText(), version, false);
        var info = new RegistrySourceInfo();
        info.setArtifact(artifact);
        info.setRegistryInfo(registryInfo);

        var source = new Source(info);

        if (onValidate != null) {
            onValidate.accept(source);
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        if (onCancel != null) {
            onCancel.run();
        }
    }

    @FXML
    void loadVersions(Event event) {
        System.out.println("load versions");
        var selected = version.getSelectionModel().getSelectedItem().getVersion();

        var groupId = this.groupId.getText();
        var artifactId = this.artifactId.getText();
        var artifact = Artifact.builder().groupId(groupId).artifactId(artifactId).build();
        var lookupVersions = mavenClient.getAvailableVersions(artifact);
        var versions = new ArrayList<>(lookupVersions);
        versions.add(0, LATEST_ARTIFACT);
        version.setItems(FXCollections.observableArrayList(versions));

        var versionArtifact = UniqueArtifact.builder().artifact(groupId, artifactId).version(selected).build();
        version.getSelectionModel().select(versionArtifact);
    }

}
