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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.util.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

import com.gluonhq.jfxapps.core.api.preference.Preference;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceMetadata;

/**
 * Implementation of {@link org.springframework.beans.factory.FactoryBean} interface to create
 * preference factories
 *
 * @param <T> the type of the preference
 */
public class PreferenceFactoryBean<T extends Preference<U>, U> implements InitializingBean, FactoryBean<T>, BeanClassLoaderAware,
    BeanFactoryAware, ApplicationContextAware {

    private final Class<? extends T> preferenceInterface;
    private final PreferenceMetadata preferenceMetadata;

    private PreferenceFactory factory;
    private ClassLoader classLoader;
    private BeanFactory beanFactory;
    private boolean lazyInit = false;

    private Lazy<T> preference;
    private ApplicationContext context;




    /**
     * Creates a new {@link PreferenceFactoryBean} for the given preference interface.
     *
     * @param preferenceInterface must not be {@literal null}.
     */
    public PreferenceFactoryBean(Class<? extends T> preferenceInterface) {
        Assert.notNull(preferenceInterface, "Preference interface must not be null");
        this.preferenceInterface = preferenceInterface;
        this.preferenceMetadata = PreferenceMetadata.getMetadata(preferenceInterface);
    }


    protected PreferenceFactory createPreferenceFactory() {
        PreferenceFactory preferenceFactory = new PreferenceFactory();
        preferenceFactory.setBeanClassLoader(classLoader);
        preferenceFactory.setBeanFactory(beanFactory);
        preferenceFactory.setApplicationContext(context);
        preferenceFactory.setPreferenceMetadata(preferenceMetadata);
        return preferenceFactory;
    }

    /**
     * Configures whether to initialize the preference proxy lazily. This defaults to {@literal false}.
     *
     * @param lazy whether to initialize the preference proxy lazily. This defaults to {@literal false}.
     */
    public void setLazyInit(boolean lazy) {
        this.lazyInit = lazy;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    @NonNull
    public T getObject() {
        return this.preference.get();
    }

    @Override
    @NonNull
    public Class<? extends T> getObjectType() {
        return preferenceInterface;
    }

    @Override
    public boolean isSingleton() {
        return DefaultListableBeanFactory.SCOPE_SINGLETON.equals(preferenceMetadata.getScope());
    }

    @Override
    public void afterPropertiesSet() {

        this.factory = createPreferenceFactory();
        this.preference = Lazy.of(() -> this.factory.getPreference(preferenceInterface));

        if (!lazyInit && isSingleton()) {
            this.preference.get();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

}