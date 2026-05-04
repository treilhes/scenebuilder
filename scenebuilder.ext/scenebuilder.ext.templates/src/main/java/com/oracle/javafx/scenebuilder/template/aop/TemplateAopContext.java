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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;

import com.oracle.javafx.scenebuilder.api.template.NoTemplateBean;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateContext;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.oracle.javafx.scenebuilder.api.template.TemplateManager;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeManager;
import com.treilhes.emc4j.boot.api.aop.AopContext;
import com.treilhes.emc4j.boot.api.aop.AopFactory;
import com.treilhes.emc4j.boot.api.aop.AopFactoryBean;
import com.treilhes.emc4j.boot.api.aop.AopMetadata;
import com.treilhes.emc4j.boot.api.aop.DefaultMethodInterceptor;
import com.treilhes.emc4j.boot.api.aop.ImplementationInterceptor;
import com.treilhes.emc4j.boot.api.context.EmContext;

public class TemplateAopContext extends AopContext<Template> {

    public TemplateAopContext() {
        super(Template.class);
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonTemplateInterface = !Template.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isTemplate = Arrays.stream(beanDefinition.getMetadata().getInterfaceNames())
                .anyMatch(Template.class.getName()::equals);
        boolean isInterface = beanDefinition.getMetadata().isInterface();
        boolean hasContextAnnotation = beanDefinition.getMetadata().isAnnotated(TemplateContext.class.getName());

        return isTemplate && isInterface && isNonTemplateInterface && hasContextAnnotation;
    }

    @Override
    public Class<? extends AopFactoryBean<Template>> factoryBeanClass() {
        return TemplateFactoryBean.class;
    }

    @Override
    public Class<? extends NoTemplateBean> getExclusionAnnotation() {
        return NoTemplateBean.class;
    }

    @Override
    public Object createProxy(AopFactory aopFactory, EmContext context, AopMetadata metadata) {

        var templateInterface = metadata.getBeanClass();

        aopFactory.addRead(templateInterface);

        var template = createTarget(context, templateInterface);

        // Create proxy
        var result = new ProxyFactory();
        result.setTarget(template);
        result.setInterfaces(templateInterface);
        result.addAdvice(new DefaultMethodInterceptor());
        result.addAdvice(new ImplementationInterceptor(template, templateInterface));

        return result.getProxy(templateInterface.getClassLoader());

    }

    private Template createTarget(EmContext context, Class<?> templateInterface) {

        var annotation = AnnotationUtils.findAnnotation(templateInterface, TemplateContext.class);

        if (annotation == null) {
            throw new IllegalStateException("Template interface " + templateInterface.getName() + " must be annotated with @TemplateContext");
        }

        var id = UUID.fromString(annotation.id());
        var name = annotation.name();
        var description = annotation.description();
        var orderKey = annotation.orderKey();
        var groupClass = annotation.groupClass();
        var groupId = annotation.groupId().isBlank() ? null : UUID.fromString(annotation.groupId());
        var fxmlUrl = templateInterface.getResource(annotation.fxmlUrl());
        var iconUrl = templateInterface.getResource(annotation.iconUrl());
        var iconX2Url = templateInterface.getResource(annotation.iconX2Url());
        var width = annotation.width();
        var height = annotation.height();
        var themeClasses = annotation.themeClasses();
        var themeIds = Arrays.stream(annotation.themeIds()).map(UUID::fromString).toList();

        TemplateGroup group = null;
        if (groupClass != TemplateContext.NoTemplateGroup.class) {
            // group class provided, so get it from context, exception if not found
            group = context.getBean(groupClass);
        }

        if (groupId != null) {
            // group id provided, so get it from manager
            var manager = context.getBean(TemplateManager.class);
            var groupFromManager = manager.getGroup(groupId);

            if (groupFromManager != null && group != null && !groupFromManager.getId().equals(group.getId())) {
                throw new IllegalArgumentException(
                        "if both TemplateContext.groupClass and TemplateContext.groupId are provided, the group must be the same");
            }
            group = groupFromManager;
        }

        List<Theme> themes = new ArrayList<>();
        if (themeClasses.length > 0) {
            for (var cls : themeClasses) {
                themes.add((Theme)context.getBean(cls));
            }
        }

        if (!themeIds.isEmpty()) {
            var manager = context.getBean(ThemeManager.class);
            for (var tId : themeIds) {
                var theme = manager.getTheme(tId);
                themes.add(theme);
            }
        }

        return new BaseTemplate(id, name, description, group, orderKey, fxmlUrl, iconUrl, iconX2Url, width, height, themes);
    }

    public static class TemplateFactoryBean extends AopFactoryBean<Template> {

        public TemplateFactoryBean(Class<?> templateInterface) {
            super(templateInterface, new TemplateAopContext());
        }

    }

    public class BaseTemplate implements Template {

        private final UUID id;
        private final String name;
        private final String description;
        private final TemplateGroup group;
        private final String orderKey;
        private final URL fxmlUrl;
        private final URL iconUrl;
        private final URL iconX2Url;
        private final int width;
        private final int height;
        private final List<Theme> themes;

        //@formatter:off
        public BaseTemplate(
                UUID id,
                String name,
                String description,
                TemplateGroup group,
                String orderKey,
                URL fxmlUrl,
                URL iconUrl,
                URL iconX2Url,
                int width,
                int height,
                List<Theme> themes
                ) {
            //@formatter:on
            this.id = id;
            this.name = name;
            this.description = description;
            this.group = group;
            this.orderKey = orderKey;
            this.fxmlUrl = fxmlUrl;
            this.iconUrl = iconUrl;
            this.iconX2Url = iconX2Url;
            this.width = width;
            this.height = height;
            this.themes = themes;
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
        public String getDescription() {
            return description;
        }

        @Override
        public TemplateGroup getGroup() {
            return group;
        }

        @Override
        public String getOrderKey() {
            return orderKey;
        }

        @Override
        public URL getFxmlUrl() {
            return fxmlUrl;
        }

        @Override
        public URL getIconUrl() {
            return iconUrl;
        }

        @Override
        public URL getIconX2Url() {
            return iconX2Url;
        }

        @Override
        public int getWidth() {
            return width;
        }

        @Override
        public int getHeight() {
            return height;
        }

        @Override
        public List<Theme> getThemes() {
            return themes;
        }
    }
}
