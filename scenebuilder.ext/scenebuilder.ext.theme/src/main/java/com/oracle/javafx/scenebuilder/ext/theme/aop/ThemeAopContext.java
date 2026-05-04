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
package com.oracle.javafx.scenebuilder.ext.theme.aop;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.core.annotation.AnnotationUtils;

import com.oracle.javafx.scenebuilder.api.theme.NoThemeBean;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeContext;
import com.oracle.javafx.scenebuilder.api.theme.ThemeGroup;
import com.oracle.javafx.scenebuilder.api.theme.ThemeManager;
import com.treilhes.emc4j.boot.api.aop.AopContext;
import com.treilhes.emc4j.boot.api.aop.AopFactory;
import com.treilhes.emc4j.boot.api.aop.AopFactoryBean;
import com.treilhes.emc4j.boot.api.aop.AopMetadata;
import com.treilhes.emc4j.boot.api.aop.DefaultMethodInterceptor;
import com.treilhes.emc4j.boot.api.aop.ImplementationInterceptor;
import com.treilhes.emc4j.boot.api.context.EmContext;

public class ThemeAopContext extends AopContext<Theme> {

    public ThemeAopContext() {
        super(Theme.class);
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {

        boolean isNonThemeInterface = !Theme.class.getName().equals(beanDefinition.getBeanClassName());
        boolean isTheme = Arrays.stream(beanDefinition.getMetadata().getInterfaceNames())
                .anyMatch(Theme.class.getName()::equals);
        boolean isInterface = beanDefinition.getMetadata().isInterface();
        boolean hasContextAnnotation = beanDefinition.getMetadata().isAnnotated(ThemeContext.class.getName());

        return isTheme && isInterface && isNonThemeInterface && hasContextAnnotation;
    }

    @Override
    public Class<? extends AopFactoryBean<Theme>> factoryBeanClass() {
        return ThemeFactoryBean.class;
    }

    @Override
    public Class<? extends NoThemeBean> getExclusionAnnotation() {
        return NoThemeBean.class;
    }


    @Override
    public Object createProxy(AopFactory aopFactory, EmContext context, AopMetadata metadata) {
        var themeInterface = metadata.getBeanClass();

        aopFactory.addRead(themeInterface);

        var theme = createTarget(context, themeInterface);

        // Create proxy
        var result = new ProxyFactory();
        result.setTarget(theme);
        result.setInterfaces(themeInterface);
        result.addAdvice(new DefaultMethodInterceptor());
        result.addAdvice(new ImplementationInterceptor(theme, themeInterface));

        return result.getProxy(themeInterface.getClassLoader());
    }

    private Theme createTarget(EmContext context, Class<?> themeInterface) {

        var annotation = AnnotationUtils.findAnnotation(themeInterface, ThemeContext.class);

        if (annotation == null) {
            throw new IllegalStateException("Theme interface must be annotated with ThemeContext");
        }

        var id = UUID.fromString(annotation.id());
        var name = annotation.name();
        var userAgentStylesheet = annotation.userAgentStylesheet();
        var stylesheets = List.of(annotation.stylesheets());
        var groupClass = annotation.groupClass();
        var groupId = annotation.groupId().isBlank() ? null : UUID.fromString(annotation.groupId());

        ThemeGroup group = null;
        if (groupClass != ThemeContext.NoThemeGroup.class) {
            // group class provided, so get it from context, exception if not found
            group = context.getBean(groupClass);
        }

        if (groupId != null) {
            // group id provided, so get it from manager
            var manager = context.getBean(ThemeManager.class);
            var groupFromManager = manager.getGroup(groupId);

            if (groupFromManager != null && group != null && !groupFromManager.getId().equals(group.getId())) {
                throw new IllegalArgumentException(
                        "if both ThemeContext.groupClass and ThemeContext.groupId are provided, the group must be the same");
            }
            group = groupFromManager;
        }

        return new BaseTheme(id, name, userAgentStylesheet, stylesheets, group);
    }

    public class BaseTheme implements Theme {

        private final UUID id;
        private final String name;
        private final String userAgentStylesheet;
        private final List<String> stylesheets;
        private final ThemeGroup group;

        //@formatter:off
        public BaseTheme(
                UUID id,
                String name,
                String userAgentStylesheet,
                List<String> stylesheets,
                ThemeGroup group) {
            //@formatter:on
            this.id = id;
            this.name = name;
            this.userAgentStylesheet = userAgentStylesheet;
            this.stylesheets = stylesheets != null ? stylesheets : Collections.emptyList();
            this.group = group;
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
        public String getUserAgentStylesheet() {
            return userAgentStylesheet;
        }

        @Override
        public List<String> getStylesheets() {
            return stylesheets;
        }

        @Override
        public ThemeGroup getGroup() {
            return group;
        }

    }

    public static class ThemeFactoryBean extends AopFactoryBean<Theme> {
        public ThemeFactoryBean(Class<?> themeInterface) {
            super(themeInterface, new ThemeAopContext());
        }
    }

}
