package com.oracle.javafx.scenebuilder.metadata.model;

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

import com.oracle.javafx.scenebuilder.metadata.bean.BeanMetaData;
import com.oracle.javafx.scenebuilder.metadata.bean.PropertyMetaData;
import com.oracle.javafx.scenebuilder.metadata.model.Property.Type;

public class MetadataFromJavafx {

    public static Map<Component, Set<Property>> load(Map<Class<?>, BeanMetaData<?>> classes, Map<Class<?>, Component> descriptorComponents) throws Exception {
        return new MetadataFromJavafx().internalLoad(classes, descriptorComponents);
    }

    private TreeMap<Component, Set<Property>> components = new TreeMap<>();

    private MetadataFromJavafx() {}

    private Map<Component, Set<Property>> internalLoad(Map<Class<?>, BeanMetaData<?>> classes, Map<Class<?>, Component> descriptorComponents) throws Exception {

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

                if (!propertyMetadata.isStatic() && !isComponentProperty) {

                    if (propertyMetadata.getMetadataClass() == null) {
                        continue;
                    }

                    Property pMeta = new Property(propertyMetadata,
                            com.oracle.javafx.scenebuilder.metadata.model.Property.Type.VALUE);

                    addComponentProperty(component, pMeta);
                } else if (!propertyMetadata.isStatic() && isComponentProperty) {

                    if (propertyMetadata.getMetadataClass() == null) {
                        continue;
                    }

                    Property pMeta = new Property(propertyMetadata,
                            com.oracle.javafx.scenebuilder.metadata.model.Property.Type.COMPONENT);

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

        private Map<Class, List<PropertyMetaData>> classToStaticProperties = new HashMap<>();
        private Map<Class, Component> classToComponents = new HashMap<>();


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

            for (Entry<Class, Component> entry : classToComponents.entrySet()) {
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
