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
package com.gluonhq.jfxapps.app.manager.store.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.app.manager.api.ManagerApiExtension;
import com.gluonhq.jfxapps.app.manager.store.model.StoreModelController;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.boot.api.registry.model.ApplicationInfo;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

@JfxAppsTest
@ContextConfiguration(classes = { StoreControllerTest.Config.class, StoreController.class, AppItemController.class,
        StoreModelController.class })
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
        RegistryManager registryManager() {
            return Mockito.mock(RegistryManager.class);
        }
    }

    @Autowired
    RegistryManager registryManager;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        var testStage = stageBuilder.controller(StoreController.class).show();
        assertNotNull(testStage.getController().getRoot());
        testStage.close();
    }

    @Test
    void must_show_ui_with_two_applications(StageBuilder stageBuilder, FxRobot robot) {
        var b = new AtomicReference<Button>();

        var app1 = new ApplicationInfo();
        app1.setUuid(UUID.randomUUID());
        app1.setImage(StoreControllerTest.class.getResource("image1.png"));
        app1.setTitle("Scene Builder");
        app1.setText(
                "Scene Builder is an open source tool that allows for drag and drop design of JavaFX user interfaces.");
        app1.setVersion("X.X.X");

        var app2 = new ApplicationInfo();
        app2.setUuid(UUID.randomUUID());
        app2.setImage(StoreControllerTest.class.getResource("image2.png"));
        app2.setTitle("App2");
        app2.setText("Description2");
        app2.setVersion("X.X.X");

        Mockito.when(registryManager.listApplicationsInfo()).thenReturn(Set.of(app1, app2));

        var loopForEdit = false;

        do {
            var testStage = stageBuilder
                    .controller(StoreController.class)
                    .size(800, 600)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .setup(StageType.Fill)
                    .show();

            var controller = testStage.getController();

            controller.getRoot().getScene().getRoot().setStyle(
                    "-fx-background-color:  radial-gradient(focus-angle 0deg , focus-distance -80% , center 0% -10% , radius 100% , #d5e3e6 30%, #72adaa 80%, #293950)");

            robot.interact(controller::onShow);
            // robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

            var labels = robot.lookup(".label").queryAllAs(Label.class);

            assertThat(labels)
            .areAtLeastOne(new Condition<>(l -> l.getText().contains(app1.getTitle()), "contains Scene Builder"))
            .areAtLeastOne(new Condition<>(l -> l.getText().contains(app2.getTitle()), "contains App2"));

            testStage.close();
        } while (loopForEdit);

    }

}
