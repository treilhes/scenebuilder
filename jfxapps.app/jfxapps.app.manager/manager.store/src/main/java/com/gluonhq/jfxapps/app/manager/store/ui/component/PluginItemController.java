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
package com.gluonhq.jfxapps.app.manager.store.ui.component;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.store.action.StoreActionFactory;
import com.gluonhq.jfxapps.app.manager.store.model.Plugin;
import com.gluonhq.jfxapps.app.manager.store.model.PluginController;
import com.gluonhq.jfxapps.app.manager.store.ui.plugin.PluginDetailController;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

@ApplicationInstancePrototype
public class PluginItemController extends AbstractFxmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginItemController.class);

    @FXML
    private Button changeLogButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Button installButton;

    @FXML
    private Rectangle rectImage;

    @FXML
    private Label titleLabel;

    @FXML
    private Button uninstallButton;

    @FXML
    private Label versionLabel;

    @FXML
    private Button viewButton;

    private final StoreActionFactory storeActionFactory;

    private Plugin item;

    private JfxAppContext context;

    private PluginController modelController;

    //@formatter:off
    protected PluginItemController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            JfxAppContext context,
            StoreActionFactory storeActionFactory) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, PluginItemController.class.getResource("PluginItem.fxml"));
        this.context = context;
        this.storeActionFactory = storeActionFactory;
    }

    @FXML
    public void initialize() {

    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(PluginItemController.class.getSimpleName());
    }

    public void load(Plugin applicationItem, PluginController modelController) {
        Objects.requireNonNull(applicationItem, "sourceItem must not be null");
        this.item = applicationItem;
        this.modelController = modelController;

        titleLabel.textProperty().bind(applicationItem.nameProperty());
        descriptionLabel.textProperty().bind(applicationItem.descriptionProperty());
        versionLabel.textProperty().bind(applicationItem.versionProperty());
        rectImage.fillProperty().bind(createImageBinding(applicationItem));

        installButton.visibleProperty().bind(applicationItem.installedProperty().not());
        uninstallButton.visibleProperty().bind(applicationItem.installedProperty());
        installButton.managedProperty().bind(applicationItem.installedProperty().not());
        uninstallButton.managedProperty().bind(applicationItem.installedProperty());
    }

    @FXML
    void install(ActionEvent event) {
        modelController.install(item);
    }

    @FXML
    void showChangeLog(ActionEvent event) {

    }

    @FXML
    void uninstall(ActionEvent event) {
        modelController.uninstall(item);
    }

    @FXML
    void view(ActionEvent event) {
        var appController = context.getBean(PluginDetailController.class);
        appController.load(item, modelController);
        storeActionFactory.switchNext(appController.getRoot()).checkAndPerform();
    }

    private ObjectBinding<ImagePattern> createImageBinding(Plugin applicationItem) {
        return Bindings.createObjectBinding(() -> {
            var image = applicationItem.imageProperty().get();
            if (image != null) {
                return new ImagePattern(new Image(image.toExternalForm()));
            }
            return null;
        }, applicationItem.imageProperty());
    }

}
