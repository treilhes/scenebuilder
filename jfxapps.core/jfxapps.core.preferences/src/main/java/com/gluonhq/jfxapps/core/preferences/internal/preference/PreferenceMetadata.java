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

import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.util.TypeInformation;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;

/**
 * Inspect generic types of {@link Preference} and {@link PreferenceContext}
 * annotation.}
 */
public class PreferenceMetadata {

    private static final String MUST_BE_A_PREFERENCE = String.format("Type must be a %s", Preference.class);

    private final Class<?> preferenceInterface;
    private final TypeInformation<?> typeInformation;
    private final TypeInformation<?> genericTypeInformation;

    private final boolean preferenceContextAnnotation;
    private final UUID id;
    private final String name;
    private final Class<? extends DefaultValueProvider<?>> defaultValueProviderClass;
    private final Class<? extends ValueValidator<?>> valueValidatorClass;

    private final String scope;

    private Class<? extends JsonMapper<?>> jsonMapperClass;

    /**
     * Creates a new {@link PreferenceMetadata} for the given preference interface.
     *
     * @param preferenceInterface must not be {@literal null}.
     * @return
     */
    public static PreferenceMetadata getMetadata(Class<?> preferenceInterface) {

        Assert.notNull(preferenceInterface, "Preference interface must not be null");

        return new PreferenceMetadata(preferenceInterface);
    }

    /**
     * Creates a new {@link PreferenceMetadata} for the given preference interface.
     *
     * @param preferenceInterface must not be {@literal null}.
     */
    public PreferenceMetadata(Class<?> preferenceInterface) {

        Assert.notNull(preferenceInterface, "Given type must not be null");
        Assert.isTrue(preferenceInterface.isInterface(), "Given type must be an interface");

        this.preferenceInterface = preferenceInterface;
        this.typeInformation = TypeInformation.of(preferenceInterface);

        Assert.isTrue(Preference.class.isAssignableFrom(preferenceInterface), MUST_BE_A_PREFERENCE);

        List<TypeInformation<?>> arguments = TypeInformation.of(preferenceInterface)
                .getRequiredSuperTypeInformation(Preference.class)
                .getTypeArguments();

        this.genericTypeInformation = resolveTypeParameter(arguments, 0,
                () -> String.format("Could not resolve type of %s", preferenceInterface));

        var contextAnnotation = AnnotationUtils.findAnnotation(preferenceInterface, PreferenceContext.class);
        this.scope = AnnotationUtils.findAnnotation(preferenceInterface, Scope.class).scopeName();

        this.preferenceContextAnnotation = contextAnnotation != null;

        if (this.preferenceContextAnnotation) {
            this.id = UUID.fromString(contextAnnotation.id());
            this.name = contextAnnotation.name();
            this.defaultValueProviderClass = contextAnnotation.defaultValueProvider();
            this.valueValidatorClass = contextAnnotation.validator();
            this.jsonMapperClass = contextAnnotation.jsonMapper();
        } else {
            this.id = null;
            this.name = null;
            this.defaultValueProviderClass = null;
            this.valueValidatorClass = null;
            this.jsonMapperClass = null;
        }

    }

    public TypeInformation<?> getTypeInformation() {
        return typeInformation;
    }

    public TypeInformation<?> getGenericTypeInformation() {
        return this.genericTypeInformation;
    }

    /**
     * Returns the preference content raw class of the given preference class.
     *
     * @return the preference content raw class.
     */
    public Class<?> getGenericType() {
        return getGenericTypeInformation().getType();
    }

    private static TypeInformation<?> resolveTypeParameter(List<TypeInformation<?>> arguments, int index,
            Supplier<String> exceptionMessage) {

        if ((arguments.size() <= index) || (arguments.get(index) == null)) {
            throw new IllegalArgumentException(exceptionMessage.get());
        }

        return arguments.get(index);
    }

    public Class<?> getPreferenceInterface() {
        return this.preferenceInterface;
    }

    public boolean hasPreferenceContextAnnotation() {
        return preferenceContextAnnotation;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Class<? extends DefaultValueProvider<?>> getDefaultValueProviderClass() {
        return defaultValueProviderClass;
    }

    public Class<? extends ValueValidator<?>> getValueValidatorClass() {
        return valueValidatorClass;
    }

    public String getScope() {
        return scope;
    }

    public Class<? extends JsonMapper<?>> getJsonMapperClass() {
        return jsonMapperClass;
    }

}