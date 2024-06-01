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
package com.gluonhq.jfxapps.core.ui.message;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.DocumentManager;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlPanelController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger.MessageEntry;
import com.gluonhq.jfxapps.core.ui.editor.messagelog.MessageLogEntry;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 *
 */
@Prototype
public class MessagePanelController extends AbstractFxmlPanelController {

    private double panelWidth;

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridPane;
    @FXML private Button clearButton;

    private final MessageLogger messageLogger;

    public MessagePanelController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            MessageLogger messageLogger) {
        super(scenebuilderManager, documentManager, MessagePanelController.class.getResource("MessagePanel.fxml"), I18N.getBundle());
        this.messageLogger = messageLogger;
    }

    @FXML
    public void onClear(ActionEvent event) {
        messageLogger.clear();
    }

    public void setPanelWidth(double panelWidth) {
        this.panelWidth = panelWidth;
        if (scrollPane != null) {
            updateScrollPaneWidth();
        }
    }

    /*
     * AbstractFxmlPanelController
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert scrollPane != null;
        assert gridPane != null;
        assert clearButton != null;

        // Listens to the message log
        messageLogger.revisionProperty().addListener(
                (ChangeListener<Number>) (ov, t, t1) -> messageLogDidChange());

        updateScrollPaneWidth();
        messageLogDidChange();
    }



    /*
     * Private
     */

    private void messageLogDidChange() {
        assert gridPane != null;
        gridPane.getChildren().clear();
        int rowIndex = 0;
        int columnIndex = 0;
        for (MessageEntry mle : messageLogger.getEntries()) {
            if (mle.getType() == MessageLogEntry.Type.WARNING) {
                Button dismissButton = new Button("x"); //NOCHECK
                dismissButton.addEventHandler(MouseEvent.MOUSE_RELEASED, t -> messageLogger.clearEntry(mle));
                StackPane paneForButton = new StackPane();
                paneForButton.getChildren().add(dismissButton);
                paneForButton.setAlignment(Pos.CENTER_RIGHT);

                Label msgLabel = new Label(mle.getText());
                msgLabel.setTooltip(new Tooltip(mle.getText()));
                Label timestampLabel = new Label(mle.getTimestamp());
                timestampLabel.getStyleClass().add("timestamp"); //NOCHECK
                VBox labelBox = new VBox();
                labelBox.getChildren().addAll(timestampLabel, msgLabel);
                StackPane paneForLabel = new StackPane();
                paneForLabel.getChildren().add(labelBox);
                paneForLabel.setAlignment(Pos.CENTER_LEFT);

                gridPane.add(paneForLabel, columnIndex, rowIndex);
                columnIndex++;

                gridPane.add(paneForButton, columnIndex, rowIndex);
                columnIndex--;
                rowIndex++;
            }
        }
    }


    private void updateScrollPaneWidth() {
        assert scrollPane != null;
        scrollPane.setPrefWidth(panelWidth);
    }
}
