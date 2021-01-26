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
package com.oracle.javafx.scenebuilder.app;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.oracle.javafx.scenebuilder.extension.Extension;
import com.oracle.javafx.scenebuilder.extension.ExtensionLoader;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Below class is based on Josh Long tutorial "Spring with Javafx" video:
 * https://spring.io/blog/2019/01/16/spring-tips-javafx source:
 * https://github.com/spring-tips/javafx
 * 
 * @author ptreilhes
 *
 */
public class JavafxApplication extends Application {
    
    private static Logger logger = LoggerFactory.getLogger(JavafxApplication.class);

    private ConfigurableApplicationContext context;

    @Override
    public void init() throws Exception {
        
        ApplicationContextInitializer<GenericApplicationContext> initializer = new ApplicationContextInitializer<GenericApplicationContext>() {
            @Override
            public void initialize(GenericApplicationContext genericApplicationContext) {
                
                genericApplicationContext.registerBean(Application.class, () -> JavafxApplication.this);
                genericApplicationContext.registerBean(Parameters.class, () -> getParameters());
                genericApplicationContext.registerBean(HostServices.class, () -> getHostServices());
                
            }
        };

        ExtensionLoader loader = new ExtensionLoader();
        Map<UUID, Extension> extensions = loader.loadExtensions();
        
        extensions.entrySet().forEach(e -> logger.info("Loading extension {} id: {}", e.getValue().getClass().getSimpleName(), e.getKey().toString()));

        List<Class<?>> sources = loader.loadExtensions().values().stream().map(c -> c.getClass()).collect(Collectors.toList());
        sources.add(0, this.getClass());
//        for (Extension ext:exts) {
//            if (!ext.components().isEmpty()) {
//                clsss.addAll(ext.components());
//          }
//        }
        System.out.println();
        this.context = new SpringApplicationBuilder()
                .sources(sources.toArray(new Class<?>[0])).initializers(initializer)
                .build().run(getParameters().getRaw().toArray(new String[0]));
        
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // we can't use injection here so publish an event
        this.context.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() throws Exception {
        this.context.close();
        Platform.exit();
    }

    public class StageReadyEvent extends ApplicationEvent {

        /**
         * 
         */
        private static final long serialVersionUID = 1L;

        public Stage getStage() {
            return Stage.class.cast(getSource());
        }

        public StageReadyEvent(Object source) {
            super(source);
        }
    }
}
