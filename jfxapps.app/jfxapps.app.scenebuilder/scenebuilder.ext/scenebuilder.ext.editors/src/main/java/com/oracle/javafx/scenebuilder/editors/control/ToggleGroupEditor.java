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
package com.oracle.javafx.scenebuilder.editors.control;

import java.util.ArrayList;
import java.util.List;

import org.scenebuilder.fxml.api.Documentation;
import org.scenebuilder.fxml.api.subjects.FxmlDocumentManager;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.fs.FileSystem;
import com.oracle.javafx.scenebuilder.api.ui.dialog.Dialog;
import com.oracle.javafx.scenebuilder.core.editors.AutoSuggestEditor;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMFxIdIndex;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.util.JavaLanguage;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Toggle Group editor.
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ToggleGroupEditor extends AutoSuggestEditor {

    List<String> suggestedTgs;

    private final FxmlDocumentManager documentManager;

    public ToggleGroupEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            FxmlDocumentManager documentManager) {
        super(dialog, documentation, fileSystem);
        this.documentManager = documentManager;
        initialize(new ArrayList<>());
    }

    private void initialize(List<String> suggestedTgs) {
        this.suggestedTgs = suggestedTgs;

        // text field events handling
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            String value = textField.getText();
            if (value != null && !value.isEmpty()) {

                if (!JavaLanguage.isIdentifier(value)) {
//                        System.err.println(I18N.getString("inspector.fxml.invalid.id", value));
                    handleInvalidValue(value);
                    return;
                }
            }
            userUpdateValueProperty((value == null || value.isEmpty()) ? null : value);
            textField.selectAll();
        };
        setTextEditorBehavior(this, textField, onActionListener);
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState, getSuggestedToggleGroups());
    }

    private List<String> getSuggestedToggleGroups() {
        FXOMDocument fxomDocument = documentManager.fxomDocument().get();
        if (fxomDocument == null) {
            return new ArrayList<>();
        }
        FXOMFxIdIndex fxomIndex = new FXOMFxIdIndex(fxomDocument);
        List<FXOMInstance> tgs = fxomIndex.collectToggleGroups();
        ArrayList<String> tgNames = new ArrayList<>();
        for (FXOMInstance tg : tgs) {
            tgNames.add(tg.getFxId());
        }
        return tgNames;
    }
}
