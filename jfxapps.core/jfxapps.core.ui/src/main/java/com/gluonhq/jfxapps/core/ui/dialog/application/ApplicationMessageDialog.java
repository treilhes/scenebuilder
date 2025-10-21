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
package com.gluonhq.jfxapps.core.ui.dialog.application;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractApplicationUiController;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow;
import com.gluonhq.jfxapps.core.api.ui.dialog.ModalWindow.ButtonID;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 * A modal dialog which displays a piece of text and provides a Close button
 * and a Copy button.
 *
 *
 */
@ApplicationSingleton
public class ApplicationMessageDialog extends AbstractApplicationUiController {

    @FXML
    private ListView<ApplicationMessage> listView;

    @FXML
    private TextArea textArea;

    private ObservableList<ApplicationMessage> messages = FXCollections.observableArrayList();

    private final JfxAppContext context;

    private final JfxAppPlatform jfxAppPlatform;

    private final ModalWindow modalWindow;

    protected ApplicationMessageDialog(
            JfxAppPlatform jfxAppPlatform,
            I18N i18n,
            ApplicationEvents sceneBuilderManager,
            JfxAppContext context,
            ModalWindow modalWindow) {
        super(i18n, sceneBuilderManager, ApplicationMessageDialog.class.getResource("ApplicationMessageDialog.fxml"));
        this.context = context;
        this.jfxAppPlatform = jfxAppPlatform;
        this.modalWindow = modalWindow;
    }

    private void setupListView() {
        listView.setCellFactory(message -> new ListCell<ApplicationMessage>() {
            @Override
            protected void updateItem(ApplicationMessage item, boolean empty) {
                super.updateItem(item, empty);
                setText(null);
                if (item != null && !empty) {
                    var controller = context.getBean(ApplicationMessageController.class);
                    controller.setMessage(item);
                    setGraphic(controller.getRoot());
                } else {
                    setGraphic(null);
                }
            }
        });
        listView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.cause() != null) {
                var sw = new StringWriter();
                newValue.cause().printStackTrace(new PrintWriter(sw));
                setText(sw.toString());
            } else {
                setText(null);
            }
        });
        listView.setItems(messages);
    }


    private void setupDialog() {
        modalWindow.setTitle(getI18n().getString("label.close"));
        modalWindow.setActionButtonTitle(getI18n().getStringOrDefault("action.copy","action.copy"));
        modalWindow.setActionButtonVisible(true);
        modalWindow.setActionButtonDisable(true);
        modalWindow.setOKButtonVisible(false);
        modalWindow.setCancelButtonTitle("cancel");
        modalWindow.setImageViewVisible(false);
        modalWindow.setShowDefaultButton(true);
        modalWindow.setDefaultButtonID(ButtonID.CANCEL);
        modalWindow.setButtonsFocusTraversable();

        modalWindow.setOnOkButtonPressed(this::okButtonPressed);
        modalWindow.setOnCancelButtonPressed(this::cancelButtonPressed);
        modalWindow.setOnActionButtonPressed(this::actionButtonPressed);
    }

    protected void okButtonPressed(ActionEvent e) {
        // Should not be called because ok button is hidden
        throw new IllegalStateException();
    }

    protected void cancelButtonPressed(ActionEvent e) {
        messages.clear();
        modalWindow.close();
    }

    protected void actionButtonPressed(ActionEvent e) {
        final Map<DataFormat, Object> content = new HashMap<>();
        content.put(DataFormat.PLAIN_TEXT, getText());
        Clipboard.getSystemClipboard().setContent(content);
    }

    public void setText(String text) {
        var hasText = text != null && !text.isEmpty();
        textArea.setManaged(hasText);
        textArea.setVisible(hasText);
        textArea.setText(text);
    }

    public String getText() {
        return textArea.getText();
    }

    public void showMessage(ApplicationMessage applicationMessage) {
        jfxAppPlatform.runOnFxThread(() -> {
            messages.add(applicationMessage);
            modalWindow.show();
        });
    }

    @Override
    public void controllerDidLoadFxml() {
        assert listView != null;
        assert textArea != null;
        setupListView();

        jfxAppPlatform.runOnFxThread(() -> {
            setupDialog();
            setText(null);
        });

        modalWindow.setContent(this.getRoot());
    }
}
