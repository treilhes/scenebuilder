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
package org.scenebuilder.ext.javafx.customization.anchorpane;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstancePrototype;
import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.Documentation;
import com.oracle.javafx.scenebuilder.api.editors.AbstractPropertiesEditor;
import com.oracle.javafx.scenebuilder.api.editors.AbstractPropertyEditor;
import com.oracle.javafx.scenebuilder.api.editors.EditorUtils;
import com.oracle.javafx.scenebuilder.metadata.custom.addon.AnchorPropertyGroupMetadata;

import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Region;

/**
 * Editor for AnchorPane constraints.
 *
 *
 */
@ApplicationInstancePrototype
public class AnchorPaneConstraintsEditor extends AbstractPropertiesEditor {

    private static final String ANCHOR_ENABLED_COLOR = "-sb-line-art-accent";
    private static final String ANCHOR_DISABLED_COLOR = "-sb-line-art";

    @FXML
    private ToggleButton bottomTb;
    @FXML
    private TextField bottomTf;
    @FXML
    private Region innerR;
    @FXML
    private ToggleButton leftTb;
    @FXML
    private TextField leftTf;
    @FXML
    private Region outerR;
    @FXML
    private ToggleButton rightTb;
    @FXML
    private TextField rightTf;
    @FXML
    private ToggleButton topTb;
    @FXML
    private TextField topTf;

    private Parent root = null;
    private final ArrayList<ConstraintEditor> constraintEditors = new ArrayList<>();
    private ChangeListener<Object> constraintListener;

    public AnchorPaneConstraintsEditor(
            I18N i18n,
            AnchorPaneConstraintsEditor.ConstraintEditor.Factory constraintEditorFactory
            ) {
        super(i18n, "Anchor constraints xxx");
        root = FXMLUtils.load(this, "AnchorPaneConstraintsEditor.fxml");
        initialize(constraintEditorFactory);
    }

    // Method to please findBugs
    private void initialize(AnchorPaneConstraintsEditor.ConstraintEditor.Factory factory) {

        constraintListener = (ov, prevValue, newValue) -> {
            propertyChanged();
            styleRegions();
        };

        constraintEditors.add(factory.getEditor(topTf, topTb, constraintListener));
        constraintEditors.add(factory.getEditor(rightTf, rightTb, constraintListener));
        constraintEditors.add(factory.getEditor(bottomTf, bottomTb, constraintListener));
        constraintEditors.add(factory.getEditor(leftTf, leftTb, constraintListener));

        // Select all text when this editor textfield is selected
        topTf.setOnMousePressed(event -> topTf.selectAll());
        topTf.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                topTf.selectAll();
            }
        }));
        rightTf.setOnMousePressed(event -> rightTf.selectAll());
        rightTf.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                rightTf.selectAll();
            }
        }));
        bottomTf.setOnMousePressed(event -> bottomTf.selectAll());
        bottomTf.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                bottomTf.selectAll();
            }
        }));
        leftTf.setOnMousePressed(event -> leftTf.selectAll());
        leftTf.focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if (newValue) {
                leftTf.selectAll();
            }
        }));
    }

    @Override
    public List<AbstractPropertyEditor> getPropertyEditors() {
        List<AbstractPropertyEditor> propertyEditors = new ArrayList<>();
        for (ConstraintEditor constraintEditor : constraintEditors) {
            propertyEditors.add(constraintEditor);
        }
        return propertyEditors;
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);
        assert propMeta.isGroup();
        assert propMeta instanceof AnchorPropertyGroupMetadata;



        AnchorPropertyGroupMetadata anchorsMeta = (AnchorPropertyGroupMetadata)propMeta;

        Set<FXOMElement> selectedInstances = selectionState.getSelectedInstances();

        constraintEditors.get(0).reset(selectedInstances, anchorsMeta.getTopAnchorPropertyPropertyMetadata());
        constraintEditors.get(1).reset(selectedInstances, anchorsMeta.getRightAnchorPropertyPropertyMetadata());
        constraintEditors.get(2).reset(selectedInstances, anchorsMeta.getBottomAnchorPropertyPropertyMetadata());
        constraintEditors.get(3).reset(selectedInstances, anchorsMeta.getLeftAnchorPropertyPropertyMetadata());

        propertyChanged();

        for (int ii = 0; ii < 4; ii++) {
            constraintEditors.get(ii).addValueListener(constraintListener);
        }
        styleRegions();
    }

    @Override
    public Node getValueEditor() {
        return root;
    }

    private void styleRegions() {
        StringBuilder styleString = new StringBuilder();
        for (int ii = 0; ii < 4; ii++) {
            if (constraintEditors.get(ii).isAnchorEnabled()) {
                styleString.append(ANCHOR_ENABLED_COLOR);
                styleString.append(" ");
            } else {
                styleString.append(ANCHOR_DISABLED_COLOR);
                styleString.append(" ");
            }
        }
        String style = "-fx-border-color: " + styleString;
        innerR.setStyle(style);
        outerR.setStyle(style);
    }

    /*
     * Editor for a single constraint (e.g. topAnchor)
     */
    @ApplicationInstancePrototype
    public static class ConstraintEditor extends AbstractPropertyEditor {

        private ToggleButton toggleButton;
        private TextField textField;
        private Set<FXOMElement> selectedInstances;
        private ValuePropertyMetadata propMeta;

        private boolean updateFromTextField = false;

        //@formatter:off
        protected ConstraintEditor(
                I18N i18n,
                Dialog dialog,
                Documentation documentation,
                FileSystem fileSystem) {
          //@formatter:on
            super(i18n, dialog, documentation, fileSystem);
        }

        protected void initialize(TextField textField, ToggleButton toggleButton, ChangeListener<Object> listener) {
            super.addValueListener(listener);
            this.textField = textField;
            this.toggleButton = toggleButton;
            //
            // Text field
            //

            EventHandler<ActionEvent> valueListener = event -> {
                if (isHandlingError()) {
                    // Event received because of focus lost due to error dialog
                    return;
                }
                String valStr = textField.getText();
                if (valStr == null || valStr.isEmpty()) {
                    if (toggleButton.isSelected()) {
                        updateFromTextField = true;
                        toggleButton.setSelected(false);
                        updateFromTextField = false;
                    }
                    userUpdateValueProperty(null);
                    return;
                }
                textField.selectAll();
                double valDouble;
                try {
                    valDouble = Double.parseDouble(valStr);
                } catch (NumberFormatException e) {
                    handleInvalidValue(valStr, textField);
                    return;
                }
                if (!((DoublePropertyMetadata) getPropertyMeta()).isValidValue(valDouble)) {
                    handleInvalidValue(valDouble, textField);
                    return;
                }
                if (!toggleButton.isSelected()) {
                    updateFromTextField = true;
                    toggleButton.setSelected(true);
                    updateFromTextField = false;
                }
                userUpdateValueProperty(valDouble);
            };
            setNumericEditorBehavior(this, textField, valueListener, false);
            // Override default promptText
            textField.setPromptText(""); //NOCHECK

            textField.setOnMouseClicked(t -> ConstraintEditor.this.toggleButton.setSelected(true));

            //
            // Toggle button
            //


            toggleButton.selectedProperty().addListener((ChangeListener<Boolean>) (ov, prevSel, newSel) -> {
//                System.out.println("toggleButton : selectedProperty changed!");
                if (isUpdateFromModel() || updateFromTextField) {
                    // nothing to do
                    return;
                }

                // Update comes from toggleButton.
                if (newSel) {
                    // Anchor selected : compute its value from the selected node
                    double anchor = 0;
                    String lcPropName = ConstraintEditor.this.propMeta.getName().toString().toLowerCase();

                    // For the moment, we don't support multi-selection with different anchors:
                    // the first instance anchor only is used.
                    // TODO Anchors property metadata must know how to calculate itself instead of delegating this task
                    if (lcPropName.contains("top")) {
                        anchor = EditorUtils.computeTopAnchor(getFirstInstance());
                    } else if (lcPropName.contains("right")) {
                        anchor = EditorUtils.computeRightAnchor(getFirstInstance());
                    } else if (lcPropName.contains("bottom")) {
                        anchor = EditorUtils.computeBottomAnchor(getFirstInstance());
                    } else if (lcPropName.contains("left")) {
                        anchor = EditorUtils.computeLeftAnchor(getFirstInstance());
                    } else {
                        assert false;
                    }
                    textField.setText(EditorUtils.valAsStr(anchor));
                    userUpdateValueProperty(getValue());
                } else {
                    // Anchor unselected
                    textField.setText(null);
                    userUpdateValueProperty(null);
                }
            });
        }

        @Override
        public Node getValueEditor() {
            // Should not be called
            assert false;
            return null;
        }

        @Override
        public Object getValue() {
            String valStr = textField.getText();
            if (valStr == null || valStr.isEmpty()) {
                return null;
            }
            return Double.valueOf(valStr);
        }

        @Override
        public void setValue(Object value) {
            setValueGeneric(value);
            if (isSetValueDone()) {
                return;
            }

            if (value == null) {
                toggleButton.setSelected(false);
                textField.setText(null);
            } else {
                assert (value instanceof Double);
                toggleButton.setSelected(true);
                textField.setText(EditorUtils.valAsStr(value));
                if (textField.isFocused()) {
                    textField.positionCaret(textField.getLength());
                }
            }
        }

        public void reset(Set<FXOMElement> selectedInstances, ValuePropertyMetadata propMeta) {
            assert propMeta instanceof DoublePropertyMetadata;
            super.reset(propMeta, null);
            this.selectedInstances = selectedInstances;
            this.propMeta = propMeta;
            // For SQE tests
            textField.setId(EditorUtils.toDisplayName(propMeta.getName().getName()) + " Value"); //NOCHECK
            textField.setPromptText(null);
        }

        @Override
        protected void valueIsIndeterminate() {
            handleIndeterminate(textField);
        }

        public boolean isAnchorEnabled() {
            return valueProperty().getValue() != null;
        }

        @Override
        public void requestFocus() {
            EditorUtils.doNextFrame(() -> textField.requestFocus());
        }

        private FXOMInstance getFirstInstance() {
            return (FXOMInstance) selectedInstances.toArray()[0];
        }

        @ApplicationInstancePrototype
        public static class Factory extends AbstractFactory<ConstraintEditor> {
            public Factory(JfxAppContext sbContext) {
                super(sbContext);
            }

            public ConstraintEditor getEditor(TextField textField, ToggleButton toggleButton, ChangeListener<Object> listener) {
                return create(ConstraintEditor.class, c -> c.initialize(textField, toggleButton, listener));
            }
        }
    }
}
