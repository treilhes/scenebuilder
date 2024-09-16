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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Singleton;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadHolder;

import jakarta.annotation.PostConstruct;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.collections.ListChangeListener.Change;
import javafx.stage.Stage;
import javafx.stage.Window;

@Singleton
public class JavafxThreadBootstrapper implements ApplicationListener<StageReadyEvent>, JavafxThreadHolder {

    private final static Logger logger = LoggerFactory.getLogger(JavafxThreadBootstrapper.class);

    private static JfxAppContext context;
    private final JavafxThreadClassloaderDispatcherImpl fxThreadClassloaderDispatcher;
    private final ContextClassLoaderEventDispatcher eventDispatcher;

    private static Application javafxApplication;



    private boolean started;

    private Runnable whenStarted;

    private Stage primaryStage;



    // @formatter:off
    public JavafxThreadBootstrapper(
            JavafxThreadClassloaderDispatcherImpl fxThreadClassloaderDispatcher,
            ContextClassLoaderEventDispatcher eventDispatcher,
            JfxAppContext context) {
     // @formatter:on
        this.fxThreadClassloaderDispatcher = fxThreadClassloaderDispatcher;
        this.eventDispatcher = eventDispatcher;

        JavafxThreadBootstrapper.context = context;
    }

    @PostConstruct
    protected void javafxThreadLaunch() {
        new Thread(() -> Application.launch(JavafxApplication.class, new String[0])).start();
    }

    @Override
    public void onApplicationEvent(@NonNull StageReadyEvent stageReadyEvent) {
        started = stageReadyEvent.getStage() != null;
        primaryStage = stageReadyEvent.getStage();
        eventDispatcher.setup(primaryStage.getEventDispatcher());

        Window.getWindows().addListener((Change<? extends Window> c) -> {
            while (c.next()) {
                c.getAddedSubList().forEach( w -> {
                    w.setEventDispatcher(eventDispatcher);
                    fxThreadClassloaderDispatcher.listenFocus(w);
                });

                //c.getRemoved().forEach(fxThreadClassloaderDispatcher::unregister);
            }
        });

        logger.info("Javafx primary stage set !");

        if (this.whenStarted != null) {
            this.whenStarted.run();
        }
    }

    @Override
    public void stop() {
        try {
            javafxApplication.stop();
            started = false;
        } catch (Exception e) {
            logger.error("Error while stoping javafx runtime application", e);
        }
    }

    @Override
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public boolean hasStarted() {
        return started;
    }

    @Override
    public void whenStarted(Runnable runnable) {
        this.whenStarted = runnable;

        if (hasStarted()) {
            this.whenStarted.run();
        }
    }

    public static class JavafxApplication extends Application {

        private static Logger logger = LoggerFactory.getLogger(JavafxApplication.class);

        @Override
        public void init() throws Exception {
            javafxApplication = this;
        }

        @Override
        public void start(Stage primaryStage) throws Exception {
            context.registerBean(Application.class, () -> JavafxApplication.this);
            context.registerBean(Parameters.class, () -> getParameters());
            context.registerBean(HostServices.class, () -> getHostServices());

            logger.info("Underlying javafx application started !");

            // we can't use injection here so publish an event
            context.publishEvent(new StageReadyEvent(primaryStage));

        }

    }

}
