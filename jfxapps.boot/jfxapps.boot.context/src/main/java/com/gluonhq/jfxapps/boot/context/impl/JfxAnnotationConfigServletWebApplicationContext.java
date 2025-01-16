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
package com.gluonhq.jfxapps.boot.context.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.server.WebServer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigRegistry;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ScopeMetadataResolver;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import com.gluonhq.jfxapps.boot.api.utils.CompositeClassloader;

public class JfxAnnotationConfigServletWebApplicationContext extends ServletWebServerApplicationContext
        implements AnnotationConfigRegistry {

    private final CompositeClassloader compositeClassloader = new CompositeClassloader();

    private final JfxAnnotatedBeanDefinitionReader reader;

    private final JfxClassPathBeanDefinitionScanner scanner;

    private final Set<Class<?>> annotatedClasses = new LinkedHashSet<>();

    private final Map<String, Class<?>> registeredClasses = new HashMap<>();

    private boolean serverEnabled = false;

    private WebServer parentWebServer;

    @Deprecated
    private String[] basePackages;

    public JfxAnnotationConfigServletWebApplicationContext(DefaultListableBeanFactory beanFactory, WebApplicationType webApplicationType) {
        super(beanFactory);
        super.setClassLoader(compositeClassloader);

        this.reader = new JfxAnnotatedBeanDefinitionReader(this);
        this.scanner = new JfxClassPathBeanDefinitionScanner(this);

        if (webApplicationType == WebApplicationType.NONE) {
            this.serverEnabled = false;
        } else if (webApplicationType == WebApplicationType.SERVLET) {
            this.serverEnabled = true;
        } else {
            throw new IllegalArgumentException("Unsupported WebApplicationType " + webApplicationType);
        }
    }

    @Override
    public void setEnvironment(ConfigurableEnvironment environment) {
        super.setEnvironment(environment);
        this.reader.setEnvironment(environment);
        this.scanner.setEnvironment(environment);
    }

    public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
        this.reader.setBeanNameGenerator(beanNameGenerator);
        this.scanner.setBeanNameGenerator(beanNameGenerator);
        getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR,
                beanNameGenerator);
    }

    public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
        this.reader.setScopeMetadataResolver(scopeMetadataResolver);
        this.scanner.setScopeMetadataResolver(scopeMetadataResolver);
    }

    @Override
    public final void scan(String... basePackages) {
        Assert.notEmpty(basePackages, "At least one base package must be specified");
        this.basePackages = basePackages;
    }

    @Override
    protected void prepareRefresh() {
        this.scanner.clearCache();
        super.prepareRefresh();
    }

    @Override
    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
        super.postProcessBeanFactory(beanFactory);
        if (!ObjectUtils.isEmpty(this.basePackages)) {
            this.scanner.scan(this.basePackages);
        }
        if (!this.annotatedClasses.isEmpty()) {
            this.reader.register(ClassUtils.toClassArray(this.annotatedClasses));
        }
    }


    @Override
    public void register(Class<?>... annotatedClasses) {
        Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");

        // keep track of provided classes
        for (Class<?> cls : annotatedClasses) {
            registerClass(cls);
        }
        this.annotatedClasses.addAll(Arrays.asList(annotatedClasses));
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
            throws BeanDefinitionStoreException {

        // Register the class if it is an AbstractBeanDefinition
        // registeredClasses is used to keep track of the classes registered
        // but it can be null during the initialization and the registration of infrastructure beans
        if (registeredClasses != null && beanDefinition instanceof AbstractBeanDefinition abd) {
            registerClass(abd.getBeanClass());
        }

        super.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public <T> void registerBean(String beanName, Class<T> beanClass, Supplier<T> supplier,
            BeanDefinitionCustomizer... customizers) {
        this.reader.registerBean(beanClass, beanName, supplier, customizers);
    }

    public Set<Class<?>> getRegisteredClasses() {
        return new HashSet<>(registeredClasses.values());
    }

    public Class<?> getRegisteredClass(String name) {
        return registeredClasses.get(name);
    }

    private void registerClass(Class<?> cls) {
        compositeClassloader.addClassLoader(cls.getClassLoader());
        registeredClasses.put(cls.getName(), cls);
    }

    @Override
    public void setClassLoader(@Nullable ClassLoader classLoader) {
        if (classLoader != null) {
            compositeClassloader.addClassLoader(classLoader);
        }
    }

    public WebServer getParentWebServer() {
        return parentWebServer;
    }

    protected void setParentWebServer(WebServer webServer) {
        this.parentWebServer = webServer;
    }

    @Override
    protected void onRefresh() {
        if (getServletContext() == null && getParentWebServer() == null && serverEnabled) {
            // create a new WebServer only if we are not in a ServletContext
            // and we do not have a parent WebServer
            super.onRefresh();
        }

    }

    static class JfxClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

        public JfxClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
            super(registry);
        }

        @Override
        protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
            // TODO Auto-generated method stub
            return super.checkCandidate(beanName, beanDefinition);
        }

        @Override
        protected boolean isCompatible(BeanDefinition newDef, BeanDefinition existingDef) {
            // TODO Auto-generated method stub
            return super.isCompatible(newDef, existingDef);
        }

    }

    static class JfxAnnotatedBeanDefinitionReader extends AnnotatedBeanDefinitionReader {

        public JfxAnnotatedBeanDefinitionReader(BeanDefinitionRegistry registry) {
            super(registry);
        }

    }
}