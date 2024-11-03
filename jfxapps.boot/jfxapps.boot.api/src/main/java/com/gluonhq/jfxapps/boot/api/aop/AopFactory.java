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
package com.gluonhq.jfxapps.boot.api.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.boot.api.aop.internal.DefaultMethodInterceptor;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;

public class AopFactory implements BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(AopFactory.class);

    private AopContext aopContext;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private AopMetadata beanMetadata;

    private ApplicationContext context;

    /**
     * Creates a new {@link AopFactory}.
     */
    public AopFactory(AopContext aopContext) {
        this.aopContext = aopContext;
        this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader == null ? org.springframework.util.ClassUtils.getDefaultClassLoader()
                : classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Returns a proxy instance for the given interface backed by an instance
     * providing implementation logic
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getProxy(Class<T> beanClass) {

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing proxy target instance for {}", beanClass.getName());
        }

        Assert.notNull(beanClass, "Bean class must not be null");

        var target = aopContext.createTarget((JfxAppContext)context, beanMetadata);

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(beanClass);

        // TODO: check if this realy needed
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);

        AopFactory.class.getModule().addReads(beanClass.getModule());

        result.addAdvice(new DefaultMethodInterceptor());
        result.addAdvice(new ImplementationInterceptor(target, beanClass));

        T proxy = (T) result.getProxy(beanClass.getClassLoader());

        if (logger.isDebugEnabled()) {
            logger.debug("Finished creation of proxy target instance for {}.", beanClass.getName());
        }

        return proxy;
    }

    /**
     * Method interceptor that calls methods on the target object.
     */
    static class ImplementationInterceptor implements MethodInterceptor {

        private final Object base;
        private Class<?> beanClass;

        public ImplementationInterceptor(Object base, Class<?> beanClass) {
            this.base = base;
            this.beanClass = beanClass;
        }

        @Nullable
        @Override
        public Object invoke(@SuppressWarnings("null") MethodInvocation invocation) throws Throwable {

            Method method = invocation.getMethod();
            Object[] arguments = invocation.getArguments();

            try {
                return method.invoke(base, arguments);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    throw ((InvocationTargetException) e).getTargetException();
                }
                throw e;
            }
        }
    }

    public void setBeanMetadata(AopMetadata beanMetadata) {
        this.beanMetadata = beanMetadata;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
