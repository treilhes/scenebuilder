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
package com.gluonhq.jfxapps.core.preferences.internal.behaviour;

import org.springframework.data.util.TypeInformation;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceMetadata;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;

public abstract class AbstractPreferenceBehaviour implements PreferenceBehaviour {

    private final PreferenceMetadata metadata;
    private final PreferenceRepository repository;

    public AbstractPreferenceBehaviour(PreferenceMetadata metadata, PreferenceRepository repository) {
        super();
        this.metadata = metadata;
        this.repository = repository;
    }

    public PreferenceMetadata getMetadata() {
        return metadata;
    }

    public PreferenceRepository getRepository() {
        return repository;
    }

    /**
     * Convert Spring Data's TypeInformation<S> to Jackson's JavaType
     */
    private JavaType convertToJavaType(ObjectMapper objectMapper, TypeInformation<?> typeInformation) {
        // Get Jackson's TypeFactory from the ObjectMapper
        TypeFactory typeFactory = objectMapper.getTypeFactory();

        // Get the raw type (class) from the TypeInformation
        Class<?> rawType = typeInformation.getType();

        // Check if the type has generics
        if (typeInformation.getTypeArguments().isEmpty()) {
            // If no generics, create a simple JavaType for the raw class
            return typeFactory.constructType(rawType);
        } else {
            // If there are generics, we need to recursively convert them to JavaType
            JavaType[] javaTypeArguments = typeInformation.getTypeArguments()
                    .stream()
                    .map(o -> this.convertToJavaType(objectMapper, o))  // Recursively convert the type arguments
                    .toArray(JavaType[]::new);

            // Construct a parameterized JavaType for the raw class with generic arguments
            return typeFactory.constructParametricType(rawType, javaTypeArguments);
        }
    }

    public JavaType getJavaType() {
        return convertToJavaType(Preference.objectMapper, metadata.getGenericTypeInformation());
    }
}
