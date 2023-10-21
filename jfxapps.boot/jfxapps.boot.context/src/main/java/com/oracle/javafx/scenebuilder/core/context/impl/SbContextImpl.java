/*
 * Copyright (c) 2016, 2023, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2023, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.core.context.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.ResolvableType;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import com.oracle.javafx.scenebuilder.core.context.DocumentScope;
import com.oracle.javafx.scenebuilder.core.context.MultipleProgressListener;
import com.oracle.javafx.scenebuilder.core.context.SbContext;
import com.oracle.javafx.scenebuilder.core.context.internal.ContextProgressHandler;
import com.oracle.javafx.scenebuilder.core.context.internal.SbBeanFactoryImpl;

public class SbContextImpl implements SbContext  {

    private SbContext parent;

    private final AnnotationConfigApplicationContext context;
    private final SbBeanFactoryImpl beanFactory;
    private final UUID id;

    private Class<?>[] registeredClasses;

    public SbContextImpl(UUID id) {
        this.id = id;
        this.beanFactory = new SbBeanFactoryImpl();
        this.context = new AnnotationConfigApplicationContext(this.beanFactory);

        registerSingleton(this);
    }

    public void setParent(SbContextImpl parent) {
        this.parent = parent;
        context.setParent(parent.context);
    }


    public void registerSingleton(Object singletonObject) {
        // Register the singleton instance with a generated name
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(singletonObject.getClass());
        String name = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, beanFactory);
        this.beanFactory.registerSingleton(name, singletonObject);
    }

    @Override
    public <T> void registerBean(Class<T> beanClass, Supplier<T> tSupplier) {
        this.context.registerBean(beanClass, tSupplier);
    }
    @Override
    public void addProgressListener(MultipleProgressListener progressListener) {
        ContextProgressHandler progressHandler = new ContextProgressHandler(id, progressListener);
        context.addApplicationListener(progressHandler);
        context.addBeanFactoryPostProcessor(progressHandler);
    }

    @Override
    public void register(Class<?>[] classes) {
        registeredClasses = classes;
        context.register(classes);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }

    @Override
    public void refresh() {
        context.refresh();
    }

    @Override
    public void start() {
        context.start();
    }

    @Override
    public int getBeanDefinitionCount() {
        return context.getBeanDefinitionCount();
    }

    @Override
    public boolean isRunning() {
        return context.isRunning();
    }

    @Override
    public boolean isActive() {
        return context.isActive();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void close() {
        context.close();
    }

    @Override
    public <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        return (T)context.getBean(beanName);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> cls, Class<?> genericClass) {
        ResolvableType resolvable = ResolvableType.forClassWithGenerics(cls, genericClass);
        return context.getBeanNamesForType(resolvable);
    }


    @Override
    public SbContext getParent() {
        return parent;
    }


    @Override
    public Object parseExpression(String spelExpression, Object rootContext) {
        try {
            StandardEvaluationContext  stContext  = new StandardEvaluationContext(rootContext);
            SpelExpressionParser parser = new SpelExpressionParser();
            return parser.parseRaw(spelExpression).getValue(stContext);
        } catch (EvaluationException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean isExpression(String spelExpression) {
        return spelExpression != null ? spelExpression.startsWith("#") : false;
    }

    @Override
    public boolean isDocumentScope(Class<?> cls) {
        String[] names = context.getBeanNamesForType(cls);

        if (names.length == 0) {
            return false;
        }
        BeanDefinition definition = context.getBeanDefinition(names[0]);
        return DocumentScope.SCOPE_NAME.equals(definition.getScope());
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.context.publishEvent(event);
    }

    @Override
    public List<Class<?>> getBeanClassesForAnnotation(Class<? extends Annotation> annotationType) {
        return Arrays.stream(context.getBeanNamesForAnnotation(annotationType))
            .map(context::getType)
            .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<Class<T>> getBeanClassesForType(Class<T> cls) {
        return Arrays.stream(context.getBeanNamesForType(cls))
            .map(n -> (Class<T>)context.getType(n))
            .collect(Collectors.toList());
    }

    @Override
    public Object getBean(String string, Object... parameters) {
        return context.getBean(string, parameters);
    }

    @Override
    public <T> T getBean(Class<T> cls, Object... parameters) {
        return context.getBean(cls, parameters);
    }

    @Override
    public Class<?>[] getRegisteredClasses() {
        return registeredClasses;
    }


}
