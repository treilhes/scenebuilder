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
package com.oracle.javafx.scenebuilder.core.ui;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.scenebuilder.fxml.api.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.boot.context.annotation.Prototype;
import com.gluonhq.jfxapps.core.api.i18n.I18N;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationEvents;
import com.gluonhq.jfxapps.core.api.subjects.ApplicationInstanceEvents;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.Dock;
import com.gluonhq.jfxapps.core.api.ui.controller.dock.DockViewController;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.MenuBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.IconSetting;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.MessageBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.SelectionBar;
import com.gluonhq.jfxapps.core.api.ui.controller.misc.Workspace;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.BottomDividerVPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.LeftDividerHPosPreference;
import com.oracle.javafx.scenebuilder.core.ui.preferences.document.RightDividerHPosPreference;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.layout.Pane;

@JfxAppsTest
@ContextConfiguration(classes = { DocumentWindowControllerTest.Config.class, DocumentWindowController.class })
class DocumentWindowControllerTest {

        @TestConfiguration
        static class Config {
            @Bean
            IconSetting iconSetting() {
                return Mockito.mock(IconSetting.class);
            }
            @Bean
            LeftDividerHPosPreference leftDividerHPosPreference() {
                return Mockito.mock(LeftDividerHPosPreference.class);
            }
            @Bean
            RightDividerHPosPreference rightDividerHPosPreference() {
                return Mockito.mock(RightDividerHPosPreference.class);
            }
            @Bean
            BottomDividerVPosPreference bottomDividerVPosPreference() {
                return Mockito.mock(BottomDividerVPosPreference.class);
            }
            @Bean
            @Prototype
            Dock dockController() {
                return Mockito.mock(Dock.class);
            }
            @Bean
            DockViewController viewMenuController() {
                return Mockito.mock(DockViewController.class);
            }
            @Bean
            MenuBar menuBar() {
                return Mockito.mock(MenuBar.class);
            }
            @Bean
            Content content() {
                return Mockito.mock(Content.class);
            }
            @Bean
            MessageBar messageBar() {
                return Mockito.mock(MessageBar.class);
            }
            @Bean
            SelectionBar selectionBar() {
                return Mockito.mock(SelectionBar.class);
            }
            @Bean
            Workspace workspace() {
                return Mockito.mock(Workspace.class);
            }
        }

        @Autowired
        I18N i18n;
        @Autowired
        ApplicationEvents sceneBuilderManager;
        @Autowired
        IconSetting iconSetting;
        @Autowired
        ApplicationInstanceEvents documentManager;

        @Autowired
        LeftDividerHPosPreference leftDividerHPos;
        @Autowired
        RightDividerHPosPreference rightDividerHPos;
        @Autowired
        BottomDividerVPosPreference bottomDividerVPos;

        @Autowired
        Dock leftDockController;
        @Autowired
        Dock rightDockController;
        @Autowired
        Dock bottomDockController;
        @Autowired
        DockViewController viewMenuController;
        @Autowired
        MenuBar menuBar;
        @Autowired
        Content content;
        @Autowired
        MessageBar messageBar;
        @Autowired
        SelectionBar selectionBar;
        @Autowired
        Workspace workspace;


    // @formatter:off
    private DocumentWindowController getInstance() {
        DocumentWindowController dwc = new DocumentWindowController(
                i18n,
                sceneBuilderManager,
                iconSetting,
                documentManager,

                () -> leftDividerHPos,
                () -> rightDividerHPos,
                () -> bottomDividerVPos,

                leftDockController,
                rightDockController,
                bottomDockController,
                viewMenuController,

                menuBar,
                content,
                messageBar,
                selectionBar,
                workspace
                );
        return dwc;
    }
    // @formatter:on

    @Test
    @DirtiesContext
    void should_load_the_fxml(StageBuilder builder) {
        var leftDivider = new SimpleDoubleProperty(0.2);
        var rightDivider = new SimpleDoubleProperty(0.8);
        var bottomDivider = new SimpleDoubleProperty(0.2);

        Mockito.when(leftDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(leftDockController.getContent()).thenReturn(new Pane());
        Mockito.when(leftDividerHPos.getValue()).thenReturn(leftDivider.doubleValue());
        Mockito.when(leftDividerHPos.getObservableValue()).thenReturn((ObservableValue)leftDivider);

        Mockito.when(rightDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(rightDockController.getContent()).thenReturn(new Pane());
        Mockito.when(rightDividerHPos.getValue()).thenReturn(rightDivider.doubleValue());
        Mockito.when(rightDividerHPos.getObservableValue()).thenReturn((ObservableValue)rightDivider);

        Mockito.when(bottomDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(bottomDockController.getContent()).thenReturn(new Pane());
        Mockito.when(bottomDividerVPos.getValue()).thenReturn(bottomDivider.doubleValue());
        Mockito.when(bottomDividerVPos.getObservableValue()).thenReturn((ObservableValue)bottomDivider);

        var controller = builder.controller(getInstance()).show();
        assertNotNull(controller.getRoot());
    }

    @Test
    @DirtiesContext
    void should_show_docks_when_adding_content(StageBuilder builder, FxRobot robot) {
        var leftDivider = new SimpleDoubleProperty(0.2);
        var rightDivider = new SimpleDoubleProperty(0.8);
        var bottomDivider = new SimpleDoubleProperty(0.2);

        var leftDividerContent = new Pane();
        var rightDividerContent = new Pane();
        var bottomDividerContent = new Pane();

        Mockito.when(leftDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(leftDockController.getContent()).thenReturn(leftDividerContent);
        Mockito.when(leftDividerHPos.getValue()).thenReturn(leftDivider.doubleValue());
        Mockito.when(leftDividerHPos.getObservableValue()).thenReturn((ObservableValue)leftDivider);

        Mockito.when(rightDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(rightDockController.getContent()).thenReturn(rightDividerContent);
        Mockito.when(rightDividerHPos.getValue()).thenReturn(rightDivider.doubleValue());
        Mockito.when(rightDividerHPos.getObservableValue()).thenReturn((ObservableValue)rightDivider);

        Mockito.when(bottomDockController.minimizedProperty()).thenReturn(new SimpleBooleanProperty(false));
        Mockito.when(bottomDockController.getContent()).thenReturn(bottomDividerContent);
        Mockito.when(bottomDividerVPos.getValue()).thenReturn(bottomDivider.doubleValue());
        Mockito.when(bottomDividerVPos.getObservableValue()).thenReturn((ObservableValue)bottomDivider);

        Mockito.when(menuBar.getMenuBar()).thenReturn(new javafx.scene.control.MenuBar(new Menu("Menu")));

        var controller = builder.controller(getInstance())
                .setup(StageType.Fill)
                .size(800, 600)
                .show();

        robot.interact(() -> {
            leftDividerContent.getChildren().add(newButton("leftDockController"));
            rightDividerContent.getChildren().add(newButton("rightDockController"));
            bottomDividerContent.getChildren().add(newButton("bottomDockController"));
        });

        Button leftDockContent = robot.lookup("#leftDockController").query();
        Button rightDockContent = robot.lookup("#rightDockController").query();
        Button bottomDockContent = robot.lookup("#bottomDockController").query();


        assertTrue(leftDockContent.isVisible());
        assertTrue(rightDockContent.isVisible());
        assertTrue(bottomDockContent.isVisible());
    }

    private Button newButton(String id) {
        Button button = new Button(id);
        button.setId(id);
        return button;
    }
}
