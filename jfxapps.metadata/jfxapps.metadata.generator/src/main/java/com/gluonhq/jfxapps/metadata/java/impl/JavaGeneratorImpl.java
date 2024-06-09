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
package com.gluonhq.jfxapps.metadata.java.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.MetadataProducer;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData.Type;
import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.java.api.ClassCustomization;
import com.gluonhq.jfxapps.metadata.java.api.JavaGenerationContext;
import com.gluonhq.jfxapps.metadata.java.model.Component;
import com.gluonhq.jfxapps.metadata.java.model.ComponentProperty;
import com.gluonhq.jfxapps.metadata.java.model.Context;
import com.gluonhq.jfxapps.metadata.java.model.ValueProperty;
import com.gluonhq.jfxapps.metadata.java.template.scenebuilderx.TemplateGeneration;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerator;
import com.gluonhq.jfxapps.metadata.util.ReflectionUtils;

import javafx.geometry.BoundingBox;
import javafx.geometry.Point3D;
import javafx.scene.text.Font;

public class JavaGeneratorImpl implements ClassCustomization {

    private static final Logger logger = LoggerFactory.getLogger(JavaGeneratorImpl.class);

    private final JavaPropsMapper mapper = new JavaPropsMapper();
    private final JavaGenerationContext javaGenerationContext;
    private final JavaType componentType;
    private final TemplateGeneration generation = new TemplateGeneration();
    private final MetadataProducer converter;

    public JavaGeneratorImpl(PropertyGenerationContext propertyContext, JavaGenerationContext javaGenerationContext) {

        this.javaGenerationContext = javaGenerationContext;
        this.converter = new MetadataProducer(propertyContext);
        TypeFactory typeFactory = TypeFactory.defaultInstance();

        // Constructing the parameterized types
        JavaType componentCustoType = typeFactory
                .constructType(propertyContext.getComponentCustomizationClass().orElse(Void.class));
        JavaType componentPropertyCustoType = typeFactory
                .constructType(propertyContext.getComponentPropertyCustomizationClass().orElse(Void.class));
        JavaType valuePropertyCustoType = typeFactory
                .constructType(propertyContext.getValuePropertyCustomizationClass().orElse(Void.class));

        // Constructing the Component<ComponentCusto, ComponentPropertyCusto,
        // ValuePropertyCusto> type
        this.componentType = typeFactory.constructParametricType(Component.class, componentCustoType,
                componentPropertyCustoType, valuePropertyCustoType);
    }

    /**
     * {@inheritDoc}
     */
    // @Override
    public void generateJavaFiles(Set<Class<?>> classes, Set<Descriptor> descriptors) {

        if (descriptors == null) {
            descriptors = Set.of();
        }

        Map<Class<?>, Component<?, ?, ?>> localComponents = new HashMap<>();
        Map<Class<?>, Component<?, ?, ?>> availableComponents = new HashMap<>();

        descriptors.stream().map(Descriptor::getClassToMetaClass).flatMap(m -> m.entrySet().stream())
                .forEach(e -> availableComponents.put(e.getKey(), toParent(e.getKey(), e.getValue())));

        var beanMap = converter.produce(classes);

        // populate the local components with metadata
        for (BeanMetaData<?> bm : beanMap.values()) {
            try {
                var component = loadComponent(bm);
                localComponents.put(bm.getType(), component);
            } catch (Exception e) {
                logger.error("Failed to load component for class {}", bm.getType(), e);
            }
        }

        // populate the components with parent and dependencies
        localComponents.values().forEach(c -> {
            populateParentComponent(c, localComponents, availableComponents);
            populateComponentDependencies(c, localComponents, availableComponents);
        });

        localComponents.values().forEach(c -> {
            Object instance = c.getMetadata().getDefaultInstance();
            populateUpdatedProperties(c, instance, localComponents, availableComponents);
        });

        Context context = new Context();
        context.setComponentSuperClassName(javaGenerationContext.getTargetComponentSuperClass());
        context.setComponentCustomizationClassName(javaGenerationContext.getTargetComponentCustomizationClass());
        context.setComponentPropertyCustomizationClassName(
                javaGenerationContext.getTargetComponentPropertyCustomizationClass());
        context.setExtensionClassSimpleName(javaGenerationContext.getExtensionName());
        context.setModuleName(javaGenerationContext.getModuleName());
        context.setPropertyNamesClassSimpleName("PropertyNames");
        context.setRequiredModules(javaGenerationContext.getModuleRequires());
        context.setTargetPackage(javaGenerationContext.getTargetPackage());
        context.setUuid(javaGenerationContext.getUuid().toString());
        context.setValuePropertyCustomizationClassName(
                javaGenerationContext.getTargetValuePropertyCustomizationClass());

        try {
            Descriptor descriptor = new Descriptor();
            Set<String> packages = new HashSet<>();

            // generate the java files
            for (Component<?, ?, ?> c : localComponents.values()) {
                generation.generateComponentClass(javaGenerationContext, context, c, this);

                descriptor.put(c.getMetadata().getType(), c.getMetadataClassName());
                packages.add(c.getMetadataClassPackage());
            }
            generation.generateDescriptor(javaGenerationContext, context, descriptor);
            generation.generateExtension(javaGenerationContext, context, localComponents.values());
            generation.generateModuleInfo(javaGenerationContext, context, packages);
            generation.generatePropertyNamesClass(javaGenerationContext, context, localComponents);

        } catch (IOException e1) {
            // TODO add log here and maybe do something else
            throw new RuntimeException(e1);
        }

    }

    public Component<?, ?, ?> loadComponent(BeanMetaData<?> bm)
            throws StreamWriteException, DatabindException, IOException {

        var cls = bm.getType();
        var cmp = load(bm.getType());

        String prefix = javaGenerationContext.getMetadataPrefix() == null ? ""
                : javaGenerationContext.getMetadataPrefix();
        String metadataPackage = javaGenerationContext.getTargetPackage() + "."
                + getLastSegments(javaGenerationContext.getKeepLastPackages(), cls);
        String metadataClassSimpleName = cls.getSimpleName() + "Metadata";
        String metadataClassName = metadataPackage + "." + prefix + metadataClassSimpleName;

        cmp.setMetadata(bm);
        cmp.setMetadataClassName(metadataClassName);
        cmp.setMetadataClassSimpleName(metadataClassSimpleName);
        cmp.setMetadataClassPackage(metadataPackage);

        var components = new HashMap<>(cmp.getComponentProperties());
        var values = new HashMap<>(cmp.getValueProperties());
        var statics = new HashMap<>(cmp.getStaticValueProperties());
        for (PropertyMetaData pm : bm.getProperties()) {
            if (!pm.isLocal()) {
                continue;
            }
            if (cmp.getComponentProperties().containsKey(pm.getName())) {
                ComponentProperty<?> p = cmp.getComponentProperties().get(pm.getName());
                p.setMetadata(pm);
                p.setMemberName(pm.getName());
                components.remove(pm.getName());
            }
            if (cmp.getValueProperties().containsKey(pm.getName())) {
                ValueProperty<?> p = cmp.getValueProperties().get(pm.getName());
                p.setMetadata(pm);
                p.setMemberName(pm.getName());

                populateDefaultValueAndNullEquivalent(bm, pm, p);
                values.remove(pm.getName());
            }
            if (cmp.getStaticValueProperties().containsKey(pm.getName())) {
                ValueProperty<?> p = cmp.getStaticValueProperties().get(pm.getName());
                p.setMetadata(pm);
                p.setMemberName(pm.getResidenceClass().getSimpleName() + "_" + pm.getName());

                populateDefaultValueAndNullEquivalent(bm, pm, p);
                statics.remove(pm.getName());
            }
        }

        components.entrySet().forEach(e -> {
            logger.warn("Component property {}.{} is not found in metadata class", bm.getType().getName(), e.getKey());
            cmp.getComponentProperties().remove(e.getKey());
        });
        values.entrySet().forEach(e -> {
            logger.warn("Value property {}.{} is not found in metadata class", bm.getType().getName(), e.getKey());
            cmp.getValueProperties().remove(e.getKey());
        });
        statics.entrySet().forEach(e -> {
            logger.warn("Static property {}.{} is not found in metadata class", bm.getType().getName(), e.getKey());
            cmp.getStaticValueProperties().remove(e.getKey());
        });
        return cmp;
    }

    private void populateDefaultValueAndNullEquivalent(BeanMetaData<?> bm, PropertyMetaData pm, ValueProperty<?> p) {
        Object def = pm.getDefaultValue();
        String defaultValue = computeStringValue(bm, pm);
        String nullEquivalent = null;

        if (pm.getType().isEnum() && def != null && defaultValue == null) {
            nullEquivalent = def.toString();
        }

        p.setDefaultValue(defaultValue);
        p.setNullEquivalent(nullEquivalent);
    }

    private void populateUpdatedProperties(Component<?, ?, ?> c, Object instance,
            Map<Class<?>, Component<?, ?, ?>> localComponents, Map<Class<?>, Component<?, ?, ?>> availableComponents) {

        if (c.getParent() == null) {
            return;
        }

        Component<?, ?, ?> parent = c.getParent();
        BeanMetaData<?> parentMetadata = parent.getMetadata();

        Object parentInstance = parentMetadata.getDefaultInstance();
        // iterate all properties (parent's ones included)
        for (PropertyMetaData property : parentMetadata.getProperties()) {
            // discard read only
            if (!property.isReadWrite()) {
                continue;
            }

            try {
                Object parentValue = property.getDefaultValue();
                Object currentValue = property.getGetterMethod().invoke(instance);

                if (Objects.equals(parentValue, currentValue)) {
                    continue;
                }
                // default value has been updated in this component
                // we need to re-declare the property metadata in this component
                var residenceClass = property.getResidenceClass();
                var updatedProperty = PropertyMetaData.relocalize(property, c.getMetadata());

                // get parent where this property is localy declared
                // first look for it in this generation run
                Component<?, ?, ?> parentComponent = localComponents.getOrDefault(residenceClass, availableComponents.get(residenceClass));

                if (parentComponent == null) {
                    continue;
                }

                var originalValue = parentComponent.getValueProperties().get(property.getName());

                if (originalValue == null) {
                    continue;
                }

                ValueProperty<Object> updatedValue = new ValueProperty<>();
                updatedValue.setCustomization(originalValue.getCustomization());
                updatedValue.setMemberName(originalValue.getMemberName());
                updatedValue.setMetadata(updatedProperty);
                updatedValue.setMetadataClass(originalValue.getMetadataClass());
                updatedValue.setNullEquivalent(originalValue.getNullEquivalent());
                updatedValue.setDefaultValue(computeStringValue(c.getMetadata(), updatedProperty));
                ((Component<?, ?, Object>) c).getUpdatedValueProperties().put(updatedProperty.getName(),
                        updatedValue);

            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }

    }

    private Component<?, ?, ?> load(Class<?> cls) throws StreamWriteException, DatabindException, IOException {
        logger.info("Loading class properties of {}", cls.getName());
        Path target = javaGenerationContext.getInputResourceFolder().toPath().resolve(PropertyGenerator.propertyPath(cls));

        if (Files.exists(target)) {
            return mapper.readValue(target.toFile(), componentType);
        }

        String resource = PropertyGenerator.resourcePath(cls);
        var stream = this.getClass().getResourceAsStream(resource);

        if (stream != null) {
            return mapper.readValue(stream, componentType);
        }

        return new Component<>();

    }

    private <CC, CPC, VPC> void populateParentComponent(Component<CC, CPC, VPC> component,
            Map<Class<?>, Component<?, ?, ?>> localComponents, Map<Class<?>, Component<?, ?, ?>> availableComponents) {
        Component<?, ?, ?> parent = null;
        Class<?> current = component.getMetadata().getType().getSuperclass();

        while (current != null && parent == null) {

            Component<?, ?, ?> parentComponent = localComponents.get(current);

            if (parentComponent != null) {
                component.setParent((Component<CC, CPC, VPC>)parentComponent);
                return;
            }
            parent = availableComponents.getOrDefault(current, null);
            current = current.getSuperclass();
        }
    }

    private void populateComponentDependencies(Component<?, ?, ?> component,
            Map<Class<?>, Component<?, ?, ?>> localComponents, Map<Class<?>, Component<?, ?, ?>> availableComponents) {

        component.getComponentProperties().values().stream().forEach(p -> {
            if (p instanceof ComponentProperty cp) {
                Class<?> contentType = cp.getMetadata().getContentType();

                if (component.getMetadata().getType().equals(contentType)) {
                    // no need to add itself to dependencies
                    return;
                }

                if (localComponents.containsKey(contentType)) {
                    cp.setContentTypeMetadataClassName(localComponents.get(contentType).getMetadataClassName());
                } else if (availableComponents.containsKey(contentType)) {
                    cp.setContentTypeMetadataClassName(availableComponents.get(contentType).getMetadataClassName());
                }

                if (cp.getContentTypeMetadataClassName() == null) {
                    logger.warn(
                            "No metadata class found for " + contentType + ", it is referenced by component property "
                                    + component.getMetadata().getType() + "." + p.getMetadata().getName());
                } else {
                    component.getComponentDependencies().put(contentType, cp.getContentTypeMetadataClassName());
                }
            }
        });

        component.getStaticValueProperties().values().stream().forEach(p -> {
            if (p instanceof ValueProperty vp) {
                Class<?> applicability = vp.getMetadata().getApplicability();

                if (component.getMetadata().getType().equals(applicability)) {
                    // no need to add itself to dependencies
                    return;
                }

                String metadataClassName = null;
                if (localComponents.containsKey(applicability)) {
                    metadataClassName = localComponents.get(applicability).getMetadataClassName();
                } else if (availableComponents.containsKey(applicability)) {
                    metadataClassName = availableComponents.get(applicability).getMetadataClassName();
                }

                if (metadataClassName == null) {
                    logger.warn(
                            "No metadata class found for " + applicability + ", it is referenced by static property "
                                    + component.getMetadata().getType() + "." + p.getMetadata().getName());
                } else {
                    component.getComponentDependencies().put(applicability, metadataClassName);
                }

            }
        });
    }

    /**
     * Compute the string value of the property
     *
     * @param beanMetadata
     * @param propertyMetadata
     * @return
     */
    // TODO move this method to a more appropriate place, this is too specific to
    // the JavaGeneratorImpl and contains too much specific cases
    protected String computeStringValue(BeanMetaData<?> beanMetadata, PropertyMetaData propertyMetadata) {

        if (propertyMetadata.getPropertyType() == Type.CALLBACK) {
            return "null";
        }
        if (propertyMetadata.getPropertyType() == Type.EVENT) {
            return "null";
        }

        Object def = propertyMetadata.getDefaultValue();
        String defaultValue = null;
        String nullEquivalent = null;

        if (def != null) {
            try {
                Class propertyReturnType = propertyMetadata.getType();
                Class valueCls = def.getClass();
                // if ((pm instanceof EnumerationPropertyMetadata) &&
                // v.getValueClass().isEnum()) {
                if (propertyReturnType.isEnum()) {

                    String enumPack = propertyMetadata.getType().getName().replace("$", ".");
                    try {
                        Enum.valueOf(propertyReturnType, def.toString()); // check enum value exists
                        defaultValue = enumPack + "." + def.toString();
                    } catch (Exception e) {
                        nullEquivalent = def.toString();
                        defaultValue = null;
                    }

                } else {

                    if (propertyReturnType == String.class) {
                        defaultValue = def.toString();
                        if (defaultValue.toString().startsWith("<html>")) {
                            defaultValue = defaultValue.toString().replace("\"", "\\\"");
                        }
                        defaultValue = "\"" + defaultValue + "\"";
                    } else if (propertyMetadata.isCollection() && (def instanceof Collection c) && c.isEmpty()) {
                        defaultValue = "java.util.Collections.emptyList()";
                    } else if (propertyMetadata.isCollection() && def instanceof Collection c) {
                        String items = (String) c.stream().map(i -> "\"" + i.toString() + "\"")
                                .collect(Collectors.joining(","));
                        defaultValue = String.format("java.util.Arrays.asList(%s)", items);
                    } else if (propertyMetadata.isCollection() && valueCls.isArray()) {
                        Object[] arrObject = ReflectionUtils.convertToObjectArray(def);
                        if (arrObject.length == 0) {
                            defaultValue = "java.util.Collections.emptyList()";
                        } else {
                            String items = Arrays.stream(arrObject)
                                    .map(i -> ReflectionUtils
                                            .getPrimitiveDeclaration(propertyMetadata.getCollectionType(), i))
                                    .collect(Collectors.joining(","));
                            defaultValue = String.format("java.util.Arrays.asList(%s)", items);
                        }

                    } else if (def instanceof Boolean) {
                        defaultValue = def.toString();
                    }

                    if (defaultValue == null) {
                        defaultValue = ReflectionUtils.findStaticMemberByValue(def.getClass(), def);
                    }
                    if (defaultValue == null) {
                        defaultValue = ReflectionUtils.findStaticGetMethodByValue(def.getClass(), def);
                    }
                    if (defaultValue == null) {
                        Class<?> componentClass = beanMetadata.getType();
                        defaultValue = ReflectionUtils.findStaticMemberByValue(componentClass, def);
                    }

                    if (defaultValue == null && def instanceof BoundingBox dv) {
                        defaultValue = String.format("new javafx.geometry.BoundingBox(%s, %s, %s, %s, %s, %s)",
                                dv.getMinX(), dv.getMinY(), dv.getMinZ(), dv.getWidth(), dv.getHeight(), dv.getDepth());
                    }

                    if (defaultValue == null && def instanceof Font dv) {
                        defaultValue = String.format("javafx.scene.text.Font.font(\"%s\", %s)", dv.getFamily(),
                                dv.getSize());
                    }

                    if (defaultValue == null && def instanceof Point3D dv) {
                        defaultValue = String.format("new javafx.geometry.Point3D(%s, %s, %s)", dv.getX(), dv.getY(),
                                dv.getZ());
                    }

                    if (defaultValue == null) {
                        defaultValue = def.toString();
                    }
                }
            } catch (Exception | Error e) {
                logger.error("Computing default value failed", e);
            }
        }

        if (defaultValue == null && nullEquivalent == null) {
            defaultValue = "null";
        }

        return defaultValue;
    }

    private Component<?, ?, ?> toParent(Class<?> cls, String metadataClassName) {
        BeanMetaData<?> metadata = converter.produce(cls);
        String pkg = metadataClassName.substring(0, metadataClassName.lastIndexOf('.'));
        String simpleName = metadataClassName.substring(metadataClassName.lastIndexOf('.') + 1);

        Component<?, ?, ?> parent = new Component<>();
        parent.setMetadataClassName(metadataClassName);
        parent.setMetadataClassSimpleName(simpleName);
        parent.setMetadataClassPackage(pkg);
        parent.setMetadata(metadata);
        return parent;
    }

    public static String getLastSegments(int numSegments, Class<?> cls) {
        String[] segments = cls.getPackage().getName().split("\\.");
        if (numSegments >= segments.length) {
            return cls.getPackage().getName();
        }
        String[] lastSegments = new String[numSegments];
        System.arraycopy(segments, segments.length - numSegments, lastSegments, 0, numSegments);
        return String.join(".", lastSegments);
    }

    @Override
    public String customizeComponent(Context context, Component<?, ?, ?> component) throws IOException {
        String template = javaGenerationContext.getComponentCustomizationTemplate();

        if (template == null || template.isEmpty()) {
            return null;
        }
        return generation.generate(Map.of("logger", logger, "context", context, "component", component), template);
    }

    @Override
    public String customizeComponentProperty(Context context, Component<?, ?, ?> component,
            ComponentProperty<?> property) throws IOException {
        String template = javaGenerationContext.getComponentPropertyCustomizationTemplate();

        if (template == null || template.isEmpty()) {
            return null;
        }
        return generation.generate(Map.of("logger", logger, "context", context, "component", component, "property", property), template);
    }

    @Override
    public String customizeValueProperty(Context context, Component<?, ?, ?> component, ValueProperty<?> property)
            throws IOException {
        String template = javaGenerationContext.getValuePropertyCustomizationTemplate();

        if (template == null || template.isEmpty()) {
            return null;
        }
        return generation.generate(Map.of("logger", logger, "context", context, "component", component, "property", property), template);
    }

    @Override
    public String customizeStaticValueProperty(Context context, Component<?, ?, ?> component, ValueProperty<?> property)
            throws IOException {
        String template = javaGenerationContext.getStaticValuePropertyCustomizationTemplate();

        if (template == null || template.isEmpty()) {
            return null;
        }
        return generation.generate(Map.of("logger", logger, "context", context, "component", component, "property", property), template);
    }

    @Override
    public String customizeComponentConstructor(Context context, Component<?, ?, ?> component) throws IOException {
        String template = javaGenerationContext.getComponentConstructorCustomizationTemplate();

        if (template == null || template.isEmpty()) {
            return null;
        }
        return generation.generate(Map.of("logger", logger, "context", context, "component", component), template);
    }

}
