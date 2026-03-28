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
package com.oracle.javafx.scenebuilder.welcome.controller;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;
import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;

@JfxAppsTest
@ContextConfiguration(classes = {TemplatesSelectionControllerTest.Config.class ,TemplatesSelectionController.class})
class TemplatesSelectionControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        Template template() {
            return Mockito.mock(Template.class);
        }
        @Bean
        TemplateGroup templateGroup() {
            return Mockito.mock(TemplateGroup.class);
        }
    }

    @Autowired
    Template template;

    @Autowired
    TemplateGroup templateGroup;

    @Test
    void show_ui(StageBuilder builder, FxRobot robot) {

        Mockito.when(template.getName()).thenReturn("template name");
        Mockito.when(template.getDescription()).thenReturn("template description");
        Mockito.when(template.getGroup()).thenReturn(templateGroup);
        Mockito.when(template.getIconUrl()).thenReturn(TemplatesSelectionControllerTest.class.getResource("empty.png"));
        Mockito.when(templateGroup.getName()).thenReturn("group name");

        TemplatesSelectionController controller = builder
            .controller(TemplatesSelectionController.class)
            .setup(StageType.Fill)
            .size(600, 800)
            .show().getController();

        assertNotNull(controller);
    }

}
