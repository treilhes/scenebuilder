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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import org.springframework.context.ApplicationEvent;

import com.gluonhq.jfxapps.boot.context.impl.SbContextImpl;

public interface SbContext {

    public static SbContext fromScratch(Class<?>[] array) {
        SbContextImpl ctx = new SbContextImpl(UUID.randomUUID());
        ctx.register(array);
        ctx.refresh();
        ctx.start();
        return ctx;
    }

    String[] getBeanDefinitionNames();

    void addProgressListener(MultipleProgressListener progressListener);

    void register(Class<?>[] array);

    void refresh();

    void start();

    int getBeanDefinitionCount();

    boolean isRunning();

    boolean isActive();

    UUID getId();

    <T> T getBean(Class<T> cls);
    <T> T getBean(String b);
    String[] getBeanNamesForType(Class<?> cls, Class<?> genericClass);

    SbContext getParent();

    boolean isExpression(String text);

    Object parseExpression(String text, Object rootContext);

    boolean isDocumentScope(Class<?> cls);

    void publishEvent(ApplicationEvent event);

    <T> void registerBean(Class<T> class1, Supplier<T> tSupplier);

    List<Class<?>> getBeanClassesForAnnotation(Class<? extends Annotation> annotationType);

    <T> List<Class<T>> getBeanClassesForType(Class<T> cls);

    Object getBean(String string, Object... parameters);

    <T> T getBean(Class<T> cls, Object... parameters);

    Class<?>[] getRegisteredClasses();

    <T> Map<String, T> getBeansOfType(Class<T> cls);





}
