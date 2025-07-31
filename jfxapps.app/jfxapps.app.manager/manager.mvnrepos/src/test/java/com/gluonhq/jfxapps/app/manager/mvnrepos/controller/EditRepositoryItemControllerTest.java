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
package com.gluonhq.jfxapps.app.manager.mvnrepos.controller;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.testfx.api.FxRobot;

import com.gluonhq.jfxapps.app.manager.api.ManagerApiExtension;
import com.gluonhq.jfxapps.app.manager.mvnrepos.i18n.I18NManagerMvnRepos;
import com.gluonhq.jfxapps.app.manager.mvnrepos.model.RepositoryMapperImpl;
import com.gluonhq.jfxapps.app.manager.mvnrepos.model.RepositoryModelController;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryClient;
import com.gluonhq.jfxapps.boot.api.maven.RepositoryType;
import com.gluonhq.jfxapps.core.api.javafx.JfxAppPlatform;
import com.gluonhq.jfxapps.core.api.ui.controller.menu.ViewMenu;
import com.gluonhq.jfxapps.test.JfxAppsTest;
import com.gluonhq.jfxapps.test.StageBuilder;
import com.gluonhq.jfxapps.test.StageType;

@JfxAppsTest
@ContextConfiguration(classes = { EditRepositoryItemControllerTest.Config.class, EditRepositoryItemController.class,
        RepositoryModelController.class, RepositoryMapperImpl.class })
class EditRepositoryItemControllerTest {

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
        List<RepositoryType> repositoryTypes() {
            return List.of(Mockito.mock(RepositoryType.class));
        }

        @Bean
        RepositoryClient repositoryClient() {
            return Mockito.mock(RepositoryClient.class);
        }
    }

//    @Autowired
//    RepositoryClient mavenClient;

    @Test
    void should_load_the_fxml(StageBuilder stageBuilder) {
        try(var testStage = stageBuilder.controller(EditRepositoryItemController.class).show()){
            assertNotNull(testStage.getController().getRoot());
        }
    }

    @Test
    void should_create_3_rows_with_only_2_lines_with_values(StageBuilder stageBuilder, FxRobot robot) {

        var loopForEdit = false;

        do {
            try(var testStage = stageBuilder
                    .controller(EditRepositoryItemController.class)
                    .size(800, 400)
                    .css(ManagerApiExtension.class.getResource("/com/gluonhq/jfxapps/app/manager/api/ui/Manager.css"))
                    .i18n(I18NManagerMvnRepos.class.getResource("ManagerMvnRepos.properties"))
                    .setup(StageType.Fill)
                    .show()){

                var controller = testStage.getController();

                addTestBackground(robot, controller);

                //robot.interact(controller::onShow);
                // robot.interact(() -> ScenicView.show(controller.getRoot().getScene()));

                System.out.println();
            }
        } while (loopForEdit);

    }


    private void addTestBackground(FxRobot robot, EditRepositoryItemController controller) {
        robot.interact(() -> controller.getRoot().getScene().getRoot().setStyle(
                "-fx-background-color:  radial-gradient(focus-angle 0deg , focus-distance -80% , center 0% -10% , radius 100% , #d5e3e6 30%, #72adaa 80%, #293950)"));
    }
}
