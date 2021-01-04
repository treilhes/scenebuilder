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

import java.util.Collection;
import java.util.List;

import com.oracle.javafx.scenebuilder.api.i18n.I18N;
import com.oracle.javafx.scenebuilder.core.editor.selection.SelectionState;
import com.oracle.javafx.scenebuilder.core.editors.AbstractPropertyEditor.LayoutFormat;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.util.EditorUtils;

import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 *
 * 
 */
public abstract class AbstractPropertiesEditor extends AbstractEditor {

    private ValuePropertyMetadata propMeta = null;
    private final HBox nameNode;
    private MenuButton menu;
    private final MenuItem resetvalueMenuItem = new MenuItem(I18N.getString("inspector.editors.resetvalue"));
    private FadeTransition fadeTransition = null;
    private final String name;

    public AbstractPropertiesEditor(String name) {
        // HBox for consistency with PropertyEditor, and potentially have an hyperlink
        this.name = name;
        nameNode = new HBox();
        nameNode.getChildren().add(new Label(name));
    }

    public HBox getNameNode() {
        return nameNode;
    }

    @Override
    public String getPropertyNameText() {
        return name;
    }

    public abstract List<AbstractPropertyEditor> getPropertyEditors();

    @Override
    public final MenuButton getMenu() {
        if (menu == null) {
            menu = new MenuButton();

            Region region = new Region();
            menu.setGraphic(region);
            region.getStyleClass().add("cog-shape"); // NOI18N

            menu.getStyleClass().add("cog-menubutton"); // NOI18N
            menu.setOpacity(0);
            fadeTransition = new FadeTransition(Duration.millis(500), menu);
            EditorUtils.handleFading(fadeTransition, menu);
            EditorUtils.handleFading(fadeTransition, getValueEditor());

            menu.focusedProperty().addListener((ChangeListener<Boolean>) (observable, oldValue, newValue) -> {
                if (newValue) {
                    // focused
                    EditorUtils.fadeTo(fadeTransition, 1);
                } else {
                    // focus lost
                    EditorUtils.fadeTo(fadeTransition, 0);
                }
            });
            menu.getItems().add(resetvalueMenuItem);
            resetvalueMenuItem.setOnAction(e -> {
                for (AbstractPropertyEditor propertyEditor : getPropertyEditors()) {
                    propertyEditor.setValue(propertyEditor.getPropertyMeta().getDefaultValueObject());
                }
            });
        }
        return menu;
    }

    @Override
    public void removeAllListeners() {
        for (AbstractPropertyEditor propertyEditor : getPropertyEditors()) {
            propertyEditor.removeAllListeners();
        }
    }

    protected void propertyChanged() {
        boolean allDefault = true;
        for (AbstractPropertyEditor propertyEditor : getPropertyEditors()) {
            Object value = propertyEditor.valueProperty().getValue();
            ValuePropertyMetadata propMeta = propertyEditor.getPropertyMeta();
            if (value == null) {
                if (!(propMeta.getDefaultValueObject() == null)) {
                    allDefault = false;
                    break;
                }
            } else if (!value.equals(propMeta.getDefaultValueObject())) {
                allDefault = false;
                break;
            }
        }
        if (allDefault) {
            resetvalueMenuItem.setDisable(true);
        } else {
            resetvalueMenuItem.setDisable(false);
        }
    }

    @Override
    public void setUpdateFromModel(boolean updateFromModelFlag) {
        getPropertyEditors().forEach(e -> e.setUpdateFromModel(updateFromModelFlag));
    }

    @Override
    public void addValueListener(ChangeListener<Object> listener) {
        getPropertyEditors().forEach(e -> e.addValueListener(listener));
    }

    @Override
    public boolean isUpdateFromModel() {
        return getPropertyEditors().stream().allMatch(e -> e.isUpdateFromModel());
    }

    @Override
    public boolean isRuledByCss() {
        return getPropertyEditors().stream().anyMatch(e -> e.isRuledByCss());
    }
    
    @Override
    public void reset(ValuePropertyMetadata propMeta, SelectionState selectionState) {
        assert propMeta instanceof PropertyGroupMetadata;
        
        List<AbstractPropertyEditor> editors = getPropertyEditors();
        PropertyGroupMetadata pgm = (PropertyGroupMetadata)propMeta;
        
        assert editors.size() == pgm.getProperties().length;
        
        this.propMeta = propMeta;
        
        //TODO there is a big flaw here, there isn't any guarantee the order of sub properties and 
        // editors are the same. At least, AnchorPaneConstraintsEditor, as the only implementing class yet
        // has a safe order
        for (int i=0;i < editors.size(); i++) {
            editors.get(i).reset(pgm.getProperties()[i], selectionState);
        }
        
    }

    @Override
    public ValuePropertyMetadata getPropertyMeta() {
        return propMeta;
    }

    @Override
    public void addTransientValueListener(ChangeListener<Object> listener) {
        getPropertyEditors().forEach(e -> e.addTransientValueListener(listener));
    }

    @Override
    public void addEditingListener(ChangeListener<Boolean> listener) {
        getPropertyEditors().forEach(e -> e.addEditingListener(listener));
    }

    @Override
    public boolean isInvalidValue() {
        return getPropertyEditors().stream().anyMatch(e -> e.isInvalidValue());
    }

    @Override
    public void addNavigateListener(ChangeListener<String> listener) {
        getPropertyEditors().forEach(e -> e.addNavigateListener(listener));
    }

    @Override
    public PropertyName getPropertyName() {
        return propMeta.getName();
    }

    @Override
    public void setRuledByCss(boolean b) {
        getPropertyEditors().forEach(e -> e.setRuledByCss(b));
    }

    @Override
    public void setCssInfo(CssPropAuthorInfo cssInfo) {
        getPropertyEditors().forEach(e -> e.setCssInfo(cssInfo));
    }

    @Override
    public boolean isDisablePropertyBound() {
        return getPropertyEditors().stream().anyMatch(e -> e.isDisablePropertyBound());
    }

    @Override
    public void unbindDisableProperty() {
        getPropertyEditors().forEach(e -> e.unbindDisableProperty());
    }

    @Override
    public void setIndeterminate(boolean b) {
        getPropertyEditors().forEach(e -> e.setIndeterminate(b));
    }

    @Override
    public void setDisable(boolean b) {
        getPropertyEditors().forEach(e -> e.setDisable(b));
    }

    @Override
    public void requestFocus() {
        List<AbstractPropertyEditor> editors = getPropertyEditors();
        if (!editors.isEmpty()) {
            editors.get(0).requestFocus();
        }
    }
        
    @Override
    public EventHandler<?> getCommitListener() {
        return null;
    }

    @Override
    public void setLayoutFormat(LayoutFormat doubleLine) {
        getPropertyEditors().forEach(e -> e.setLayoutFormat(doubleLine));
    }


    @Override
    public void setValue(Object value) {
        List<AbstractPropertyEditor> editors = getPropertyEditors();
        if (value == null) {
            getPropertyEditors().forEach(e -> e.setValue(value));
        }
        
        //TODO there is a big flaw here, there isn't any guarantee the order of values are the same. 
        
        Object[] values = null;
        if (value instanceof Collection<?>) {
            values = ((Collection<?>)value).toArray(); 
        }
        
        if (value instanceof Object[]) {
            values = (Object[])value;
        }
        
        if (values == null) {
            getPropertyEditors().forEach(e -> e.setValue(value));
        }
        
        assert editors.size() == values.length;
        
        for (int i=0;i < editors.size(); i++) {
            editors.get(i).setValue(values[i]);
        }
    }
}
