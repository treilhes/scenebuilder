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
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.core.api.editor.selection.SelectionState;
import com.gluonhq.jfxapps.core.api.factory.AbstractFactory;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.api.util.FXMLUtils;
import com.gluonhq.jfxapps.core.fxom.FXOMInstance;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.IMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata;
import com.oracle.javafx.scenebuilder.api.editors.EditorUtils;
import com.oracle.javafx.scenebuilder.core.editors.AutoSuggestEditor;

import javafx.beans.InvalidationListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Slider;
import javafx.scene.layout.StackPane;

/**
 * Editor for bounded double properties. (e.g. 0 &lt;= opacity &lt;= 1)
 *
 *
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
public class BoundedDoubleEditor extends AutoSuggestEditor {

    @FXML
    private Slider slider;
    @FXML
    private StackPane textSp;

    private Parent root = null;
    private Map<String, Object> constants;
    // default min and max
    private double min = 0;
    private double max = 100;
    private boolean minMaxForSliderOnly = false;
    private int roundingFactor = 1; // no decimals
    private boolean updateFromTextField = false;
    private boolean updateFromSlider = false;
    private final IMetadata metadata;

    public BoundedDoubleEditor(
            Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem,
            IMetadata metadata) {
        super(dialog, documentation, fileSystem);
        this.metadata = metadata;
        preInit(Type.DOUBLE, new ArrayList<>());
        initialize();
    }

    // Method to please FindBugs
    private void initialize() {
        root = FXMLUtils.load(this, "BoundedDoubleEditor.fxml");

        //
        // Text field
        //
        EventHandler<ActionEvent> onActionListener = event -> {
            if (isHandlingError()) {
                // Event received because of focus lost due to error dialog
                return;
            }
            if (isUpdateFromModel() || updateFromSlider) {
                // nothing to do
                return;
            }

            Object value = getValue();
            if (getPropertyMeta() != null) {
                if ((value == null)
                        || !((DoublePropertyMetadata) getPropertyMeta()).isValidValue((Double) value)) {
                    handleInvalidValue(getTextField().getText());
                }
            }
            assert value instanceof Double;
            double valDouble = (Double) value;
            // Check if the entered value is a constant string
            boolean isConstant = constants.get(getTextField().getText().toUpperCase(Locale.ROOT)) != null;
            // Check if the entered value is a constant value
            for (Map.Entry<String, Object> entry : constants.entrySet()) {
                if (value.equals(entry.getValue())) {
                    isConstant = true;
                    break;
                }
            }
            // If the value is not a constant,
            // and is less than the minimum, or more than the maximum,
            // set the value to min or max
            if (!minMaxForSliderOnly && !isConstant && (valDouble < min || valDouble > max)) {
                if (valDouble < min) {
                    valDouble = min;
                } else if (valDouble > max) {
                    valDouble = max;
                }
                getTextField().setText(EditorUtils.valAsStr(valDouble));
            }
            getTextField().selectAll();
            updateFromTextField = true;
            slider.setValue(valDouble);
            updateFromTextField = false;
            userUpdateValueProperty(valDouble);
        };
        setNumericEditorBehavior(this, getTextField(), onActionListener, false);

        //
        // Slider
        //
        configureSlider(getPropertyMeta(), null);

        slider.valueProperty().addListener((InvalidationListener) valueModel -> {
//                System.out.println("Slider : valueProperty changed!");
            if (isUpdateFromModel() || updateFromTextField) {
                // nothing to do
                return;
            }

            // Slider button moved or left/right key typed.
            // In this case, we want to round the value,
            // since the Slider may returns many decimals.
            double value = EditorUtils.round(slider.getValue(), roundingFactor);
            updateFromSlider = true;
            getTextField().setText(EditorUtils.valAsStr(value));
            updateFromSlider = false;
            userUpdateTransientValueProperty(value);
        });

        slider.pressedProperty().addListener((InvalidationListener) valueModel -> {
            if (!slider.isPressed()) {
                double value = EditorUtils.round(slider.getValue(), roundingFactor);
                userUpdateValueProperty(value);
            }
        });
        // Add the AutoSuggest text field in the scene graph
        textSp.getChildren().add(super.getRoot());
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(root);
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
        slider.setValue((Double) value);
        // Get the corresponding constant if any
        for (Map.Entry<String, Object> entry : constants.entrySet()) {
            if (value.equals(entry.getValue())) {
                value = entry.getKey();
            }
        }
        getTextField().setText(EditorUtils.valAsStr(value));
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState, new ArrayList<>(propMeta.getConstants().keySet()));
        this.constants = propMeta.getConstants();
        //TODO check those specific cases and delete
        handleSpecificCases(propMeta, null);//selectionState.getSelectedInstances());
        configureSlider(propMeta, selectionState);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(getTextField());
    }

    private void configureSlider(PropertyMetadata propMeta, SelectionState selectionState) {
        if (propMeta != null) {
            //if (propMeta instanceof DoublePropertyMetadata) {
                assert propMeta instanceof DoublePropertyMetadata;
                DoublePropertyMetadata doublePropMeta = (DoublePropertyMetadata) propMeta;
                min = doublePropMeta.getMin(selectionState);
                max = doublePropMeta.getMax(selectionState);

                if (max <= 1) {
                    roundingFactor = 100; // 2 decimals
                } else if (max <= 10) {
                    roundingFactor = 10; // 1 decimal
                } else {
                    roundingFactor = 1; // no decimal
                }

                setMinMaxForSliderOnly(doublePropMeta.hasLenientBoundary());
//            }
//            if (propMeta instanceof DoubleBoundedPropertyGroupMetadata) {
//                assert propMeta instanceof DoubleBoundedPropertyGroupMetadata;
//                DoubleBoundedPropertyGroupMetadata doublePropMeta = (DoubleBoundedPropertyGroupMetadata) propMeta;
//
//                selectionState.getSelectedInstances().stream().forEach(i -> {
//                    min = Math.max(min, (Double)doublePropMeta.getMinPropertyMetadata().getValueObject(i));
//                    max = Math.min(max, (Double)doublePropMeta.getMaxPropertyMetadata().getValueObject(i));
//                });
//
//                if (max <= 1) {
//                    roundingFactor = 100; // 2 decimals
//                } else if (max <= 10) {
//                    roundingFactor = 10; // 1 decimal
//                } else {
//                    roundingFactor = 1; // no decimal
//                }
//            }
        }
        slider.setMin(min);
        slider.setMax(max);
        slider.setBlockIncrement((max - min) / 20);
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> getTextField().requestFocus());
    }

    private void handleSpecificCases(PropertyMetadata propMeta, Set<FXOMInstance> selectedInstances) {
        //TODO handle using groups
        if (true)
            return;

        // Specific case for ScrollPane hValue/vValue, that have their bounds
        // related to properties (hMin/hMax, vMin/Vmax)
        // Since we only have one case of this, the generic case
        // (bounds=properties) has not been implemented.
        // (to avoid to add complexity for a single case)
        String[] scrollBarPropsArray = { };//AbstractEditor.hValuePropName, AbstractEditor.vValuePropName };
        String[] scrollBarHprops = { };//AbstractEditor.hMinPropName, AbstractEditor.hMaxPropName };
        String[] scrollBarVprops = { };//AbstractEditor.vMinPropName, AbstractEditor.vMaxPropName };
        List<String> scrollBarProps = Arrays.asList(scrollBarPropsArray);
        if (!scrollBarProps.contains(propMeta.getName().toString())) {
            return;
        }
        // TODO read/understand and update
        String[] minMaxProps;
        if (propMeta.getName().toString().equals("This condition is a temporary fake")) { //AbstractEditor.hValuePropName)) {
            minMaxProps = scrollBarHprops;
        } else {
            minMaxProps = scrollBarVprops;
        }

        for (String minMaxProp : minMaxProps) {
            // Set min and max
            Object propValue = null;
            boolean different = false;
            for (FXOMInstance instance : selectedInstances) {
                Object valueCurr = metadata.queryValueProperty(instance, new PropertyName(minMaxProp))
                        .getValueInSceneGraphObject(instance);
                if (propValue != null && valueCurr != propValue) {
                    different = true;
                    break;
                }
                propValue = valueCurr;
            }
            if (!different) {
                assert propValue instanceof Double;
                if (minMaxProp.contains("min")) { //NOCHECK
                    this.min = (Double) propValue;
                } else {
                    this.max = (Double) propValue;
                }
            }
        }
    }

    public boolean isMinMaxForSliderOnly() {
        return minMaxForSliderOnly;
    }

    public void setMinMaxForSliderOnly(boolean minMaxForSliderOnly) {
        this.minMaxForSliderOnly = minMaxForSliderOnly;
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_SINGLETON)
    public static class Factory extends AbstractFactory<BoundedDoubleEditor> {
        public Factory(SceneBuilderBeanFactory sbContext) {
            super(sbContext);
        }


        public BoundedDoubleEditor getEditor(String name, String defaultValue, List<String> suggestedList, Double min, Double max, boolean minMaxForSliderOnly) {
            return sbContext.getBean(BoundedDoubleEditor.class, name, defaultValue, suggestedList, min, max, minMaxForSliderOnly);
        }
    }

}
