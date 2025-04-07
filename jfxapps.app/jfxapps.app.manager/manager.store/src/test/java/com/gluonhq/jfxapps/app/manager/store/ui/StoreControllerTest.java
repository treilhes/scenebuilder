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
package com.gluonhq.jfxapps.app.manager.store.ui;

import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.app.manager.api.ManagerApiExtension;
import com.gluonhq.jfxapps.app.manager.store.TestUtil;
import com.gluonhq.jfxapps.app.manager.store.action.StoreActionFactory;
import com.gluonhq.jfxapps.app.manager.store.action.SwitchBackAction;
import com.gluonhq.jfxapps.app.manager.store.action.SwitchNextAction;
import com.gluonhq.jfxapps.app.manager.store.ui.component.SwitchFactory;
import com.gluonhq.jfxapps.app.manager.store.ui.root.RootController;
import com.gluonhq.jfxapps.core.api.action.ActionExtensionFactory;
import com.gluonhq.jfxapps.core.api.action.ActionFactory;
import com.gluonhq.jfxapps.core.api.javafx.DisableAutomaticFxmlLoading;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.scene.control.Label;

@JfxAppsTest
@ContextConfiguration(classes = { StoreControllerTest.Config.class, StoreController.class, SwitchFactory.class,
        StoreActionFactory.class, SwitchBackAction.class, SwitchNextAction.class, ActionFactory.class, ActionExtensionFactory.class})
class StoreControllerTest {

    @TestConfiguration
    static class Config {

        @Bean
        JfxAppPlatform jfxAppPlatform() {
            return Mockito.mock(JfxAppPlatform.class);
        }

        @Bean
        ViewMenu viewMenuController() {
            return Mockito.mock(ViewMenu.class);
        }

        @Bean
        @DisableAutomaticFxmlLoading
        RootController rootController() {
            return Mockito.mock(RootController.class);
        }

    }

    @Autowired
    RootController rootController;

    @Autowired
    StoreActionFactory storeActionFactory;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        try(var testStage = stageBuilder.controller(StoreController.class).show()){
            assertNotNull(testStage.getController());
            assertNotNull(testStage.getController().getRoot());
        }
    }

    @Test
    void must_show_ui_with_two_applications(StageBuilder stageBuilder, FxRobot robot) {

        Mockito.when(rootController.getRoot()).thenReturn(new Label("Root"));

        var loopForEdit = false;

        do {
            try(var testStage = stageBuilder
                    .controller(StoreController.class)
                    .size(800, 600)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .setup(StageType.Fill)
                    .show()) {

                var controller = testStage.getController();

                TestUtil.setSceneBackground(robot, controller);

                robot.interact(controller::onShow);

                System.out.println();

                robot.interact(() -> storeActionFactory.switchNext(() -> {
                    var node = new Label("next1");
                    node.setStyle("-fx-background-color: red; -fx-min-width: 200px; -fx-min-height: 200px;");
                    return node;
                }).checkAndPerform());

                System.out.println();

                robot.interact(() -> storeActionFactory.switchNext(() -> {
                    var node = new Label("next2");
                    node.setStyle("-fx-background-color: red; -fx-min-width: 200px; -fx-min-height: 200px;");
                    return node;
                }).checkAndPerform());

                System.out.println();

                robot.interact(() -> controller.back());

                System.out.println();

                robot.interact(() -> controller.back());

                System.out.println();

            }
        } while (loopForEdit);

    }

}
