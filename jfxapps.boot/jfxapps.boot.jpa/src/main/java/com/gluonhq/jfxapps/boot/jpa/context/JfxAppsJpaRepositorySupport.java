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
package com.gluonhq.jfxapps.boot.jpa.context;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.ResolvableType;
import org.springframework.core.env.Environment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.repository.config.RepositoryConfigurationDelegate;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;

import com.gluonhq.jfxapps.boot.api.context.JfxAppBeanFactory;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.gluonhq.jfxapps.boot.api.context.JfxAppsBeanNameGenerator;
import com.gluonhq.jfxapps.boot.api.jpa.ResolvablePersistenceManagedTypes;
import com.gluonhq.jfxapps.boot.api.utils.CompositeClassloader;

import jakarta.persistence.Entity;

public class JfxAppsJpaRepositorySupport
        implements BeanDefinitionRegistryPostProcessor, ApplicationContextAware, BeanFactoryAware, EnvironmentAware, ResourceLoaderAware {

    private static final Logger logger = LoggerFactory.getLogger(JfxAppsJpaRepositorySupport.class);

    private JfxAppContext applicationContext;
    private BeanFactory beanFactory;
    private Environment environment;
    private ResourceLoader resourceLoader;

    @EnableJpaRepositories
    private class DefaultConfiguration{

    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        try {
            var nameGenerator = JfxAppsBeanNameGenerator.getInstance();
            var lazy = false;
            var primary = false;
            var factoryBeanClassName = JfxAppsJpaRepositoryFactoryBean.class.getName();
            var managedTypes = new ArrayList<Class<?>>();
            var repositoriyCandidates = new ArrayList<BeanDefinition>();

            var compositeLoader = new CompositeClassloader();

            if (beanFactory instanceof JfxAppBeanFactory jfxBeanFactory
                    && applicationContext instanceof JfxAppContext jfxContext) {

                for (var name:registry.getBeanDefinitionNames()) {

                    var beanDefinition = registry.getBeanDefinition(name);
                    var beanClass = jfxContext.getRegisteredClass(beanDefinition.getBeanClassName());

                    if (beanClass == null) {
                        // Not a registered class, probably a springboot one
                        continue;
                    }
                    if (JpaRepository.class.isAssignableFrom(beanClass) && beanClass.isInterface()) {



//                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanClassName);
//
//                        builder.addConstructorArgValue(beanClass);
//                        builder.addPropertyValue("lazyInit", lazy);
//                        builder.setLazyInit(lazy);
//                        builder.setPrimary(primary);
//
//                        RootBeanDefinition factoryDefinition = (RootBeanDefinition) builder.getBeanDefinition();
//                        factoryDefinition.setTargetType(getFactoryBeanType(beanClass));
//                        factoryDefinition.setResourceDescription(String.format("%s for %s", factoryBeanClassName, name));
//                        //factoryDefinition.setScope(configuration.getBeanMetadata().getScope());
//
//                        String beanName = nameGenerator.generateBeanName(factoryDefinition, registry);
                          registry.removeBeanDefinition(name);
//                        registry.registerBeanDefinition(beanName, factoryDefinition);

                        repositoriyCandidates.add(beanDefinition);
                        compositeLoader.addClassLoader(beanClass.getClassLoader());
                        continue;
                    }

                    if (jfxBeanFactory.findAnnotationOnBean(name, Entity.class) != null) {
                        managedTypes.add(beanClass);
                        compositeLoader.addClassLoader(beanClass.getClassLoader());
                    }
                }

                if (!managedTypes.isEmpty()) {
                    var resolvableManagedTypes = new ResolvablePersistenceManagedTypes(managedTypes);

                    if (registry.containsBeanDefinition(PersistenceManagedTypes.class.getName())) {
                        registry.removeBeanDefinition(PersistenceManagedTypes.class.getName());
                    }
                    if (registry.containsBeanDefinition("persistenceManagedTypes")) {
                        registry.removeBeanDefinition("persistenceManagedTypes");
                    }

                    jfxContext.registerBean(ResolvablePersistenceManagedTypes.class, () -> resolvableManagedTypes);
                    //jfxContext.registerBean(PersistenceManagedTypes.class, () -> resolvableManagedTypes);
                }
            }

            // test
            Class<? extends Annotation> annotation = EnableJpaRepositories.class;
            boolean inMultiStoreMode = false;

            SimpleMetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory();
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(DefaultConfiguration.class.getName());
            AnnotationMetadata metadata = metadataReader.getAnnotationMetadata();

            var compositeResourceLoader = new DefaultResourceLoader(compositeLoader);

            JfxAppsAnnotationRepositoryConfigurationSource configurationSource = new JfxAppsAnnotationRepositoryConfigurationSource(metadata,
                    annotation, resourceLoader, environment, registry, nameGenerator, repositoriyCandidates);


            JfxAppsJpaRepositoryConfigExtension jpaRepositoryConfigExtension = new JfxAppsJpaRepositoryConfigExtension(
                    applicationContext, registry, resourceLoader);

//        RepositoryBeanDefinitionBuilder builder = new RepositoryBeanDefinitionBuilder(registry, jpaRepositoryConfigExtension,
//                configurationSource, resourceLoader, environment);

//            Collection<RepositoryConfiguration<RepositoryConfigurationSource>> configurations = jpaRepositoryConfigExtension
//                    .getRepositoryConfigurations(configurationSource, resourceLoader, inMultiStoreMode);

            RepositoryConfigurationDelegate delegate = new RepositoryConfigurationDelegate(configurationSource,
                    resourceLoader, environment);

            delegate.registerRepositoriesIn(registry, jpaRepositoryConfigExtension);







        } catch (BeanDefinitionStoreException | NoSuchBeanDefinitionException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private ResolvableType getFactoryBeanType(Class<?> interfaceClass) {
        ResolvableType[] jpaRepositoryGenerics = ResolvableType.forClass(JpaRepository.class, interfaceClass)
                .getGenerics();

        return ResolvableType.forClassWithGenerics(JpaRepositoryFactoryBean.class,
                ResolvableType.forClass(interfaceClass),
                jpaRepositoryGenerics[0], jpaRepositoryGenerics[1]);

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (applicationContext instanceof JfxAppContext jfx) {
            this.applicationContext = jfx;
        } else {
            throw new IllegalArgumentException("ApplicationContext must be an instance of JfxAppContext");
        }
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

//    @Bean
//    JpaRepositoryFactoryBean<RepositoryRepository, Repository, String> repositoryRepository() {
//        JpaRepositoryFactoryBean factory = new JpaRepositoryFactoryBean(RepositoryRepository.class);
//        factory.getRepositoryInformation().
//        return factory;
//    }

}
