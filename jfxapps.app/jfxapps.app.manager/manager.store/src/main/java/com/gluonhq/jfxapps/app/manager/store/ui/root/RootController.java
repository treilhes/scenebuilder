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
package com.gluonhq.jfxapps.app.manager.store.ui.root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.store.action.StoreActionFactory;
import com.gluonhq.jfxapps.app.manager.store.model.Application;
import com.gluonhq.jfxapps.app.manager.store.ui.app.ApplicationDetailController;
import com.gluonhq.jfxapps.app.manager.store.ui.component.ApplicationItemController;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

@ApplicationInstanceSingleton
public class RootController extends AbstractFxmlController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RootController.class);

    @FXML
    private Button changeLogButton;

    @FXML
    private Label descriptionLabel;

    @FXML
    private Label errorLabel;

    @FXML
    private Tooltip errorTooltip;

    @FXML
    private ListView<Application> installedList;

    @FXML
    private ListView<Application> availablesList;

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

    private final RootModelControllerImpl modelController;

    private final JfxAppContext context;

    private FilteredList<Application> filteredAvailablesList;

    private FilteredList<Application> filteredInstalledList;

    private RootModel<Application, Application> model;

    private StoreActionFactory storeActionFactory;

    //private Consumer<Node> nextAction;

    //private Runnable backAction;

    //@formatter:off
    protected RootController(
            I18N i18n,
            ApplicationEvents applicationEvents,
            ApplicationInstanceEvents instanceEvents,
            ViewMenu viewMenu,
            RootModelControllerImpl rootModelController,
            JfxAppContext context,
            StoreActionFactory storeActionFactory) {
        //@formatter:on
        super(i18n, applicationEvents, instanceEvents, RootController.class.getResource("RootDetail.fxml"));

        this.modelController = rootModelController;
        this.context = context;
        this.storeActionFactory = storeActionFactory;
    }

    @FXML
    public void initialize() {

        availablesList.setCellFactory(l -> {
            return new ListCell<Application>() {
                @Override
                protected void updateItem(Application item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        var controller = context.getBean(ApplicationItemController.class);
                        controller.load(item, modelController);
                        setGraphic(controller.getRoot());
                    }
                }
            };
        });

        installedList.setCellFactory(l -> {
            return new ListCell<Application>() {
                @Override
                protected void updateItem(Application item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        var controller = context.getBean(ApplicationItemController.class);
                        controller.load(item, modelController);
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

    public void load() {
        model = modelController.load();

        filteredAvailablesList = new FilteredList<>(model.getAvailables(), data -> true);
        availablesList.setItems(filteredAvailablesList);

        filteredInstalledList = new FilteredList<>(model.getInstalled(), data -> true);
        installedList.setItems(filteredInstalledList);

        var item = model.getItem();

        bindAll(item.get());
    }

    private void bindAll(Application item) {
        titleLabel.textProperty().bind(item.nameProperty());
        descriptionLabel.textProperty().bind(item.descriptionProperty());
        versionLabel.textProperty().bind(item.versionProperty());
        rectImage.fillProperty().bind(createImageBinding(item));

        errorLabel.visibleProperty().bind(item.errorProperty());
        errorTooltip.textProperty().bind(item.errorMessageProperty());

        changeLogButton.disableProperty().bind(item.changeLogProperty().isEmpty());

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
    void updateItem(ActionEvent event) {
        modelController.update(model.getItem().get());
    }

    @FXML
    void showChangeLog(ActionEvent event) {

    }

    @FXML
    void view(ActionEvent event) {
        var item = model.getItem().get();

        var appController = context.getBean(ApplicationDetailController.class);
        appController.load(item, modelController);

        storeActionFactory.switchNext(appController.getRoot()).checkAndPerform();
    }

    @FXML
    void searchAvailables(ActionEvent event) {
        filteredAvailablesList.setPredicate(data -> data.match(searchAvailablesTextField.getText()));
    }

    @FXML
    void searchInstalled(ActionEvent event) {
        filteredInstalledList.setPredicate(data -> data.match(searchInstalledTextField.getText()));
    }

    private ObjectBinding<ImagePattern> createImageBinding(Application sourceItem) {
        return Bindings.createObjectBinding(() -> {
            var image = sourceItem.imageProperty().get();
            if (image != null) {
                return new ImagePattern(new Image(image.toExternalForm()));
            }
            return null;
        }, sourceItem.imageProperty());
    }

}
