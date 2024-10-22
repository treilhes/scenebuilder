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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import javax.sql.DataSource;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.testfx.api.FxToolkit;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.layer.Layer;
import com.gluonhq.jfxapps.boot.api.layer.ModuleLayerManager;
import com.gluonhq.jfxapps.boot.api.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.api.loader.extension.RootExtension;
import com.gluonhq.jfxapps.boot.api.loader.extension.SealedExtension;
import com.gluonhq.jfxapps.boot.context.impl.ContextManagerImpl;
import com.gluonhq.jfxapps.boot.context.impl.JfxAppContextImpl;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper;
import com.gluonhq.jfxapps.boot.loader.internal.context.ContextBootstraper.ServiceLoader;
import com.gluonhq.jfxapps.boot.loader.model.AbstractExtension;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.javafx.internal.FxmlControllerBeanPostProcessor;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.subjects.LifecyclePostProcessor;

import javafx.stage.Stage;

public class JfxAppsExtension implements BeforeEachCallback, ParameterResolver {
    private final static Logger logger = LoggerFactory.getLogger(JfxAppsExtension.class);
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
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        // Check if the parameter is supported, e.g., by type or annotation
        var type = parameterContext.getParameter().getType();
        return type == Stage.class || type == StageBuilder.class;
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

    public static class JfxAppsTestContextBootstrapper extends SpringBootTestContextBootstrapper {

        @Override
        protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
            return JfxAppsContextLoader.class;
        }

        @Override
        protected String[] getProperties(Class<?> testClass) {
            return MergedAnnotations.from(testClass, SearchStrategy.INHERITED_ANNOTATIONS).get(JfxAppsTest.class)
                    .getValue("properties", String[].class).orElse(null);
        }

    }

    public static class JfxAppsContextLoader extends AbstractContextLoader {

        protected static final ThreadLocal<JfxAppContext> testContextHolder = new ThreadLocal<>();

        private Class<?> testClass;

        private Boolean loadDefaultScopes;

        public JfxAppsContextLoader() {
            super();
        }

        @Override
        protected String[] generateDefaultLocations(Class<?> clazz) {
            this.testClass = clazz;

            this.loadDefaultScopes = MergedAnnotations.from(testClass, SearchStrategy.INHERITED_ANNOTATIONS)
                    .get(JfxAppsTest.class).getValue("loadDefaultScopes", Boolean.class).orElse(null);

            return super.generateDefaultLocations(clazz);
        }

        @Override
        public ApplicationContext loadContext(MergedContextConfiguration mergedConfig) throws Exception {
            JfxAppContextImpl.applicationScope.clear();

            var contextId = SealedExtension.ROOT_ID;
            var parent = Mockito.mock(JfxAppContext.class);
            var loader = Mockito.mock(ServiceLoader.class);
            var extensionDefinition = Mockito.mock(AbstractExtension.class);
            var extension = Mockito.mock(RootExtension.class);
            var layer = Mockito.mock(Layer.class);
            var layerManager = Mockito.mock(ModuleLayerManager.class);
            // we want the context manager to return the same context everytime
            var contextManager = new ContextManagerImpl(null);
            var bootstraper = new ContextBootstraper(layerManager, contextManager);

            when(layer.getId()).thenReturn(contextId);
            when(layer.getModuleLayer()).thenReturn(ModuleLayer.boot());
            when(layerManager.get(any())).thenReturn(layer);
            when(loader.loadService(layer, Extension.class)).thenReturn(Set.of(extension));

            when(extension.getId()).thenReturn(contextId);

            var classes = new ArrayList<Class<?>>(List.of(mergedConfig.getClasses()));

            //@formatter:off
            classes.addAll(List.of(
                    I18NTestConfig.class,
                    LifecyclePostProcessor.class,
                    FxmlControllerBeanPostProcessor.class,
                    JfxAppsTest.Application1Bean.class,
                    JfxAppsTest.Application1InstanceBean.class,
                    JfxAppsTest.Application2Bean.class,
                    JfxAppsTest.Application2InstanceBean.class,
                    ApplicationEvents.ApplicationEventsImpl.class,
                    ApplicationInstanceEvents.ApplicationInstanceEventsImpl.class,
                    JavafxThreadClassloader.class,
                    StageBuilder.class));
            //@formatter:on

            when(extension.localContextClasses()).thenReturn(classes);
            //@formatter:on
            var ctx = bootstraper.create(parent, extensionDefinition, List.of(contextManager), null, loader);

            if (loadDefaultScopes) {
                // set the current scopes
                var appBean = ctx.getBean(JfxAppsTest.Application1Bean.class);
                logger.info("Loaded Application Bean: {}", appBean);
                var instanceBean = ctx.getBean(JfxAppsTest.Application1InstanceBean.class);
                logger.info("Loaded Application Instance Bean: {}", instanceBean);

            }

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
    //@AutoConfigureMockMvc
    static class I18NTestConfig {
        @Bean
        @ConditionalOnMissingBean
        I18N i18nTest() {
            return new I18N(List.of(), true);
        }

        @Bean
        @ConditionalOnMissingBean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.h2.Driver");
            dataSource.setUrl("jdbc:h2:mem:testdb"); // Pour une base en mémoire ou file:./data/testdb pour fichier
            dataSource.setUsername("sa");
            dataSource.setPassword(""); // H2 par défaut n'a pas de mot de passe pour l'utilisateur 'sa'

            return dataSource;
        }

        @Bean
        @ConditionalOnBean(name = "servletContext")
        public JpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
            adapter.setGenerateDdl(true); // Générer automatiquement le schéma de base de données
            adapter.setShowSql(true); // Afficher les requêtes SQL dans la console
            adapter.setDatabasePlatform("org.hibernate.dialect.H2Dialect"); // Utilisation de H2
            return adapter;
        }

        @Bean
        @ConditionalOnBean(name = "servletContext")
        public SwaggerUiConfigProperties swaggerUiConfigProperties() {
            return new SwaggerUiConfigProperties();
        }

        @Bean
        @ConditionalOnBean(name = "servletContext")
        public SwaggerUiOAuthProperties swaggerUiOAuthProperties() {
            return new SwaggerUiOAuthProperties();
        }

        @Bean
        @ConditionalOnBean(name = "servletContext")
        public ObjectMapperProvider objectMapperProvider(SpringDocConfigProperties config) {
            return new ObjectMapperProvider(config);
        }

    }
}
