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
package com.gluonhq.jfxapps.boot.context.impl;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.MapPropertySource;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.lang.Nullable;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.gluonhq.jfxapps.boot.api.context.Application;
import com.gluonhq.jfxapps.boot.api.context.ApplicationInstance;
import com.gluonhq.jfxapps.boot.api.context.ContextManager;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.api.context.ScopedExecutor;
import com.gluonhq.jfxapps.boot.api.context.annotation.LayerContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.LocalContextOnly;
import com.gluonhq.jfxapps.boot.context.internal.ContextProgressHandler;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationInstanceScope;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationInstanceScopeHolder;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationScope;
import com.gluonhq.jfxapps.boot.context.scope.ApplicationScopeHolder;

public class JfxAppContextImpl extends JfxAnnotationConfigServletWebApplicationContext implements JfxAppContext {

    /** The scope holder */
    public static final ApplicationScopeHolder applicationScope = new ApplicationScopeHolder();

    /** The scope holder */
    public static final ApplicationInstanceScopeHolder applicationInstanceScope = new ApplicationInstanceScopeHolder(applicationScope);

    // private final AnnotationConfigServletWebApplicationContext context;
    private final SbBeanFactoryImpl beanFactory;
    private final UUID id;
    private final Map<String, Class<?>> registeredClasses = new HashMap<>();
    private final Set<Class<?>> deportedClasses = new HashSet<>();


    public static JfxAppContext fromScratch(String[] propertySourceProperties, Class<?>[] array) {

        JfxAppContextImpl ctx = new JfxAppContextImpl(UUID.randomUUID());

        if (array != null) {
            ctx.getEnvironment().getPropertySources().addLast(toMapSource(propertySourceProperties));
        }

        ctx.register(array);
        ctx.refresh();
        ctx.start();
        return ctx;
    }
    public static JfxAppContext fromScratch(Class<?>... array) {
        return fromScratch(new String[0], array);
    }

    private static MapPropertySource toMapSource(String[] array) {
        Map<String, Object> propertyMap = new HashMap<>();

        // Convert the String array to a Map
        for (String pair : array) {
            if (pair.contains("=")) {
                String[] keyValue = pair.split("=", 2);
                propertyMap.put(keyValue[0], keyValue[1]);
            }
        }

        // Create a MapPropertySource
        return new MapPropertySource("customMapProperties", propertyMap);
    }
    public JfxAppContextImpl(UUID id) {
        this(id, null);
    }

    public JfxAppContextImpl(UUID contextId, ClassLoader loader) {
        super(new SbBeanFactoryImpl());

        this.id = contextId;
        this.beanFactory = (SbBeanFactoryImpl) getBeanFactory();

        this.setClassLoader(loader);
        this.setAllowBeanDefinitionOverriding(true);
        this.setId(contextId.toString());

        // registerSingleton(this);
    }

    @Override
    public UUID getUuid() {
        return id;
    }

    @Override
    public void setParent(ApplicationContext parent) {
        if (parent != null) {
            super.setParent(parent);
            this.setServletContext(((GenericWebApplicationContext) parent).getServletContext());
        }
    }

    public void registerSingleton(Object singletonObject) {
        // Register the singleton instance with a generated name
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(singletonObject.getClass());
        String name = BeanDefinitionReaderUtils.generateBeanName(beanDefinition, beanFactory);
        this.beanFactory.registerSingleton(name, singletonObject);
    }

    @Override
    public void addProgressListener(MultipleProgressListener progressListener) {
        ContextProgressHandler progressHandler = new ContextProgressHandler(UUID.fromString(getId()), progressListener);
        addApplicationListener(progressHandler);
        addBeanFactoryPostProcessor(progressHandler);
    }

    @Override
    public void register(Class<?>... classes) {
        for (Class<?> cls : classes) {
            registeredClasses.put(cls.getName(), cls);
        }
        super.register(classes);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getLocalBean(Class<T> cls) {

        if (getParent() == null) {
            return getBean(cls);
        }

        Map<String, T> globalMap = getBeansOfType(cls);
        Map<String, T> parentMap = getParent().getBeansOfType(cls);

        Set<T> result = new HashSet<>(globalMap.values());
        result.removeAll(parentMap.values());

        if (result.size() == 1) {
            return result.iterator().next();
        } else {
            throw new NoUniqueBeanDefinitionException(cls, result.size(), "No unique bean found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> T getLayerBean(Class<?> layerClass, Class<T> cls) {

        var moduleLayer = layerClass.getModule().getLayer();
        var contextManager = getBean(ContextManager.class);
        var layerContext = contextManager.get(moduleLayer);

        if (layerContext == null) {
            return getBean(cls);
        }

        return layerContext.getBean(cls);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> cls, Class<?> genericClass) {
        ResolvableType resolvable = ResolvableType.forClassWithGenerics(cls, genericClass);
        return getBeanNamesForType(resolvable);
    }

    @Override
    public Object parseExpression(String spelExpression, Object rootContext) {
        try {
            StandardEvaluationContext stContext = new StandardEvaluationContext(rootContext);
            SpelExpressionParser parser = new SpelExpressionParser();
            return parser.parseRaw(spelExpression).getValue(stContext);
        } catch (EvaluationException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean isExpression(String spelExpression) {
        return spelExpression != null && spelExpression.startsWith("#");
    }

    @Override
    public boolean isApplicationScope(Class<?> cls) {
        String[] names = getBeanNamesForType(cls);

        if (names.length == 0) {
            return false;
        }
        BeanDefinition definition = getBeanDefinition(names[0]);
        return ApplicationInstanceScope.SCOPE_NAME.equals(definition.getScope());
    }

    @Override
    public boolean isApplicationInstanceScope(Class<?> cls) {
        String[] names = getBeanNamesForType(cls);

        if (names.length == 0) {
            return false;
        }
        BeanDefinition definition = getBeanDefinition(names[0]);
        return ApplicationInstanceScope.SCOPE_NAME.equals(definition.getScope());
    }

    @Override
    public List<Class<?>> getBeanClassesForAnnotation(Class<? extends Annotation> annotationType) {
        return Arrays.stream(getBeanNamesForAnnotation(annotationType)).map(this::getType).collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<Class<T>> getBeanClassesForType(Class<T> cls) {
        return Arrays.stream(getBeanNamesForType(cls)).map(n -> (Class<T>) getType(n)).collect(Collectors.toList());
    }

    @Override
    public Set<Class<?>> getRegisteredClasses() {
        return new HashSet<>(registeredClasses.values());
    }

    @Override
    public Class<?> getRegisteredClass(String name) {
        return registeredClasses.get(name);
    }

    @Override
    public <T, G, U> Map<String, U> getBeansOfTypeWithGeneric(Class<T> cls, Class<G> generic) {
        String[] beanNamesForType = getBeanNamesForType(cls, generic);
        Map<String, U> map = new HashMap<>();
        if (beanNamesForType.length > 0) {
            Arrays.asList(beanNamesForType).forEach(n -> map.put(n, (U) getBean(n)));
        }
        return map;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return beanFactory.getBeanClassLoader();
    }

    @Override
    public void destroyBean(Object existingBean) {
        beanFactory.destroyBean(existingBean);
    }

    @Override
    public void destroyScopedBean(String beanName) {
        beanFactory.destroyScopedBean(beanName);
    }

    @Override
    public void close() {
        beanFactory.cleanScopedBeans();
        super.close();
    }

    public static class SbBeanFactoryImpl extends DefaultListableBeanFactory {

        private final ApplicationScope applicationScope;
        private final ApplicationInstanceScope applicationInstanceScope;

        public SbBeanFactoryImpl() {
            super();

            this.applicationScope = new ApplicationScope(this, JfxAppContextImpl.applicationScope);
            this.applicationInstanceScope = new ApplicationInstanceScope(this, JfxAppContextImpl.applicationInstanceScope);

            registerScope(ApplicationScope.SCOPE_NAME, this.applicationScope);
            registerScope(ApplicationInstanceScope.SCOPE_NAME, this.applicationInstanceScope);

            // addBeanPostProcessor(new FxmlControllerBeanPostProcessor());
            setAutowireCandidateResolver(new SbContextAnnotationAutowireCandidateResolver());
        }

        public void cleanScopedBeans() {
            var applicationHolders = applicationScope.getAllContext().stream()
                    .filter(c -> c.getScopeHolder() == applicationScope).toList();

            applicationHolders.forEach(h -> JfxAppContextImpl.applicationScope.removeScope(h.getScopedObject()));

            var applicationInstanceHolders = applicationInstanceScope.getAllContext().stream()
                    .filter(c -> c.getScopeHolder() == applicationInstanceScope).toList();
            applicationInstanceHolders.forEach(h -> JfxAppContextImpl.applicationInstanceScope.removeScope(h.getScopedObject()));
        }

        @Override
        protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args)
                throws BeanCreationException {
            Class<?> rawClass = mbd.getResolvableType().getRawClass();
            try {
                return super.createBean(beanName, mbd, args);
            } catch (Exception e) {
                if (e instanceof BeanCreationException) {
                    try {
                        rawClass.getModule().addOpens(rawClass.getPackage().getName(), BeanUtils.class.getModule());
                    } catch (Exception e1) {
                        throw e;
                    }
                    return super.createBean(beanName, mbd, args);

                } else {
                    throw e;
                }
            }
        }

//        @Override
//        public BeanFactory getParentBeanFactory() {
//            DefaultListableBeanFactory parent = (DefaultListableBeanFactory)super.getParentBeanFactory();
        //
//            if (parent == null) {
//                return null;
//            }
        //
//            ChildFirstBeanFactoryWrapper bf = new ChildFirstBeanFactoryWrapper(parent, this);
//            return bf;
//        }


        @Override
        protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType,
                DependencyDescriptor descriptor) {

            var layerContext = descriptor.getAnnotation(LayerContext.class);

            if (layerContext != null) {
                var resolvable = descriptor.getResolvableType();
                var beanClass = resolvable.getRawClass();

                if (beanClass.getClassLoader() != getBeanClassLoader()) {
                    var moduleLayer = beanClass.getModule().getLayer();
                    var contextManager = getBean(ContextManager.class);
                    var targetContext = contextManager.get(moduleLayer);

                    if (targetContext != null && targetContext.getBeanFactory() instanceof JfxAppContextImpl.SbBeanFactoryImpl beanFactory) {
                        return beanFactory.findAutowireCandidates(beanName, requiredType, descriptor);
                    }
                }
            }
            return super.findAutowireCandidates(beanName, requiredType, descriptor);
        }

        @Override
        protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor,
                AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {

            LocalContextOnly localContextOnly = descriptor.getAnnotation(LocalContextOnly.class);

            if (localContextOnly != null) {
                String bdName = BeanFactoryUtils.transformedBeanName(beanName);
                if (containsBeanDefinition(bdName)) {
                    return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
                } else if (containsSingleton(beanName)) {
                    return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor,
                            resolver);
                }
                return false;
            }

            return super.isAutowireCandidate(beanName, descriptor, resolver);
        }

        private class SbContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {

            @Override
            protected Object buildLazyResolutionProxy(DependencyDescriptor descriptor, String beanName) {
                TargetSource ts = new TargetSource() {
                    private Object savedTarget = null;

                    @Override
                    public Class<?> getTargetClass() {
                        return descriptor.getDependencyType();
                    }

                    @Override
                    public boolean isStatic() {
                        return false;
                    }

                    @Override
                    public Object getTarget() {
                        if (savedTarget != null) {
                            return savedTarget;
                        }
                        Set<String> autowiredBeanNames = (beanName != null ? new LinkedHashSet<>(1) : null);
                        Object target = doResolveDependency(descriptor, beanName, autowiredBeanNames, null);
                        if (target == null) {
                            Class<?> type = getTargetClass();
                            if (Map.class == type) {
                                return Collections.emptyMap();
                            } else if (List.class == type) {
                                return Collections.emptyList();
                            } else if (Set.class == type || Collection.class == type) {
                                return Collections.emptySet();
                            }
                            throw new NoSuchBeanDefinitionException(descriptor.getResolvableType(),
                                    "Optional dependency not present for lazy injection point");
                        }
                        if (autowiredBeanNames != null) {
                            for (String autowiredBeanName : autowiredBeanNames) {
                                if (containsBean(autowiredBeanName)) {
                                    registerDependentBean(autowiredBeanName, beanName);
                                }
                            }
                        }
                        savedTarget = target;
                        return target;
                    }

                    @Override
                    public void releaseTarget(Object target) {
                    }
                };
                ProxyFactory pf = new ProxyFactory();
                pf.setTargetSource(ts);
                Class<?> dependencyType = descriptor.getDependencyType();
                if (dependencyType.isInterface()) {
                    pf.addInterface(dependencyType);
                }
                return pf.getProxy(dependencyType.getClassLoader());
            }

        }

        public ApplicationScope getApplicationScope() {
            return applicationScope;
        }

        public ApplicationInstanceScope getApplicationInstanceScope() {
            return applicationInstanceScope;
        }

    }

    @Override
    public <T> void registerBean(Class<T> cls, Supplier<T> supplier) {
        super.registerBean(cls, supplier);
    }

    @Override
    public Set<Class<?>> getDeportedClasses() {
        return deportedClasses;
    }

    public void deport(Class<?>... deportedClasses) {
        this.deportedClasses.addAll(Arrays.asList(deportedClasses));
    }

    @Override
    public ScopedExecutor<Application> getApplicationExecutor() {
        return JfxAppContextImpl.applicationScope;
    }

    @Override
    public ScopedExecutor<ApplicationInstance> getApplicationInstanceExecutor() {
        return JfxAppContextImpl.applicationInstanceScope;
    }


}
