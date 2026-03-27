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
package com.oracle.javafx.scenebuilder.template.templates;

import com.treilhes.emc4j.boot.api.context.annotation.ApplicationSingleton;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateContext;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroupContext;

public class DefaultTemplateList {

    @ApplicationSingleton
    @TemplateGroupContext(id = "2bce2804-921f-4ddb-81f6-5b9dad69d258", name = "", orderKey = "0")
    public interface DefaultGroup extends TemplateGroup {
    }

    @ApplicationSingleton
    @TemplateGroupContext(id = "fb2723b6-e647-42a7-95f0-572b3436c1e2", name = "template.title.header.desktop", orderKey = "A")
    public interface DesktopGroup extends TemplateGroup {
    }

    @ApplicationSingleton
    @TemplateGroupContext(id = "dfc1deb6-b2b5-496f-9e13-da8d05255619", name = "template.title.header.phone", orderKey = "B")
    public interface PhoneGroup extends TemplateGroup {
    }

    @ApplicationSingleton
    @TemplateGroupContext(id = "bc6580ef-ff66-4a54-b3e3-d2957aef1a37", name = "template.title.header.tablet", orderKey = "C")
    public interface TabletGroup extends TemplateGroup {
    }

    @ApplicationSingleton
    @TemplateGroupContext(id = "5d587dc0-0d5e-4682-b304-0e414cc7fb72", name = "template.title.header.other", orderKey = "D")
    public interface OtherGroup extends TemplateGroup {
    }

    //@formatter:off
    @ApplicationSingleton
    @TemplateContext(
            id = "ef57f18d-0d4b-4754-9d67-bd8b54f27bfd",
            name = "template.title.new.empty.app",
            description = "template.description.new.empty.app",
            orderKey = "A",
            iconUrl = "empty_desktop.png",
            iconX2Url = "empty_desktop@2x.png",
            width = 640,
            height = 480,
            groupClass = DesktopGroup.class)
    //@formatter:on
    public interface EmptyTemplate extends Template{}

    //@formatter:off
    @ApplicationSingleton
    @TemplateContext(
            id = "ba373a83-0f6a-4025-9041-3a0106b13e41",
            name = "template.title.new.basic.desktop.app",
            description = "template.description.new.basic.desktop.app",
            orderKey = "A",
            width = 640,
            height = 480,
            fxmlUrl = "BasicDesktopApplication.fxml",
            iconUrl = "basic_desktop.png",
            iconX2Url = "basic_desktop@2x.png",
            groupClass = DesktopGroup.class)
    //@formatter:on
    public interface BasicDesktopTemplate extends Template {}

    //@formatter:off
    @ApplicationSingleton
    @TemplateContext(
            id = "9f98b594-a9a0-4487-8f31-1964a0cb38e1",
            name = "template.title.new.complex.desktop.app",
            description = "template.description.new.complex.desktop.app",
            orderKey = "B",
            width = 640,
            height = 480,
            fxmlUrl = "ComplexDesktopApplication.fxml",
            iconUrl = "complex_desktop.png",
            iconX2Url = "complex_desktop@2x.png",
            groupClass = DesktopGroup.class)
    //@formatter:on
    public interface ComplexDesktopTemplate extends Template{}

}
