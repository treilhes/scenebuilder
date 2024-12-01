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

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

@JfxAppsTest
@ContextConfiguration(classes = { WorkspaceBuilderTest.Config.class, WorkspaceBuilderTest.Something2.class })
class WorkspaceBuilderTest {

    private final static Logger logger = LoggerFactory.getLogger(WorkspaceBuilderTest.class);

    public interface Something {

    }
    public static class Something2 {

    }

    @TestConfiguration
    static class Config {
        @Bean
        Something something() {
            return Mockito.mock(Something.class);
        }
    }

    @Test
    void must_create_fake_workspace_components_and_layout(StageBuilder builder, FxRobot robot) throws Exception {

        //@formatter:off
        var testStage = builder.workspace()
                .size(800, 600)
                .document("""
                        <?xml version="1.0" encoding="UTF-8"?>

                        <?import javafx.scene.control.ComboBox?>
                        <?import javafx.scene.control.Label?>
                        <?import javafx.scene.layout.Pane?>
                        <?import javafx.scene.shape.Rectangle?>


                        <Label fx:id="theLabel" layoutX="0.0" layoutY="0.0" minHeight="40.0" minWidth="40.0" maxHeight="40.0" maxWidth="40.0" prefHeight="40.0" prefWidth="40.0" text="drag" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" />
                        <!--<ComboBox layoutX="40.0" layoutY="274.0" prefWidth="150.0"  xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1" />-->
                        <!--<Rectangle fx:id="square" layoutX="0.0" layoutY="0.0" width="100.0" height="100.0" arcHeight="5.0" arcWidth="5.0" fill="DODGERBLUE" stroke="BLACK" strokeType="INSIDE" xmlns="http://javafx.com/javafx/23.0.1" xmlns:fx="http://javafx.com/fxml/1"/>-->
                        """)
                .setup(StageType.Fill).show();
        //@formatter:on

        var uiController = testStage.getController();
        var root = uiController.getRoot();
        var scene = uiController.getRoot().getScene();
        var subScene = uiController.getSubScene();

        assertEquals("subScene's container must be root first child", root.getChildren().get(0),
                uiController.getSubSceneHolder());
        assertEquals("glass layer must be root second child", root.getChildren().get(1), uiController.getGlassLayer());
        assertEquals("layer must be glass layer child", uiController.getGlassLayer().getChildren().get(0),
                uiController.getLayer());
    }

}