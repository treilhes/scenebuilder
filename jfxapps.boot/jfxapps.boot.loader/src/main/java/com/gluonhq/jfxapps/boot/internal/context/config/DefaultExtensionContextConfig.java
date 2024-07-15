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
package com.gluonhq.jfxapps.boot.internal.context.config;

import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.hibernate.annotations.GenericGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.customizers.OpenApiBuilderCustomizer;
import org.springdoc.core.customizers.ServerBaseUrlCustomizer;
import org.springdoc.core.customizers.SpringDocCustomizers;
import org.springdoc.core.properties.SpringDocConfigProperties;
import org.springdoc.core.properties.SwaggerUiConfigParameters;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.core.providers.ActuatorProvider;
import org.springdoc.core.providers.CloudFunctionProvider;
import org.springdoc.core.providers.JavadocProvider;
import org.springdoc.core.providers.ObjectMapperProvider;
import org.springdoc.core.providers.RepositoryRestResourceProvider;
import org.springdoc.core.providers.RouterFunctionProvider;
import org.springdoc.core.providers.SecurityOAuth2Provider;
import org.springdoc.core.providers.SpringDocProviders;
import org.springdoc.core.providers.SpringWebProvider;
import org.springdoc.core.providers.WebConversionServiceProvider;
import org.springdoc.core.service.AbstractRequestService;
import org.springdoc.core.service.GenericResponseService;
import org.springdoc.core.service.OpenAPIService;
import org.springdoc.core.service.OperationService;
import org.springdoc.core.service.SecurityService;
import org.springdoc.core.utils.PropertyResolverUtils;
import org.springdoc.webmvc.api.OpenApiWebMvcResource;
import org.springdoc.webmvc.core.configuration.MultipleOpenApiSupportConfiguration;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.core.providers.SpringWebMvcProvider;
import org.springdoc.webmvc.ui.SwaggerConfigResource;
import org.springdoc.webmvc.ui.SwaggerIndexPageTransformer;
import org.springdoc.webmvc.ui.SwaggerIndexTransformer;
import org.springdoc.webmvc.ui.SwaggerUiHome;
import org.springdoc.webmvc.ui.SwaggerWebMvcConfigurer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.Ordered;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.MutablePersistenceUnitInfo;
import org.springframework.orm.jpa.persistenceunit.PersistenceUnitPostProcessor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.util.MimeType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.thymeleaf.context.IExpressionContext;
import org.thymeleaf.linkbuilder.ILinkBuilder;
import org.thymeleaf.linkbuilder.StandardLinkBuilder;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.LocalContextOnly;
import com.gluonhq.jfxapps.boot.context.bpp.OverridedBeanPostProcessor;
import com.gluonhq.jfxapps.boot.loader.extension.Extension;
import com.gluonhq.jfxapps.boot.platform.InternalRestClient;
//import org.springframework.data.repository.core.support.TransactionalRepositoryProxyPostProcessor;
import com.gluonhq.jfxapps.boot.platform.JfxAppsPlatform;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Table;
//import app.root.rest.RootRestController;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * This class is the default configuration for an extension context. It is used
 * to configure the extension context with the necessary beans to support the
 * extension.<br/>
 * <br/>
 * The following main features are supported:<br/>
 * - JPA repositories<br/>
 * - Transaction management<br/>
 * - Aspect oriented programming<br/>
 * - Web MVC<br/>
 * - Swagger documentation<br/>
 * - Thymeleaf template engine<br/>
 *
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@EnableJpaRepositories
@EnableTransactionManagement
@EnableWebMvc
public class DefaultExtensionContextConfig implements WebMvcConfigurer {

    private final static Logger logger = LoggerFactory.getLogger(DefaultExtensionContextConfig.class);

    private final JfxAppContext context;

    private final String basePath;
    private final String internalContextPath;
    private final String externalContextPath;

    public DefaultExtensionContextConfig(JfxAppContext context,
            @Value(InternalRestClient.SERVLET_PATH_PROP) String servletPath,
            @Value(InternalRestClient.CONTEXT_PATH_PROP) String contextPath) {
        super();
        this.context = context;
        this.basePath = ((StringUtils.hasText(contextPath) ? "/" + contextPath : "")
                + (StringUtils.hasText(servletPath) ? "/" + servletPath : "")).replaceAll("/+", "/");
        this.internalContextPath = "/" + JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX + "/" + context.getId();
        this.externalContextPath = basePath + this.internalContextPath;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        registry.addResourceHandler(internalContextPath + "/**").addResourceLocations("classpath:/static/");
    }

    public final static List<Class<?>> classesToRegister = List.of(
            DefaultExtensionContextConfig.class,
            // default spring beans
            org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor.class,
            org.springframework.context.annotation.CommonAnnotationBeanPostProcessor.class,
            org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor.class,
            com.gluonhq.jfxapps.boot.context.bpp.OverridedBeanPostProcessor.class,
            //org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator.class,
            org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor.class,

            OverridedBeanPostProcessor.class,
            JfxAppsExtensionRestController.class,
            SwaggerConfig.class,
            SpringDocWebMvcConfiguration.class,
            MultipleOpenApiSupportConfiguration.class,
            JacksonAutoConfiguration.class);

    @Bean
    AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator() {
        final var processor = new AnnotationAwareAspectJAutoProxyCreator();
        processor.setProxyTargetClass(true);
        return processor;

    }

    @Bean(name = "templateEngine")
    public SpringTemplateEngine springTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(rawTemplateResolver());
        templateEngine.setLinkBuilder(linkBuilder());
        return templateEngine;
    }

    private ILinkBuilder linkBuilder() {
        return new StandardLinkBuilder() {
            @Override
            protected String computeContextPath(IExpressionContext context, String base,
                    Map<String, Object> parameters) {
                return externalContextPath;
            }

        };
    }

    private ITemplateResolver rawTemplateResolver() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(context.getBeanClassLoader());
        templateResolver.setPrefix("/templates/"); // or whatever other directory you have the files
        templateResolver.setSuffix(".html"); // if they are html files
        // templateResolver.setTemplateMode(TemplateMode.RAW);
        // templateResolver.setForceTemplateMode(true); //to turn off suffix-based
        // choosing
        templateResolver.setCharacterEncoding("UTF8");
        templateResolver.setCheckExistence(true);
        return templateResolver;
    }

    @Bean(DispatcherServlet.VIEW_RESOLVER_BEAN_NAME)
    org.thymeleaf.spring6.view.ThymeleafViewResolver thymeleafViewResolver(ThymeleafProperties properties,
            SpringTemplateEngine templateEngine) {
        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setTemplateEngine(springTemplateEngine());
        resolver.setCharacterEncoding(properties.getEncoding().name());
        resolver.setContentType(
                appendCharset(properties.getServlet().getContentType(), resolver.getCharacterEncoding()));
        resolver.setProducePartialOutputWhileProcessing(
                properties.getServlet().isProducePartialOutputWhileProcessing());
        resolver.setExcludedViewNames(properties.getExcludedViewNames());
        resolver.setViewNames(properties.getViewNames());
        // This resolver acts as a fallback resolver (e.g. like a
        // InternalResourceViewResolver) so it needs to have low precedence
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 5);
        resolver.setCache(properties.isCache());
        return resolver;
    }

    private String appendCharset(MimeType type, String charset) {
        if (type.getCharset() != null) {
            return type.toString();
        }
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        parameters.put("charset", charset);
        parameters.putAll(type.getParameters());
        return new MimeType(type, parameters).toString();
    }

    /**
     * This class is used to register the redirector dispatcher servlet in the
     * extension context. The redirector dispatcher servlet is a custom dispatcher
     * servlet that is used to forward requests to the appropriate context. The
     * context is determined by the path of the request. The path of the request is
     * used to determine the context id. The context id is used to forward the
     * request to the appropriate context. The redirection occurs in the
     * DispatcherRestController loaded by the jfxapps.boot.loader module.
     *
     */
    @Bean(name = "redirector")
    DispatcherServlet dispatcherServlet(ServletContext sctx, ServletConfig scfg, JfxAppContext context) {

        var dispatcherServlet = new DispatcherServlet(context);
        // dispatcherServlet.setDetectAllHandlerMappings(false);
        dispatcherServlet.setDetectAllViewResolvers(false);

        ServletConfig servletConfig = new ServletConfig() {

            @Override
            public String getServletName() {
                return "redirector";
            }

            @Override
            public ServletContext getServletContext() {
                return sctx;
            }

            @Override
            public String getInitParameter(String name) {
                return null;
            }

            @Override
            public Enumeration<String> getInitParameterNames() {
                return Collections.emptyEnumeration();
            }
        };

        try {
            dispatcherServlet.init(servletConfig);
        } catch (ServletException e) {
            logger.error("Error initializing redirector dispatcher servlet", e);
        }

        return dispatcherServlet;
    }

    /**
     * Customized handler mapping for redirector.Redirected requests contains the
     * context id in the path as prefix. Adding manualy the prefix to the
     * {@link RequestMapping} annotation path is tedious so we add a custom handler
     * mapping to automatically add the prefix to the request path of each
     * endpoints.
     *
     * @param context
     * @return
     */
    @Bean
    RequestMappingHandlerMapping requestMappingHandlerMapping(JfxAppContext context) {
        var handler = new RequestMappingHandlerMapping();
        handler.setOrder(0);
        handler.setPathPrefixes(Map.of(internalContextPath, c -> true));
        return handler;
    }

//    private Predicate<? super Class<?>> webContextValidClassesCheck() {
//        return c -> c.getAnnotationsByType(RestController.class).length > 0
//                || c.getAnnotationsByType(ControllerAdvice.class).length > 0
//        // || c.getAnnotationsByType(Configuration.class).length > 0
//        ;
//    }

//    @Bean(name = "jpaMappingContext")
//    public JpaMetamodelMappingContextFactoryBean jpaMetamodelMappingContextFactoryBean(SbContext ctx) {
//        JpaMetamodelMappingContextFactoryBean factory = new JpaMetamodelMappingContextFactoryBean();
//
//        factory.setBeanClassLoader(ctx.getBeanClassLoader());
//
//        return factory;
//    }

    /**
     * This method is used to create the entity manager factory for the local
     * context Mainly here to propagate the classloader
     *
     * @param dataSource       the data source
     * @param jpaVendorAdapter the jpa vendor adapter
     * @param ext              the extensions
     * @param ctx              the sb context
     * @return
     */
    @Bean("entityManagerFactory")
    LocalContainerEntityManagerFactoryBean localEntityManagerFactory(DataSource dataSource,
            JpaVendorAdapter jpaVendorAdapter, List<Extension> ext, JfxAppContext ctx) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();

        var packageName = ext.stream()
                .filter(e -> e.getClass().getClassLoader() == ctx.getBeanClassLoader())
                .map(e -> e.getClass().getPackageName()).findAny();

        packageName.ifPresent(p -> {
            em.setDataSource(dataSource);
            em.setJpaVendorAdapter(jpaVendorAdapter);
            em.setPersistenceUnitPostProcessors(new JfxAppsPersistenceRulesCheck());
            em.setPackagesToScan(p);
            em.setJpaProperties(hibernateProperties());
        });

        return em;
    }

    /**
     * This method is used to create the transaction manager for the local context
     * Mainly here to propagate the classloader
     */
    @Bean(name = "transactionManager")
    PlatformTransactionManager localTransactionManager(EntityManagerFactory factory, DataSource dataSource) {
        JpaTransactionManager tm = new JpaTransactionManager();
        tm.setEntityManagerFactory(factory);
        tm.setDataSource(dataSource);
        tm.setNestedTransactionAllowed(true);
        return tm;
    }

    /**
     * This method is used to create the repository factory for the local context
     * Mainly here to propagate the classloader
     *
     * @param mngr the entity manager
     * @return the repository factory
     */
    @Bean
    RepositoryFactorySupport factoryBean(EntityManager mngr) {
        return new JpaRepositoryFactory(mngr);
    }

    /**
     * Hibernate configuration properties. Do not use the spring.jpa.hibernate.*
     * prefix as it is not supported by hibernate.
     *
     * @return the hibernate properties
     */
    final Properties hibernateProperties() {
        final Properties hibernateProperties = new Properties();

        hibernateProperties.setProperty("hibernate.show_sql", "true");
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        // hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

//        hibernateProperties.setProperty("spring.jpa.open-in-view", "false");

//        hibernateProperties.setProperty("spring.jpa.hibernate.naming.physical-strategy",
//                "com.gluonhq.jfxapps.boot.jpa.ModuleAwarePhysicalNamingStrategyStandardImpl");
//        hibernateProperties.setProperty("spring.jpa.hibernate.naming.implicit-strategy",
//                "com.gluonhq.jfxapps.boot.jpa.ModuleAwareImplicitNamingStrategyLegacyJpaImpl");

        hibernateProperties.setProperty("hibernate.physical_naming_strategy",
                "com.gluonhq.jfxapps.boot.jpa.ModuleAwarePhysicalNamingStrategyStandardImpl");
        hibernateProperties.setProperty("hibernate.implicit_naming_strategy",
                "com.gluonhq.jfxapps.boot.jpa.ModuleAwareImplicitNamingStrategyLegacyJpaImpl");
        hibernateProperties.setProperty("hibernate.id.db_structure_naming_strategy",
                "com.gluonhq.jfxapps.boot.jpa.ModuleAwareImplicitDatabaseObjectNamingStrategyImpl");
//
//
        // hibernateProperties.setProperty("spring.datasource.driverClassName",
        // "org.h2.Driver");
        // hibernateProperties.setProperty("spring.datasource.url",
        // "jdbc:h2:file:./jfxapps-h2-db");
        // hibernateProperties.setProperty("spring.datasource.username", "sa");
        // hibernateProperties.setProperty("spring.datasource.password", "");

        return hibernateProperties;
    }

    /**
     * This class is used to prevent the usage of some annotations in JPA entities.
     * The annotations that are forbidden are: - {@link Table} The annotation
     * attributes that are forbidden are: - sequence_name for
     * {@link GenericGenerator}
     *
     * The list isn't exhaustive and can be extended in the future.
     *
     * The forbidden annotations are forbidden because they allow customization of
     * the underlying database schema. Extensions share the same schema and the
     * schema is managed by the application to prevent name collisions.
     *
     */
    class JfxAppsPersistenceRulesCheck implements PersistenceUnitPostProcessor {

        /**
         * This method is called by the JPA provider to validate the JPA entities
         * detected in the classpath.
         *
         * @param pui the persistence unit info
         * @throws JfxAppsJpaForbiddenException if a forbidden JPA annotation or
         *                                      attribute is found
         */
        @Override
        public void postProcessPersistenceUnitInfo(MutablePersistenceUnitInfo pui) {
            try {
                for (String clsName : pui.getManagedClassNames()) {
                    var cls = pui.getClassLoader().loadClass(clsName);

                    // prevent table name
                    var tableAnnotationExists = cls.getAnnotationsByType(Table.class).length > 0;
                    if (tableAnnotationExists) {
                        throw new JfxAppsJpaForbiddenException(
                                Table.class.getName() + " is a forbidden JPA annotation in JfxApps");
                    }

                    // prevent sequence_name
                    var nameParamExist = Stream.of(cls.getDeclaredFields())
                            .flatMap(f -> Stream.of(f.getAnnotationsByType(GenericGenerator.class)))
                            .flatMap(g -> Stream.of(g.parameters())).anyMatch(p -> "sequence_name".equals(p.name()));

                    if (nameParamExist) {
                        throw new JfxAppsJpaForbiddenException(GenericGenerator.class.getName()
                                + " sequence_name parameter is a forbidden JPA annotation in JfxApps");
                    }

                }
            } catch (ClassNotFoundException e) {
                throw new JfxAppsJpaForbiddenException("Unable to validate class", e);
            }
        }

    }

    /**
     * This exception is thrown when a forbidden JPA annotation or attribute is
     * found in the detected JPA entities.
     */
    class JfxAppsJpaForbiddenException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        public JfxAppsJpaForbiddenException(String message, Throwable cause) {
            super(message, cause);
        }

        public JfxAppsJpaForbiddenException(String message) {
            super(message);
        }
    }

    public static class SwaggerConfig {

        @Bean
        SwaggerWelcomeWebMvc swaggerWelcome(SwaggerUiConfigProperties swaggerUiConfig,
                SpringDocConfigProperties springDocConfigProperties,
                SwaggerUiConfigParameters swaggerUiConfigParameters, SpringWebProvider springWebProvider) {
            return new SwaggerWelcomeWebMvc(swaggerUiConfig, springDocConfigProperties, swaggerUiConfigParameters,
                    springWebProvider);
        }

        @Bean
        SpringWebProvider springWebProvider(ApplicationContext ctx) {
            var swp = new SpringWebMvcProvider();
            swp.setApplicationContext(ctx);
            return swp;
        }

        @Bean
        SwaggerConfigResource swaggerConfigResource(SwaggerWelcomeCommon swaggerWelcomeCommon) {
            return new SwaggerConfigResource(swaggerWelcomeCommon);
        }

        @Bean
        SwaggerUiHome swaggerUiHome() {
            return new SwaggerUiHome();
        }

        @Bean
        SwaggerIndexTransformer indexPageTransformer(SwaggerUiConfigProperties swaggerUiConfig,
                SwaggerUiOAuthProperties swaggerUiOAuthProperties, SwaggerUiConfigParameters swaggerUiConfigParameters,
                SwaggerWelcomeCommon swaggerWelcomeCommon, ObjectMapperProvider objectMapperProvider) {
            return new SwaggerIndexPageTransformer(swaggerUiConfig, swaggerUiOAuthProperties, swaggerUiConfigParameters,
                    swaggerWelcomeCommon, objectMapperProvider);
        }

        @Bean
        SwaggerWebMvcConfigurer swaggerWebMvcConfigurer(SwaggerUiConfigParameters swaggerUiConfigParameters,
                SwaggerIndexTransformer swaggerIndexTransformer, Optional<ActuatorProvider> actuatorProvider) {
            return new SwaggerWebMvcConfigurer(swaggerUiConfigParameters, swaggerIndexTransformer, actuatorProvider);
        }

    }
//    @Bean("org.springdoc.core.properties.SwaggerUiConfigProperties")
//    SwaggerUiConfigProperties swaggerUiConfigProperties() {
//        var param = new SwaggerUiConfigProperties();
//
//        //param.setConfigUrl(externalContextPath + "/v3/api-docs/swagger-config");
//        //param.setPath(externalContextPath + "/swagger-ui.html");
//
//        return param;
//    }

    @Bean
    ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
        return new ServerBaseUrlCustomizer() {
            @Override
            public String customize(String server) {
                return server == null ? null : server.replace(internalContextPath, "");
            }
        };
    }

    @Bean("org.springdoc.core.properties.SwaggerUiConfigParameters")
    SwaggerUiConfigParameters swaggerUiConfigParameters(SwaggerUiConfigProperties swaggerUiConfigProperties) {
        var param = new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        param.setPath(
                String.format("/%s/%s/swagger-ui.html", JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX, context.getId()));
        return param;
    }

    @Bean("org.springdoc.core.properties.SpringDocConfigProperties")
    SpringDocConfigProperties springDocConfigProperties() {
        var param = new SpringDocConfigProperties();
        param.getApiDocs().setPath(
                String.format("/%s/%s/v3/api-docs", JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX, context.getId()));
        return param;
    }

    @Bean
    OpenApiWebMvcResource openApiResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
            AbstractRequestService requestBuilder, GenericResponseService responseBuilder,
            OperationService operationParser, SpringDocConfigProperties springDocConfigProperties,
            SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
        return new OpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder, responseBuilder, operationParser,
                springDocConfigProperties, springDocProviders, springDocCustomizers);
    }

    @Bean
    OpenAPIService openAPIBuilder(@LocalContextOnly Optional<OpenAPI> openAPI, SecurityService securityParser,
            SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils,
            Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
            Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
            Optional<JavadocProvider> javadocProvider) {
        return new OpenAPIService(openAPI.or(() -> Optional.of(defaultOpenAPI())), securityParser, springDocConfigProperties, propertyResolverUtils,
                openApiBuilderCustomisers, serverBaseUrlCustomisers, javadocProvider);
    }

    @Bean
    SpringDocProviders springDocProviders(Optional<ActuatorProvider> actuatorProvider,
            Optional<CloudFunctionProvider> springCloudFunctionProvider,
            Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
            Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider,
            Optional<RouterFunctionProvider> routerFunctionProvider, Optional<SpringWebProvider> springWebProvider,
            Optional<WebConversionServiceProvider> webConversionServiceProvider,
            ObjectMapperProvider objectMapperProvider) {
        return new SpringDocProviders(actuatorProvider, springCloudFunctionProvider, springSecurityOAuth2Provider,
                repositoryRestResourceProvider, routerFunctionProvider, springWebProvider, webConversionServiceProvider,
                objectMapperProvider);
    }

    @Bean(autowireCandidate = false)
    OpenAPI defaultOpenAPI() {
      Info info = new Info()
          .title(context.getId().toString());
      return new OpenAPI().info(info);
    }
}
