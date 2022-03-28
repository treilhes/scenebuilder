/*
 * Copyright (c) 2016, 2022, Gluon and/or its affiliates.
 * Copyright (c) 2021, 2022, Pascal Treilhes and/or its affiliates.
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
package com.oracle.javafx.scenebuilder.test;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;

import com.oracle.javafx.scenebuilder.extension.ExtensionLoader;

/**
 * @author ptreilhes
 *
 */
public class TestContext {
    public static ApplicationContext get() {

//        ApplicationContextInitializer<GenericApplicationContext> initializer = new ApplicationContextInitializer<GenericApplicationContext>() {
//            @Override
//            public void initialize(GenericApplicationContext genericApplicationContext) {
//                SceneBuilderLoadingProgress.get().initContext();
//                genericApplicationContext.addApplicationListener(SceneBuilderLoadingProgress.get().getProgressListener());
//                genericApplicationContext.addBeanFactoryPostProcessor(SceneBuilderLoadingProgress.get().getProgressListener());
//                genericApplicationContext.registerBean(Application.class, () -> JavafxApplication.this);
//                genericApplicationContext.registerBean(Parameters.class, () -> getParameters());
//                genericApplicationContext.registerBean(HostServices.class, () -> getHostServices());
//
//            }
//        };

        ExtensionLoader loader = new ExtensionLoader();
        //Map<UUID, Extension> extensions = loader.loadExtensions();

        //extensions.entrySet().forEach(e -> logger.info("Loading extension {} id: {}", e.getValue().getClass().getSimpleName(), e.getKey().toString()));

        List<Class<?>> sources = new ArrayList<>();

        loader.loadExtensions().values().forEach(e -> {
            System.out.println("Loaded : " + e.getClass().getName());
            sources.add(e.getClass());
            sources.addAll(e.explicitClassToRegister());
        });
        //sources.addAll(explicitClassToRegister());
        //sources.add(0, this.getClass());
//

//        return new SpringApplicationBuilder()
//                .sources(sources.toArray(new Class<?>[0])).initializers(initializer)
//                .build().run(new String[0]);

        return new CustomAnnotationConfigApplicationContext(sources.toArray(new Class<?>[0]));
    }
}
