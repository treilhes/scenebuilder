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
package com.oracle.javafx.scenebuilder.editor.script.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.treilhes.jfxplace.core.api.i18n.I18N;
import com.treilhes.jfxplace.core.api.subjects.ApplicationEvents;
import com.treilhes.jfxplace.core.api.subjects.ApplicationInstanceEvents;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;
import com.treilhes.jfxplace.core.api.ui.controller.AbstractFxmlViewController;
import com.treilhes.jfxplace.core.api.ui.controller.dock.ViewSearch;
import com.treilhes.jfxplace.core.api.ui.controller.dock.annotation.ViewAttachment;
import com.treilhes.jfxplace.core.api.ui.controller.menu.ViewMenu;
import com.treilhes.jfxplace.fxom.api.document.DocumentTitleGenerator;
import com.treilhes.jfxplace.fxom.api.subjects.FxomEvents;
import com.treilhes.jfxplace.fxom.model.FXOMDocument;
import com.treilhes.jfxplace.fxom.model.pipeline.FXOMSerializer;

import eu.mihosoft.monacofx.MonacoFX;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.StackPane;

/**
 *
 */
// @formatter:off
@ApplicationInstanceSingleton
@ViewAttachment(
        name = ScriptEditorViewController.VIEW_NAME,
        id = ScriptEditorViewController.VIEW_ID,
        icon = "ViewIconSkeleton.png",
        iconX2 = "ViewIconSkeleton@2x.png")
// @formatter:on
public class ScriptEditorViewController extends AbstractFxmlViewController {

    public static final String VIEW_ID = "05c449b9-8669-4a75-93ee-43d250136f7c";
    public static final String VIEW_NAME = "view.name.controller.script.editor";

    @FXML
    ChoiceBox<SkeletonSettings.LANGUAGE> languageChoiceBox;
    @FXML
    CheckBox commentCheckBox;
    @FXML
    CheckBox formatCheckBox;
    @FXML
    StackPane stackPane;

    MonacoFX monacoFX;

    private FXOMDocument fxomDocument;
    private String documentName;
    private boolean dirty = true;
    private final ApplicationInstanceEvents documentManager;
    private final FxomEvents fxomEvents;
    private final DocumentTitleGenerator documentTitleGenerator;
    private final FXOMSerializer fxomSerializer;

    public ScriptEditorViewController(
            I18N i18n,
            ApplicationEvents scenebuilderManager,
            ApplicationInstanceEvents documentManager,
            FxomEvents fxomEvents,
            @Autowired MainInstanceWindow document,
            ViewMenu viewMenuController,
            FXOMSerializer fxomSerializer,
            DocumentTitleGenerator documentTitleGenerator) {
        super(i18n, scenebuilderManager, documentManager, viewMenuController, ScriptEditorViewController.class.getResource("ScriptEditor.fxml"));

        this.documentManager = documentManager;
        this.fxomEvents = fxomEvents;
        this.fxomSerializer = fxomSerializer;
        this.documentTitleGenerator = documentTitleGenerator;
    }

    private void setFxomDocument(FXOMDocument fxomDocument) {
        assert fxomDocument != null;
        this.fxomDocument = fxomDocument;
        this.documentName = documentTitleGenerator.makeTitle(fxomDocument);
        update();
    }
    @FXML
    private void onCopyAction(ActionEvent event) {
        final Map<DataFormat, Object> content = new HashMap<>();

        content.put(DataFormat.PLAIN_TEXT, monacoFX.getEditor().getDocument().getText());

        Clipboard.getSystemClipboard().setContent(content);
    }

    /*
     * AbstractFxmlWindowController
     */
    @Override
    public void controllerDidLoadFxml() {
        assert languageChoiceBox != null;
        assert commentCheckBox != null;
        assert formatCheckBox != null;
        assert stackPane != null;

        monacoFX = new MonacoFX();
        stackPane.getChildren().add(monacoFX);

        languageChoiceBox.getItems().addAll(SkeletonSettings.LANGUAGE.values());
        languageChoiceBox.getSelectionModel().select(SkeletonSettings.LANGUAGE.JAVA);
        languageChoiceBox.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) -> update());

        commentCheckBox.selectedProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> update());

        formatCheckBox.selectedProperty().addListener((ChangeListener<Boolean>) (ov, t, t1) -> update());

        fxomEvents.fxomDocument().subscribe(fx -> setFxomDocument(fx));
        fxomEvents.sceneGraphRevisionDidChange().subscribe(fx -> update());
    }

    private void updateTitle() {
        final String title = getI18n().getString("skeleton.window.title", documentName);
        setName(title);
    }

    private void update() {
        assert fxomDocument != null;

        // No need to eat CPU if the skeleton window isn't opened
        if (isVisible()) {
            updateTitle();
//            final SkeletonBuffer buf = new SkeletonBuffer(fxomDocument, documentName);
//
//            buf.setLanguage(languageChoiceBox.getSelectionModel().getSelectedItem());
//
//            if (commentCheckBox.isSelected()) {
//                buf.setTextType(SkeletonSettings.TEXT_TYPE.WITH_COMMENTS);
//            } else {
//                buf.setTextType(SkeletonSettings.TEXT_TYPE.WITHOUT_COMMENTS);
//            }
//
//            if (formatCheckBox.isSelected()) {
//                buf.setFormat(SkeletonSettings.FORMAT_TYPE.FULL);
//            } else {
//                buf.setFormat(SkeletonSettings.FORMAT_TYPE.COMPACT);
//            }

            try {
                var text = fxomSerializer.serialize(fxomDocument);
                monacoFX.getEditor().getDocument().setText(text);
                // use a predefined language like 'c'
                monacoFX.getEditor().setCurrentLanguage("xml");
                monacoFX.getEditor().setCurrentTheme("vs-dark");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            dirty = false;
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
