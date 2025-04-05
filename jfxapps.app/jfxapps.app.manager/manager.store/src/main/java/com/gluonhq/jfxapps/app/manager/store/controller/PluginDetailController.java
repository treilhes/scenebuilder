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
package com.gluonhq.jfxapps.app.manager.store.controller;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.store.model.Plugin;
import com.gluonhq.jfxapps.app.manager.store.model.PluginController;
import com.gluonhq.jfxapps.app.manager.store.model.PluginModelControllerImpl;
import com.gluonhq.jfxapps.app.manager.store.model.RootModel;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

@ApplicationInstancePrototype
public class PluginDetailController extends AbstractFxmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginDetailController.class);

    @FXML
    private Button backButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button launchButton;

    @FXML
    private Button changeLogButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Tooltip errorTooltip;

    @FXML
    private ListView<Plugin> installedList;

    @FXML
    private ListView<Plugin> availablesList;

    @FXML
    private Label typeLabel;

    @FXML
    private Rectangle rectImage;

    @FXML
    private VBox rootVbox;

    @FXML
    private TextField searchAvailablesTextField;

    @FXML
    private TextField searchInstalledTextField;

    @FXML
    private Label titleLabel;

    @FXML
    private Button updateButton;

    @FXML
    private Tooltip updateTooltip;

    @FXML
    private Label versionLabel;

    private final PluginModelControllerImpl applicationModelController;

    private final JfxAppContext context;

    private FilteredList<Plugin> filteredAvailablesList;

    private FilteredList<Plugin> filteredInstalledList;

    private RootModel<Plugin, Plugin> model;

    private Consumer<Node> nextAction;

    private Runnable backAction;

    private PluginController modelController;

    //@formatter:off
    protected PluginDetailController(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents instanceEvents,
            PluginModelControllerImpl modelController,
            JfxAppContext context) {
        //@formatter:on
        super(i18n, applicationEvents, instanceEvents, RootController.class.getResource("ApplicationDetail.fxml"));

        this.applicationModelController = modelController;
        this.context = context;
    }

    @FXML
    public void initialize() {

        availablesList.setCellFactory(l -> {
            return new ListCell<Plugin>() {
                @Override
                protected void updateItem(Plugin item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        var controller = context.getBean(PluginItemController.class);
                        controller.onNext(nextAction);
                        controller.onBack(backAction);
                        controller.load(item, applicationModelController);
                        setGraphic(controller.getRoot());
                    }
                }
            };
        });

        installedList.setCellFactory(l -> {
            return new ListCell<Plugin>() {
                @Override
                protected void updateItem(Plugin item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        var controller = context.getBean(PluginItemController.class);
                        controller.onNext(nextAction);
                        controller.onBack(backAction);
                        controller.load(item, applicationModelController);
                        setGraphic(controller.getRoot());
                    }
                }
            };
        });
    }


    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(RootController.class.getSimpleName());
        getRoot().minWidth(400.0);
        getRoot().minHeight(400.0);
    }

    public void load(Plugin application, PluginController parentModelController) {
        this.modelController = parentModelController;
        model = applicationModelController.load(application);

        filteredAvailablesList = new FilteredList<>(model.getAvailables(), data -> true);
        availablesList.setItems(filteredAvailablesList);

        filteredInstalledList = new FilteredList<>(model.getInstalled(), data -> true);
        installedList.setItems(filteredInstalledList);

        var item = model.getItem();

        bindAll(item.get());
    }

//    @Override
//    public void onHidden() {
//        if (subscription != null) {
//            subscription.unsubscribe();
//        }
//    }

    private void bindAll(Plugin item) {
        titleLabel.textProperty().bind(item.nameProperty());
        descriptionLabel.textProperty().bind(item.descriptionProperty());
        versionLabel.textProperty().bind(item.versionProperty());
        rectImage.fillProperty().bind(createImageBinding(item));

        errorLabel.visibleProperty().bind(item.errorProperty());
        errorTooltip.textProperty().bind(item.errorMessageProperty());

        deleteButton.disableProperty()
        .bind(Bindings.createBooleanBinding(() -> !item.installedProperty().get(), item.installedProperty()));

        launchButton.disableProperty()
        .bind(Bindings.createBooleanBinding(() -> !item.installedProperty().get(), item.installedProperty()));

        changeLogButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            var value = item.changeLogProperty().get();
            return value == null || value.isBlank();
        }, item.installedProperty()));

        updateButton.disableProperty().bind(Bindings.createBooleanBinding(() -> {
            var installed = item.installedProperty().get();
            var nextVersion = item.nextVersionProperty().get();
            var version = item.versionProperty().get();
            var hasUpdate = nextVersion != null && !nextVersion.isBlank() && !nextVersion.equals(version);
            return !installed || !hasUpdate;
        }, item.installedProperty(), item.nextVersionProperty()));

        updateTooltip.textProperty().bind(item.nextVersionProperty());
    }

    @FXML
    void goBack(ActionEvent event) {
        if (backAction != null) {
            backAction.run();
        }
    }

    @FXML
    void updateItem(ActionEvent event) {
        modelController.update(model.getItem().get());
    }

    @FXML
    void deleteItem(ActionEvent event) {
        modelController.uninstall(model.getItem().get());
        if (backAction != null) {
            backAction.run();
        }
    }

    @FXML
    void showChangeLog(KeyEvent event) {

    }

    @FXML
    void launch(KeyEvent event) {
        new IllegalArgumentException("Nothing to launch here");
    }

    @FXML
    void view(ActionEvent event) {
        new IllegalArgumentException("Nothing to view here");
    }

    @FXML
    void searchAvailables(ActionEvent event) {
        filteredAvailablesList.setPredicate(data -> data.match(searchAvailablesTextField.getText()));
    }

    @FXML
    void searchInstalled(ActionEvent event) {
        filteredInstalledList.setPredicate(data -> data.match(searchInstalledTextField.getText()));
    }

    private ObjectBinding<ImagePattern> createImageBinding(Plugin sourceItem) {
        return Bindings.createObjectBinding(() -> {
            var image = sourceItem.imageProperty().get();
            if (image != null) {
                return new ImagePattern(new Image(image.toExternalForm()));
            }
            return null;
        }, sourceItem.imageProperty());
    }

    public void onBack(Runnable backAction) {
        this.backAction = backAction;
    }

    public void onNext(Consumer<Node> nextAction) {
        this.nextAction = nextAction;
    }
}
