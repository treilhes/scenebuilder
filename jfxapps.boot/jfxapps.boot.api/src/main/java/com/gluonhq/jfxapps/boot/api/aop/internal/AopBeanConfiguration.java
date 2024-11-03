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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.lang.Nullable;

import com.gluonhq.jfxapps.boot.api.aop.AopContext;
import com.gluonhq.jfxapps.boot.api.aop.AopMetadata;

/**
 * Default implementation of {@link AopBeanConfiguration}.
 */
public class AopBeanConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(AopBeanConfiguration.class);
    private static final String CLASS_LOADING_ERROR = "Could not load type {} using class loader {}";

    private final BeanDefinition definition;
    private final Class<?> beanClass;
    private final AopMetadata beanMetadata;
    private final Class<?> factoryBeanClass;

    public AopBeanConfiguration(ClassLoader loader, AopContext<?, ?, ?> aopContext, BeanDefinition definition) {

        this.definition = definition;
        this.beanClass = loadBeanClass(loader);
        this.beanMetadata = this.beanClass != null
                ? aopContext.loadMetadata(this.beanClass)
                : null;
        this.factoryBeanClass = aopContext.factoryBeanClass();
    }

    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    public AopMetadata getBeanMetadata() {
        return beanMetadata;
    }

    public Class<?> getFactoryBeanClass() {
        return factoryBeanClass;
    }

    public boolean isLazyInit() {
        return definition.isLazyInit();
    }

    public boolean isPrimary() {
        return definition.isPrimary();
    }

    /**
     * Loads the bean class from the bean definition using the given
     * {@link ClassLoader}.
     *
     * @param classLoader can be {@literal null}.
     * @return the bean class or {@literal null} if it can't be loaded.
     */
    @Nullable
    private Class<?> loadBeanClass(@Nullable ClassLoader classLoader) {

        String beanClassName = definition.getBeanClassName();

        try {
            return org.springframework.util.ClassUtils.forName(beanClassName, classLoader);
        } catch (ClassNotFoundException | LinkageError e) {
            logger.warn(CLASS_LOADING_ERROR, beanClassName, classLoader, e);
        }

        return null;
    }

    public String getResourceDescription() {
        return String.format("%s bean", getBeanClass());
    }
}