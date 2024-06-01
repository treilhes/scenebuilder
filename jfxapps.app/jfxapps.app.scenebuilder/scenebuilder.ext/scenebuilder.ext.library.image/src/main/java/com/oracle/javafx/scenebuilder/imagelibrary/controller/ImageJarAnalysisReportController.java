/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.imagelibrary.controller;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.application.ApplicationInstanceWindow;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.SceneBuilderManager;
import com.gluonhq.jfxapps.core.api.ui.controller.AbstractFxmlWindowController;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageLibrary;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageReport;
import com.oracle.javafx.scenebuilder.imagelibrary.library.ImageReportEntry;

import javafx.collections.ListChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
public class ImageJarAnalysisReportController extends AbstractFxmlWindowController {

    @FXML
    TextFlow textFlow;
    @FXML
    Label timestampLabel;

    private final ImageLibrary library;
    private final String TIMESTAMP_PATTERN = "h:mm a EEE d MMM. yyyy"; // NOI18N
    private final DateTimeFormatter TIMESTAMP_DATE_FORMAT = DateTimeFormatter.ofPattern(TIMESTAMP_PATTERN);
    private int prefixCounter = 0;
    private boolean dirty = false;

    public ImageJarAnalysisReportController(
            SceneBuilderManager sceneBuilderManager,
            IconSetting iconSetting,
            ImageLibrary library,
            ApplicationInstanceWindow document) {
        super(sceneBuilderManager, iconSetting, ImageJarAnalysisReportController.class.getResource("JarAnalysisReport.fxml"),
                I18N.getBundle(), document); // NOI18N
        this.library = library;
    }

    @FXML
    void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();
        StringBuilder sb = new StringBuilder();

        for (Node item : textFlow.getChildrenUnmodifiable()) {
            if (item instanceof Text) {
                sb.append(((Text) item).getText());
            }
        }

        content.put(DataFormat.PLAIN_TEXT, sb.toString());
        Clipboard.getSystemClipboard().setContent(content);
    }

    @Override
    public void onCloseRequest() {
        getStage().close();
    }

    @Override
    public void onFocus() {
    }

    @Override
    public void openWindow() {
        super.openWindow();

        if (dirty) {
            update();
        }
    }

    @Override
    protected void controllerDidCreateStage() {
        // Setup window title
        getStage().setTitle(I18N.getString("jar.analysis.report.title"));
    }

    @Override
    public void controllerDidLoadFxml() {
        assert textFlow != null;
        assert timestampLabel != null;

        library.getReports().addListener((ListChangeListener<ImageReport>) change -> update());

        update();
    }

    private void update() {
        // No need to eat CPU if the skeleton window isn't opened
        if (getStage().isShowing()) {
            textFlow.getChildren().clear();

            updateTimeStampLabel();

            for (ImageReport report : library.getReports()) {
                for (ImageReportEntry entry : report.getEntries()) {
                    if (entry.getStatus() != ImageReportEntry.Status.OK) {
                     // We use a Text instance for header and another one
                        // for full stack in order to style them separately
                        StringBuilder sb = new StringBuilder();
                        sb.append(getSectionPrefix()).append(I18N.getString("jar.analysis.exception"));
                        sb.append(" ").append(entry.getName()); // NOI18N
                        Text text = new Text();
                        text.setText(sb.toString());
                        text.getStyleClass().add("header"); // NOI18N
                        textFlow.getChildren().add(text);

                        StringBuilder sb2 = new StringBuilder();
                        sb2.append(getFullStack(entry.getException()));
                        Text text2 = new Text();
                        text2.setText(sb2.toString());
                        text2.getStyleClass().add("body"); // NOI18N
                        textFlow.getChildren().add(text2);
                    }
                }
            }

            dirty = false;
        } else {
            dirty = true;
        }
    }

    // The very first section must start on top, it is only for the next one we
    // need a separator.
    private String getSectionPrefix() {
        if (prefixCounter == 0) {
            prefixCounter++;
            return ""; // NOI18N
        } else {
            return "\n\n"; // NOI18N
        }
    }

    private StringBuilder getFullStack(Throwable t) {
        StringBuilder res = new StringBuilder("\n"); // NOI18N
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer, true));
        res.append(writer.getBuffer().toString());
        return res;
    }

    private void updateTimeStampLabel() {
        LocalDateTime date = library.getExplorationDate();
        String timestampValue = date.format(TIMESTAMP_DATE_FORMAT);
        timestampLabel.setText(I18N.getString("jar.analysis.report.timestamp", timestampValue));
    }
}
