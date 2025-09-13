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
package com.gluonhq.jfxapps.boot.aop.extension;

import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;

/**
 * This class is the default configuration for an extension context. It is used
 * to configure the extension context with the necessary beans to support aop
 */
@Configuration
public class AopExtensionContextConfig {

    private final JfxAppContext context;

    public AopExtensionContextConfig(JfxAppContext context) {
        super();
        this.context = context;
    }


    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    static AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator(ConfigurableListableBeanFactory beanFactory) {
        final var processor = new MyAnnotationAwareAspectJAutoProxyCreator();
        processor.setProxyTargetClass(true);
        processor.setBeanClassLoader(beanFactory.getBeanClassLoader());
        return processor;

    }


    /**
     * This class is a custom implementation of the AnnotationAwareAspectJAutoProxyCreator
     * that sets the class loader to be used to create the proxy.
     * This is necessary to avoid class loader issues when creating the proxy.
     * This allows the proxy to be created with the class loader of the bean class as the bean
     * class may be loaded by a different class loader than the extension context class loader.
     * (e.g. when the bean class is loaded by another extension)
     *
     * @deprecated This class is deprecated and will be removed in a future release. JfxAppContext implementation
     * should register the classloader of each registered class into his own composite classloader.
     * The composite classloader is the default classloader provided to the proxy creator
     * So this class seems not needed anymore
     */
    @Deprecated
    public static class MyAnnotationAwareAspectJAutoProxyCreator extends AnnotationAwareAspectJAutoProxyCreator {

        private static final long serialVersionUID = 1L;

        @Override
        protected Object createProxy(Class<?> beanClass, @Nullable String beanName,
                @Nullable Object[] specificInterceptors, TargetSource targetSource) {
            this.setProxyClassLoader(beanClass.getClassLoader());
            return super.createProxy(beanClass, beanName, specificInterceptors, targetSource);
        }

    }
}
