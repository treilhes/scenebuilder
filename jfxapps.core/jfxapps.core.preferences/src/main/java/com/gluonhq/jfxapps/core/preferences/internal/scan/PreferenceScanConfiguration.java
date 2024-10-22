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
package com.gluonhq.jfxapps.core.preferences.internal.scan;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.TypeFilterUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.config.ConfigurationUtils;
import org.springframework.data.util.Streamable;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.gluonhq.jfxapps.core.api.preference.PreferenceScan;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanNameGenerator;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceMetadata;

/**
 * Annotation-based {@link PreferenceScanConfiguration}.
 */
public class PreferenceScanConfiguration {

    private final BeanDefinition definition;
    private final Class<?> holderClass;
    private final PreferenceScanMetadata preferenceScanMetadata;

    //private final Environment environment;
    //private final PreferenceBeanNameGenerator beanNameGenerator;
    //private final BeanDefinitionRegistry registry;
    //private final AnnotationMetadata configMetadata;
    //private final AnnotationMetadata enableAnnotationMetadata;
    //private final AnnotationAttributes attributes;
    //private final Function<AnnotationAttributes, Stream<TypeFilter>> typeFilterFunction;
    private final boolean hasExplicitFilters;
    private final Function<AnnotationAttributes, Stream<TypeFilter>> typeFilterFunction;
    private final AnnotationAttributes attributes;

    public PreferenceScanConfiguration(Class<?> holderClass, AnnotatedBeanDefinition definition, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry) {
        this.definition = definition;
        this.holderClass = holderClass;
        this.preferenceScanMetadata = this.holderClass != null
                ? PreferenceScanMetadata.getMetadata(this.holderClass)
                : null;
        this.hasExplicitFilters = hasExplicitFilters(this.preferenceScanMetadata);
        var annotationAttributes = definition.getMetadata().getAnnotationAttributes(PreferenceScan.class.getName());
        this.attributes = new AnnotationAttributes(annotationAttributes);
        this.typeFilterFunction = it -> TypeFilterUtils.createTypeFiltersFor(it, environment, resourceLoader, registry)
                .stream();
    }

    public Streamable<String> getBasePackages() {

        String[] value = this.preferenceScanMetadata.getValue();
        String[] basePackages = this.preferenceScanMetadata.getBasePackages();
        Class<?>[] basePackageClasses = this.preferenceScanMetadata.getBasePackageClasses();

        // return package of annotated class
        if (value.length == 0 && basePackages.length == 0 && basePackageClasses.length == 0) {
            return Streamable.of(holderClass.getPackageName());
        }

        Set<String> packages = new HashSet<>(value.length + basePackages.length + basePackageClasses.length);
        packages.addAll(Arrays.asList(value));
        packages.addAll(Arrays.asList(basePackages));

        for (Class<?> c : basePackageClasses) {
            packages.add(ClassUtils.getPackageName(c));
        }

        return Streamable.of(packages);
    }


    protected Iterable<TypeFilter> getIncludeFilters() {
        return parseFilters("includeFilters");
    }

    public Streamable<TypeFilter> getExcludeFilters() {
        return parseFilters("excludeFilters");
    }

    public boolean shouldConsiderNestedPreferenceScan() {
        return true;
    }

    public boolean usesExplicitFilters() {
        return hasExplicitFilters;
    }

    public String getResourceDescription() {
        return String.format("@%s declared on %s", PreferenceScan.class, holderClass);
    }

    private Streamable<TypeFilter> parseFilters(String attributeName) {

        AnnotationAttributes[] filters = attributes.getAnnotationArray(attributeName);
        return Streamable.of(() -> Arrays.stream(filters).flatMap(typeFilterFunction));
    }
    /**
     * Returns whether there's explicit configuration of include- or exclude filters.
     *
     * @param attributes must not be {@literal null}.
     * @return
     */
    private static boolean hasExplicitFilters(PreferenceScanMetadata preferenceScanMetadata) {
        return preferenceScanMetadata.getExcludeFilters().length > 0 || preferenceScanMetadata.getIncludeFilters().length > 0;
    }
}
