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
import java.util.ArrayList;
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
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.AutowireCandidateResolver;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.expression.EvaluationException;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import com.gluonhq.jfxapps.boot.context.DocumentScope;
import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.MultipleProgressListener;
import com.gluonhq.jfxapps.boot.context.annotation.LocalContextOnly;
import com.gluonhq.jfxapps.boot.context.internal.ContextProgressHandler;


public class JfxAppContextImpl implements JfxAppContext  {

    private JfxAppContext parent;

    private final AnnotationConfigServletWebApplicationContext context;
    private final SbBeanFactoryImpl beanFactory;
    private final UUID id;

    private Class<?>[] registeredClasses;

    public JfxAppContextImpl(UUID id) {
        this(id, null);
    }

    public JfxAppContextImpl(UUID contextId, ClassLoader loader) {
        this.id = contextId;
        this.beanFactory = new SbBeanFactoryImpl();
        this.context = new AnnotationConfigServletWebApplicationContext(this.beanFactory);
        this.context.setId(id.toString());
        this.context.setClassLoader(loader);
        this.context.setAllowBeanDefinitionOverriding(true);

        registerSingleton(this);
    }

    protected void setParent(JfxAppContextImpl parent) {
        this.parent = parent;
        this.setParent(parent.context);
    }

    protected void setParent(ApplicationContext parent) {
        if (parent != null) {
            this.context.setParent(parent);
            this.context.setServletContext(((GenericWebApplicationContext) parent).getServletContext());
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
    public <T> void registerBean(Class<T> beanClass, Supplier<T> tSupplier) {
        this.context.registerBean(beanClass, tSupplier);
    }
    @Override
    public void addProgressListener(MultipleProgressListener progressListener) {
        ContextProgressHandler progressHandler = new ContextProgressHandler(id, progressListener);
        context.addApplicationListener(progressHandler);
        context.addBeanFactoryPostProcessor(progressHandler);
    }

    @Override
    public void register(Class<?>[] classes) {
        registeredClasses = classes;
        context.register(classes);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return context.getBeanDefinitionNames();
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> cls) {
        return context.getBeansOfType(cls);
    }

    @Override
    public void refresh() {
        context.refresh();
    }

    @Override
    public void start() {
        context.start();
    }

    @Override
    public int getBeanDefinitionCount() {
        return context.getBeanDefinitionCount();
    }

    @Override
    public boolean isRunning() {
        return context.isRunning();
    }

    @Override
    public boolean isActive() {
        return context.isActive();
    }

    @Override
    public UUID getId() {
        return id;
    }

    public void close() {
        context.close();
    }

    @Override
    public <T> T getBean(Class<T> cls) {
        return context.getBean(cls);
    }

    @Override
    public <T> T getLocalBean(Class<T> cls) {

        if (parent == null) {
            return context.getBean(cls);
        }

        Map<String, T> globalMap = context.getBeansOfType(cls);
        Map<String, T> parentMap = parent.getBeansOfType(cls);

        Set<T> result = new HashSet<>(globalMap.values());
        result.removeAll(parentMap.values());

        if (result.size() == 1) {
            return result.iterator().next();
        } else {
            throw new NoUniqueBeanDefinitionException(cls, result.size(), "No unique bean found");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getBean(String beanName) {
        return (T)context.getBean(beanName);
    }

    @Override
    public String[] getBeanNamesForType(Class<?> cls, Class<?> genericClass) {
        ResolvableType resolvable = ResolvableType.forClassWithGenerics(cls, genericClass);
        return context.getBeanNamesForType(resolvable);
    }


    @Override
    public JfxAppContext getParent() {
        return parent;
    }


    @Override
    public Object parseExpression(String spelExpression, Object rootContext) {
        try {
            StandardEvaluationContext  stContext  = new StandardEvaluationContext(rootContext);
            SpelExpressionParser parser = new SpelExpressionParser();
            return parser.parseRaw(spelExpression).getValue(stContext);
        } catch (EvaluationException e) {
            return e.getMessage();
        }
    }

    @Override
    public boolean isExpression(String spelExpression) {
        return spelExpression != null ? spelExpression.startsWith("#") : false;
    }

    @Override
    public boolean isDocumentScope(Class<?> cls) {
        String[] names = context.getBeanNamesForType(cls);

        if (names.length == 0) {
            return false;
        }
        BeanDefinition definition = context.getBeanDefinition(names[0]);
        return DocumentScope.SCOPE_NAME.equals(definition.getScope());
    }

    @Override
    public void publishEvent(ApplicationEvent event) {
        this.context.publishEvent(event);
    }

    @Override
    public List<Class<?>> getBeanClassesForAnnotation(Class<? extends Annotation> annotationType) {
        return Arrays.stream(context.getBeanNamesForAnnotation(annotationType))
            .map(context::getType)
            .collect(Collectors.toList());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<Class<T>> getBeanClassesForType(Class<T> cls) {
        return Arrays.stream(context.getBeanNamesForType(cls))
            .map(n -> (Class<T>)context.getType(n))
            .collect(Collectors.toList());
    }

    @Override
    public Object getBean(String string, Object... parameters) {
        return context.getBean(string, parameters);
    }

    @Override
    public <T> T getBean(Class<T> cls, Object... parameters) {
        return context.getBean(cls, parameters);
    }

    @Override
    public Class<?>[] getRegisteredClasses() {
        return registeredClasses;
    }

    @Override
    public <T, G, U> Map<String, U> getBeansOfTypeWithGeneric(Class<T> cls, Class<G> generic) {
        String[] beanNamesForType = getBeanNamesForType(cls, generic);
        Map<String, U> map = new HashMap<>();
        if (beanNamesForType.length > 0) {
            Arrays.asList(beanNamesForType).forEach(n -> map.put(n, (U)getBean(n)));
        }
        return map;
    }

    @Override
    public ClassLoader getBeanClassLoader() {
        return beanFactory.getBeanClassLoader();
    }

    public class SbBeanFactoryImpl extends DefaultListableBeanFactory {

        public SbBeanFactoryImpl() {
            DocumentScope scope = new DocumentScope(this);
            registerScope(DocumentScope.SCOPE_NAME, scope);
            //addBeanPostProcessor(new FxmlControllerBeanPostProcessor());
            setAutowireCandidateResolver(new SbContextAnnotationAutowireCandidateResolver());
            //registerSingleton(DocumentScope.class.getName(), scope);
        }

        @Override
        protected Object createBean(String beanName, RootBeanDefinition mbd, Object[] args) throws BeanCreationException {
            Class<?> rawClass = mbd.getResolvableType().getRawClass();
            try {
                return super.createBean(beanName, mbd, args);
            } catch (Exception e) {
                if (e instanceof BeanCreationException) {
                    rawClass.getModule().addOpens(rawClass.getPackage().getName(), BeanUtils.class.getModule());
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
        protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor,
                AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {

            LocalContextOnly localContextOnly = descriptor.getAnnotation(LocalContextOnly.class);

            if (localContextOnly != null) {
                String bdName = BeanFactoryUtils.transformedBeanName(beanName);
                if (containsBeanDefinition(bdName)) {
                    return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(bdName), descriptor, resolver);
                }
                else if (containsSingleton(beanName)) {
                    return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor, resolver);
                }
                return false;
            }

            return super.isAutowireCandidate(beanName, descriptor, resolver);
        }



        private class SbContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {


            @Override
            public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
                if (super.isAutowireCandidate(bdHolder, descriptor)) {
                    return checkLocalOnly(bdHolder, descriptor);
                }
                return false;
            }

            private boolean checkLocalOnly(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
                LocalContextOnly targetAnn = descriptor.getAnnotation(LocalContextOnly.class);
                if (targetAnn != null) {
                    BeanDefinition bd = bdHolder.getBeanDefinition();
                    String beanName = bdHolder.getBeanName();
                    String ctxId = JfxAppContextImpl.this.getId().toString();
                    BeanDefinition mlbd = null;
                    BeanDefinition mbd = null;
                    try {
                        mlbd = beanFactory.getMergedLocalBeanDefinition(bdHolder.getBeanName());
                    } catch (BeansException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    try {
                        mbd = beanFactory.getMergedBeanDefinition(bdHolder.getBeanName());
                    } catch (BeansException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    Object source = bd.getSource();
                    System.out.println();
//                    context.
//                    String[] candidateTags = null;
//
//                    if (source instanceof AnnotatedTypeMetadata) { // source is not null when a candidate was created with @Bean annotation
//                        Map<String, Object> attributes = ((AnnotatedTypeMetadata) source).getAnnotationAttributes(Tags.class.getName());
//                        if (attributes != null) {
//                            candidateTags = (String[]) attributes.get("value");
//                        }
//                    } else {
//                        ResolvableType candidateType = ((RootBeanDefinition) bd).getResolvableType();
//                        if (candidateType != null) { // candidateType is not null when candidate was created with @Component like annotation
//                            Class<?> candidateClass = candidateType.resolve();
//                            if (candidateClass != null) {
//                                Tags tagsAnn = candidateClass.getAnnotation(Tags.class);
//                                if (tagsAnn != null) {
//                                    candidateTags = tagsAnn.value();
//                                }
//                            }
//                        }
//                    }
//
//                    if (candidateTags != null) {
//                        List<String> targetTags = new ArrayList<>(Arrays.asList(targetAnn.value()));
//                        targetTags.retainAll(Arrays.asList(candidateTags));
//                        return !targetTags.isEmpty();
//                    } else {
//                        // If a candidate doesn't have @Tags annotation then it's not a suitable candidate
//                        return false;
//                    }
                }
                // If target doesn't have @LocalOnly annotation then return 'true' as super.isAutowireCandidate() does.
                return true;
            }

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
                return pf.getProxy(getBeanClassLoader());
            }

        }



    }
}
