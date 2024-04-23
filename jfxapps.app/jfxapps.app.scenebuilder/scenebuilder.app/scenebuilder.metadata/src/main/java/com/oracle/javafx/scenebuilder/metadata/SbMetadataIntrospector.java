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
package com.oracle.javafx.scenebuilder.metadata;

import static com.oracle.javafx.scenebuilder.metadata.custom.InspectorPath.CUSTOM_SECTION;
import static com.oracle.javafx.scenebuilder.metadata.custom.InspectorPath.CUSTOM_SUB_SECTION;

import java.beans.PropertyDescriptor;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.MetadataIntrospector;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentClassMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.ComponentPropertyMetadataCustomization;
import com.oracle.javafx.scenebuilder.metadata.custom.InspectorPath;
import com.oracle.javafx.scenebuilder.metadata.custom.ValuePropertyMetadataCustomization;

public class SbMetadataIntrospector extends
        MetadataIntrospector<ComponentClassMetadataCustomization, ComponentPropertyMetadataCustomization, ValuePropertyMetadataCustomization> {

    public SbMetadataIntrospector() {

        // Populates hiddenProperties
        addHiddenProperty(new PropertyName("activated")); // NOCHECK
        addHiddenProperty(new PropertyName("alignWithContentOrigin")); // NOCHECK
        addHiddenProperty(new PropertyName("armed")); // NOCHECK
        addHiddenProperty(new PropertyName("anchor")); // NOCHECK
        addHiddenProperty(new PropertyName("antiAliasing")); // NOCHECK
        addHiddenProperty(new PropertyName("border")); // NOCHECK
        addHiddenProperty(new PropertyName("background")); // NOCHECK
        addHiddenProperty(new PropertyName("caretPosition")); // NOCHECK
        addHiddenProperty(new PropertyName("camera")); // NOCHECK
        addHiddenProperty(new PropertyName("cellFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("cellValueFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("characters")); // NOCHECK
        addHiddenProperty(new PropertyName("childrenUnmodifiable")); // NOCHECK
        addHiddenProperty(new PropertyName("chronology")); // NOCHECK
        addHiddenProperty(new PropertyName("class")); // NOCHECK
        addHiddenProperty(new PropertyName("comparator")); // NOCHECK
        addHiddenProperty(new PropertyName("converter")); // NOCHECK
        addHiddenProperty(new PropertyName("controlCssMetaData")); // NOCHECK
        addHiddenProperty(new PropertyName("cssMetaData")); // NOCHECK
        addHiddenProperty(new PropertyName("customColors")); // NOCHECK
        addHiddenProperty(new PropertyName("data")); // NOCHECK
        addHiddenProperty(new PropertyName("dayCellFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("depthBuffer")); // NOCHECK
        addHiddenProperty(new PropertyName("disabled")); // NOCHECK
        addHiddenProperty(new PropertyName("dividers")); // NOCHECK
        addHiddenProperty(new PropertyName("editingCell")); // NOCHECK
        addHiddenProperty(new PropertyName("editingIndex")); // NOCHECK
        addHiddenProperty(new PropertyName("editingItem")); // NOCHECK
        addHiddenProperty(new PropertyName("editor")); // NOCHECK
        addHiddenProperty(new PropertyName("engine")); // NOCHECK
        addHiddenProperty(new PropertyName("eventDispatcher")); // NOCHECK
        addHiddenProperty(new PropertyName("expandedPane")); // NOCHECK
        addHiddenProperty(new PropertyName("filter")); // NOCHECK
        addHiddenProperty(new PropertyName("focused")); // NOCHECK
        addHiddenProperty(new PropertyName("focusModel")); // NOCHECK
        addHiddenProperty(new PropertyName("graphicsContext2D")); // NOCHECK
        addHiddenProperty(new PropertyName("hover")); // NOCHECK
        addHiddenProperty(new PropertyName("inputMethodRequests")); // NOCHECK
        addHiddenProperty(new PropertyName("localToParentTransform")); // NOCHECK
        addHiddenProperty(new PropertyName("localToSceneTransform")); // NOCHECK
        addHiddenProperty(new PropertyName("managed")); // NOCHECK
        addHiddenProperty(new PropertyName("mediaPlayer")); // NOCHECK
        addHiddenProperty(new PropertyName("needsLayout")); // NOCHECK
        addHiddenProperty(new PropertyName("nodeColumnEnd", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeColumnIndex", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeColumnSpan", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeHgrow", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeMargin", javafx.scene.layout.BorderPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeRowEnd", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeRowIndex", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeRowSpan", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("nodeVgrow", javafx.scene.layout.GridPane.class)); // NOCHECK
        addHiddenProperty(new PropertyName("ownerWindow")); // NOCHECK
        addHiddenProperty(new PropertyName("ownerNode")); // NOCHECK
        addHiddenProperty(new PropertyName("pageFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("paragraphs")); // NOCHECK
        addHiddenProperty(new PropertyName("parent")); // NOCHECK
        addHiddenProperty(new PropertyName("parentColumn")); // NOCHECK
        addHiddenProperty(new PropertyName("parentMenu")); // NOCHECK
        addHiddenProperty(new PropertyName("parentPopup")); // NOCHECK
        addHiddenProperty(new PropertyName("pressed")); // NOCHECK
        addHiddenProperty(new PropertyName("properties")); // NOCHECK
        addHiddenProperty(new PropertyName("pseudoClassStates")); // NOCHECK
        addHiddenProperty(new PropertyName("redoable")); // NOCHECK
        addHiddenProperty(new PropertyName("root")); // NOCHECK
        addHiddenProperty(new PropertyName("rowFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("scene")); // NOCHECK
        addHiddenProperty(new PropertyName("selection")); // NOCHECK
        addHiddenProperty(new PropertyName("selectionModel")); // NOCHECK
        addHiddenProperty(new PropertyName("selectedText")); // NOCHECK
        addHiddenProperty(new PropertyName("showing")); // NOCHECK
        addHiddenProperty(new PropertyName("sortPolicy")); // NOCHECK
        addHiddenProperty(new PropertyName("skin")); // NOCHECK
        addHiddenProperty(new PropertyName("strokeDashArray")); // NOCHECK
        addHiddenProperty(new PropertyName("styleableParent")); // NOCHECK
        addHiddenProperty(new PropertyName("tableView")); // NOCHECK
        addHiddenProperty(new PropertyName("tabPane")); // NOCHECK
        addHiddenProperty(new PropertyName("transforms")); // NOCHECK
        addHiddenProperty(new PropertyName("treeTableView")); // NOCHECK
        addHiddenProperty(new PropertyName("typeInternal")); // NOCHECK
        addHiddenProperty(new PropertyName("typeSelector")); // NOCHECK
        addHiddenProperty(new PropertyName("undoable")); // NOCHECK
        addHiddenProperty(new PropertyName("userData")); // NOCHECK
        addHiddenProperty(new PropertyName("useSystemMenuBar")); // NOCHECK
        addHiddenProperty(new PropertyName("valueChanging")); // NOCHECK
        addHiddenProperty(new PropertyName("valueConverter")); // NOCHECK
        addHiddenProperty(new PropertyName("valueFactory")); // NOCHECK
        addHiddenProperty(new PropertyName("visibleLeafColumns")); // NOCHECK

    }

    @Override
    protected ComponentClassMetadataCustomization customizeComponent(Class<?> componentClass,
            ComponentClassMetadata<?, ComponentClassMetadataCustomization, ComponentPropertyMetadataCustomization, ValuePropertyMetadataCustomization> ancestorMetadata,
            Set<PropertyMetadata<?>> properties, Exception exception) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ComponentPropertyMetadataCustomization customizeComponentProperty(PropertyDescriptor d, int counter) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected ValuePropertyMetadataCustomization customizeValueProperty(PropertyDescriptor d, int counter) {
        final InspectorPath inspectorPath = new InspectorPath(CUSTOM_SECTION, CUSTOM_SUB_SECTION, counter++);
        return new ValuePropertyMetadataCustomization.Builder().inspectorPath(inspectorPath).build();
    }

}
