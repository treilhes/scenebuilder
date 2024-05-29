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

import org.scenebuilder.fxml.api.Documentation;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.fs.FileSystem;
import com.gluonhq.jfxapps.core.api.ui.dialog.Dialog;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;
import com.oracle.javafx.scenebuilder.fxml.api.selection.SelectionState;

import javafx.beans.InvalidationListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class EnumEditor extends AbstractPropertyEditor {
    private ComboBox<String> comboBox;

    public EnumEditor(Dialog dialog,
            Documentation documentation,
            FileSystem fileSystem) {
        super(dialog, documentation, fileSystem);
        comboBox = new ComboBox<String>();
        comboBox.disableProperty().bind(disableProperty());
        EditorUtils.makeWidthStretchable(comboBox);
        comboBox.getSelectionModel().selectedItemProperty().addListener((InvalidationListener) o -> {
            if (!isUpdateFromModel()) {
                userUpdateValueProperty(getValue());
            }
        });
    }

    @Override
    public Object getValue() {
        return comboBox.getSelectionModel().getSelectedItem();
    }

    @Override
    public void setValue(Object value) {
        setValueGeneric(value);
        if (isSetValueDone()) {
            return;
        }

        if (value != null) {
            comboBox.getSelectionModel().select(value.toString());
        } else {
            comboBox.getSelectionModel().clearSelection();
        }
    }

    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);
        // ComboBox items have to be updated, since this editor may have been used by a different Enum...
        updateItems();
    }

    @Override
    public Node getValueEditor() {
        return super.handleGenericModes(comboBox);
    }

    @Override
    protected void valueIsIndeterminate() {
        handleIndeterminate(comboBox);
    }

    protected ComboBox<String> getComboBox() {
        return comboBox;
    }

    protected void updateItems() {
        updateItems(comboBox.getItems());
    }

    protected void updateItems(ObservableList<String> itemsList) {
        assert getPropertyMeta() instanceof EnumerationPropertyMetadata;
        final EnumerationPropertyMetadata enumPropMeta
                = (EnumerationPropertyMetadata) getPropertyMeta();
        itemsList.clear();
        for (Object val : enumPropMeta.getValidValues()) {
            itemsList.add(val.toString());
        }
    }

    @Override
    public void requestFocus() {
        EditorUtils.doNextFrame(() -> comboBox.requestFocus());
    }

    @Component
    @Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
    public static class GenericEnumEditor extends EnumEditor {

        public GenericEnumEditor(Dialog dialog,
                Documentation documentation,
                FileSystem fileSystem) {
            super(dialog, documentation, fileSystem);
        }
    }

}
