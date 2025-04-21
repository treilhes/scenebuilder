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
package com.gluonhq.jfxapps.app.manager.store.ui.app;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

import java.util.UUID;

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
import com.gluonhq.jfxapps.app.manager.store.model.Application;
import com.gluonhq.jfxapps.app.manager.store.model.Plugin;
import com.gluonhq.jfxapps.app.manager.store.ui.component.PluginItemController;
import com.gluonhq.jfxapps.boot.api.registry.RegistryManager;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

@JfxAppsTest
@ContextConfiguration(classes = { ApplicationDetailControllerTest.Config.class, ApplicationDetailController.class,
        PluginItemController.class })
class ApplicationDetailControllerTest {

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
        ApplicationModelControllerImpl applicationModelController() {
            return Mockito.mock(ApplicationModelControllerImpl.class);
        }

        @Bean
        StoreActionFactory storeActionFactory() {
            return Mockito.mock(StoreActionFactory.class);
        }

    }

    @Autowired
    RegistryManager registryManager;

    @Autowired
    ApplicationModelControllerImpl applicationModelController;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        try(var testStage = stageBuilder.controller(ApplicationDetailController.class).show()){
            assertNotNull(testStage.getController().getRoot());
        }
    }

    @Test
    void must_load_the_app_and_sub_apps(StageBuilder stageBuilder, FxRobot robot) {

        var appModel = new ApplicationModel();
        var app = new Application(null, null);
        var plug1 = new Plugin(null, null);
        var plug2 = new Plugin(null, null);

        appModel.getItem().set(app);
        appModel.getAvailables().add(plug1);
        appModel.getInstalled().add(plug2);

        app.uuidProperty().set(UUID.randomUUID());
        app.imageProperty().set(ApplicationDetailControllerTest.class.getResource("/com/gluonhq/jfxapps/app/manager/store/ui/image1.png"));
        app.nameProperty().set("Scene Builder");
        app.descriptionProperty().set("Scene Builder is an open source tool that allows for drag and drop design of JavaFX user interfaces.");
        app.versionProperty().set("X.X.X");

        plug1.imageProperty().set(ApplicationDetailControllerTest.class.getResource("/com/gluonhq/jfxapps/app/manager/store/ui/image1.png"));
        plug1.nameProperty().set("Scene Builder");
        plug1.descriptionProperty().set("Scene Builder is an open source tool that allows for drag and drop design of JavaFX user interfaces.");
        plug1.versionProperty().set("X.X.X");

        plug2.imageProperty().set(ApplicationDetailControllerTest.class.getResource("/com/gluonhq/jfxapps/app/manager/store/ui/image2.png"));
        plug2.nameProperty().set("App2");
        plug2.descriptionProperty().set("Description2");
        plug2.versionProperty().set("X.X.X");

        Mockito.when(applicationModelController.load(any())).thenReturn(appModel);

        var loopForEdit = false;

        do {
            try (var testStage = stageBuilder.controller(ApplicationDetailController.class)
                    .size(800, 600)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .setup(StageType.Fill)
                    .show()) {


                var controller = testStage.getController();

                TestUtil.setSceneBackground(robot, controller);

                robot.interact(() -> controller.load(null, null));
                //robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

                System.out.println();

                testStage.close();
            }
        } while (loopForEdit);

    }


}
