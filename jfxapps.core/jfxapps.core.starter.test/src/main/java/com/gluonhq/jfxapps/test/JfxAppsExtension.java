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
package com.gluonhq.jfxapps.test;

import static org.junit.jupiter.api.extension.ExtensionContext.Namespace.create;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.internal.FxmlControllerBeanPostProcessor;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;

import javafx.stage.Stage;

public class JfxAppsExtension implements BeforeEachCallback, ParameterResolver {

    private final static Namespace JFXAPPS = create("com.gluonhq.jfxapps");

    // This constructor is invoked by JUnit Jupiter via reflection or ServiceLoader
    @SuppressWarnings("unused")
    public JfxAppsExtension() {
    }

    /**
     * Callback that is invoked <em>before</em> each test is invoked.
     *
     * @param context the current extension context; never {@code null}
     */
    @Override
    public void beforeEach(final ExtensionContext context) {
        Stage stage = FxToolkit.toolkitContext().getRegisteredStage();

        final var globalStore = context.getStore(ExtensionContext.Namespace.GLOBAL);
        final var spring = globalStore.get(SpringExtension.class, SpringExtension.class);
        final var app = globalStore.get(ApplicationExtension.class, ApplicationExtension.class);
System.out.println();

//        context.getStore(JFXAPPS).put(MOCKS, new HashSet<>());
//        context.getStore(JFXAPPS).put(SESSION, session);
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Check if the parameter is supported, e.g., by type or annotation
        var type = parameterContext.getParameter().getType();
        return type == Stage.class
                || type == StageBuilder.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Provide the instance of the parameter
        var type = parameterContext.getParameter().getType();

        if (type == Stage.class) {
            return FxToolkit.toolkitContext().getRegisteredStage();
        }

        if (type == StageBuilder.class) {
            var context = JfxAppsContextLoader.testContextHolder.get();
            var builder = context.getBean(StageBuilder.class);
            builder.stage(FxToolkit.toolkitContext().getRegisteredStage());

            return builder;
        }

        return null;
    }

    @ApplicationSingleton
    public static class AppBean implements Application {
    }

    @ApplicationInstanceSingleton
    public static class AppInstanceBean implements ApplicationInstance {
    }

    public static class JfxAppsTestContextBootstrapper extends DefaultTestContextBootstrapper {

        @Override
        protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
            return JfxAppsContextLoader.class;
        }

    }

    public static class JfxAppsContextLoader extends AbstractContextLoader {

        protected static final ThreadLocal<JfxAppContext> testContextHolder = new ThreadLocal<>();

        @Override
        public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
            JfxAppContextImpl.applicationScope.clear();

            var classes = new ArrayList<Class<?>>(List.of(mergedConfig.getClasses()));
            classes.addAll(List.of(
                    I18NTestConfig.class,
                    FxmlControllerBeanPostProcessor.class,
                    AppBean.class,
                    AppInstanceBean.class,
                    ApplicationEvents.ApplicationEventsImpl.class,
                    ApplicationInstanceEvents.ApplicationInstanceEventsImpl.class,
                    JavafxThreadClassloader.class,
                    StageBuilder.class));


            var ctx = JfxAppContextImpl.fromScratch(classes.toArray(new Class<?>[0]));

            // set the current scopes
            ctx.getBean(AppBean.class);
            ctx.getBean(AppInstanceBean.class);

            testContextHolder.set(ctx);

            return ctx;
        }

        @Override
        protected String[] getResourceSuffixes() {
            return new String[] { "-context.xml", "Context.groovy" };
        }

        @Override
        protected String getResourceSuffix() {
            throw new IllegalStateException();
        }

    }

    @TestConfiguration
    static class I18NTestConfig {
        @Bean
        @ConditionalOnMissingBean
        I18N i18nTest() {
            return new I18N(List.of(), true);
        }
    }
}
