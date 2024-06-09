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
package com.gluonhq.jfxapps.metadata.properties.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;
import com.gluonhq.jfxapps.metadata.bean.BeanMetaData;
import com.gluonhq.jfxapps.metadata.bean.MetadataProducer;
import com.gluonhq.jfxapps.metadata.bean.PropertyMetaData;
import com.gluonhq.jfxapps.metadata.finder.api.Descriptor;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerationContext;
import com.gluonhq.jfxapps.metadata.properties.api.PropertyGenerator;
import com.gluonhq.jfxapps.metadata.properties.model.Component;
import com.gluonhq.jfxapps.metadata.properties.model.ComponentProperty;
import com.gluonhq.jfxapps.metadata.properties.model.ValueProperty;

public class PropertyGeneratorImpl implements PropertyGenerator {

    private static final Logger logger = LoggerFactory.getLogger(PropertyGeneratorImpl.class);

    private JavaPropsMapper mapper = new JavaPropsMapper();

    private final PropertyGenerationContext context;

    private Optional<Supplier<?>> compCustoSupplier;

    private Optional<Supplier<?>> compPropCustoSupplier;

    private Optional<Supplier<?>> valuePropCustoSupplier;

    private JavaType componentType;

    public PropertyGeneratorImpl(PropertyGenerationContext context) {
        this.context = context;
        this.compCustoSupplier = toDefaultSupplier(context.getComponentCustomizationClass());
        this.compPropCustoSupplier = toDefaultSupplier(context.getComponentPropertyCustomizationClass());
        this.valuePropCustoSupplier = toDefaultSupplier(context.getValuePropertyCustomizationClass());

        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        TypeFactory typeFactory = TypeFactory.defaultInstance();

        // Constructing the parameterized types
        JavaType componentCustoType = typeFactory.constructType(context.getComponentCustomizationClass().orElse(Void.class));
        JavaType componentPropertyCustoType = typeFactory.constructType(context.getComponentPropertyCustomizationClass().orElse(Void.class));
        JavaType valuePropertyCustoType = typeFactory.constructType(context.getValuePropertyCustomizationClass().orElse(Void.class));

        // Constructing the Component<ComponentCusto, ComponentPropertyCusto, ValuePropertyCusto> type
        this.componentType = typeFactory.constructParametricType(
                Component.class,
                componentCustoType,
                componentPropertyCustoType,
                valuePropertyCustoType
        );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generateProperties(Set<Class<?>> classes, Set<Descriptor> descriptors) {
        MetadataProducer converter = new MetadataProducer(context);

        if (descriptors == null) {
            descriptors = Set.of();
        }

        Set<Class<?>> availableComponentsInClasspath = descriptors.stream()
                .flatMap(d -> d.getClassToMetaClass().keySet().stream()).collect(Collectors.toSet());

        var beanMap = converter.produce(classes);

        for (BeanMetaData<?> bm : beanMap.values()) {

            try {

                var component = buildComponent(context.getOutputResourceFolder(), bm, beanMap, availableComponentsInClasspath);

                save(bm.getType(), component, context.getOutputResourceFolder());

            } catch (Exception e) {
                logger.error("Failed to generate properties for class {}", bm.getType(), e);
                if (context.isFailOnError()) {
                    throw new RuntimeException("Failed to generate properties for class " + bm.getType(),e);
                }
            }
        }

    }

    @Override
    public Component<?, ?, ?> buildComponent(
            File targetFolder,
            BeanMetaData<?> bm,
            Map<Class<?>, BeanMetaData<?>> beanMap,
            Set<Class<?>> availableComponents) throws StreamWriteException, DatabindException, IOException {

        var component = load(bm.getType(), targetFolder);

        var componentCustomization = component.getCustomization();

        if (component.getCustomization() == null) {
            compCustoSupplier.ifPresent(s -> component.setCustomization(s.get()));
        }


        for (PropertyMetaData pm : bm.getProperties()) {
            //if (!pm.isHidden() && pm.isLocal()) {
            if (pm.isLocal()) {

                if (beanMap.containsKey(pm.getContentType()) || availableComponents.contains(pm.getContentType())) { // componentProperty
                    // Extract the existing customization and add it to the new value property
                    var componentProperty = component.getComponentProperties().computeIfAbsent(pm.getName(), k -> new ComponentProperty<>());
                    if (componentProperty.getCustomization() == null) {
                        compPropCustoSupplier.ifPresent(s -> componentProperty.setCustomization(s.get()));
                    }
                } else { // valueProperty
                    if (pm.isStatic()) {
                        var valueProperty = component.getStaticValueProperties().computeIfAbsent(pm.getName(), k -> new ValueProperty<>());
                        if (valueProperty.getCustomization() == null) {
                            valuePropCustoSupplier.ifPresent(s -> valueProperty.setCustomization(s.get()));
                        }
                    } else {
                        var valueProperty = component.getValueProperties().computeIfAbsent(pm.getName(), k -> new ValueProperty<>());
                        if (valueProperty.getCustomization() == null) {
                            valuePropCustoSupplier.ifPresent(s -> valueProperty.setCustomization(s.get()));
                        }
                    }
                }
            }
        }
        return component;
    }

    private Optional<Supplier<?>> toDefaultSupplier(final Optional<Class<?>> cls) {
        return cls.map(c -> () -> {
            try {
                Constructor<?> constructor = c.getConstructor();
                return constructor == null ? null : constructor.newInstance();
            } catch (Exception e) {
                throw new RuntimeException("Unable to find defaultconstructor for class " + cls.get(), e);
            }
        });
    }

    private void save(Class<?> cls, Component<?, ?, ?> component, File targetFolder)
            throws StreamWriteException, DatabindException, IOException {

        Path target = targetFolder.toPath().resolve(PropertyGenerator.propertyPath(cls));
        Files.createDirectories(target.getParent());
        mapper.writeValue(target.toFile(), component);

    }

    private Component<Object, Object, Object> load(Class<?> cls, File targetFolder)
            throws StreamWriteException, DatabindException, IOException {

        Path target = targetFolder.toPath().resolve(PropertyGenerator.propertyPath(cls));

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


}
