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
package com.gluonhq.jfxapps.core.ui.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenu;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;
import com.gluonhq.jfxapps.util.URLUtils;

import javafx.scene.Node;

@JfxAppsTest
@ContextConfiguration(classes = { WorkspaceControllerTest.Config.class, WorkspaceController.class })
class WorkspaceControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        ContextMenu contextMenu() {
            return Mockito.mock(ContextMenu.class);
        }
    }

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private ApplicationInstanceEvents instanceEvents;

    @Autowired
    private ContextMenu contextMenu;

    @Test
    @DirtiesContext
    void should_load_the_fxml(StageBuilder builder) {
        var controller = builder.controller(WorkspaceController.class).show();
        assertNotNull(controller.getRoot());
    }

    @Test
    @DirtiesContext
    void show_ui(StageBuilder builder, FxRobot robot) {

        builder.controller(WorkspaceController.class)
                .size(800, 600)
                .setup(StageType.Fill)
                .css("""
                    #scrollPane {
                        -fx-border-color: red;
                    }
                    #workspacePane {
                        -fx-background-color: green;
                    }
                    """)
                .show();

        System.out.println();

        applicationEvents.stylesheetConfig().onNext(ToolStylesheetProvider.builder()
                .stylesheet(URLUtils.toDataURI("""
                    #scrollPane {
                        -fx-border-color: green;
                    }
                    #workspacePane {
                        -fx-background-color: blue;
                    }
                    """).toString())
                .build());

        System.out.println();
    }

    @Test
    @DirtiesContext
    void should_scale_the_content(StageBuilder builder, FxRobot robot) throws IOException {
        WorkspaceController workspace = builder.controller(WorkspaceController.class)
            .size(800, 600)
            .setup(StageType.Fill)
            .document("""
                <?import javafx.scene.control.Label?>
                <Label xmlns="http://javafx.com/javafx/18" xmlns:fx="http://javafx.com/fxml/1"
                    text="sceneGraphObjectXX"/>
                """)
            .css("""
                #scrollPane {
                    -fx-border-color: red;
                }
                #workspacePane {
                    -fx-background-color: green;
                }
                """)
            .show();

        FXOMDocument fxomDocument = instanceEvents.fxomDocument().get();
        Node sceneGraph = fxomDocument.getFxomRoot().getSceneGraphObject().getAs(Node.class);

        var before = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        robot.interact(() -> {
            workspace.setScaling(2.0d);
        });

        var after = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        assertEquals(before.getWidth()*2, after.getWidth(), 0.1);
        assertEquals(before.getHeight()*2, after.getHeight(), 0.1);
    }

    @Test
    @DirtiesContext
    void should_show_windowt(StageBuilder builder, FxRobot robot) throws IOException {
        WorkspaceController workspace = builder.controller(WorkspaceController.class)
            .size(800, 600)
            .setup(StageType.Fill)
            .document("""
                <?import javafx.scene.Scene?>
                <?import javafx.scene.layout.AnchorPane?>
                <?import javafx.stage.Stage?>

                <Stage xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                    <scene>
                        <Scene>
                            <AnchorPane prefHeight="200" prefWidth="200" />
                        </Scene>
                    </scene>
                </Stage>
                """)
            .css("""
                #scrollPane {
                    -fx-border-color: red;
                }
                #workspacePane {
                    -fx-background-color: green;
                }
                """)
            .show();

        FXOMDocument fxomDocument = instanceEvents.fxomDocument().get();
        Node sceneGraph = (Node)fxomDocument.getDisplayNodeOrSceneGraphRoot();

        var before = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        robot.interact(() -> {
            workspace.setScaling(2.0d);
        });

        var after = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        assertEquals(before.getWidth()*2, after.getWidth(), 0.1);
        assertEquals(before.getHeight()*2, after.getHeight(), 0.1);
    }

    @Test
    @DirtiesContext
    void should_show_scene(StageBuilder builder, FxRobot robot) throws IOException {
        WorkspaceController workspace = builder.controller(WorkspaceController.class)
            .size(800, 600)
            .setup(StageType.Fill)
            .document("""
                <?import javafx.scene.Scene?>
                <?import javafx.scene.control.Button?>
                <?import javafx.scene.layout.AnchorPane?>

                <Scene xmlns="http://javafx.com/javafx/21" xmlns:fx="http://javafx.com/fxml/1">
                    <AnchorPane prefHeight="200" prefWidth="200">
                      <children>
                         <Button mnemonicParsing="false" text="Button" />
                      </children></AnchorPane>
                </Scene>
                """)
            .css("""
                #scrollPane {
                    -fx-border-color: red;
                }
                #workspacePane {
                    -fx-background-color: green;
                }
                """)
            .show();

        FXOMDocument fxomDocument = instanceEvents.fxomDocument().get();
        Node sceneGraph = (Node)fxomDocument.getDisplayNodeOrSceneGraphRoot();

        var before = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        robot.interact(() -> {
            workspace.setScaling(2.0d);
        });

        var after = sceneGraph.localToScreen(sceneGraph.getLayoutBounds());

        assertEquals(before.getWidth()*2, after.getWidth(), 0.1);
        assertEquals(before.getHeight()*2, after.getHeight(), 0.1);
    }

}
