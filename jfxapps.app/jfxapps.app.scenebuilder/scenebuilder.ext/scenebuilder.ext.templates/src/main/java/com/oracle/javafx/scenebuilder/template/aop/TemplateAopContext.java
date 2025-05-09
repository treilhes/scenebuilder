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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;

import com.gluonhq.jfxapps.boot.api.aop.AopContext;
import com.gluonhq.jfxapps.boot.api.aop.AopFactoryBean;
import com.gluonhq.jfxapps.boot.api.aop.AopMetadata;
import com.gluonhq.jfxapps.boot.api.context.JfxAppContext;
import com.oracle.javafx.scenebuilder.api.template.NoTemplateBean;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateContext;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.oracle.javafx.scenebuilder.api.template.TemplateManager;
import com.oracle.javafx.scenebuilder.api.theme.Theme;
import com.oracle.javafx.scenebuilder.api.theme.ThemeManager;

public class TemplateAopContext extends AopContext<Template, TemplateContext, TemplateAopContext.TemplateMetadata> {

    public TemplateAopContext() {
        super(Template.class, TemplateContext.class);
    }

    @Override
    public TemplateAopContext.TemplateMetadata loadMetadata(Class<?> clazz) {
        return new TemplateMetadata(getContexAnnotationClass(), getMarkerClass(), clazz);
    }

    @Override
    public Template createTarget(JfxAppContext context, TemplateMetadata metadata) {


        var id = metadata.getId();
        var name = metadata.getName();
        var description = metadata.getDescription();
        var orderKey = metadata.getOrderKey();
        var fxmlUrl = metadata.getFxmlUrl();
        var iconUrl = metadata.getIconUrl();
        var iconX2Url = metadata.getIconX2Url();
        var width = metadata.getWidth();
        var height = metadata.getHeight();

        var groupClass = metadata.getGroupClass();
        var groupId = metadata.getGroupId();

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

        var themeClasses = metadata.getThemeClasses();
        var themeIds = metadata.getThemeIds();

        List<Theme> themes = new ArrayList<>();
        if (themeClasses.length > 0) {
            for (var cls : themeClasses) {
                themes.add((Theme)context.getBean(cls));
            }
        }

        if (themeIds.size() > 0) {
            var manager = context.getBean(ThemeManager.class);
            for (var tId : themeIds) {
                var theme = manager.getTheme(tId);
                themes.add(theme);
            }
        }

        return new BaseTemplate(id, name, description, group, orderKey, fxmlUrl, iconUrl, iconX2Url, width, height, themes);
    }

    @Override
    public Class<? extends AopFactoryBean<Template, TemplateMetadata>> factoryBeanClass() {
        return TemplateFactoryBean.class;
    }

    public static class TemplateFactoryBean extends AopFactoryBean<Template, TemplateMetadata> {

        public TemplateFactoryBean(Class<?> templateInterface) {
            super(templateInterface, new TemplateAopContext());
        }

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
    public Class<NoTemplateBean> getExclusionAnnotation() {
        return NoTemplateBean.class;
    }

    public class BaseTemplate implements Template {

        private static final Logger logger = LoggerFactory.getLogger(BaseTemplate.class);

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

    public static class TemplateMetadata extends AopMetadata<TemplateContext, Template> {

        private UUID id;
        private String name;
        private String description;
        private String orderKey;
        private Class<? extends TemplateGroup> groupClass;
        private UUID groupId;
        private URL fxmlUrl;
        private URL iconUrl;
        private URL iconX2Url;
        private int width;
        private int height;
        private Class<? extends TemplateGroup>[] themeClasses;
        private List<UUID> themeIds;

        public TemplateMetadata(Class<TemplateContext> annotationClass, Class<Template> markerClass, Class<?> templateInterface) {
            super(annotationClass, markerClass, templateInterface);
        }

        @Override
        protected void loadMetadata(TemplateContext annotation) {
            if (hasAnnotation()) {
                this.id = UUID.fromString(annotation.id());
                this.name = annotation.name();
                this.description = annotation.description();
                this.orderKey = annotation.orderKey();
                this.groupClass = annotation.groupClass();
                this.groupId = annotation.groupId().isBlank() ? null : UUID.fromString(annotation.groupId());
                this.fxmlUrl = this.getBeanClass().getResource(annotation.fxmlUrl());
                this.iconUrl = this.getBeanClass().getResource(annotation.iconUrl());
                this.iconX2Url = this.getBeanClass().getResource(annotation.iconX2Url());
                this.width = annotation.width();
                this.height = annotation.height();
                this.themeClasses = annotation.themeClasses();
                this.themeIds = Arrays.stream(annotation.themeIds()).map(UUID::fromString).toList();
            }
        }

        public UUID getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public String getOrderKey() {
            return orderKey;
        }

        public Class<? extends TemplateGroup> getGroupClass() {
            return groupClass;
        }

        public UUID getGroupId() {
            return groupId;
        }

        public URL getFxmlUrl() {
            return fxmlUrl;
        }

        public URL getIconUrl() {
            return iconUrl;
        }

        public URL getIconX2Url() {
            return iconX2Url;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public Class<? extends TemplateGroup>[] getThemeClasses() {
            return themeClasses;
        }

        public List<UUID> getThemeIds() {
            return themeIds;
        }

    }
}
