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

import static org.springframework.beans.factory.config.BeanDefinition.ROLE_INFRASTRUCTURE;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.data.config.ParsingUtils;
import org.springframework.data.jpa.repository.config.JpaRepositoryConfigExtension;
import org.springframework.data.repository.config.FragmentMetadata;
import org.springframework.data.repository.config.ImplementationDetectionConfiguration;
import org.springframework.data.repository.config.RepositoryConfiguration;
import org.springframework.data.repository.config.RepositoryConfigurationSource;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.AbstractRepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.RepositoryFragmentsFactoryBean;
import org.springframework.data.util.Optionals;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;

public class JfxAppsJpaRepositoryConfigExtension extends JpaRepositoryConfigExtension  {

    private static final Logger logger = LoggerFactory.getLogger(JfxAppsJpaRepositoryConfigExtension.class);

    private final JfxAppContext applicationContext;
    private final BeanDefinitionRegistry registry;

    private final CachingMetadataReaderFactory metadataReaderFactory;
    private final FragmentMetadata fragmentMetadata;

    public JfxAppsJpaRepositoryConfigExtension(JfxAppContext applicationContext, BeanDefinitionRegistry registry, ResourceLoader loader) {
        super();
        this.applicationContext = applicationContext;
        this.registry = registry;

        this.metadataReaderFactory = new CachingMetadataReaderFactory(loader);
        this.fragmentMetadata = new FragmentMetadata(metadataReaderFactory);
    }

    @Override
    public void postProcess(BeanDefinitionBuilder builder, RepositoryConfigurationSource source) {

        // At this step the repository interface is the first constructor argument of
        // the bean definition in builder
        String repositoryInterface = builder.getRawBeanDefinition().getConstructorArgumentValues()
                .getIndexedArgumentValue(0, String.class).getValue().toString();

        String fragmentsBeanName = registerRepositoryFragments(source, repositoryInterface);
        builder.addPropertyValue("repositoryFragments", new RuntimeBeanReference(fragmentsBeanName));

        super.postProcess(builder, source);
    }

    private String registerRepositoryFragments(RepositoryConfigurationSource source, String repositoryInterface) {

        BeanDefinitionBuilder fragmentsBuilder = BeanDefinitionBuilder
                .rootBeanDefinition(RepositoryFragmentsFactoryBean.class) //
                .setRole(ROLE_INFRASTRUCTURE);

        List<String> fragmentBeanNames = registerRepositoryFragmentsImplementation(source, repositoryInterface) //
                .map(RepositoryFragmentConfiguration::getFragmentBeanName) //
                .collect(Collectors.toList());

        fragmentsBuilder.addConstructorArgValue(fragmentBeanNames);

        String fragmentsBeanName = BeanDefinitionReaderUtils
                .uniqueBeanName(String.format("%s.%s.fragments", this.getModuleName().toLowerCase(Locale.ROOT),
                        ClassUtils.getShortName(repositoryInterface)), registry);
        registry.registerBeanDefinition(fragmentsBeanName, fragmentsBuilder.getBeanDefinition());
        return fragmentsBeanName;
    }

    private Stream<RepositoryFragmentConfiguration> registerRepositoryFragmentsImplementation(
            RepositoryConfigurationSource source, String repositoryInterface) {

//        ImplementationDetectionConfiguration config = configuration
//                .toImplementationDetectionConfiguration(metadataReaderFactory);
        ImplementationDetectionConfiguration config = null;
        Stream<RepositoryFragmentConfiguration> discovered = discoverFragments(repositoryInterface, source, config);

        return discovered //
                .peek(it -> potentiallyRegisterFragmentImplementation(source, it)) //
                .peek(it -> potentiallyRegisterRepositoryFragment(source, it));
    }

    private Stream<RepositoryFragmentConfiguration> discoverFragments(String repositoryInterface, RepositoryConfigurationSource source,
            ImplementationDetectionConfiguration config) {
        return fragmentMetadata.getFragmentInterfaces(repositoryInterface)
                .map(it -> detectRepositoryFragmentConfiguration(it, config, source)) //
                .flatMap(Optionals::toStream);
    }

    private Optional<RepositoryFragmentConfiguration> detectRepositoryFragmentConfiguration(String fragmentInterface,
            ImplementationDetectionConfiguration config, RepositoryConfigurationSource source) {

        var cls = applicationContext.getRegisteredClass(fragmentInterface + source.getRepositoryImplementationPostfix().orElse("Impl"));

        if (cls == null) {
            return Optional.empty();
        }
        String[] beanNames = applicationContext.getBeanNamesForType(cls);

        if (beanNames.length != 1) {
            return Optional.empty();
        }

        var fragmentBeanDefinition = applicationContext.getBeanFactory().getBeanDefinition(beanNames[0]);
        Optional<AbstractBeanDefinition> beanDefinition = Optional.of((AbstractBeanDefinition)fragmentBeanDefinition);

        return beanDefinition.map(bd -> createFragmentConfiguration(fragmentInterface, source, bd));
    }

    private RepositoryFragmentConfiguration createFragmentConfiguration(@Nullable String fragmentInterface,
            RepositoryConfigurationSource source, String className) {

        try {

            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
            AnnotatedGenericBeanDefinition bd = new AnnotatedGenericBeanDefinition(metadataReader.getAnnotationMetadata());
            return createFragmentConfiguration(fragmentInterface, source, bd);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static RepositoryFragmentConfiguration createFragmentConfiguration(@Nullable String fragmentInterface,
            RepositoryConfigurationSource source, AbstractBeanDefinition beanDefinition) {

        return new RepositoryFragmentConfiguration(fragmentInterface, beanDefinition,
                source.generateBeanName(beanDefinition));
    }

    private String potentiallyRegisterRepositoryImplementation(RepositoryConfiguration<?> configuration,
            AbstractBeanDefinition beanDefinition) {

        String targetBeanName = configuration.getConfigurationSource().generateBeanName(beanDefinition);
        beanDefinition.setSource(configuration.getSource());

        if (registry.containsBeanDefinition(targetBeanName)) {

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Custom repository implementation already registered: %s %s", targetBeanName,
                        beanDefinition.getBeanClassName()));
            }
        } else {

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Registering custom repository implementation: %s %s", targetBeanName,
                        beanDefinition.getBeanClassName()));
            }

            registry.registerBeanDefinition(targetBeanName, beanDefinition);
        }

        return targetBeanName;
    }

    private void potentiallyRegisterFragmentImplementation(RepositoryConfigurationSource source,
            RepositoryFragmentConfiguration fragmentConfiguration) {

        String beanName = fragmentConfiguration.getImplementationBeanName();

        // Already a bean configured?
        if (registry.containsBeanDefinition(beanName)) {

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Repository fragment implementation already registered: %s", beanName));
            }

            return;
        }

        fragmentConfiguration.getBeanDefinition().ifPresent(bd -> {

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Registering repository fragment implementation: %s %s", beanName,
                        fragmentConfiguration.getClassName()));
            }

            bd.setSource(source.getSource());
            registry.registerBeanDefinition(beanName, bd);
        });
    }

    private void potentiallyRegisterRepositoryFragment(RepositoryConfigurationSource source,
            RepositoryFragmentConfiguration fragmentConfiguration) {

        String beanName = fragmentConfiguration.getFragmentBeanName();

        // Already a bean configured?
        if (registry.containsBeanDefinition(beanName)) {

            if (logger.isDebugEnabled()) {
                logger.debug(String.format("RepositoryFragment already registered: %s", beanName));
            }

            return;
        }

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Registering RepositoryFragment: %s", beanName));
        }

        BeanDefinitionBuilder fragmentBuilder = BeanDefinitionBuilder.rootBeanDefinition(RepositoryFragment.class,
                "implemented");

        if (StringUtils.hasText(fragmentConfiguration.getInterfaceName())) {
            fragmentBuilder.addConstructorArgValue(fragmentConfiguration.getInterfaceName());
        }
        fragmentBuilder.addConstructorArgReference(fragmentConfiguration.getImplementationBeanName());

        registry.registerBeanDefinition(beanName,
                ParsingUtils.getSourceBeanDefinition(fragmentBuilder, source.getSource()));
    }

    @Override
    public <T extends RepositoryConfigurationSource> Collection<RepositoryConfiguration<T>> getRepositoryConfigurations(
            T configSource, ResourceLoader loader, boolean strictMatchesOnly) {

        Assert.notNull(configSource, "ConfigSource must not be null");
        Assert.notNull(loader, "Loader must not be null");

        Set<RepositoryConfiguration<T>> result = new HashSet<>();

        for (BeanDefinition candidate : configSource.getCandidates(loader)) {

            RepositoryConfiguration<T> configuration = getRepositoryConfiguration(candidate, configSource);

            Class<?> repositoryInterface = null;

            if (candidate instanceof AbstractBeanDefinition abd) {
                repositoryInterface = abd.getBeanClass();
            }

            if (repositoryInterface == null) {
                result.add(configuration);
                continue;
            }

            RepositoryMetadata metadata = AbstractRepositoryMetadata.getMetadata(repositoryInterface);

            boolean qualifiedForImplementation = !strictMatchesOnly || configSource.usesExplicitFilters()
                    || isStrictRepositoryCandidate(metadata);

            if (qualifiedForImplementation && useRepositoryConfiguration(metadata)) {
                result.add(configuration);
            }
        }

        return result;
    }


}
