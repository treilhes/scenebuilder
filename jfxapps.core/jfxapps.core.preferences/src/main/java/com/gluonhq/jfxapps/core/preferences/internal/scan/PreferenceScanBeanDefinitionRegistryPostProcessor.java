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
package com.gluonhq.jfxapps.core.preferences.internal.scan;

import java.lang.annotation.Annotation;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.util.Streamable;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.annotation.DeportedSingleton;
import com.gluonhq.jfxapps.core.api.preference.PreferenceScan;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanFactoryDefinitionRegistrar;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceBeanNameGenerator;
import com.gluonhq.jfxapps.core.preferences.internal.preference.PreferenceConfiguration;

@DeportedSingleton
public class PreferenceScanBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor,ResourceLoaderAware, EnvironmentAware, ApplicationContextAware {

    private final BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();
    private Environment environment;
    private ResourceLoader loader;
    private ApplicationContext context;

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        if (context instanceof JfxAppContext jfxc) {

            var registrar = new PreferenceBeanFactoryDefinitionRegistrar(registry);

            PreferenceScanComponentProvider scanScanner = new PreferenceScanComponentProvider(registry);
            scanScanner.setConsiderNestedPreferenceInterfaces(true);

            var scanCandidates = scanScanner.findCandidateComponents();

            for (var scanCandidate : scanCandidates) {
                var clazz = jfxc.getRegisteredClass(scanCandidate.getBeanClassName());

                ResourceLoader localLoader = new DefaultResourceLoader(clazz.getClassLoader());

                PreferenceScanConfiguration configuration = new PreferenceScanConfiguration(clazz, scanCandidate,
                        localLoader, environment, registry);

                PreferenceComponentProvider scanner = new PreferenceComponentProvider(configuration.getIncludeFilters(), registry);
                scanner.setConsiderNestedPreferenceInterfaces(true);//shouldConsiderNestedRepositories());
                scanner.setEnvironment(environment);
                scanner.setResourceLoader(localLoader);

                configuration.getExcludeFilters().forEach(scanner::addExcludeFilter);

                var candidates = Streamable.of(() -> configuration.getBasePackages().stream().flatMap(it -> scanner.findCandidateComponents(it).stream()));

                for (var candidate:candidates) {
                    var generator = new PreferenceBeanNameGenerator(clazz.getClassLoader(), beanNameGenerator);
                    var pc = new PreferenceConfiguration(clazz.getClassLoader(), candidate);

                    registrar.register(pc, generator);

//                    PreferenceScanConfiguration configurationSource = new PreferenceScanConfiguration(metadata, getAnnotation(),
//                            resourceLoader, environment, registry, generator);
    //
//                    PreferenceScanExtension extension = getExtension();
    //
//                    PreferenceConfigurationDelegate delegate = new PreferenceConfigurationDelegate(configurationSource,
//                            resourceLoader);
    //
//                    delegate.registerRepositoriesIn(registry, extension);
                }

            }
        }

    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.loader = resourceLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    protected Class<? extends Annotation> getAnnotation() {
        return PreferenceScan.class;
    }

}
