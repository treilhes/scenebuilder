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
package com.gluonhq.jfxapps.core.metadata;

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

import com.gluonhq.jfxapps.core.fxom.FXOMElement;
import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ComponentPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyGroupMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.ValuePropertyMetadata;

/**
 *
 */

public abstract class AbstractMetadata<CC, CPC, VPC> {

    private final Map<Class<?>, ComponentClassMetadata<?, CC, CPC, VPC>> componentClassMap = new HashMap<>();
    private final Map<Class<?>, ComponentClassMetadata<?, CC, CPC, VPC>> customComponentClassMap = new WeakHashMap<>();

    private final MetadataIntrospector<CC, CPC, VPC> metadataIntrospector;

    protected AbstractMetadata(List<ComponentClassMetadata<?, CC, CPC, VPC>> componentClassMetadatas,
            MetadataIntrospector<CC, CPC, VPC> metadataIntrospector) {
        this.metadataIntrospector = metadataIntrospector;

        // Populate componentClassMap
        componentClassMetadatas.forEach(c -> componentClassMap.put(c.getKlass(), c));

    }


    public ComponentClassMetadata<?, CC, CPC, VPC> queryComponentMetadata(Class<?> componentClass) {
        final ComponentClassMetadata<?, CC, CPC, VPC> result;


        final ComponentClassMetadata<?, CC, CPC, VPC> componentMetadata = componentClassMap.get(componentClass);
        if (componentMetadata != null) {
            // componentClass is a certified component
            result = componentMetadata;
        } else {
            // componentClass is a custom component
            final ComponentClassMetadata<?, CC, CPC, VPC> customMetadata = customComponentClassMap.get(componentClass);
            if (customMetadata != null) {
                // componentClass has already been introspected
                result = customMetadata;
            } else {
                // componentClass must be introspected
                // Let's find the first certified ancestor
                Class<?> ancestorClass = componentClass.getSuperclass();
                ComponentClassMetadata<?, CC, CPC, VPC> ancestorMetadata = null;
                while ((ancestorClass != null) && (ancestorMetadata == null)) {
                    ancestorMetadata = componentClassMap.get(ancestorClass);
                    ancestorClass = ancestorClass.getSuperclass();
                }

                result = metadataIntrospector.introspect(componentClass, ancestorMetadata, this);

                customComponentClassMap.put(componentClass, result);
            }
        }

        return result;
    }


    public Set<PropertyMetadata<?>> queryProperties(Class<?> componentClass) {
        final Map<PropertyName, PropertyMetadata<?>> result = new HashMap<>();
        ComponentClassMetadata<?, CC, CPC, VPC> classMetadata = queryComponentMetadata(componentClass);

        Set<PropertyName> shadowed = new HashSet<>();
        while (classMetadata != null) {
            for (PropertyMetadata<?> pm : classMetadata.getProperties()) {
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


    public Set<PropertyMetadata<?>> queryProperties(Collection<Class<?>> componentClasses) {
        final Set<PropertyMetadata<?>> result = new HashSet<>();

        int count = 0;
        for (Class<?> componentClass : componentClasses) {
            final Set<PropertyMetadata<?>> propertyMetadata = queryProperties(componentClass);
            if (count == 0) {
                result.addAll(propertyMetadata);
            } else {
                result.retainAll(propertyMetadata);
            }
            count++;
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    public Set<ComponentPropertyMetadata<CPC>> queryComponentProperties(Class<?> componentClass) {
        final Set<ComponentPropertyMetadata<CPC>> result = new HashSet<>();

        for (PropertyMetadata<?> propertyMetadata : queryProperties(Arrays.asList(componentClass))) {
            if (propertyMetadata instanceof ComponentPropertyMetadata cpc) {
                result.add(cpc);
            }
        }
        return result;
    }


    public ComponentPropertyMetadata<CPC> queryComponentProperty(Class<?> componentClass, PropertyName name) {
        ComponentClassMetadata<?, CC, CPC, VPC> classMetadata = queryComponentMetadata(componentClass);
        Optional<ComponentPropertyMetadata<CPC>> result = classMetadata.getAllSubComponentProperties().stream()
            .filter(scp -> scp.getName().equals(name))
            .findFirst();
        return result.isEmpty() ? null : result.get();
    }


    @SuppressWarnings("unchecked")
    public Set<ValuePropertyMetadata<VPC>> queryValueProperties(Set<Class<?>> componentClasses) {
        final Set<ValuePropertyMetadata<VPC>> result = new HashSet<>();
        for (PropertyMetadata<?> propertyMetadata : queryProperties(componentClasses)) {
            if (propertyMetadata instanceof ValuePropertyMetadata vpc) {
                result.add(vpc);
            }
        }
        return result;
    }


    public PropertyMetadata<?> queryProperty(Class<?> componentClass, PropertyName targetName) {
        final Set<PropertyMetadata<?>> propertyMetadataSet = queryProperties(componentClass);
        final Iterator<PropertyMetadata<?>> iterator = propertyMetadataSet.iterator();
        PropertyMetadata<?> result = null;

        while ((result == null) && iterator.hasNext()) {
            final PropertyMetadata<?> propertyMetadata = iterator.next();
            if (propertyMetadata.getName().equals(targetName)) {
                return propertyMetadata;
            }

            if (propertyMetadata.isGroup()) {
                PropertyGroupMetadata<?> pgm = (PropertyGroupMetadata<?>)propertyMetadata;
                for (int i=0; i < pgm.getProperties().length; i++) {
                    if (pgm.getProperties()[i].getName().equals(targetName)) {
                        return pgm.getProperties()[i];
                    }
                }
            }
        }
        return null;
    }


    @SuppressWarnings("unchecked")
    public ValuePropertyMetadata<VPC> queryValueProperty(FXOMElement fxomInstance, PropertyName targetName) {
        final ValuePropertyMetadata<VPC> result;
        assert fxomInstance != null;
        assert targetName != null;

        if (fxomInstance.getMetadataClass() == null) {
            // FXOM object is unresolved
            result = null;
        } else {
            final Class<?> componentClass = fxomInstance.getMetadataClass();

            final PropertyMetadata<?> m = queryProperty(componentClass, targetName);
            if (m instanceof ValuePropertyMetadata vpc) {
                result = vpc;
            } else {
                result = null;
            }
        }

        return result;
    }

    public Collection<ComponentClassMetadata<?, CC, CPC, VPC>> getComponentClasses() {
        return componentClassMap.values();
    }

    public ComponentClassMetadata<?, CC, CPC, VPC> queryComponentMetadata(Class<?> clazz, PropertyName propName) {

        ComponentClassMetadata<?, CC, CPC, VPC> classMeta = queryComponentMetadata(clazz);
        while (classMeta != null) {
            for (PropertyMetadata<?> propMeta : classMeta.getProperties()) {
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


