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
package com.gluonhq.jfxapps.app.manager.main.model;

import java.util.UUID;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Extension {

    private ObjectProperty<UUID> id = new SimpleObjectProperty<>();
    private StringProperty name = new SimpleStringProperty();
    private StringProperty version = new SimpleStringProperty();
    private StringProperty licence = new SimpleStringProperty();
    private StringProperty description = new SimpleStringProperty();
    private ObservableList<Extension> extensions = FXCollections.observableArrayList();

    public final ObjectProperty<UUID> idProperty() {
        return this.id;
    }

    public final UUID getId() {
        return this.idProperty().get();
    }

    public final void setId(final UUID id) {
        this.idProperty().set(id);
    }

    public final StringProperty nameProperty() {
        return this.name;
    }

    public final String getName() {
        return this.nameProperty().get();
    }

    public final void setName(final String name) {
        this.nameProperty().set(name);
    }

    public final StringProperty versionProperty() {
        return this.version;
    }

    public final String getVersion() {
        return this.versionProperty().get();
    }

    public final void setVersion(final String version) {
        this.versionProperty().set(version);
    }

    public final StringProperty licenceProperty() {
        return this.licence;
    }

    public final String getLicence() {
        return this.licenceProperty().get();
    }

    public final void setLicence(final String licence) {
        this.licenceProperty().set(licence);
    }

    public final StringProperty descriptionProperty() {
        return this.description;
    }

    public final String getDescription() {
        return this.descriptionProperty().get();
    }

    public final void setDescription(final String description) {
        this.descriptionProperty().set(description);
    }

    public ObservableList<Extension> getExtensions() {
        return extensions;
    }

}
