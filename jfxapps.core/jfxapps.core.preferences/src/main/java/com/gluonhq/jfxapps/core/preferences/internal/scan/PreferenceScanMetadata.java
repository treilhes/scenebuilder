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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.PreferenceScan;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;

/**
 * Inspect generic types of {@link Preference} and {@link PreferenceContext}
 * annotation.}
 */
public class PreferenceScanMetadata {

    private static final String MUST_BE_A_CONFIGURATION = String.format("Type must be annoted with %s", Configuration.class);

    private final Class<?> classHolder;
    private final TypeInformation<?> typeInformation;

    private final boolean preferenceScanAnnotation;

    private String[] value;
    private Class<?>[] basePackageClasses;
    private String[] basePackages;
    private Filter[] excludeFilters;
    private Filter[] includeFilters;

    /**
     * Creates a new {@link PreferenceScanMetadata} for the given preference interface.
     *
     * @param holdingClass must not be {@literal null}.
     * @return
     */
    public static PreferenceScanMetadata getMetadata(Class<?> holdingClass) {

        Assert.notNull(holdingClass, "Class holding @PreferenceScan annotation must not be null");

        return new PreferenceScanMetadata(holdingClass);
    }

    /**
     * Creates a new {@link PreferenceScanMetadata} for the given class.
     *
     * @param holdingClass must not be {@literal null}.
     */
    public PreferenceScanMetadata(Class<?> holdingClass) {

        Assert.notNull(holdingClass, "Given type must not be null");

        this.classHolder = holdingClass;
        this.typeInformation = TypeInformation.of(holdingClass);

        var scanAnnotation = AnnotationUtils.findAnnotation(holdingClass, PreferenceScan.class);

        this.preferenceScanAnnotation = scanAnnotation != null;

        if (this.preferenceScanAnnotation) {
            this.value = scanAnnotation.value();
            this.basePackageClasses = scanAnnotation.basePackageClasses();
            this.basePackages = scanAnnotation.basePackages();
            this.excludeFilters = scanAnnotation.excludeFilters();
            this.includeFilters = scanAnnotation.includeFilters();
        } else {
            this.value = new String[0];
            this.basePackageClasses = new Class<?>[0];
            this.basePackages = new String[0];
            this.excludeFilters = new Filter[0];
            this.includeFilters = new Filter[0];
        }

    }

    public TypeInformation<?> getTypeInformation() {
        return typeInformation;
    }

    public Class<?> getClassHolder() {
        return classHolder;
    }

    public boolean hasPreferenceScanAnnotation() {
        return preferenceScanAnnotation;
    }

    public String[] getValue() {
        return value;
    }

    public Class<?>[] getBasePackageClasses() {
        return basePackageClasses;
    }

    public String[] getBasePackages() {
        return basePackages;
    }

    public Filter[] getExcludeFilters() {
        return excludeFilters;
    }

    public Filter[] getIncludeFilters() {
        return includeFilters;
    }


}