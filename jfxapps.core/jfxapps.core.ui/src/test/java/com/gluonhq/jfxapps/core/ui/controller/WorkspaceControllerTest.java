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

import com.gluonhq.jfxapps.core.api.content.mode.ModeManager;
import com.gluonhq.jfxapps.core.api.editor.selection.Selection;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.mask.FXOMObjectMask;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.tooltheme.ToolStylesheetProvider;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ContextMenu;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Content;
import com.gluonhq.jfxapps.core.api.ui.tool.Driver;
import com.gluonhq.jfxapps.core.fxom.FXOMDocument;
import com.gluonhq.jfxapps.core.ui.preference.BackgroundImagePreference;
import com.gluonhq.jfxapps.core.ui.preference.BackgroundImagePreference.BackgroundImage;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;
import com.gluonhq.jfxapps.util.URLUtils;

import io.reactivex.rxjava3.subjects.PublishSubject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;

@JfxAppsTest
@ContextConfiguration(classes = { WorkspaceControllerTest.Config.class, WorkspaceController.class, FXOMObjectMask.Factory.class })
class WorkspaceControllerTest {

    private static final String WORKSPACE_CSS = """
        #scrollPane {
            -fx-border-color: red;
        }
        #workspacePane {
            -fx-background-color: green;
        }
        """;

    @TestConfiguration
    static class Config {

        @Bean
        JfxAppPlatform jfxAppPlatform() {
            return Mockito.mock(JfxAppPlatform.class);
        }

        @Bean
        ContextMenu contextMenu() {
            return Mockito.mock(ContextMenu.class);
        }

        @Bean
        BackgroundImagePreference backgroundImagePreference() {
            return Mockito.mock(BackgroundImagePreference.class);
        }

        @Bean
        Selection selection() {
            return Mockito.mock(Selection.class);
        }

        @Bean
        Content content() {
            return Mockito.mock(Content.class);
        }

        @Bean
        ModeManager nodeManager() {
            return Mockito.mock(ModeManager.class);
        }

        @Bean
        Driver driver() {
            return Mockito.mock(Driver.class);
        }
    }

    @Autowired
    private ApplicationEvents applicationEvents;

    @Autowired
    private ApplicationInstanceEvents instanceEvents;

    @Autowired
    private ContextMenu contextMenu;

    @Autowired
    private Content content;

    @Autowired
    private BackgroundImagePreference backgroundImagePreference;

    @Test
    @DirtiesContext
    void should_load_the_fxml(StageBuilder builder) {
        Mockito.when(backgroundImagePreference.getObservableValue())
                .thenReturn(new SimpleObjectProperty<>(BackgroundImage.BACKGROUND_01));
        Mockito.when(content.contentChanged()).thenReturn(PublishSubject.create());

        var controller = builder.controller(WorkspaceController.class).show();
        assertNotNull(controller.getRoot());
    }

    @Test
    @DirtiesContext
    void show_ui(StageBuilder builder, FxRobot robot) {

        Mockito.when(backgroundImagePreference.getObservableValue())
            .thenReturn(new SimpleObjectProperty<>(BackgroundImage.BACKGROUND_01));
        Mockito.when(content.contentChanged()).thenReturn(PublishSubject.create());

        builder.controller(WorkspaceController.class)
                .size(800, 600)
                .setup(StageType.Fill)
                .css(WORKSPACE_CSS)
                .show();

        System.out.println();

        applicationEvents.stylesheetConfig().set(ToolStylesheetProvider.builder()
                .stylesheet(URLUtils.toDataURI("""
                    #scrollPane {
                        -fx-border-color: green;
                    }
                    #workspacePane {
                        -fx-background-color: blue;
                    }
                    """).toString())
                .build());

    }

    @Test
    @DirtiesContext
    void should_show_document_null_label(StageBuilder builder, FxRobot robot) throws IOException {

        var contentChanged = PublishSubject.<Boolean>create();

        Mockito.when(backgroundImagePreference.getObservableValue())
            .thenReturn(new SimpleObjectProperty<>(BackgroundImage.BACKGROUND_01));
        Mockito.when(content.contentChanged()).thenReturn(contentChanged);

        WorkspaceController workspace = builder.controller(WorkspaceController.class)
            .size(800, 600)
            .setup(StageType.Fill)
            .css(WORKSPACE_CSS)
            .show();

        robot.interact(() -> {
            Mockito.when(content.hasContent()).thenReturn(false);
            contentChanged.onNext(true);
        });

        assertEquals("FXOMDocument is null", ((Label)robot.lookup("#backgroundPane").query()).getText());
    }

    @Test
    @DirtiesContext
    void should_show_undisplayable_document_label(StageBuilder builder, FxRobot robot) throws IOException {

        var contentChanged = PublishSubject.<Boolean>create();

        Mockito.when(backgroundImagePreference.getObservableValue())
            .thenReturn(new SimpleObjectProperty<>(BackgroundImage.BACKGROUND_01));
        Mockito.when(content.contentChanged()).thenReturn(contentChanged);

        WorkspaceController workspace = builder.controller(WorkspaceController.class)
            .size(800, 600)
            .setup(StageType.Fill)
            .css(WORKSPACE_CSS)
            .show();

        robot.interact(() -> {
            Mockito.when(content.hasContent()).thenReturn(true);
            Mockito.when(content.isDisplayable()).thenReturn(false);
            contentChanged.onNext(true);
        });

        assertEquals("content.label.status.invitation", ((Label)robot.lookup("#backgroundPane").query()).getText());
    }

    @Test
    @DirtiesContext
    void should_scale_the_content(StageBuilder builder, FxRobot robot) throws IOException {

        var contentChanged = PublishSubject.<Boolean>create();

        Mockito.when(backgroundImagePreference.getObservableValue())
            .thenReturn(new SimpleObjectProperty<>(BackgroundImage.BACKGROUND_01));
        Mockito.when(content.contentChanged()).thenReturn(contentChanged);

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
            .css(WORKSPACE_CSS)
            .show();

        FXOMDocument fxomDocument = instanceEvents.fxomDocument().get();

        robot.interact(() -> {
            Mockito.when(content.hasContent()).thenReturn(true);
            Mockito.when(content.isDisplayable()).thenReturn(true);
            Mockito.when(content.getRoot()).thenReturn(fxomDocument.getDisplayNodeOrSceneGraphRoot());
            contentChanged.onNext(true);
        });

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
