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
package com.gluonhq.jfxapps.app.manager.mvnrepos.model;

import java.net.URL;

import com.gluonhq.jfxapps.boot.api.maven.Repository.Content;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryType;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Repository {

    private StringProperty name = new SimpleStringProperty();
    private StringProperty url = new SimpleStringProperty();
    private StringProperty login = new SimpleStringProperty();
    private StringProperty password = new SimpleStringProperty();
    private ObjectProperty<Content> contentType = new SimpleObjectProperty<>(Content.SNAPSHOT_RELEASE);
    private ObjectProperty<RepositoryType> repositoryType = new SimpleObjectProperty();
    private ObjectProperty<URL> image = new SimpleObjectProperty<>();

    private BooleanProperty mandatory = new SimpleBooleanProperty(false);
    private BooleanProperty error = new SimpleBooleanProperty(false);
    private StringProperty errorMessage = new SimpleStringProperty();

    private ObjectProperty<com.gluonhq.jfxapps.boot.api.maven.Repository> source = new SimpleObjectProperty<>();

    public Repository(com.gluonhq.jfxapps.boot.api.maven.Repository source) {
        this.sourceProperty().addListener((obs, ov, nv) -> {
            if (nv != null) {
                update(nv);
            }
        });
        this.source.set(source);
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty urlProperty() {
        return url;
    }

    public StringProperty loginProperty() {
        return login;
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public ObjectProperty<Content> contentTypeProperty() {
        return contentType;
    }

    public ObjectProperty<RepositoryType> repositoryTypeProperty() {
        return repositoryType;
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

    public ObjectProperty<com.gluonhq.jfxapps.boot.api.maven.Repository> sourceProperty() {
        return source;
    }

    public boolean match(String searchTerm) {

        if (searchTerm == null || searchTerm.isEmpty() || source.get() == null) {
            return true;
        }
        var data = source.get();
        var lowerCaseSearch = searchTerm.toLowerCase();
        var name = data.getName().toLowerCase();
        var url = data.getUrl().toLowerCase();

        return name.contains(lowerCaseSearch) || url.contains(lowerCaseSearch);

    }

    private void update(com.gluonhq.jfxapps.boot.api.maven.Repository source) {
        var id = source.getId();
        var name = source.getName();
        var login = source.getUser();
        var password = source.getPassword();
        var url = source.getUrl();
        var type = source.getType();
        var contentType = source.getContentType();

        //this.id.set(name);
        this.name.set(name);
        //this.description.set(description);
        this.login.set(login);
        this.password.set(password);
        this.url.set(url);
        //this.repositoryType.set(type);
        this.contentType.set(contentType);
        //this.mandatory.set(mandatory);
    }
}
