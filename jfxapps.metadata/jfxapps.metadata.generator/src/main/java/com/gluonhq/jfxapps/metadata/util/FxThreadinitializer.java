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

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.javafx.tk.Toolkit;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * This class has a method that initializes the JavaFX thread. It is used by
 * the JavaFX Maven Plugin to start the JavaFX thread in a separate thread.
 * This is necessary because the JavaFX thread cannot be started from the
 * main thread of the Maven Plugin.
 *
 * The class also has a method that stops the JavaFX thread. This is necessary
 * because the JavaFX thread cannot be stopped from the main thread of the
 * Maven Plugin.
 *
 * This class has a big issue with the stop method. It is not able to stop all
 * the threads that are started by JavaFX. This is because JavaFX uses a
 * shutdown hook to stop the threads. This shutdown hook is not called when the
 * threads are interrupted. This is a known issue in JavaFX.
 *
 * This class has an experimental feature that tries to stop all the threads
 * that are started by JavaFX. This feature is not reliable and should be used
 * with caution.
 *
 * The main issue with this class is that it must be able to relaunch the JavaFX
 * thread if it is stopped. This is not possible because the JavaFX runtime does
 * not allow to restart the JavaFX thread mainly due to  uncleaned static contents
 * and probably other hidden things. This is a known issue in JavaFX.
 *
 * The consequence is that the JavaFX thread can only be started once. If it is
 * stopped, it cannot be restarted. This is a big limitation of this class.
 *
 */
public class FxThreadinitializer {
    private static final Logger logger = LoggerFactory.getLogger(FxThreadinitializer.class);
    public static boolean ENABLE_EXPERIMENTAL_FEATURES = false;
    private static FxThreadinitializer INSTANCE;

    private CompletableFuture<Boolean> threadInitialized;
    private final FxThreadinitializer.JfxThread thread = new JfxThread();
    private boolean stopped = false;

    public static boolean initJFX(String javafxVersion) {
        if (INSTANCE != null) {
            INSTANCE.internalStop();
        }
        INSTANCE = new FxThreadinitializer();

        try {
            return INSTANCE.initJFXInternal(javafxVersion).get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private static void notifyStarted() {
        INSTANCE.internalNotifyStarted();
    }

    private static void notifyException(Throwable t) {
        INSTANCE.internalNotifyException(t);
    }

    public static void stop() {

        if (INSTANCE != null) {
            try {
                INSTANCE.internalStop().get();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ExecutionException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    private FxThreadinitializer() {
        thread.setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
            FxThreadinitializer.notifyException(throwable);
        });
        thread.setDaemon(true);
    }

    private Future<Boolean> initJFXInternal(String javafxVersion) {
        logger.info("Initializing JavaFX thread...");
        String fxv = System.getProperty("javafx.version", "versionless");
        logger.debug("JavaFX version : {}", fxv);
        // System.setProperty("javafx.version", javafxVersion);
        System.setProperty("javafx.version", "versionless");
        fxv = System.getProperty("javafx.version", "versionless");
        logger.debug("JavaFX version property: {}", fxv);

        threadInitialized = new CompletableFuture<>();
        thread.start();

        return threadInitialized;
    }

    private void internalNotifyStarted() {
        threadInitialized.complete(true);
    }

    private void internalNotifyException(Throwable t) {
        threadInitialized.completeExceptionally(t);
    }

    private CompletableFuture<Boolean> internalStop() {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        if (!stopped) {
            stopped = true;
            thread.interrupt();

            // experimental
            if (ENABLE_EXPERIMENTAL_FEATURES) {
                internalStopRemainingThreadsExperimental(future);
            } else {
                future.complete(true);
            }

        }
        return future;
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

    private CompletableFuture<Boolean> internalStopRemainingThreadsExperimental(CompletableFuture<Boolean> future) {
//        CompletableFuture<Boolean> cf = new CompletableFuture<>();
//
//        Platform.runLater(() -> {
//            try {
//                Toolkit.getToolkit().exit();
//                cf.complete(true);
//            } catch (Exception e) {
//                cf.completeExceptionally(e);
//            }
//        });

        try {

            //cf.get(10, java.util.concurrent.TimeUnit.SECONDS);
            Class<com.sun.javafx.application.PlatformImpl> platformImpl = com.sun.javafx.application.PlatformImpl.class;
            unsetAtomicBoolean(platformImpl,"initialized");
            unsetAtomicBoolean(platformImpl,"platformExit");
            unsetAtomicBoolean(platformImpl,"toolkitExit");

            Class<com.sun.javafx.application.LauncherImpl> launcherImpl = com.sun.javafx.application.LauncherImpl.class;
            unsetAtomicBoolean(launcherImpl,"launchCalled");
            unsetAtomicBoolean(launcherImpl,"toolkitStarted");

            Class<com.sun.javafx.tk.Toolkit> toolkit = com.sun.javafx.tk.Toolkit.class;
            unset(toolkit,"TOOLKIT");

            while (com.sun.prism.GraphicsPipeline.getPipeline() != null) {
                Thread.sleep(100);
                System.out.println("x");
            }
            future.complete(true);
        } catch (Exception e) {
            e.printStackTrace();
            future.completeExceptionally(e);
        }
//        Platform.runLater(() -> {
//            Thread.currentThread().getThreadGroup().interrupt();
//        });
//        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
//        ThreadGroup parentGroup;
//        while ((parentGroup = rootGroup.getParent()) != null) {
//            rootGroup = parentGroup;
//        }
//
//        Thread[] threads = new Thread[rootGroup.activeCount()];
//        while (rootGroup.enumerate(threads, true) == threads.length) {
//            threads = new Thread[threads.length * 2];
//        }
//
//        List<WeakReference<Thread>> weakThreads = new ArrayList<>();
//        Arrays.stream(threads).forEach(t -> weakThreads.add(new WeakReference<>(t)));
//        threads = null;
//
//        for (var wt : weakThreads) {
//            try {
//                System.out.println("Existing thread " + (wt.get() != null ? wt.get().getName() : "unreferenced"));
//                if (javafxThreads.contains((wt.get() != null ? wt.get().getName() : "unreferenced"))) {
//
//                    System.out.println("Interrupting thread " + (wt.get() != null ? wt.get().getName() : "unreferenced"));
//
//                    if (wt.get() != null) {
//                        wt.get().interrupt();
//                    }
//
//                }
//            } catch (Exception e) {
//                System.out.println("Interrupting thread failed, " + e.getMessage());
//            }
//        }

        return future;
    }

    private void unsetAtomicBoolean(Class<?> platformImpl, String fieldName) {
        try {
            var field = platformImpl.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object initializedObject = field.get(null);
            if (initializedObject instanceof AtomicBoolean ab) {
                ab.set(false);
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void unset(Class<?> platformImpl, String fieldName) {
        try {
            var field = platformImpl.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, null);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
            try {
                Application.launch(FxThreadinitializer.DummyApp.class, new String[0]);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        @Override
        public void interrupt() {
            CompletableFuture<Boolean> cf = new CompletableFuture<>();
            Platform.exit();
            Toolkit.getToolkit().addShutdownHook(() -> {
                logger.info("Shutdown hook executed");
                cf.complete(true);
            });
            //super.interrupt();
        }
    }
}