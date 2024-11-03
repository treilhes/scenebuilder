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
package com.oracle.javafx.scenebuilder.template.aop;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;

import com.gluonhq.jfxapps.boot.api.aop.AopContext;
import com.gluonhq.jfxapps.boot.api.aop.AopFactoryBean;
import com.gluonhq.jfxapps.boot.api.aop.AopMetadata;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroup;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroupContext;

public class TemplateGroupAopContext extends AopContext<ThemeGroup, ThemeGroupContext, TemplateGroupAopContext.ThemeGroupMetadata> {

    public TemplateGroupAopContext() {
        super(ThemeGroup.class, ThemeGroupContext.class);
    }

    @Override
    public TemplateGroupAopContext.ThemeGroupMetadata loadMetadata(Class<?> clazz) {
        return new ThemeGroupMetadata(getContexAnnotationClass(), getMarkerClass(), clazz);
    }

    @Override
    public ThemeGroup createTarget(JfxAppContext context, ThemeGroupMetadata metadata) {

        var id = metadata.getId();
        var name = metadata.getName();

        return new BaseThemeGroup(id, name);
    }

    @Override
    public Class<? extends AopFactoryBean<ThemeGroup, ThemeGroupMetadata>> factoryBeanClass() {
        return ThemeGroupFactoryBean.class;
    }


    @Override
    public <EX extends Annotation> Class<EX> getExclusionAnnotation() {
        // TODO Auto-generated method stub
        return null;
    }

    public static class ThemeGroupFactoryBean extends AopFactoryBean<ThemeGroup, ThemeGroupMetadata> {

        public ThemeGroupFactoryBean(Class<?> preferenceInterface) {
            super(preferenceInterface, new TemplateGroupAopContext());
        }

    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonPreferenceInterface = !ThemeGroup.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isPreference = Arrays.stream(beanDefinition.getMetadata().getInterfaceNames())
                .anyMatch(ThemeGroup.class.getName()::equals);
        boolean isInterface = beanDefinition.getMetadata().isInterface();
        boolean hasContextAnnotation = beanDefinition.getMetadata().isAnnotated(ThemeGroupContext.class.getName());

        return isPreference && isInterface && isNonPreferenceInterface && hasContextAnnotation;
    }

    public class BaseThemeGroup implements ThemeGroup {

        private static final Logger logger = LoggerFactory.getLogger(BaseThemeGroup.class);

        private final UUID id;
        private final String name;

        //@formatter:off
        public BaseThemeGroup(
                UUID id,
                String name) {
            //@formatter:on
            this.id = id;
            this.name = name;
        }

        @Override
        public UUID getId() {
            return id;
        }

        @Override
        public String getName() {
            return name;
        }
    }

    public static class ThemeGroupMetadata extends AopMetadata<ThemeGroupContext, ThemeGroup> {

        private UUID id;
        private String name;

        public ThemeGroupMetadata(Class<ThemeGroupContext> annotationClass, Class<ThemeGroup> markerClass, Class<?> preferenceInterface) {
            super(annotationClass, markerClass, preferenceInterface);
        }

        @Override
        protected void loadMetadata(ThemeGroupContext annotation) {
            if (hasAnnotation()) {
                this.id = UUID.fromString(annotation.id());
                this.name = annotation.name();
            } else {
                this.id = null;
                this.name = null;
            }
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

}
