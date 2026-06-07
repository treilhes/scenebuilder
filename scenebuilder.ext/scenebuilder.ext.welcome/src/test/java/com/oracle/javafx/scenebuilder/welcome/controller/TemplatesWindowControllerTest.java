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
import org.testfx.api.FxRobot;

import com.oracle.javafx.scenebuilder.api.template.Template;
import com.oracle.javafx.scenebuilder.api.template.TemplateGroup;
import com.treilhes.emc4j.test.EmcInject;
import com.treilhes.emc4j.test.EmcInjectMock;
import com.treilhes.jfxplace.core.api.application.ApplicationActionFactory;
import com.treilhes.jfxplace.core.api.application.InstancesManager;
import com.treilhes.jfxplace.core.api.ui.MainInstanceWindow;
import com.treilhes.jfxplace.core.api.ui.controller.misc.IconSetting;
import com.treilhes.jfxplace.fxom.api.document.DocumentActionFactory;
import com.treilhes.jfxplace.test.JfxPlaceTest;
import com.treilhes.jfxplace.test.builder.StageBuilder;
import com.treilhes.jfxplace.test.builder.StageType;

import javafx.stage.Stage;

@JfxPlaceTest(classes = { TemplatesSelectionController.class, TemplatesWindowController.class, TemplateLoader.class })
class TemplatesWindowControllerTest {

    @EmcInjectMock
    InstancesManager instancesManager;

    @EmcInjectMock
    ApplicationActionFactory applicationActionFactory;

    @EmcInjectMock
    DocumentActionFactory documentActionFactory;

    @EmcInjectMock
    MainInstanceWindow mainInstanceWindow;

    @EmcInjectMock
    IconSetting iconSetting;

    @EmcInjectMock
    Template template;

    @EmcInjectMock
    TemplateGroup templateGroup;

    @EmcInject
    StageBuilder builder;

    @Test
    void show_ui(Stage stage, FxRobot robot) {
        Mockito.when(mainInstanceWindow.getStage()).thenReturn(stage);

        Mockito.when(template.getName()).thenReturn("template name");
        Mockito.when(template.getDescription()).thenReturn("template description");
        Mockito.when(template.getGroup()).thenReturn(templateGroup);
        Mockito.when(template.getIconUrl()).thenReturn(TemplatesWindowControllerTest.class.getResource("empty.png"));
        Mockito.when(templateGroup.getName()).thenReturn("group name");

        try (var testStage = builder
            .controller(TemplatesWindowController.class)
            .setup(StageType.None)
            .size(600, 800)
            .show()){

            var controller = testStage.getController();
            controller.openWindow();
            assertNotNull(controller);
        }


    }

}
