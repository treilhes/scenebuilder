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
package com.oracle.javafx.scenebuilder.editors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.api.Dialog;
import com.oracle.javafx.scenebuilder.api.util.SceneBuilderBeanFactory;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.value.TableViewResizePolicyPropertyMetadata;

import javafx.scene.control.TableView;
import javafx.scene.control.TreeTableView;

/**
 * Editor for TableView columnResizePolicy property.
 */
@Component
@Scope(SceneBuilderBeanFactory.SCOPE_PROTOTYPE)
@Lazy
public class ColumnResizePolicyEditor extends EnumEditor {

    boolean isTableView;

    public ColumnResizePolicyEditor(
            @Autowired Dialog dialog) {
        super(dialog);
        
    }

    @Override
    public Object getValue() {
        String policy = getComboBox().getSelectionModel().getSelectedItem();
        if (isTableView) {
            if (policy.equals(TableView.UNCONSTRAINED_RESIZE_POLICY.toString())) {
                return TableView.UNCONSTRAINED_RESIZE_POLICY;
            } else {
                return TableView.CONSTRAINED_RESIZE_POLICY;
            }
        } else {
            if (policy.equals(TreeTableView.UNCONSTRAINED_RESIZE_POLICY.toString())) {
                return TreeTableView.UNCONSTRAINED_RESIZE_POLICY;
            } else {
                return TreeTableView.CONSTRAINED_RESIZE_POLICY;
            }
        }
    }


   @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        super.reset(propMeta, selectionState);
        isTableView = propMeta instanceof TableViewResizePolicyPropertyMetadata;
    }

    @Override
    protected void updateItems() {
        getComboBox().getItems().clear();
        getComboBox().getItems().add(TableView.UNCONSTRAINED_RESIZE_POLICY.toString());
        getComboBox().getItems().add(TableView.CONSTRAINED_RESIZE_POLICY.toString());
    }

}