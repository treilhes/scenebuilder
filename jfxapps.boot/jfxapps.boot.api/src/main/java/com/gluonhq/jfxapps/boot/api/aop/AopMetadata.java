/*
 * Copyright (c) 2016, 2025, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2025, Pascal Treilhes and/or its affiliates.
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
package com.gluonhq.jfxapps.boot.api.aop;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.data.core.TypeInformation;
import org.springframework.util.Assert;

/**
 * Inspect types extending/implementing the marker class and annotated with the metadata annotation.
 * @param <A> the annotation type holding the metadata.
 * @param <M> the marker class type.
 */
public abstract class AopMetadata<A extends Annotation, M> {

    private static final String MUST_BE_A = "Type must be a %s";

    private final Class<?> beanClass;
    private final TypeInformation<?> typeInformation;
    private final TypeInformation<?> genericTypeInformation;

    private final boolean hasAnnotation;
    private final String scope;

    /**
     * Creates a new {@link AopMetadata} for the given bean class.
     *
     * @param beanClass must not be {@literal null}.
     */
    public AopMetadata(Class<A> annotationClass, Class<M> markerClass, Class<?> beanClass) {

        Assert.notNull(beanClass, "Given type must not be null");
        Assert.isTrue(beanClass.isInterface(), "Given type must be an interface");

        this.beanClass = beanClass;
        this.typeInformation = TypeInformation.of(beanClass);

        Assert.isTrue(markerClass.isAssignableFrom(beanClass), String.format(MUST_BE_A, markerClass));

        List<TypeInformation<?>> arguments = TypeInformation.of(beanClass)
                .getRequiredSuperTypeInformation(markerClass)
                .getTypeArguments();

        // FIXME only handling one generic type for now
        if (arguments.isEmpty()) {
            this.genericTypeInformation = null;
        } else {
            this.genericTypeInformation = resolveTypeParameter(arguments, 0,
                    () -> String.format("Could not resolve type of %s", beanClass));
        }


        A contextAnnotation = AnnotationUtils.findAnnotation(beanClass, annotationClass);
        this.scope = AnnotationUtils.findAnnotation(beanClass, Scope.class).scopeName();

        this.hasAnnotation = contextAnnotation != null;

        loadMetadata(contextAnnotation);

    }

    protected abstract void loadMetadata(A annotation);

    public TypeInformation<?> getTypeInformation() {
        return typeInformation;
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public boolean hasAnnotation() {
        return hasAnnotation;
    }

    public String getScope() {
        return scope;
    }


    public boolean hasGenericType() {
        return this.genericTypeInformation != null;
    }

    public TypeInformation<?> getGenericTypeInformation() {
        return this.genericTypeInformation;
    }

    /**
     * Returns the generic content raw class of the given class.
     *
     * @return the content raw class.
     */
    public Class<?> getGenericType() {
        return hasGenericType() ? getGenericTypeInformation().getType() : null;
    }

    private static TypeInformation<?> resolveTypeParameter(List<TypeInformation<?>> arguments, int index,
            Supplier<String> exceptionMessage) {

        if ((arguments.size() <= index) || (arguments.get(index) == null)) {
            throw new IllegalArgumentException(exceptionMessage.get());
        }

        return arguments.get(index);
    }
}