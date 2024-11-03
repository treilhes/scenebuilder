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
package com.gluonhq.jfxapps.boot.api.aop.internal;

import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

public class AopBeanFactoryDefinitionRegistrar {

    private final BeanDefinitionRegistry registry;

    public AopBeanFactoryDefinitionRegistrar(BeanDefinitionRegistry registry) {
        super();
        this.registry = registry;
    }


    public void register(AopBeanConfiguration configuration, BeanNameGenerator nameGenerator) {
        BeanDefinitionBuilder definitionBuilder = buildDefinitionBuilder(configuration);

        RootBeanDefinition beanDefinition = (RootBeanDefinition) definitionBuilder.getBeanDefinition();
        beanDefinition.setTargetType(getFactoryBeanType(configuration));
        beanDefinition.setResourceDescription(configuration.getResourceDescription());
        beanDefinition.setScope(configuration.getBeanMetadata().getScope());

        String beanName = nameGenerator.generateBeanName(beanDefinition, registry);

        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    private BeanDefinitionBuilder buildDefinitionBuilder(AopBeanConfiguration configuration) {

        BeanDefinitionBuilder builder = BeanDefinitionBuilder
                .rootBeanDefinition(configuration.getFactoryBeanClass());

        builder.addConstructorArgValue(configuration.getBeanClass());
        builder.addPropertyValue("lazyInit", configuration.isLazyInit());
        builder.setLazyInit(configuration.isLazyInit());
        builder.setPrimary(configuration.isPrimary());

        return builder;
    }

    /**
     * Returns the factory bean type from the given {@link AopBeanConfiguration} as loaded {@link Class}.
     *
     * @param configuration must not be {@literal null}.
     * @return can be {@literal null}.
     */
    @Nullable
    private ResolvableType getFactoryBeanType(AopBeanConfiguration configuration) {

        Class<?> beanClass = configuration.getBeanClass();

        if (beanClass == null) {
            return null;
        }

        Class<?> factoryBean = configuration.getFactoryBeanClass();

        if (factoryBean == null) {
            return null;
        }

        var metadata = configuration.getBeanMetadata();

        if (metadata.hasGenericType()) {
            List<Class<?>> types = List.of(beanClass, metadata.getGenericType());

            ResolvableType[] declaredGenerics = ResolvableType.forClass(factoryBean).getGenerics();

            //FIXME not the original intention of the code : (factoryBean,factoryBean)
            ResolvableType[] parentGenerics = ResolvableType.forClass(factoryBean,factoryBean)//(AopFactoryBean.class, factoryBean)
                    .getGenerics();

            List<ResolvableType> resolvedGenerics = new ArrayList<>(factoryBean.getTypeParameters().length);

            for (int i = 0; i < parentGenerics.length; i++) {

                ResolvableType parameter = parentGenerics[i];

                if (parameter.getType() instanceof TypeVariable<?>) {
                    resolvedGenerics.add(i < types.size() ? ResolvableType.forClass(types.get(i)) : parameter);
                }
            }

            if (resolvedGenerics.size() < declaredGenerics.length) {
                resolvedGenerics.addAll(Arrays.asList(declaredGenerics).subList(parentGenerics.length, declaredGenerics.length));
            }

            return ResolvableType.forClassWithGenerics(factoryBean,
                    resolvedGenerics.subList(0, declaredGenerics.length).toArray(ResolvableType[]::new));
        } else {
            return ResolvableType.forClass(factoryBean);
        }


    }
}
