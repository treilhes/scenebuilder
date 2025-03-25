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
package com.gluonhq.jfxapps.app.manager.source.controller;

import java.util.Objects;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.source.model.Source;
import com.gluonhq.jfxapps.app.manager.source.model.SourceModelController;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.animation.Animation.Status;
import javafx.animation.RotateTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

@ApplicationInstancePrototype
public class SourceItemController extends AbstractFxmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceItemController.class);

    @FXML
    private Label titleLabel;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label versionLabel;

    @FXML
    private Label currentVersionLabel;

    @FXML
    private Rectangle rectImage;

    @FXML
    private Node syncGraphic;

    @FXML
    private Button deleteButton;

    @FXML
    private Button editButton;

    @FXML
    private Label errorLabel;

    @FXML
    private Tooltip errorTooltip;

    private final SourceModelController sourceModelController;

    private RotateTransition rotateTransition;

    private Runnable onStartEditing;

    private Consumer<Source> onValidate;

    private Runnable onCancel;

    private Source source;

    //@formatter:off
    protected SourceItemController(
            I18N i18n,
            SourceModelController sourceModelController,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, SourceItemController.class.getResource("SourceItem.fxml"));
        this.sourceModelController = sourceModelController;
    }

    @FXML
    public void initialize() {
        // Animation de rotation
        rotateTransition = new RotateTransition(Duration.seconds(2), syncGraphic);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(RotateTransition.INDEFINITE);
        rotateTransition.onFinishedProperty().set(e -> {
            syncGraphic.getTransforms().clear();
        });
    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(SourceItemController.class.getSimpleName());
    }

    public void setSource(Source source) {
        Objects.requireNonNull(source, "sourceItem must not be null");
        this.source = source;
        titleLabel.textProperty().bind(source.nameProperty());
        descriptionLabel.textProperty().bind(source.descriptionProperty());
        versionLabel.textProperty().bind(Bindings.createStringBinding(() -> {
            var version = source.versionProperty().get();
            return version == null || version.isBlank() ? SourceModelController.LATEST : version;
        }, source.versionProperty()));

        currentVersionLabel.textProperty().bind(source.currentVersionProperty());
        rectImage.fillProperty().bind(createImageBinding(source));

        errorLabel.visibleProperty().bind(source.errorProperty());
        errorTooltip.textProperty().bind(source.errorMessageProperty());

        deleteButton.disableProperty().bind(source.mandatoryProperty());
        editButton.disableProperty().bind(source.mandatoryProperty());
        source.updatingProperty().addListener((obs, ov, nv) -> {
            if (nv) {
                if (rotateTransition.getStatus() != Status.RUNNING) {
                    rotateTransition.play();
                }
            } else {
                rotateTransition.stop();
            }
        });
    }

    private ObjectBinding<ImagePattern> createImageBinding(Source sourceItem) {
        return Bindings.createObjectBinding(() -> {
            var image = sourceItem.imageProperty().get();
            if (image != null) {
                return new ImagePattern(new Image(image.toExternalForm()));
            }
            return null;
        }, sourceItem.imageProperty());
    }

    @FXML
    void startSync(ActionEvent event) {
        sourceModelController.launchUpdate(source);
    }

    @FXML
    void deleteSource(ActionEvent event) {
        sourceModelController.delete(source);
    }

    @FXML
    void editSource(ActionEvent event) {
        if (onStartEditing != null) {
            onStartEditing.run();
        }
    }

    public void setOnStartEditing(Runnable onStartEditing) {
        this.onStartEditing = onStartEditing;
    }

    public void setOnValidate(Consumer<Source> onValidate) {
        this.onValidate = onValidate;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public Source getSource() {
        return source;
    }

}
