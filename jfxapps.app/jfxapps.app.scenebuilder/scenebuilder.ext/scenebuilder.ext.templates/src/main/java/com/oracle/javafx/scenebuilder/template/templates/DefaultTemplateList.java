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
package com.oracle.javafx.scenebuilder.template.templates;

import java.net.URL;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.gluonhq.jfxapps.boot.api.context.annotation.ApplicationSingleton;
import com.gluonhq.jfxapps.core.api.Size;
import com.gluonhq.jfxapps.core.api.template.Template;
import com.gluonhq.jfxapps.core.api.template.TemplateGroup;
import com.gluonhq.jfxapps.core.api.util.Ordered;

public class DefaultTemplateList {

    @ApplicationSingleton
    public static class DefaultGroup extends InnerGroup{
        public DefaultGroup() {
            super("0", null);
        }
    }

    @ApplicationSingleton
    public static class DesktopGroup extends InnerGroup{
        public DesktopGroup() {
            super("A", "template.title.header.desktop");
        }
    }

    @ApplicationSingleton
    public static class PhoneGroup extends InnerGroup{
        public PhoneGroup() {
            super("B", "template.title.header.phone");
        }
    }

    @ApplicationSingleton
    public static class TabletGroup extends InnerGroup{
        public TabletGroup() {
            super("C", "template.title.header.tablet");
        }
    }

    @ApplicationSingleton
    public static class OtherGroup extends InnerGroup{
        public OtherGroup() {
            super("D", "template.title.header.other");
        }
    }

    @ApplicationSingleton
    public static class EmptyTemplate extends InnerTemplate{

        public EmptyTemplate(@Autowired DefaultGroup group) {
            //@formatter:off
            super(UUID.fromString("a4c55174-e299-4dbe-bd0c-ee7f90cf89aa"),
                    group,
                    "A",
                    "template.title.new.empty.app",
                    Size.SIZE_640x480.getI18nKey(),
                    null,
                    null,
                    null,
                    null);
            //@formatter:on
        }

    }

    @ApplicationSingleton
    public static class BasicDesktopTemplate extends InnerTemplate{

        public BasicDesktopTemplate(@Autowired DesktopGroup group) {
            //@formatter:off
            super(UUID.fromString("ba373a83-0f6a-4025-9041-3a0106b13e41"),
                    group,
                    "A",
                    "template.title.new.basic.desktop.app",
                    Size.SIZE_640x480.getI18nKey(),
                    BasicDesktopTemplate.class.getResource("BasicDesktopApplication.fxml"),
                    BasicDesktopTemplate.class.getResource("basic_desktop.png"),
                    BasicDesktopTemplate.class.getResource("basic_desktop@2x.png"),
                    null);
            //@formatter:on
        }

    }

    @ApplicationSingleton
    public static class ComplexDesktopTemplate extends InnerTemplate{
        public ComplexDesktopTemplate(@Autowired DesktopGroup group) {
            //@formatter:off
            super(UUID.fromString("9f98b594-a9a0-4487-8f31-1964a0cb38e1"),
                    group,
                    "B",
                    "template.title.new.complex.desktop.app",
                    Size.SIZE_640x480.getI18nKey(),
                    ComplexDesktopTemplate.class.getResource("ComplexDesktopApplication.fxml"),
                    ComplexDesktopTemplate.class.getResource("complex_desktop.png"),
                    ComplexDesktopTemplate.class.getResource("complex_desktop@2x.png"),
                    null);
            //@formatter:on
        }
    }

    private static class InnerGroup extends Ordered implements TemplateGroup {
        public InnerGroup(String orderKey, String name) {
            super(orderKey, name);
        }
    }

    private static class InnerTemplate extends Ordered implements Template {

        private final UUID id;
        private final TemplateGroup group;
        private final String size;
        private final URL fxmlUrl;
        private final URL iconUrl;
        private final URL iconX2Url;
        private final UUID defaultThemeId;

        public InnerTemplate(UUID id, TemplateGroup group, String orderKey, String name, String size, URL fxmlUrl, URL iconUrl, URL iconX2Url, UUID defaultThemeId) {
            super(orderKey, name);
            this.id = id;
            this.group = group;
            this.size = size;
            this.fxmlUrl = fxmlUrl;
            this.iconUrl = iconUrl;
            this.iconX2Url = iconX2Url;
            this.defaultThemeId = defaultThemeId;
        }

        @Override
        public TemplateGroup getGroup() {
            return group;
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
        public UUID getId() {
            return id;
        }

        public String getSize() {
            return size;
        }

        @Override
        public UUID getDefaultThemeId() {
            return defaultThemeId;
        }

}
}
