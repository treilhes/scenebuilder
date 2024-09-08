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
package com.gluonhq.jfxapps.test;


import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.gluonhq.jfxapps.boot.context.JfxAppContext;
import com.gluonhq.jfxapps.core.api.javafx.FxmlController;
import com.gluonhq.jfxapps.core.api.javafx.JavafxThreadClassloader;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class StageBuilderTest {

    @Mock
    JfxAppContext context;

    @Mock
    JavafxThreadClassloader classloader;

    @Mock
    ApplicationEvents events;

    @Mock
    ApplicationInstanceEvents instanceEvents;

    @Mock
    Stage stage;

    @Mock
    FxmlController controller;

    @InjectMocks
    StageBuilder stageBuilder;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    public void shouldSetupStageWithDefaultSizeWhenNoSizeProvided() {
//        StageSetup x;
//        stageBuilder.controller(FxmlController.class)
//            .setup((stage, width, height, c) -> {})
//            .show();
//        verify(stage, times(1)).setWidth(800);
//        verify(stage, times(1)).setHeight(600);
//    }
//
//    @Test
//    public void shouldSetupStageWithProvidedSize() {
//        stageBuilder.controller(controller.getClass()).size(1024, 768).setup((stage, width, height, root) -> {}).show();
//        verify(stage, times(1)).setWidth(1024);
//        verify(stage, times(1)).setHeight(768);
//    }
//
//    @Test
//    public void shouldSetupStageWithPaneWhenNoRootProvided() {
//        when(controller.getRoot()).thenReturn(null);
//        stageBuilder.controller(controller.getClass()).setup((stage, width, height, root) -> {}).show();
//        verify(controller, times(1)).setRoot(any(Pane.class));
//    }
//
//    @Test
//    public void shouldThrowExceptionWhenInvalidFxmlDocumentProvided() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            stageBuilder.controller(controller.getClass()).document("invalid.fxml").show();
//        });
//    }
}
