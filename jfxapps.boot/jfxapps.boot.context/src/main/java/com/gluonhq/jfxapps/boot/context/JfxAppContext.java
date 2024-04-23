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
package com.gluonhq.jfxapps.boot.context;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationInstanceScopeHolder;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationScopeHolder;

public interface JfxAppContext extends ConfigurableWebApplicationContext {

    /** The scope holder */
    public static final ApplicationScopeHolder applicationScope = new ApplicationScopeHolder();

    /** The scope holder */
    public static final ApplicationInstanceScopeHolder applicationInstanceScope = new ApplicationInstanceScopeHolder(applicationScope);


    public static JfxAppContext fromScratch(Class<?>... array) {
        JfxAppContextImpl ctx = new JfxAppContextImpl(UUID.randomUUID());
        ctx.register(array);
        ctx.refresh();
        ctx.start();
        return ctx;
    }

    void addProgressListener(MultipleProgressListener progressListener);

    String[] getBeanNamesForType(Class<?> cls, Class<?> genericClass);

    UUID getUuid();

    boolean isExpression(String text);

    Object parseExpression(String text, Object rootContext);

    boolean isApplicationScope(Class<?> cls);

    boolean isApplicationInstanceScope(Class<?> cls);

    //<T> void registerBean(Class<T> class1, Supplier<T> tSupplier);

    List<Class<?>> getBeanClassesForAnnotation(Class<? extends Annotation> annotationType);

    <T> List<Class<T>> getBeanClassesForType(Class<T> cls);

    Set<Class<?>> getRegisteredClasses();

    Set<Class<?>> getDeportedClasses();

    <T, G, U> Map<String, U> getBeansOfTypeWithGeneric(Class<T> cls, Class<G> generic);

    ClassLoader getBeanClassLoader();

    <T> T getLocalBean(Class<T> cls);

    void removeAlias(String alias);

    void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

    void destroyBean(Object existingBean);

    void destroyScopedBean(String beanName);

    <T> void registerBean(Class<T> class1, Supplier<T> object);

}