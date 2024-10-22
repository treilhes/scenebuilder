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
package com.gluonhq.jfxapps.core.preferences.internal.preference;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * {@link BeanNameGenerator} to create bean names for preferences. Will delegate to an
 * {@link AnnotationBeanNameGenerator} but let the delegate work with a customized {@link BeanDefinition} to make sure
 * the preference interface is inspected and not the actual bean definition class.
 *
 */
public class PreferenceBeanNameGenerator implements BeanNameGenerator {

    private final ClassLoader beanClassLoader;
    private final BeanNameGenerator generator;

    /**
     * Creates a new {@link PreferenceBeanNameGenerator} for the given {@link ClassLoader}, {@link BeanNameGenerator}, and
     * {@link BeanDefinitionRegistry}.
     *
     * @param beanClassLoader must not be {@literal null}.
     * @param generator must not be {@literal null}.
     * @param registry must not be {@literal null}.
     */
    public PreferenceBeanNameGenerator(ClassLoader beanClassLoader, BeanNameGenerator generator) {

        Assert.notNull(beanClassLoader, "Bean ClassLoader must not be null");
        Assert.notNull(generator, "BeanNameGenerator must not be null");

        this.beanClassLoader = beanClassLoader;
        this.generator = generator;

    }

    /**
     * Generate a bean name for the given bean definition.
     *
     * @param definition the bean definition to generate a name for
     * @return the generated bean name
     */
    @Override
    public String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry) {

        AnnotatedBeanDefinition beanDefinition = definition instanceof AnnotatedBeanDefinition
                ? (AnnotatedBeanDefinition) definition
                : new AnnotatedGenericBeanDefinition(getPreferenceInterface(definition));

        return generator.generateBeanName(beanDefinition, registry);
    }

    /**
     * Returns the type configured for the {@code preferenceInterface} property of the given bean definition. Uses a
     * potential {@link Class} being configured as is or tries to load a class with the given value's {@link #toString()}
     * representation.
     *
     * @param beanDefinition
     * @return
     */
    private Class<?> getPreferenceInterface(BeanDefinition beanDefinition) {

        ConstructorArgumentValues.ValueHolder argumentValue = beanDefinition.getConstructorArgumentValues()
                .getArgumentValue(0, Class.class);

        if (argumentValue == null) {
            throw new IllegalStateException(
                    String.format("Failed to obtain first constructor parameter value of BeanDefinition %s", beanDefinition));
        }

        Object value = argumentValue.getValue();

        if (value == null) {

            throw new IllegalStateException(
                    String.format("Value of first constructor parameter value of BeanDefinition %s is null", beanDefinition));

        } else if (value instanceof Class<?>) {

            return (Class<?>) value;

        } else {

            try {
                return ClassUtils.forName(value.toString(), beanClassLoader);
            } catch (Exception o_O) {
                throw new RuntimeException(o_O);
            }
        }
    }
}