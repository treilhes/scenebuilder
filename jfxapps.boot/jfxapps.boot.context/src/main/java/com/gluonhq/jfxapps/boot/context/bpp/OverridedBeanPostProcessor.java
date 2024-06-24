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
package com.gluonhq.jfxapps.boot.context.bpp;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.lang.Nullable;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.context.annotation.OverrideBean;
import com.gluonhq.jfxapps.boot.context.annotation.OverridedBeanAware;
import com.gluonhq.jfxapps.boot.context.annotation.Singleton;

/**
 * BeanPostProcessor implementation that handles the OverrideBean annotation.
 * This processor creates proxies for beans that have overridden methods defined
 * by classes annotated with OverrideBean.
 */
@Singleton
public class OverridedBeanPostProcessor implements BeanPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(OverridedBeanPostProcessor.class);

    private final JfxAppContext context;
    private List<Class<?>> classes;

    /**
     * Constructor for OverridedBeanPostProcessor.
     *
     * @param context the application context
     */
    public OverridedBeanPostProcessor(JfxAppContext context) {
        super();
        this.context = context;
    }

    /**
     * Post-process the given bean instance after its initialization.
     *
     * @param bean     the new bean instance
     * @param beanName the name of the bean
     * @return the bean instance to use, either the original or a wrapped one
     * @throws BeansException in case of errors
     */
    @Override
    @Nullable
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // Initialize the list of override classes if not already done
        if (classes == null) {
            classes = context.getBeansWithAnnotation(OverrideBean.class).values().stream().map(Object::getClass)
                    .collect(Collectors.toList());
        }

        var beanClass = bean.getClass();
        var overrides = classes.stream()
                .filter(onlyApplicableOverrides(beanName, beanClass))
                .sorted(orderByOrderAnnotation()).toList();

        if (overrides.isEmpty()) {
            return bean;
        }

        Map<Method, Method> methodMap = mapOverridedMethods(beanClass, overrides);

        // Log all methods of the bean and their overrides if available
        logResultMethodMap(beanClass, methodMap);

        if (methodMap.isEmpty()) {
            return bean;
        }

        ProxyFactory proxyFactory = createMappedProxy(bean, methodMap);

        return proxyFactory.getProxy();
    }

    /**
     * Create a proxy factory for the given bean and method map.
     *
     * @param bean      the original bean
     * @param methodMap the map of original methods to override methods
     * @return the proxy factory
     */
    private ProxyFactory createMappedProxy(Object bean, Map<Method, Method> methodMap) {
        ProxyFactory proxyFactory = new ProxyFactory(bean);
        proxyFactory.addAdvice((org.aopalliance.intercept.MethodInterceptor) invocation -> {
            Method method = invocation.getMethod();
            Method overrideMethod = methodMap.get(method);
            if (overrideMethod != null) {
                Object overrideBean = context.getBean(overrideMethod.getDeclaringClass());

                if (OverridedBeanAware.class.isAssignableFrom(overrideBean.getClass())) {
                    ((OverridedBeanAware)overrideBean).setOverridedBean(bean);
                }

                return overrideMethod.invoke(overrideBean, invocation.getArguments());
            }
            return invocation.proceed();
        });
        return proxyFactory;
    }

    /**
     * Log the result method map, indicating which methods have overrides.
     *
     * @param beanClass the class of the bean
     * @param methodMap the map of original methods to override methods
     */
    private void logResultMethodMap(Class<?> beanClass, Map<Method, Method> methodMap) {
        if (logger.isDebugEnabled()) {
            for (Method method : beanClass.getMethods()) {
                Method overrideMethod = methodMap.get(method);
                if (overrideMethod != null) {
                    logger.debug("Method {} -> {}.{}", method, overrideMethod.getDeclaringClass().getName(),
                            overrideMethod.getName());
                } else {
                    logger.debug("Method {} has no override", method);
                }
            }
        }
    }

    /**
     * Map the overridden methods from the override classes to the bean class
     * methods.
     *
     * @param beanClass the class of the bean
     * @param overrides the list of override classes
     * @return a map of original methods to override methods
     */
    private Map<Method, Method> mapOverridedMethods(Class<?> beanClass, List<Class<?>> overrides) {
        Map<Method, Method> methodMap = new HashMap<>();
        for (var cls : overrides) {
            for (Method overrideMethod : cls.getMethods()) {
                if (overrideMethod.getDeclaringClass() != cls) {
                    // no need to process methods from super classes
                    continue;
                }
                try {
                    Method originalMethod = beanClass.getMethod(overrideMethod.getName(),
                            overrideMethod.getParameterTypes());
                    methodMap.put(originalMethod, overrideMethod);
                } catch (NoSuchMethodException e) {
                    // No matching method found, ignore
                }
            }
        }
        return methodMap;
    }

    /**
     * Create a predicate to filter only the applicable overrides based on the bean
     * name and class.
     *
     * @param beanName  the name of the bean
     * @param beanClass the class of the bean
     * @return a predicate for filtering override classes
     */
    private Predicate<? super Class<?>> onlyApplicableOverrides(String beanName, Class<?> beanClass) {
        return c -> {
            OverrideBean annotation = c.getAnnotation(OverrideBean.class);
            return annotation.value().isAssignableFrom(beanClass)
                    && (annotation.qualifier().isEmpty() || annotation.qualifier().equals(beanName));
        };
    }

    /**
     * Create a comparator to sort override classes by the @Order annotation.
     *
     * @return a comparator for sorting override classes
     */
    @SuppressWarnings("null")
    private Comparator<? super Class<?>> orderByOrderAnnotation() {
        return (c1, c2) -> {
            Order o1 = AnnotationUtils.findAnnotation(c1, Order.class);
            Order o2 = AnnotationUtils.findAnnotation(c2, Order.class);
            int order1 = (o1 != null) ? o1.value() : Ordered.LOWEST_PRECEDENCE;
            int order2 = (o2 != null) ? o2.value() : Ordered.LOWEST_PRECEDENCE;
            return Integer.compare(order1, order2);
        };
    }
}
