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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import com.gluonhq.jfxapps.core.fxom.util.PropertyName;
import com.gluonhq.jfxapps.core.metadata.klass.ComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.klass.CustomComponentClassMetadata;
import com.gluonhq.jfxapps.core.metadata.property.PropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.BooleanPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DoublePropertyMetadata.CoordinateDoublePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.DurationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EnumerationPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.EventHandlerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.FontPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.ImagePropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.IntegerPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.StringPropertyMetadata.I18nStringPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.list.StringListPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.paint.ColorPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.property.value.paint.PaintPropertyMetadata;
import com.gluonhq.jfxapps.core.metadata.util.SBDuration;

import javafx.fxml.FXMLLoader;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;

/**
 *
 *
 */
// FIXME rewrite me fully
public abstract class MetadataIntrospector<CC, PC, VC> {

    /**
     * During data introspection of an unknown custom component, if the name match,
     * the property will be ignored.
     */
    private final Set<PropertyName> hiddenProperties = new HashSet<>();

    public MetadataIntrospector() {
    }

    protected void addHiddenProperty(PropertyName propertyName) {
        hiddenProperties.add(propertyName);
    }

    public Set<PropertyName> getHiddenProperties() {
        return hiddenProperties;
    }

    protected abstract CC customizeComponent(Class<?> componentClass,
            ComponentClassMetadata<?, CC, PC, VC> ancestorMetadata, Set<PropertyMetadata<?>> properties, Exception exception);

    protected abstract PC customizeComponentProperty(PropertyDescriptor d, int counter);

    protected abstract VC customizeValueProperty(PropertyDescriptor d, int counter);

    public ComponentClassMetadata<?, CC, PC, VC> introspect(Class<?> componentClass,
            ComponentClassMetadata<?, CC, PC, VC> ancestorMetadata, AbstractMetadata<CC, PC, VC> metadata) {

        final Set<PropertyMetadata<?>> properties = new HashSet<>();
        final Set<PropertyName> hiddenProperties = getHiddenProperties();
        Exception exception;
        int counter = 0;

        try {

            final Object sample = instantiate(componentClass);
            final BeanInfo beanInfo = Introspector.getBeanInfo(componentClass);

            for (PropertyDescriptor d : beanInfo.getPropertyDescriptors()) {

                final PropertyName name = new PropertyName(d.getName());

                if (!hiddenProperties.contains(name)) {

                    PropertyMetadata<?> propertyMetadata = lookupPropertyMetadata(ancestorMetadata, name);

                    if (propertyMetadata == null) {
                        propertyMetadata = makePropertyMetadata(name, d, sample, counter);
                    }

                    if (propertyMetadata != null) {
                        properties.add(propertyMetadata);
                    }
                }

            }
            exception = null;
        } catch (IOException | IntrospectionException x) {
            exception = x;
        }

        final CC componentCustomization = customizeComponent(componentClass, ancestorMetadata, properties, exception);
        final CustomComponentClassMetadata<?, CC, PC, VC> result = new CustomComponentClassMetadata<>(componentClass,
                ancestorMetadata, componentCustomization, exception);

        result.getProperties().addAll(properties);

        return result;
    }

    /*
     * Private
     */

    private Object instantiate(Class<?> componentClass) throws IOException {
        final StringBuilder sb = new StringBuilder();
        Object result;

        /*
         * <?xml version="1.0" encoding="UTF-8"?> // NOCHECK
         *
         * <?import a.b.C?>
         *
         * <C/>
         */

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"); // NOI18N

        sb.append("<?import "); // NOI18N
        sb.append(componentClass.getCanonicalName());
        sb.append("?>"); // NOI18N
        sb.append("<"); // NOI18N
        sb.append(componentClass.getSimpleName());
        sb.append("/>\n"); // NOI18N

        final FXMLLoader fxmlLoader = new FXMLLoader();
        final String fxmlText = sb.toString();
        final byte[] fxmlBytes = fxmlText.getBytes(Charset.forName("UTF-8")); // NOI18N

        try {
            fxmlLoader.setClassLoader(componentClass.getClassLoader());
            result = fxmlLoader.load(new ByteArrayInputStream(fxmlBytes));
        } catch (RuntimeException x) {
            throw new IOException(x);
        }

        return result;
    }

    private PropertyMetadata<?> lookupPropertyMetadata(ComponentClassMetadata<?, CC, PC, VC> ccm, PropertyName propertyName) {
        PropertyMetadata<?> result = null;

        while ((ccm != null) && (result == null)) {
            result = ccm.lookupProperty(propertyName);
            ccm = ccm.getParentMetadata();
        }

        return result;
    }

    private PropertyMetadata<?> makePropertyMetadata(PropertyName name, PropertyDescriptor propertyDescriptor,
            Object sample, int counter) {
        PropertyMetadata<?> result;

        if (propertyDescriptor.getPropertyType() == null) {
            result = null;
        } else if (propertyDescriptor.getReadMethod() == null) {
            result = null;
        } else {
            final Class<?> propertyType = canonizeClass(propertyDescriptor.getPropertyType());
            final boolean readWrite = propertyDescriptor.getWriteMethod() != null;

            VC propertyCustomization = customizeValueProperty(propertyDescriptor, counter);

            if (propertyType.isArray()) {
                result = null;
            } else if (propertyType.isEnum()) {
                final Object fallback = propertyType.getEnumConstants()[0];

                // @formatter:off
                result = new EnumerationPropertyMetadata.Builder<Enum<?>, VC>((Class<Enum<?>>) propertyType)
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Enum<?>) getDefaultValue(sample, propertyDescriptor.getReadMethod(), fallback))
                        .withCustomization(propertyCustomization)
                        .build();
                // @formatter:on

            } else if (propertyType == Boolean.class) {
                // @formatter:off
                result = new BooleanPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Boolean) getDefaultValue(sample, propertyDescriptor.getReadMethod(), false))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on

            } else if (propertyType == Integer.class) {

             // @formatter:off
                result = new IntegerPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Integer) getDefaultValue(sample, propertyDescriptor.getReadMethod(), 0))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on

            } else if (propertyType == Double.class) {

             // @formatter:off
                result = new CoordinateDoublePropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Double) getDefaultValue(sample, propertyDescriptor.getReadMethod(), 0.0))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on

            } else if (propertyType == String.class) {
             // @formatter:off
                result = new I18nStringPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((String) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.scene.paint.Color.class) {
             // @formatter:off
                result = new ColorPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Color) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.scene.paint.Paint.class) {
             // @formatter:off
                result = new PaintPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Paint) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.scene.text.Font.class) {
             // @formatter:off
                result = new FontPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue((Font) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.scene.image.Image.class) {
             // @formatter:off
                result = new ImagePropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue(null)
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.util.Duration.class) {
             // @formatter:off
                Duration defaultValue = (Duration) getDefaultValue(sample, propertyDescriptor.getReadMethod(), null);
                result = new DurationPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue(defaultValue == null ? null : new SBDuration(defaultValue))
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on
            } else if (propertyType == javafx.event.EventHandler.class) {
             // @formatter:off
                result = new EventHandlerPropertyMetadata.Builder<VC>()
                        .withName(name)
                        .withReadWrite(readWrite)
                        .withDefaultValue(null)
                        .withCustomization(propertyCustomization)
                        .build();
             // @formatter:on

//            The following doesn't work because FXMLLoader is only prepared to load 'function' types
//            of type EventHandler
//
//            } else if (propertyType == java.util.function.Function.class) {
//                result = new FunctionalInterfacePropertyMetadata(
//                        name,
//                        readWrite,
//                        null,
//                        inspectorPath, FUNCTION);
            } else if (propertyType == javafx.collections.ObservableList.class) {
                String propertyName = name.getName();
                String methodName = "get" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT)
                        + propertyName.substring(1); // NOI18N
                result = null;
                try {
                    Method method = sample.getClass().getMethod(methodName);
                    Type type = method.getGenericReturnType();
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        Type genericType = parameterizedType.getActualTypeArguments()[0];
                        if (genericType instanceof Class) {
                            Class<?> genericClass = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                            if (genericClass.equals(java.lang.String.class)) {
                             // @formatter:off
                                result = new StringListPropertyMetadata.Builder<VC>()
                                        .withName(name)
                                        .withReadWrite(readWrite)
                                        .withDefaultValue(Collections.emptyList())
                                        .withCustomization(propertyCustomization)
                                        .build();
                             // @formatter:on
                            }
                        }
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
            } else {
                result = null;
            }
        }

        return result;
    }

    private Class<?> canonizeClass(Class<?> c) {
        final Class<?> result;

        if (c.equals(boolean.class)) {
            result = Boolean.class;
        } else if (c.equals(double.class)) {
            result = Double.class;
        } else if (c.equals(int.class)) {
            result = Integer.class;
        } else {
            result = c;
        }

        return result;
    }

    private Object getDefaultValue(Object sample, Method readMethod, Object fallback) {
        Object result;

        try {
            result = readMethod.invoke(sample);
        } catch (InvocationTargetException | IllegalAccessException x) {
            result = fallback;
        }

        return result;
    }
}
