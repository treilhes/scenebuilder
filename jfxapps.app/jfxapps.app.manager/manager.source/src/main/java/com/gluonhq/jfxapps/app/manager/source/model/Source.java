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
package com.gluonhq.jfxapps.app.manager.source.model;

import java.net.URL;

import com.gluonhq.jfxapps.boot.api.registry.model.RegistrySourceInfo;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Source {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty groupId = new SimpleStringProperty();
    private StringProperty artifactId = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private StringProperty version = new SimpleStringProperty();
    private StringProperty currentVersion = new SimpleStringProperty();
    private ObjectProperty<URL> image = new SimpleObjectProperty<>();

    private BooleanProperty mandatory = new SimpleBooleanProperty(false);
    private BooleanProperty error = new SimpleBooleanProperty(false);
    private StringProperty errorMessage = new SimpleStringProperty();
    private BooleanProperty updating = new SimpleBooleanProperty(false);
    private ObjectProperty<RegistrySourceInfo> info = new SimpleObjectProperty<>();

    public Source(RegistrySourceInfo info) {
        this.infoProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                update(nv);
            }
        });
        this.info.set(info);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty descriptionProperty() {
        return description;
    }

    public StringProperty versionProperty() {
        return version;
    }

    public StringProperty currentVersionProperty() {
        return currentVersion;
    }

    public ObjectProperty<URL> imageProperty() {
        return image;
    }

    public BooleanProperty errorProperty() {
        return error;
    }

    public StringProperty errorMessageProperty() {
        return errorMessage;
    }

    public BooleanProperty mandatoryProperty() {
        return mandatory;
    }

    public BooleanProperty updatingProperty() {
        return updating;
    }

    public StringProperty groupIdProperty() {
        return groupId;
    }

    public StringProperty artifactIdProperty() {
        return artifactId;
    }

    public ObjectProperty<RegistrySourceInfo> infoProperty() {
        return info;
    }

    public boolean match(String searchTerm) {

        if (searchTerm == null || searchTerm.isEmpty() || info.get() == null) {
            return true;
        }
        var data = info.get();
        var lowerCaseSearch = searchTerm.toLowerCase();
        var lcGroupId = data.getArtifact().groupId().toLowerCase();
        var lcArtifactId = data.getArtifact().artifactId().toLowerCase();
        var lcTitle = data.getRegistryInfo() != null && data.getRegistryInfo().getTitle() != null
                ? data.getRegistryInfo().getTitle().toLowerCase()
                        : "";
        return lcGroupId.contains(lowerCaseSearch) || lcArtifactId.contains(lowerCaseSearch)
                || lcTitle.contains(lowerCaseSearch);

    }

    private void update(RegistrySourceInfo source) {
        var group = source.getArtifact().groupId();
        var artifact = source.getArtifact().artifactId();
        var mandatory = source.getArtifact().mandatory();
        var subtitle = group + ":" + artifact;

        var registryInfo = source.getRegistryInfo();

        var title = "";
        var version = source.getArtifact().version();
        var currentVersion = "";
        var description = "";
        var changelog = "";

        if (registryInfo != null) {
            title = registryInfo.getTitle() != null ? registryInfo.getTitle() : subtitle;
            currentVersion = registryInfo.getVersion() != null ? registryInfo.getVersion() : "";
            description = registryInfo.getText() != null ? registryInfo.getText() : "";
            changelog = registryInfo.getChangelog() != null ? registryInfo.getChangelog() : "";

            if (registryInfo.getImage() != null) {
                this.image.set(registryInfo.getImage());
            }
        }

        if (title.isBlank()) {
            title = subtitle;
            subtitle = "";
        }

        this.name.set(title);
        this.description.set(description);
        this.version.set(version);
        this.currentVersion.set(currentVersion);
        this.mandatory.set(mandatory);
        this.groupId.set(group);
        this.artifactId.set(artifact);
    }
}
