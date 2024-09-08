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
package com.gluonhq.jfxapps.core.metadata.klass;

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

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener.Change;

/**
 * This class describes an fxml component class
 * @param <T> the component class
 * @param <CC> the component metadata customization class
 * @param <CPC> the component property metadata customization class
 * @param <CPM> the component property metadata class
 * @param <VPC> the component value metadata customization class
 * @param <VPM> the component value metadata class
 */
public class ComponentClassMetadata<T, CC,
    CPC,
    CPM extends ComponentPropertyMetadata,
    VPC,
    VPM extends ValuePropertyMetadata,
    P extends ComponentClassMetadata> extends ClassMetadata<T> {

    public static final Comparator<ComponentPropertyMetadata> COMPARATOR = Comparator.comparing(cpm -> cpm.getName().getName());

    /** The component properties. */
    private final ObservableSet<PropertyMetadata<?>> properties = FXCollections.observableSet(new HashSet<>());

    /** The shadowed properties subset. */
    private final Set<PropertyName> shadowedProperties = new HashSet<>();

    /** The component properties values subset. */
    private final Map<PropertyName, VPM> values = new HashMap<>();

    /** The group properties values subset. */
    private final Set<PropertyGroupMetadata<VPC>> groups = new HashSet<>();

    /** The component properties component subset. */
    private final Map<PropertyName, CPM> subComponents = new HashMap<>();

    /** The inherited parent metadata. */
    private final P parentMetadata;

    private final CC customization;
    /**
     * Instantiates a new component class metadata.
     *
     * @param klass the component's class
     * @param parentMetadata the inherited parent component's metadata
     */
    public ComponentClassMetadata(Class<T> klass, P parentMetadata, CC customization) {
        super(klass);
        this.parentMetadata = parentMetadata;
        this.customization = customization;
        setupSetSync();
    }

    public CC getCustomization() {
        return customization;
    }

    // TODO is it realy required? properties are set explicitly using codegen, can't it be done without ?
    private void setupSetSync() {
        properties.addListener((Change<? extends PropertyMetadata<?>> e) -> {
            if (e.wasAdded() && e.getElementAdded() != null) {
                if (ValuePropertyMetadata.class.isAssignableFrom(e.getElementAdded().getClass())) {
                    if (e.getElementAdded().isGroup()) {
                        groups.add((PropertyGroupMetadata<VPC>)e.getElementAdded());
                    } else {
                        var added = (VPM)e.getElementAdded();
                        values.put(added.getName(), added);
                    }
                } else if (ComponentPropertyMetadata.class.isAssignableFrom(e.getElementAdded().getClass())) {
                    var added = (CPM)e.getElementAdded();
                    subComponents.put(added.getName(), added);
                }
            } else if (e.wasRemoved() && e.getElementRemoved() != null) {
                if (ValuePropertyMetadata.class.isAssignableFrom(e.getElementRemoved().getClass())) {
                    if (e.getElementRemoved().isGroup()) {
                        groups.remove(e.getElementRemoved());
                    } else {
                        values.remove(e.getElementRemoved().getName());
                    }
                } else if (ComponentPropertyMetadata.class.isAssignableFrom(e.getElementRemoved().getClass())) {
                    subComponents.remove(e.getElementRemoved().getName());
                }
            }

        });
    }

    /**
     * Gets the component's properties.
     *
     * @return the properties
     */
    public Set<PropertyMetadata<?>> getProperties() {
        return properties;
    }

    /**
     * Gets the component's shadowed properties (hidden).
     *
     * @return the properties
     */
    public Set<PropertyName> getShadowedProperties() {
        return shadowedProperties;
    }

    /**
     * Gets the component's properties values subset.
     *
     * @return the values subset properties
     */
    public Set<ValuePropertyMetadata<VPC>> getValueProperties() {
        return Collections.unmodifiableSet(values.values().stream()
                .map(i -> i)
                .filter(c -> !shadowedProperties.contains(c.getName()))
                .collect(Collectors.toSet()));
    }

    public VPM getValueProperty(PropertyName propertyName) {
        if (!shadowedProperties.contains(propertyName)) {
            return values.get(propertyName);
        }
        return null;
    }

    /**
     * Gets the component's properties sub components subset.
     *
     * @return the components subset properties
     */
    public Set<CPM> getSubComponentProperties() {
        return Collections.unmodifiableSet(subComponents.values().stream()
                .map(i -> i)
                .filter(c -> !shadowedProperties.contains(c.getName()))
                .collect(Collectors.toSet()));
    }

    public CPM getSubComponentProperty(PropertyName propertyName) {
        if (!shadowedProperties.contains(propertyName)) {
            return subComponents.get(propertyName);
        }
        return null;
    }

    public Set<CPM> getAllSubComponentProperties() {
        return getAllSubComponentProperties(COMPARATOR);
    }

    /**
     * Gets the component's properties sub components subset for all the inheritance chain.
     * which aren't shadowed
     * @return all the components subset properties
     */
    public Set<CPM> getAllSubComponentProperties(Comparator<? extends ComponentPropertyMetadata> comparator) {

        TreeSet<CPM> result = new TreeSet<CPM>((Comparator<CPM>)comparator);

        ComponentClassMetadata<?, ?, ?, CPM, ?, ?, ?> current = this;

        while (current != null) {
            current.getSubComponentProperties().stream()
                .filter(p -> !shadowedProperties.contains(p.getName()))
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
    public CPM getMainComponentProperty() {
        ComponentClassMetadata<?, ?, ?, CPM, ?, ?, ?> current = this;

        while (current != null) {
            Optional<CPM> optional = current.getSubComponentProperties().stream()
                .filter(p -> p.isMain())
                .filter(p -> !shadowedProperties.contains(p.getName()))
                .findFirst();
            if (optional.isPresent()) {
                return optional.get();
            }
            current = current.getParentMetadata();
        }
        return null;
    }

    /**
     * Gets the inherited parent component metadata.
     *
     * @return the parent metadata
     */
    public P getParentMetadata() {
        return parentMetadata;
    }

    /**
     * Lookup property by name.
     *
     * @param propertyName the property name
     * @return the property metadata
     */
    public PropertyMetadata<?> lookupProperty(PropertyName propertyName) {

        assert propertyName != null;

        final Iterator<PropertyMetadata<?>> it = properties.iterator();
        while (it.hasNext()) {
            final PropertyMetadata<?> pm = it.next();
            if (pm.getName().equals(propertyName)) {
                return pm;
            }
        }

        for (PropertyGroupMetadata<VPC> g:groups) {
            for (int i=0; i<g.getProperties().length; i++) {
                PropertyMetadata<?> pm = g.getProperties()[i];
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

}
