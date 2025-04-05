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
package com.gluonhq.jfxapps.app.manager.store.ui.root;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.gluonhq.jfxapps.app.manager.store.model.Application;
import com.gluonhq.jfxapps.app.manager.store.ui.component.ApplicationItemController;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

import javafx.scene.control.Label;

@JfxAppsTest
@ContextConfiguration(classes = { RootControllerTest.Config.class, RootController.class, ApplicationItemController.class })
class RootControllerTest {

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

        @Bean
        RootModelControllerImpl rootModelController() {
            return Mockito.mock(RootModelControllerImpl.class);
        }
    }

    @Autowired
    RootModelControllerImpl rootModelController;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        try(var testStage = stageBuilder.controller(RootController.class).show()) {
            assertNotNull(testStage.getController().getRoot());
        }
    }

    @Test
    void root_application_must_show_title(StageBuilder stageBuilder, FxRobot robot) {

        var rootModel = new RootModel<Application, Application>();
        var app = new Application(null, null);

        rootModel.getItem().set(app);
        app.nameProperty().set("App1");

        Mockito.when(rootModelController.load()).thenReturn(rootModel);

        try (var testStage = stageBuilder.controller(RootController.class)
                .size(800, 600)
                .setup(StageType.Fill)
                .show()) {

            var controller = testStage.getController();
            robot.interact(controller::load);
            assertThat(robot.from(controller.getRoot()).lookup(app.nameProperty().get()).queryAs(Label.class))
                    .matches(b -> b.getStyleClass().contains("store-item-title"));
        }
    }

    @Test
    void root_application_must_show_update_version_button_only_if_next_version_available(StageBuilder stageBuilder, FxRobot robot) {

        var rootModel = new RootModel<Application, Application>();
        var app = new Application(null, null);

        rootModel.getItem().set(app);
        app.installedProperty().set(true);
        app.versionProperty().set("X.X.X");

        Mockito.when(rootModelController.load()).thenReturn(rootModel);

        try (var testStage = stageBuilder.controller(RootController.class)
                .size(800, 600)
                .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                .setup(StageType.Fill)
                .show()) {

            var controller = testStage.getController();

            robot.interact(controller::load);

            var updateButton = robot.from(controller.getRoot()).lookup("#updateButton").queryButton();

            assertThat(updateButton).matches(b -> b.isDisabled());

            app.nextVersionProperty().set("Y.Y.Y");

            assertThat(updateButton).matches(b -> !b.isDisabled());
        }
    }

    @Test
    void should_create_3_rows_with_only_2_lines_with_values(StageBuilder stageBuilder, FxRobot robot) {

        var rootModel = new RootModel<Application, Application>();
        var app1 = new Application(null, null);
        var app2 = new Application(null, null);

        rootModel.getItem().set(app1);
        rootModel.getAvailables().add(app1);
        rootModel.getInstalled().add(app2);

        app1.imageProperty().set(RootControllerTest.class.getResource("../image1.png"));
        app1.nameProperty().set("Scene Builder");
        app1.descriptionProperty().set("Scene Builder is an open source tool that allows for drag and drop design of JavaFX user interfaces.");
        app1.versionProperty().set("X.X.X");

        app2.imageProperty().set(RootControllerTest.class.getResource("../image2.png"));
        app2.nameProperty().set("App2");
        app2.descriptionProperty().set("Description2");
        app2.versionProperty().set("X.X.X");
        app2.nextVersionProperty().set("Y.Y.Y");
        app2.installedProperty().set(true);

        Mockito.when(rootModelController.load()).thenReturn(rootModel);

        var loopForEdit = false;

        do {
            try (var testStage = stageBuilder.controller(RootController.class)
                    .size(800, 600)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .setup(StageType.Fill)
                    .show()) {


                var controller = testStage.getController();

                TestUtil.setSceneBackground(robot, controller);

                robot.interact(controller::load);
                //robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

                System.out.println();

            }
        } while (loopForEdit);

    }


}
