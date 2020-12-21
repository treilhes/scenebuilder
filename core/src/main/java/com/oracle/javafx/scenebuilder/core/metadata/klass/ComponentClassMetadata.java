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
 *  - Neither the name of Oracle Corporation nor the names of its
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
package com.oracle.javafx.scenebuilder.core.metadata.klass;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.util.PropertyName;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;

/**
 * This class describes an fxml component class 
 * 
 */
public class ComponentClassMetadata<T> extends ClassMetadata<T> {
    
    /** The component properties. */
    private final ObservableSet<PropertyMetadata> properties = FXCollections.observableSet(new HashSet<>());
    
    /** The component properties values subset. */
    private final Set<ValuePropertyMetadata> values = new HashSet<>();
    
    /** The component properties component subset. */
    private final Set<ComponentPropertyMetadata> subComponents = new HashSet<>();
    
    /** The free child positioning flag. default false */
    private final boolean freeChildPositioning;
    
    /** The inherited parent metadata. */
    private final ComponentClassMetadata<?> parentMetadata;

    /**
     * Instantiates a new component class metadata.
     *
     * @param klass the component's class
     * @param parentMetadata the inherited parent component's metadata
     */
    public ComponentClassMetadata(Class<T> klass, ComponentClassMetadata<?> parentMetadata) {
        super(klass);
        this.parentMetadata = parentMetadata;
        this.freeChildPositioning = false; // TODO(elp)
        setupSetSync();
    } 

    private void setupSetSync() {
        properties.addListener((Change<? extends PropertyMetadata> e) -> {
            if (e.wasAdded() && e.getElementAdded() != null) {
                if (e.getElementAdded().getClass().isAssignableFrom(ValuePropertyMetadata.class)) {
                    values.add((ValuePropertyMetadata)e.getElementAdded());
                } else if (e.getElementAdded().getClass().isAssignableFrom(ComponentPropertyMetadata.class)) {
                    subComponents.add((ComponentPropertyMetadata)e.getElementAdded());
                } 
            } else if (e.wasRemoved() && e.getElementRemoved() != null) {
                if (e.getElementRemoved().getClass().isAssignableFrom(ValuePropertyMetadata.class)) {
                    values.remove(e.getElementAdded());
                } else if (e.getElementRemoved().getClass().isAssignableFrom(ComponentPropertyMetadata.class)) {
                    subComponents.remove(e.getElementAdded());
                } 
            }
            
        });
    }
    /**
     * Gets the component's properties.
     *
     * @return the properties
     */
    public Set<PropertyMetadata> getProperties() {
        return properties;
    }
    
    /**
     * Gets the component's properties values subset.
     *
     * @return the values subset properties
     */
    public Set<ValuePropertyMetadata> getValueProperties() {
        return Collections.unmodifiableSet(values);
    }
    
    /**
     * Gets the component's properties sub components subset.
     *
     * @return the components subset properties
     */
    public Set<ComponentPropertyMetadata> getSubComponentProperties() {
        return Collections.unmodifiableSet(subComponents);
    }

    /**
     * Gets the sub component property.
     * Components with more than one sub component properties are ignored 
     * and those properties are treated as accessories
     *
     * @return the sub component property or null if none or more than one
     */
    //TODO find a way to handle multiple sub component properties without using special cases "if"
    //TODO enable handling future "multiple sub component properties" in a generic way
    public PropertyName getSubComponentProperty() {
        //return getSubComponentPropertyV2();
        PropertyName result = null;
        Class<?> componentClass = getKlass();
        
        if (componentClass == javafx.scene.layout.BorderPane.class) {
            // We consider that BorderPane has no subcomponents.
            // left, right, bottom and top components are treated as "accessories".
            result = null;
        } else if (componentClass == javafx.scene.control.DialogPane.class) {
            // We consider that DialogPane has no subcomponents.
            // content, expanded content, header and graphic components are treated as "accessories".
            result = null;
        } else {
            while ((result == null) && (componentClass != null)) {
                result = getSubComponentProperty(componentClass);
                componentClass = componentClass.getSuperclass();
            }
        }

        return result;
    }
    
    public PropertyName getSubComponentPropertyV2() {
        PropertyName result = null;
        ComponentClassMetadata<?> componentClass = this;
        
        if (getKlass() == javafx.scene.layout.BorderPane.class) {
            // We consider that BorderPane has no subcomponents.
            // left, right, bottom and top components are treated as "accessories".
            result = null;
        } else if (getKlass() == javafx.scene.control.DialogPane.class) {
            // We consider that DialogPane has no subcomponents.
            // content, expanded content, header and graphic components are treated as "accessories".
            result = null;
        } else {
            while ((result == null) && (componentClass != null)) {
                result = componentClass.getSubComponentProperties().size() == 1 ?
                            componentClass.getSubComponentProperties().stream().findFirst().get().getName():
                            null;
                componentClass = componentClass.getParentMetadata();
            }
        }

        return result;
    }

    /**
     * Checks if is child positioning is free or constrained.
     *
     * @return true, if is free child positioning
     */
    public boolean isFreeChildPositioning() {
        return freeChildPositioning;
    }

    /**
     * Gets the inherited parent component metadata.
     *
     * @return the parent metadata
     */
    public ComponentClassMetadata getParentMetadata() {
        return parentMetadata;
    }
    
    /**
     * Lookup property by name.
     *
     * @param propertyName the property name
     * @return the property metadata
     */
    public PropertyMetadata lookupProperty(PropertyName propertyName) {
        PropertyMetadata result = null;
        
        assert propertyName != null;
        
        final Iterator<PropertyMetadata> it = properties.iterator();
        while ((result == null) && it.hasNext()) {
            final PropertyMetadata pm = it.next();
            if (pm.getName().equals(propertyName)) {
                result = pm;
            }
        }
        
        return result;
    }

    /*
     * Object
     */
    
    @Override
    public int hashCode() {
        return super.hashCode(); // Only to please FindBugs
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); // Only to please FindBugs
    }
    
    
    /*
     * Private
     */
    
    /**
     * Gets the sub component property for the specified component class.
     *
     * @param componentClass the component class
     * @return the sub component property or null if none or more than one
     */
    //TODO this method is not dynamic and not extensible, need to do something about it
    @Deprecated
    private static PropertyName getSubComponentProperty(Class<?> componentClass) {
        final PropertyName result;
        
        assert componentClass != javafx.scene.layout.BorderPane.class
                && componentClass != javafx.scene.control.DialogPane.class;
        
        /*
         * Component Class -> Sub Component Property
         * =========================================
         * 
         * Accordion                    panes
         * ButtonBar                    buttons
         * ContextMenu                  items
         * Menu                         items
         * MenuBar                      menus
         * MenuButton                   items
         * Path                         elements
         * SplitPane                    items
         * SplitMenuButton              items
         * TableColumn                  columns
         * TableView                    columns
         * TabPane                      tabs
         * ToolBar                      items
         * TreeTableColumn              columns
         * TreeTableView                columns
         * 
         * Group                        children
         * Panes                        children
         *
         * ------------ Gluon ------------------
         *
         * BottomNavigation             actionItems
         * CardPane                     items
         * DropdownButton               items
         * ExpansionPanelContainer      items
         * ToggleButtonGroup            toggles
         * CollapsedPanel               titleNodes
         * SettingsPane                 options
         *
         * ------------------------------------
         *
         * Other            null
         */
      
        if (componentClass == javafx.scene.control.Accordion.class) {
            result = panesName;
        } else if (componentClass == javafx.scene.control.ButtonBar.class) {
            result = buttonsName;
        } else if (componentClass == javafx.scene.control.ContextMenu.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.control.Menu.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.control.MenuBar.class) {
            result = menusName;
        } else if (componentClass == javafx.scene.control.MenuButton.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.shape.Path.class) {
            result = elementsName;
        } else if (componentClass == javafx.scene.control.SplitMenuButton.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.control.SplitPane.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.control.TableColumn.class) {
            result = columnsName;
        } else if (componentClass == javafx.scene.control.TableView.class) {
            result = columnsName;
        } else if (componentClass == javafx.scene.control.TabPane.class) {
            result = tabsName;
        } else if (componentClass == javafx.scene.control.ToolBar.class) {
            result = itemsName;
        } else if (componentClass == javafx.scene.control.TreeTableColumn.class) {
            result = columnsName;
        } else if (componentClass == javafx.scene.control.TreeTableView.class) {
            result = columnsName;
        } else if (componentClass == javafx.scene.Group.class) {
            result = childrenName;
        } else if (componentClass == javafx.scene.layout.Pane.class) {
            result = childrenName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.BottomNavigation.class) {
            result = actionItemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.CardPane.class) {
            result = itemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.DropdownButton.class) {
            result = itemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.ExpansionPanelContainer.class) {
            result = itemsName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.ToggleButtonGroup.class) {
            result = togglesName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.ExpansionPanel.CollapsedPanel.class) {
            result = titleNodesName;
        } else if (componentClass == com.gluonhq.charm.glisten.control.SettingsPane.class) {
            result = optionsName;
        } else {
            result = null;
        }
        
        return result;
    }
        
    private static final PropertyName buttonsName = new PropertyName("buttons");
    private static final PropertyName columnsName = new PropertyName("columns");
    private static final PropertyName elementsName = new PropertyName("elements");
    private static final PropertyName itemsName = new PropertyName("items");
    private static final PropertyName menusName = new PropertyName("menus");
    private static final PropertyName panesName = new PropertyName("panes");
    private static final PropertyName tabsName = new PropertyName("tabs");
    private static final PropertyName childrenName = new PropertyName("children");
    // Gluon
    private static final PropertyName actionItemsName = new PropertyName("actionItems");
    private static final PropertyName togglesName = new PropertyName("toggles");
    private static final PropertyName titleNodesName = new PropertyName("titleNodes");
    private static final PropertyName optionsName = new PropertyName("options");

}
