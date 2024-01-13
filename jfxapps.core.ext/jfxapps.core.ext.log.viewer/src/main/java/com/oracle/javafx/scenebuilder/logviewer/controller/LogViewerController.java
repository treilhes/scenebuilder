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
package com.oracle.javafx.scenebuilder.logviewer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.oracle.javafx.scenebuilder.api.di.SbPlatform;
import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.subjects.SceneBuilderManager;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.api.ui.ViewMenuController;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewSearch;
import com.oracle.javafx.scenebuilder.api.ui.dock.annotation.ViewAttachment;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;

/**
 *
 */
@ApplicationInstanceSingleton
@ViewAttachment(name = LogViewerController.VIEW_NAME, id = LogViewerController.VIEW_ID,
    icon = "ViewIconLog.png", iconX2 = "ViewIconLog@2x.png")
public class LogViewerController extends AbstractFxmlViewController {

    public final static String VIEW_ID = "f0769ce0-08cd-463f-bf6f-c65c96f6c6d0";
    public final static String VIEW_NAME = "view.name.log.viewer";
    public final static int MAX_LINES = 10000;
    private LogReader logReader = new LogReader();

    @FXML
    ListView<String> logs;

    public LogViewerController(
            SceneBuilderManager scenebuilderManager,
            DocumentManager documentManager,
            ViewMenuController viewMenuController) {
        super(scenebuilderManager, documentManager, viewMenuController, LogViewerController.class.getResource("LogViewerWindow.fxml"), I18N.getBundle());

    }

    @FXML
    private void initialize() {
        logs.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        logReader.initialize(this::update, MAX_LINES);
    }

    @FXML
    private void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (logs.getSelectionModel().isEmpty()) {
            content.put(DataFormat.PLAIN_TEXT, logs.getItems().stream().collect(Collectors.joining("\n")));
        } else {
            content.put(DataFormat.PLAIN_TEXT,
                    logs.getSelectionModel().getSelectedItems().stream().collect(Collectors.joining("\n")));
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        assert logs != null;
        updateTitle();
    }

    private void updateTitle() {
        final String title = "logfile";//;I18N.getString("sourceview.window.title", documentName);
        setName(title);
    }

    private void update(List<String> newLines) {
        int numLines = logs.getItems().size();
        int numNewLines = newLines.size();
        int toDelete = numLines + numNewLines - MAX_LINES;

        SbPlatform.runOnFxThread(() -> {
            if (toDelete > 0) {
                logs.getItems().remove(0, toDelete);
            }
            if (numNewLines > 0) {
                logs.getItems().addAll(newLines);
            }
            int index = logs.getItems().size() - 1;
            logs.scrollTo(index);
        });

    }

    @Override
    public ViewSearch getSearchController() {
        return null;
    }

    @Override
    public void onShow() {
        logReader.startUpdateThread();
    }

    @Override
    public void onHidden() {
        logReader.stopUpdateThread();
        logs.getItems().clear();
    }

}