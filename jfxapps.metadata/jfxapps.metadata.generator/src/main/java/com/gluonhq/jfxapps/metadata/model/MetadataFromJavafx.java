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
package com.gluonhq.jfxapps.metadata.model;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.model.Property.Type;

public class MetadataFromJavafx {

    private static Logger logger = LoggerFactory.getLogger(MetadataFromJavafx.class);

    public static Map<Component, Set<Property>> load(Map<Class<?>, BeanMetaData<?>> classes,
            Map<Class<?>, Component> descriptorComponents) throws Exception {
        return new MetadataFromJavafx().internalLoad(classes, descriptorComponents);
    }

    private TreeMap<Component, Set<Property>> components = new TreeMap<>();

    private MetadataFromJavafx() {
    }

    private Map<Component, Set<Property>> internalLoad(Map<Class<?>, BeanMetaData<?>> classes,
            Map<Class<?>, Component> descriptorComponents) throws Exception {

        // load the store with provided components
        Store store = new Store(descriptorComponents);

        for (Entry<Class<?>, BeanMetaData<?>> classesEntry : classes.entrySet()) {

            BeanMetaData<?> componentMetadata = classesEntry.getValue();

            Component component = new Component(componentMetadata);

            addComponent(component);
            store.addComponent(componentMetadata.getType(), component);
        }

        for (Entry<Class<?>, BeanMetaData<?>> classesEntry : classes.entrySet()) {
            Class<?> componentClass = classesEntry.getKey();
            Component component = store.getComponent(componentClass);
            BeanMetaData<?> componentMetadata = component.getRaw();

            Object instance = componentMetadata.getDefaultInstance();

            List<PropertyMetaData> properties = componentMetadata.getProperties().stream().filter(p -> p.isLocal())
                    .collect(Collectors.toList());

            findAndSetParentComponent(component, store);

            // look for changes between the top instance and the first parent metadata
            if (component.getParent() != null) {

                BeanMetaData<?> parentMetadata = component.getParent().getRaw();
                Object parentInstance = parentMetadata.getDefaultInstance();

                for (PropertyMetaData parentProperty : parentMetadata.getProperties()) {
                    try {
                        if (parentProperty.isReadWrite() && !hasSameValue(parentProperty, instance, parentInstance)) {
                            properties.add(PropertyMetaData.relocalize(parentProperty, componentMetadata));
                        }
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }

            for (PropertyMetaData propertyMetadata : properties) {

                boolean isComponentProperty = classes.containsKey(propertyMetadata.getContentType())
                        || descriptorComponents.containsKey(propertyMetadata.getContentType());

                if (propertyMetadata.isComponent().isPresent()) {
                    isComponentProperty = propertyMetadata.isComponent().get();
                }

                logger.debug("Property {} has content type of : {} and is component {} or component isPResent {}",
                        propertyMetadata.getName(), propertyMetadata.getContentType(), isComponentProperty,
                        propertyMetadata.isComponent().isPresent());

                if (!propertyMetadata.isStatic() && !isComponentProperty) {

                    if (propertyMetadata.getMetadataClass() == null) {
                        continue;
                    }

                    Property pMeta = new Property(propertyMetadata,
                            com.gluonhq.jfxapps.metadata.model.Property.Type.VALUE);

                    addComponentProperty(component, pMeta);
                } else if (!propertyMetadata.isStatic() && isComponentProperty) {

                    if (propertyMetadata.getMetadataClass() == null) {
                        continue;
                    }

                    Property pMeta = new Property(propertyMetadata,
                            com.gluonhq.jfxapps.metadata.model.Property.Type.COMPONENT);

                    addComponentProperty(component, pMeta);

                } else if (propertyMetadata.isStatic()) {
                    // metaTmpl.addProperty(new Property(propName, name, residenceClass));

                    store.addStaticProperty(propertyMetadata.getApplicability(), propertyMetadata);
                } else {

                    System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println("XXXXXXXXXXXXXXXXXX XNOTAVALUEPROPERTYX XXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println(propertyMetadata != null
                            ? propertyMetadata.getName() + " " + propertyMetadata.getApplicability().getName()
                            : "NULL");
                    System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                    System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");

                }
            }
        }

        store.flushToMetadata();

        return components;
    }

    private static void findAndSetParentComponent(Component component, Store store) {
        Component parentComponent = null;
        Class<?> current = component.getRaw().getType().getSuperclass();
        while (current != null && parentComponent == null) {
            parentComponent = store.getComponent(current);
            if (parentComponent != null) {
                component.setParent(parentComponent);
                return;
            }
            current = current.getSuperclass();
        }
    }

    private static boolean hasSameValue(PropertyMetaData property, Object instance, Object otherInstance)
            throws Exception {
        Method getter = property.getGetterMethod();
        Object topDefaultValue = getter.invoke(instance);
        Object parentDefaultValue = getter.invoke(otherInstance);

        if ((topDefaultValue == null && parentDefaultValue != null)
                || (topDefaultValue != null && parentDefaultValue == null) || (topDefaultValue != null
                        && parentDefaultValue != null && !topDefaultValue.equals(parentDefaultValue))) {
            return false;
        }
        return true;
    }

    private class Store {

        private Map<Class<?>, List<PropertyMetaData>> classToStaticProperties = new HashMap<>();
        private Map<Class<?>, Component> classToComponents = new HashMap<>();

        public Store(Map<Class<?>, Component> descriptorComponents) {
            super();
            classToComponents.putAll(descriptorComponents);
        }

        void addComponent(Class<?> cls, Component cmp) {
            classToComponents.put(cls, cmp);
        }

        void addStaticProperty(Class<?> cls, PropertyMetaData prop) {
            List<PropertyMetaData> list = classToStaticProperties.computeIfAbsent(cls, k -> new ArrayList<>());
            list.add(prop);
        }

        Component getComponent(Class<?> cls) {
            return classToComponents.get(cls);
        }

        void flushToMetadata() {

            for (Entry<Class<?>, Component> entry : classToComponents.entrySet()) {
                Class<?> componentClass = entry.getKey();
                Component component = entry.getValue();

                List<PropertyMetaData> list = classToStaticProperties.computeIfAbsent(componentClass,
                        k -> new ArrayList<>());

                // only static properties are processed
                for (PropertyMetaData propertyMetadata : list) {

                    try {

                        boolean isComponentProperty = classToComponents.containsKey(propertyMetadata.getContentType());

                        if (propertyMetadata.isComponent().isPresent()) {
                            isComponentProperty = propertyMetadata.isComponent().get();
                        }

                        PropertyMetaData relocalized = PropertyMetaData.relocalizeStatic(propertyMetadata,
                                component.getRaw());

                        if (!isComponentProperty) {

                            if (propertyMetadata.getMetadataClass() == null) {
                                continue;
                            }

                            Property pMeta = new Property(relocalized, Type.VALUE);

                            addComponentProperty(component, pMeta);
                        } else if (isComponentProperty) {

                            if (propertyMetadata.getMetadataClass() == null) {
                                continue;
                            }

                            Property pMeta = new Property(relocalized, Type.COMPONENT);

                            addComponentProperty(component, pMeta);

                        } else {

                            System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                            System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                            System.out.println("XXXXXXXXXXXXXXXXXX XNOTAVALUEPROPERTYX XXXXXXXXXXXXXXXXXXXXXXX");
                            System.out.println(propertyMetadata != null
                                    ? propertyMetadata.getName() + " " + propertyMetadata.getApplicability().getName()
                                    : "NULL");
                            System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                            System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");
                            System.out.println("XXXXXXXXXXXXXXXXXX NOTAVALUEPROPERTY XXXXXXXXXXXXXXXXXXXXXXX");

                        }

                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }

        }
    }

    private void addComponent(Component component) {
        components.computeIfAbsent(component, c -> new TreeSet<>());
    }

    private void addComponentProperty(Component component, Property property) {
        components.computeIfAbsent(component, c -> new TreeSet<>()).add(property);
    }

}
