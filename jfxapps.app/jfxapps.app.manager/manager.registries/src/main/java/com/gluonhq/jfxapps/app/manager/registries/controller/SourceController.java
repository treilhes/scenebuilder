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

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.app.manager.api.ui.Docks;
import com.gluonhq.jfxapps.app.manager.registries.model.Source;
import com.gluonhq.jfxapps.app.manager.registries.model.SourceModelController;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;

import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

@ApplicationInstanceSingleton
@ViewAttachment(name = "Registries", id = "ac6869c7-52e7-4a4b-8501-2681f31dc8e7", prefDockId = Docks.CENTER_DOCK_ID, openOnStart = false, selectOnStart = false, order = 5000, icon = "openapi_tool.png", iconX2 = "openapi_tool@2x.png")
public class SourceController extends AbstractFxmlViewController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SourceController.class);

    @FXML
    Label tmpLabel;

    @FXML
    TextField searchField;

    @FXML
    ListView<Source> sources;

    @FXML
    VBox rootVbox;

    @FXML
    StackPane sourceStack;

    FilteredList<Source> filteredList;

    private final JfxAppContext context;

    private final SourceModelController sourceModelController;

    private FxmlController currentEdit;

    private final static GaussianBlur EDIT_BLUR = new GaussianBlur(60);

    //@formatter:off
    protected SourceController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            ViewMenu viewMenu,
            SourceModelController sourceModelController,
            JfxAppContext context) {
        //@formatter:on
        super(i18n, scenebuilderManager, documentManager, viewMenu, SourceController.class.getResource("Source.fxml"));

        this.context = context;
        this.sourceModelController = sourceModelController;

    }

    @FXML
    public void initialize() {
        sources.setSelectionModel(null);
        sources.setCellFactory(l -> new ListCell<Source>() {
            @Override
            protected void updateItem(Source item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    var controller = context.getBean(SourceItemController.class);
                    controller.setSource(item);
                    controller.setOnStartEditing(() -> {
                        LOGGER.info("Adding new source");

                        final var editController = context.getBean(EditSourceItemController.class);

                        startEdition(editController);

                        editController.setOnValidate(source -> {
                            if (sourceModelController.update(controller.getSource(), source)) {
                                endEdition(editController);
                            }
                        });

                        editController.setOnCancel(() -> {
                            endEdition(editController);
                        });

                        editController.setSource(controller.getSource());
                    });
                    setGraphic(controller.getRoot());
                }
            }
        });
    }

    @Override
    public void controllerDidLoadFxml() {
        getRoot().setId(SourceController.class.getSimpleName());
        getRoot().minWidth(400.0);
        getRoot().minHeight(400.0);
    }

    @Override
    public void onShow() {
        sourceModelController.load();
        filteredList = new FilteredList<>(sourceModelController.getSources(), data -> true);
        sources.setItems(filteredList);
    }

    @Override
    public void onHidden() {
        if (currentEdit != null) {
            endEdition(currentEdit);
        }
    }

    @FXML
    void search(ActionEvent event) {
        filteredList.setPredicate(data -> data.match(searchField.getText()));
        // getI18n().getString("manager.source.results")
        if (getI18n().getLocale() == Locale.ENGLISH) {
            getI18n().changeLocale(Locale.FRENCH);
        } else {
            getI18n().changeLocale(Locale.ENGLISH);
        }

    }

    @FXML
    void addSource(ActionEvent event) {
        LOGGER.info("Adding new source");

        final var editController = context.getBean(EditSourceItemController.class);
        startEdition(editController);

        editController.setOnValidate(source -> {
            if (sourceModelController.create(source)) {
                endEdition(editController);
            }
        });

        editController.setOnCancel(() -> {
            endEdition(editController);
        });
    }

    private void startEdition(FxmlController editController) {
        this.currentEdit = editController;
        rootVbox.setEffect(EDIT_BLUR);
        rootVbox.setMouseTransparent(true);
        sourceStack.getChildren().add(editController.getRoot());
    }

    private void endEdition(FxmlController editController) {
        sourceStack.getChildren().remove(editController.getRoot());
        rootVbox.setEffect(null);
        rootVbox.setMouseTransparent(false);
        this.currentEdit = null;
    }

    @FXML
    void syncAllSource(ActionEvent event) {

    }
}
