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
package com.oracle.javafx.scenebuilder.template.aop;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;

import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroupContext;
import com.treilhes.emc4j.boot.api.aop.AopContext;
import com.treilhes.emc4j.boot.api.aop.AopFactory;
import com.treilhes.emc4j.boot.api.aop.AopFactoryBean;
import com.treilhes.emc4j.boot.api.aop.AopMetadata;
import com.treilhes.emc4j.boot.api.aop.DefaultMethodInterceptor;
import com.treilhes.emc4j.boot.api.aop.ImplementationInterceptor;
import com.treilhes.emc4j.boot.api.context.EmContext;

public class TemplateGroupAopContext extends AopContext<TemplateGroup> {

    public TemplateGroupAopContext() {
        super(TemplateGroup.class);
    }


    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonPreferenceInterface = !TemplateGroup.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isPreference = Arrays.stream(beanDefinition.getMetadata().getInterfaceNames())
                .anyMatch(TemplateGroup.class.getName()::equals);
        boolean isInterface = beanDefinition.getMetadata().isInterface();
        boolean hasContextAnnotation = beanDefinition.getMetadata().isAnnotated(TemplateGroupContext.class.getName());

        return isPreference && isInterface && isNonPreferenceInterface && hasContextAnnotation;
    }

    @Override
    public Class<? extends AopFactoryBean<TemplateGroup>> factoryBeanClass() {
        return TemplateGroupFactoryBean.class;
    }


    @Override
    public <E extends Annotation> Class<E> getExclusionAnnotation() {
        // no exclusion annotation for template groups
        return null;
    }

    @Override
    public Object createProxy(AopFactory aopFactory, EmContext context, AopMetadata metadata) {
        var templateInterface = metadata.getBeanClass();

        aopFactory.addRead(templateInterface);

        var preference = createTarget(templateInterface);

        // Create proxy
        var result = new ProxyFactory();
        result.setTarget(preference);
        result.setInterfaces(templateInterface);
        result.addAdvice(new DefaultMethodInterceptor());
        result.addAdvice(new ImplementationInterceptor(preference, templateInterface));

        return result.getProxy(templateInterface.getClassLoader());
    }

    public TemplateGroup createTarget(Class<?> templateGroupInterface) {

        var annotation = AnnotationUtils.findAnnotation(templateGroupInterface, TemplateGroupContext.class);

        if (annotation == null) {
            throw new IllegalStateException("Template group interface " + templateGroupInterface.getName() + " must be annotated with @TemplateGroupContext");
        }

        var id = UUID.fromString(annotation.id());
        var name = annotation.name();
        var orderKey = annotation.orderKey();

        return new BaseTemplateGroup(id, name, orderKey);
    }


    public static class TemplateGroupFactoryBean extends AopFactoryBean<TemplateGroup> {
        public TemplateGroupFactoryBean(Class<?> templateGroupInterface) {
            super(templateGroupInterface, new TemplateGroupAopContext());
        }
    }

    public class BaseTemplateGroup implements TemplateGroup {

        private final UUID id;
        private final String name;
        private final String orderKey;

        //@formatter:off
        public BaseTemplateGroup(
                UUID id,
                String name,
                String orderKey) {
            //@formatter:on
            this.id = id;
            this.name = name;
            this.orderKey = orderKey;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public String getOrderKey() {
            return orderKey;
        }
    }

}
