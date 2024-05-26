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
package com.gluonhq.jfxapps.metadata.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class FxThreadinitializer {
    private static final Logger logger = LoggerFactory.getLogger(FxThreadinitializer.class);
    private static boolean ENABLE_EXPERIMENTAL_FEATURES = false;
    private static FxThreadinitializer INSTANCE;

    private final CompletableFuture<Boolean> threadInitialized = new CompletableFuture<>();
    private final FxThreadinitializer.JfxThread thread = new JfxThread();
    private boolean stopped = false;

    public static Future<Boolean> initJFX(String javafxVersion) {
        if (INSTANCE != null) {
            INSTANCE.internalStop();
        }
        INSTANCE = new FxThreadinitializer();
        return INSTANCE.initJFXInternal(javafxVersion);
    }

    private static void notifyStarted() {
        INSTANCE.internalNotifyStarted();
    }

    private static void notifyException(Throwable t) {
        INSTANCE.internalNotifyException(t);
    }

    public static void stop() {
        INSTANCE.internalStop();
    }

    private FxThreadinitializer() {
        thread.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            FxThreadinitializer.notifyException(throwable);
        });
        thread.setDaemon(true);
    }

    private Future<Boolean> initJFXInternal(String javafxVersion) {
        logger.info("Initializing JavaFX thread");
        String fxv = System.getProperty("javafx.version", "versionless");
        logger.debug("JavaFX version : {}", fxv);
        // System.setProperty("javafx.version", javafxVersion);
        System.setProperty("javafx.version", "versionless");
        fxv = System.getProperty("javafx.version", "versionless");
        logger.debug("JavaFX version property: {}", fxv);

        thread.start();

        return threadInitialized;
    }

    private void internalNotifyStarted() {
        threadInitialized.complete(true);
    }

    private void internalNotifyException(Throwable t) {
        threadInitialized.completeExceptionally(t);
    }

    private void internalStop() {
        if (!stopped) {
            stopped = true;
            thread.interrupt();

            // experimental
            if (ENABLE_EXPERIMENTAL_FEATURES) {
                internalStopRemainingThreadsExperimental();
            }

        }
    }

    private static List<String> javafxThreads = List.of(
            "JavaFX Init Thread",
            "JavaFX-Launcher",
            "JavaFX Application Thread",
            "InvokeLaterDispatcher",
            "Thread-2",
            "ScheduledService Delay Timer",
            "Prism Font Disposer",
            "XXXXXXXXXXXXXXXXXXXXXX");

    private void internalStopRemainingThreadsExperimental() {
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parentGroup;
        while ((parentGroup = rootGroup.getParent()) != null) {
            rootGroup = parentGroup;
        }

        Thread[] threads = new Thread[rootGroup.activeCount()];
        while (rootGroup.enumerate(threads, true) == threads.length) {
            threads = new Thread[threads.length * 2];
        }

        List<WeakReference<Thread>> weakThreads = new ArrayList<>();
        Arrays.stream(threads).forEach(t -> weakThreads.add(new WeakReference<>(t)));
        threads = null;

        for (var wt : weakThreads) {
            try {
                System.out.println("Existing thread " + (wt.get() != null ? wt.get().getName() : "unreferenced"));
                if (javafxThreads.contains((wt.get() != null ? wt.get().getName() : "unreferenced"))) {

                    System.out.println("Interrupting thread " + (wt.get() != null ? wt.get().getName() : "unreferenced"));

                    if (wt.get() != null) {
                        wt.get().interrupt();
                    }

                }
            } catch (Exception e) {
                System.out.println("Interrupting thread failed, " + e.getMessage());
            }
        }
    }

    public static class DummyApp extends Application {

        @Override
        public void start(Stage primaryStage) throws Exception {
            FxThreadinitializer.notifyStarted();
        }
    }

    protected static class JfxThread extends Thread {

        public JfxThread() {
            super("JavaFX Init Thread");
        }

        @Override
        public void run() {
            Application.launch(FxThreadinitializer.DummyApp.class, new String[0]);
        }

        @Override
        public void interrupt() {
            Platform.exit();
            super.interrupt();
        }

    }
}