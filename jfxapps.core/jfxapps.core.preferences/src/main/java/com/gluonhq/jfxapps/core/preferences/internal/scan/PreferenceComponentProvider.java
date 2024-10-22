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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.core.api.preference.NoPreferenceBean;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;

/**
 * Custom {@link ClassPathScanningCandidateComponentProvider} scanning for
 * interfaces extending {@link Preference} and annotated with
 * {@link PreferenceContext}. Skips interfaces annotated with
 * {@link NoPreferenceBean}.
 */
public class PreferenceComponentProvider extends ClassPathScanningCandidateComponentProvider {

    private boolean considerNestedPreferenceInterfaces;
    private BeanDefinitionRegistry registry;

    /**
     * Creates a new {@link PreferenceComponentProvider} using the given
     * {@link TypeFilter} to include components to be picked up.
     *
     * @param includeFilters the {@link TypeFilter}s to select preference interfaces
     *                       to consider, must not be {@literal null}.
     */
    public PreferenceComponentProvider(Iterable<? extends TypeFilter> includeFilters, BeanDefinitionRegistry registry) {

        super(false);

        Assert.notNull(includeFilters, "Include filters must not be null");
        Assert.notNull(registry, "BeanDefinitionRegistry must not be null");

        this.registry = registry;

        if (includeFilters.iterator().hasNext()) {
            for (TypeFilter filter : includeFilters) {
                //@formatter:off
                super.addIncludeFilter(new AggregatedTypeFilter(List.of(
                        filter,
                        (mr, mrf) -> mr.getClassMetadata().isInterface(),
                        new AssignableTypeFilter(Preference.class),
                        new AnnotationTypeFilter(PreferenceContext.class, true, true))));
                //@formatter:on
            }
        } else {
            //@formatter:off
            super.addIncludeFilter(new AggregatedTypeFilter(List.of(
                    (mr, mrf) -> mr.getClassMetadata().isInterface(),
                    new AssignableTypeFilter(Preference.class),
                    new AnnotationTypeFilter(PreferenceContext.class, true, true))));
            //@formatter:on
        }

        addExcludeFilter(new AnnotationTypeFilter(NoPreferenceBean.class));
    }

    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonPreferenceInterface = !Preference.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isTopLevelType = !beanDefinition.getMetadata().hasEnclosingClass();
        boolean isConsiderNestedRepositories = isConsiderNestedPreferenceInterfaces();

        return isNonPreferenceInterface && (isTopLevelType || isConsiderNestedRepositories);
    }

    /**
     * Customizes the preference interface detection and triggers annotation
     * detection on them.
     */
    @Override
    public Set<BeanDefinition> findCandidateComponents(String basePackage) {

        Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);

        for (BeanDefinition candidate : candidates) {
            if (candidate instanceof AnnotatedBeanDefinition) {
                AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
            }
        }

        return candidates;
    }

    @NonNull
    @Override
    protected BeanDefinitionRegistry getRegistry() {
        return registry;
    }

    /**
     * @return the considerNestedPreferenceInterfaces
     */
    public boolean isConsiderNestedPreferenceInterfaces() {
        return considerNestedPreferenceInterfaces;
    }

    /**
     * Controls whether nested inner-class {@link Preference} interface definitions
     * should be considered for automatic discovery. This defaults to
     * {@literal false}.
     *
     * @param considerNestedPreferenceInterfaces
     */
    public void setConsiderNestedPreferenceInterfaces(boolean considerNestedPreferenceInterfaces) {
        this.considerNestedPreferenceInterfaces = considerNestedPreferenceInterfaces;
    }

    private static class AggregatedTypeFilter implements TypeFilter {

        private final List<TypeFilter> filters;

        public AggregatedTypeFilter(List<TypeFilter> delegates) {

            Assert.notNull(delegates, "filters must not be null");
            this.filters = delegates;
        }

        @Override
        public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory)
                throws IOException {

            for (TypeFilter filter : filters) {
                if (!filter.match(metadataReader, metadataReaderFactory)) {
                    return false;
                }
            }

            return true;
        }
    }
}