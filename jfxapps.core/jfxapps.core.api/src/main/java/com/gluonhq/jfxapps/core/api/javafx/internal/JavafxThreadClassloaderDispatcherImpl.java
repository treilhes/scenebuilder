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
package com.gluonhq.jfxapps.core.api.javafx.internal;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gluonhq.jfxapps.boot.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloaderDispatcher;

import javafx.application.Platform;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * This class is responsible of switching the context class loader according
 * to focused window<br/>
 * To do so it maintains a map of windows to class loaders and need from jfxApp
 * windows to register themselves on creation<br/>
 */
@Singleton
public class JavafxThreadClassloaderDispatcherImpl implements JavafxThreadClassloaderDispatcher {

    private static final Logger log = LoggerFactory.getLogger(JavafxThreadClassloaderDispatcherImpl.class);

    private final Map<Window, JavafxThreadClassloader> windowToClassloader = new ConcurrentHashMap<>();

    private Map<Predicate<Window>[], JavafxThreadClassloader> predicatesToWindow = new ConcurrentHashMap<>();

    JavafxThreadClassloaderDispatcherImpl() {

    }

    protected void listenFocus(Window window) {
        if (window instanceof Stage) {
            window.focusedProperty().addListener((obs, ov, nv) -> {
                if (nv) {
                    tryCheckAndRegister(window);
                    setupThreadClassloader(window);
                }
            });
        }
    }

    @Override
    public void register(Window window, JavafxThreadClassloader classloader) {
        log.info("Registering window {} with classloader {}", window, classloader);
        windowToClassloader.put(window, classloader);
    }

    public void unregister(Window window) {
        log.info("Unregistering window {}", window);
        windowToClassloader.remove(window);
    }

    public void setupThreadClassloader(Window window) {
        assert Platform.isFxApplicationThread();

        try {
            if (windowToClassloader.containsKey(window)) {
                var loader = windowToClassloader.get(window);
                log.info("Setting context class loader for window {} to {}", window, loader);
                Thread.currentThread().setContextClassLoader(loader);
            } else {
                throw new IllegalArgumentException("Window not registered: " + window);
            }
        } catch (RuntimeException e) {
            throw e;
        }
    }

    public <T> T callWith(Window window, Callable<T> callable) throws Exception {
        assert Platform.isFxApplicationThread();

        var backup = Thread.currentThread().getContextClassLoader();

        try {
            if (windowToClassloader.containsKey(window)) {
                var loader = windowToClassloader.get(window);
                if (loader != Thread.currentThread().getContextClassLoader()) {
                    log.info("Setting context class loader for window {} to {}", window, loader);
                    Thread.currentThread().setContextClassLoader(loader);
                }
            } else {
                throw new IllegalArgumentException("Window not registered: " + window);
            }
            return callable.call();
        } catch (RuntimeException e) {
            throw e;
        } finally {
            Thread.currentThread().setContextClassLoader(backup);
        }

    }

    @Override
    public void registerWithNextWindow(JavafxThreadClassloader classloader, Predicate<Window>... predicates) {
        if (classloader == null)
            throw new IllegalArgumentException("classloader cannot be null");
        predicatesToWindow.put(predicates, classloader);
    }


    private void tryCheckAndRegister(Window window) {
        if (!predicatesToWindow.isEmpty() && !windowToClassloader.containsKey(window)) {
            predicatesToWindow.forEach((predicates, classloader) -> {
                boolean match = Arrays.stream(predicates).allMatch(p -> p.test(window));
                if (match && predicatesToWindow.remove(predicates, classloader)) {
                    register(window, classloader);
                }
            });
        }
    }

}
