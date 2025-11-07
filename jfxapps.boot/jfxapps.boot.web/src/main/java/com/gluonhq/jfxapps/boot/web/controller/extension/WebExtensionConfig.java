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
package com.gluonhq.jfxapps.boot.web.controller.extension;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.aopalliance.aop.Advice;
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
import org.springdoc.webmvc.ui.SwaggerResourceResolver;
import org.springdoc.webmvc.ui.SwaggerUiHome;
import org.springdoc.webmvc.ui.SwaggerWebMvcConfigurer;
import org.springdoc.webmvc.ui.SwaggerWelcomeCommon;
import org.springdoc.webmvc.ui.SwaggerWelcomeWebMvc;
import org.springframework.aop.Advisor;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.AdvisedSupportListener;
import org.springframework.aop.framework.AdvisorChainFactory;
import org.springframework.aop.framework.AopConfigException;
import org.springframework.aop.framework.AopProxyFactory;
import org.springframework.aop.framework.ProxyConfig;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.thymeleaf.autoconfigure.ThymeleafProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
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

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.Lazy;
import com.gluonhq.jfxapps.boot.api.context.annotation.LocalContextOnly;
import com.gluonhq.jfxapps.boot.api.platform.JfxAppsPlatform;
import com.gluonhq.jfxapps.boot.api.web.client.InternalRestClient;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
//import app.root.rest.RootRestController;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;

/**
 * This class is the default configuration for an extension context. It is used
 * to configure the extension context with the necessary beans to support the
 * web configuration.<br/>
 * <br/>
 * The following main features are supported:<br/>
 * - Web MVC<br/>
 * - Swagger documentation<br/>
 * - Thymeleaf template engine<br/>
 *
 */
@Configuration
@ConditionalOnBean(name = "servletContext")
@EnableWebMvc
//@formatter:off
@Import({
    JfxAppsExtensionRestController.class,
    WebExtensionConfig.SpringDocConfig.class,
    SpringDocWebMvcConfiguration.class,
    MultipleOpenApiSupportConfiguration.class,
    JacksonAutoConfiguration.class
})
//@formatter:on
@PropertySource(value = "classpath:/application.properties", ignoreResourceNotFound = true)
public class WebExtensionConfig {

    private final static Logger logger = LoggerFactory.getLogger(WebExtensionConfig.class);

    public WebExtensionConfig() {
        super();
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
    // this method is here instead of WebConfig because in web config the bean isn't
    // created (don't know why)
    @Bean
    RequestMappingHandlerMapping requestMappingHandlerMapping(ExtensionWebContext extContext) {
        var handler = new RequestMappingHandlerMapping();
        handler.setOrder(0);
        handler.setPathPrefixes(Map.of(extContext.getInternalContextPath(), c -> true));
        return handler;
    }

    @Bean
    public ExtensionWebContext extensionWebContext(JfxAppContext context,
            @Value(InternalRestClient.CONTEXT_PATH_PROP) String contextPath,
            @Value(InternalRestClient.SERVLET_PATH_PROP) String servletPath) {
        return new ExtensionWebContext(context, contextPath, servletPath);
    }

    @Configuration
    public static class WebConfig implements WebMvcConfigurer {

        private final ExtensionWebContext extContext;

        public WebConfig(ExtensionWebContext extContext) {
            super();
            this.extContext = extContext;
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
        @Lazy
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

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            WebMvcConfigurer.super.addResourceHandlers(registry);
            registry //
                    .addResourceHandler(extContext.getInternalContextPath() + "/**") //
                    .addResourceLocations("classpath:/static/");
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        static AsyncAnnotationBeanPostProcessor asyncAnnotationBeanPostProcessor() {
            final var processor = new MyAsyncAnnotationBeanPostProcessor();
            return processor;

        }
    }

    @Configuration
    public static class MvcConfig {

        private final ExtensionWebContext extContext;
        private final JfxAppContext context;

        public MvcConfig(ExtensionWebContext extContext, JfxAppContext context) {
            super();
            this.extContext = extContext;
            this.context = context;
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
                    return extContext.getExternalContextPath();
                }

            };
        }

        private ITemplateResolver rawTemplateResolver() {
            ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver(
                    context.getBeanClassLoader());
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
    }

    @Configuration
    @ConditionalOnBean(name = "servletContext")
    public static class SpringDocConfig {

        private final ExtensionWebContext extContext;

        public SpringDocConfig(ExtensionWebContext extContext) {
            this.extContext = extContext;
        }

        @Lazy
        @Bean("org.springdoc.core.properties.SwaggerUiConfigProperties")
        SwaggerUiConfigProperties swaggerUiConfigProperties(JfxAppContext context) {
            var config = new SwaggerUiConfigProperties();
            String path = String.format("/%s/%s/swagger-ui.html", JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX,
                    context.getId());
            config.setPath(path);
            return config;
        }

        @Lazy
        @Bean("org.springdoc.core.properties.SwaggerUiConfigParameters")
        SwaggerUiConfigParameters swaggerUiConfigParameters(
                @LocalContextOnly SwaggerUiConfigProperties swaggerUiConfigProperties) {
            return new SwaggerUiConfigParameters(swaggerUiConfigProperties);
        }

        @Lazy
        @Bean("org.springdoc.core.properties.SpringDocConfigProperties")
        SpringDocConfigProperties springDocConfigProperties() {
            var param = new SpringDocConfigProperties();
            param.getApiDocs().setPath(String.format("/%s/%s/v3/api-docs", JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX,
                    extContext.getContext().getId()));
            return param;
        }

        @Lazy
        @Bean
        GenericResponseService genericResponseService(OperationService operationService,
                @LocalContextOnly SpringDocConfigProperties springDocConfigProperties, PropertyResolverUtils propertyResolverUtils) {

            var genericResponseService = new GenericResponseService(operationService, springDocConfigProperties,
                    propertyResolverUtils);

            genericResponseService.setApplicationContext(extContext.getContext());

            return genericResponseService;
        }

        @Lazy
        @Bean
        OpenApiWebMvcResource openApiResource(ObjectFactory<OpenAPIService> openAPIBuilderObjectFactory,
                AbstractRequestService requestBuilder, @LocalContextOnly GenericResponseService responseBuilder,
                OperationService operationParser, @LocalContextOnly SpringDocConfigProperties springDocConfigProperties,
                SpringDocProviders springDocProviders, SpringDocCustomizers springDocCustomizers) {
            responseBuilder.setApplicationContext(extContext.getContext());
            return new OpenApiWebMvcResource(openAPIBuilderObjectFactory, requestBuilder, responseBuilder,
                    operationParser, springDocConfigProperties, springDocProviders, springDocCustomizers);
        }

        @Lazy
        @Bean
        OpenAPIService openAPIBuilder(@LocalContextOnly Optional<OpenAPI> openAPI, SecurityService securityParser,
                @LocalContextOnly SpringDocConfigProperties springDocConfigProperties,
                PropertyResolverUtils propertyResolverUtils,
                Optional<List<OpenApiBuilderCustomizer>> openApiBuilderCustomisers,
                Optional<List<ServerBaseUrlCustomizer>> serverBaseUrlCustomisers,
                Optional<JavadocProvider> javadocProvider) {
            return new OpenAPIService(openAPI.or(() -> Optional.of(defaultOpenAPI())), securityParser,
                    springDocConfigProperties, propertyResolverUtils, openApiBuilderCustomisers,
                    serverBaseUrlCustomisers, javadocProvider);
        }

        @Lazy
        @Bean
        SpringDocProviders springDocProviders(Optional<ActuatorProvider> actuatorProvider,
                Optional<CloudFunctionProvider> springCloudFunctionProvider,
                Optional<SecurityOAuth2Provider> springSecurityOAuth2Provider,
                Optional<RepositoryRestResourceProvider> repositoryRestResourceProvider,
                Optional<RouterFunctionProvider> routerFunctionProvider, Optional<SpringWebProvider> springWebProvider,
                // Optional<WebConversionServiceProvider> webConversionServiceProvider,
                ObjectMapperProvider objectMapperProvider) {

            // , webConversionServiceProvider
            return new SpringDocProviders(actuatorProvider, springCloudFunctionProvider, springSecurityOAuth2Provider,
                    repositoryRestResourceProvider, routerFunctionProvider, springWebProvider, objectMapperProvider);
        }

        @Lazy
        @Bean(autowireCandidate = false)
        OpenAPI defaultOpenAPI() {
            Info info = new Info().title(extContext.getContext().getId().toString());
            return new OpenAPI().info(info);
        }

        @Lazy
        // SwaggerUiConfigParameters swaggerUiConfigParameters,
        @Bean
        SwaggerWelcomeWebMvc swaggerWelcome(@LocalContextOnly SwaggerUiConfigProperties swaggerUiConfig,
                @LocalContextOnly SpringDocConfigProperties springDocConfigProperties, SpringWebProvider springWebProvider) {
            return new SwaggerWelcomeWebMvc(swaggerUiConfig, springDocConfigProperties, springWebProvider);
        }

        @Lazy
        @Bean
        SpringWebProvider springWebProvider(ApplicationContext ctx) {
            var swp = new SpringWebMvcProvider();
            swp.setApplicationContext(ctx);
            return swp;
        }

        @Lazy
        @Bean
        SwaggerConfigResource swaggerConfigResource(SwaggerWelcomeCommon swaggerWelcomeCommon) {
            return new SwaggerConfigResource(swaggerWelcomeCommon);
        }

        @Lazy
        @Bean
        SwaggerUiHome swaggerUiHome() {
            return new SwaggerUiHome();
        }

        @Lazy
        @Bean
        SwaggerIndexTransformer indexPageTransformer(@LocalContextOnly SwaggerUiConfigProperties swaggerUiConfig,
                SwaggerUiOAuthProperties swaggerUiOAuthProperties, SwaggerWelcomeCommon swaggerWelcomeCommon,
                ObjectMapperProvider objectMapperProvider) {

            return new SwaggerIndexPageTransformer(swaggerUiConfig, swaggerUiOAuthProperties, swaggerWelcomeCommon,
                    objectMapperProvider);
        }

        @Lazy
        @Bean
//        SwaggerWebMvcConfigurer swaggerWebMvcConfigurer(SwaggerUiConfigParameters swaggerUiConfigParameters,
//                SwaggerIndexTransformer swaggerIndexTransformer, Optional<ActuatorProvider> actuatorProvider) {
        SwaggerWebMvcConfigurer swaggerWebMvcConfigurer(
                @LocalContextOnly SwaggerUiConfigProperties swaggerUiConfigProperties,
                SwaggerIndexTransformer swaggerIndexTransformer, Optional<ActuatorProvider> actuatorProvider,
                SwaggerResourceResolver swaggerResourceResolver) {

            return new SwaggerWebMvcConfigurer(swaggerUiConfigProperties, swaggerIndexTransformer, actuatorProvider,
                    swaggerResourceResolver);
        }

        @Lazy
        @Bean
        ServerBaseUrlCustomizer serverBaseUrlCustomizer() {
            return (serverBaseUrl, request) -> "";
        }

    }

    public static class ExtensionWebContext {

        private final JfxAppContext context;

        private final String basePath;
        private final String internalContextPath;
        private final String externalContextPath;

        public ExtensionWebContext(JfxAppContext context, String contextPath, String servletPath) {
            super();
            this.context = context;
            this.basePath = buildBasePath(contextPath, servletPath);
            this.internalContextPath = "/" + JfxAppsPlatform.EXTENSION_REST_PATH_PREFIX + "/" + context.getId();
            this.externalContextPath = basePath + this.internalContextPath;
        }

        public JfxAppContext getContext() {
            return context;
        }

        public String getBasePath() {
            return basePath;
        }

        public String getInternalContextPath() {
            return internalContextPath;
        }

        public String getExternalContextPath() {
            return externalContextPath;
        }

        private String buildBasePath(String contextPath, String servletPath) {
            return ((StringUtils.hasText(contextPath) ? "/" + contextPath : "")
                    + (StringUtils.hasText(servletPath) ? "/" + servletPath : "")).replaceAll("/+", "/");
        }
    }

    public static class MyAsyncAnnotationBeanPostProcessor extends AsyncAnnotationBeanPostProcessor {

        private static final long serialVersionUID = 1L;

        @Override
        protected ProxyFactory prepareProxyFactory(Object bean, String beanName) {
            final var factory = super.prepareProxyFactory(bean, beanName);
            final var wrappedFactory = new ProxyFactoryWrapper(factory, bean);
            return wrappedFactory;
        }

        private class ProxyFactoryWrapper extends ProxyFactory {
            private final ProxyFactory wrappedFactory;
            private final Object bean;

            public ProxyFactoryWrapper(ProxyFactory wrappedFactory, Object bean) {
                super();
                this.wrappedFactory = wrappedFactory;
                this.bean = bean;
            }

            @Override
            public void setProxyTargetClass(boolean proxyTargetClass) {
                wrappedFactory.setProxyTargetClass(proxyTargetClass);
            }

            @Override
            public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
                wrappedFactory.setAopProxyFactory(aopProxyFactory);
            }

            @Override
            public boolean isProxyTargetClass() {
                return wrappedFactory.isProxyTargetClass();
            }

            @Override
            public AopProxyFactory getAopProxyFactory() {
                return wrappedFactory.getAopProxyFactory();
            }

            @Override
            public void addListener(AdvisedSupportListener listener) {
                wrappedFactory.addListener(listener);
            }

            @Override
            public void setOptimize(boolean optimize) {
                wrappedFactory.setOptimize(optimize);
            }

            @Override
            public void removeListener(AdvisedSupportListener listener) {
                wrappedFactory.removeListener(listener);
            }

            @Override
            public boolean isOptimize() {
                return wrappedFactory.isOptimize();
            }

            @Override
            public Object getProxy() {
                return wrappedFactory.getProxy();
            }

            @Override
            public void setOpaque(boolean opaque) {
                wrappedFactory.setOpaque(opaque);
            }

            @Override
            public boolean isOpaque() {
                return wrappedFactory.isOpaque();
            }

            @Override
            public Object getProxy(@Nullable ClassLoader classLoader) {
                return wrappedFactory.getProxy(bean.getClass().getClassLoader());
            }

            @Override
            public boolean equals(Object obj) {
                return wrappedFactory.equals(obj);
            }

            @Override
            public void setExposeProxy(boolean exposeProxy) {
                wrappedFactory.setExposeProxy(exposeProxy);
            }

            @Override
            public Class<?> getProxyClass(@Nullable ClassLoader classLoader) {
                return wrappedFactory.getProxyClass(classLoader);
            }

            @Override
            public boolean isExposeProxy() {
                return wrappedFactory.isExposeProxy();
            }

            @Override
            public void setFrozen(boolean frozen) {
                wrappedFactory.setFrozen(frozen);
            }

            @Override
            public boolean isFrozen() {
                return wrappedFactory.isFrozen();
            }

            @Override
            public void copyFrom(ProxyConfig other) {
                wrappedFactory.copyFrom(other);
            }

            @Override
            public void setTarget(Object target) {
                wrappedFactory.setTarget(target);
            }

            @Override
            public void setTargetSource(@Nullable TargetSource targetSource) {
                wrappedFactory.setTargetSource(targetSource);
            }

            @Override
            public TargetSource getTargetSource() {
                return wrappedFactory.getTargetSource();
            }

            @Override
            public void setTargetClass(@Nullable Class<?> targetClass) {
                wrappedFactory.setTargetClass(targetClass);
            }

            @Override
            public Class<?> getTargetClass() {
                return wrappedFactory.getTargetClass();
            }

            @Override
            public void setPreFiltered(boolean preFiltered) {
                wrappedFactory.setPreFiltered(preFiltered);
            }

            @Override
            public boolean isPreFiltered() {
                return wrappedFactory.isPreFiltered();
            }

            @Override
            public void setAdvisorChainFactory(AdvisorChainFactory advisorChainFactory) {
                wrappedFactory.setAdvisorChainFactory(advisorChainFactory);
            }

            @Override
            public AdvisorChainFactory getAdvisorChainFactory() {
                return wrappedFactory.getAdvisorChainFactory();
            }

            @Override
            public void setInterfaces(Class<?>... interfaces) {
                wrappedFactory.setInterfaces(interfaces);
            }

            @Override
            public void addInterface(Class<?> intf) {
                wrappedFactory.addInterface(intf);
            }

            @Override
            public boolean removeInterface(Class<?> intf) {
                return wrappedFactory.removeInterface(intf);
            }

            @Override
            public Class<?>[] getProxiedInterfaces() {
                return wrappedFactory.getProxiedInterfaces();
            }

            @Override
            public boolean isInterfaceProxied(Class<?> intf) {
                return wrappedFactory.isInterfaceProxied(intf);
            }

            @Override
            public int getAdvisorCount() {
                return wrappedFactory.getAdvisorCount();
            }

            @Override
            public void addAdvisor(Advisor advisor) {
                wrappedFactory.addAdvisor(advisor);
            }

            @Override
            public void addAdvisor(int pos, Advisor advisor) throws AopConfigException {
                wrappedFactory.addAdvisor(pos, advisor);
            }

            @Override
            public boolean removeAdvisor(Advisor advisor) {
                return wrappedFactory.removeAdvisor(advisor);
            }

            @Override
            public void removeAdvisor(int index) throws AopConfigException {
                wrappedFactory.removeAdvisor(index);
            }

            @Override
            public int indexOf(Advisor advisor) {
                return wrappedFactory.indexOf(advisor);
            }

            @Override
            public boolean replaceAdvisor(Advisor a, Advisor b) throws AopConfigException {
                return wrappedFactory.replaceAdvisor(a, b);
            }

            @Override
            public void addAdvisors(Advisor... advisors) {
                wrappedFactory.addAdvisors(advisors);
            }

            @Override
            public void addAdvisors(Collection<Advisor> advisors) {
                wrappedFactory.addAdvisors(advisors);
            }

            @Override
            public void addAdvice(Advice advice) throws AopConfigException {
                wrappedFactory.addAdvice(advice);
            }

            @Override
            public void addAdvice(int pos, Advice advice) throws AopConfigException {
                wrappedFactory.addAdvice(pos, advice);
            }

            @Override
            public boolean removeAdvice(Advice advice) throws AopConfigException {
                return wrappedFactory.removeAdvice(advice);
            }

            @Override
            public int indexOf(Advice advice) {
                return wrappedFactory.indexOf(advice);
            }

            @Override
            public boolean adviceIncluded(@Nullable Advice advice) {
                return wrappedFactory.adviceIncluded(advice);
            }

            @Override
            public int countAdvicesOfType(@Nullable Class<?> adviceClass) {
                return wrappedFactory.countAdvicesOfType(adviceClass);
            }

            @Override
            public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method,
                    @Nullable Class<?> targetClass) {
                return wrappedFactory.getInterceptorsAndDynamicInterceptionAdvice(method, targetClass);
            }

            @Override
            public String toProxyConfigString() {
                return wrappedFactory.toProxyConfigString();
            }

        }
    }
}
