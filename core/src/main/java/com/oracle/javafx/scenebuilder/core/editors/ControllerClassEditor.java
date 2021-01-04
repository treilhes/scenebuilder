/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.Glossary;
import com.oracle.javafx.scenebuilder.api.subjects.DocumentManager;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMDocument;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.util.JavaLanguage;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Controller class editor.
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ControllerClassEditor extends AutoSuggestEditor {

    private static final String PROPERTY_NAME = "Controller class"; //NOI18N
    private static final String DEFAULT_VALUE = null;
    private FXOMDocument fxomDocument;
    private final Glossary glossary;

    public ControllerClassEditor(
            @Autowired Dialog dialog,
            @Autowired Glossary glossary,
            @Autowired DocumentManager documentManager
            ) {
        super(dialog);
        this.glossary = glossary;
        preInit(Type.ALPHA, new ArrayList<>());
        documentManager.fxomDocument().subscribe(fxom -> this.fxomDocument = fxom);
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
                if (!JavaLanguage.isClassName(value)) {
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
        super.reset(PROPERTY_NAME, DEFAULT_VALUE, getSuggestedControllerClasses());
    }

    // DTL-6625. Compared to super implementation we do not call isSetValueDone.
    @Override
    public void setValue(Object value) {
        setValueGeneric(value);

        if (value == null) {
            getTextField().setText(null);
        } else {
            assert value instanceof String;
            getTextField().setText((String) value);
        }
    }
    
    private List<String> getSuggestedControllerClasses() {
        return glossary.queryControllerClasses(fxomDocument == null ? null : fxomDocument.getLocation());
    }
}