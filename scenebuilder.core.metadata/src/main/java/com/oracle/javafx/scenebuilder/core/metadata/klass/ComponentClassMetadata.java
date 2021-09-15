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
package com.oracle.javafx.scenebuilder.core.metadata.klass;

import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.oracle.javafx.scenebuilder.core.fxom.util.PropertyName;
import com.oracle.javafx.scenebuilder.core.metadata.property.ComponentPropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyGroupMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.PropertyMetadata;
import com.oracle.javafx.scenebuilder.core.metadata.property.ValuePropertyMetadata;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;
import javafx.scene.Node;
import lombok.Getter;

/**
 * This class describes an fxml component class 
 * 
 */
public class ComponentClassMetadata<T> extends ClassMetadata<T> {
    
    public static final Comparator<ComponentPropertyMetadata> COMPARATOR = Comparator.comparing( ComponentPropertyMetadata::getOrder )
            .thenComparing(Comparator.comparing((cpm) -> cpm.getName().getName()));
    
    /** The component properties. */
    private final ObservableSet<PropertyMetadata> properties = FXCollections.observableSet(new HashSet<>());
    
    /** The shadowed properties subset. */
    private final Set<PropertyMetadata> shadowedProperties = new HashSet<>();
    
    /** The component properties values subset. */
    private final Set<ValuePropertyMetadata> values = new HashSet<>();
    
    /** The group properties values subset. */
    private final Set<PropertyGroupMetadata> groups = new HashSet<>();
    
    /** The component properties component subset. */
    private final Set<ComponentPropertyMetadata> subComponents = new HashSet<>();
    
    /** The component properties component subset. */
    private final Map<String, Qualifier> qualifiers = new HashMap<>();
    
    /** The free child positioning flag. default false */
    private final Map<ComponentPropertyMetadata, Boolean> freeChildPositioning = new HashMap<>();
    
    /** The inherited parent metadata. */
    private final ComponentClassMetadata<?> parentMetadata;

    /** true if the component deserves a resizing while used as top element of the layout. default: true */
    private boolean resizeNeededWhenTopElement = true;
    
    /** property used for object description */
    private PropertyName descriptionProperty = null;
    
    /** if set operate a custom mutation on the original string */
    private LabelMutation labelMutation = null;
    
    /** if set operate a custom mutation on the original string */
    private final Map<ComponentPropertyMetadata, ChildLabelMutation> childLabelMutations = new HashMap<>();
    
    /**
     * Instantiates a new component class metadata.
     *
     * @param klass the component's class
     * @param parentMetadata the inherited parent component's metadata
     */
    public ComponentClassMetadata(Class<T> klass, ComponentClassMetadata<?> parentMetadata) {
        super(klass);
        this.parentMetadata = parentMetadata;
        setupSetSync();
    } 

    private void setupSetSync() {
        properties.addListener((Change<? extends PropertyMetadata> e) -> {
            if (e.wasAdded() && e.getElementAdded() != null) {
                if (ValuePropertyMetadata.class.isAssignableFrom(e.getElementAdded().getClass())) {
                    if (e.getElementAdded().isGroup()) {
                        groups.add((PropertyGroupMetadata)e.getElementAdded());
                    } else {
                        values.add((ValuePropertyMetadata)e.getElementAdded());
                    }
                } else if (ComponentPropertyMetadata.class.isAssignableFrom(e.getElementAdded().getClass())) {
                    subComponents.add((ComponentPropertyMetadata)e.getElementAdded());
                } 
            } else if (e.wasRemoved() && e.getElementRemoved() != null) {
                if (ValuePropertyMetadata.class.isAssignableFrom(e.getElementRemoved().getClass())) {
                    if (e.getElementRemoved().isGroup()) {
                        groups.remove(e.getElementRemoved());
                    } else {
                        values.remove(e.getElementRemoved());
                    }
                } else if (ComponentPropertyMetadata.class.isAssignableFrom(e.getElementRemoved().getClass())) {
                    subComponents.remove(e.getElementRemoved());
                } 
            }
            
        });
    }
    
    /**
     * Gets the component's qualifiers (aka default setups).
     *
     * @return the properties
     */
    public Map<String, Qualifier> getQualifiers() {
        return qualifiers;
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
     * Gets the component's shadowed properties (hidden).
     *
     * @return the properties
     */
    public Set<PropertyMetadata> getShadowedProperties() {
        return shadowedProperties;
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
        return Collections.unmodifiableSet(
                subComponents.stream().filter(c -> !shadowedProperties.contains(c)).collect(Collectors.toSet()));
    }

    /**
     * Gets the component's properties sub components subset for all the inheritance chain.
     * which aren't shadowed
     * @return all the components subset properties
     */
    public Set<ComponentPropertyMetadata> getAllSubComponentProperties() {
        TreeSet<ComponentPropertyMetadata> result = new TreeSet<>(COMPARATOR);
        ComponentClassMetadata<?> current = this;
        
        while (current != null) {
            current.getSubComponentProperties().stream()
                .filter(p -> !shadowedProperties.contains(p))
                .forEach(p -> result.add(p));
            current = current.getParentMetadata();
        }
        
        return Collections.unmodifiableSet(result);
    }
    
    /**
     * Gets the first main component's property for all the inheritance chain.
     * which isn't shadowed
     * @return all the components subset properties
     */
    public ComponentPropertyMetadata getMainComponentProperty() {
        ComponentClassMetadata<?> current = this;
        
        while (current != null) {
            Optional<ComponentPropertyMetadata> optional = current.getSubComponentProperties().stream()
                .filter(p -> p.isMain())
                .filter(p -> !shadowedProperties.contains(p))
                .findFirst();
            if (optional.isPresent()) {
                return optional.get();
            }
            current = current.getParentMetadata();
        }
        return null;
    }
    

    /**
     * Checks if is child positioning is free or constrained.
     *
     * @return true, if is free child positioning
     */
    public boolean isFreeChildPositioning(ComponentPropertyMetadata componentProperty) {
        ComponentClassMetadata<?> current = this;
        
        while (current != null && !current.freeChildPositioning.containsKey(componentProperty)) {
            current = current.getParentMetadata();
        }
        
        if (current == null) {
            return false;
        }
        
        Boolean free = current.freeChildPositioning.get(componentProperty);
        return free == null ? false : free;
    }
    
    
    
    protected void setDescriptionProperty(ValuePropertyMetadata promptTextPropertyMetadata) {
        this.descriptionProperty = promptTextPropertyMetadata.getName();
    }

    /**
     * Get the property name used for the description
     * A lookup is done on parents metadata
     *
     * @return the property name or null if none found
     */
    public PropertyName getDescriptionProperty() {
        ComponentClassMetadata<?> current = this;
        
        while (current != null && current.descriptionProperty == null ) {
            current = current.getParentMetadata();
        }
        
        if (current == null) {
            return null;
        }
        
        return current.descriptionProperty;
    }

    /**
     * Gets the inherited parent component metadata.
     *
     * @return the parent metadata
     */
    public ComponentClassMetadata<?> getParentMetadata() {
        return parentMetadata;
    }
    
    /**
     * Lookup property by name.
     *
     * @param propertyName the property name
     * @return the property metadata
     */
    public PropertyMetadata lookupProperty(PropertyName propertyName) {
        
        assert propertyName != null;
        
        final Iterator<PropertyMetadata> it = properties.iterator();
        while (it.hasNext()) {
            final PropertyMetadata pm = it.next();
            if (pm.getName().equals(propertyName)) {
                return pm;
            }
        }
        
        for (PropertyGroupMetadata g:groups) {
            for (int i=0; i<g.getProperties().length; i++) {
                PropertyMetadata pm = g.getProperties()[i];
                if (pm.getName().equals(propertyName)) {
                    return pm;
                }
            }
        }
        return null;
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
    
       
    /**
     * Find the applicable qualifiers in the available qualifiers.
     * A {@link ComponentClassMetadata.Qualifier} is applicable if the {@link ApplicabilityCheck} provided trough
     * {@link Qualifier#Qualifier(URL, String, String, URL, URL, String, ApplicabilityCheck)} return true
     * If none were provided during the {@link Qualifier} instantiation then the {@link Qualifier} is always applicable
     * 
     * @param sceneGraphObject the scene graph object
     * @return the applicable qualifiers sets
     */
    public Set<Qualifier> applicableQualifiers(Object sceneGraphObject) {
        if (!getKlass().isAssignableFrom(sceneGraphObject.getClass()) || getQualifiers().size() == 0) {
            return Collections.unmodifiableSet(new HashSet<>());
        }
        return Collections.unmodifiableSet(getQualifiers().values().stream().filter(q -> q.isApplicable(sceneGraphObject)).collect(Collectors.toSet()));
    }
    
    public static class Qualifier {
        
        public static final Qualifier UNKNOWN = new Qualifier(null, null, null, null, null, null);
        
        public static final String HIDDEN = null;
        public static final String DEFAULT = "";
        public static final String EMPTY = "empty";
        
        @Getter private final URL fxmlUrl;
        @Getter private final String label;
        @Getter private final String description;
        @Getter private final URL iconUrl;
        @Getter private final URL iconX2Url;
        @Getter private final String category;
        @SuppressWarnings("rawtypes")
        @Getter private final ApplicabilityCheck applicabilityCheck;
        
        public Qualifier(URL fxmlUrl, String label, String description, URL iconUrl, URL iconX2Url, String category) {
            this(fxmlUrl, label, description, iconUrl, iconX2Url, category, (o) -> true);
        }
        
        public Qualifier(URL fxmlUrl, String label, String description, URL iconUrl, URL iconX2Url, String category, ApplicabilityCheck<?> applicabilityCheck) {
            super();
            this.fxmlUrl = fxmlUrl;
            this.label = label;
            this.description = description;
            this.iconUrl = iconUrl != null ? iconUrl : getClass().getResource("MissingIcon.png");
            this.iconX2Url = iconX2Url != null ? iconX2Url : getClass().getResource("MissingIcon@2x.png");
            this.category = category;// != null ? category : DefaultSectionNames.TAG_USER_DEFINED;
            this.applicabilityCheck = applicabilityCheck;
        }
        
        @SuppressWarnings("unchecked")
        public boolean isApplicable(Object object) {
            return applicabilityCheck.isApplicable(object);
        }
    }
    
    @FunctionalInterface
    public interface ApplicabilityCheck<T> {
        boolean isApplicable(T object);
    }
    
    @FunctionalInterface
    public interface LabelMutation {
        String mutate(String originalLabel, Object object);
    }
    
    @FunctionalInterface
    public interface ChildLabelMutation {
        String mutate(String originalLabel, Object object, Node child);
    }

    public boolean isResizeNeededWhenTopElement() {
        return resizeNeededWhenTopElement;
    }

    protected ComponentClassMetadata<T> setResizeNeededWhenTopElement(boolean resizeNeededWhenTopElement) {
        this.resizeNeededWhenTopElement = resizeNeededWhenTopElement;
        return this;
    }

    public LabelMutation getLabelMutation() {
        return labelMutation;
    }

    protected ComponentClassMetadata<T> setLabelMutation(LabelMutation labelMutation) {
        this.labelMutation = labelMutation;
        return this;
    }

    public ChildLabelMutation getChildLabelMutations(ComponentPropertyMetadata cmp) {
        return childLabelMutations.get(cmp);
    }
    
    protected ComponentClassMetadata<T> setChildLabelMutation(ComponentPropertyMetadata cmp, ChildLabelMutation labelMutation) {
        assert getAllSubComponentProperties().contains(cmp);
        childLabelMutations.put(cmp, labelMutation);
        return this;
    }

    protected void setFreeChildPositioning(ComponentPropertyMetadata componentProperty, boolean freeChildPositioning) {
        this.freeChildPositioning.put(componentProperty, freeChildPositioning);
    }
    
}
