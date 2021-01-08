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
package com.oracle.javafx.scenebuilder.core.metadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.oracle.javafx.scenebuilder.core.fxom.FXOMInstance;
import com.oracle.javafx.scenebuilder.core.fxom.FXOMIntrinsic;
import com.oracle.javafx.scenebuilder.core.metadata.klass.ComponentClassMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.InspectorPathComparator;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

/**
 *
 */
@Component
public class Metadata implements InitializingBean {
    
    private static Metadata metadata = null;
    
    
    private final Map<Class<?>, ComponentClassMetadata> componentClassMap = new HashMap<>();
    private final Map<Class<?>, ComponentClassMetadata> customComponentClassMap = new WeakHashMap<>();
    private final Set<PropertyName> hiddenProperties = new HashSet<>();
    private final Set<PropertyName> parentRelatedProperties = new HashSet<>();
    private final List<String> sectionNames = new ArrayList<>();
    private final Map<String, List<String>> subSectionMap = new HashMap<>();
    
    public final InspectorPathComparator INSPECTOR_PATH_COMPARATOR
            = new InspectorPathComparator(sectionNames, subSectionMap);

    // TODO remove me
    public static synchronized Metadata getMetadata() {
    	assert metadata != null;
        return metadata;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        metadata = this;
    }
    
    private Metadata(
            @Autowired List<ComponentClassMetadata<?>> componentClassMetadatas
            
            ) {

        // Populate componentClassMap
        componentClassMetadatas.forEach(c -> componentClassMap.put(c.getKlass(), c));
        
        // Populates hiddenProperties
        hiddenProperties.add(new PropertyName("activated"));
        hiddenProperties.add(new PropertyName("alignWithContentOrigin"));
        hiddenProperties.add(new PropertyName("armed"));
        hiddenProperties.add(new PropertyName("anchor"));
        hiddenProperties.add(new PropertyName("antiAliasing"));
        hiddenProperties.add(new PropertyName("border"));
        hiddenProperties.add(new PropertyName("background"));
        hiddenProperties.add(new PropertyName("caretPosition"));
        hiddenProperties.add(new PropertyName("camera"));
        hiddenProperties.add(new PropertyName("cellFactory"));
        hiddenProperties.add(new PropertyName("cellValueFactory"));
        hiddenProperties.add(new PropertyName("characters"));
        hiddenProperties.add(new PropertyName("childrenUnmodifiable"));
        hiddenProperties.add(new PropertyName("chronology"));
        hiddenProperties.add(new PropertyName("class"));
        hiddenProperties.add(new PropertyName("comparator"));
        hiddenProperties.add(new PropertyName("converter"));
        hiddenProperties.add(new PropertyName("controlCssMetaData"));
        hiddenProperties.add(new PropertyName("cssMetaData"));
        hiddenProperties.add(new PropertyName("customColors"));
        hiddenProperties.add(new PropertyName("data"));
        hiddenProperties.add(new PropertyName("dayCellFactory"));
        hiddenProperties.add(new PropertyName("depthBuffer"));
        hiddenProperties.add(new PropertyName("disabled"));
        hiddenProperties.add(new PropertyName("dividers"));
        hiddenProperties.add(new PropertyName("editingCell"));
        hiddenProperties.add(new PropertyName("editingIndex"));
        hiddenProperties.add(new PropertyName("editingItem"));
        hiddenProperties.add(new PropertyName("editor"));
        hiddenProperties.add(new PropertyName("engine"));
        hiddenProperties.add(new PropertyName("eventDispatcher"));
        hiddenProperties.add(new PropertyName("expandedPane"));
        hiddenProperties.add(new PropertyName("filter"));
        hiddenProperties.add(new PropertyName("focused"));
        hiddenProperties.add(new PropertyName("focusModel"));
        hiddenProperties.add(new PropertyName("graphicsContext2D"));
        hiddenProperties.add(new PropertyName("hover"));
        hiddenProperties.add(new PropertyName("inputMethodRequests"));
        hiddenProperties.add(new PropertyName("localToParentTransform"));
        hiddenProperties.add(new PropertyName("localToSceneTransform"));
        hiddenProperties.add(new PropertyName("managed"));
        hiddenProperties.add(new PropertyName("mediaPlayer"));
        hiddenProperties.add(new PropertyName("needsLayout"));
        hiddenProperties.add(new PropertyName("nodeColumnEnd", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeColumnIndex", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeColumnSpan", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeHgrow", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeMargin", javafx.scene.layout.BorderPane.class));
        hiddenProperties.add(new PropertyName("nodeRowEnd", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeRowIndex", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeRowSpan", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("nodeVgrow", javafx.scene.layout.GridPane.class));
        hiddenProperties.add(new PropertyName("ownerWindow"));
        hiddenProperties.add(new PropertyName("ownerNode"));
        hiddenProperties.add(new PropertyName("pageFactory"));
        hiddenProperties.add(new PropertyName("paragraphs"));
        hiddenProperties.add(new PropertyName("parent"));
        hiddenProperties.add(new PropertyName("parentColumn"));
        hiddenProperties.add(new PropertyName("parentMenu"));
        hiddenProperties.add(new PropertyName("parentPopup"));
        hiddenProperties.add(new PropertyName("pressed"));
        hiddenProperties.add(new PropertyName("properties"));
        hiddenProperties.add(new PropertyName("pseudoClassStates"));
        hiddenProperties.add(new PropertyName("redoable"));
        hiddenProperties.add(new PropertyName("root"));
        hiddenProperties.add(new PropertyName("rowFactory"));
        hiddenProperties.add(new PropertyName("scene"));
        hiddenProperties.add(new PropertyName("selection"));
        hiddenProperties.add(new PropertyName("selectionModel"));
        hiddenProperties.add(new PropertyName("selectedText"));
        hiddenProperties.add(new PropertyName("showing"));
        hiddenProperties.add(new PropertyName("sortPolicy"));
        hiddenProperties.add(new PropertyName("skin"));
        hiddenProperties.add(new PropertyName("strokeDashArray"));
        hiddenProperties.add(new PropertyName("styleableParent"));
        hiddenProperties.add(new PropertyName("tableView"));
        hiddenProperties.add(new PropertyName("tabPane"));
        hiddenProperties.add(new PropertyName("transforms"));
        hiddenProperties.add(new PropertyName("treeTableView"));
        hiddenProperties.add(new PropertyName("typeInternal"));
        hiddenProperties.add(new PropertyName("typeSelector"));
        hiddenProperties.add(new PropertyName("undoable"));
        hiddenProperties.add(new PropertyName("userData"));
        hiddenProperties.add(new PropertyName("useSystemMenuBar"));
        hiddenProperties.add(new PropertyName("valueChanging"));
        hiddenProperties.add(new PropertyName("valueConverter"));
        hiddenProperties.add(new PropertyName("valueFactory"));
        hiddenProperties.add(new PropertyName("visibleLeafColumns"));

        // Populates parentRelatedProperties
        parentRelatedProperties.add(PropertyNames.layoutXName);
        parentRelatedProperties.add(PropertyNames.layoutYName);
        parentRelatedProperties.add(PropertyNames.translateXName);
        parentRelatedProperties.add(PropertyNames.translateYName);
        parentRelatedProperties.add(PropertyNames.translateZName);
        parentRelatedProperties.add(PropertyNames.scaleXName);
        parentRelatedProperties.add(PropertyNames.scaleYName);
        parentRelatedProperties.add(PropertyNames.scaleZName);
        parentRelatedProperties.add(PropertyNames.rotationAxisName);
        parentRelatedProperties.add(PropertyNames.rotateName);

        // Populates sectionNames
        sectionNames.add("Properties");
        sectionNames.add("Layout");
        sectionNames.add("Code");

        // Populates subSectionMap
        final List<String> ss0 = new ArrayList<>();
        ss0.add("Custom");
        ss0.add("Text");
        ss0.add("Specific");
        ss0.add("Graphic");
        ss0.add("3D");
        ss0.add("Pagination");
        ss0.add("Stroke");
        ss0.add("Node");
        ss0.add("JavaFX CSS");
        ss0.add("Extras");
        ss0.add("Accessibility");
        subSectionMap.put("Properties", ss0);
        final List<String> ss1 = new ArrayList<>();
        ss1.add("Anchor Pane Constraints");
        ss1.add("Border Pane Constraints");
        ss1.add("Flow Pane Constraints");
        ss1.add("Grid Pane Constraints");
        ss1.add("HBox Constraints");
        ss1.add("Split Pane Constraints");
        ss1.add("Stack Pane Constraints");
        ss1.add("Tile Pane Constraints");
        ss1.add("VBox Constraints");
        ss1.add("Internal");
        ss1.add("Specific");
        ss1.add("Size");
        ss1.add("Position");
        ss1.add("Transforms");
        ss1.add("Bounds");
        ss1.add("Extras");
        ss1.add("Specific");
        subSectionMap.put("Layout", ss1);
        final List<String> ss2 = new ArrayList<>();
        ss2.add("Main");
        ss2.add("Edit");
        ss2.add("DragDrop");
        ss2.add("Closing");
        ss2.add("HideShow");
        ss2.add("Keyboard");
        ss2.add("Mouse");
        ss2.add("Rotation");
        ss2.add("Swipe");
        ss2.add("Touch");
        ss2.add("Zoom");
        subSectionMap.put("Code", ss2);
    }
    
    public ComponentClassMetadata queryComponentMetadata(Class<?> componentClass) {
        final ComponentClassMetadata result;
        
        
        final ComponentClassMetadata componentMetadata
                = componentClassMap.get(componentClass);
        if (componentMetadata != null) {
            // componentClass is a certified component
            result = componentMetadata;
        } else {
            // componentClass is a custom component
            final ComponentClassMetadata customMetadata
                    = customComponentClassMap.get(componentClass);
            if (customMetadata != null) {
                // componentClass has already been introspected
                result = customMetadata;
            } else {
                // componentClass must be introspected
                // Let's find the first certified ancestor
                Class<?> ancestorClass = componentClass.getSuperclass();
                ComponentClassMetadata ancestorMetadata = null;
                while ((ancestorClass != null) && (ancestorMetadata == null)) {
                    ancestorMetadata = componentClassMap.get(ancestorClass);
                    ancestorClass = ancestorClass.getSuperclass();
                }
                final MetadataIntrospector introspector
                        = new MetadataIntrospector(componentClass, ancestorMetadata);
                result = introspector.introspect();
                customComponentClassMap.put(componentClass, result);
            }
        }
        
        return result;
    }
    
    public Set<PropertyMetadata> queryProperties(Class<?> componentClass) {
        final Map<PropertyName, PropertyMetadata> result = new HashMap<>();
        ComponentClassMetadata<?> classMetadata = queryComponentMetadata(componentClass);
        
        while (classMetadata != null) {
            for (PropertyMetadata pm : classMetadata.getProperties()) {
                if (result.containsKey(pm.getName()) == false) {
                    result.put(pm.getName(), pm);
                }
            }
            classMetadata = classMetadata.getParentMetadata();
        }
        
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

    public ValuePropertyMetadata queryValueProperty(FXOMInstance fxomInstance, PropertyName targetName) {
        final ValuePropertyMetadata result;
        assert fxomInstance != null;
        assert targetName != null;

        if (fxomInstance.getSceneGraphObject() == null) {
            // FXOM object is unresolved
            result = null;
        } else {
            final Class<?> componentClass;
            if (fxomInstance.getDeclaredClass() == FXOMIntrinsic.class) {
                componentClass = fxomInstance.getDeclaredClass();
            } else {
                componentClass = fxomInstance.getSceneGraphObject().getClass();
            }

            final PropertyMetadata m = Metadata.getMetadata().queryProperty(componentClass, targetName);
            if (m instanceof ValuePropertyMetadata) {
                result = (ValuePropertyMetadata) m;
            } else {
                result = null;
            }
        }

        return result;
    }
    
    
    public Collection<ComponentClassMetadata> getComponentClasses() {
        return componentClassMap.values();
    }

    public Set<PropertyName> getHiddenProperties() {
        return hiddenProperties;
    }

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

}


