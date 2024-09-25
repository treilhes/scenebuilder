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

import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.fxom.util.PrefixedValue;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.api.editors.AbstractPropertyEditor;
import com.oracle.javafx.scenebuilder.api.editors.AbstractPropertyEditor.LayoutFormat;
import com.oracle.javafx.scenebuilder.api.editors.EditorUtils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

/**
 * String editor with I18n + multi-line handling.
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class I18nStringEditor extends AbstractPropertyEditor {

    private static final String PERCENT_STR = "%"; //NOCHECK
    private TextInputControl textNode = new TextField();
    private HBox i18nHBox = null;
    private EventHandler<ActionEvent> valueListener;
    private final MenuItem i18nMenuItem = new MenuItem();
    private final String I18N_ON = I18N.getString("inspector.i18n.on");
    private final String I18N_OFF = I18N.getString("inspector.i18n.off");
    private final MenuItem multilineMenuItem = new MenuItem();
    private final String MULTI_LINE = I18N.getString("inspector.i18n.multiline");
    private final String SINGLE_LINE = I18N.getString("inspector.i18n.singleline");
    private boolean multiLineSupported = false;
    // Specific states
    private boolean i18nMode = false;
    private boolean multiLineMode = false;

    public I18nStringEditor(Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem) {
        super(dialog, documentation, fileSystem);
        initialize(true);
    }

    private void initialize(boolean multiLineSupported) {
        this.multiLineSupported = multiLineSupported;
        valueListener = event -> {
            userUpdateValueProperty(getValue());
            textNode.selectAll();
        };
        setTextEditorBehavior(this, textNode, valueListener);

        getMenu().getItems().add(i18nMenuItem);
        getMenu().getItems().add(multilineMenuItem);

        i18nMenuItem.setOnAction(e -> {
            if (!i18nMode) {
                setValue(new PrefixedValue(PrefixedValue.Type.RESOURCE_KEY, I18N.getString("inspector.i18n.dummykey")).toString());
            } else {
                setValue(""); //NOCHECK
            }
            I18nStringEditor.this.getCommitListener().handle(null);
            updateMenuItems();
        });
        multilineMenuItem.setOnAction(e -> {
            if (!multiLineMode) {
                switchToTextArea();
            } else {
                switchToTextField();
            }
            multiLineMode = !multiLineMode;
            updateMenuItems();
        });

        // Select all text when this editor is selected
        textNode.setOnMousePressed(event -> textNode.selectAll());
        textNode.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                textNode.selectAll();
            }
        }));
    }

    @Override
    public Object getValue() {
        String val = textNode.getText();
        if (i18nMode) {
            val = new PrefixedValue(PrefixedValue.Type.RESOURCE_KEY, val).toString();
        } else {
            val = EditorUtils.getPlainString(val);
        }
        return val;
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        if (value == null) {
            textNode.setText(null);
            return;
        }
        assert value instanceof String;
        String val = (String) value;
        PrefixedValue prefixedValue = new PrefixedValue(val);
        String suffix = prefixedValue.getSuffix();

        // Handle i18n
        if (prefixedValue.isResourceKey()) {
            if (!i18nMode) {
                wrapInHBox();
                i18nMode = true;
            }
        } else if (i18nMode) {
            // no percent + i18nMode
            unwrapHBox();
            i18nMode = false;
        }

        // Handle multi-line
        if (containsLineFeed(prefixedValue.toString())) {
            if (i18nMode) {
                // multi-line + i18n ==> set as i18n only
                multiLineMode = false;
                switchToTextField();
            } else {
                if (!multiLineMode) {
                    multiLineMode = true;
                    switchToTextArea();
                }
            }
        } else {
            // no line feed
            if (multiLineMode) {
                multiLineMode = false;
                switchToTextField();
            }
        }

        if (i18nMode) {
            textNode.setText(suffix);
        } else {
            // We may have other special characters (@, $, ...) to display in the text field
            textNode.setText(prefixedValue.toString());
        }
        updateMenuItems();
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);
        this.multiLineSupported = isMultiLinesSupported(selectionState.getSelectedClasses(), propMeta);
        textNode.setPromptText(null);
    }

    @Override
    public Node getValueEditor() {
        Node valueEditor;
        if (i18nMode) {
            valueEditor = i18nHBox;
        } else {
            valueEditor = textNode;
        }

        return super.handleGenericModes(valueEditor);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(textNode);
    }

    protected void switchToTextArea() {
        if (textNode instanceof TextArea) {
            return;
        }
        // Move the node from TextField to TextArea
        TextArea textArea = new TextArea(textNode.getText());
        setTextEditorBehavior(this, textArea, valueListener);
        textArea.setPrefRowCount(5);
        setLayoutFormat(LayoutFormat.SIMPLE_LINE_TOP);
        if (textNode.getParent() != null) {
            // textNode is already in scene graph
            EditorUtils.replaceNode(textNode, textArea, getLayoutFormat());
        }
        textNode = textArea;
    }

    protected void switchToTextField() {
        if (textNode instanceof TextField) {
            return;
        }
        // Move the node from TextArea to TextField.
        // The current text is compacted to a single line.
        String val = textNode.getText().replace("\n", "");//NOCHECK
        TextField textField = new TextField(val);
        setTextEditorBehavior(this, textField, valueListener);
        setLayoutFormat(LayoutFormat.SIMPLE_LINE_CENTERED);
        if (textNode.getParent() != null) {
            // textNode is already in scene graph
            EditorUtils.replaceNode(textNode, textField, getLayoutFormat());
        }
        textNode = textField;
    }

    private void wrapInHBox() {
        i18nHBox = new HBox();
        i18nHBox.setAlignment(Pos.CENTER);
        EditorUtils.replaceNode(textNode, i18nHBox, null);
        Label percentLabel = new Label(PERCENT_STR);
        percentLabel.getStyleClass().add("symbol-prefix"); //NOCHECK
        i18nHBox.getChildren().addAll(percentLabel, textNode);
        HBox.setHgrow(percentLabel, Priority.NEVER);
        // we have to set a small pref width for the text node else it will
        // revert to it's API set pref width which is too wide
        textNode.setPrefWidth(30.0);
        HBox.setHgrow(textNode, Priority.ALWAYS);
    }

    private void unwrapHBox() {
        i18nHBox.getChildren().remove(textNode);
        EditorUtils.replaceNode(i18nHBox, textNode, null);
    }

    private static boolean containsLineFeed(String str) {
        return str.contains("\n"); //NOCHECK
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> textNode.requestFocus());
    }

    private void updateMenuItems() {
        if (i18nMode) {
            i18nMenuItem.setText(I18N_OFF);
            multilineMenuItem.setDisable(true);
        } else {
            i18nMenuItem.setText(I18N_ON);
            multilineMenuItem.setDisable(false);
        }

        if (multiLineMode) {
            multilineMenuItem.setText(SINGLE_LINE);
            i18nMenuItem.setDisable(true);
        } else {
            multilineMenuItem.setText(MULTI_LINE);
            i18nMenuItem.setDisable(false);
        }

        if (!multiLineSupported) {
            multilineMenuItem.setDisable(true);
        }
    }

    private boolean isMultiLinesSupported(Set<Class<?>> selectedClasses, PropertyMetadata propMeta) {

        //FIXME no way to know if the the text property is multiline or not
        // so we keep this special case
        String propertyNameStr = propMeta.getName().getName();
        if (selectedClasses.contains(TextField.class) || selectedClasses.contains(PasswordField.class)) {
            if (propertyNameStr.equalsIgnoreCase("text")) {
                return false;
            }
        }

        if (propMeta instanceof I18nStringPropertyMetadata) {
            return ((I18nStringPropertyMetadata)propMeta).isMultiline();
        }

        return true;
    }
}
