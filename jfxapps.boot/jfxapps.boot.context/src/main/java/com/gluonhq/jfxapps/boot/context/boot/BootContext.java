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
package com.gluonhq.jfxapps.boot.context.boot;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.logging.ConditionEvaluationReportLoggingListener;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.Configuration;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;

public class BootContext {

    private static final String BOOT_PROFILE = "boot";

    public static JfxAppContext create(List<Class<?>> classes, String[] args) {
        return create(classes, null, args, null);
    }

    public static JfxAppContext create(String[] args) {
        return create(List.of(), null, args, null);
    }

    public static JfxAppContext create(List<Class<?>> classes, WebApplicationType type, String[] args, ApplicationContextInitializer<JfxAppContext> initializer) {

        if (classes == null) {
            classes = List.of();
        }

        var startup = new BufferingApplicationStartup(10000);
        //var startup = new FlightRecorderApplicationStartup();

        var step = startup.start("Initializing Boot context");

        var bootInitializer = new BootContextInitializer(classes, List.of());
        var conditionEvaluation = new ConditionEvaluationReportLoggingListener();
        var initializerList = List.of(bootInitializer, conditionEvaluation);

        SpringApplication application = new SpringApplication(BootConfig.class);
        application.setApplicationStartup(startup);
        application.setApplicationContextFactory(new BootContextFactory());
        application.setInitializers(initializerList);
        application.setWebApplicationType(type == null ? WebApplicationType.NONE : type);
        application.setAdditionalProfiles(BOOT_PROFILE);

        if (initializer != null) {
            application.addInitializers(initializer);
        }

        var context = (JfxAppContext)application.run(args);

        step.end();

        return context;
    }

    @Configuration
    public static class BootConfig {

    }
}
