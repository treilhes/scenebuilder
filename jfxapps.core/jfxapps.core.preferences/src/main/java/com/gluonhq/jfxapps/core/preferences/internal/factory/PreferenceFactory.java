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
package com.gluonhq.jfxapps.core.preferences.internal.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationInstanceSingleton;
import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.preference.DefaultValueProvider;
import com.gluonhq.jfxapps.core.api.preference.JsonMapper;
import com.gluonhq.jfxapps.core.api.preference.PreferenceContext;
import com.gluonhq.jfxapps.core.api.preference.PreferenceEditorFactory;
import com.gluonhq.jfxapps.core.api.preference.UserPreference;
import com.gluonhq.jfxapps.core.api.preference.ValueValidator;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.ApplicationPreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.GlobalPreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.behaviour.InstancePreferenceBehaviour;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceMetadata;
import com.gluonhq.jfxapps.core.preferences.repository.PreferenceRepository;

public class PreferenceFactory implements BeanClassLoaderAware, BeanFactoryAware, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(PreferenceFactory.class);

    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private PreferenceMetadata preferenceMetadata;

    private ApplicationContext context;

    /**
     * Creates a new {@link PreferenceFactory}.
     */
    public PreferenceFactory() {
        this.classLoader = org.springframework.util.ClassUtils.getDefaultClassLoader();
    }

    protected final BasePreference<?> getTargetPreference(PreferenceMetadata metadata) {

        var preferenceInterface = metadata.getPreferenceInterface();

        boolean isEditable = UserPreference.class.isAssignableFrom(preferenceInterface);

        var jfxAppContext = (JfxAppContext) context;

        var preferenceRepository = jfxAppContext.getLayerBean(this.getClass(), PreferenceRepository.class);

        var defaultEditorFactory = isEditable ? jfxAppContext.getBean(PreferenceEditorFactory.class) : null;

        var id = metadata.getId();
        var name = metadata.getName();
        var defaultValueProviderClass = metadata.getDefaultValueProviderClass();
        var valueValidatorClass = metadata.getValueValidatorClass();
        var jsonMapperClass = metadata.getJsonMapperClass();
        var preferenceType = metadata.getGenericTypeInformation();
        var scope = metadata.getScope();
        var dataClass = preferenceType.getType();

        DefaultValueProvider<?> defaultValueProvider = null;
        try {
            defaultValueProvider = defaultValueProviderClass != PreferenceContext.NoOpDefaultValueProvider.class //
                    ? defaultValueProviderClass.getDeclaredConstructor().newInstance()
                    : () -> null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create DefaultValueProvider", e);
        }

        ValueValidator<?> valueValidator = null;
        try {
            valueValidator = valueValidatorClass != PreferenceContext.NoOpValueValidator.class //
                    ? valueValidatorClass.getDeclaredConstructor().newInstance()
                    : (v) -> true;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create ValueValidator", e);
        }

        JsonMapper<?> jsonMapper = null;
        try {
            jsonMapper = jsonMapperClass != PreferenceContext.NoOpJsonMapper.class //
                    ? jsonMapperClass.getDeclaredConstructor().newInstance()
                    : null;
        } catch (Exception e) {
            throw new IllegalArgumentException("Unable to create ValueValidator", e);
        }

        var behaviourClass = switch (scope) {

        case DefaultListableBeanFactory.SCOPE_SINGLETON ->
            new GlobalPreferenceBehaviour(metadata, preferenceRepository);

        case ApplicationSingleton.SCOPE_NAME -> new ApplicationPreferenceBehaviour(metadata, preferenceRepository,
                beanFactory.getBean(ApplicationEvents.class));

        case ApplicationInstanceSingleton.SCOPE_NAME -> new InstancePreferenceBehaviour(metadata, preferenceRepository,
                beanFactory.getBean(ApplicationEvents.class), beanFactory.getBean(ApplicationInstanceEvents.class));

        default -> throw new IllegalArgumentException("Unexpected value: " + scope);
        };

        var preference = new BasePreference(jfxAppContext, id, name, dataClass, defaultValueProvider, valueValidator,
                behaviourClass, defaultEditorFactory, jsonMapper);

        return preference;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader == null ? org.springframework.util.ClassUtils.getDefaultClassLoader()
                : classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    /**
     * Returns a preference instance for the given interface backed by an instance
     * providing implementation logic
     */
    @SuppressWarnings({ "unchecked" })
    public <T> T getPreference(Class<T> preferenceInterface) {

        if (logger.isDebugEnabled()) {
            logger.debug("Initializing preference instance for {}", preferenceInterface.getName());
        }

        Assert.notNull(preferenceInterface, "Preference interface must not be null");

        var target = getTargetPreference(preferenceMetadata);

        // Create proxy
        ProxyFactory result = new ProxyFactory();
        result.setTarget(target);
        result.setInterfaces(preferenceInterface);

        // TODO: check if this realy needed
        result.addAdvisor(ExposeInvocationInterceptor.ADVISOR);

        result.addAdvice(new ImplementationInterceptor(target));

        T preference = (T) result.getProxy(preferenceInterface.getClassLoader());

        if (logger.isDebugEnabled()) {
            logger.debug("Finished creation of preference instance for {}.", preferenceInterface.getName());
        }

        return preference;
    }

    /**
     * Method interceptor that calls methods on the {@link BasePreference}.
     */
    static class ImplementationInterceptor implements MethodInterceptor {

        private final BasePreference<?> base;

        public ImplementationInterceptor(BasePreference<?> base) {
            this.base = base;
        }

        @Nullable
        @Override
        public Object invoke(@SuppressWarnings("null") MethodInvocation invocation) throws Throwable {

            Method method = invocation.getMethod();
            Object[] arguments = invocation.getArguments();

            try {
                return method.invoke(base, arguments);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    throw ((InvocationTargetException) e).getTargetException();
                }
                throw e;
            }
        }
    }

    public void setPreferenceMetadata(PreferenceMetadata preferenceMetadata) {
        this.preferenceMetadata = preferenceMetadata;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}
