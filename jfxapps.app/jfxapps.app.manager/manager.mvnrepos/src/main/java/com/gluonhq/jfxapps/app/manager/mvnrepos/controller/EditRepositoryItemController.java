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
package com.gluonhq.jfxapps.app.manager.mvnrepos.controller;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.mvnrepos.model.Repository;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.boot.api.maven.Repository.Content;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryType;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

@ApplicationInstancePrototype
public class EditRepositoryItemController extends AbstractFxmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EditRepositoryItemController.class);

    @FXML
    private TextField name;

    @FXML
    private TextField url;

    @FXML
    private TextField login;

    @FXML
    private PasswordField password;

    @FXML
    private ComboBox<RepositoryType> repositoryType;

    @FXML
    private ComboBox<Content> contentType;

    @FXML
    private Label errorLabel;

    @FXML
    private Button validate;

    @FXML
    private Button cancel;

    private Consumer<Repository> onValidate;

    private Runnable onCancel;

    private Repository source;

    private final RepositoryClient mavenClient;

    private final List<RepositoryType> repositoryTypes;

    //@formatter:off
    protected EditRepositoryItemController(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents instanceEvents,
            RepositoryClient mavenClient,
            List<RepositoryType> repositoryTypes) {
        //@formatter:on
        super(i18n, applicationEvents, instanceEvents, EditRepositoryItemController.class.getResource("EditRepositoryItem.fxml"));
        this.mavenClient = mavenClient;
        this.repositoryTypes = repositoryTypes;
    }

    @FXML
    public void initialize() {
        LOGGER.info("initialize EditRepositoryItemController");

        repositoryType.setItems(FXCollections.observableArrayList(repositoryTypes));
        repositoryType.getSelectionModel().selectFirst();

        contentType.setItems(FXCollections.observableArrayList(Content.values()));
        contentType.getSelectionModel().selectFirst();

        //validate.disableProperty().bind(Bindings.isEmpty(name.textProperty()).or(Bindings.isEmpty(url.textProperty())));

    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(EditRepositoryItemController.class.getSimpleName());
    }

    public Repository getSource() {
        return source;
    }

    public void setSource(Repository source) {
        Objects.requireNonNull(source, "sourceItem must not be null");
        this.source = source;

        var sourceName = source.nameProperty().get();
        var sourceUrl = source.urlProperty().get();
        var sourceLogin = source.loginProperty().get();
        var sourcePassword = source.passwordProperty().get();
        var sourceContentType = source.contentTypeProperty().get();
        var sourceType = source.repositoryTypeProperty().get();

        name.setText(sourceName);
        url.setText(sourceUrl);
        login.setText(sourceLogin);
        password.setText(sourcePassword);
        contentType.getSelectionModel().select(sourceContentType);
        repositoryType.getSelectionModel().select(sourceType);

        errorLabel.textProperty().bind(source.errorMessageProperty());
    }

    public void setOnValidate(Consumer<Repository> onValidate) {
        this.onValidate = onValidate;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    @FXML
    void validate(ActionEvent event) {
        var sourceRepository = source == null ? null : source.sourceProperty().get();

        var name = this.name.getText();
        var url = this.url.getText();
        var login = this.login.getText();
        var password = this.password.getText();
        var contentType = this.contentType.getSelectionModel().getSelectedItem();
        var repositoryType = this.repositoryType.getSelectionModel().getSelectedItem();

        var source = new Repository(sourceRepository);
        source.nameProperty().set(name);
        source.urlProperty().set(url);
        source.loginProperty().set(login);
        source.passwordProperty().set(password);
        source.contentTypeProperty().set(contentType);
        source.repositoryTypeProperty().set(repositoryType);

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

}
