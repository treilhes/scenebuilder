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
package com.oracle.javafx.scenebuilder.core.editors;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.api.FileSystem;
import com.oracle.javafx.scenebuilder.api.Glossary;
import com.oracle.javafx.scenebuilder.api.MessageLogger;
import com.oracle.javafx.scenebuilder.api.di.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.api.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMFxIdIndex;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * fx:id editor.
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class FxIdEditor extends AutoSuggestEditor {

    private static final String PROPERTY_NAME = "fx:id";
    private static final String DEFAULT_VALUE = null;
    //private Editor editorController;
    private final MessageLogger messageLog;
    private final Glossary glossary;
    private final DocumentManager documentManager;

//    public FxIdEditor(List<String> suggestedFxIds, Editor editorController) {
//        super(PROPERTY_NAME, DEFAULT_VALUE, suggestedFxIds); //NOCHECK
//        initialize(editorController);
//    }

    public FxIdEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            Glossary glossary,
            DocumentManager documentManager,
            MessageLogger messageLogger) {
        super(dialog, documentation, fileSystem);
        this.messageLog = messageLogger;
        this.glossary = glossary;
        this.documentManager = documentManager;

        preInit(Type.ALPHA, new ArrayList<>());
        initialize();
    }

    private void initialize() {
        // text field events handling
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            String value = textField.getText();
            if (value != null && !value.isEmpty()) {
                if (!JavaLanguage.isIdentifier(value)) {
//                        System.err.println(I18N.getString("log.warning.invalid.fxid", value));
                    handleInvalidValue(value);
                    return;
                }
                if (isValueChanged(value)) {
                    // Avoid multiple identical messages
                    if (getFxIdsInUse().contains(value)) {
                        messageLog.logWarningMessage(
                                "log.warning.duplicate.fxid", value);
                    } else if ((getControllerClass() != null) && !getSuggestedList().contains(value)) {
                        messageLog.logWarningMessage(
                                "log.warning.no.injectable.fxid", value);
                    }
                }
            }
            userUpdateValueProperty((value == null || value.isEmpty()) ? null : value);
            textField.selectAll();
        };
        setTextEditorBehavior(this, textField, onActionListener);
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(PROPERTY_NAME, DEFAULT_VALUE, getSuggestedFxIds(getControllerClass(), selectionState));
    }

//    @Override
//    public void reset(String name, String defaultValue, SelectionState selectionState) {
//        super.reset(PROPERTY_NAME, DEFAULT_VALUE, getSuggestedFxIds(getControllerClass(), selectionState));
//    }

    private List<String> getFxIdsInUse() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        FXOMFxIdIndex fxomIndex = new FXOMFxIdIndex(fxomDocument);
        return new ArrayList<>(fxomIndex.getFxIds().keySet());
    }

    private String getControllerClass() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        return fxomDocument == null ? null : fxomDocument.getFxomRoot().getFxController();
    }

    private List<String> getSuggestedFxIds(String controllerClass, SelectionState selectionState) {
        // Is not needed if multiple selection.
        if (controllerClass == null || hasMultipleSelection(selectionState)) {
            return Collections.emptyList();
        }
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        URL location = null;
        if (fxomDocument != null) {
            location = fxomDocument.getLocation();
        }
        List<String> fxIds = glossary.queryFxIds(location, controllerClass, getSelectedClass(selectionState));
        // Remove the already used FxIds
        fxIds.removeAll(getFxIdsInUse());
        return fxIds;
    }

    private boolean hasMultipleSelection(SelectionState selectionState) {
        return selectionState.getSelectedInstances().size() > 1;
    }

    private Class<?> getSelectedClass(SelectionState selectionState) {
        assert selectionState != null;
        assert selectionState.getSelectedClasses().size() == 1;
        return (Class<?>) selectionState.getSelectedClasses().toArray()[0];
    }
}
