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
package com.oracle.javafx.scenebuilder.sourceview.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.graalvm.compiler.lir.CompositeValue.Component;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;

import com.gluonhq.jfxapps.core.fs.preference.global.WildcardImportsPreference;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.api.SbEditor;
import com.oracle.javafx.scenebuilder.api.ui.AbstractFxmlViewController;
import com.oracle.javafx.scenebuilder.api.ui.ViewMenuController;
import com.oracle.javafx.scenebuilder.api.ui.dock.ViewSearch;
import com.oracle.javafx.scenebuilder.api.ui.dock.annotation.ViewAttachment;
import com.oracle.javafx.scenebuilder.api.util.FXOMDocumentUtils;

import io.reactivex.rxjavafx.schedulers.JavaFxScheduler;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.util.Duration;

/**
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_DOCUMENT)
@Lazy
@ViewAttachment(name = SourceViewWindowController.VIEW_NAME, id = SourceViewWindowController.VIEW_ID,
    icon = "ViewIconSource.png", iconX2 = "ViewIconSource@2x.png")
public class SourceViewWindowController extends AbstractFxmlViewController {

    public final static String VIEW_ID = "d7e4ec15-eabc-4e0c-a9b9-49ed9bb05eed";
    public final static String VIEW_NAME = "view.name.fxml.source";

    private final static Logger logger = LoggerFactory.getLogger(SourceViewWindowController.class);

    @FXML
    TextArea textArea;

    @FXML
    Label updateResultLabel;

    private FXOMDocument fxomDocument;
    private String documentName;
    private final FxmlDocumentManager documentManager;
    private final WildcardImportsPreference wildcardImportsPreference;

    private final SbEditor editor;

    private double scrollLeftSave;
    private double scrollTopSave;
    private FadeTransition labelFadeTransition;
    private boolean dirty = true;

    public SourceViewWindowController(
            SceneBuilderManager scenebuilderManager,
            FxmlDocumentManager documentManager,
            @Autowired SbEditor editor,
            @Autowired WildcardImportsPreference wildcardImportsPreference,
            ViewMenuController viewMenuController) {
        super(scenebuilderManager, documentManager, viewMenuController, SourceViewWindowController.class.getResource("SourceWindow.fxml"), I18N.getBundle());

        this.documentManager = documentManager;
        this.editor = editor;
        this.wildcardImportsPreference = wildcardImportsPreference;
    }

    private void setupFadeTransition() {
        labelFadeTransition = new FadeTransition(Duration.millis(3000), updateResultLabel);
        labelFadeTransition.setFromValue(1.0);
        labelFadeTransition.setToValue(0.0);
        labelFadeTransition.setCycleCount(1);
        labelFadeTransition.setAutoReverse(false);
    }

    private void setFxomDocument(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        this.fxomDocument = fxomDocument;
        this.documentName = FXOMDocumentUtils.makeTitle(fxomDocument);
        update();
    }
    @FXML
    private void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        if (textArea.getSelection().getLength() == 0) {
            content.put(DataFormat.PLAIN_TEXT, textArea.getText());
        } else {
            content.put(DataFormat.PLAIN_TEXT, textArea.getSelectedText());
        }

        Clipboard.getSystemClipboard().setContent(content);
    }

    @FXML
    private void onUpdateAction(ActionEvent event) {
        try {
            scrollLeftSave = textArea.getScrollLeft();
            scrollTopSave = textArea.getScrollTop();

            updateResultLabel.setText("");

            String fxmlText = textArea.getText();
            editor.setFxmlTextAndLocation(fxmlText, fxomDocument.getLocation(), false);

            updateResultLabel.setOpacity(1.0);
            updateResultLabel.setText("SUCCESS!");
            labelFadeTransition.play();

        } catch (IOException e) {
            logger.error("Unable to update fxomDocument", e);
            updateResultLabel.setOpacity(1.0);
            updateResultLabel.setText(concatenateStackMessage(e));
        }
    }

    private String concatenateStackMessage(Exception e) {
        StringBuilder builder = new StringBuilder();
        if (e != null) {
            builder.append(e.getMessage());
            Throwable cause = e;
            while (cause.getCause() != null) {
                cause = cause.getCause();

                if (cause.getMessage() != null && !cause.getMessage().isBlank()) {
                    builder.append("\n").append(cause.getMessage());
                }
            }
        }
        return builder.toString();
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        assert textArea != null;
        setupFadeTransition();
        documentManager.fxomDocument().subscribe(fx -> setFxomDocument(fx));
        documentManager.sceneGraphRevisionDidChange().observeOn(JavaFxScheduler.platform()).subscribe(fx -> {
            scrollLeftSave = textArea.getScrollLeft();
            scrollTopSave = textArea.getScrollTop();
            update();
        });
    }

    private void updateTitle() {
        final String title = I18N.getString("sourceview.window.title", documentName);
        setName(title);
    }

    private void update() {
        assert fxomDocument != null;

        // No need to eat CPU if the skeleton window isn't opened
        if (isVisible()) {
            updateTitle();
            String fxml = fxomDocument.getFxmlText(wildcardImportsPreference.getValue());
            textArea.setText(fxml);

            SbPlatform.runLater(() -> {
                textArea.setScrollLeft(scrollLeftSave);
                textArea.setScrollTop(scrollTopSave);
            });
            documentManager.dirty().set(true);
            dirty  = false;
        } else {
            dirty = true;
        }
    }

    @Override
    public ViewSearch getSearchController() {
        return null;
    }

    @Override
    public void onShow() {
        if (dirty) {
            update();
        }
    }

    @Override
    public void onHidden() {
    }


}
