/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.WeakHashMap;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMElement;
import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPathComparator;

/**
 *
 */

public abstract class AbstractMetadata {

    private final Map<Class<?>, ComponentClassMetadata<?>> componentClassMap = new HashMap<>();
    private final Map<Class<?>, ComponentClassMetadata<?>> customComponentClassMap = new WeakHashMap<>();

    /**
     * During data introspection of an unknown custom component, if the name match,
     * the property will be ignored.
     */
    private final Set<PropertyName> hiddenProperties = new HashSet<>();

    /**
     * parent related properties can be understood as transient properties that have a meaning only in the current parent
     * Changing the parent means those properties can be deleted because the meaning is lost
     * Ex: positioning/scaling/rotation
     */
    private final Set<PropertyName> parentRelatedProperties = new HashSet<>();
    private final List<String> sectionNames = new ArrayList<>();
    private final Map<String, List<String>> subSectionMap = new HashMap<>();

    public final InspectorPathComparator INSPECTOR_PATH_COMPARATOR
            = new InspectorPathComparator(sectionNames, subSectionMap);

    protected AbstractMetadata(List<ComponentClassMetadata<?>> componentClassMetadatas) {

        // Populate componentClassMap
        componentClassMetadatas.forEach(c -> componentClassMap.put(c.getKlass(), c));

        // Populates hiddenProperties
        hiddenProperties.add(new PropertyName("activated")); //NOCHECK
        hiddenProperties.add(new PropertyName("alignWithContentOrigin")); //NOCHECK
        hiddenProperties.add(new PropertyName("armed")); //NOCHECK
        hiddenProperties.add(new PropertyName("anchor")); //NOCHECK
        hiddenProperties.add(new PropertyName("antiAliasing")); //NOCHECK
        hiddenProperties.add(new PropertyName("border")); //NOCHECK
        hiddenProperties.add(new PropertyName("background")); //NOCHECK
        hiddenProperties.add(new PropertyName("caretPosition")); //NOCHECK
        hiddenProperties.add(new PropertyName("camera")); //NOCHECK
        hiddenProperties.add(new PropertyName("cellFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("cellValueFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("characters")); //NOCHECK
        hiddenProperties.add(new PropertyName("childrenUnmodifiable")); //NOCHECK
        hiddenProperties.add(new PropertyName("chronology")); //NOCHECK
        hiddenProperties.add(new PropertyName("class")); //NOCHECK
        hiddenProperties.add(new PropertyName("comparator")); //NOCHECK
        hiddenProperties.add(new PropertyName("converter")); //NOCHECK
        hiddenProperties.add(new PropertyName("controlCssMetaData")); //NOCHECK
        hiddenProperties.add(new PropertyName("cssMetaData")); //NOCHECK
        hiddenProperties.add(new PropertyName("customColors")); //NOCHECK
        hiddenProperties.add(new PropertyName("data")); //NOCHECK
        hiddenProperties.add(new PropertyName("dayCellFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("depthBuffer")); //NOCHECK
        hiddenProperties.add(new PropertyName("disabled")); //NOCHECK
        hiddenProperties.add(new PropertyName("dividers")); //NOCHECK
        hiddenProperties.add(new PropertyName("editingCell")); //NOCHECK
        hiddenProperties.add(new PropertyName("editingIndex")); //NOCHECK
        hiddenProperties.add(new PropertyName("editingItem")); //NOCHECK
        hiddenProperties.add(new PropertyName("editor")); //NOCHECK
        hiddenProperties.add(new PropertyName("engine")); //NOCHECK
        hiddenProperties.add(new PropertyName("eventDispatcher")); //NOCHECK
        hiddenProperties.add(new PropertyName("expandedPane")); //NOCHECK
        hiddenProperties.add(new PropertyName("filter")); //NOCHECK
        hiddenProperties.add(new PropertyName("focused")); //NOCHECK
        hiddenProperties.add(new PropertyName("focusModel")); //NOCHECK
        hiddenProperties.add(new PropertyName("graphicsContext2D")); //NOCHECK
        hiddenProperties.add(new PropertyName("hover")); //NOCHECK
        hiddenProperties.add(new PropertyName("inputMethodRequests")); //NOCHECK
        hiddenProperties.add(new PropertyName("localToParentTransform")); //NOCHECK
        hiddenProperties.add(new PropertyName("localToSceneTransform")); //NOCHECK
        hiddenProperties.add(new PropertyName("managed")); //NOCHECK
        hiddenProperties.add(new PropertyName("mediaPlayer")); //NOCHECK
        hiddenProperties.add(new PropertyName("needsLayout")); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeColumnEnd", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeColumnIndex", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeColumnSpan", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeHgrow", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeMargin", javafx.scene.layout.BorderPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeRowEnd", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeRowIndex", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeRowSpan", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("nodeVgrow", javafx.scene.layout.GridPane.class)); //NOCHECK
        hiddenProperties.add(new PropertyName("ownerWindow")); //NOCHECK
        hiddenProperties.add(new PropertyName("ownerNode")); //NOCHECK
        hiddenProperties.add(new PropertyName("pageFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("paragraphs")); //NOCHECK
        hiddenProperties.add(new PropertyName("parent")); //NOCHECK
        hiddenProperties.add(new PropertyName("parentColumn")); //NOCHECK
        hiddenProperties.add(new PropertyName("parentMenu")); //NOCHECK
        hiddenProperties.add(new PropertyName("parentPopup")); //NOCHECK
        hiddenProperties.add(new PropertyName("pressed")); //NOCHECK
        hiddenProperties.add(new PropertyName("properties")); //NOCHECK
        hiddenProperties.add(new PropertyName("pseudoClassStates")); //NOCHECK
        hiddenProperties.add(new PropertyName("redoable")); //NOCHECK
        hiddenProperties.add(new PropertyName("root")); //NOCHECK
        hiddenProperties.add(new PropertyName("rowFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("scene")); //NOCHECK
        hiddenProperties.add(new PropertyName("selection")); //NOCHECK
        hiddenProperties.add(new PropertyName("selectionModel")); //NOCHECK
        hiddenProperties.add(new PropertyName("selectedText")); //NOCHECK
        hiddenProperties.add(new PropertyName("showing")); //NOCHECK
        hiddenProperties.add(new PropertyName("sortPolicy")); //NOCHECK
        hiddenProperties.add(new PropertyName("skin")); //NOCHECK
        hiddenProperties.add(new PropertyName("strokeDashArray")); //NOCHECK
        hiddenProperties.add(new PropertyName("styleableParent")); //NOCHECK
        hiddenProperties.add(new PropertyName("tableView")); //NOCHECK
        hiddenProperties.add(new PropertyName("tabPane")); //NOCHECK
        hiddenProperties.add(new PropertyName("transforms")); //NOCHECK
        hiddenProperties.add(new PropertyName("treeTableView")); //NOCHECK
        hiddenProperties.add(new PropertyName("typeInternal")); //NOCHECK
        hiddenProperties.add(new PropertyName("typeSelector")); //NOCHECK
        hiddenProperties.add(new PropertyName("undoable")); //NOCHECK
        hiddenProperties.add(new PropertyName("userData")); //NOCHECK
        hiddenProperties.add(new PropertyName("useSystemMenuBar")); //NOCHECK
        hiddenProperties.add(new PropertyName("valueChanging")); //NOCHECK
        hiddenProperties.add(new PropertyName("valueConverter")); //NOCHECK
        hiddenProperties.add(new PropertyName("valueFactory")); //NOCHECK
        hiddenProperties.add(new PropertyName("visibleLeafColumns")); //NOCHECK

        // Populates parentRelatedProperties
//        parentRelatedProperties.add(PropertyNames.layoutXName);
//        parentRelatedProperties.add(PropertyNames.layoutYName);
//        parentRelatedProperties.add(PropertyNames.translateXName);
//        parentRelatedProperties.add(PropertyNames.translateYName);
//        parentRelatedProperties.add(PropertyNames.translateZName);
//        parentRelatedProperties.add(PropertyNames.scaleXName);
//        parentRelatedProperties.add(PropertyNames.scaleYName);
//        parentRelatedProperties.add(PropertyNames.scaleZName);
//        parentRelatedProperties.add(PropertyNames.rotationAxisName);
//        parentRelatedProperties.add(PropertyNames.rotateName);
        parentRelatedProperties.add(new PropertyName("layoutX"));
        parentRelatedProperties.add(new PropertyName("layoutY"));
        parentRelatedProperties.add(new PropertyName("translateX"));
        parentRelatedProperties.add(new PropertyName("translateY"));
        parentRelatedProperties.add(new PropertyName("translateZ"));
        parentRelatedProperties.add(new PropertyName("scaleX"));
        parentRelatedProperties.add(new PropertyName("scaleY"));
        parentRelatedProperties.add(new PropertyName("scaleZ"));
        parentRelatedProperties.add(new PropertyName("rotationAxis"));
        parentRelatedProperties.add(new PropertyName("rotate"));

        // Populates sectionNames
        sectionNames.add("Properties"); //NOCHECK
        sectionNames.add("Layout"); //NOCHECK
        sectionNames.add("Code"); //NOCHECK

        // Populates subSectionMap
        final List<String> ss0 = new ArrayList<>();
        ss0.add("Custom"); //NOCHECK
        ss0.add("Text"); //NOCHECK
        ss0.add("Specific"); //NOCHECK
        ss0.add("Graphic"); //NOCHECK
        ss0.add("3D"); //NOCHECK
        ss0.add("Pagination"); //NOCHECK
        ss0.add("Stroke"); //NOCHECK
        ss0.add("Node"); //NOCHECK
        ss0.add("JavaFX CSS"); //NOCHECK
        ss0.add("Extras"); //NOCHECK
        ss0.add("Accessibility"); //NOCHECK
        subSectionMap.put("Properties", ss0); //NOCHECK
        final List<String> ss1 = new ArrayList<>();
        ss1.add("Anchor Pane Constraints"); //NOCHECK
        ss1.add("Border Pane Constraints"); //NOCHECK
        ss1.add("Flow Pane Constraints"); //NOCHECK
        ss1.add("Grid Pane Constraints"); //NOCHECK
        ss1.add("HBox Constraints"); //NOCHECK
        ss1.add("Split Pane Constraints"); //NOCHECK
        ss1.add("Stack Pane Constraints"); //NOCHECK
        ss1.add("Tile Pane Constraints"); //NOCHECK
        ss1.add("VBox Constraints"); //NOCHECK
        ss1.add("Internal"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        ss1.add("Size"); //NOCHECK
        ss1.add("Position"); //NOCHECK
        ss1.add("Transforms"); //NOCHECK
        ss1.add("Bounds"); //NOCHECK
        ss1.add("Extras"); //NOCHECK
        ss1.add("Specific"); //NOCHECK
        subSectionMap.put("Layout", ss1); //NOCHECK
        final List<String> ss2 = new ArrayList<>();
        ss2.add("Main"); //NOCHECK
        ss2.add("Edit"); //NOCHECK
        ss2.add("DragDrop"); //NOCHECK
        ss2.add("Closing"); //NOCHECK
        ss2.add("HideShow"); //NOCHECK
        ss2.add("Keyboard"); //NOCHECK
        ss2.add("Mouse"); //NOCHECK
        ss2.add("Rotation"); //NOCHECK
        ss2.add("Swipe"); //NOCHECK
        ss2.add("Touch"); //NOCHECK
        ss2.add("Zoom"); //NOCHECK
        subSectionMap.put("Code", ss2); //NOCHECK
    }


    public ComponentClassMetadata<?> queryComponentMetadata(Class<?> componentClass) {
        final ComponentClassMetadata<?> result;


        final ComponentClassMetadata<?> componentMetadata = componentClassMap.get(componentClass);
        if (componentMetadata != null) {
            // componentClass is a certified component
            result = componentMetadata;
        } else {
            // componentClass is a custom component
            final ComponentClassMetadata<?> customMetadata = customComponentClassMap.get(componentClass);
            if (customMetadata != null) {
                // componentClass has already been introspected
                result = customMetadata;
            } else {
                // componentClass must be introspected
                // Let's find the first certified ancestor
                Class<?> ancestorClass = componentClass.getSuperclass();
                ComponentClassMetadata<?> ancestorMetadata = null;
                while ((ancestorClass != null) && (ancestorMetadata == null)) {
                    ancestorMetadata = componentClassMap.get(ancestorClass);
                    ancestorClass = ancestorClass.getSuperclass();
                }
                final MetadataIntrospector introspector
                        = new MetadataIntrospector(componentClass, ancestorMetadata, this);
                result = introspector.introspect();
                customComponentClassMap.put(componentClass, result);
            }
        }

        return result;
    }


    public Set<PropertyMetadata> queryProperties(Class<?> componentClass) {
        final Map<PropertyName, PropertyMetadata> result = new HashMap<>();
        ComponentClassMetadata<?> classMetadata = queryComponentMetadata(componentClass);

        Set<PropertyName> shadowed = new HashSet<>();
        while (classMetadata != null) {
            for (PropertyMetadata pm : classMetadata.getProperties()) {
                if (result.containsKey(pm.getName()) == false) {
                    result.put(pm.getName(), pm);
                }
            }
            shadowed.addAll(classMetadata.getShadowedProperties());
            classMetadata = classMetadata.getParentMetadata();
        }
        shadowed.forEach(result::remove);

        return new HashSet<>(result.values());
    }


    public Set<PropertyMetadata> queryProperties(Collection<Class<?>> componentClasses) {
        final Set<PropertyMetadata> result = new HashSet<>();

        int count = 0;
        for (Class<?> componentClass : componentClasses) {
            final Set<PropertyMetadata> propertyMetadata = queryProperties(componentClass);
            if (count == 0) {
                result.addAll(propertyMetadata);
            } else {
                result.retainAll(propertyMetadata);
            }
            count++;
        }

        return result;
    }


    public Set<ComponentPropertyMetadata> queryComponentProperties(Class<?> componentClass) {
        final Set<ComponentPropertyMetadata> result = new HashSet<>();

        for (PropertyMetadata propertyMetadata : queryProperties(Arrays.asList(componentClass))) {
            if (propertyMetadata instanceof ComponentPropertyMetadata) {
                result.add((ComponentPropertyMetadata) propertyMetadata);
            }
        }
        return result;
    }


    public ComponentPropertyMetadata queryComponentProperty(Class<?> componentClass, PropertyName name) {
        ComponentClassMetadata<?> classMetadata = queryComponentMetadata(componentClass);
        Optional<ComponentPropertyMetadata> result = classMetadata.getAllSubComponentProperties().stream()
            .filter(scp -> scp.getName().equals(name))
            .findFirst();
        return result.isEmpty() ? null : result.get();
    }


    public Set<ValuePropertyMetadata> queryValueProperties(Set<Class<?>> componentClasses) {
        final Set<ValuePropertyMetadata> result = new HashSet<>();
        for (PropertyMetadata propertyMetadata : queryProperties(componentClasses)) {
            if (propertyMetadata instanceof ValuePropertyMetadata) {
                result.add((ValuePropertyMetadata) propertyMetadata);
            }
        }
        return result;
    }


    public PropertyMetadata queryProperty(Class<?> componentClass, PropertyName targetName) {
        final Set<PropertyMetadata> propertyMetadataSet = queryProperties(componentClass);
        final Iterator<PropertyMetadata> iterator = propertyMetadataSet.iterator();
        PropertyMetadata result = null;

        while ((result == null) && iterator.hasNext()) {
            final PropertyMetadata propertyMetadata = iterator.next();
            if (propertyMetadata.getName().equals(targetName)) {
                return propertyMetadata;
            }

            if (propertyMetadata.isGroup()) {
                PropertyGroupMetadata pgm = (PropertyGroupMetadata)propertyMetadata;
                for (int i=0; i < pgm.getProperties().length; i++) {
                    if (pgm.getProperties()[i].getName().equals(targetName)) {
                        return pgm.getProperties()[i];
                    }
                }
            }
        }
        return null;
    }


    public ValuePropertyMetadata queryValueProperty(FXOMElement fxomInstance, PropertyName targetName) {
        final ValuePropertyMetadata result;
        assert fxomInstance != null;
        assert targetName != null;

        if (fxomInstance.getMetadataClass() == null) {
            // FXOM object is unresolved
            result = null;
        } else {
            final Class<?> componentClass = fxomInstance.getMetadataClass();

            final PropertyMetadata m = queryProperty(componentClass, targetName);
            if (m instanceof ValuePropertyMetadata) {
                result = (ValuePropertyMetadata) m;
            } else {
                result = null;
            }
        }

        return result;
    }



    public Collection<ComponentClassMetadata<?>> getComponentClasses() {
        return componentClassMap.values();
    }


    public Set<PropertyName> getHiddenProperties() {
        return hiddenProperties;
    }

    /**
     * During prune properties job a property is trimmed
     * if the property is static
     * if the property is transient (has a meaning in the current parent only)
     * @param name
     * @return
     */

    public boolean isPropertyTrimmingNeeded(PropertyName name) {
        final boolean result;

        if (name.getResidenceClass() != null) {
            // It's a static property eg GridPane.rowIndex
            // All static property are "parent related" and needs trimming
            result = true;
        } else {
            result = parentRelatedProperties.contains(name);
        }

        return result;
    }

    // The following properties have been rejected:
    //     javafx.embed.swing.SwingNode -> content : Property type (JComponent) is not certified
    //     javafx.scene.control.ChoiceBox -> items : Property items has no section/subsection assigned
    //     javafx.scene.control.ComboBox -> items : Property items has no section/subsection assigned
    //     javafx.scene.control.ListView -> items : Property items has no section/subsection assigned
    //     javafx.scene.control.TableColumnBase -> columns : Property is a collection but type of its items is unknown
    //     javafx.scene.control.TableView -> items : Property items has no section/subsection assigned


    // No uncertified properties have been found


    public ComponentClassMetadata<?> queryComponentMetadata(Class<?> clazz, PropertyName propName) {

        ComponentClassMetadata<?> classMeta = queryComponentMetadata(clazz);
        while (classMeta != null) {
            for (PropertyMetadata propMeta : classMeta.getProperties()) {
                if (propMeta.getName().compareTo(propName) == 0) {
                    return classMeta;
                }
            }
            // Check the inherited classes
            classMeta = classMeta.getParentMetadata();
        }
        return null;
    }
}


