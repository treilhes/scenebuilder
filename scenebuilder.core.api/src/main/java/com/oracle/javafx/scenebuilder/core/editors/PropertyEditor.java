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

import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor.LayoutFormat;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;

public interface PropertyEditor {

    /**
     * Gets the javafx root node containing the components of this editor.
     *
     * @return the root node
     */
    public Node getValueEditor();

    /**
     * Return the menu button associated with this editor
     * It contains all the specific actions of this property editor
     * @return the menu button
     */
    public MenuButton getMenu();

    /**
     * Clean all listeners of this editor
     */
    public void removeAllListeners();
    
    
    /**
     * Reset/Initialize everything so that the editor can be re-used for another property
     * @param propMeta the property metadata
     * @param selectionState the current selection states
     */
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState);
    
//    /**
//     * Reset/Initialize everything so that the editor can be re-used for another property
//     * Special for elements which are not JavaFX properties (e.g fx:id, controllerClass)
//     * In this case, propMeta and selectedClasses are null.
//     * @param name the property name
//     * @param defaultValue the property default value
//     */
//    public void reset(String name, String defaultValue);
    
    /**
     * Sets the update from model flag.
     * When set to true, update events are not propagated
     *
     * @param b the update from model flag value
     */
    public void setUpdateFromModel(boolean b);

    public void addValueListener(ChangeListener<Object> listener);

    public boolean isUpdateFromModel();

    public boolean isRuledByCss();

    public String getPropertyNameText();

    public ValuePropertyMetadata getPropertyMeta();

    public void addTransientValueListener(ChangeListener<Object> listener);

    public void addEditingListener(ChangeListener<Boolean> listener);

    public boolean isInvalidValue();

    EventHandler<?> getCommitListener();

    public void addNavigateListener(ChangeListener<String> listener);

    public PropertyName getPropertyName();

    public void setRuledByCss(boolean b);

    public void setCssInfo(CssPropAuthorInfo cssInfo);

    public boolean isDisablePropertyBound();

    public void unbindDisableProperty();

    //adds CSS values to the ValueEditor
    public void setValue(Object fxValue);

    public void setIndeterminate(boolean b);

    public void setDisable(boolean b);

    public void setLayoutFormat(LayoutFormat doubleLine);

    public void requestFocus();

}
