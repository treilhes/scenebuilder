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
package com.gluonhq.jfxapps.boot.api.aop.internal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.TypeFilterUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.data.util.Streamable;
import org.springframework.util.ClassUtils;

import com.gluonhq.jfxapps.boot.api.aop.AopScanContext;
import com.gluonhq.jfxapps.boot.api.aop.ScanMetadata;

/**
 * Annotation-based {@link AopScanConfiguration}.
 */
public class AopScanConfiguration {

    private final AopScanContext aopContext;
    private final BeanDefinition definition;
    private final Class<?> holderClass;
    private final ScanMetadata beanScanMetadata;

    private final boolean hasExplicitFilters;
    private final Function<AnnotationAttributes, Stream<TypeFilter>> typeFilterFunction;
    private final AnnotationAttributes attributes;


    public AopScanConfiguration(Class<?> holderClass, AopScanContext aopContext, AnnotatedBeanDefinition definition, ResourceLoader resourceLoader, Environment environment, BeanDefinitionRegistry registry) {
        this.aopContext = aopContext;
        this.definition = definition;
        this.holderClass = holderClass;
        this.beanScanMetadata = this.holderClass != null
                ? aopContext.loadScanMetadata(this.holderClass)
                : null;
        this.hasExplicitFilters = hasExplicitFilters(this.beanScanMetadata);
        var annotationAttributes = definition.getMetadata().getAnnotationAttributes(aopContext.getScanAnnotationClass().getName());
        this.attributes = new AnnotationAttributes(annotationAttributes);
        this.typeFilterFunction = it -> TypeFilterUtils.createTypeFiltersFor(it, environment, resourceLoader, registry)
                .stream();
    }

    public Streamable<String> getBasePackages() {

        String[] value = this.beanScanMetadata.getValue();
        String[] basePackages = this.beanScanMetadata.getBasePackages();
        Class<?>[] basePackageClasses = this.beanScanMetadata.getBasePackageClasses();

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


    public Iterable<TypeFilter> getIncludeFilters() {
        return parseFilters("includeFilters");
    }

    public Streamable<TypeFilter> getExcludeFilters() {
        return parseFilters("excludeFilters");
    }

    public boolean shouldConsiderNestedScan() {
        return true;
    }

    public boolean usesExplicitFilters() {
        return hasExplicitFilters;
    }

    public String getResourceDescription() {
        return String.format("@%s declared on %s", aopContext.getScanAnnotationClass(), holderClass);
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
    private static boolean hasExplicitFilters(ScanMetadata beanScanMetadata) {
        return beanScanMetadata.getExcludeFilters().length > 0 || beanScanMetadata.getIncludeFilters().length > 0;
    }
}
