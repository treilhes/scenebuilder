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
package com.gluonhq.jfxapps.core.api.application;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.BeanClassLoaderAware;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;

/**
 * Composite class loader dedicated to a specific application that delegates to a list of class loaders.
 *
 * This class loader is used to load classes that are not available in the JavaFX thread class loader.
 * The javafx thread is loaded with the root api class loader so it didn't know of any extension
 *
 * The {@link ContextClassLoaderEventDispatcher} is used to set the class loader of the extension into
 * Thread.currentThread().getContextClassLoader() so that the JavaFX thread can load the classes
 * and resources of the extension during event processing.
 *
 * The {@link ApplicationClassloader} is also set into Thread.currentThread().getContextClassLoader()
 * according to the current ApplicationScope so that the JavaFX thread can load the classes and resources
 * of the extension during class to {@link JfxAppPlatform} runWithScope/callWithScope methods invocation
 */
@ApplicationSingleton
public class ApplicationClassloader extends ClassLoader implements BeanClassLoaderAware {

    private final Map<String, ClassLoader> classLoaders = new HashMap<>();
    private final ApplicationEvents applicationEvents;

    ApplicationClassloader(ApplicationEvents applicationEvents) {
        this.applicationEvents = applicationEvents;
    }

    public void putClassLoader(String key, ClassLoader classLoader) {
        if (classLoader != null && !classLoaders.containsValue(classLoader) && classLoader != this) {
            classLoaders.put(key, classLoader);
            applicationEvents.classloader().set(this);
        }
    }

    public void removeClassLoader(String key) {
        var removed = classLoaders.remove(key);
        if (removed != null) {
            applicationEvents.classloader().set(this);
        }
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders.values()) {
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore and try the next class loader
            }
        }
        throw new ClassNotFoundException("Class " + name + " not found in any class loader");
    }

    @Override
    protected URL findResource(String name) {
        for (ClassLoader classLoader : classLoaders.values()) {
            var resource = classLoader.getResource(name);
            if (resource != null) {
                return resource;
            }
        }
        return null;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        classLoaders.put(ApplicationClassloader.class.getName(), classLoader);
    }


}