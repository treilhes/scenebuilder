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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.controller.dialog.Dialog;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.editors.AutoSuggestEditor;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * Editor for Double properties, with pre-defined constants (handled by
 * auto-suggest popup).
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class DoubleEditor extends AutoSuggestEditor {

    private Map<String, Object> constants;

    public DoubleEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem
            ) {
        super(dialog, documentation, fileSystem);
        preInit(Type.DOUBLE, new ArrayList<>());
        initialize(new HashMap<>());
    }

    private void initialize(Map<String, Object> constants) {
        this.constants = constants;

        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            Object value = getValue();
            if ((value != null) && ((DoublePropertyMetadata) getPropertyMeta()).isValidValue((Double) value)) {
                userUpdateValueProperty(value);
                getTextField().selectAll();
            } else {
                handleInvalidValue(getTextField().getText());
            }
        };

        setNumericEditorBehavior(this, getTextField(), onActionListener);
    }

    @Override
    public Object getValue() {
        String val = getTextField().getText();
        if (val.isEmpty()) {
            val = "0"; //NOCHECK
            getTextField().setText(val);
            return Double.valueOf(val);
        }
        Object constantValue = constants.get(val.toUpperCase(Locale.ROOT));
        if (constantValue != null) {
            val = EditorUtils.valAsStr(constantValue);
        }
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        assert (value instanceof Double);
        // Get the corresponding constant if any
        for (Entry<String, Object> entry : constants.entrySet()) {
            if (value.equals(entry.getValue())) {
                value = entry.getKey();
            }
        }
        getTextField().setText(EditorUtils.valAsStr(value));
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> getTextField().requestFocus());
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState, new ArrayList<>(propMeta.getConstants().keySet()));
        this.constants = propMeta.getConstants();
    }
}
