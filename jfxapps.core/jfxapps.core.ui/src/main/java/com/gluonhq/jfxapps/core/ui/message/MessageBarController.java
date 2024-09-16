/*
 * Copyright (c) 2016, 2024, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2024, Pascal Treilhes and/or its affiliates.
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

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageLogger.MessageEntry;
import com.gluonhq.jfxapps.core.ui.editor.messagelog.MessageLogEntry;

import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 *
 */
@ApplicationInstanceSingleton
public class MessageBarController extends AbstractFxmlController implements MessageBar{

    private static final Logger logger = LoggerFactory.getLogger(MessageBarController.class);

    private final MessageLogger messageLogger;
    private final MessagePopupController messagePopup;

    private final ImageView fileDirtyImage;

    private int previousTotalNumOfMessages = 0;
    private Tooltip statusLabelTooltip = null;


    @FXML
    private HBox messageBox;
    @FXML
    private Button messageButton;
    @FXML
    private Label messageLabel;
    @FXML
    private Label statusLabel;
    @FXML
    private StackPane selectionBarHost;
    @FXML
    private HBox messagePart;
    @FXML
    private HBox iconsHbox;

    public MessageBarController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            MessageLogger messageLogger,
            MessagePopupController messagePopupController
            ) {
        super(i18n, scenebuilderManager, documentManager, MessageBarController.class.getResource("MessageBar.fxml"));
        this.messageLogger = messageLogger;
        this.messagePopup = messagePopupController;

        // Initialize file dirty image
        final URL fileDirtyURL = MessageBarController.class.getResource("file-dirty.png");
        assert fileDirtyURL != null;
        fileDirtyImage = new ImageView(new Image(fileDirtyURL.toExternalForm()));
    }

    /*
     * Action Handlers
     */
    @FXML
    void onOpenCloseAction(ActionEvent e) {
//        if (messageWindowController == null) {
//            messageWindowController = new MessagePopupController(context);
//        }
        if (messagePopup.isWindowOpened()) {
            messagePopup.closeWindow();
        } else {
            messagePopup.openWindow(messageBox);
        }
    }
    /*
     * AbstractFxmlController
     */
    @Override
    public void controllerDidLoadFxml() {

        // Sanity checks
        assert messageBox != null;
        assert messageLabel != null;
        assert statusLabel != null;
        assert selectionBarHost != null;

        // Remove fake data
        messageLabel.setText(""); //NOCHECK
        statusLabel.setText(""); //NOCHECK
        messageButton.setVisible(false);

        // Listens to the message log
        messageLogger.revisionProperty().addListener((ov, t, t1) -> messageLogDidChange());
        messageLogger.numOfWarningMessagesProperty().addListener((ov, t, t1) -> {
            String numberOfMessages = Integer.toString(t1.intValue());
            if (t1.intValue() > 9) {
                numberOfMessages = "*"; // NOI18N
            }
            messageButton.setText(numberOfMessages);
        });

        statusLabelTooltip = statusLabel.getTooltip();

        // Update output components
        messageLogDidChange();
    }

    /*
     * Private
     */
    public void setDocumentDirty(boolean isDirty) {
        if (statusLabel != null) {
            if (isDirty) {
                statusLabel.setGraphic(fileDirtyImage);
                statusLabel.setTooltip(statusLabelTooltip);
            } else {
                statusLabel.setGraphic(null);
                statusLabel.setTooltip(null);
            }
        }
    }

    private void messageLogDidChange() {
        assert messageLabel != null;

        final MessageEntry entry = messageLogger.getYoungestEntry();
        int logSize = messageLogger.getEntries().size();

        // When an old message is dismissed the message log changes but there's
        // no need to display anything in the message bar.
        if (entry != null && logSize > previousTotalNumOfMessages) {
            // We mask the host
            HBox.setHgrow(messagePart, Priority.ALWAYS);
            selectionBarHost.setVisible(false);
            selectionBarHost.setManaged(false);
            messageLabel.setManaged(true);

            // Styling message area according severity
            setStyle(entry.getType());

            // If no message panel is defined nor in use we compute message bar
            // button. But as soon as a message panel is opened it means one or
            // more message is being displayed so we do not alter the message button.
            if (messagePopup == null || ! messagePopup.isWindowOpened()) {
                if (messageLogger.getWarningEntryCount() == 0) {
                    messageButton.setVisible(false);
                    messageButton.setManaged(false);
                } else {
                    messageButton.setVisible(true);
                    messageButton.setManaged(true);
                }
            }

            // Displaying the message
            messageLabel.setText(entry.getText());
            messageLabel.setVisible(true);

            // We go back to the host after a given time
            FadeTransition showHost = new FadeTransition(Duration.seconds(1), messagePart);
            showHost.setFromValue(1.0);
            showHost.setToValue(0.0);
            showHost.setDelay(Duration.seconds(3)); // DTL-5073
            showHost.setOnFinished(t -> {
                messageLabel.setVisible(false);
                messageLabel.setGraphic(null);
                messageLabel.setManaged(false);
                if (messageLogger.getWarningEntryCount() == 0) {
                    messageButton.setVisible(false);
                    messageButton.setManaged(false);
                }
                resetStyle();
                selectionBarHost.setManaged(true);
                selectionBarHost.setVisible(true);
                messagePart.setOpacity(1.0);
                HBox.setHgrow(messagePart, Priority.NEVER);
            });
            showHost.play();
        } else if (messageLogger.getEntryCount() == 0) {
            messageButton.setVisible(false);
            messageButton.setManaged(false);

            if (messagePopup != null && messagePopup.isWindowOpened()) {
                messagePopup.closeWindow();
            }
        }

        previousTotalNumOfMessages = logSize;
    }

    private void resetStyle() {
        // We clear all previous use, the sole way to control what's going on.
        messageLabel.getStyleClass().removeAll("message-info"); //NOCHECK
        messageLabel.getStyleClass().removeAll("message-warning"); //NOCHECK
        statusLabel.getStyleClass().removeAll("message-info"); //NOCHECK
        statusLabel.getStyleClass().removeAll("message-warning"); //NOCHECK
        iconsHbox.getStyleClass().removeAll("message-info"); //NOCHECK
        iconsHbox.getStyleClass().removeAll("message-warning"); //NOCHECK
    }

    private void setStyle(MessageLogEntry.Type type) {
        resetStyle();

        switch (type) {
            case INFO:
                messageLabel.getStyleClass().add("message-info"); //NOCHECK
                statusLabel.getStyleClass().add("message-info"); //NOCHECK
                iconsHbox.getStyleClass().add("message-info"); //NOCHECK
                break;
            case WARNING:
                messageLabel.getStyleClass().add("message-warning"); //NOCHECK
                statusLabel.getStyleClass().add("message-warning"); //NOCHECK
                iconsHbox.getStyleClass().add("message-warning"); //NOCHECK
                break;
            default:
                break;
        }
    }

    @Override
    public void setSelectionBar(Parent root) {
        if (root == null) {
            logger.warn("SelectionBar can't be set to null");
            return;
        }
        selectionBarHost.getChildren().add(root);
    }
}
