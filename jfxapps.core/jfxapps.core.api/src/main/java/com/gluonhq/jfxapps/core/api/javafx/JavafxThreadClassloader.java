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
package com.gluonhq.jfxapps.core.api.javafx;

import java.util.ArrayList;
import java.util.List;

import com.gluonhq.jfxapps.boot.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.core.api.javafx.internal.ContextClassLoaderEventDispatcher;
import com.gluonhq.jfxapps.core.api.ui.MainInstanceWindow;

/**
 * Composite class loader dedicated to the JavaFX thread for a specific application instance
 * that delegates to a list of class loaders.
 *
 * This class loader is used to load classes that are not available in the JavaFX thread class loader.
 * The javafx thread is loaded with the root api class loader so it didn't know of any extension
 *
 * The {@link ContextClassLoaderEventDispatcher} is used to set the class loader of the extension into
 * Thread.currentThread().getContextClassLoader() so that the JavaFX thread can load the classes
 * and resources of the extension during event processing.
 *
 * The {@link JavafxThreadClassloader} is also set into Thread.currentThread().getContextClassLoader()
 * according to the current ApplicationScope so that the JavaFX thread can load the classes and resources
 * of the extension during class to {@link JfxAppPlatform} runWithScope/callWithScope methods invocation
 */
@ApplicationInstanceSingleton
public class JavafxThreadClassloader extends ClassLoader {

    private final List<ClassLoader> classLoaders = new ArrayList<>();

    JavafxThreadClassloader() {
    }

    public void addClassLoader(ClassLoader classLoader) {
        classLoaders.add(classLoader);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        for (ClassLoader classLoader : classLoaders) {
            try {
                return classLoader.loadClass(name);
            } catch (ClassNotFoundException e) {
                // Ignore and try the next class loader
            }
        }
        throw new ClassNotFoundException("Class " + name + " not found in any class loader");
    }
}