/*
 * Copyright (c) 2016, 2021, Gluon and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.launcher.app;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.target.LazyInitTargetSource;
import org.springframework.context.annotation.Bean;

import com.oracle.javafx.scenebuilder.api.util.SceneBuilderLoadingProgress;
import com.oracle.javafx.scenebuilder.launcher.splash.SplashScreenPreloader;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

public class SceneBuilderBootstrap extends JavafxApplication {

    private static final CountDownLatch launchLatch = new CountDownLatch(1);
    
    public static void start(String[] args) {
        System.setProperty("javafx.preloader", SplashScreenPreloader.class.getName()); //NOCHECK
        SceneBuilderLoadingProgress.get().start();
        Application.launch(SceneBuilderBootstrap.class, args);
    }
    
    @Override
    public List<Class<?>> explicitClassToRegister() {
        return Arrays.asList(
//                I18NApp.class,
//                DocumentPreferencesController.class,
//                DocumentPreferencesNodeImpl.class,
//                PreferencesController.class,
//                RootPreferencesNodeImpl.class,
//                WindowIconSetting.class
                
                );
    }

    public SceneBuilderBootstrap() {
        // set design time flag
        java.beans.Beans.setDesignTime(true);

        /*
         * We spawn our two threads for handling background startup.
         */
        final Runnable p0 = () -> backgroundStartPhase0();
        final Runnable p1 = () -> {
            try {
                launchLatch.await();
                backgroundStartPhase2();
            } catch (InterruptedException x) {
                // JavaFX thread has been interrupted. Simply exits.
            }
        };
        final Thread phase0 = new Thread(p0, "Phase 0"); // NOI18N
        final Thread phase1 = new Thread(p1, "Phase 1"); // NOI18N
        phase0.setDaemon(true);
        phase1.setDaemon(true);

        // Note : if you suspect a race condition bug, comment the two next
        // lines to make startup fully sequential.
        phase0.start();
        phase1.start();
    }

    /*
     * Application
     */
    @Override
    public void start(Stage stage) throws Exception {
        super.start(stage);
        launchLatch.countDown();
        setApplicationUncaughtExceptionHandler();
    }

    private void setApplicationUncaughtExceptionHandler() {
        if (Thread.getDefaultUncaughtExceptionHandler() == null) {
            // Register a Default Uncaught Exception Handler for the application
            Thread.setDefaultUncaughtExceptionHandler(new SceneBuilderUncaughtExceptionHandler());
        }
    }

    private static class SceneBuilderUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            // Print the details of the exception in SceneBuilder log file
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "An exception was thrown:", e); // NOI18N
        }
    }

    /*
     * Background startup
     *
     * To speed SB startup, we create two threads which anticipate some
     * initialization tasks and offload the JFX thread: - 'Phase 0' thread executes
     * tasks that do not require JFX initialization - 'Phase 1' thread executes
     * tasks that requires JFX initialization
     *
     * Tasks executed here must be carefully chosen: 1) they must be thread-safe 2)
     * they should be order-safe : whether they are executed in background or by the
     * JFX thread should make no difference.
     *
     * Currently we simply anticipate creation of big singleton instances (like
     * Metadata, Preferences...)
     */

    private void backgroundStartPhase0() {
        assert Platform.isFxApplicationThread() == false; // Warning

        // PreferencesController.getSingleton();
        // Api.get().getMetadata();
    }

    private void backgroundStartPhase2() {
        assert Platform.isFxApplicationThread() == false; // Warning
        assert launchLatch.getCount() == 0; // i.e JavaFX is initialized

        // BuiltinLibrary.getLibrary();
//        if (EditorPlatform.IS_MAC) {
//            MenuBarController.getSystemMenuBarController();
//        }

    }

    @Bean
    public LazyInitTargetSource firstLinkedInstanceSource() {
        LazyInitTargetSource lazyInit = new LazyInitTargetSource();
        lazyInit.setTargetBeanName("documentWindowController");
        return lazyInit;
    }

    @Bean
    public ProxyFactoryBean proxyFactoryBean() {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTargetSource(firstLinkedInstanceSource());
        return proxyFactoryBean;
    }

}
